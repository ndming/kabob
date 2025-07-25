package com.ndming.kabob

import androidx.compose.runtime.snapshots.SnapshotStateList
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

data class Gs2mUiState(
    val dtuViewerSceneIndex: Int = 0,
    val dtuViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val dtuViewerFrames: SnapshotStateList<ImageBitmap> = SnapshotStateList(),
    val shinyViewerSceneIndex: Int = 0,
    val shinyViewerSceneState: AnimatedImageState = AnimatedImageState(),
    val shinyViewerPairedMap: DecompositionMap = DecompositionMap.Normal,
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
            val codecL = Codec.makeFromData(Data.makeFromBytes(bytesL))
            val codecs = bytesR.map { Codec.makeFromData(Data.makeFromBytes(it)) }
            val bitmapL = Bitmap().apply { allocPixels(codecL.imageInfo) }
            val bitmapR = Bitmap().apply { allocPixels(codecL.imageInfo) }
            shinyViewerData = AnimatedImagePairData(codecL, codecs, bitmapL, bitmapR)
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

    companion object {
        val DTU_SCENES = listOf(24, 37, 40, 55, 63, 65, 69, 83, 97, 105, 106, 110, 114, 118, 122)
        val SHINY_SCENES = listOf("helmet", "car", "teapot", "ball", "coffee", "toaster")
    }
}