package com.myhealth.app.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.util.CrashLog
import com.myhealth.app.feature.fasting.FastingInfo
import com.myhealth.app.feature.fasting.FastingPlan
import com.myhealth.app.ui.kit.HGap
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.VGap
import java.time.LocalDateTime
import kotlinx.coroutines.delay

// Ana sayfa (sekme ekrani): selamlama, yapay zeka onerisi, hizli kartlar, Ramazan geri sayimi ve kalori.
@Composable
fun HomeScreen() {
    val ctx = LocalContext.current
    val fasting: FastingInfo? = remember(ctx) {
        try {
            FastingPlan.today(ctx)
        } catch (e: Exception) {
            // CrashLog paketi: com.myhealth.app.util
            CrashLog.note(ctx, "home_fasting", e)
            null
        }
    }
    var nowDt by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            nowDt = LocalDateTime.now()
        }
    }
    ScreenScaffold(clearBottomNav = true) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            VGap(8.dp)
            HomeHeader()
            VGap(16.dp)
            AiTipCard()
            VGap(14.dp)
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                SportCard(modifier = Modifier.weight(1f).fillMaxHeight())
                HGap(12.dp)
                BreathCard(modifier = Modifier.weight(1f).fillMaxHeight())
            }
            VGap(12.dp)
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                StepsCard(modifier = Modifier.weight(1f).fillMaxHeight())
                HGap(12.dp)
                WaterCard(modifier = Modifier.weight(1f).fillMaxHeight())
            }
            VGap(12.dp)
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                WeatherCard(modifier = Modifier.weight(1f).fillMaxHeight())
                HGap(12.dp)
                AlarmCard(now = nowDt, modifier = Modifier.weight(1f).fillMaxHeight())
            }
            VGap(12.dp)
            val info = fasting
            if (info != null && info.ramadan) {
                RamadanCard(info = info, now = nowDt, modifier = Modifier.fillMaxWidth())
                VGap(12.dp)
            }
            KcalCard(modifier = Modifier.fillMaxWidth())
            VGap(LocalBottomClearance.current + 24.dp)
        }
    }
}
