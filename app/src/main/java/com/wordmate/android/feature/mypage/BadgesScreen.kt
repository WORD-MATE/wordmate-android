package com.wordmate.android.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.Medal

/** 랭킹 배지 전체 (Figma "배지"). */
@Composable
fun BadgesScreen(onBack: () -> Unit) {
    val badges = rememberRepository().badges
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("전체", "주간", "월간")
    val visible = when (tab) {
        1 -> badges.filter { "주차" in it.title }
        2 -> badges.filter { "월간" in it.title }
        else -> badges
    }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("랭킹 배지", onBack = onBack)
        LazyColumn(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            item {
                WmCard(Modifier.fillMaxWidth()) {
                    Text("보유 배지 ${badges.size}개", style = WmTypography.Title2, color = WmColors.TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Medal.entries.forEach { medal ->
                            val label = when (medal) { Medal.Gold -> "금"; Medal.Silver -> "은"; Medal.Bronze -> "동" }
                            WmTag(
                                "$label ${badges.count { it.medal == medal }}",
                                background = medal.color().copy(alpha = 0.2f),
                                contentColor = WmColors.TextPrimary,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "주간·월간 랭킹 상위권에 들면 배지를 받아요. 배지는 마이페이지와 랭킹에 표시돼요.",
                        style = WmTypography.Caption1,
                        color = WmColors.TextTertiary,
                    )
                }
                Spacer(Modifier.height(16.dp))
                WmSegmentedTabs(tabs, tab, { tab = it })
                Spacer(Modifier.height(8.dp))
            }
            items(visible) { badge ->
                BadgeRow(badge)
                WmDivider()
            }
        }
    }
}
