// Plugins that will be used throughout the project, but not the root project. This is necessary to avoid
// the plugins to be loaded multiple times in each subproject's classloader
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}