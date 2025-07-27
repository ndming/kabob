package com.ndming.kabob

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.lifecycle.viewModelScope
import com.ndming.kabob.gs_2m.generated.resources.Res
import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.MissingResourceException
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

enum class DecompositionMap(val resName: String, val fullName: String, val shortName: String) {
    Normal("normal", "Normal", "N"),
    Albedo("albedo", "Albedo", "A"),
    Roughness("roughness", "Rough", "R"),
    Diffuse("diffuse", "Diffuse", "Diff."),
    Specular("specular", "Specular", "Spec."),
}

@Suppress("SpellCheckingInspection")
enum class ReconstructionMethod(val prefix: String, val authors: String, val year: String, val title: String, val venue: String) {
    PGSR("PGSR", "Chen, D., Li, H., Ye, W., Wang, Y., Xie, W., Zhai, S., Wang, N., Liu, H., Bao, H., and Zhang, G.", "2024", "Pgsr: Planar-based gaussian splatting for efficient and high-fidelity surface reconstruction", "IEEE Transactions on Visualization and Computer Graphics"),
    TDGS("2DGS", "Huang, B., Yu, Z., Chen, A., Geiger, A., and Gao, S.", "2024", "2d gaussian splatting for geometrically accurate radiance fields", "SIGGRAPH 2024 Conference Papers. Association for Computing Machinery"),
}

data class Gs2mUiState(
    val dtuViewerSceneIndex: Int = 0,
    val dtuViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyViewerSceneIndex: Int = 0,
    val shinyViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyViewerPairedMap: DecompositionMap = DecompositionMap.Normal,
    val shinyMeshSceneIndex: Int = 0,
    val shinyMeshSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyMeshPairedMethod: ReconstructionMethod = ReconstructionMethod.PGSR,
    val chamfer: Chamfer = Chamfer(),
)

data class AnimatedImageData(
    val codec: Codec,
    val bitmap: Bitmap,
)

data class AnimatedImagePairData(
    val codecL: Codec,
    val codecs: List<Codec>,
    val bitmapL: Bitmap,
    val bitmapR: Bitmap,
)

data class AnimatedImageState(
    val loading: Boolean = true,
    val missing: Boolean = false,
)

@Serializable
data class Chamfer(
    val scan24: List<Float> = emptyList(),
    val scan37: List<Float> = emptyList(),
    val scan40: List<Float> = emptyList(),
    val scan55: List<Float> = emptyList(),
    val scan63: List<Float> = emptyList(),
    val scan65: List<Float> = emptyList(),
    val scan69: List<Float> = emptyList(),
    val scan83: List<Float> = emptyList(),
    val scan97: List<Float> = emptyList(),
    val scan105: List<Float> = emptyList(),
    val scan106: List<Float> = emptyList(),
    val scan110: List<Float> = emptyList(),
    val scan114: List<Float> = emptyList(),
    val scan118: List<Float> = emptyList(),
    val scan122: List<Float> = emptyList(),
    val mean: List<Float> = emptyList(),
)

@OptIn(ExperimentalResourceApi::class)
class Gs2mViewModel : ThemeAwareViewModel() {
    private val _uiState = MutableStateFlow(Gs2mUiState())
    val uiState: StateFlow<Gs2mUiState> = _uiState.asStateFlow()

    private var dtuViewerJob: Job = viewModelScope.launch { loadDtuViewerScene() }
    private lateinit var dtuViewerData: AnimatedImageData

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadDtuViewerScene() {
        val index = _uiState.value.dtuViewerSceneIndex
        val bytes = try {
            Res.readBytes("files/dtu/scan${DTU_SCENES[index]}/anim.webp")
        } catch (_: MissingResourceException) {
            ByteArray(0)
        }

        if (bytes.isEmpty()) {
            _uiState.update { it.copy(dtuViewerSceneState = AnimatedImageState(loading = false, missing = true)) }
        } else {
            val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
            val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
            dtuViewerData = AnimatedImageData(codec, bitmap)
            _uiState.update { it.copy(dtuViewerSceneState = AnimatedImageState(loading = false, missing = false)) }
        }
    }

    fun requestDtuViewerFrame(index: Int): ImageBitmap {
        viewModelScope.launch {
            dtuViewerData.codec.readPixels(dtuViewerData.bitmap, index)
        }
        return dtuViewerData.bitmap.asComposeImageBitmap()
    }

    fun changeDtuViewerScene(index: Int) {
        _uiState.update {
            it.copy(
                dtuViewerSceneState = AnimatedImageState(loading = true),
                dtuViewerSceneIndex = index
            )
        }

        dtuViewerJob.cancel()
        dtuViewerJob = viewModelScope.launch { loadDtuViewerScene() }
    }

    private var shinyViewerJob: Job = viewModelScope.launch { loadShinyViewerScene() }
    private lateinit var shinyViewerData: AnimatedImagePairData

    @Suppress("DuplicatedCode")
    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadShinyViewerScene() {
        val index = _uiState.value.shinyViewerSceneIndex

        val bytesL = try {
            Res.readBytes("files/shiny/${SHINY_SCENES[index]}/gt.webp")
        } catch (_: MissingResourceException) {
            ByteArray(0)
        }

        val bytesR = try {
            DecompositionMap.entries.map { Res.readBytes("files/shiny/${SHINY_SCENES[index]}/${it.resName}.webp") }
        } catch (_: MissingResourceException) {
            emptyList()
        }

        if (bytesL.isEmpty() || bytesR.isEmpty()) {
            _uiState.update { it.copy(shinyViewerSceneState = AnimatedImageState(loading = false, missing = true)) }
        } else {
            shinyViewerData = loadPairedData(bytesL, bytesR)
            _uiState.update { it.copy(shinyViewerSceneState = AnimatedImageState(loading = false, missing = false)) }
        }
    }

    fun requestShinyViewerFrame(index: Int): Pair<ImageBitmap, ImageBitmap> {
        viewModelScope.launch {
            shinyViewerData.codecL.readPixels(shinyViewerData.bitmapL, index)
            val mapIndex = _uiState.value.shinyViewerPairedMap.ordinal
            shinyViewerData.codecs[mapIndex].readPixels(shinyViewerData.bitmapR, index)
        }
        return shinyViewerData.bitmapL.asComposeImageBitmap() to shinyViewerData.bitmapR.asComposeImageBitmap()
    }

    fun changeShinyViewerScene(index: Int) {
        _uiState.update {
            it.copy(
                shinyViewerSceneState = AnimatedImageState(loading = true),
                shinyViewerSceneIndex = index
            )
        }

        shinyViewerJob.cancel()
        shinyViewerJob = viewModelScope.launch { loadShinyViewerScene() }
    }

    fun changeShinyViewerPairedMap(map: DecompositionMap) {
        _uiState.update { it.copy(shinyViewerPairedMap = map) }
    }

    private var shinyMeshJob: Job = viewModelScope.launch { loadShinyMeshScene() }
    private lateinit var shinyMeshData: AnimatedImagePairData
    private lateinit var shinyMeshGTData: AnimatedImageData

    @Suppress("DuplicatedCode")
    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadShinyMeshScene() {
        val index = _uiState.value.shinyMeshSceneIndex
        val bytes = try {
            Res.readBytes("files/shiny/${SHINY_MESHES[index]}/gt.webp")
        } catch (_: MissingResourceException) {
            ByteArray(0)
        }

        val bytesL = try {
            Res.readBytes("files/shiny/${SHINY_MESHES[index]}/Ours_anim.webp")
        } catch (_: MissingResourceException) {
            ByteArray(0)
        }

        val bytesR = try {
            ReconstructionMethod.entries.map { Res.readBytes("files/shiny/${SHINY_MESHES[index]}/${it.prefix}_anim.webp") }
        } catch (_: MissingResourceException) {
            emptyList()
        }

        if (bytesL.isEmpty() || bytesR.isEmpty() || bytes.isEmpty()) {
            _uiState.update { it.copy(shinyMeshSceneState = AnimatedImageState(loading = false, missing = true)) }
        } else {
            val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
            val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
            shinyMeshGTData = AnimatedImageData(codec, bitmap)
            shinyMeshData = loadPairedData(bytesL, bytesR)
            _uiState.update { it.copy(shinyMeshSceneState = AnimatedImageState(loading = false, missing = false)) }
        }
    }

    fun requestShinyMeshFrame(index: Int): Triple<ImageBitmap, ImageBitmap, ImageBitmap> {
        viewModelScope.launch {
            shinyMeshGTData.codec.readPixels(shinyMeshGTData.bitmap, index)
            shinyMeshData.codecL.readPixels(shinyMeshData.bitmapL, index)
            val methodIndex = _uiState.value.shinyMeshPairedMethod.ordinal
            shinyMeshData.codecs[methodIndex].readPixels(shinyMeshData.bitmapR, index)
        }
        return Triple(
            shinyMeshGTData.bitmap.asComposeImageBitmap(),
            shinyMeshData.bitmapL.asComposeImageBitmap(),
            shinyMeshData.bitmapR.asComposeImageBitmap()
        )
    }

    fun changeShinyMeshScene(index: Int) {
        _uiState.update {
            it.copy(
                shinyMeshSceneState = AnimatedImageState(loading = true),
                shinyMeshSceneIndex = index
            )
        }

        shinyMeshJob.cancel()
        shinyMeshJob = viewModelScope.launch { loadShinyMeshScene() }
    }

    fun changeShinyMeshPairedMethod(method: ReconstructionMethod) {
        _uiState.update { it.copy(shinyMeshPairedMethod = method) }
    }

    private fun loadPairedData(bytesL: ByteArray, bytesR: List<ByteArray>): AnimatedImagePairData {
        val codecL = Codec.makeFromData(Data.makeFromBytes(bytesL))
        val codecs = bytesR.map { Codec.makeFromData(Data.makeFromBytes(it)) }
        val bitmapL = Bitmap().apply { allocPixels(codecL.imageInfo) }
        val bitmapR = Bitmap().apply { allocPixels(codecL.imageInfo) }
        return AnimatedImagePairData(codecL, codecs, bitmapL, bitmapR)
    }

    init {
        viewModelScope.launch {
            val bytes = try {
                Res.readBytes("files/dtu/chamfer.json")
            } catch (_: MissingResourceException) {
                ByteArray(0)
            }

            val chamfer = if (bytes.isNotEmpty()) {
                Json.decodeFromString<Chamfer>(bytes.decodeToString())
            } else {
                Chamfer()
            }
            _uiState.update { it.copy(chamfer = chamfer) }
        }
    }


    companion object {
        val DTU_SCENES = listOf(24, 37, 40, 55, 63, 65, 69, 83, 97, 105, 106, 110, 114, 118, 122)
        val SHINY_SCENES = listOf("helmet", "car", "teapot", "ball", "coffee", "toaster")
        val SHINY_MESHES = listOf("ball", "car", "teapot", "helmet", "coffee", "toaster")
    }
}