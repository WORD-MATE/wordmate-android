package com.wordmate.android.feature.battle

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmBottomSheet
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmLargeTopBar
import com.wordmate.android.core.designsystem.component.WmSectionHeader
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.BattleRecord
import com.wordmate.android.data.model.BattleSettings
import kotlinx.coroutines.delay
import java.text.NumberFormat

@Composable
fun BattleHomeScreen(
    onCreateClick: () -> Unit,
    onCodeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onMatched: () -> Unit,
) {
    val repository = rememberRepository()
    val records by repository.battleRecords.collectAsStateWithLifecycle()
    var matching by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmLargeTopBar("대결") { WmIconButton(Icons.Rounded.Notifications, "알림", {}, showBadge = true) }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EntryCard(
                    title = "빠른 대결",
                    description = "비슷한 실력의 친구와 바로 대결해요",
                    icon = Icons.Rounded.Bolt,
                    container = WmColors.Primary,
                    iconContainer = WmColors.Black,
                    iconTint = WmColors.Primary,
                    onClick = { matching = true },
                )
                EntryCard(
                    title = "대결방 만들기",
                    description = "단어팩을 골라 친구들을 초대해요",
                    icon = Icons.Rounded.Add,
                    onClick = onCreateClick,
                )
                EntryCard(
                    title = "코드로 참가",
                    description = "친구에게 받은 6자리 코드를 입력해요",
                    icon = Icons.Rounded.Groups,
                    onClick = onCodeClick,
                )
            }
            WmSectionHeader("내 대결 기록", Modifier.padding(top = 12.dp), "전체 보기", onHistoryClick)
            BattleStatsCard(records, Modifier.padding(horizontal = 20.dp))
            WmSectionHeader("최근 대결", Modifier.padding(top = 12.dp))
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                records.take(2).forEach { BattleRecordRow(it) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (matching) {
        QuickMatchSheet(
            onCancel = { matching = false },
            onMatched = {
                matching = false
                repository.createRoom(BattleSettings(repository.packs.value.first()))
                onMatched()
            },
        )
    }
}

@Composable
private fun EntryCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    container: Color = WmColors.Surface,
    iconContainer: Color = WmColors.Background,
    iconTint: Color = WmColors.TextPrimary,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(WmRadius.lg)
            .background(container)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).clip(WmRadius.md).background(iconContainer), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
            Text(description, style = WmTypography.Caption1, color = WmColors.TextSecondary)
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = WmColors.TextSecondary)
    }
}

@Composable
fun BattleStatsCard(records: List<BattleRecord>, modifier: Modifier = Modifier) {
    val wins = records.count { it.rank == 1 }
    val winRate = if (records.isEmpty()) 0 else wins * 100 / records.size
    WmCard(modifier.fillMaxWidth()) {
        Row {
            StatColumn("대결", "${records.size}", "회", Modifier.weight(1f))
            StatColumn("승률", "$winRate", "%", Modifier.weight(1f))
            StatColumn("평균 정답률", "78", "%", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, unit: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = WmTypography.Title1, color = WmColors.TextPrimary)
            Text(unit, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(start = 2.dp, bottom = 3.dp))
        }
    }
}

@Composable
fun BattleRecordRow(record: BattleRecord) {
    val (bg, fg) = when (record.rank) {
        1 -> WmColors.Primary to WmColors.OnPrimary
        2 -> WmPalette.Gray06 to WmColors.TextPrimary
        3 -> Color(0xFFFFE6CC) to WmPalette.Orange07
        else -> WmColors.SurfaceVariant to WmColors.TextSecondary
    }
    Row(
        Modifier.fillMaxWidth().clip(WmRadius.md).background(WmColors.Surface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(36.dp).clip(WmRadius.sm).background(bg), contentAlignment = Alignment.Center) {
            Text("${record.rank}위", style = WmTypography.Caption1Bold, color = fg)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(record.packTitle, style = WmTypography.Label1Bold, color = WmColors.TextPrimary, maxLines = 1)
            Text(record.dateLabel, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        Text("${NumberFormat.getIntegerInstance().format(record.score)}점", style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
    }
}

/** 빠른 대결 매칭 시트 (Figma "빠른대결"). */
@Composable
private fun QuickMatchSheet(onCancel: () -> Unit, onMatched: () -> Unit) {
    var seconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (seconds < 3) {
            delay(1_000)
            seconds++
        }
        onMatched()
    }
    WmBottomSheet(onCancel) {
        Text(
            "상대를 찾고 있어요",
            style = WmTypography.Title2,
            color = WmColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "$seconds",
            style = WmTypography.Score,
            color = WmColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            WmTag("비슷한 레벨의 친구를 찾는 중 · 평균 10초")
        }
        Spacer(Modifier.height(24.dp))
        WmButton("매칭 취소", onCancel, Modifier.fillMaxWidth(), style = WmButtonStyle.Secondary)
    }
}
