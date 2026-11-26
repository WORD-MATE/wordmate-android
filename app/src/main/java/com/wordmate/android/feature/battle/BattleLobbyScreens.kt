package com.wordmate.android.feature.battle

import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmAvatar
import com.wordmate.android.core.designsystem.component.WmBottomCta
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.component.WmTopBar
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.BattlePlayer
import com.wordmate.android.data.model.BattleRoom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 대기실 (Figma "대기실", "내가 방장이 아닐때 대기실"). */
@Composable
fun BattleLobbyScreen(onBack: () -> Unit, onStart: () -> Unit) {
    val repository = rememberRepository()
    val initialRoom = repository.currentRoom ?: return
    var room by remember { mutableStateOf(initialRoom) }
    val me = room.players.first { it.isMe }
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    // 친구 입장 시뮬레이션: 잠시 후 한 명이 더 들어온다. 방장이 아니면 방장이 곧 시작한다.
    LaunchedEffect(room.code) {
        delay(2_500)
        if (room.players.size < room.settings.maxPlayers) {
            room = room.copy(players = room.players + BattlePlayer("이서연", "2학년 2반"))
            repository.currentRoom = room
        }
        if (!me.isHost) {
            delay(2_000)
            onStart()
        }
    }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding()) {
        WmTopBar("대기실", onBack = onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Column(Modifier.fillMaxWidth().clip(WmRadius.lg).background(WmColors.Surface).padding(16.dp)) {
                Text("초대 코드", style = WmTypography.Caption1, color = WmColors.TextTertiary)
                Spacer(Modifier.height(8.dp))
                CodeBoxes(room.code, filled = true)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WmButton(
                        "코드 복사",
                        {
                            scope.launch {
                                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("code", room.code)))
                            }
                            Toast.makeText(context, "코드를 복사했어요", Toast.LENGTH_SHORT).show()
                        },
                        Modifier.weight(1f), style = WmButtonStyle.Outline, height = 40.dp, leadingIcon = Icons.Rounded.ContentCopy,
                    )
                    WmButton(
                        "링크 공유",
                        {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "WORD MATE 대결에 참여해요! 코드: ${room.code}")
                            }
                            context.startActivity(Intent.createChooser(send, "대결 초대"))
                        },
                        Modifier.weight(1f), style = WmButtonStyle.Outline, height = 40.dp, leadingIcon = Icons.Rounded.Share,
                    )
                }
                Spacer(Modifier.height(16.dp))
                InfoLine("단어팩", room.settings.pack.title)
                InfoLine("점수 방식", room.settings.mode.label)
                InfoLine("문항", "${room.settings.questionCount}문항 · 문항당 ${room.settings.secondsPerQuestion}초")
            }
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("참가자 ", style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                Text("${room.players.size}", style = WmTypography.Body2Bold, color = WmColors.PrimaryDark)
                Text(" / ${room.settings.maxPlayers}명", style = WmTypography.Body2, color = WmColors.TextTertiary)
                Spacer(Modifier.weight(1f))
                Text(
                    if (me.isHost) "방장이 대결을 시작할 수 있어요" else "방장이 시작하면 바로 대결이 시작돼요",
                    style = WmTypography.Caption1,
                    color = WmColors.TextTertiary,
                )
            }
            Spacer(Modifier.height(10.dp))
            PlayerGrid(room)
            Spacer(Modifier.height(16.dp))
        }
        if (me.isHost) {
            WmBottomCta("${room.players.size}명으로 시작하기", onStart, enabled = room.players.size >= 2)
        } else {
            WmBottomCta("방장이 시작하길 기다리는 중", {}, enabled = false)
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Text(label, style = WmTypography.Caption1, color = WmColors.TextTertiary, modifier = Modifier.width(72.dp))
        Text(value, style = WmTypography.Caption1Bold, color = WmColors.TextPrimary)
    }
}

@Composable
private fun PlayerGrid(room: BattleRoom) {
    val slots = room.players + List((4 - room.players.size).coerceAtLeast(0)) { null }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { player -> PlayerSlot(player, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PlayerSlot(player: BattlePlayer?, modifier: Modifier) {
    Box(
        modifier
            .aspectRatio(1.5f)
            .clip(WmRadius.md)
            .background(if (player?.isMe == true) WmColors.SurfacePressed else WmColors.Surface)
            .border(1.dp, if (player == null) WmColors.Divider else WmColors.Surface, WmRadius.md),
        contentAlignment = Alignment.Center,
    ) {
        if (player == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.PersonOutline, null, tint = WmPalette.Gray11, modifier = Modifier.size(28.dp))
                Text("빈 자리", style = WmTypography.Caption1, color = WmColors.TextDisabled)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WmAvatar(size = 40.dp)
                Spacer(Modifier.height(6.dp))
                Text(if (player.isMe) "${player.name} (나)" else player.name, style = WmTypography.Label2Bold, color = WmColors.TextPrimary)
                Text(player.schoolInfo, style = WmTypography.Caption2, color = WmColors.TextTertiary)
            }
            if (player.isHost) {
                WmTag("방장", Modifier.align(Alignment.TopStart).padding(8.dp), WmColors.Primary, WmColors.OnPrimary)
            }
        }
    }
}

/** 6칸 코드 박스. [filled] 가 false 면 입력 중 상태로 다음 칸을 강조한다. */
@Composable
fun CodeBoxes(code: String, filled: Boolean, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(6) { i ->
            val char = code.getOrNull(i)?.toString() ?: ""
            val focused = !filled && i == code.length
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(0.85f)
                    .clip(WmRadius.sm)
                    .background(if (filled || char.isNotEmpty()) WmColors.Background else WmColors.SurfaceVariant)
                    .border(
                        if (focused) 2.dp else 1.dp,
                        if (focused) WmColors.TextPrimary else WmColors.Border,
                        WmRadius.sm,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(char, style = WmTypography.Headline1, color = WmColors.TextPrimary)
            }
        }
    }
}

/** 참가 코드 입력 (Figma "코드 입력"). */
@Composable
fun BattleCodeScreen(onBack: () -> Unit, onJoined: () -> Unit) {
    val repository = rememberRepository()
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }

    Column(Modifier.fillMaxSize().background(WmColors.Background).navigationBarsPadding().imePadding()) {
        WmTopBar("코드로 참가", onBack = onBack)
        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("참가 코드를 입력하세요", style = WmTypography.Headline2, color = WmColors.TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("친구나 선생님에게 받은 6자리 코드", style = WmTypography.Label2, color = WmColors.TextSecondary)
            Spacer(Modifier.height(24.dp))
            BasicTextField(
                value = code,
                onValueChange = { value ->
                    code = value.uppercase().filter { it.isLetterOrDigit() }.take(6)
                    error = null
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType = KeyboardType.Ascii,
                ),
                modifier = Modifier.focusRequester(focus),
                decorationBox = { CodeBoxes(code, filled = false) },
            )
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = WmTypography.Caption1, color = WmColors.Error)
            }
        }
        WmBottomCta("참가하기", {
            if (repository.joinRoom(code) != null) onJoined() else error = "코드를 다시 확인해 주세요"
        }, enabled = code.length == 6, style = if (code.length == 6) WmButtonStyle.Primary else WmButtonStyle.Secondary)
    }
}

/** 대결 기록 전체 (Figma "대결 기록"). */
@Composable
fun BattleHistoryScreen(onBack: () -> Unit) {
    val records by rememberRepository().battleRecords.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmTopBar("대결 기록", onBack = onBack)
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text("내 대결 기록", style = WmTypography.Title2, color = WmColors.TextPrimary, modifier = Modifier.padding(vertical = 8.dp))
                BattleStatsCard(records)
                Spacer(Modifier.height(8.dp))
            }
            items(records, key = { it.id }) { BattleRecordRow(it) }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
