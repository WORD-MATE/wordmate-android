package com.wordmate.android.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Figma "Wordmate / 01 · Color" 팔레트.
 */
object WmPalette {
    // Gray
    val Gray01 = Color(0xFFFFFFFF)
    val Gray02 = Color(0xFFF8F9FA)
    val Gray03 = Color(0xFFEFF1F3)
    val Gray04 = Color(0xFFE8E9EA)
    val Gray05 = Color(0xFFE8EBEB)
    val Gray06 = Color(0xFFE5E7EA)
    val Gray08 = Color(0xFFDFE3E6)
    val Gray10 = Color(0xFFD1D3D6)
    val Gray11 = Color(0xFFC4C9CF)
    val Gray12 = Color(0xFF737A83)
    val Gray14 = Color(0xFF5B626C)
    val Gray15 = Color(0xFF4E5968)
    val Gray16 = Color(0xFF3D3D3D)
    val Gray17 = Color(0xFF121417)
    val Gray18 = Color(0xFF0B0C0E)

    // Lime (brand)
    val Lime01 = Color(0xFFB6E02A)
    val Lime02 = Color(0xFF96C814)

    // Yellow / Orange
    val Yellow02 = Color(0xFFFFBF00)
    val Orange02 = Color(0xFFFFB331)
    val Orange06 = Color(0xFFFF8000)
    val Orange07 = Color(0xFFD48500)

    // Red
    val Red02 = Color(0xFFEF4452)
    val Red03 = Color(0xFFF03E3E)
    val Red04 = Color(0xFFC13D2C)

    // Blue
    val Blue02 = Color(0xFF3181F5)
    val Blue04 = Color(0xFF007FF9)

    // Teal / Mint
    val Teal01 = Color(0xFF00C9C7)
    val Mint = Color(0xFF6BE3B5)
}

/** 화면에서 사용하는 의미 기반 컬러 토큰. */
object WmColors {
    val Primary = WmPalette.Lime01
    val PrimaryDark = WmPalette.Lime02
    val OnPrimary = WmPalette.Gray17

    val Background = WmPalette.Gray01
    val Surface = WmPalette.Gray02
    val SurfaceVariant = WmPalette.Gray03
    val SurfacePressed = WmPalette.Gray06
    val Divider = WmPalette.Gray06
    val Border = WmPalette.Gray08

    val TextPrimary = WmPalette.Gray17
    val TextSecondary = WmPalette.Gray14
    val TextTertiary = WmPalette.Gray12
    val TextDisabled = WmPalette.Gray11

    val Black = WmPalette.Gray17
    val Error = WmPalette.Red03
    val ErrorStrong = WmPalette.Red04
    val Success = WmPalette.Lime02
    val Info = WmPalette.Blue02
    val Warning = WmPalette.Orange07
    val Notification = WmPalette.Red02

    val Scrim = Color(0x80121417)

    val Gold = WmPalette.Yellow02
    val Silver = WmPalette.Gray11
    val Bronze = Color(0xFFD08A4C)

    val HeroGradient = Brush.linearGradient(
        listOf(WmPalette.Lime01, WmPalette.Mint),
    )
}
