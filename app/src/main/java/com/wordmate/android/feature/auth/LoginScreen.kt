package com.wordmate.android.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.designsystem.theme.WordMateTheme

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(WmColors.Background)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text("wordmate", style = WmTypography.Title2.copy(fontWeight = FontWeight.ExtraBold), color = WmColors.TextPrimary)

        Spacer(Modifier.weight(1f))
        MateLogo()
        Spacer(Modifier.weight(1.2f))

        Text(
            text = buildAnnotatedString {
                append("매일 10분,\n단어로 ")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("겨루고") }
                append(" 성장하기")
            },
            style = WmTypography.Headline2,
            color = WmColors.TextPrimary,
        )
        Spacer(Modifier.height(24.dp))
        WmButton(
            text = "DataGSM으로 로그인",
            onClick = onLoginClick,
            style = WmButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
    }
}

/** 라임색 하이라이트 위에 얹힌 "mate" 워드마크. */
@Composable
fun MateLogo(modifier: Modifier = Modifier) {
    Text(
        "mate",
        style = WmTypography.Display.copy(fontSize = 84.sp, lineHeight = 88.sp, fontWeight = FontWeight.Black),
        color = WmColors.TextPrimary,
        modifier = modifier.drawBehind {
            val inset = 6.dp.toPx()
            drawRect(
                color = WmColors.Primary,
                topLeft = Offset(-inset, size.height * 0.58f),
                size = Size(size.width + inset * 2, size.height * 0.2f),
            )
        },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun LoginPreview() = WordMateTheme { LoginScreen {} }
