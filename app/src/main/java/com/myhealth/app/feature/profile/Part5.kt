package com.myhealth.app.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Gender
import com.myhealth.app.data.HealthLists
import com.myhealth.app.data.UserProfile
import com.myhealth.app.feature.meds.MedicationEditor
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxSwitch
import com.myhealth.app.ui.kit.LuxTextField
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.SettingsRow
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Kisisel bilgi duzenleme taslagi: sayilar metin olarak tutulur; kaydetmeden once applyDraft ile dogrulanir. */
internal data class ProfileDraft(
    val name: String,
    val age: String,
    val heightCm: String,
    val weightKg: String,
    val gender: Gender,
    val conditions: Set<String>,
    val allergies: Set<String>
)

internal fun draftOf(p: UserProfile): ProfileDraft = ProfileDraft(
    name = p.name,
    age = p.age.toString(),
    heightCm = p.heightCm.toString(),
    weightKg = p.weightKg.toString(),
    gender = p.gender,
    conditions = p.conditions,
    allergies = p.allergies
)

/** Taslagi profile uygular. Ad bos veya sayilar sinirlarin disindaysa null: yas 1-120, boy 1-400 cm, kilo 1-600 kg. */
internal fun applyDraft(p: UserProfile, d: ProfileDraft): UserProfile? {
    val name = d.name.trim().take(40)
    val age = d.age.trim().toIntOrNull() ?: return null
    val height = d.heightCm.trim().toIntOrNull() ?: return null
    val weight = d.weightKg.trim().toIntOrNull() ?: return null
    if (name.isEmpty() || age !in 1..120 || height !in 1..400 || weight !in 1..600) return null
    return p.copy(
        name = name,
        age = age,
        heightCm = height,
        weightKg = weight,
        gender = d.gender,
        conditions = d.conditions,
        allergies = d.allergies
    )
}

/** Alan etiketi ve icerik blogu. */
@Composable
internal fun FieldBlock(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Txt(label, sty = Sty.LABEL, color = Lux.textDim)
        VGap(6.dp)
        content()
    }
}

/** Kaydet ve vazgec dugmeleri; gecersiz taslakta uyari metni gosterir. */
@Composable
internal fun EditButtons(valid: Boolean, onSave: () -> Unit, onCancel: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!valid) {
            Txt(t("profile.edit.invalid"), sty = Sty.CAPTION, color = Lux.red)
            VGap(8.dp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GhostButton(text = t("profile.edit.cancel"), onClick = onCancel, modifier = Modifier.weight(1f))
            PrimaryButton(
                text = t("profile.edit.save"),
                onClick = onSave,
                enabled = valid,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Kisisel bilgi duzenleme ogeleri (LazyColumn icinde). Kaydetme ve vazgecme ProfileScreen'de tanimlanir. */
internal fun LazyListScope.PersonalEditItems(
    d: ProfileDraft,
    valid: Boolean,
    meds: Pair<Set<String>, Set<String>>,
    onDraft: (ProfileDraft) -> Unit,
    onMeds: (Set<String>, Set<String>) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    item(key = "edit_name") {
        FieldBlock(label = t("profile.edit.name")) {
            LuxTextField(
                value = d.name,
                onValueChange = { onDraft(d.copy(name = it.take(40))) },
                placeholder = t("profile.edit.name"),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    item(key = "edit_age") {
        FieldBlock(label = t("profile.edit.age")) {
            LuxTextField(
                value = d.age,
                onValueChange = { onDraft(d.copy(age = it.filter { c -> c.isDigit() }.take(3))) },
                placeholder = t("profile.edit.age"),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    item(key = "edit_height") {
        FieldBlock(label = t("profile.edit.height")) {
            LuxTextField(
                value = d.heightCm,
                onValueChange = { onDraft(d.copy(heightCm = it.filter { c -> c.isDigit() }.take(3))) },
                placeholder = t("profile.edit.height"),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    item(key = "edit_weight") {
        FieldBlock(label = t("profile.edit.weight")) {
            LuxTextField(
                value = d.weightKg,
                onValueChange = { onDraft(d.copy(weightKg = it.filter { c -> c.isDigit() }.take(3))) },
                placeholder = t("profile.edit.weight"),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    item(key = "edit_gender") {
        FieldBlock(label = t("profile.edit.gender")) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(
                    text = t("profile.gender.male"),
                    selected = d.gender == Gender.MALE,
                    onClick = { onDraft(d.copy(gender = Gender.MALE)) },
                    modifier = Modifier.weight(1f)
                )
                Chip(
                    text = t("profile.gender.female"),
                    selected = d.gender == Gender.FEMALE,
                    onClick = { onDraft(d.copy(gender = Gender.FEMALE)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    item(key = "edit_buttons_top") {
        EditButtons(valid = valid, onSave = onSave, onCancel = onCancel)
    }
    item(key = "edit_cond_head") {
        Txt(t("profile.edit.conditions"), sty = Sty.LABEL, color = Lux.textDim)
    }
    items(HealthLists.conditions, key = { "edit_cond_$it" }) { id ->
        val on = id in d.conditions
        SettingsRow(
            icon = Ic.MEDICAL,
            title = t(HealthLists.conditionKey(id)),
            onClick = { onDraft(d.copy(conditions = if (on) d.conditions - id else d.conditions + id)) },
            trailing = {
                LuxSwitch(
                    checked = on,
                    onChange = { v -> onDraft(d.copy(conditions = if (v) d.conditions + id else d.conditions - id)) }
                )
            }
        )
    }
    item(key = "edit_allergy_head") {
        Txt(t("profile.edit.allergies"), sty = Sty.LABEL, color = Lux.textDim)
    }
    items(HealthLists.allergies, key = { "edit_allergy_$it" }) { id ->
        val on = id in d.allergies
        SettingsRow(
            icon = Ic.SHIELD,
            title = t(HealthLists.allergyKey(id)),
            onClick = { onDraft(d.copy(allergies = if (on) d.allergies - id else d.allergies + id)) },
            trailing = {
                LuxSwitch(
                    checked = on,
                    onChange = { v -> onDraft(d.copy(allergies = if (v) d.allergies + id else d.allergies - id)) }
                )
            }
        )
    }
    item(key = "edit_meds") {
        FieldBlock(label = t("profile.edit.meds")) {
            Txt(t("profile.edit.meds_hint"), sty = Sty.CAPTION, color = Lux.textFaint)
            VGap(8.dp)
            // Sabit yukseklik: ilac secicinin ic kaydiricisi sinirli kalsin (sinirsiz yukseklik LazyColumn icinde cokertir).
            MedicationEditor(
                regular = meds.first,
                occasional = meds.second,
                onChange = onMeds,
                modifier = Modifier.fillMaxWidth().height(460.dp)
            )
        }
    }
    item(key = "edit_buttons_bottom") {
        EditButtons(valid = valid, onSave = onSave, onCancel = onCancel)
    }
}
