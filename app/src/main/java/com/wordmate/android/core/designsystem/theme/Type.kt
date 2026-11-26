package com.wordmate.android.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Figma "Wordmate / 02 · Typography" 기반 타입 스케일.
 * 폰트 파일이 추가되면 [WmFontFamily] 만 교체하면 된다.
 */
val WmFontFamily: FontFamily = FontFamily.Default

private fun style(size: Int, lineHeight: Int, weight: FontWeight, letterSpacing: Double = -0.01) =
    TextStyle(
        fontFamily = WmFontFamily,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        letterSpacing = letterSpacing.em,
    )

object WmTypography {
    val Display = style(56, 60, FontWeight.ExtraBold, -0.04)
    val Score = style(48, 52, FontWeight.ExtraBold, -0.03)
    val WordLarge = style(32, 38, FontWeight.ExtraBold, -0.02)
    val Headline1 = style(24, 32, FontWeight.Bold, -0.02)
    val Headline2 = style(22, 30, FontWeight.Bold, -0.02)
    val Title1 = style(20, 28, FontWeight.Bold)
    val Title2 = style(18, 26, FontWeight.Bold)
    val Title3 = style(17, 24, FontWeight.SemiBold)
    val Body1Bold = style(16, 24, FontWeight.SemiBold)
    val Body1 = style(16, 24, FontWeight.Normal)
    val Body2Bold = style(15, 22, FontWeight.SemiBold)
    val Body2 = style(15, 22, FontWeight.Normal)
    val Label1Bold = style(14, 20, FontWeight.SemiBold)
    val Label1 = style(14, 20, FontWeight.Normal)
    val Label2Bold = style(13, 18, FontWeight.SemiBold)
    val Label2 = style(13, 18, FontWeight.Normal)
    val Caption1Bold = style(12, 16, FontWeight.SemiBold)
    val Caption1 = style(12, 16, FontWeight.Normal)
    val Caption2 = style(11, 14, FontWeight.Medium)
}
