package com.wordmate.android.feature.wordpack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordmate.android.core.designsystem.component.WmButton
import com.wordmate.android.core.designsystem.component.WmButtonStyle
import com.wordmate.android.core.designsystem.component.WmCard
import com.wordmate.android.core.designsystem.component.WmChipRow
import com.wordmate.android.core.designsystem.component.WmEmptyState
import com.wordmate.android.core.designsystem.component.WmIconButton
import com.wordmate.android.core.designsystem.component.WmLargeTopBar
import com.wordmate.android.core.designsystem.component.WmProgressBar
import com.wordmate.android.core.designsystem.component.WmSearchField
import com.wordmate.android.core.designsystem.component.WmSegmentedTabs
import com.wordmate.android.core.designsystem.component.WmTag
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmTypography
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.data.model.PackCategory
import com.wordmate.android.data.model.WordPack

@Composable
fun WordPacksScreen(
    onPackClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
) {
    val repository = rememberRepository()
    val packs by repository.packs.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(PackCategory.All) }

    val visible = packs
        .filter { it.isMine == (tab == 1) }
        .filter { category == PackCategory.All || it.category == category }
        .filter { query.isBlank() || it.title.contains(query.trim(), ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        WmLargeTopBar("단어팩") {
            WmIconButton(Icons.Rounded.Notifications, "알림", {}, showBadge = true)
        }
        WmSegmentedTabs(
            tabs = listOf("기본 단어팩", "내 단어팩"),
            selectedIndex = tab,
            onSelect = { tab = it },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))
        WmSearchField(query, { query = it }, "단어팩 이름으로 검색", Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(12.dp))
        WmChipRow(
            items = PackCategory.entries,
            selected = category,
            label = { it.label },
            onSelect = { category = it },
        )
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            if (tab == 1) {
                item {
                    WmButton(
                        "새 단어팩", onCreateClick, Modifier.fillMaxWidth(),
                        style = WmButtonStyle.Primary, height = 44.dp, leadingIcon = Icons.Rounded.Add,
                    )
                }
            }
            if (visible.isEmpty()) {
                item { WmEmptyState(Icons.Rounded.Bookmark, "검색 결과가 없어요") }
            }
            items(visible, key = { it.id }) { pack -> PackCard(pack) { onPackClick(pack.id) } }
        }
    }
}

@Composable
fun PackCard(pack: WordPack, onClick: () -> Unit) {
    WmCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WmTag("${pack.wordCount}단어")
            if (pack.isCompleted) {
                Spacer(Modifier.width(4.dp))
                WmTag("완료", background = WmColors.Primary, contentColor = WmColors.OnPrimary)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(pack.title, style = WmTypography.Body1Bold, color = WmColors.TextPrimary)
        Spacer(Modifier.height(2.dp))
        Text(pack.description, style = WmTypography.Caption1, color = WmColors.TextTertiary)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WmProgressBar(
                pack.progress,
                Modifier.weight(1f),
                color = if (pack.isCompleted) WmPalette.Lime02 else WmColors.Primary,
            )
            Spacer(Modifier.width(10.dp))
            Text("${(pack.progress * 100).toInt()}%", style = WmTypography.Caption1, color = WmColors.TextTertiary)
        }
    }
}
