package com.ndming.kabob.ui

import com.ndming.kabob.fourierseries.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

private const val BUNDLE_PREFIX = "files/bundles"

/**
 * A [DrawableBundle] registers all relevant attributes to include a drawing to the list of all Fourier Series drawables.
 */
enum class DrawableBundle(val path: String, val drawable: DrawableResource, val displayName: StringResource) {
    TrebleClef("$BUNDLE_PREFIX/treble-clef.svg", Res.drawable.treble_clef, Res.string.fs_bundle_treble_clef_display_name),
    Vietnam("$BUNDLE_PREFIX/vietnam.svg", Res.drawable.vietnam, Res.string.fs_bundle_vietnam_display_name),
    Avenger("$BUNDLE_PREFIX/avenger.svg", Res.drawable.avenger, Res.string.fs_bundle_avenger_display_name),
    Sunflower("$BUNDLE_PREFIX/sunflower.svg", Res.drawable.sunflower, Res.string.fs_bundle_sunflower_display_name),
}