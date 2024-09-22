package com.ndming.kabob.ui.article

import com.ndming.kabob.main.generated.resources.Res
import com.ndming.kabob.main.generated.resources.main_article_tag_cg_name
import com.ndming.kabob.main.generated.resources.main_article_tag_ml_name
import org.jetbrains.compose.resources.StringResource

enum class ArticleTag(val tagLabel: String, val filterName: StringResource) {
    MachineLearning("ML", Res.string.main_article_tag_ml_name),
    ComputerGraphics("CG", Res.string.main_article_tag_cg_name),
}