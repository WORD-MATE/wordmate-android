package com.wordmate.android.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

/** 좌측 뒤로가기 + 제목 + 우측 액션으로 구성된 상단바. */
@Composable
fun WmTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            onBack != null -> WmIconButton(Icons.AutoMirrored.Rounded.ArrowBackIos, "뒤로", onBack)
            onClose != null -> WmIconButton(Icons.Rounded.Close, "닫기", onClose)
            else -> Spacer(Modifier.width(12.dp))
        }
        Text(
            title,
            style = WmTypography.Title3,
            color = WmColors.TextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        actions()
    }
}

/** 하단 탭 루트 화면의 대제목 상단바. */
@Composable
fun WmLargeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 20.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = WmTypography.Title1, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
        actions()
    }
}

/** 회색 배경의 라운드 카드. */
@Composable
fun WmCard(
    modifier: Modifier = Modifier,
    color: Color = WmColors.Surface,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(WmRadius.lg)
            .background(color)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun WmSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
        if (actionText != null) {
            Row(
                modifier = Modifier
                    .clip(WmRadius.sm)
                    .clickable(enabled = onAction != null) { onAction?.invoke() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(actionText, style = WmTypography.Label2, color = WmColors.TextSecondary)
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    null,
                    tint = WmColors.TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/** 아이콘 + 제목 + 보조텍스트 + 화살표로 구성된 메뉴 행. */
@Composable
fun WmListRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingText: String? = null,
    titleColor: Color = WmColors.TextPrimary,
    showChevron: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = WmColors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
        }
        Text(title, style = WmTypography.Body2, color = titleColor, modifier = Modifier.weight(1f))
        if (trailingText != null) {
            Text(trailingText, style = WmTypography.Label2, color = WmColors.TextTertiary)
            Spacer(Modifier.width(4.dp))
        }
        if (trailing != null) {
            trailing()
        } else if (showChevron) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                null,
                tint = WmColors.TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun WmDivider(modifier: Modifier = Modifier, thickness: Dp = 1.dp) {
    HorizontalDivider(modifier = modifier, thickness = thickness, color = WmColors.Divider)
}

@Composable
fun WmThickDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(WmColors.Surface),
    )
}

/** 회색 원 안 사람 아이콘 아바타. */
@Composable
fun WmAvatar(modifier: Modifier = Modifier, size: Dp = 40.dp, background: Color = WmPalette.Gray06) {
    Box(
        modifier = modifier
            .size(size)
            .clip(WmRadius.sm)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Rounded.Person, null, tint = WmPalette.Gray12, modifier = Modifier.size(size * 0.6f))
    }
}

/** 작은 텍스트 배지 (예: "24단어", "완료", "명사"). */
@Composable
fun WmTag(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = WmColors.SurfaceVariant,
    contentColor: Color = WmColors.TextSecondary,
) {
    Text(
        text = text,
        style = WmTypography.Caption1Bold,
        color = contentColor,
        modifier = modifier
            .clip(WmRadius.xs)
            .background(background)
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

@Composable
fun WmEmptyState(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(56.dp).clip(WmRadius.md).background(WmColors.SurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = WmColors.Info, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(message, style = WmTypography.Label1, color = WmColors.TextTertiary)
    }
}
