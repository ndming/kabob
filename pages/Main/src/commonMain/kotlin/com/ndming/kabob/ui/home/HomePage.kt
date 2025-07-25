package com.ndming.kabob.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Loyalty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HomePage(
    portrait: Boolean,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                    ) {
                        if (this@BoxWithConstraints.maxWidth > 640.dp) {
                            HomeAvatar()
                            Spacer(Modifier.width(32.dp))
                        }
                        HomeHeadlines()
                    }
                }
            }

            Text(
                text = stringResource(Res.string.home_overview_info),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 26.dp).padding(top = 24.dp, bottom = 12.dp).widthIn(max = 1024.dp),
            )

            Row(modifier = Modifier.basicMarquee()) {
                SkillTag.entries.forEach { skill ->
                    ElevatedAssistChip(
                        onClick = {},
                        label = { Text(text = stringResource(skill.tagName)) },
                        modifier = Modifier.padding(start = 24.dp),
                    )
                }
            }

            Section(
                title = stringResource(Res.string.home_interest_section_title),
                iconImage = Icons.Outlined.Loyalty,
                modifier = Modifier.padding(24.dp),
            ) {
                SectionItem(
                    modifier = Modifier.padding(top = 16.dp),
                    portrait = portrait,
                    leadingText = "Neural Rendering",
                    trailingText = "3D reconstruction and scene decomposition from multi-view images"
                )

                SectionItem(
                    modifier = Modifier.padding(top = 16.dp),
                    portrait = portrait,
                    leadingText = "Graphics Programming",
                    trailingText = "implementation of graphics and parallel algorithms"
                )
            }

            Section(
                title = stringResource(Res.string.home_project_section_title),
                iconImage = Icons.Default.Terminal,
                modifier = Modifier.padding(24.dp),
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

//                SectionItem(
//                    modifier = Modifier.padding(top = 16.dp),
//                    portrait = portrait,
//                    leadingText = "urgent",
//                    leadingLink = "https://github.com/ndming/remote-patient-monitoring",
//                    trailingText = "real-time remote medical monitoring",
//                )
            }
        }
    }
}

@Composable
private fun HomeAvatar(modifier: Modifier = Modifier) {
    Image(
        imageVector = vectorResource(Res.drawable.kabob_logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(128.dp),
    )
}

@Composable
private fun HomeHeadlines(modifier: Modifier = Modifier) {
    val headlineDetailStyle = MaterialTheme.typography.bodyLarge

    Column(modifier = modifier.fillMaxWidth()) {
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
            Text(
                text = stringResource(Res.string.banner_profession),
                style = headlineDetailStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(Res.string.banner_location),
                style = headlineDetailStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

enum class SkillTag(val tagName: StringResource) {
    Cpp(Res.string.home_skill_tag_cpp),
    Vulkan(Res.string.home_skill_tag_vulkan),
    GLSL(Res.string.home_skill_tag_glsl),
    Slang(Res.string.home_skill_tag_slang),
    Python(Res.string.home_skill_tag_python),
    PyTorch(Res.string.home_skill_tag_torch),
}