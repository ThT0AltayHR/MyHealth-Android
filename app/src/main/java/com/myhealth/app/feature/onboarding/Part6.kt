package com.myhealth.app.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.AiMode
import com.myhealth.app.data.Gender
import com.myhealth.app.data.Repo
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** 17. adim: istege bagli Groq anahtari. Anahtar taslakta kalir (kayit durumuna yazilmaz); bitiste ayarlara gider. */
@Composable
internal fun StepGroq(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    OnbFrame(
        title = t("ob.ai.title"),
        subtitle = t("ob.ai.sub"),
        stepNo = stepNo,
        onBack = onBack,
        bottom = {
            OnbCta(
                text = t("ob.next"),
                onClick = onNext,
                secondaryText = t("ob.ai.skip"),
                onSecondary = {
                    draft.groqKey = ""
                    onNext()
                }
            )
        }
    ) {
        LuxTextField(
            value = draft.groqKey,
            onValueChange = { draft.groqKey = it.trim() },
            placeholder = t("ob.ai.key"),
            password = true
        )
        VGap(12.dp)
        Txt(t("ob.groq.free"), sty = Sty.SMALL, color = Lux.textDim)
    }
}

/** Son adim: ozet. CTA, saveOnboarding'i (onFinish) cagirarak profili ve bayragi yazar. */
@Composable
internal fun StepReady(draft: OnbDraft, onBack: () -> Unit, onFinish: () -> Unit) {
    OnbFrame(
        title = t("ob.ready.title", draft.name.trim()),
        subtitle = t("ob.ready.sub"),
        stepNo = ONB_TOTAL,
        onBack = onBack,
        showProgress = false,
        bottom = { OnbCta(t("ob.ready.cta"), onFinish) }
    ) {
        LuxIcon(
            path = Ic.CHECK,
            size = 76.dp,
            tint = Lux.orange,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Bitis: profil (ad, cinsiyet, yas, boy, kilo, hastaliklar, alerjiler) ve onboarding tamam bayragi,
 * ardindan Groq anahtari ve yapay zeka modu ayarlara yazilir.
 */
internal fun saveOnboarding(draft: OnbDraft) {
    val gender = Gender.values().firstOrNull { it.name == draft.gender } ?: Gender.MALE
    val conditions = draft.conditions - ONB_NONE
    val allergies = draft.allergies - ONB_NONE
    Repo.updateProfile { p ->
        p.copy(
            name = draft.name.trim(),
            gender = gender,
            age = draft.age,
            heightCm = draft.heightCm,
            weightKg = draft.weightKg,
            conditions = conditions,
            allergies = allergies,
            onboarded = true // DOGRULA: bayrak UserProfile.onboarded alani; Repo/Models bu alani okuyor mu API ozetinden dogrulanamadi.
        )
    }
    val key = draft.groqKey.trim()
    Repo.updateSettings { s ->
        s.copy(groqKey = key, aiMode = if (key.isNotEmpty()) AiMode.GROQ else AiMode.LOCAL)
    }
}
