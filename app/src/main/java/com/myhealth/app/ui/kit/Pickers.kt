package com.myhealth.app.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import kotlin.math.abs

/**
 * Dikey liste secici (iOS tekerlegi gibi): ortadaki satir secili olur, birakinca en yakina oturur.
 * selectedIndex yalnizca ilk konumu belirler; secim degistikce onSelectedChange cagrilir.
 */
@Composable
fun WheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 52.dp,
    visibleCount: Int = 5
) {
    val lastIndex = (items.size - 1).coerceAtLeast(0)
    val start = selectedIndex.coerceIn(0, lastIndex)
    val state = rememberLazyListState(initialFirstVisibleItemIndex = start)
    val pad = itemHeight * (visibleCount / 2)
    val current by remember(items.size) {
        derivedStateOf {
            val info = state.layoutInfo
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
            val best = info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - center) }
            (best?.index ?: start).coerceIn(0, lastIndex)
        }
    }

    // Parmak kalkinca en yakin satira oturt.
    LaunchedEffect(state, items.size) {
        snapshotFlow { state.isScrollInProgress }.collect { scrolling ->
            if (!scrolling) state.animateScrollToItem(current)
        }
    }
    LaunchedEffect(current) { onSelectedChange(current) }

    Box(modifier.height(itemHeight * visibleCount), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Lux.glass, RoundedCornerShape(16.dp))
        )
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxWidth().height(itemHeight * visibleCount),
            contentPadding = PaddingValues(vertical = pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = items.size) { i ->
                val dist = abs(i - current)
                Box(Modifier.fillMaxWidth().height(itemHeight), contentAlignment = Alignment.Center) {
                    Txt(
                        items[i],
                        sty = if (dist == 0) Sty.H2 else Sty.TITLE,
                        color = when (dist) {
                            0 -> Lux.gold
                            1 -> Lux.textDim
                            else -> Lux.textFaint
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** Saat:dakika secim diyalogu (alarm, ogun saati). Dakika olarak 0..1439 dondurur. */
@Composable
fun TimeWheelDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val init = initialMinutes.coerceIn(0, 1439)
    var hour by remember { mutableIntStateOf(init / 60) }
    var minute by remember { mutableIntStateOf(init % 60) }
    val hours = remember { (0..23).map { if (it < 10) "0$it" else "$it" } }
    val minutes = remember { (0..59).map { if (it < 10) "0$it" else "$it" } }
    LuxDialog(onDismiss = onDismiss) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(hours, init / 60, { hour = it }, Modifier.width(96.dp), itemHeight = 48.dp, visibleCount = 5)
            Txt(":", sty = Sty.H1, color = Lux.gold)
            WheelPicker(minutes, init % 60, { minute = it }, Modifier.width(96.dp), itemHeight = 48.dp, visibleCount = 5)
        }
        Gap(16.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton(t("common.cancel"), onClick = onDismiss, modifier = Modifier.weight(1f))
            PrimaryButton(t("common.done"), onClick = { onConfirm(hour * 60 + minute) }, modifier = Modifier.weight(1f))
        }
    }
}
