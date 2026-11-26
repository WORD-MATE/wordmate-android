package com.wordmate.android.feature.battle

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmAvatar
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.BattleStanding
import com.wordmate.android.feature.wordpack.ReportSheet
import java.text.NumberFormat

private val numberFormat: NumberFormat = NumberFormat.getIntegerInstance()

/** 대결 결과 (Figma "결과", "신고"). */
@Composable
fun BattleResultScreen(onClose: () -> Unit, onWrongWordsClick: () -> Unit) {
    val result = rememberRepository().lastBattleResult
    if (result == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }
    val context = LocalContext.current
    var reporting by remember { mutableStateOf<BattleStanding?>(null) }
    BackHandler(onBack = onClose)

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            WmIconButton(Icons.Rounded.Close, "닫기", onClose)
            Text("대결 결과", style = WmTypography.Title3, color = WmColors.TextPrimary)
        }
        LazyColumn(Modifier.weight(1f)) {
            item {
                Text(
                    if (result.myRank == 1) "1위! 축하해요" else "${result.myRank}위! 수고했어요",
                    style = WmTypography.Headline1,
                    color = WmColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
                Podium(result.standings.take(3))
                Row(
                    Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(WmRadius.md).background(WmColors.Surface).padding(16.dp),
                ) {
                    ResultStat("내 점수", numberFormat.format(result.myScore), "", Modifier.weight(1f))
                    ResultStat("정답", "${result.correctCount}", "개", Modifier.weight(1f))
                    ResultStat("획득 XP", "+${result.earnedXp}", "", Modifier.weight(1f))
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "WORD MATE '${result.packTitle}' 대결에서 ${result.myRank}위! (${numberFormat.format(result.myScore)}점)",
                            )
                        }
                        context.startActivity(Intent.createChooser(send, "결과 공유"))
                    }) {
                        Icon(Icons.Rounded.IosShare, null, tint = WmColors.TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("결과 공유하기", style = WmTypography.Label2, color = WmColors.TextSecondary)
                    }
                }
                Text(
                    "전체 순위",
                    style = WmTypography.Body2Bold,
                    color = WmColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
            items(result.standings) { standing ->
                StandingRow(standing, onReport = { reporting = standing })
            }
            item {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(WmRadius.md)
                        .background(WmColors.Surface)
                        .clickable(enabled = result.wrongWords.isNotEmpty(), onClick = onWrongWordsClick)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(22.dp).clip(CircleShape).background(WmColors.Error), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Close, null, tint = WmPalette.Gray01, modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("틀린 단어", style = WmTypography.Label1Bold, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
                    Text("${result.wrongWords.size}개", style = WmTypography.Label2, color = WmColors.TextSecondary)
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = WmColors.TextTertiary)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    reporting?.let { target ->
        ReportSheet(
            title = "${target.name}님을 신고할까요?",
            description = "신고 내용은 검토 후 처리되며, 신고한 사실은 상대에게 알려지지 않아요.",
            reasons = listOf("부적절한 닉네임", "불쾌한 반응 반복", "부정행위가 의심돼요", "기타"),
            onDismiss = { reporting = null },
            onSubmit = { _, _ ->
                Toast.makeText(context, "신고가 접수되었어요", Toast.LENGTH_SHORT).show()
                reporting = null
            },
        )
    }
}

@Composable
private fun Podium(top: List<BattleStanding>) {
    // 2위 - 1위 - 3위 순서로 배치
    val order = listOfNotNull(top.getOrNull(1), top.getOrNull(0), top.getOrNull(2))
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        order.forEach { s ->
            val (height, color, textColor) = when (s.rank) {
                1 -> Triple(88.dp, WmColors.Primary, WmColors.OnPrimary)
                2 -> Triple(56.dp, WmColors.SurfaceVariant, WmColors.TextSecondary)
                else -> Triple(40.dp, WmColors.SurfaceVariant, WmColors.TextSecondary)
            }
            PodiumColumn(s, height, color, textColor, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumColumn(s: BattleStanding, height: Dp, color: Color, textColor: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        WmAvatar(size = if (s.rank == 1) 48.dp else 40.dp)
        Spacer(Modifier.height(6.dp))
        Text(if (s.isMe) "${s.name} (나)" else s.name, style = WmTypography.Label2Bold, color = WmColors.TextPrimary)
        Text(numberFormat.format(s.score), style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier.fillMaxWidth().height(height).clip(WmRadius.sm).background(color),
            contentAlignment = Alignment.Center,
        ) {
            Text("${s.rank}", style = WmTypography.Headline2, color = textColor)
        }
    }
}

@Composable
private fun ResultStat(label: String, value: String, unit: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = WmTypography.Title1, color = WmColors.TextPrimary)
            if (unit.isNotEmpty()) {
                Text(unit, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(start = 2.dp, bottom = 3.dp))
            }
        }
    }
}

@Composable
private fun StandingRow(s: BattleStanding, onReport: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 2.dp)
            .fillMaxWidth()
            .clip(WmRadius.sm)
            .background(if (s.isMe) WmPalette.Gray06 else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${s.rank}", style = WmTypography.Label1Bold, color = WmColors.TextPrimary, modifier = Modifier.width(24.dp))
        WmAvatar(size = 32.dp, background = if (s.isMe) WmColors.Background else WmPalette.Gray06)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(if (s.isMe) "${s.name} (나)" else s.name, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
            Text(s.detail, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        Text(numberFormat.format(s.score), style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
        if (!s.isMe) {
            Icon(
                Icons.Outlined.Flag,
                "신고",
                tint = WmColors.TextTertiary,
                modifier = Modifier.padding(start = 6.dp).size(28.dp).clip(CircleShape).clickable(onClick = onReport).padding(5.dp),
            )
        } else {
            Spacer(Modifier.width(34.dp))
        }
    }
}

/** 대결에서 틀린 단어 목록 (Figma "틀린 단어"). */
@Composable
fun BattleWrongWordsScreen(onBack: () -> Unit, onGoToPack: (Long) -> Unit) {
    val repository = rememberRepository()
    val result = repository.lastBattleResult ?: return
    val packId = repository.currentRoom?.settings?.pack?.id
    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar("틀린 단어", onBack = onBack)
        LazyColumn(Modifier.weight(1f)) {
            item {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text("틀린 단어 ${result.wrongWords.size}개", style = WmTypography.Headline2, color = WmColors.TextPrimary)
                    Text("틀린 단어는 오답노트에 자동으로 담겨요", style = WmTypography.Label2, color = WmColors.TextSecondary)
                }
                WmDivider(Modifier.padding(top = 8.dp))
            }
            items(result.wrongWords) { word ->
                Row(Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Box(Modifier.padding(top = 2.dp).size(20.dp).clip(CircleShape).background(WmColors.Error), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Close, null, tint = WmPalette.Gray01, modifier = Modifier.size(12.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(word.term, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
                        Text(word.meaning, style = WmTypography.Label2, color = WmColors.TextSecondary)
                        if (word.example.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(word.example, style = WmTypography.Caption1, color = WmColors.TextTertiary)
                        }
                    }
                }
                WmDivider(Modifier.padding(horizontal = 20.dp))
            }
        }
        WmBottomCta("오답노트로 가기", { packId?.let(onGoToPack) ?: onBack() })
    }
}
