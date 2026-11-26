package com.wordmate.android.feature.wordpack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmTextField
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository

/** 새 단어팩 만들기 (Figma "추가 페이지"). */
@Composable
fun PackCreateScreen(onBack: () -> Unit, onCreated: (Long) -> Unit) {
    val repository = rememberRepository()
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var term by rememberSaveable { mutableStateOf("") }
    var meaning by rememberSaveable { mutableStateOf("") }
    val words = remember { mutableStateListOf<Pair<String, String>>() }

    fun addCurrent() {
        if (term.isBlank() || meaning.isBlank()) return
        words += term.trim() to meaning.trim()
        term = ""
        meaning = ""
    }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding().imePadding()) {
        WmTopBar("새 단어팩", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Label("단어팩 이름")
            WmTextField(title, { title = it }, "예) 중간고사 대비")
            Spacer(Modifier.height(16.dp))
            Label("설명 (선택)")
            WmTextField(description, { description = it }, "어떤 단어를 모았는지 적어주세요")
            Spacer(Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("단어 추가", style = WmTypography.Title3, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
                Text("${words.size}개", style = WmTypography.Label2, color = WmColors.TextTertiary)
            }
            Spacer(Modifier.height(12.dp))
            WmCard(Modifier.fillMaxWidth()) {
                WmTextField(term, { term = it }, "영어 단어")
                Spacer(Modifier.height(8.dp))
                WmTextField(meaning, { meaning = it }, "뜻", onImeAction = ::addCurrent)
                Spacer(Modifier.height(12.dp))
                WmButton(
                    "목록에 추가", ::addCurrent, Modifier.fillMaxWidth(),
                    style = WmButtonStyle.Dark, height = 44.dp, leadingIcon = Icons.Rounded.Add,
                    enabled = term.isNotBlank() && meaning.isNotBlank(),
                )
            }
            Spacer(Modifier.height(12.dp))
            words.forEachIndexed { index, (t, m) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}", style = WmTypography.Label2, color = WmColors.TextTertiary, modifier = Modifier.width(24.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t, style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                        Text(m, style = WmTypography.Label2, color = WmColors.TextSecondary)
                    }
                    RowAction(Icons.Rounded.Close, "삭제", WmColors.TextTertiary) { words.removeAt(index) }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        WmBottomCta(
            "만들기",
            enabled = title.isNotBlank(),
            onClick = {
                val pack = repository.createPack(title.trim(), description.trim())
                words.forEach { (t, m) -> repository.addWord(pack.id, t, m) }
                onCreated(pack.id)
            },
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = WmTypography.Caption1Bold, color = WmColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
}
