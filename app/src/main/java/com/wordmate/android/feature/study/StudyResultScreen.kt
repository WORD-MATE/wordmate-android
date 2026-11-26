package com.wordmate.android.feature.study

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmSwitch
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository

fun resultHeadline(accuracy: Int): String = when {
    accuracy == 100 -> "완벽해요!"
    accuracy >= 80 -> "잘했어요!"
    accuracy >= 50 -> "조금만 더 하면 돼요"
    else -> "다시 한 번 도전해봐요"
}

@Composable
fun StudyResultScreen(
    onClose: () -> Unit,
    onRetryWrong: () -> Unit,
    onRetryAll: () -> Unit,
) {
    val result = rememberRepository().lastStudyResult
    if (result == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }
    var onlyWrong by rememberSaveable { mutableStateOf(false) }
    val list = if (onlyWrong) result.answers.filterNot { it.correct } else result.answers

    BackHandler(onBack = onClose)

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
            WmIconButton(Icons.Rounded.Close, "닫기", onClose)
        }
        LazyColumn(Modifier.weight(1f)) {
            item {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Text("${result.packTitle} · ${result.mode.title}", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                    Spacer(Modifier.height(6.dp))
                    Text(resultHeadline(result.accuracy), style = WmTypography.Headline1, color = WmColors.TextPrimary)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${result.accuracy}", style = WmTypography.Display, color = WmColors.TextPrimary)
                        Text("%", style = WmTypography.Title1, color = WmColors.TextPrimary, modifier = Modifier.padding(bottom = 8.dp, start = 2.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth().clip(WmRadius.md).background(WmColors.Surface).padding(16.dp),
                    ) {
                        Stat("맞음", "${result.correctCount}", WmColors.TextPrimary, Modifier.weight(1f))
                        Stat("틀림", "${result.wrongCount}", WmColors.Error, Modifier.weight(1f))
                        Stat("획득 XP", "+${result.earnedXp}", WmColors.TextPrimary, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    if (result.wrongCount > 0) {
                        WmButton("틀린 단어만 다시 풀기", onRetryWrong, Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                    }
                    WmButton("다시 풀기", onRetryAll, Modifier.fillMaxWidth(), style = WmButtonStyle.Secondary)
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = onClose) {
                            Text("홈으로", style = WmTypography.Label1, color = WmColors.TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("푼 단어 ${result.answers.size}", style = WmTypography.Body2Bold, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
                        Text("틀린 것만", style = WmTypography.Caption1, color = WmColors.TextSecondary)
                        Spacer(Modifier.width(4.dp))
                        WmSwitch(onlyWrong, { onlyWrong = it })
                    }
                }
            }
            items(list) { answer ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(22.dp).clip(CircleShape).background(if (answer.correct) WmColors.Primary else WmColors.Error),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (answer.correct) Icons.Rounded.Check else Icons.Rounded.Close,
                            null,
                            tint = if (answer.correct) WmColors.OnPrimary else WmPalette.Gray01,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(answer.word.term, style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                        Text(answer.word.meaning, style = WmTypography.Caption1, color = WmColors.TextSecondary)
                    }
                }
                WmDivider(Modifier.padding(horizontal = 20.dp))
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Text(value, style = WmTypography.Title1, color = color)
    }
}
