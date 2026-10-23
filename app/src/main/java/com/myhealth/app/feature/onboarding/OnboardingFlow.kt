package com.myhealth.app.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

private const val ONB_LAST_STEP = 18

/** Ilk acilis akisi. Adimlar: 0 dil, 1 karsilama (numarasiz), 2..10 bilgiler, 11..16 izinler, 17 Groq, 18 bitis. */
@Composable
fun OnboardingFlow() {
    val draft = rememberSaveable(saver = OnbDraftSaver) { OnbDraft() }
    var step by rememberSaveable { mutableStateOf(0) }
    val next: () -> Unit = { step = minOf(step + 1, ONB_LAST_STEP) }
    val back: () -> Unit = { step = maxOf(step - 1, 0) }
    BackHandler(enabled = step > 0, onBack = back)
    when (step) {
        0 -> StepLanguage(onNext = next)
        1 -> StepWelcome(onBack = back, onNext = next)
        2 -> StepGender(draft, 2, back, next)
        3 -> StepName(draft, 3, back, next)
        4 -> StepAge(draft, 4, back, next)
        5 -> StepHeight(draft, 5, back, next)
        6 -> StepWeight(draft, 6, back, next)
        7 -> StepConditions(draft, 7, back, next)
        8 -> StepAllergies(draft, 8, back, next)
        9 -> StepMeds(draft, 9, back, next)
        10 -> StepDoctor(draft, 10, back, next)
        11, 12, 13, 14, 15, 16 -> StepPermissions(step - 11, step, back, next)
        17 -> StepGroq(draft, 17, back, next)
        else -> StepReady(draft, back) { saveOnboarding(draft) }
    }
}
