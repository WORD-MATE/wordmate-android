package com.wordmate.android.feature.wordpack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmBottomSheet
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmTextField
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmTypography

/** 단어 추가/수정 바텀시트 (Figma "수정 모달"). */
@Composable
fun WordEditSheet(
    title: String,
    initialTerm: String,
    initialMeaning: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (term: String, meaning: String) -> Unit,
) {
    var term by remember { mutableStateOf(initialTerm) }
    var meaning by remember { mutableStateOf(initialMeaning) }
    WmBottomSheet(onDismiss) {
        Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary)
        Spacer(Modifier.height(20.dp))
        Text("영어", style = WmTypography.Caption1Bold, color = WmColors.TextSecondary)
        Spacer(Modifier.height(6.dp))
        WmTextField(term, { term = it }, "예) abandon")
        Spacer(Modifier.height(14.dp))
        Text("뜻", style = WmTypography.Caption1Bold, color = WmColors.TextSecondary)
        Spacer(Modifier.height(6.dp))
        WmTextField(meaning, { meaning = it }, "예) 버리다, 포기하다")
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WmButton("취소", onDismiss, Modifier.weight(1f), style = WmButtonStyle.Secondary)
            WmButton(
                confirmText,
                { onConfirm(term, meaning) },
                Modifier.weight(1f),
                enabled = term.isNotBlank() && meaning.isNotBlank(),
            )
        }
    }
}

@Composable
fun DeleteConfirmSheet(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    WmBottomSheet(onDismiss) {
        Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(description, style = WmTypography.Label1, color = WmColors.TextSecondary)
        Spacer(Modifier.height(24.dp))
        WmButton("삭제", onConfirm, Modifier.fillMaxWidth(), style = WmButtonStyle.Danger)
        Spacer(Modifier.height(8.dp))
        WmButton("취소", onDismiss, Modifier.fillMaxWidth(), style = WmButtonStyle.Secondary)
    }
}
