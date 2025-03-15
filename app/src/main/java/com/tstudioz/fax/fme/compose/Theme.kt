package com.tstudioz.fax.fme.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

//private val LightColors = lightColorScheme(
//    primary = theme_light_primary,
//    onPrimary = theme_light_onPrimary,
//    primaryContainer = theme_light_primaryContainer,
//    onPrimaryContainer = theme_light_onPrimaryContainer,
//    secondary = theme_light_secondary,
//    onSecondary = theme_light_onSecondary,
//    secondaryContainer = theme_light_secondaryContainer,
//    onSecondaryContainer = theme_light_onSecondaryContainer,
//    tertiary = theme_light_tertiary,
//    onTertiary = theme_light_onTertiary,
//    tertiaryContainer = theme_light_tertiaryContainer,
//    onTertiaryContainer = theme_light_onTertiaryContainer,
//    error = theme_light_error,
//    errorContainer = theme_light_errorContainer,
//    onError = theme_light_onError,
//    onErrorContainer = theme_light_onErrorContainer,
//    background = theme_light_background,
//    onBackground = theme_light_onBackground,
//    surface = theme_light_surface,
//    onSurface = theme_light_onSurface,
//    surfaceVariant = theme_light_surfaceVariant,
//    onSurfaceVariant = theme_light_onSurfaceVariant,
//    outline = theme_light_outline,
//    inverseOnSurface = theme_light_inverseOnSurface,
//    inverseSurface = theme_light_inverseSurface,
//    inversePrimary = theme_light_inversePrimary,
//    surfaceTint = theme_light_surfaceTint,
//    outlineVariant = theme_light_outlineVariant,
//    scrim = theme_light_scrim,
//)

private val colors = darkColorScheme(
    primary = theme_dark_primary,
    onPrimary = theme_dark_onPrimary,
    primaryContainer = theme_dark_primaryContainer,
    onPrimaryContainer = theme_dark_onPrimaryContainer,
    secondary = theme_dark_secondary,
    onSecondary = theme_dark_onSecondary,
    secondaryContainer = theme_dark_secondaryContainer,
    onSecondaryContainer = theme_dark_onSecondaryContainer,
    tertiary = theme_dark_tertiary,
    onTertiary = theme_dark_onTertiary,
    tertiaryContainer = theme_dark_tertiaryContainer,
    onTertiaryContainer = theme_dark_onTertiaryContainer,
    error = theme_dark_error,
    errorContainer = theme_dark_errorContainer,
    onError = theme_dark_onError,
    onErrorContainer = theme_dark_onErrorContainer,
    background = theme_dark_background,
    onBackground = theme_dark_onBackground,
    surface = theme_dark_surface,
    onSurface = theme_dark_onSurface,
    surfaceVariant = theme_dark_surfaceVariant,
    onSurfaceVariant = theme_dark_onSurfaceVariant,
    outline = theme_dark_outline,
    inverseOnSurface = theme_dark_inverseOnSurface,
    inverseSurface = theme_dark_inverseSurface,
    inversePrimary = theme_dark_inversePrimary,
    surfaceTint = theme_dark_surfaceTint,
    outlineVariant = theme_dark_outlineVariant,
    scrim = theme_dark_scrim,
    surfaceDim = theme_dark_surfaceDimDark,
    surfaceBright = theme_dark_surfaceBrightDark,
    surfaceContainerLowest = theme_dark_surfaceContainerLowestDark,
    surfaceContainerLow = theme_dark_surfaceContainerLowDark,
    surfaceContainer = theme_dark_surfaceContainerDark,
    surfaceContainerHigh = theme_dark_surfaceContainerHighDark,
    surfaceContainerHighest = theme_dark_surfaceContainerHighestDark,
    )

data class ContentColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)

@SuppressLint("CompositionLocalNaming")
val AppComposition = staticCompositionLocalOf {
    ContentColors(
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified
    )
}

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val contentColors = ContentColors(
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFC3C3C3),
        tertiary = Color(0xFFAAAAAA)
    )

    CompositionLocalProvider(AppComposition.provides(contentColors)) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
            typography = CustomTypography,
            shapes = Shapes
        )
    }
}

val MaterialTheme.contentColors: ContentColors
    @Composable
    @ReadOnlyComposable
    get() = AppComposition.current