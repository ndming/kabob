package com.ndming.kabob.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.core.generated.resources.github
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperBanner(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = "GS-2M: Gaussian Splatting for",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = "Joint Mesh Reconstruction and Material Decomposition",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Medium,
        )

        Spacer(Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Author.entries.forEach { author ->
                PaperAuthor(author.fullName, author.affiliates)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
            horizontalArrangement = Arrangement.Center
        ) {
            Affiliation.entries.forEachIndexed { index, affiliation ->
                AuthorAffiliation(index + 1, affiliation.title)
            }
        }

        Spacer(Modifier.height(32.dp))

        Row {
            Button(
                onClick = { window.open("composeResources/com.ndming.kabob.gs_2m.generated.resources/files/report.pdf", "_blank") },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp).size(20.dp),
                    )

                    DisableSelection {
                        Text(
                            text = "Report",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = { window.open("https://github.com/ndming/GS-2M", "_blank") },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(com.ndming.kabob.core.generated.resources.Res.drawable.github),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp).size(20.dp),
                    )

                    DisableSelection {
                        Text(
                            text = "Code",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Suppress("SpellCheckingInspection")
private enum class Author(val fullName: String, val affiliates: List<Int>) {
    Minh("Dinh Minh Nguyen", listOf(1)),
    Malte("Malte Avenhaus", listOf(2)),
    Thomas("Thomas Lindemeier", listOf(2)),
    Phlippe("Philippe Colantoni", listOf(3)),
}

@Composable
private fun PaperAuthor(
    name: String,
    affiliates: List<Int>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(horizontal = 24.dp).padding(top = 12.dp)) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(end = 12.dp * affiliates.size),
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.align(Alignment.TopEnd),
            text = affiliates.joinToString(separator = ", "),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Suppress("SpellCheckingInspection")
enum class Affiliation(val title: String) {
    NTNU("Norwegian University of Science and Technology"),
    ZEISS("Carl ZEISS AG"),
    UJM("Université Jean Monnet")
}

@Composable
private fun AuthorAffiliation(
    index: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(horizontal = 24.dp)) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(start = 12.dp, top = 4.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = index.toString(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}