package com.wordmate.android.feature.study

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmConfirmSheet
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmProgressBar
import com.wordmate.android.core.designsystem.component.WmTextField
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberWordSpeaker
import com.wordmate.android.core.ui.wmViewModel
import com.wordmate.android.data.model.QuizDirection
import com.wordmate.android.data.model.StudyMode
import com.wordmate.android.data.model.StudyOptions

@Composable
fun StudySessionScreen(
    packId: Long,
    options: StudyOptions,
    onlyWrong: Boolean,
    onExit: () -> Unit,
    onFinished: () -> Unit,
) {
    val viewModel = wmViewModel(key = "study-$packId-$options-$onlyWrong") {
        StudyViewModel(it, packId, options, onlyWrong)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val speaker = rememberWordSpeaker()
    var showQuit by remember { mutableStateOf(false) }

    LaunchedEffect(state.finished) { if (state.finished) onFinished() }
    BackHandler { showQuit = true }

    val question = state.current
    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding().imePadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            WmIconButton(Icons.Rounded.Close, "학습 종료", { showQuit = true })
            WmProgressBar(state.progress, Modifier.weight(1f).padding(horizontal = 8.dp))
            Text(
                "${(state.index + 1).coerceAtMost(state.questions.size)}/${state.questions.size}",
                style = WmTypography.Caption1,
                color = WmColors.TextTertiary,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        Text(
            "${state.packTitle} · ${state.mode.title}",
            style = WmTypography.Caption1,
            color = WmColors.TextTertiary,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))

        if (question == null) return@Column
        when (state.mode) {
            StudyMode.Flashcard -> FlashcardContent(state, question, speaker::speak, viewModel::flip, viewModel::markKnown)
            StudyMode.MultipleChoice -> MultipleChoiceContent(state, question, speaker::speak, viewModel::select, viewModel::next)
            StudyMode.Typing -> TypingContent(
                state, question, speaker::speak, viewModel::onInputChange, viewModel::submitTyped, viewModel::next,
            )
        }
    }

    if (showQuit) {
        WmConfirmSheet(
            title = "학습을 멈출까요?",
            description = "지금까지 푼 문제는 저장되지 않아요.\n다음에 처음부터 다시 시작해요.",
            confirmText = "이어하기",
            dismissText = "그만하기",
            onConfirm = { showQuit = false },
            onDismiss = {
                showQuit = false
                onExit()
            },
        )
    }
}

@Composable
private fun PromptCard(
    label: String,
    prompt: String,
    sub: String?,
    showSpeaker: Boolean,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(WmRadius.lg)
            .background(WmColors.Surface)
            .padding(vertical = 28.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(prompt, style = WmTypography.WordLarge, color = WmColors.TextPrimary, textAlign = TextAlign.Center)
            if (showSpeaker) {
                Icon(
                    Icons.AutoMirrored.Rounded.VolumeUp,
                    "발음 듣기",
                    tint = WmColors.TextSecondary,
                    modifier = Modifier.padding(start = 6.dp).size(28.dp).clip(CircleShape).clickable(onClick = onSpeak).padding(4.dp),
                )
            }
        }
        if (!sub.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(sub, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
    }
}

@Composable
private fun FlashcardContent(
    state: StudyUiState,
    question: StudyQuestion,
    onSpeak: (String) -> Unit,
    onFlip: () -> Unit,
    onKnown: (Boolean) -> Unit,
) {
    val rotation by animateFloatAsState(if (state.flipped) 180f else 0f, tween(350), label = "flip")
    val showBack = rotation > 90f
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                }
                .clip(WmRadius.xl)
                .background(WmColors.Surface)
                .border(1.dp, WmColors.Divider, WmRadius.xl)
                .clickable(onClick = onFlip),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.graphicsLayer { rotationY = if (showBack) 180f else 0f }.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(if (showBack) "뜻" else "단어", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (showBack) question.answer else question.prompt,
                    style = WmTypography.WordLarge,
                    color = WmColors.TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (showBack) question.word.example else "탭해서 뜻 확인",
                    style = WmTypography.Caption1,
                    color = WmColors.TextTertiary,
                    textAlign = TextAlign.Center,
                )
            }
            if (!showBack) {
                WmIconButton(
                    Icons.AutoMirrored.Rounded.VolumeUp,
                    "발음 듣기",
                    { onSpeak(question.word.term) },
                    Modifier.align(Alignment.TopEnd).padding(8.dp),
                    tint = WmColors.TextSecondary,
                )
            }
        }
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WmButton("몰라요", { onKnown(false) }, Modifier.weight(1f), style = WmButtonStyle.Secondary)
            WmButton("알아요", { onKnown(true) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MultipleChoiceContent(
    state: StudyUiState,
    question: StudyQuestion,
    onSpeak: (String) -> Unit,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
) {
    val answered = state.answerState as? AnswerState.Answered
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            PromptCard(
                label = if (state.direction == QuizDirection.EnToKo) "알맞은 뜻을 고르세요" else "알맞은 단어를 고르세요",
                prompt = question.prompt,
                sub = if (state.direction == QuizDirection.EnToKo) question.word.pronunciation else null,
                showSpeaker = state.direction == QuizDirection.EnToKo,
                onSpeak = { onSpeak(question.word.term) },
            )
            Spacer(Modifier.height(16.dp))
            question.choices.forEachIndexed { i, choice ->
                val isAnswer = choice == question.answer
                val isSelected = answered?.selectedIndex == i
                val choiceState = when {
                    answered == null -> ChoiceState.Idle
                    isAnswer -> ChoiceState.Correct
                    isSelected -> ChoiceState.Wrong
                    else -> ChoiceState.Dimmed
                }
                ChoiceRow(i + 1, choice, choiceState, enabled = answered == null) { onSelect(i) }
            }
        }
        if (answered != null) {
            Feedback(answered.correct, question)
            WmBottomCta("다음", onNext)
        }
    }
}

private enum class ChoiceState { Idle, Correct, Wrong, Dimmed }

@Composable
private fun ChoiceRow(number: Int, text: String, state: ChoiceState, enabled: Boolean, onClick: () -> Unit) {
    val bg = when (state) {
        ChoiceState.Correct, ChoiceState.Wrong -> WmColors.SurfacePressed
        else -> WmColors.Surface
    }
    val fg = when (state) {
        ChoiceState.Dimmed -> WmColors.TextDisabled
        ChoiceState.Wrong -> WmColors.Error
        else -> WmColors.TextPrimary
    }
    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(WmRadius.md)
            .background(bg)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$number", style = WmTypography.Label1Bold, color = if (state == ChoiceState.Dimmed) WmColors.TextDisabled else WmColors.TextTertiary)
        Spacer(Modifier.width(14.dp))
        Text(text, style = WmTypography.Body2Bold, color = fg, modifier = Modifier.weight(1f))
        when (state) {
            ChoiceState.Correct -> ResultDot(WmColors.Primary, Icons.Rounded.Check, WmColors.OnPrimary)
            ChoiceState.Wrong -> ResultDot(WmColors.Error, Icons.Rounded.Close, WmPalette.Gray01)
            else -> Unit
        }
    }
}

@Composable
private fun ResultDot(bg: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Box(Modifier.size(22.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun Feedback(correct: Boolean, question: StudyQuestion) {
    Column(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (correct) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline,
                null,
                tint = if (correct) WmColors.PrimaryDark else WmColors.Error,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (correct) "정답이에요" else "아쉬워요, 오답노트에 담았어요",
                style = WmTypography.Label1Bold,
                color = if (correct) WmColors.TextPrimary else WmColors.Error,
            )
        }
        if (!correct) {
            Text("정답: ${question.answer}", style = WmTypography.Label2, color = WmColors.TextSecondary, modifier = Modifier.padding(start = 24.dp, top = 2.dp))
        }
        if (question.word.example.isNotBlank()) {
            Text(question.word.example, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(start = 24.dp, top = 2.dp))
        }
    }
}

@Composable
private fun TypingContent(
    state: StudyUiState,
    question: StudyQuestion,
    onSpeak: (String) -> Unit,
    onInput: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
) {
    val answered = state.answerState as? AnswerState.Answered
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            PromptCard(
                label = if (state.direction == QuizDirection.EnToKo) "뜻을 입력하세요" else "영어 단어를 입력하세요",
                prompt = question.prompt,
                sub = if (state.direction == QuizDirection.EnToKo) question.word.pronunciation else null,
                showSpeaker = state.direction == QuizDirection.EnToKo,
                onSpeak = { onSpeak(question.word.term) },
            )
            Spacer(Modifier.height(16.dp))
            WmTextField(
                value = state.input,
                onValueChange = { if (answered == null) onInput(it) },
                placeholder = if (state.direction == QuizDirection.EnToKo) "뜻을 입력하세요" else "단어를 입력하세요",
                isError = answered?.correct == false,
                modifier = Modifier.padding(horizontal = 20.dp),
                onImeAction = onSubmit,
            )
            if (answered != null && !answered.correct) {
                Text(
                    "정답 ${question.answer}",
                    style = WmTypography.Label1Bold,
                    color = WmColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                )
            }
        }
        if (answered != null) {
            Feedback(answered.correct, question)
            WmBottomCta("다음", onNext)
        } else {
            WmBottomCta("확인", onSubmit, enabled = state.input.isNotBlank(), style = WmButtonStyle.Secondary)
        }
    }
}
