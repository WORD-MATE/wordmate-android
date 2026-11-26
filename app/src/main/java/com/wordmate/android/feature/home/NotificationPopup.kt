package com.wordmate.android.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.component.WmDivider
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.data.model.AppNotification
import com.wordmate.android.data.model.NotificationType

/** 홈 상단 종 아이콘을 누르면 나오는 알림 팝오버 (Figma "알림"). */
@Composable
fun NotificationPopup(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onItemClick: (AppNotification) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
    ) {
        Column(
            Modifier
                .padding(top = 52.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .shadow(16.dp, WmRadius.lg)
                .clip(WmRadius.lg)
                .background(WmColors.Background)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("알림", style = WmTypography.Body2Bold, color = WmColors.TextPrimary)
                Spacer(Modifier.width(6.dp))
                val unread = notifications.count { !it.read }
                if (unread > 0) WmTag("$unread", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
                Spacer(Modifier.weight(1f))
                Text(
                    "모두 읽음",
                    style = WmTypography.Caption1,
                    color = WmColors.TextSecondary,
                    modifier = Modifier.clip(WmRadius.xs).clickable(onClick = onMarkAllRead).padding(4.dp),
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Settings, "알림 설정", tint = WmColors.TextPrimary, modifier = Modifier.size(20.dp))
            }
            WmDivider()
            notifications.forEach { item ->
                NotificationRow(item) { onItemClick(item) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NotificationRow(item: AppNotification, onClick: () -> Unit) {
    val (icon, tint) = when (item.type) {
        NotificationType.BattleInvite -> Icons.Rounded.Bolt to WmPalette.Yellow02
        NotificationType.DailySentence -> Icons.Rounded.WbSunny to WmPalette.Orange06
        NotificationType.FriendRequest -> Icons.Rounded.PersonAdd to WmColors.Info
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(WmRadius.sm).background(WmColors.SurfaceVariant),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.title, style = WmTypography.Label1Bold, color = WmColors.TextPrimary)
                if (!item.read) {
                    Spacer(Modifier.width(4.dp))
                    Box(Modifier.size(5.dp).clip(CircleShape).background(WmColors.Notification))
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(item.body, style = WmTypography.Caption1, color = WmColors.TextSecondary, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text(item.timeAgo, style = WmTypography.Caption2, color = WmColors.TextTertiary)
        }
        Text(
            if (item.read) "읽음" else "",
            style = WmTypography.Caption2,
            color = if (item.read) WmColors.TextTertiary else Color.Transparent,
        )
    }
}
