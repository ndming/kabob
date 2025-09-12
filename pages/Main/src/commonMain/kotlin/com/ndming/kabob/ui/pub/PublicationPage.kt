package com.ndming.kabob.ui.pub

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.Res
import com.ndming.kabob.markup.HyperlinkText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@Serializable
data class PubLink(
    val text: String,
    val url: String,
)

@Serializable
data class Publication(
    val title: String,
    val year: Int,
    val authors: List<String>,
    val links: List<PubLink>,
    val highlight: String,
    val media: String,
    val tags: List<String>,
    val abstract: String,
)

@OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
@Composable
fun PublicationPage(
    modifier: Modifier = Modifier,
) {
    val pageWidth = 960.dp
    val listState = rememberLazyListState()

    var dataBytes by rememberSaveable { mutableStateOf(ByteArray(0)) }
    var dateBytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        dataBytes = Res.readBytes("files/pubs/data.json")
        dateBytes = Res.readBytes("files/pubs/date.txt")
    }

    if (dataBytes.isEmpty()) {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                LinearProgressIndicator(modifier = modifier.fillMaxWidth())
            }
        }
    } else {
        val publications = rememberSaveable(dataBytes) { Json.decodeFromString<List<Publication>>(dataBytes.decodeToString()) }
        val grouped = publications.groupBy { it.year }

        SelectionContainer(modifier = modifier) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                grouped.forEach { (year, pubs) ->
                    stickyHeader {
                        YearHeader(
                            year = year,
                            modifier = Modifier
                                .widthIn(max = pageWidth)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        )
                    }

                    items(pubs) { publication ->
                        Publication(
                            publication = publication,
                            modifier = Modifier
                                .widthIn(max = pageWidth)
                                .fillMaxWidth()
                                .padding(horizontal = 26.dp)
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(64.dp))
                    Text(
                        text = "Last updated: ${dateBytes.decodeToString()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .widthIn(max = pageWidth)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun YearHeader(
    year: Int,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
private fun Publication(
    publication: Publication,
    modifier: Modifier = Modifier,
) {
    var expand by remember { mutableStateOf(false) }
    var mediaBytes by remember { mutableStateOf(ByteArray(0)) }

    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(start = 32.dp, end = 24.dp, top = 24.dp, bottom = 12.dp)) {
            Text(
                text = publication.title,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "[${publication.highlight}]")

            Spacer(Modifier.height(12.dp))
            Text(
                text = buildAnnotatedString {
                    publication.authors.forEachIndexed { index, author ->
                        if (index > 0) {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(", ")
                            }
                        }
                        if (author == "Dinh Minh Nguyen" || author == "Minh Nguyen-Dinh" || author == "Minh Dinh Nguyen") {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            ) {
                                append(author)
                            }
                        } else {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Light,
                                )
                            ) {
                                append(author)
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    publication.tags.forEach { tag ->
                        when (tag) {
                            "thesis" -> {
                                Icon(
                                    imageVector = Icons.Default.HistoryEdu,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "poster" -> {
                                Icon(
                                    imageVector = Icons.Default.AmpStories,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "conference" -> {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "journal" -> {
                                Icon(
                                    imageVector = Icons.Outlined.AutoStories,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "presentation" -> {
                                Icon(
                                    imageVector = Icons.Default.CoPresent,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "report" -> {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "dataset" -> {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                            "code" -> {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                }

                IconButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = { expand = !expand },
                ) {
                    Icon(
                        imageVector = if (expand) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            AnimatedVisibility(visible = expand) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()

                    Spacer(Modifier.height(24.dp))
                    FlowRow {
                        publication.links.forEachIndexed { index, link ->
                            if (index > 0) {
                                Text(
                                    text = "|",
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                            HyperlinkText(linkText = link.text, linkUrl = link.url, prefix = "", suffix = "")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(text = publication.abstract, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                    Spacer(Modifier.height(24.dp))
                    if (publication.media.isNotEmpty() && mediaBytes.isEmpty()) {
                        LaunchedEffect(Unit) {
                            mediaBytes = Res.readBytes("files/pubs/${publication.media}")
                        }
                    }
                    if (publication.media.isNotEmpty() && mediaBytes.isEmpty()) {
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                        Spacer(Modifier.height(12.dp))
                    }
                    else if (publication.media.isNotEmpty()) {
                        Image(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            bitmap = mediaBytes.decodeToImageBitmap(),
                            contentDescription = null,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}