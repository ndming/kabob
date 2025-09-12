package com.ndming.kabob.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.*
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import com.ndming.kabob.ui.KabobFooter
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.jetbrains.compose.resources.*

@Composable
fun HomePage(
    portrait: Boolean,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    val pageWidth = 960.dp
    SelectionContainer {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            ProfileHeadlines(pageWidth, portrait)

            Spacer(Modifier.height(32.dp))
            NewsPanel(
                portrait = portrait,
                modifier = Modifier
                    .widthIn(max = pageWidth)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(Modifier.height(32.dp))
            ProfileDetails(pageWidth, portrait)

            Spacer(Modifier.height(64.dp))
            KabobFooter()
        }
    }
}

@Composable
private fun ColumnScope.ProfileHeadlines(
    pageWidth: Dp,
    portrait: Boolean,
) {
    Card(
        modifier = Modifier
            .widthIn(max = pageWidth)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (!portrait) {
                HomeAvatar()
                Spacer(Modifier.width(32.dp))
            }
            HomeHeadlines(portrait = portrait)
        }
    }

    Spacer(Modifier.height(32.dp))

    Text(
        text = stringResource(Res.string.home_info_overview),
        textAlign = TextAlign.Justify,
        modifier = Modifier
            .widthIn(max = pageWidth)
            .padding(horizontal = 24.dp)
            .align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun ColumnScope.ProfileDetails(
    pageWidth: Dp,
    portrait: Boolean,
) {
    Text(
        text = stringResource(Res.string.home_info_academic),
        textAlign = TextAlign.Justify,
        modifier = Modifier
            .widthIn(max = pageWidth)
            .padding(horizontal = 24.dp)
            .align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(24.dp))

    Section(
        title = stringResource(Res.string.home_research_section_title),
        iconImage = Icons.Outlined.Book,
        modifier = Modifier
            .widthIn(max = pageWidth)
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.CenterHorizontally),
    ) {
        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "Neural Rendering",
            trailingText = "Gaussian Splatting, 3D Reconstruction, Image-based Rendering"
        )

        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "Computer Graphics",
            trailingText = "Global Illumination, Deep Learning for Super Sampling"
        )

        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "Computer Vision",
            trailingText = "Scene Reconstruction, Scene Decomposition"
        )

        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "Generative AI",
            trailingText = "Transformers and Diffusion Models for Visual Computing"
        )
    }

    Section(
        title = stringResource(Res.string.home_personal_section_title),
        iconImage = Icons.Default.Terminal,
        modifier = Modifier
            .widthIn(max = pageWidth)
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.CenterHorizontally),
    ) {
        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "torpedo",
            leadingLink = "https://github.com/ndming/torpedo",
            trailingText = "Vulkan-based renderer for semi-transparent particles",
        )

        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "void",
            leadingLink = "https://github.com/ndming/virtual-object-insertion",
            trailingText = "virtual objection insertion with deep learning",
        )

        SectionItem(
            modifier = Modifier.padding(top = 16.dp),
            portrait = portrait,
            leadingText = "pan",
            leadingLink = "https://github.com/ndming/pan",
            trailingText = "CLI tool for real-time HSI analysis",
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
@Composable
private fun NewsPanel(
    portrait: Boolean,
    modifier: Modifier = Modifier,
) {
    var expandNews by remember { mutableStateOf(true) }
    var showScroll by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val monoFamily = getJetBrainsMonoFamily()

    var bytes by rememberSaveable { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = Res.readBytes("files/news.md")
    }

    OutlinedCard(
        modifier = modifier.heightIn(max = 256.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "News",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                TextButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = { expandNews = !expandNews }
                ) {
                    DisableSelection {
                        Text(text = if (expandNews) "Collapse" else "Expand")
                    }
                }
            }

            if (expandNews) {
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
            }

            AnimatedVisibility(visible = expandNews) {
                if (bytes.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally))
                } else {
                    val text = bytes.decodeToString()
                    val tree = MarkdownParser(CommonMarkFlavourDescriptor()).buildMarkdownTreeFromString(text)
                    val news = mutableListOf<ASTNode>()
                    collectListItems(tree, text, news)

                    val scrollBarColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                    val surfaceColor = MaterialTheme.colorScheme.surface
                    val scrollBarLengthFraction = (if (portrait) 2.0f else 4.0f) / news.size.toFloat()
                    val scrollFraction = listState.firstVisibleItemIndex.toFloat() / listState.layoutInfo.totalItemsCount.toFloat()
                    val scrollBarVisibleOffset by animateFloatAsState(
                        targetValue = if (showScroll) 12.0f else 0.0f,
                        animationSpec = spring(Spring.DampingRatioLowBouncy),
                    )

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPointerEvent(PointerEventType.Enter) { showScroll = true }
                            .onPointerEvent(PointerEventType.Exit) { showScroll = false }
                            .drawWithCache {
                                val topBrush = Brush.verticalGradient(listOf(surfaceColor.copy(alpha = 0.4f), surfaceColor.copy(alpha = 0.0f)))
                                val botBrush = Brush.verticalGradient(listOf(surfaceColor.copy(alpha = 0.0f), surfaceColor.copy(alpha = 0.4f)))

                                onDrawWithContent {
                                    drawContent()

                                    drawRect(
                                        brush = topBrush,
                                        size = Size(size.width, 20.0f)
                                    )

                                    drawRect(
                                        brush = botBrush,
                                        topLeft = Offset(0.0f, size.height - 20.0f)
                                    )

                                    drawRoundRect(
                                        color = scrollBarColor,
                                        cornerRadius = CornerRadius(2.0f, 2.0f),
                                        topLeft = Offset(size.width - scrollBarVisibleOffset, (size.height - 24.0f) * scrollFraction + 12.0f),
                                        size = Size(4.0f, (size.height - 24.0f) * scrollBarLengthFraction),
                                    )
                                }
                            },
                    ) {
                        items(news) { newsItem ->
                            val paragraph = newsItem.children[1] // 0: item bullet, 1: paragraph
                            MarkdownParagraph(paragraph, text, monoFamily)
                        }
                    }
                }
            }
        }
    }
}

private fun collectListItems(node: ASTNode, text: String, results: MutableList<ASTNode>) {
    if (node.type == MarkdownElementTypes.LIST_ITEM) {
        results.add(node)
    }
    node.children.forEach { child -> collectListItems(child, text, results) }
}

@Composable
private fun MarkdownParagraph(
    paragraph: ASTNode,
    text: String,
    monoFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    val content = text.substring(paragraph.startOffset, paragraph.endOffset)
    val date = content.substringBefore('#')

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontFamily = monoFamily, color = MaterialTheme.colorScheme.primary)) {
                append("$date ")
            }

            // Drop the date text and white space nodes
            paragraph.children.drop(2).forEach { node ->
                when (node.type.name) {
                    "TEXT" -> append(text.substring(node.startOffset, node.endOffset))
                    "'" -> append("'")
                    "(" -> append("(")
                    ")" -> append(")")
                    ":" -> append(":")
                    "WHITE_SPACE" -> append(' ')
                    "STRONG" -> node.children.forEach {
                        when (it.type.name) {
                            "TEXT" -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(text.substring(it.startOffset, it.endOffset))
                            }
                            "WHITE_SPACE" -> append(' ')
                        }
                    }
                    "EMPH" -> node.children.forEach {
                        when (it.type.name) {
                            "TEXT" -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(text.substring(it.startOffset, it.endOffset))
                            }
                            "WHITE_SPACE" -> append(' ')
                        }
                    }
                    "INLINE_LINK" -> {
                        val linkTxtNode = node.children[0]
                        val linkDstNode = node.children[2]

                        withLink(
                            LinkAnnotation.Url(
                                url = text.substring(linkDstNode.startOffset, linkDstNode.endOffset),
                                styles = TextLinkStyles(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Light,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                )
                            )
                        ) {
                            append(
                                text
                                    .substring(linkTxtNode.startOffset, linkTxtNode.endOffset)
                                    .trimStart('[').trimEnd(']')
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeAvatar(modifier: Modifier = Modifier) {
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = Res.readBytes("files/ava.jpg")
    }

    if (bytes.isNotEmpty()) {
        Surface(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.tertiary),
        ) {
            Image(
                bitmap = bytes.decodeToImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(128.dp),
            )
        }
    } else {
        Image(
            imageVector = vectorResource(Res.drawable.kabob_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(128.dp),
        )
    }
}

@Composable
private fun HomeHeadlines(
    portrait: Boolean,
    modifier: Modifier = Modifier,
) {
    val headlineDetailStyle = MaterialTheme.typography.bodyLarge

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.banner_title),
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(Modifier.width(16.dp))
            Image(
                imageVector = vectorResource(Res.drawable.vietnam_flag),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.height(20.dp),
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.School, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )

            if (portrait) {
                Text(
                    text = stringResource(Res.string.banner_profession),
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                @Suppress("SpellCheckingInspection")
                HyperlinkText(
                    linkText = "NTNU",
                    linkUrl = "https://www.ntnu.edu/",
                    prefix = {
                        Text(
                            text = stringResource(Res.string.banner_profession) + " – Erasmus Mundus COSI  @  ",
                            style = headlineDetailStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Loyalty, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            if (portrait) {
                Text(
                    text = buildAnnotatedString {
                        SkillTag.entries.forEachIndexed { index, tag ->
                            if (index > 0) {
                                append(", ")
                            }
                            append(stringResource(tag.title))
                        }
                    },
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = stringResource(Res.string.banner_interests),
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

enum class SkillTag(val title: StringResource) {
    Cpp(Res.string.home_skill_tag_cpp),
    Vulkan(Res.string.home_skill_tag_vulkan),
    PyTorch(Res.string.home_skill_tag_torch),
}