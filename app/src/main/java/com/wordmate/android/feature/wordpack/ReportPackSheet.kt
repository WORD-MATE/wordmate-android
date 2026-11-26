package com.wordmate.android.feature.wordpack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.wordmate.android.core.designsystem.component.WmBottomSheet
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCheckCircle
import com.wordmate.android.core.designsystem.component.WmTextField
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

/** 신고 사유 선택 시트. 단어팩·대결 상대 신고에 공용으로 쓴다. */
@Composable
fun ReportSheet(
    title: String,
    description: String,
    reasons: List<String>,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, detail: String) -> Unit,
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf("") }
    WmBottomSheet(onDismiss) {
        Text(title, style = WmTypography.Title2, color = WmColors.TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(description, style = WmTypography.Label2, color = WmColors.TextSecondary)
        Spacer(Modifier.height(16.dp))
        reasons.forEach { reason ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(WmRadius.sm)
                    .clickable(role = Role.RadioButton) { selected = reason }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WmCheckCircle(selected == reason, size = 20)
                Spacer(Modifier.width(10.dp))
                Text(reason, style = WmTypography.Label1, color = WmColors.TextPrimary)
            }
        }
        Spacer(Modifier.height(8.dp))
        WmTextField(detail, { detail = it }, "자세한 내용 (선택)", singleLine = false, minHeight = 96)
        Spacer(Modifier.height(20.dp))
        WmButton(
            "신고하기",
            { selected?.let { onSubmit(it, detail) } },
            Modifier.fillMaxWidth(),
            style = WmButtonStyle.Danger,
            enabled = selected != null,
        )
        Spacer(Modifier.height(8.dp))
        WmButton("취소", onDismiss, Modifier.fillMaxWidth(), style = WmButtonStyle.Secondary)
    }
}

@Composable
fun ReportPackSheet(packTitle: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    ReportSheet(
        title = "'$packTitle' 단어팩을 신고할까요?",
        description = "신고 내용은 검토 후 처리돼요.\n허위 신고 시 이용이 제한될 수 있어요.",
        reasons = listOf("뜻이 틀렸거나 오타가 있어요", "부적절한 단어가 있어요", "광고·스팸이에요", "기타"),
        onDismiss = onDismiss,
        onSubmit = { _, _ ->
            Toast.makeText(context, "신고가 접수되었어요", Toast.LENGTH_SHORT).show()
            onDismiss()
        },
    )
}
