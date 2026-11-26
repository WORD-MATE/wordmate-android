package com.wordmate.android.feature.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.wordmate.android.core.designsystem.component.WmBottomSheet
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCheckCircle
import com.wordmate.android.core.designsystem.component.WmChip
import com.wordmate.android.core.designsystem.component.WmChipRow
import com.wordmate.android.core.designsystem.component.WmSearchField
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.BattleMode
import com.wordmate.android.data.model.BattleSettings
import com.wordmate.android.data.model.WordPack
import kotlin.math.roundToInt

/** 대결방 만들기 1단계: 단어팩 선택. */
@Composable
fun BattleCreateScreen(onBack: () -> Unit, onNext: (Long) -> Unit) {
    val packs by rememberRepository().packs.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableLongStateOf(packs.firstOrNull()?.id ?: -1L) }
    var showPicker by remember { mutableStateOf(false) }
    val featured = (packs.take(4) + packs.filter { it.id == selectedId }).distinctBy { it.id }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar("대결방 만들기", onBack = onBack)
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("단어팩", style = WmTypography.Label2Bold, color = WmColors.TextSecondary, modifier = Modifier.weight(1f))
            Text("대결에서 출제할 단어팩을 골라주세요", style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        Spacer(Modifier.height(10.dp))
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            featured.forEach { pack -> PackRadioRow(pack, pack.id == selectedId) { selectedId = pack.id } }
            WmButton(
                "다른 단어팩 고르기 ${packs.size}",
                { showPicker = true },
                Modifier.fillMaxWidth(),
                style = WmButtonStyle.Secondary,
                height = 44.dp,
            )
        }
        WmBottomCta("다음", { onNext(selectedId) }, enabled = selectedId != -1L)
    }

    if (showPicker) {
        PackPickerSheet(
            packs = packs,
            selectedId = selectedId,
            onDismiss = { showPicker = false },
            onPick = {
                selectedId = it
                showPicker = false
            },
        )
    }
}

@Composable
private fun PackRadioRow(pack: WordPack, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(WmRadius.md)
            .background(if (selected) WmColors.SurfacePressed else WmColors.Surface)
            .border(1.5.dp, if (selected) WmColors.Primary else Color.Transparent, WmRadius.md)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(pack.title, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
            Text("${pack.sourceLabel} · ${pack.wordCount}단어", style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        WmCheckCircle(selected)
    }
}

/** 다른 단어팩 고르기 시트. */
@Composable
private fun PackPickerSheet(packs: List<WordPack>, selectedId: Long, onDismiss: () -> Unit, onPick: (Long) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableIntStateOf(0) }
    val filters = listOf("전체", "기본 단어팩", "내 단어팩")
    val visible = packs
        .filter { filter == 0 || it.isMine == (filter == 2) }
        .filter { query.isBlank() || it.title.contains(query.trim(), true) }
    WmBottomSheet(onDismiss) {
        Text("단어팩 고르기", style = WmTypography.Title2, color = WmColors.TextPrimary)
        Spacer(Modifier.height(12.dp))
        WmSearchField(query, { query = it }, "단어팩 이름으로 검색")
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            filters.forEachIndexed { i, f -> WmChip(f, i == filter, { filter = i }) }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(Modifier.heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { pack -> PackRadioRow(pack, pack.id == selectedId) { onPick(pack.id) } }
        }
    }
}

/** 대결방 만들기 2단계: 대결 설정. */
@Composable
fun BattleSettingsScreen(packId: Long, onBack: () -> Unit, onCreated: () -> Unit) {
    val repository = rememberRepository()
    val pack = repository.pack(packId) ?: return
    var mode by rememberSaveable { mutableStateOf(BattleMode.Speed) }
    var count by rememberSaveable { mutableIntStateOf(10) }
    var seconds by rememberSaveable { mutableIntStateOf(10) }
    var players by rememberSaveable { mutableFloatStateOf(8f) }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar("대결방 만들기", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Text("${pack.title} · ${pack.wordCount}단어", style = WmTypography.Caption1, color = WmColors.TextTertiary)
            Spacer(Modifier.height(20.dp))
            Label("점수 방식")
            WmSegmentedTabs(BattleMode.entries.map { it.label }, mode.ordinal, { mode = BattleMode.entries[it] })
            Spacer(Modifier.height(24.dp))
            Label("문항 수")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(10, 20, 30).forEach { c -> WmChip("${c}문항", c == count, { count = c }) }
            }
            Spacer(Modifier.height(24.dp))
            Label("문항당 시간")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(5, 10, 15).forEach { s -> WmChip("${s}초", s == seconds, { seconds = s }) }
            }
            Spacer(Modifier.height(24.dp))
            Column(Modifier.fillMaxWidth().clip(WmRadius.md).background(WmColors.Surface).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("최대 인원", style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                        Text("2명부터 30명까지 참여할 수 있어요", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                    }
                    Text("${players.roundToInt()}명", style = WmTypography.Title2, color = WmColors.TextPrimary)
                }
                Slider(
                    value = players,
                    onValueChange = { players = it },
                    valueRange = 2f..30f,
                    steps = 27,
                    colors = SliderDefaults.colors(
                        thumbColor = WmPalette.Gray01,
                        activeTrackColor = WmColors.Primary,
                        inactiveTrackColor = WmPalette.Gray06,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                    ),
                )
            }
        }
        WmBottomCta("방 만들기", {
            repository.createRoom(BattleSettings(pack, mode, count, seconds, players.roundToInt()))
            onCreated()
        })
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = WmTypography.Label2Bold, color = WmColors.TextSecondary, modifier = Modifier.padding(bottom = 10.dp))
}
