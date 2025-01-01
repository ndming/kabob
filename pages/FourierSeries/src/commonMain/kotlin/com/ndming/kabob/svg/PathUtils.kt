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

/**
 * Parses an SVG file provided as a byte array and extracts the viewBox and path data from it.
 *
 * This function reads an SVG document, retrieves the viewBox attribute to construct
 * a rectangle representing the bounds of the SVG, and extracts the path data string
 * from the first `<path>` tag in the document.
 *
 * @param bytes The byte array containing the SVG file's data.
 * @return A pair where the first element is a [Rect] representing the viewBox of the SVG,
 * and the second element is a [String] containing the path data.
 * @throws IllegalArgumentException if the SVG file structure is invalid or the necessary attributes are missing.
 */
fun readPathString(bytes: ByteArray): Pair<Rect, String> {
    val document = Ksoup.parse(bytes.openSourceReader(), "", "UTF-8", Parser.xmlParser())

    val svg = document.child(0)

    val viewBoxRect = svg.attr("viewBox").split(" ")
    val (offsetX, offsetY, width, height) = viewBoxRect
    val viewBox = Rect(Offset(offsetX.toFloat(), offsetY.toFloat()), Size(width.toFloat(), height.toFloat()))

    val path = svg.getElementsByTag("path")[0].attr("d")

    return viewBox to path
}

/**
 * Creates a [Path] object by scaling and transforming the provided path data 
 * to fit within a given view box and a specified extent.
 *
 * This function parses the SVG path data string into a [Path], scales it to match 
 * the desired half-extent, and centers it around the origin. The vertical axis is flipped 
 * to align with the expected coordinate system.
 *
 * @param viewBox A [Rect] object representing the bounds of the view box for the SVG.
 * @param pathData A [String] containing the path data (in SVG format) to parse and transform.
 * @param halfExtent A [Float] representing half of the desired extent for the transformed path.
 * @return A [Path] object representing the transformed path.
 */
fun makePath(viewBox: Rect, pathData: String, halfExtent: Float): Path {
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

/**
 * Samples evenly spaced points along a [Path] based on the specified sampling rate.
 *
 * This function uses a [PathMeasure] to extract points at regular intervals along the path, 
 * determined by multiplying the path's total length by the specified sampling rate.
 *
 * @param rate A [Float] representing the number of samples to take per unit length of the path.
 *             For example, a rate of `10.0` means approximately 10 samples for every 1 unit 
 *             of the path's length.
 * @return A [List] of [Offset] points representing the sampled positions along the path.
 */
fun Path.sample(rate: Float): List<Offset> {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, true)

    val sampleCount = (pathMeasure.length * rate).toInt()
    return mk.linspace<Float>(0.0, 1.0, sampleCount)
        .toList()
        .map { fraction -> pathMeasure.getPosition(fraction * pathMeasure.length) }
}
