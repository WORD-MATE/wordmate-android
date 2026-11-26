package com.wordmate.android.feature.mypage

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmChip
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.component.WmTextField
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

private data class Notice(val id: Int, val title: String, val date: String, val body: String, val pinned: Boolean = false)

private val notices = listOf(
    Notice(1, "WORD MATE 정식 오픈 안내", "11월 1일", "WORD MATE가 정식으로 문을 열었어요! 매일 10분, 친구들과 단어로 겨루며 함께 성장해요.", pinned = true),
    Notice(2, "2학기 기말고사 대비 단어팩 업데이트", "11월 6일", "교과서 2학년 4~6단원 단어팩이 추가되었어요. 단어팩 탭에서 확인해 보세요."),
    Notice(3, "주간 랭킹 배지 지급 기준 변경", "10월 28일", "주간 랭킹 배지는 이제 학교 전체 상위 10%와 반별 1등에게 지급돼요."),
    Notice(4, "서버 점검 안내 (10월 20일 02:00~04:00)", "10월 18일", "안정적인 서비스를 위해 서버 점검이 진행돼요. 점검 중에는 대결 기능을 사용할 수 없어요."),
)

/** 공지사항 (Figma "공지", "단건 조회"). */
@Composable
fun NoticeScreen(onBack: () -> Unit) {
    var expanded by rememberSaveable { mutableIntStateOf(-1) }
    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("공지사항", onBack = onBack)
        LazyColumn(Modifier.weight(1f)) {
            items(notices, key = { it.id }) { notice ->
                val open = expanded == notice.id
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = if (open) -1 else notice.id }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (notice.pinned) {
                                    WmTag("필독", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
                                    Spacer(Modifier.width(6.dp))
                                }
                                Text(notice.title, style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(notice.date, style = WmTypography.Caption1, color = WmColors.TextTertiary)
                        }
                        Icon(
                            if (open) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            null,
                            tint = WmColors.TextTertiary,
                        )
                    }
                    AnimatedVisibility(open) {
                        Text(
                            notice.body,
                            style = WmTypography.Label1,
                            color = WmColors.TextSecondary,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .clip(WmRadius.md)
                                .background(WmColors.Surface)
                                .padding(16.dp),
                        )
                    }
                }
                WmDivider(Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}

private data class Inquiry(val type: String, val title: String, val date: String, val answer: String?)

private val inquiryTypes = listOf("학습", "대결", "계정", "오류 신고", "기타")

/** 문의하기 (Figma "문의 유형", "작성", "보내기 버튼 활성화", "답변"). */
@Composable
fun InquiryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var type by rememberSaveable { mutableStateOf<String?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    val history = remember {
        mutableStateListOf(
            Inquiry("대결", "대결 중 화면이 멈췄어요", "11월 4일", "불편을 드려 죄송해요. 11월 6일 업데이트에서 해당 문제를 수정했어요. 앱을 최신 버전으로 업데이트해 주세요."),
            Inquiry("학습", "내 단어팩을 친구와 공유하고 싶어요", "11월 7일", null),
        )
    }
    val canSend = type != null && title.isNotBlank() && body.length >= 10

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding().imePadding()) {
        WmTopBar("고객센터", onBack = onBack)
        WmSegmentedTabs(listOf("문의하기", "내 문의 ${history.size}"), tab, { tab = it }, Modifier.padding(horizontal = 20.dp))
        if (tab == 0) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
                Text("문의 유형", style = WmTypography.Label2Bold, color = WmColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    inquiryTypes.take(3).forEach { WmChip(it, it == type, { type = it }) }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    inquiryTypes.drop(3).forEach { WmChip(it, it == type, { type = it }) }
                }
                Spacer(Modifier.height(20.dp))
                Text("제목", style = WmTypography.Label2Bold, color = WmColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                WmTextField(title, { title = it.take(40) }, "제목을 입력해 주세요")
                Spacer(Modifier.height(20.dp))
                Text("내용", style = WmTypography.Label2Bold, color = WmColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                WmTextField(body, { body = it.take(1000) }, "어떤 점이 궁금하거나 불편했는지 자세히 적어주세요 (10자 이상)", singleLine = false, minHeight = 180)
                Text(
                    "${body.length}/1000",
                    style = WmTypography.Caption1,
                    color = WmColors.TextTertiary,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                )
            }
            WmBottomCta("보내기", {
                history.add(0, Inquiry(type!!, title, "방금", null))
                type = null
                title = ""
                body = ""
                tab = 1
                Toast.makeText(context, "문의가 접수되었어요", Toast.LENGTH_SHORT).show()
            }, enabled = canSend)
        } else {
            LazyColumn(Modifier.weight(1f).padding(top = 8.dp)) {
                items(history) { inquiry ->
                    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WmTag(inquiry.type)
                            Spacer(Modifier.width(6.dp))
                            Text(inquiry.title, style = WmTypography.Body2Bold, color = WmColors.TextPrimary, modifier = Modifier.weight(1f))
                            WmTag(
                                if (inquiry.answer != null) "답변 완료" else "답변 대기",
                                background = if (inquiry.answer != null) WmColors.Primary else WmColors.SurfaceVariant,
                                contentColor = if (inquiry.answer != null) WmColors.OnPrimary else WmColors.TextSecondary,
                            )
                        }
                        Text(inquiry.date, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.padding(top = 2.dp))
                        inquiry.answer?.let {
                            Text(
                                "워드메이트 · $it",
                                style = WmTypography.Label2,
                                color = WmColors.TextSecondary,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                                    .clip(WmRadius.md)
                                    .background(WmColors.Surface)
                                    .padding(14.dp),
                            )
                        }
                    }
                    WmDivider(Modifier.padding(horizontal = 20.dp))
                }
            }
        }
    }
}
