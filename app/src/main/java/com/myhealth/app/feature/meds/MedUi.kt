package com.myhealth.app.feature.meds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.PlaceholderScreen

/**
 * Ilac secici (SABIT SOZLESME): arama kutusu + iki kutu ("duzenli kullandigim" / "ara sira kullandigim");
 * kategori cipleri, ilac satirina dokununca bilgi sayfasi (amac + uyarilar). Ilk acilista (OnboardingFlow)
 * ve profilde kullanilir; degisiklikte onChange cagrilir. Ilac ajani govdeyi doldurur.
 */
@Composable
fun MedicationEditor(
    regular: Set<String>,
    occasional: Set<String>,
    onChange: (Set<String>, Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    // TASLAK
}

/** Tam sayfa ilac ekrani (Route.Medications): MedicationEditor + MedStore + uyarilar. TASLAK: Ilac ajani doldurur. */
@Composable
fun MedicationsScreen() {
    val nav = LocalNav.current
    PlaceholderScreen("Medications", onBack = { nav.pop() })
}
