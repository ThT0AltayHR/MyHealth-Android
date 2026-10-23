package com.myhealth.app.feature.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Gender
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Istisnai Android hatalari icin gunluk. DOGRULA: CrashLog.note(ctx, tag, e) paketi API ozetinde yok; bu yuzden Log kullaniliyor. */
internal fun onbLog(what: String, e: Throwable) {
    android.util.Log.w("Onboarding", what, e)
}

/** 2. adim: cinsiyet (draft.gender = Gender.name). */
@Composable
internal fun StepGender(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbFrame(
        title = t("ob.gender.title"),
        subtitle = t("ob.gender.sub"),
        stepNo = stepNo,
        onBack = onBack,
        scroll = false,
        bottom = { OnbCta(t("ob.next"), onNext, enabled = draft.gender.isNotEmpty()) }
    ) {
        OnbCheckRow(
            text = t("ob.gender.male"),
            selected = draft.gender == Gender.MALE.name,
            onClick = { draft.gender = Gender.MALE.name }
        )
        VGap(10.dp)
        OnbCheckRow(
            text = t("ob.gender.female"),
            selected = draft.gender == Gender.FEMALE.name,
            onClick = { draft.gender = Gender.FEMALE.name }
        )
    }
}

/** 3. adim: ad (en fazla 40 karakter; bos iken devam kapali). */
@Composable
internal fun StepName(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    val ok = draft.name.trim().isNotEmpty()
    OnbFrame(
        title = t("ob.name.title"),
        subtitle = t("ob.name.sub"),
        stepNo = stepNo,
        onBack = onBack,
        bottom = { OnbCta(t("ob.next"), onNext, enabled = ok) }
    ) {
        LuxTextField(
            value = draft.name,
            onValueChange = { draft.name = it.take(40) },
            placeholder = t("ob.name.hint"),
            onDone = { if (ok) onNext() }
        )
    }
}

/** Tekerlekli secici adimi (yas, boy, kilo): 1..max arasi tamsayi. */
@Composable
internal fun OnbWheelStep(
    title: String,
    subtitle: String,
    unit: String,
    max: Int,
    value: Int,
    stepNo: Int,
    onChange: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val values = remember(max) { (1..max).map { it.toString() } }
    OnbFrame(
        title = title,
        subtitle = subtitle,
        stepNo = stepNo,
        onBack = onBack,
        scroll = false,
        bottom = { OnbCta(t("ob.next"), onNext) }
    ) {
        Txt(t("ob.scroll.hint"), sty = Sty.CAPTION, color = Lux.textDim)
        VGap(8.dp)
        WheelPicker(
            items = values,
            selectedIndex = (value - 1).coerceIn(0, max - 1),
            onSelectedChange = { onChange((it + 1).coerceIn(1, max)) },
            modifier = Modifier.fillMaxWidth()
        )
        VGap(8.dp)
        Txt("$value $unit", sty = Sty.H2, color = Lux.orange)
    }
}

/** 4. adim: yas (1..120). */
@Composable
internal fun StepAge(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbWheelStep(
        title = t("ob.age.title"),
        subtitle = t("ob.age.sub"),
        unit = t("ob.unit.age"),
        max = 120,
        value = draft.age,
        stepNo = stepNo,
        onChange = { draft.age = it },
        onBack = onBack,
        onNext = onNext
    )
}

/** 5. adim: boy (1..400 cm). */
@Composable
internal fun StepHeight(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbWheelStep(
        title = t("ob.height.title"),
        subtitle = t("ob.height.sub"),
        unit = t("ob.unit.cm"),
        max = 400,
        value = draft.heightCm,
        stepNo = stepNo,
        onChange = { draft.heightCm = it },
        onBack = onBack,
        onNext = onNext
    )
}

/** 6. adim: kilo (1..600 kg). */
@Composable
internal fun StepWeight(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbWheelStep(
        title = t("ob.weight.title"),
        subtitle = t("ob.weight.sub"),
        unit = t("ob.unit.kg"),
        max = 600,
        value = draft.weightKg,
        stepNo = stepNo,
        onChange = { draft.weightKg = it },
        onBack = onBack,
        onNext = onNext
    )
}
