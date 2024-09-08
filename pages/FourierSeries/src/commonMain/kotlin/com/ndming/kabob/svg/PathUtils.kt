package com.ndming.kabob.svg

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.vector.PathParser
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parser.Parser
import com.fleeksoft.ksoup.ported.openSourceReader
import org.jetbrains.kotlinx.multik.api.linspace
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.operations.toList

fun parseDrawableSVG(bytes: ByteArray): Pair<Rect, String> {
    val document = Ksoup.parse(bytes.openSourceReader(), "", "UTF-8", Parser.xmlParser())

    val svg = document.child(0)

    val viewBoxRect = svg.attr("viewBox").split(" ")
    val (offsetX, offsetY, width, height) = viewBoxRect
    val viewBox = Rect(Offset(offsetX.toFloat(), offsetY.toFloat()), Size(width.toFloat(), height.toFloat()))

    val path = svg.getElementsByTag("path")[0].attr("d")

    return viewBox to path
}

fun buildStandardPath(viewBox: Rect, pathData: String, halfExtent: Float): Path {
    val path = PathParser().parsePathString(pathData).toPath()

    val (width, height) = viewBox.width to viewBox.height
    val scaleFactor = halfExtent / (maxOf(width, height) / 2.0f)

    val matrix = Matrix().apply {
        scale(scaleFactor, -scaleFactor)
        translate(-width / 2.0f, -height / 2.0f)
    }
    path.transform(matrix)

    return path
}

fun Path.sample(rate: Float): List<Offset> {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, true)

    return mk.linspace<Float>(0.0, 1.0, (pathMeasure.length * rate).toInt()).toList()
        .map { fraction -> pathMeasure.getPosition(fraction * pathMeasure.length) }
}
