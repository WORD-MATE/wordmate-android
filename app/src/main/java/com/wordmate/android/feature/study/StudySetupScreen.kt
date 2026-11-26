package com.wordmate.android.feature.study

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmCheckCircle
import com.wordmate.android.core.designsystem.component.WmChip
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmSwitch
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.QuizDirection
import com.wordmate.android.data.model.StudyMode
import com.wordmate.android.data.model.StudyOptions

/** 학습 설정 (1단계: 학습 방식, 2단계: 출제 옵션). */
@Composable
fun StudySetupScreen(
    packId: Long,
    onBack: () -> Unit,
    onStart: (StudyOptions) -> Unit,
) {
    val packs by rememberRepository().packs.collectAsStateWithLifecycle()
    val pack = packs.firstOrNull { it.id == packId } ?: return

    var step by rememberSaveable { mutableIntStateOf(0) }
    var mode by rememberSaveable { mutableStateOf(StudyMode.MultipleChoice) }
    var direction by rememberSaveable { mutableStateOf(QuizDirection.EnToKo) }
    val countOptions = listOf(10, 20, pack.wordCount).filter { it <= pack.wordCount }.distinct()
    var count by rememberSaveable { mutableIntStateOf(countOptions.first()) }
    var shuffle by rememberSaveable { mutableStateOf(true) }

    BackHandler(enabled = step == 1) { step = 0 }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar("학습 설정", onBack = { if (step == 1) step = 0 else onBack() })
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Text("${pack.title} · ${pack.wordCount}단어", style = WmTypography.Caption1, color = WmColors.TextTertiary)
            Spacer(Modifier.height(20.dp))
            if (step == 0) {
                SectionLabel("학습 방식")
                StudyMode.entries.forEach { m ->
                    ModeCard(m, selected = m == mode) { mode = m }
                    Spacer(Modifier.height(8.dp))
                }
            } else {
                SectionLabel("출제 방향")
                WmSegmentedTabs(
                    tabs = QuizDirection.entries.map { it.label },
                    selectedIndex = direction.ordinal,
                    onSelect = { direction = QuizDirection.entries[it] },
                )
                Spacer(Modifier.height(24.dp))
                SectionLabel("문항 수")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    countOptions.forEach { c ->
                        WmChip(if (c == pack.wordCount) "전체 ${c}" else "${c}문항", c == count, { count = c })
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth().clip(WmRadius.md).background(WmColors.Surface).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("순서 섞기", style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                        Text("단어 순서를 무작위로 출제해요", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                    }
                    WmSwitch(shuffle, { shuffle = it })
                }
            }
        }
        WmBottomCta(
            text = if (step == 0) "다음" else "${count}문항 시작하기",
            onClick = {
                if (step == 0 && mode != StudyMode.Flashcard) step = 1
                else onStart(StudyOptions(mode, direction, if (step == 0) pack.wordCount else count, shuffle))
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = WmTypography.Label2Bold, color = WmColors.TextSecondary, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun ModeCard(mode: StudyMode, selected: Boolean, onClick: () -> Unit) {
    val (icon, tint, bg) = when (mode) {
        StudyMode.Flashcard -> Triple(Icons.Rounded.Style, WmColors.Info, Color(0xFFE6F0FF))
        StudyMode.MultipleChoice -> Triple(Icons.Rounded.GpsFixed, WmPalette.Red02, Color(0xFFFFE9EB))
        StudyMode.Typing -> Triple(Icons.Rounded.Edit, WmPalette.Orange06, Color(0xFFFFF1E0))
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(WmRadius.lg)
            .background(if (selected) WmColors.SurfaceVariant else WmColors.Surface)
            .border(1.5.dp, if (selected) WmColors.Primary else Color.Transparent, WmRadius.lg)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Box(Modifier.size(36.dp).clip(WmRadius.sm).background(bg), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(mode.title, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(mode.description, style = WmTypography.Caption1, color = WmColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            Text(mode.xpHint, style = WmTypography.Caption1Bold, color = WmColors.PrimaryDark)
        }
        Spacer(Modifier.width(12.dp))
        WmCheckCircle(selected)
    }
}
