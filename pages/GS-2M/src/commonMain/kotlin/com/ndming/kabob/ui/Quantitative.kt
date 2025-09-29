package com.ndming.kabob.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ndming.kabob.DtuQuantitativeData
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import kotlin.math.round

enum class NerfMethod(val prefix: String, val link: String) {
    VolSDF("VolSDF", "https://lioryariv.github.io/volsdf"),
    NeuS("NeuS", "https://lingjie0206.github.io/papers/NeuS"),
    RegSDF("RegSDF", "https://machinelearning.apple.com/research/critical-regularizations"),
    NeuS2("NeuS2", "https://vcai.mpi-inf.mpg.de/projects/NeuS2"),
    NeuralWarp("NeuralWarp", "https://imagine.enpc.fr/~darmonf/NeuralWarp"),
    Neuralangelo("Neuralangelo", "https://research.nvidia.com/labs/dir/neuralangelo"),
}

@Suppress("SpellCheckingInspection")
enum class GsMethod(val prefix: String, val link: String) {
    SuGaR("SuGaR", "https://anttwo.github.io/sugar"),
    GaussianSurfels("GaussianSurfels", "https://turandai.github.io/projects/gaussian_surfels"),
    GS2D("2DGS", "https://surfsplatting.github.io"),
    GOF("GOF", "https://niujinshuchong.github.io/gaussian-opacity-fields"),
    MILo("MILo", "https://anttwo.github.io/milo"),
    PlanarGS("PGSR", "https://zju3dv.github.io/pgsr"),
    GaussSurf("GausSurf", "https://jiepengwang.github.io/GausSurf")
}

@Suppress("SpellCheckingInspection")
enum class SotaMethod(val prefix: String, val link: String) {
    RegSDF("RegSDF", "https://machinelearning.apple.com/research/critical-regularizations"),
    NeuS("NeuS", "https://lingjie0206.github.io/papers/NeuS"),
    NeuS2("NeuS2", "https://vcai.mpi-inf.mpg.de/projects/NeuS2"),
    VolSDF("VolSDF", "https://lioryariv.github.io/volsdf"),
    Neuralangelo("Neuralangelo", "https://research.nvidia.com/labs/dir/neuralangelo"),
    PlanarGS("PGSR", "https://zju3dv.github.io/pgsr"),
}

enum class Rank(val code: String) {
    Best("1st"),
    Good("2nd"),
    Nice("3rd"),
    None("Rest"),
}

@Composable
fun DtuQuantitativeChamfer(
    chamfer: DtuQuantitativeData,
    modifier: Modifier = Modifier,
) {
    val monoFamily = getJetBrainsMonoFamily()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = modifier.padding(horizontal = 24.dp)) {
            ChamferHeaderColumn()

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                ChamferValueColumn(
                    scanID = "24",
                    values = chamfer.scan24,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "37",
                    values = chamfer.scan37,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "40",
                    values = chamfer.scan40,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "55",
                    values = chamfer.scan55,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "63",
                    values = chamfer.scan63,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "65",
                    values = chamfer.scan65,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "69",
                    values = chamfer.scan69,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "83",
                    values = chamfer.scan83,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "97",
                    values = chamfer.scan97,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "105",
                    values = chamfer.scan105,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "106",
                    values = chamfer.scan106,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "110",
                    values = chamfer.scan110,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "114",
                    values = chamfer.scan114,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "118",
                    values = chamfer.scan118,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "122",
                    values = chamfer.scan122,
                    fontFamily = monoFamily,
                )

                ChamferValueColumn(
                    scanID = "Mean",
                    values = chamfer.mean,
                    fontFamily = monoFamily,
                )
            }
        }

        RankCode(fontFamily = monoFamily, modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
private fun ChamferHeaderColumn(modifier: Modifier = Modifier) {
    ChamferColumn(
        modifier = modifier,
        headlineRow = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "CD",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = Icons.Default.South,
                    contentDescription = null,
                )
            }
        },
        nerfRow = {
            NerfMethod.entries.forEach { method ->
                HyperlinkText(
                    linkText = method.prefix,
                    linkUrl = method.link,
                    prefix = "",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        gsRow = {
            GsMethod.entries.forEach { method ->
                HyperlinkText(
                    linkText = method.prefix,
                    linkUrl = method.link,
                    prefix = "",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        ourRow = { OurMethodRow() }
    )
}

@Composable
private fun OurMethodRow() {
    Text(
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 1,
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Ours")
            }
            append(" w/o BRDF")
        }
    )

    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = "Ours",
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ChamferValueColumn(
    scanID: String,
    values: List<Float>,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    val rankedValues = if (values.isNotEmpty()) values.asRankedList(false) else emptyList()

    ChamferColumn(
        align = true,
        modifier = modifier,
        headlineRow = {
            Text(
                text = scanID, style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        },
        nerfRow = {
            if (rankedValues.isNotEmpty()) {
                rankedValues.subList(0, NerfMethod.entries.size).forEach { (value, rank) ->
                    Spacer(Modifier.height(9.dp))
                    RankedText(
                        text = value.format(),
                        rank = rank,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
        gsRow = {
            if (rankedValues.isNotEmpty()) {
                rankedValues.subList(NerfMethod.entries.size, NerfMethod.entries.size + GsMethod.entries.size).forEach { (value, rank) ->
                    Spacer(Modifier.height(9.dp))
                    RankedText(
                        text = value.format(),
                        rank = rank,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
        ourRow = {
            if (rankedValues.isNotEmpty()) {
                rankedValues.subList(NerfMethod.entries.size + GsMethod.entries.size, values.size).forEach { (value, rank) ->
                    Spacer(Modifier.height(8.dp))
                    RankedText(
                        text = value.format(),
                        rank = rank,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
    )
}

@Composable
private fun ChamferColumn(
    align: Boolean = false,
    modifier: Modifier = Modifier,
    headlineRow: @Composable () -> Unit,
    nerfRow: @Composable () -> Unit,
    gsRow: @Composable () -> Unit,
    ourRow: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = if (align) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primaryContainer)

        headlineRow()

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        nerfRow()

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        gsRow()

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        ourRow()

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 4.dp, color = MaterialTheme.colorScheme.primaryContainer)
    }
}

@Composable
fun DtuQuantitativeNvs(
    psnr: DtuQuantitativeData,
    modifier: Modifier = Modifier,
) {
    val monoFamily = getJetBrainsMonoFamily()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = modifier.padding(horizontal = 24.dp)) {
            NvsHeaderColumn()

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                NvsValueColumn(
                    scanID = "24",
                    values = psnr.scan24,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "37",
                    values = psnr.scan37,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "40",
                    values = psnr.scan40,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "55",
                    values = psnr.scan55,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "63",
                    values = psnr.scan63,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "65",
                    values = psnr.scan65,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "69",
                    values = psnr.scan69,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "83",
                    values = psnr.scan83,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "97",
                    values = psnr.scan97,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "105",
                    values = psnr.scan105,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "106",
                    values = psnr.scan106,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "110",
                    values = psnr.scan110,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "114",
                    values = psnr.scan114,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "118",
                    values = psnr.scan118,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "122",
                    values = psnr.scan122,
                    fontFamily = monoFamily,
                )

                NvsValueColumn(
                    scanID = "Mean",
                    values = psnr.mean,
                    fontFamily = monoFamily,
                )
            }
        }

        RankCode(fontFamily = monoFamily, modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
private fun NvsHeaderColumn(modifier: Modifier = Modifier) {
    NvsColumn(
        modifier = modifier,
        headRow = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "PSNR",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = Icons.Default.North,
                    contentDescription = null,
                )
            }
        },
        sotaRow = {
            SotaMethod.entries.forEach { method ->
                HyperlinkText(
                    linkText = method.prefix,
                    linkUrl = method.link,
                    prefix = "",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        oursRow = { OurMethodRow() }
    )
}

@Composable
private fun NvsValueColumn(
    scanID: String,
    values: List<Float>,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    val rankedValues = if (values.isNotEmpty()) values.asRankedList(true) else emptyList()

    NvsColumn(
        align = true,
        modifier = modifier,
        headRow = {
            Text(
                text = scanID, style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        },
        sotaRow = {
            if (rankedValues.isNotEmpty()) {
                rankedValues.subList(0, 6).forEach { (value, rank) ->
                    Spacer(Modifier.height(9.dp))
                    RankedText(
                        text = value.format(),
                        rank = rank,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
        oursRow = {
            if (rankedValues.isNotEmpty()) {
                rankedValues.subList(6, 8).forEach { (value, rank) ->
                    Spacer(Modifier.height(8.dp))
                    RankedText(
                        text = value.format(),
                        rank = rank,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
    )
}

@Composable
private fun NvsColumn(
    align: Boolean = false,
    modifier: Modifier = Modifier,
    headRow: @Composable () -> Unit,
    sotaRow: @Composable () -> Unit,
    oursRow: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        horizontalAlignment = if (align) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primaryContainer)

        headRow()

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        sotaRow()

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)

        oursRow()

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 4.dp, color = MaterialTheme.colorScheme.primaryContainer)
    }
}

@Composable
private fun RankedText(
    text: String,
    rank: Rank,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = fontFamily,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = rank.getWeight(),
        color = rank.getColor(),
    )
}

@Composable
private fun RankCode(
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Rank.entries.forEach { rank ->
            Text(
                text = rank.code,
                modifier = Modifier.padding(end = 16.dp),
                fontFamily = fontFamily,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = rank.getWeight(),
                color = rank.getColor(),
            )
        }
    }
}

@Composable
private fun Rank.getColor() = when (this) {
    Rank.Best -> MaterialTheme.colorScheme.tertiary
    Rank.Good -> MaterialTheme.colorScheme.primary
    Rank.Nice -> MaterialTheme.colorScheme.onSurface
    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
}

private fun Rank.getWeight() = if (this == Rank.None || this == Rank.Nice) FontWeight.Normal else FontWeight.Bold

private fun Float.format(): String {
    val num = round(this * 100).toString().split('.')[0]
    val prefix = num.dropLast(2).takeIf { it.isNotEmpty() } ?: "0"
    val suffix = num.takeLast(2)
    return "$prefix.$suffix"
}

private fun List<Float>.asRankedList(descending: Boolean = true): List<Pair<Float, Rank>> {
    val topThreeMap = this
        .let { if (descending) sortedDescending() else sorted() }
        .distinct()
        .take(3)
        .mapIndexed { index, value ->
            val rank = when (index) {
                0 -> Rank.Best
                1 -> Rank.Good
                else -> Rank.Nice
            }
            value to rank
        }.toMap()
    return this.map { value -> value to (topThreeMap[value] ?: Rank.None) }
}