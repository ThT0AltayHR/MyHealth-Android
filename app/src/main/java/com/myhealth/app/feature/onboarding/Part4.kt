package com.myhealth.app.feature.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.meds.MedicationEditor
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** 9. adim: ilaclar. Duzenli ve ara sira kutulari MedicationEditor'da; "ilac kullanmiyorum" ikisini de bosaltir. */
@Composable
internal fun StepMeds(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbFrame(
        title = t("ob.meds.title"),
        subtitle = t("ob.meds.sub"),
        stepNo = stepNo,
        onBack = onBack,
        scroll = false,
        bottom = {
            OnbCta(
                text = t("ob.next"),
                onClick = onNext,
                secondaryText = t("ob.meds.none"),
                onSecondary = {
                    draft.medsRegular = emptySet()
                    draft.medsOccasional = emptySet()
                    onNext()
                }
            )
        }
    ) {
        MedicationEditor(
            regular = draft.medsRegular,
            occasional = draft.medsOccasional,
            onChange = { reg, occ ->
                draft.medsRegular = reg
                draft.medsOccasional = occ
            },
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
    }
}

/** 10. adim: aile hekimi (istege bagli). Devam eder ve ayarlara yazar; Atla hicbir seyi kaydetmez. */
@Composable
internal fun StepDoctor(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    val save: () -> Unit = {
        Repo.updateSettings { s ->
            s.copy(doctorName = draft.doctorName.trim(), doctorPhone = draft.doctorPhone.trim())
        }
        onNext()
    }
    OnbFrame(
        title = t("ob.doctor.title"),
        subtitle = t("ob.doctor.sub"),
        stepNo = stepNo,
        onBack = onBack,
        bottom = { OnbCta(t("ob.next"), save, secondaryText = t("ob.skip"), onSecondary = onNext) }
    ) {
        LuxTextField(
            value = draft.doctorName,
            onValueChange = { draft.doctorName = it.take(60) },
            placeholder = t("ob.doctor.name")
        )
        VGap(10.dp)
        LuxTextField(
            value = draft.doctorPhone,
            onValueChange = { draft.doctorPhone = it.take(20) },
            placeholder = t("ob.doctor.phone"),
            keyboardType = KeyboardType.Phone
        )
        VGap(12.dp)
        Txt(t("ob.doctor.note"), sty = Sty.SMALL, color = Lux.textDim)
    }
}
