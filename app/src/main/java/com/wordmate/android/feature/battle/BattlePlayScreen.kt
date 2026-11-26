package com.wordmate.android.feature.battle

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmConfirmSheet
import com.wordmate.android.core.designsystem.component.WmProgressBar
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.wmViewModel
import java.text.NumberFormat
import kotlin.math.ceil

private val Reactions = listOf("👍", "🔥", "👏", "😂", "😮", "😭")

/** 실제 대결 페이지 (Figma "실제 대결 페이지", "틀림"). */
@Composable
fun BattlePlayScreen(onExit: () -> Unit, onFinished: () -> Unit) {
    val viewModel = wmViewModel { BattlePlayViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showQuit by remember { mutableStateOf(false) }

    LaunchedEffect(state.finished) { if (state.finished) onFinished() }
    BackHandler { showQuit = true }

    val q = state.question ?: return
    val urgent = state.timeFraction < 0.3f
    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WmTag("대결 중", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
            Spacer(Modifier.width(10.dp))
            WmProgressBar(
                state.timeFraction,
                Modifier.weight(1f),
                height = 8.dp,
                color = if (urgent) WmColors.Error else WmColors.Primary,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "${ceil(state.remainingMs / 1000.0).toInt()}",
                style = WmTypography.Title2,
                color = if (urgent) WmColors.Error else WmColors.TextPrimary,
            )
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("문제 ${state.index + 1}/${state.questions.size}", style = WmTypography.Label2, color = WmColors.TextSecondary)
            Spacer(Modifier.weight(1f))
            if (state.lastGain > 0) {
                Text("+${state.lastGain} ", style = WmTypography.Label2Bold, color = WmColors.PrimaryDark)
            }
            Text("${NumberFormat.getIntegerInstance().format(state.score)}점", style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(WmRadius.lg)
                .background(WmColors.Surface)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("알맞은 단어를 고르세요", style = WmTypography.Caption1, color = WmColors.TextTertiary)
            Spacer(Modifier.height(8.dp))
            Text(q.prompt, style = WmTypography.WordLarge, color = WmColors.TextPrimary)
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.weight(1f).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            q.choices.forEachIndexed { i, choice ->
                val isAnswer = i == q.answerIndex
                val isSelected = i == state.selectedIndex
                val bg = when {
                    state.revealed && (isAnswer || isSelected) -> WmColors.SurfacePressed
                    else -> WmColors.Surface
                }
                val fg = when {
                    !state.revealed -> WmColors.TextPrimary
                    isSelected && !isAnswer -> WmColors.Error
                    isAnswer -> WmColors.TextPrimary
                    else -> WmColors.TextDisabled
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(WmRadius.md)
                        .background(bg)
                        .clickable(enabled = !state.revealed, role = Role.Button) { viewModel.select(i) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(choice, style = WmTypography.Body1Bold, color = fg, modifier = Modifier.weight(1f))
                    if (state.revealed && isAnswer) Mark(true)
                    if (state.revealed && isSelected && !isAnswer) Mark(false)
                }
            }
            if (state.revealed) {
                Text(
                    when {
                        state.selectedIndex == q.answerIndex -> "정답! +${state.lastGain}점"
                        state.selectedIndex == null -> "시간 초과! 정답은 ${q.choices[q.answerIndex]}"
                        else -> "아쉬워요, 오답노트에 담았어요"
                    },
                    style = WmTypography.Label1Bold,
                    color = if (state.selectedIndex == q.answerIndex) WmColors.PrimaryDark else WmColors.Error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        AnimatedVisibility(
            state.reaction != null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enter = scaleIn() + fadeIn(),
            exit = fadeOut(),
        ) {
            Text(state.reaction ?: "", fontSize = 40.sp)
        }
        Text(
            "친구들에게 반응 보내기",
            style = WmTypography.Caption1,
            color = WmColors.TextTertiary,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Reactions.forEach { emoji ->
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(WmColors.Surface).clickable { viewModel.react(emoji) },
                    contentAlignment = Alignment.Center,
                ) { Text(emoji, fontSize = 20.sp) }
            }
        }
    }

    if (showQuit) {
        WmConfirmSheet(
            title = "대결을 나갈까요?",
            description = "지금 나가면 이번 대결 기록이 남지 않아요.",
            confirmText = "계속하기",
            dismissText = "나가기",
            onConfirm = { showQuit = false },
            onDismiss = {
                showQuit = false
                onExit()
            },
        )
    }
}

@Composable
private fun Mark(correct: Boolean) {
    Box(
        Modifier.size(22.dp).clip(CircleShape).background(if (correct) WmColors.Primary else WmColors.Error),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (correct) Icons.Rounded.Check else Icons.Rounded.Close,
            null,
            tint = if (correct) WmColors.OnPrimary else WmPalette.Gray01,
            modifier = Modifier.size(14.dp),
        )
    }
}
