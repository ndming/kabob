package com.ndming.kabob

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.lifecycle.viewModelScope
import com.ndming.kabob.gs_2m.generated.resources.Res
import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.MissingResourceException
import org.jetbrains.compose.resources.decodeToImageBitmap
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
    TDGS("2DGS", "Huang, B., Yu, Z., Chen, A., Geiger, A., and Gao, S.", "2024", "2d gaussian splatting for geometrically accurate radiance fields", "SIGGRAPH 2024 Conference Papers. Association for Computing Machinery"),
    GOF("GOF", "Yu, Z., Sattler, T., and Geiger, A.", "2024", "Gaussian opacity fields: Efficient adaptive surface reconstruction in unbounded scenes", "SIGGRAPH ASIA 2024 (Journal Track). ACM Transactions on Graphics"),
    PGSR("PGSR", "Chen, D., Li, H., Ye, W., Wang, Y., Xie, W., Zhai, S., Wang, N., Liu, H., Bao, H., and Zhang, G.", "2024", "Pgsr: Planar-based gaussian splatting for efficient and high-fidelity surface reconstruction", "IEEE Transactions on Visualization and Computer Graphics"),
}

data class Gs2mUiState(
    val dtuViewerBitmapGT: ImageBitmap? = null,
    val dtuViewerSceneIndex: Int = 0,
    val dtuViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyViewerBitmapL: ImageBitmap? = null,
    val shinyViewerBitmapR: ImageBitmap? = null,
    val shinyViewerSceneIndex: Int = 0,
    val shinyViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyViewerPairedMap: DecompositionMap = DecompositionMap.Normal,
    val shinyViewerPlaying: Boolean = false,
    val shinyMeshBitmapL: ImageBitmap? = null,
    val shinyMeshBitmapR: ImageBitmap? = null,
    val shinyMeshBitmapGT: ImageBitmap? = null,
    val shinyMeshSceneIndex: Int = 0,
    val shinyMeshSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyMeshPairedMethod: ReconstructionMethod = ReconstructionMethod.PGSR,
    val shinyMeshPlaying: Boolean = false,
    val chamfer: DtuQuantitativeData = DtuQuantitativeData(),
    val psnr: DtuQuantitativeData = DtuQuantitativeData(),
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
data class DtuQuantitativeData(
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

        val bytesGT = try {
            Res.readBytes("files/dtu/scan${DTU_SCENES[index]}/gt.png")
        } catch (_: MissingResourceException) {
            ByteArray(0)
        }

        if (bytes.isEmpty() || bytesGT.isEmpty()) {
            _uiState.update { it.copy(dtuViewerSceneState = AnimatedImageState(loading = false, missing = true)) }
        } else {
            val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
            val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
            dtuViewerData = AnimatedImageData(codec, bitmap)
            _uiState.update { it.copy(
                dtuViewerSceneState = AnimatedImageState(loading = false, missing = false),
                dtuViewerBitmapGT = bytesGT.decodeToImageBitmap(),
            ) }
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
            loadShinyViewerFrame(shinyViewerAnimator.value)
            _uiState.update { it.copy(shinyViewerSceneState = AnimatedImageState(loading = false, missing = false)) }
        }
    }

    private val shinyViewerAnimator = Animatable(0, Int.VectorConverter)

    fun changeShinyViewerPlaying(playing: Boolean, scope: CoroutineScope) {
        if (!playing) {
            scope.launch { shinyViewerAnimator.stop() }
        } else {
            scope.launch {
                shinyViewerAnimator.animatePlay(SHINY_FRAME_COUNT, FRAME_DURATION) {
                    loadShinyViewerFrame(it)
                }
            }
        }
        _uiState.update { it.copy(shinyViewerPlaying = playing) }
    }

    private fun loadShinyViewerFrame(index: Int) {
        with(shinyViewerData) {
            codecL.readPixels(bitmapL, index)
            val mapIndex = _uiState.value.shinyViewerPairedMap.ordinal
            codecs[mapIndex].readPixels(bitmapR, index)
            _uiState.update { it.copy(
                shinyViewerBitmapL = bitmapL.asComposeImageBitmap(),
                shinyViewerBitmapR = bitmapR.asComposeImageBitmap(),
            ) }
        }
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
        if (!shinyViewerAnimator.isRunning) {
            loadShinyViewerFrame(shinyViewerAnimator.value)
        }
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
            loadShinyMeshFrame(shinyMeshAnimator.value)
            _uiState.update { it.copy(shinyMeshSceneState = AnimatedImageState(loading = false, missing = false)) }
        }
    }

    private val shinyMeshAnimator = Animatable(0, Int.VectorConverter)

    fun changeShinyMeshPlaying(playing: Boolean, scope: CoroutineScope) {
        if (!playing) {
            scope.launch { shinyMeshAnimator.stop() }
        } else {
            scope.launch {
                shinyMeshAnimator.animatePlay(SHINY_FRAME_COUNT, FRAME_DURATION) {
                    loadShinyMeshFrame(it)
                }
            }
        }
        _uiState.update { it.copy(shinyMeshPlaying = playing) }
    }

    private fun loadShinyMeshFrame(index: Int) {
        shinyMeshGTData.codec.readPixels(shinyMeshGTData.bitmap, index)
        shinyMeshData.codecL.readPixels(shinyMeshData.bitmapL, index)
        val methodIndex = _uiState.value.shinyMeshPairedMethod.ordinal
        shinyMeshData.codecs[methodIndex].readPixels(shinyMeshData.bitmapR, index)
        _uiState.update { it.copy(
            shinyMeshBitmapGT = shinyMeshGTData.bitmap.asComposeImageBitmap(),
            shinyMeshBitmapL = shinyMeshData.bitmapL.asComposeImageBitmap(),
            shinyMeshBitmapR = shinyMeshData.bitmapR.asComposeImageBitmap(),
        ) }
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
        if (!shinyMeshAnimator.isRunning) {
            loadShinyMeshFrame(shinyMeshAnimator.value)
        }
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
                Json.decodeFromString<DtuQuantitativeData>(bytes.decodeToString())
            } else {
                DtuQuantitativeData()
            }
            _uiState.update { it.copy(chamfer = chamfer) }
        }

        viewModelScope.launch {
            val bytes = try {
                Res.readBytes("files/dtu/psnr.json")
            } catch (_: MissingResourceException) {
                ByteArray(0)
            }

            val psnr = if (bytes.isNotEmpty()) {
                Json.decodeFromString<DtuQuantitativeData>(bytes.decodeToString())
            } else {
                DtuQuantitativeData()
            }
            _uiState.update { it.copy(psnr = psnr) }
        }
    }


    companion object {
        val DTU_SCENES = listOf(24, 37, 40, 55, 63, 65, 69, 83, 97, 105, 106, 110, 114, 118, 122)
        val SHINY_SCENES = listOf("helmet", "car", "teapot", "ball", "coffee", "toaster")
        val SHINY_MESHES = listOf("ball", "car", "teapot", "helmet", "coffee", "toaster")
        private const val SHINY_FRAME_COUNT = 200
        private const val FRAME_DURATION = 1000 / 24
    }
}

private suspend fun Animatable<Int, AnimationVector1D>.animatePlay(
    frameCount: Int,
    frameDuration: Int,
    onFrame: (Int) -> Unit,
) {
    // Ongoing animatable retains its state when stopped, we will have to animate it to the full period
    // before starting the infinitely repeatable animation
    if (value > 0) {
        val remainingDuration = (frameCount - value - 1) * frameDuration
        animateTo(frameCount - 1, tween(remainingDuration, easing = LinearEasing)) {
            onFrame(this@animatePlay.value)
        }
        // This resets the state to 0, otherwise we'd stuck at 1
        delay(frameDuration.toLong())
        snapTo(0)
        onFrame(0)
    }
    val duration = (frameCount - 1) * frameDuration
    animateTo(frameCount - 1, infiniteRepeatable(tween(duration, easing = LinearEasing))) {
        onFrame(this@animatePlay.value)
    }
}