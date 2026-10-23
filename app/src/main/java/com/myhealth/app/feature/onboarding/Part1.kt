package com.myhealth.app.feature.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Lang
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Onboarding ortak parcalari: taslak durum, cerceve, ilerleme, secim satiri; dil ve karsilama adimlari. */

internal const val ONB_NONE = "__none__"
internal const val ONB_TOTAL = 17

internal fun encodeSet(s: Set<String>): String = s.joinToString("|")

internal fun decodeSet(s: String): Set<String> =
    if (s.isEmpty()) emptySet() else s.split("|").filter { it.isNotEmpty() }.toSet()

/** Secimi degistirir; "Yok" isareti varsa kaldirir. */
internal fun toggleSel(sel: Set<String>, id: String): Set<String> {
    val base = sel - ONB_NONE
    return if (id in base) base - id else base + id
}

/** Akis boyunca tutulan taslak; Groq anahtari guvenlik icin kaydedilen duruma yazilmaz. */
internal class OnbDraft {
    var gender by mutableStateOf("")
    var name by mutableStateOf("")
    var age by mutableStateOf(30)
    var heightCm by mutableStateOf(175)
    var weightKg by mutableStateOf(70)
    var conditions by mutableStateOf<Set<String>>(emptySet())
    var allergies by mutableStateOf<Set<String>>(emptySet())
    var medsRegular by mutableStateOf<Set<String>>(emptySet())
    var medsOccasional by mutableStateOf<Set<String>>(emptySet())
    var doctorName by mutableStateOf("")
    var doctorPhone by mutableStateOf("")
    var groqKey by mutableStateOf("")
}

private fun List<Any>.sAt(i: Int): String = (getOrNull(i) as? String).orEmpty()
private fun List<Any>.iAt(i: Int, def: Int): Int = (getOrNull(i) as? Int) ?: def

internal val OnbDraftSaver: Saver<OnbDraft, Any> = listSaver<OnbDraft, Any>(
    save = { d ->
        listOf<Any>(
            d.gender, d.name, d.age, d.heightCm, d.weightKg,
            encodeSet(d.conditions), encodeSet(d.allergies),
            encodeSet(d.medsRegular), encodeSet(d.medsOccasional),
            d.doctorName, d.doctorPhone
        )
    },
    restore = { l ->
        OnbDraft().also { d ->
            d.gender = l.sAt(0)
            d.name = l.sAt(1)
            d.age = l.iAt(2, 30)
            d.heightCm = l.iAt(3, 175)
            d.weightKg = l.iAt(4, 70)
            d.conditions = decodeSet(l.sAt(5))
            d.allergies = decodeSet(l.sAt(6))
            d.medsRegular = decodeSet(l.sAt(7))
            d.medsOccasional = decodeSet(l.sAt(8))
            d.doctorName = l.sAt(9)
            d.doctorPhone = l.sAt(10)
        }
    }
)

/** Ince ilerleme cubugu + "n/17" metni. */
@Composable
internal fun OnbProgress(stepNo: Int, modifier: Modifier = Modifier) {
    val frac = (stepNo.toFloat() / ONB_TOTAL.toFloat()).coerceIn(0f, 1f)
    val anim by animateFloatAsState(targetValue = frac, animationSpec = tween(300))
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Lux.glassStrong)
        ) {
            Box(Modifier.fillMaxWidth(anim.coerceIn(0f, 1f)).fillMaxHeight().background(Lux.accentBrush))
        }
        HGap(10.dp)
        Txt(t("ob.step", stepNo, ONB_TOTAL), sty = Sty.CAPTION, color = Lux.textDim)
    }
}

/** Tum adimlarin ortak cercevesi: arka plan, geri dugmesi, ilerleme, baslik, icerik ve alt dugmeler. */
@Composable
internal fun OnbFrame(
    title: String,
    subtitle: String?,
    stepNo: Int,
    onBack: (() -> Unit)?,
    bottom: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    showProgress: Boolean = true,
    scroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier.fillMaxSize()) {
        LuxBackdrop(glow = Glow.ARCS)
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconBtn(
                        icon = Ic.BACK, onClick = onBack, description = t("ob.back"),
                        size = 48.dp, autoMirror = true
                    )
                } else {
                    Spacer(Modifier.size(48.dp))
                }
                HGap(12.dp)
                if (showProgress) OnbProgress(stepNo, Modifier.weight(1f)) else Spacer(Modifier.weight(1f))
            }
            VGap(8.dp)
            Txt(title, sty = Sty.H1)
            if (subtitle != null) {
                VGap(8.dp)
                Txt(subtitle, sty = Sty.BODY, color = Lux.textDim)
            }
            VGap(12.dp)
            val body = Modifier.weight(1f).fillMaxWidth()
            if (scroll) {
                Column(body.verticalScroll(rememberScrollState()), content = content)
            } else {
                Column(body, content = content)
            }
            Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp), content = bottom)
        }
    }
}

/** Ana dugme + istege bagli ikincil dugme. */
@Composable
internal fun OnbCta(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    secondaryText: String? = null,
    onSecondary: () -> Unit = {}
) {
    PrimaryButton(text = text, onClick = onClick, modifier = Modifier.fillMaxWidth(), enabled = enabled)
    if (secondaryText != null) {
        VGap(8.dp)
        GhostButton(text = secondaryText, onClick = onSecondary, modifier = Modifier.fillMaxWidth())
    }
}

/** Isaret kutulu secim satiri (dil, hastalik, alerji). Yuksekligi >= 52dp. */
@Composable
internal fun OnbCheckRow(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(shape)
            .background(if (selected) Lux.glassStrong else Lux.glass)
            .border(1.dp, if (selected) Lux.orange else Lux.strokeSoft, shape)
            .pressable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Txt(
            text, modifier = Modifier.weight(1f), sty = Sty.BODY_STRONG,
            color = if (selected) Lux.text else Lux.textDim
        )
        HGap(8.dp)
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (selected) Lux.orange else Color.Transparent)
                .border(1.5.dp, if (selected) Lux.orange else Lux.stroke, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) LuxIcon(path = Ic.CHECK, size = 16.dp, tint = Lux.black)
        }
    }
}

/** 1. adim: dil secimi; secim aninda uygulanir (Repo.setLang). */
@Composable
internal fun StepLanguage(onNext: () -> Unit) {
    val current by Repo.lang.collectAsState()
    OnbFrame(
        title = t("ob.lang.title"),
        subtitle = t("ob.lang.sub"),
        stepNo = 1,
        onBack = null,
        scroll = false,
        bottom = { OnbCta(t("ob.next"), onNext) }
    ) {
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(Lang.values().toList(), key = { it.code }) { l ->
                OnbCheckRow(text = l.nativeName, selected = l == current, onClick = { Repo.setLang(l) })
            }
        }
    }
}

/** Karsilama: halka + GoControl. Giris/hesap yok. */
@Composable
internal fun StepWelcome(onBack: () -> Unit, onNext: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val prog by animateFloatAsState(targetValue = if (started) 0.82f else 0f, animationSpec = tween(1600))
    Box(Modifier.fillMaxSize()) {
        LuxBackdrop(glow = Glow.ARCS)
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                IconBtn(
                    icon = Ic.BACK, onClick = onBack, description = t("ob.back"),
                    size = 48.dp, autoMirror = true
                )
            }
            Spacer(Modifier.weight(1f))
            ProgressRing(progress = prog, diameter = 220.dp, strokeWidth = 12.dp) {
                LuxIcon(path = Ic.PULSE, size = 76.dp, tint = Lux.orange)
            }
            VGap(28.dp)
            TagPill(t("ob.welcome.tag"))
            VGap(14.dp)
            Txt(t("ob.welcome.title"), sty = Sty.DISPLAY, align = TextAlign.Center)
            VGap(10.dp)
            Txt(t("ob.welcome.sub"), color = Lux.textDim, align = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            GoControl(label = t("ob.welcome.start"), onClick = onNext)
            VGap(24.dp)
        }
    }
}
