package com.wordmate.android.feature.wordpack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmChipRow
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmEmptyState
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmSearchField
import com.wordmate.android.core.designsystem.component.WmSwitch
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.core.ui.rememberWordSpeaker
import com.wordmate.android.data.model.Word
import com.wordmate.android.data.model.WordStatus

/** 단어 목록 필터. null 은 "전체". */
private enum class WordFilter(val label: String, val status: WordStatus?) {
    All("전체", null),
    New("새 단어", WordStatus.New),
    Learning("학습 중", WordStatus.Learning),
    Memorized("외운 단어", WordStatus.Memorized),
}

internal fun WordStatus.dotColor(): Color = when (this) {
    WordStatus.New -> WmPalette.Gray11
    WordStatus.Learning -> WmPalette.Orange06
    WordStatus.Memorized -> WmPalette.Lime01
}

@Composable
fun PackDetailScreen(
    packId: Long,
    onBack: () -> Unit,
    onStudyClick: (Long) -> Unit,
) {
    val repository = rememberRepository()
    val packs by repository.packs.collectAsStateWithLifecycle()
    val pack = packs.firstOrNull { it.id == packId }
    val speaker = rememberWordSpeaker()

    var filter by rememberSaveable { mutableStateOf(WordFilter.All) }
    var query by rememberSaveable { mutableStateOf("") }
    var hideMeaning by rememberSaveable { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }

    if (pack == null) {
        Column(Modifier.fillMaxSize()) {
            WmTopBar("", onBack = onBack)
            WmEmptyState(Icons.Outlined.Flag, "삭제되었거나 찾을 수 없는 단어팩이에요")
        }
        return
    }

    val words = pack.words
        .filter { filter.status == null || it.status == filter.status }
        .filter { query.isBlank() || it.term.contains(query.trim(), true) || it.meaning.contains(query.trim()) }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar(pack.title, onBack = onBack) {
            WmIconButton(Icons.Outlined.Flag, "신고", { showReport = true })
        }
        Text(
            "${pack.sourceLabel} · ${pack.wordCount}단어 · ${pack.description}",
            style = WmTypography.Caption1,
            color = WmColors.TextTertiary,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))
        WmChipRow(
            items = WordFilter.entries,
            selected = filter,
            label = { it.label },
            onSelect = { filter = it },
            count = { f -> if (f.status == null) pack.wordCount else pack.words.count { it.status == f.status } },
            dot = { it.status?.dotColor() },
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            WmSearchField(query, { query = it }, "단어나 뜻으로 찾기", Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Text("뜻 가리기", style = WmTypography.Caption1, color = WmColors.TextSecondary)
            Spacer(Modifier.width(4.dp))
            WmSwitch(hideMeaning, { hideMeaning = it })
        }
        LazyColumn(Modifier.weight(1f).padding(top = 8.dp)) {
            if (words.isEmpty()) item { WmEmptyState(Icons.Outlined.Flag, "해당하는 단어가 없어요") }
            items(words, key = { it.id }) { word ->
                WordRow(
                    word = word,
                    hideMeaning = hideMeaning,
                    onSpeak = { speaker.speak(word.term) },
                    onStar = { repository.toggleStar(pack.id, word.id) },
                )
                WmDivider(Modifier.padding(horizontal = 20.dp))
            }
        }
        WmBottomCta("학습 시작", onClick = { onStudyClick(pack.id) }, enabled = pack.words.isNotEmpty())
    }

    if (showReport) {
        ReportPackSheet(packTitle = pack.title, onDismiss = { showReport = false })
    }
}

@Composable
internal fun WordRow(
    word: Word,
    hideMeaning: Boolean,
    onSpeak: () -> Unit,
    onStar: () -> Unit,
    trailing: @Composable () -> Unit = {},
) {
    var revealed by remember(hideMeaning) { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = hideMeaning) { revealed = !revealed }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(word.status.dotColor()))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(word.term, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
            Spacer(Modifier.height(2.dp))
            if (hideMeaning && !revealed) {
                Box(Modifier.width(96.dp).height(16.dp).clip(WmRadius.xs).background(WmColors.SurfaceVariant))
            } else {
                Text(word.meaning, style = WmTypography.Label2, color = WmColors.TextSecondary)
            }
        }
        RowAction(Icons.AutoMirrored.Rounded.VolumeUp, "발음 듣기", WmColors.TextSecondary, onSpeak)
        RowAction(
            if (word.starred) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            "즐겨찾기",
            if (word.starred) WmPalette.Yellow02 else WmColors.TextTertiary,
            onStar,
        )
        trailing()
    }
}

@Composable
internal fun RowAction(icon: ImageVector, description: String, tint: Color, onClick: () -> Unit) {
    Icon(
        icon,
        contentDescription = description,
        tint = tint,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(6.dp),
    )
}
