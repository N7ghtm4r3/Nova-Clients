package com.tecknobit.nova.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.tecknobit.nova.ui.theme.tester.backgroundLight
import com.tecknobit.nova.ui.theme.tester.errorContainerLight
import com.tecknobit.nova.ui.theme.tester.errorLight
import com.tecknobit.nova.ui.theme.tester.inverseOnSurfaceLight
import com.tecknobit.nova.ui.theme.tester.inversePrimaryLight
import com.tecknobit.nova.ui.theme.tester.inverseSurfaceLight
import com.tecknobit.nova.ui.theme.tester.onBackgroundLight
import com.tecknobit.nova.ui.theme.tester.onErrorContainerLight
import com.tecknobit.nova.ui.theme.tester.onErrorLight
import com.tecknobit.nova.ui.theme.tester.onPrimaryContainerLight
import com.tecknobit.nova.ui.theme.tester.onPrimaryLight
import com.tecknobit.nova.ui.theme.tester.onSecondaryContainerLight
import com.tecknobit.nova.ui.theme.tester.onSecondaryLight
import com.tecknobit.nova.ui.theme.tester.onSurfaceLight
import com.tecknobit.nova.ui.theme.tester.onSurfaceVariantLight
import com.tecknobit.nova.ui.theme.tester.onTertiaryContainerLight
import com.tecknobit.nova.ui.theme.tester.onTertiaryLight
import com.tecknobit.nova.ui.theme.tester.outlineLight
import com.tecknobit.nova.ui.theme.tester.outlineVariantLight
import com.tecknobit.nova.ui.theme.tester.primaryContainerLight
import com.tecknobit.nova.ui.theme.tester.primaryLight
import com.tecknobit.nova.ui.theme.tester.scrimLight
import com.tecknobit.nova.ui.theme.tester.secondaryContainerLight
import com.tecknobit.nova.ui.theme.tester.secondaryLight
import com.tecknobit.nova.ui.theme.tester.surfaceDimLight
import com.tecknobit.nova.ui.theme.tester.surfaceLight
import com.tecknobit.nova.ui.theme.tester.surfaceVariantLight
import com.tecknobit.nova.ui.theme.tester.tertiaryContainerLight
import com.tecknobit.nova.ui.theme.tester.tertiaryLight

/**
 * **LightColors** default light colors scheme
 */
private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

/**
 * **DarkColors** default dark colors scheme
 */
private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

/**
 * **RedSchemeColors** red light colors scheme
 */
val RedSchemeColors = lightColorScheme(
    primary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_primary,
    onPrimary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onPrimary,
    primaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_primaryContainer,
    onPrimaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onPrimaryContainer,
    secondary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_secondary,
    onSecondary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onSecondary,
    secondaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onSecondaryContainer,
    tertiary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_tertiary,
    onTertiary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onTertiary,
    tertiaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onTertiaryContainer,
    error = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_error,
    errorContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_errorContainer,
    onError = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onError,
    onErrorContainer = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onErrorContainer,
    background = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_background,
    onBackground = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onBackground,
    surface = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_surface,
    onSurface = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onSurface,
    surfaceVariant = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_onSurfaceVariant,
    outline = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_outline,
    inverseOnSurface = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_inverseOnSurface,
    inverseSurface = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_inverseSurface,
    inversePrimary = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_inversePrimary,
    surfaceTint = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_surfaceTint,
    outlineVariant = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_outlineVariant,
    scrim = com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_scrim,
)

/**
 * **VioletSchemeColors** violet light colors scheme
 */
val VioletSchemeColors = lightColorScheme(
    primary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_primary,
    onPrimary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onPrimary,
    primaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_primaryContainer,
    onPrimaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onPrimaryContainer,
    secondary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_secondary,
    onSecondary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onSecondary,
    secondaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onSecondaryContainer,
    tertiary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_tertiary,
    onTertiary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onTertiary,
    tertiaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onTertiaryContainer,
    error = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_error,
    errorContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_errorContainer,
    onError = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onError,
    onErrorContainer = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onErrorContainer,
    background = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_background,
    onBackground = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onBackground,
    surface = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_surface,
    onSurface = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onSurface,
    surfaceVariant = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_onSurfaceVariant,
    outline = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_outline,
    inverseOnSurface = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_inverseOnSurface,
    inverseSurface = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_inverseSurface,
    inversePrimary = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_inversePrimary,
    surfaceTint = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_surfaceTint,
    outlineVariant = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_outlineVariant,
    scrim = com.tecknobit.nova.ui.theme.tagstheme.issue.md_theme_light_scrim,
)

/**
 * **LightblueSchemeColors** light blue colors scheme
 */
val LightblueSchemeColors = lightColorScheme(
    primary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_primary,
    onPrimary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onPrimary,
    primaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_primaryContainer,
    onPrimaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onPrimaryContainer,
    secondary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_secondary,
    onSecondary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onSecondary,
    secondaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onSecondaryContainer,
    tertiary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_tertiary,
    onTertiary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onTertiary,
    tertiaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onTertiaryContainer,
    error = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_error,
    errorContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_errorContainer,
    onError = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onError,
    onErrorContainer = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onErrorContainer,
    background = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_background,
    onBackground = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onBackground,
    surface = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_surface,
    onSurface = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onSurface,
    surfaceVariant = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_onSurfaceVariant,
    outline = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_outline,
    inverseOnSurface = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_inverseOnSurface,
    inverseSurface = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_inverseSurface,
    inversePrimary = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_inversePrimary,
    surfaceTint = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_surfaceTint,
    outlineVariant = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_outlineVariant,
    scrim = com.tecknobit.nova.theme.tagstheme.layoutchange.md_theme_light_scrim,
)

/**
 * **BlueSchemeColors** blue light colors scheme
 */
val BlueSchemeColors = lightColorScheme(
    primary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_primary,
    onPrimary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onPrimary,
    primaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_primaryContainer,
    onPrimaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onPrimaryContainer,
    secondary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_secondary,
    onSecondary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onSecondary,
    secondaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onSecondaryContainer,
    tertiary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_tertiary,
    onTertiary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onTertiary,
    tertiaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onTertiaryContainer,
    error = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_error,
    errorContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_errorContainer,
    onError = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onError,
    onErrorContainer = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onErrorContainer,
    background = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_background,
    onBackground = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onBackground,
    surface = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_surface,
    onSurface = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onSurface,
    surfaceVariant = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_onSurfaceVariant,
    outline = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_outline,
    inverseOnSurface = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_inverseOnSurface,
    inverseSurface = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_inverseSurface,
    inversePrimary = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_inversePrimary,
    surfaceTint = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_surfaceTint,
    outlineVariant = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_outlineVariant,
    scrim = com.tecknobit.nova.theme.tagstheme.tip.md_theme_light_scrim,
)

/**
 * **TesterThemeColor** the color scheme for the [Role.Tester]
 */
val TesterThemeColor = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    errorContainer = errorContainerLight,
    onError = onErrorLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inverseSurface = inverseSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceTint = surfaceDimLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
)

/**
 * Function to set the Nova theme to the content
 *
 * @param content: the content to display
 */
@Composable
fun NovaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}