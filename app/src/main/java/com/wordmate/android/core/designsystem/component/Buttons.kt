package com.wordmate.android.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

enum class WmButtonStyle { Primary, Dark, Secondary, Outline, Danger, Mint }

private data class ButtonColors(val container: Color, val content: Color, val border: Color? = null)

private fun WmButtonStyle.colors(enabled: Boolean): ButtonColors = when {
    !enabled -> ButtonColors(WmColors.SurfaceVariant, WmColors.TextDisabled)
    else -> when (this) {
        WmButtonStyle.Primary -> ButtonColors(WmColors.Primary, WmColors.OnPrimary)
        WmButtonStyle.Dark -> ButtonColors(WmColors.Black, WmPalette.Gray01)
        WmButtonStyle.Secondary -> ButtonColors(WmColors.SurfaceVariant, WmColors.TextPrimary)
        WmButtonStyle.Outline -> ButtonColors(WmColors.Background, WmColors.TextPrimary, WmColors.Border)
        WmButtonStyle.Danger -> ButtonColors(WmColors.ErrorStrong, WmPalette.Gray01)
        WmButtonStyle.Mint -> ButtonColors(Color(0xFFD9F7E9), WmColors.TextPrimary)
    }
}

/** Figma "05 · Actions & Inputs" 의 기본 버튼. */
@Composable
fun WmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WmButtonStyle = WmButtonStyle.Primary,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    val colors = style.colors(enabled)
    Row(
        modifier = modifier
            .height(height)
            .clip(WmRadius.md)
            .background(colors.container)
            .then(if (colors.border != null) Modifier.border(1.dp, colors.border, WmRadius.md) else Modifier)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, null, tint = colors.content, modifier = Modifier.size(18.dp))
            Box(Modifier.size(6.dp))
        }
        Text(text, style = WmTypography.Body2Bold, color = colors.content)
        if (trailingIcon != null) {
            Box(Modifier.size(6.dp))
            Icon(trailingIcon, null, tint = colors.content, modifier = Modifier.size(18.dp))
        }
    }
}

/** 화면 하단 고정 CTA. */
@Composable
fun WmBottomCta(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: WmButtonStyle = WmButtonStyle.Primary,
) {
    WmButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        style = style,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

@Composable
fun WmSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WmButtonStyle = WmButtonStyle.Primary,
    trailingIcon: ImageVector? = null,
) {
    WmButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        style = style,
        height = 36.dp,
        trailingIcon = trailingIcon,
    )
}

@Composable
fun WmIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = WmColors.TextPrimary,
    showBadge: Boolean = false,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(WmRadius.full)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material3.ripple(bounded = false),
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(24.dp))
        if (showBadge) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(7.dp)
                    .clip(WmRadius.full)
                    .background(WmColors.Notification),
            )
        }
    }
}
