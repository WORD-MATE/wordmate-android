package com.wordmate.android.feature.ranking

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Remove
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmAvatar
import com.wordmate.android.core.designsystem.component.WmChip
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmLargeTopBar
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.RankingEntry
import com.wordmate.android.data.model.RankingPeriod
import com.wordmate.android.data.model.RankingScope
import java.text.NumberFormat

private val fmt: NumberFormat = NumberFormat.getIntegerInstance()

/** 랭킹 페이지 (Figma "랭킹 페이지"). */
@Composable
fun RankingScreen() {
    val repository = rememberRepository()
    val user by repository.user.collectAsStateWithLifecycle()
    var scope by rememberSaveable { mutableStateOf(RankingScope.School) }
    var period by rememberSaveable { mutableStateOf(RankingPeriod.Weekly) }
    val entries = remember(scope, period, user.xp) { repository.ranking(scope, period) }
    val me = entries.firstOrNull { it.isMe }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmLargeTopBar("랭킹") { WmIconButton(Icons.Rounded.Notifications, "알림", {}, showBadge = true) }
        WmSegmentedTabs(
            tabs = RankingScope.entries.map { it.label },
            selectedIndex = scope.ordinal,
            onSelect = { scope = RankingScope.entries[it] },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RankingPeriod.entries.forEach { p -> WmChip(p.label, p == period, { period = p }) }
        }
        LazyColumn(Modifier.weight(1f)) {
            item { PodiumCard(entries.take(3)) }
            items(entries.drop(3), key = { it.rank }) { RankingRow(it) }
            item { Spacer(Modifier.height(12.dp)) }
        }
        if (me != null) {
            Box(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                RankingRow(me, pinned = true)
            }
        }
    }
}

@Composable
private fun PodiumCard(top: List<RankingEntry>) {
    val order = listOfNotNull(top.getOrNull(1), top.getOrNull(0), top.getOrNull(2))
    Row(
        Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .clip(WmRadius.lg)
            .background(WmColors.Surface)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        order.forEach { e ->
            val (barHeight, barColor, numberColor) = when (e.rank) {
                1 -> Triple(88.dp, WmColors.Primary, WmColors.OnPrimary)
                2 -> Triple(52.dp, WmPalette.Gray06, WmColors.TextSecondary)
                else -> Triple(36.dp, WmPalette.Gray06, WmColors.TextSecondary)
            }
            PodiumColumn(e, barHeight, barColor, numberColor, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumColumn(e: RankingEntry, barHeight: Dp, barColor: Color, numberColor: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        WmAvatar(size = if (e.rank == 1) 48.dp else 40.dp, background = WmColors.Background)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(e.name, style = WmTypography.Label2Bold, color = WmColors.TextPrimary)
            if (e.rank == 1) {
                Icon(Icons.Rounded.LocalFireDepartment, null, tint = WmPalette.Orange06, modifier = Modifier.size(14.dp))
            }
        }
        Text(e.schoolInfo, style = WmTypography.Caption2, color = WmColors.TextTertiary)
        Text("${fmt.format(e.xp)} XP", style = WmTypography.Caption1Bold, color = WmColors.TextSecondary)
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier.fillMaxWidth().height(barHeight).clip(WmRadius.sm).background(barColor),
            contentAlignment = Alignment.Center,
        ) {
            Text("${e.rank}", style = WmTypography.Headline2, color = numberColor)
        }
    }
}

@Composable
private fun RankingRow(e: RankingEntry, pinned: Boolean = false) {
    Row(
        Modifier
            .padding(horizontal = if (pinned) 0.dp else 20.dp)
            .fillMaxWidth()
            .clip(WmRadius.md)
            .background(if (pinned) WmPalette.Gray06 else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${e.rank}", style = WmTypography.Label1Bold, color = WmColors.TextPrimary, modifier = Modifier.width(32.dp))
        WmAvatar(size = 36.dp, background = if (pinned) WmColors.Background else WmPalette.Gray06)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(e.name, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
                if (e.isMe) {
                    Spacer(Modifier.width(4.dp))
                    WmTag("나", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
                }
            }
            Text(e.schoolInfo, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        Delta(e.delta)
        Spacer(Modifier.width(12.dp))
        Text(fmt.format(e.xp), style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
        Text(" XP", style = WmTypography.Caption1, color = WmColors.TextTertiary)
    }
}

@Composable
private fun Delta(delta: Int) {
    val (icon, color) = when {
        delta > 0 -> Icons.Rounded.ArrowDropUp to WmColors.Info
        delta < 0 -> Icons.Rounded.ArrowDropDown to WmColors.Error
        else -> Icons.Rounded.Remove to WmColors.TextDisabled
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        if (delta != 0) Text("${kotlin.math.abs(delta)}", style = WmTypography.Caption1Bold, color = color)
    }
}
