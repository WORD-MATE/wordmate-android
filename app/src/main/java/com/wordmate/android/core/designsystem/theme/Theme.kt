package com.wordmate.android.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/** Figma "Wordmate / 03 · Spacing & Radius". */
object WmSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val screenHorizontal = 20.dp
}

object WmRadius {
    val xs = RoundedCornerShape(4.dp)
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(20.dp)
    val sheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val full = RoundedCornerShape(percent = 50)
}

private val LightColors = lightColorScheme(
    primary = WmColors.Primary,
    onPrimary = WmColors.OnPrimary,
    secondary = WmColors.Black,
    onSecondary = WmPalette.Gray01,
    background = WmColors.Background,
    onBackground = WmColors.TextPrimary,
    surface = WmColors.Background,
    onSurface = WmColors.TextPrimary,
    surfaceVariant = WmColors.Surface,
    onSurfaceVariant = WmColors.TextSecondary,
    outline = WmColors.Border,
    outlineVariant = WmColors.Divider,
    error = WmColors.Error,
    onError = WmPalette.Gray01,
    surfaceContainer = WmColors.Background,
    surfaceContainerLow = WmColors.Background,
    surfaceContainerHigh = WmColors.Background,
)

private val MaterialTypography = Typography(
    headlineLarge = WmTypography.Headline1,
    headlineMedium = WmTypography.Headline2,
    titleLarge = WmTypography.Title1,
    titleMedium = WmTypography.Title2,
    titleSmall = WmTypography.Title3,
    bodyLarge = WmTypography.Body1,
    bodyMedium = WmTypography.Body2,
    bodySmall = WmTypography.Label2,
    labelLarge = WmTypography.Label1Bold,
    labelMedium = WmTypography.Caption1Bold,
    labelSmall = WmTypography.Caption2,
)

@Composable
fun WordMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTypography,
        shapes = Shapes(
            small = WmRadius.sm,
            medium = WmRadius.md,
            large = WmRadius.lg,
        ),
        content = content,
    )
}
