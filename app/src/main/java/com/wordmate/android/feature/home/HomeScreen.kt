package com.wordmate.android.feature.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmAvatar
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmProgressBar
import com.wordmate.android.core.designsystem.component.WmSectionHeader
import com.wordmate.android.core.designsystem.component.WmSmallButton
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.wmViewModel
import com.wordmate.android.data.model.DailyMission
import com.wordmate.android.data.model.DailySentence
import com.wordmate.android.data.model.RankingEntry
import com.wordmate.android.data.model.User
import com.wordmate.android.data.model.WordPack
import java.text.NumberFormat

@Composable
fun HomeRoute(
    onStudyClick: (packId: Long) -> Unit,
    onBattleClick: () -> Unit,
    onPackClick: (packId: Long) -> Unit,
    onAllPacksClick: () -> Unit,
    onRankingClick: () -> Unit,
    onBattleCodeClick: () -> Unit,
) {
    val viewModel = wmViewModel { HomeViewModel(it) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showNotifications by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            onBellClick = { showNotifications = true },
            onStudyClick = { state.continuePack?.let { onStudyClick(it.id) } ?: onAllPacksClick() },
            onBattleClick = onBattleClick,
            onReviewClick = { state.continuePack?.let { onStudyClick(it.id) } },
            onWrongNoteClick = { state.continuePack?.let { onPackClick(it.id) } },
            onPackClick = onPackClick,
            onAllPacksClick = onAllPacksClick,
            onQuizClick = { state.continuePack?.let { onStudyClick(it.id) } },
            onClaimMission = viewModel::claimMission,
            onRankingClick = onRankingClick,
        )
        if (showNotifications) {
            NotificationPopup(
                notifications = state.notifications,
                onDismiss = { showNotifications = false },
                onMarkAllRead = viewModel::markAllRead,
                onItemClick = { notification ->
                    viewModel.markRead(notification.id)
                    showNotifications = false
                    if (notification.type == com.wordmate.android.data.model.NotificationType.BattleInvite) onBattleCodeClick()
                },
            )
        }
    }
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onBellClick: () -> Unit,
    onStudyClick: () -> Unit,
    onBattleClick: () -> Unit,
    onReviewClick: () -> Unit,
    onWrongNoteClick: () -> Unit,
    onPackClick: (Long) -> Unit,
    onAllPacksClick: () -> Unit,
    onQuizClick: () -> Unit,
    onClaimMission: (Long) -> Unit,
    onRankingClick: () -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize().background(WmColors.Background)) {
        item {
            Row(
                Modifier.fillMaxWidth().height(56.dp).padding(start = 20.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "wordmate",
                    style = WmTypography.Title1.copy(fontWeight = FontWeight.ExtraBold),
                    color = WmColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                WmIconButton(Icons.Rounded.Notifications, "알림", onBellClick, showBadge = state.unreadCount > 0)
            }
        }
        item { HeroCard(state.user.name, onStudyClick, onBattleClick) }
        item { StatRow(state.user) }
        item { WmSectionHeader("오늘 할 일", Modifier.padding(top = 12.dp)) }
        item {
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TodoCard("오늘의 복습", state.reviewCount, Icons.Rounded.Refresh, WmColors.Info, onReviewClick, Modifier.weight(1f))
                TodoCard("오답노트", state.wrongNoteCount, Icons.Rounded.Close, WmColors.Error, onWrongNoteClick, Modifier.weight(1f))
            }
        }
        state.continuePack?.let { pack ->
            item { WmSectionHeader("이어서 학습", Modifier.padding(top = 12.dp), "전체 단어팩", onAllPacksClick) }
            item { ContinueCard(pack) { onPackClick(pack.id) } }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item { SentenceCard(state.sentence, onQuizClick) }
        item { WmSectionHeader("일일 미션", Modifier.padding(top = 12.dp)) }
        item { MissionCard(state.missions, onClaimMission) }
        state.myRank?.let { me ->
            item { Spacer(Modifier.height(12.dp)) }
            item { RankingPreview(me, state.nearbyRanking, onRankingClick) }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun HeroCard(name: String, onStudyClick: () -> Unit, onBattleClick: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(WmRadius.lg)
            .background(WmColors.HeroGradient)
            .padding(16.dp),
    ) {
        Text(
            buildAnnotatedString {
                append("${name}님, 오늘도\n단어 ")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("10개") }
                append("만 해볼까요?")
            },
            style = WmTypography.Headline2,
            color = WmColors.TextPrimary,
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WmButton(
                "학습하기", onStudyClick, Modifier.weight(1f), style = WmButtonStyle.Dark, height = 44.dp,
                trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            )
            WmButton("대결하기", onBattleClick, Modifier.weight(1f), style = WmButtonStyle.Mint, height = 44.dp)
        }
    }
}

@Composable
private fun StatRow(user: User) {
    Row(
        Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatCard("연속 학습", Icons.Rounded.LocalFireDepartment, WmPalette.Orange06, Modifier.weight(1f)) {
            ValueWithUnit("${user.streakDays}", "일")
        }
        StatCard(user.tier, Icons.Rounded.MilitaryTech, WmPalette.Gray12, Modifier.weight(1f)) {
            Text("Lv.${user.level}", style = WmTypography.Title2, color = WmColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            WmProgressBar(user.xp.toFloat() / user.xpToNextLevel, height = 4.dp)
        }
        StatCard("오늘 XP", Icons.Rounded.Bolt, WmColors.PrimaryDark, Modifier.weight(1f)) {
            ValueWithUnit("${user.todayXp}", "/${user.todayXpGoal}")
            Spacer(Modifier.height(6.dp))
            WmProgressBar(user.todayXp.toFloat() / user.todayXpGoal, height = 4.dp)
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    WmCard(modifier, contentPadding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = WmTypography.Caption1, color = WmColors.TextSecondary)
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ValueWithUnit(value: String, unit: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(value, style = WmTypography.Title2, color = WmColors.TextPrimary)
        Spacer(Modifier.width(2.dp))
        Text(unit, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(bottom = 3.dp))
    }
}

@Composable
private fun TodoCard(
    title: String,
    count: Int,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    WmCard(modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(36.dp).clip(WmRadius.sm).background(WmPalette.Gray06),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = WmColors.TextTertiary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = WmTypography.Caption1, color = WmColors.TextSecondary)
        ValueWithUnit("$count", "단어")
    }
}

@Composable
fun ContinueCard(pack: WordPack, onClick: () -> Unit) {
    WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(pack.title, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text("${pack.sourceLabel} · ${pack.description}", style = WmTypography.Caption1, color = WmColors.TextTertiary)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = WmColors.TextTertiary)
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WmProgressBar(pack.progress, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Text("${pack.learnedCount}", style = WmTypography.Caption1Bold, color = WmColors.TextPrimary)
            Text(" / ${pack.wordCount}", style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
    }
}

@Composable
private fun SentenceCard(sentence: DailySentence, onQuizClick: () -> Unit) {
    WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Text(sentence.dateLabel, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Spacer(Modifier.height(8.dp))
        Text(
            buildAnnotatedString {
                val words = sentence.sentence.split(" ")
                words.forEachIndexed { i, token ->
                    val bare = token.trim(',', '.', '!', '?')
                    if (bare in sentence.underlined) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(bare) }
                        append(token.removePrefix(bare))
                    } else {
                        append(token)
                    }
                    if (i != words.lastIndex) append(" ")
                }
            },
            style = WmTypography.Title3.copy(fontWeight = FontWeight.Bold),
            color = WmColors.TextPrimary,
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            WmSmallButton("퀴즈 풀기", onQuizClick, trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight)
        }
    }
}

@Composable
private fun MissionCard(missions: List<DailyMission>, onClaim: (Long) -> Unit) {
    WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), contentPadding = 0.dp) {
        missions.forEachIndexed { index, mission ->
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (mission.isDone) WmColors.Primary else WmColors.Background)
                        .border(1.5.dp, if (mission.isDone) WmColors.Primary else WmColors.Border, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (mission.isDone) Icon(Icons.Rounded.Check, null, tint = WmColors.OnPrimary, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row {
                        Text(mission.title, style = WmTypography.Label1Bold, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
                        Text("${mission.current}/${mission.goal}", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                    }
                    Spacer(Modifier.height(6.dp))
                    WmProgressBar(mission.current.toFloat() / mission.goal, height = 4.dp)
                }
                Spacer(Modifier.width(12.dp))
                when {
                    mission.isDone && !mission.claimed ->
                        WmSmallButton("+${mission.rewardXp} XP 받기", { onClaim(mission.id) })
                    mission.claimed ->
                        Text("받음", style = WmTypography.Caption1Bold, color = WmColors.TextTertiary, modifier = Modifier.width(72.dp))
                    else ->
                        Text("+${mission.rewardXp} XP", style = WmTypography.Caption1Bold, color = WmColors.TextSecondary, modifier = Modifier.width(72.dp))
                }
            }
            if (index != missions.lastIndex) WmDivider(Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun RankingPreview(me: RankingEntry, nearby: List<RankingEntry>, onClick: () -> Unit) {
    WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${me.rank}위", style = WmTypography.Headline1, color = WmColors.TextPrimary)
            Spacer(Modifier.width(8.dp))
            Row(
                Modifier.clip(WmRadius.xs).background(Color(0xFFE6F0FF)).padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.ArrowUpward, null, tint = WmColors.Info, modifier = Modifier.size(12.dp))
                Text("${me.delta}", style = WmTypography.Caption1Bold, color = WmColors.Info)
            }
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.clip(WmRadius.sm).clickable(onClick = onClick).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("전체 랭킹", style = WmTypography.Label2, color = WmColors.TextSecondary)
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = WmColors.TextSecondary, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        nearby.forEach { entry -> RankRow(entry, me.xp) }
    }
}

@Composable
private fun RankRow(entry: RankingEntry, myXp: Int) {
    val fmt = NumberFormat.getIntegerInstance()
    Row(
        Modifier
            .fillMaxWidth()
            .clip(WmRadius.sm)
            .background(if (entry.isMe) WmPalette.Gray06 else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${entry.rank}", style = WmTypography.Label1Bold, color = WmColors.TextPrimary, modifier = Modifier.width(28.dp))
        WmAvatar(size = 28.dp)
        Spacer(Modifier.width(10.dp))
        Text(if (entry.isMe) "나" else entry.name, style = WmTypography.Label1, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
        Text("${fmt.format(entry.xp)} XP", style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
        val diff = entry.xp - myXp
        Text(
            if (entry.isMe) "" else if (diff > 0) "+$diff" else "$diff",
            style = WmTypography.Caption1,
            color = WmColors.TextTertiary,
            modifier = Modifier.width(40.dp).padding(start = 8.dp),
        )
    }
}
