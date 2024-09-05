package com.ndming.kabob

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainFourierSeries")
fun main() {
    ComposeViewport(document.body!!) {
        KabobTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("FS Page")
                }
            }
        }
    }
}