package com.wordmate.android.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmCheckCircle
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.designsystem.theme.WordMateTheme

enum class TermType(val title: String, val required: Boolean, val body: List<Pair<String, String>>) {
    Service(
        "서비스 이용약관", true,
        listOf(
            "목적" to "본 약관은 WORD MATE(이하 \"서비스\")의 이용 조건과 절차, 이용자와 운영자의 권리·의무를 규정합니다.",
            "이용 계약" to "이용자는 학교 계정(DataGSM)으로 로그인하고 본 약관에 동의함으로써 서비스를 이용할 수 있습니다.",
            "이용자의 의무" to "이용자는 대결·단어팩 기능을 이용할 때 다른 이용자에게 불쾌감을 주는 행위를 해서는 안 됩니다.",
        ),
    ),
    Privacy(
        "개인정보 수집·이용 동의", true,
        listOf(
            "수집 항목" to "이름, 학년·반·번호, 학교 계정 식별자, 학습 및 대결 기록",
            "이용 목적" to "학습 진도 관리, 랭킹 산정, 대결 매칭 및 서비스 개선",
            "보유 기간" to "회원 탈퇴 시 지체 없이 파기 (관계 법령에 따라 보관이 필요한 경우 제외)",
        ),
    ),
    Marketing(
        "마케팅 정보 수신 동의", false,
        listOf(
            "수신 내용" to "새 단어팩 출시, 학교 대항전 등 이벤트 소식",
            "수신 방법" to "앱 푸시 알림, 학교 이메일",
            "철회" to "동의하지 않아도 서비스 이용에 제한이 없으며, 설정 > 알림 설정에서 언제든 변경할 수 있습니다.",
        ),
    ),
}

@Composable
fun TermsScreen(
    userName: String,
    onBack: () -> Unit,
    onOpenDetail: (TermType) -> Unit,
    onAgree: () -> Unit,
) {
    var agreed by rememberSaveable { mutableStateOf(setOf<TermType>()) }
    val allAgreed = agreed.size == TermType.entries.size
    val requiredAgreed = TermType.entries.filter { it.required }.all { it in agreed }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar(title = "", onBack = onBack)
        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("${userName}님, 환영해요!\n약관에 동의해 주세요", style = WmTypography.Headline1, color = WmColors.TextPrimary)
            Spacer(Modifier.height(32.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(WmRadius.md)
                    .background(if (allAgreed) WmColors.SurfacePressed else WmColors.Surface)
                    .clickable(role = Role.Checkbox) {
                        agreed = if (allAgreed) emptySet() else TermType.entries.toSet()
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WmCheckCircle(allAgreed)
                Spacer(Modifier.width(12.dp))
                Text("전체 동의하기", style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
            }
            Spacer(Modifier.height(12.dp))
            TermType.entries.forEach { term ->
                val checked = term in agreed
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Checkbox) { agreed = if (checked) agreed - term else agreed + term }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WmCheckCircle(checked)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "[${if (term.required) "필수" else "선택"}] ${term.title}",
                        style = WmTypography.Label1,
                        color = WmColors.TextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "${term.title} 자세히 보기",
                        tint = WmColors.TextTertiary,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(WmRadius.full)
                            .clickable { onOpenDetail(term) }
                            .padding(4.dp),
                    )
                }
            }
        }
        WmBottomCta(text = "동의하고 시작하기", onClick = onAgree, enabled = requiredAgreed)
    }
}

@Composable
fun TermDetailScreen(term: TermType, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar(title = "", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Text(term.title, style = WmTypography.Headline2, color = WmColors.TextPrimary)
            Spacer(Modifier.height(24.dp))
            term.body.forEach { (heading, text) ->
                Text(heading, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text(text, style = WmTypography.Label1, color = WmColors.TextSecondary)
                Spacer(Modifier.height(20.dp))
            }
        }
        WmBottomCta(text = "확인", onClick = onBack)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TermsPreview() = WordMateTheme { TermsScreen("한국", {}, {}, {}) }
