package com.wordmate.android.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmAvatar
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmLargeTopBar
import com.wordmate.android.core.designsystem.component.WmListRow
import com.wordmate.android.core.designsystem.component.WmProgressBar
import com.wordmate.android.core.designsystem.component.WmSectionHeader
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.Badge
import com.wordmate.android.data.model.Medal
import com.wordmate.android.data.model.User

/** 마이페이지 (Figma "마이페이지"). */
@Composable
fun MyPageScreen(
    onBadgesClick: () -> Unit,
    onStudyClick: () -> Unit,
    onBattleHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNoticeClick: () -> Unit,
    onInquiryClick: () -> Unit,
) {
    val repository = rememberRepository()
    val user by repository.user.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmLargeTopBar("마이페이지") { WmIconButton(Icons.Rounded.Notifications, "알림", {}, showBadge = true) }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            ProfileCard(user)
            WmSectionHeader("랭킹 배지", Modifier.padding(top = 8.dp), "전체 보기", onBadgesClick)
            WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), contentPadding = 4.dp) {
                repository.badges.take(3).forEach { BadgeRow(it) }
            }
            Spacer(Modifier.height(16.dp))
            WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), contentPadding = 0.dp) {
                WmListRow("학습", icon = Icons.Rounded.BarChart, trailingText = "학습 기록 · 오답노트", onClick = onStudyClick)
                WmListRow("활동", icon = Icons.Rounded.Star, trailingText = "대결 기록 · 배지", onClick = onBattleHistoryClick)
                WmListRow("설정", icon = Icons.Rounded.Settings, trailingText = "알림 · 계정", onClick = onSettingsClick)
                WmListRow("공지사항", icon = Icons.Rounded.Campaign, onClick = onNoticeClick)
                WmListRow("고객센터", icon = Icons.AutoMirrored.Rounded.HelpOutline, trailingText = "문의하기", onClick = onInquiryClick)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileCard(user: User) {
    WmCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                WmAvatar(size = 56.dp, background = WmColors.Background)
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(WmColors.Black)
                        .border(2.dp, WmColors.Surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Edit, "프로필 편집", tint = WmPalette.Gray01, modifier = Modifier.size(11.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(user.name, style = WmTypography.Title1, color = WmColors.TextPrimary)
                Text(user.schoolInfo, style = WmTypography.Caption1, color = WmColors.TextTertiary)
                Spacer(Modifier.height(4.dp))
                WmTag("🔥 ${user.title}", background = WmColors.Background, contentColor = WmColors.TextSecondary)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Lv.${user.level}", style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
            Spacer(Modifier.width(6.dp))
            WmTag(user.tier, background = WmPalette.Gray06)
            Spacer(Modifier.weight(1f))
            Text("XP ${user.xp}/${user.xpToNextLevel}", style = WmTypography.Caption1, color = WmColors.TextSecondary)
        }
        Spacer(Modifier.height(8.dp))
        WmProgressBar(user.xp.toFloat() / user.xpToNextLevel)
        Spacer(Modifier.height(6.dp))
        Row {
            Text("누적 XP 기준", style = WmTypography.Caption2, color = WmColors.TextTertiary, modifier = Modifier.weight(1f))
            Text(
                "다음 레벨까지 ${(user.xpToNextLevel - user.xp).coerceAtLeast(0)} XP",
                style = WmTypography.Caption2,
                color = WmColors.TextTertiary,
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat("연속 학습", "${user.streakDays}", "일", Modifier.weight(1f))
            MiniStat("맞힌 단어", "${user.totalCorrectWords}", "개", Modifier.weight(1f))
            MiniStat("대결 승리", "${user.battleWins}", "회", Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, unit: String, modifier: Modifier) {
    Column(modifier.clip(WmRadius.md).background(WmColors.Background).padding(12.dp)) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = WmTypography.Title2, color = WmColors.TextPrimary)
            Text(unit, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(start = 2.dp, bottom = 3.dp))
        }
    }
}

fun Medal.color(): Color = when (this) {
    Medal.Gold -> WmColors.Gold
    Medal.Silver -> WmColors.Silver
    Medal.Bronze -> WmColors.Bronze
}

@Composable
fun BadgeRow(badge: Badge) {
    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(badge.medal.color().copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.MilitaryTech, null, tint = badge.medal.color(), modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(badge.title, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
            Text(badge.dateLabel, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        if (badge.isNew) WmTag("NEW", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
    }
}
