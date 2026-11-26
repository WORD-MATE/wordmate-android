package com.wordmate.android.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

@Composable
fun WmProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    color: Color = WmColors.Primary,
    trackColor: Color = WmPalette.Gray06,
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "progress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(WmRadius.full)
            .background(trackColor),
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .clip(WmRadius.full)
                .background(color),
        )
    }
}

/** 하단 시트 (단어팩 고르기, 학습 중단 확인, 신고 등). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WmBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = WmRadius.sheet,
        containerColor = WmColors.Background,
        scrimColor = WmColors.Scrim,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
            content = content,
        )
    }
}

/** 제목/설명 + 확인/취소 버튼 시트. */
@Composable
fun WmConfirmSheet(
    title: String,
    description: String?,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmStyle: WmButtonStyle = WmButtonStyle.Primary,
) {
    WmBottomSheet(onDismiss = onDismiss) {
        Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary)
        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(description, style = WmTypography.Label1, color = WmColors.TextSecondary)
        }
        Spacer(Modifier.height(24.dp))
        WmButton(confirmText, onConfirm, Modifier.fillMaxWidth(), style = confirmStyle)
        Spacer(Modifier.height(8.dp))
        WmButton(dismissText, onDismiss, Modifier.fillMaxWidth(), style = WmButtonStyle.Secondary)
    }
}

@Composable
fun WmDialog(
    title: String,
    description: String?,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "취소",
    confirmStyle: WmButtonStyle = WmButtonStyle.Primary,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .clip(WmRadius.xl)
                .background(WmColors.Background)
                .padding(24.dp),
        ) {
            Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary)
            if (description != null) {
                Spacer(Modifier.height(8.dp))
                Text(description, style = WmTypography.Label1, color = WmColors.TextSecondary)
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WmButton(dismissText, onDismiss, Modifier.weight(1f), style = WmButtonStyle.Secondary, height = 48.dp)
                WmButton(confirmText, onConfirm, Modifier.weight(1f), style = confirmStyle, height = 48.dp)
            }
        }
    }
}
