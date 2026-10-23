package com.myhealth.app.ui.kit

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myhealth.app.ui.LocalLang
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.theme.luxTextStyle

/** Cam gorunumlu metin alani. */
@Composable
fun LuxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {},
    leadingIcon: String? = null,
    password: Boolean = false,
    minHeight: Int = 56,
    trailing: (@Composable () -> Unit)? = null
) {
    val lang = LocalLang.current
    val shape = RoundedCornerShape(18.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        textStyle = luxTextStyle(Sty.BODY_STRONG, lang, Lux.text),
        cursorBrush = SolidColor(Lux.orange),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onDone() },
            onSend = { onDone() },
            onSearch = { onDone() },
            onGo = { onDone() }
        ),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(if (singleLine) minHeight.dp else (minHeight * 2).dp)
                    .clip(shape)
                    .background(Lux.glass)
                    .border(1.dp, Lux.strokeSoft, shape)
                    .padding(horizontal = 16.dp),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (leadingIcon != null) {
                    Box(Modifier.height(minHeight.dp), contentAlignment = Alignment.Center) {
                        LuxIcon(leadingIcon, tint = Lux.textDim, size = 20.dp)
                    }
                }
                Box(
                    Modifier.weight(1f).height(if (singleLine) minHeight.dp else (minHeight * 2).dp),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                ) {
                    Box(Modifier.padding(top = if (singleLine) 0.dp else 16.dp)) {
                        if (value.isEmpty()) Txt(placeholder, sty = Sty.BODY, color = Lux.textFaint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        inner()
                    }
                }
                if (trailing != null) {
                    Box(Modifier.height(minHeight.dp), contentAlignment = Alignment.Center) { trailing() }
                }
            }
        }
    )
}

/** Acma/kapama anahtari: acikken turuncu-amber, kapaliyken koyu. */
@Composable
fun LuxSwitch(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val x by animateDpAsState(if (checked) 24.dp else 3.dp, label = "switch")
    val track: Brush = if (checked) Brush.horizontalGradient(listOf(Lux.orange, Lux.amber)) else SolidColor(Color(0xFF2A2420))
    Box(
        modifier = modifier
            .width(54.dp)
            .height(32.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .background(track)
            .border(1.dp, Lux.strokeSoft, CircleShape)
            .pressable(enabled = enabled) { onChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = x, y = 3.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/** Yan yana sekmeler (secili olan turuncu hap). */
@Composable
fun SegmentedTabs(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(Lux.glass)
            .border(1.dp, Lux.strokeSoft, shape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            Box(
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (on) Brush.horizontalGradient(listOf(Lux.orange, Lux.amber)) else SolidColor(Color.Transparent))
                    .pressable { onSelect(i) },
                contentAlignment = Alignment.Center
            ) {
                Txt(label, sty = Sty.LABEL, color = if (on) Color(0xFF1A0E05) else Lux.textDim, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/**
 * Ayar/profil listesi satiri: yuvarlak ikon, baslik (+ alt baslik), sagda deger/anahtar/ok.
 * onClick verilirse satir tiklanabilir ve sonda ok gosterilir.
 */
@Composable
fun SettingsRow(
    icon: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    var m = modifier.fillMaxWidth()
    if (onClick != null) m = m.pressable(onClick = onClick)
    Row(
        modifier = m.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(Lux.glass).border(1.dp, Lux.strokeSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) { LuxIcon(icon, tint = Lux.goldSoft, size = 20.dp) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Txt(title, sty = Sty.BODY_STRONG, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) Txt(subtitle, sty = Sty.SMALL, color = Lux.textDim, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
        if (trailing != null) trailing()
        if (onClick != null && trailing == null) LuxIcon(Ic.FORWARD, tint = Lux.textFaint, size = 18.dp, autoMirror = true)
    }
}

/** Turuncu izli kaydirici. */
@Composable
fun LuxSlider(
    value: Float,
    onChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    onChangeFinished: (() -> Unit)? = null
) {
    Slider(
        value = value,
        onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = Lux.gold,
            activeTrackColor = Lux.orange,
            inactiveTrackColor = Lux.glassStrong,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        )
    )
}
