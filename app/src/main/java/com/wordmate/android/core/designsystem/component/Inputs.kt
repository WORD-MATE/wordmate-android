package com.wordmate.android.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmPalette
import com.wordmate.android.core.designsystem.theme.WmRadius
import com.wordmate.android.core.designsystem.theme.WmTypography

@Composable
fun WmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    singleLine: Boolean = true,
    minHeight: Int = 52,
    leading: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    val borderColor = if (isError) WmColors.Error else Color.Transparent
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = WmTypography.Body2.copy(color = WmColors.TextPrimary),
        cursorBrush = SolidColor(WmColors.TextPrimary),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        modifier = modifier,
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight.dp)
                    .clip(WmRadius.md)
                    .background(if (isError) Color(0xFFFFF4F4) else WmColors.SurfaceVariant)
                    .border(1.dp, borderColor, WmRadius.md)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
            ) {
                if (leading != null) {
                    leading()
                    Spacer(Modifier.width(8.dp))
                }
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = WmTypography.Body2, color = WmColors.TextTertiary)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
fun WmSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    WmTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        minHeight = 48,
        imeAction = ImeAction.Search,
        leading = {
            Icon(Icons.Rounded.Search, null, tint = WmColors.TextTertiary, modifier = Modifier.size(20.dp))
        },
    )
}

/** 검정 채움 / 회색 기본 필터 칩. */
@Composable
fun WmChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    dotColor: Color? = null,
) {
    val bg = if (selected) WmColors.Black else WmColors.Background
    val fg = if (selected) WmPalette.Gray01 else WmColors.TextSecondary
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(WmRadius.sm)
            .background(bg)
            .border(1.dp, if (selected) bg else WmColors.Border, WmRadius.sm)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dotColor != null) {
            Box(Modifier.size(6.dp).clip(WmRadius.full).background(dotColor))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = WmTypography.Label2Bold, color = fg)
        if (count != null) {
            Spacer(Modifier.width(4.dp))
            Text("$count", style = WmTypography.Label2, color = if (selected) WmPalette.Gray11 else WmColors.TextTertiary)
        }
    }
}

@Composable
fun <T> WmChipRow(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    count: ((T) -> Int?)? = null,
    dot: ((T) -> Color?)? = null,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(items) { item ->
            WmChip(
                text = label(item),
                selected = item == selected,
                onClick = { onSelect(item) },
                count = count?.invoke(item),
                dotColor = dot?.invoke(item),
            )
        }
    }
}

/** 상단 세그먼트 탭 (기본 단어팩 / 내 단어팩 등). */
@Composable
fun WmSegmentedTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(WmRadius.md)
            .background(WmColors.SurfaceVariant)
            .padding(4.dp),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(WmRadius.sm)
                    .background(if (selected) WmColors.Background else Color.Transparent)
                    .clickable(role = Role.Tab) { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    title,
                    style = if (selected) WmTypography.Label1Bold else WmTypography.Label1,
                    color = if (selected) WmColors.TextPrimary else WmColors.TextTertiary,
                )
            }
        }
    }
}

/** 원형 체크박스 (약관 / 라디오 선택). */
@Composable
fun WmCheckCircle(checked: Boolean, modifier: Modifier = Modifier, size: Int = 22) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(WmRadius.full)
            .background(if (checked) WmColors.Primary else WmColors.Background)
            .border(1.5.dp, if (checked) WmColors.Primary else WmColors.Border, WmRadius.full),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(Icons.Rounded.Check, null, tint = WmColors.OnPrimary, modifier = Modifier.size((size * 0.7).dp))
        }
    }
}

@Composable
fun WmSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = WmPalette.Gray01,
            checkedTrackColor = WmColors.Primary,
            checkedBorderColor = WmColors.Primary,
            uncheckedThumbColor = WmPalette.Gray01,
            uncheckedTrackColor = WmPalette.Gray10,
            uncheckedBorderColor = WmPalette.Gray10,
        ),
    )
}
