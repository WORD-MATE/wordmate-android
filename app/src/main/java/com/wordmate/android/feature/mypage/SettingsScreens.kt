package com.wordmate.android.feature.mypage

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmDialog
import com.wordmate.android.core.designsystem.component.WmListRow
import com.wordmate.android.core.designsystem.component.WmSmallButton
import com.wordmate.android.core.designsystem.component.WmSwitch
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.component.WmThickDivider
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository

@Composable
private fun GroupLabel(text: String) {
    Text(
        text,
        style = WmTypography.Caption1Bold,
        color = WmColors.TextTertiary,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp),
    )
}

/** 설정 (알림 · 계정 · 약관). */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNotificationSettings: () -> Unit,
    onLoginManagement: () -> Unit,
    onTerms: (String) -> Unit,
    onLoggedOut: () -> Unit,
) {
    val repository = rememberRepository()
    var dialog by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("설정", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            GroupLabel("알림")
            WmListRow("알림 설정", onClick = onNotificationSettings)
            WmThickDivider(Modifier.padding(top = 8.dp))
            GroupLabel("계정")
            WmListRow("로그인 관리", onClick = onLoginManagement)
            WmListRow("로그아웃", showChevron = false, onClick = { dialog = "logout" })
            WmListRow("탈퇴하기", titleColor = WmColors.Error, showChevron = false, onClick = { dialog = "withdraw" })
            WmThickDivider(Modifier.padding(top = 8.dp))
            GroupLabel("약관")
            WmListRow("서비스 이용약관", onClick = { onTerms("Service") })
            WmListRow("개인정보 처리방침", onClick = { onTerms("Privacy") })
            WmListRow("앱 버전", trailingText = "1.0.0", showChevron = false)
        }
    }

    when (dialog) {
        "logout" -> WmDialog(
            title = "로그아웃할까요?",
            description = "다시 로그인하면 학습 기록을 그대로 이어갈 수 있어요.",
            confirmText = "로그아웃",
            onConfirm = {
                dialog = null
                repository.logout()
                onLoggedOut()
            },
            onDismiss = { dialog = null },
        )
        "withdraw" -> WmDialog(
            title = "정말 탈퇴할까요?",
            description = "학습 기록, 단어팩, 랭킹 배지가 모두 삭제되고 복구할 수 없어요.",
            confirmText = "탈퇴하기",
            confirmStyle = WmButtonStyle.Danger,
            onConfirm = {
                dialog = null
                repository.logout()
                onLoggedOut()
            },
            onDismiss = { dialog = null },
        )
    }
}

private data class NotificationOption(val key: String, val title: String, val description: String)

private val notificationOptions = listOf(
    NotificationOption("battle", "대결 초대", "친구가 대결방에 초대하면 알려드려요"),
    NotificationOption("sentence", "오늘의 문장", "매일 아침 새 문장이 도착하면 알려드려요"),
    NotificationOption("friend", "친구 요청", "새 친구 요청을 알려드려요"),
    NotificationOption("reminder", "학습 리마인더", "매일 오후 8시에 학습을 잊지 않게 알려드려요"),
    NotificationOption("ranking", "랭킹 변동", "주간 랭킹이 마감되면 결과를 알려드려요"),
    NotificationOption("marketing", "이벤트·혜택 (선택)", "새 단어팩, 학교 대항전 소식을 받아요"),
)

/** 알림 설정. */
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit) {
    val states = remember {
        mutableStateMapOf<String, Boolean>().apply { notificationOptions.forEach { put(it.key, it.key != "marketing") } }
    }
    val allOn = states.values.all { it }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("알림 설정", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            ToggleRow("전체 알림", "모든 알림을 한 번에 켜고 끌 수 있어요", allOn) { on ->
                notificationOptions.forEach { states[it.key] = on }
            }
            WmThickDivider()
            notificationOptions.forEach { option ->
                ToggleRow(option.title, option.description, states[option.key] == true) { states[option.key] = it }
            }
        }
    }
}

@Composable
private fun ToggleRow(title: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
            Text(description, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
        WmSwitch(checked, onChange)
    }
}

private data class LoginDevice(val id: Int, val name: String, val detail: String, val icon: ImageVector, val current: Boolean = false)

/** 로그인 관리 (Figma "로그인 관리", "특정 기기 로그아웃", "다른 기기 모두 로그아웃"). */
@Composable
fun LoginManagementScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val devices = remember {
        mutableStateListOf(
            LoginDevice(1, "Android · 이 기기", "지금 사용 중", Icons.Rounded.PhoneAndroid, current = true),
            LoginDevice(2, "Galaxy Tab S9", "광주 · 3일 전", Icons.Rounded.Tablet),
            LoginDevice(3, "Chrome · Windows", "광주 · 1주 전", Icons.Rounded.Computer),
        )
    }
    var target by remember { mutableStateOf<LoginDevice?>(null) }
    var logoutAll by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("로그인 관리", onBack = onBack)
        Text(
            "WORD MATE에 로그인된 기기 ${devices.size}대",
            style = WmTypography.Label2,
            color = WmColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            devices.forEach { device ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(WmRadius.md).background(WmColors.Surface), contentAlignment = Alignment.Center) {
                        Icon(device.icon, null, tint = WmColors.TextSecondary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(device.name, style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                            if (device.current) {
                                Spacer(Modifier.width(4.dp))
                                WmTag("현재", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
                            }
                        }
                        Text(device.detail, style = WmTypography.Caption1, color = WmColors.TextTertiary)
                    }
                    if (!device.current) WmSmallButton("로그아웃", { target = device }, style = WmButtonStyle.Secondary)
                }
            }
        }
        WmButton(
            "다른 기기 모두 로그아웃",
            { logoutAll = true },
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            style = WmButtonStyle.Outline,
            enabled = devices.size > 1,
        )
        Spacer(Modifier.height(8.dp))
    }

    target?.let { device ->
        WmDialog(
            title = "${device.name}에서 로그아웃할까요?",
            description = "해당 기기에서 다시 로그인해야 WORD MATE를 쓸 수 있어요.",
            confirmText = "로그아웃",
            onConfirm = {
                devices.remove(device)
                target = null
                Toast.makeText(context, "로그아웃했어요", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { target = null },
        )
    }
    if (logoutAll) {
        WmDialog(
            title = "다른 기기에서 모두 로그아웃할까요?",
            description = "지금 사용 중인 기기를 제외한 모든 기기에서 로그아웃돼요.",
            confirmText = "모두 로그아웃",
            onConfirm = {
                devices.removeAll { !it.current }
                logoutAll = false
            },
            onDismiss = { logoutAll = false },
        )
    }
}
