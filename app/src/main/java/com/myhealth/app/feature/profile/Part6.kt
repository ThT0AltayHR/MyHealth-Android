package com.myhealth.app.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myhealth.app.core.Fmt
import com.myhealth.app.data.UserProfile
import com.myhealth.app.logic.Body
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LuxTextField
import com.myhealth.app.ui.kit.PrimaryButton
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Profil ustu: avatar (dokununca foto secilir), ad, yas, VKI ve boy/kilo ozeti. Bos veride cokmez. */
@Composable
internal fun ProfileHeader(p: UserProfile, onPhoto: () -> Unit) {
    val fallbackName = t("profile.title")
    val name = p.name.trim().ifEmpty { fallbackName }
    val bmi = p.bmi
    val bmiOk = bmi.isFinite() && bmi > 0.0
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Avatar(size = 104.dp, onClick = onPhoto)
        VGap(6.dp)
        Txt(t("profile.photo.hint"), sty = Sty.CAPTION, color = Lux.textFaint, align = TextAlign.Center)
        VGap(12.dp)
        Txt(name, sty = Sty.H3, color = Lux.text, align = TextAlign.Center)
        Txt(t("profile.age_fmt", p.age.toString()), sty = Sty.CAPTION, color = Lux.textDim)
        VGap(14.dp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BigValue(if (bmiOk) Fmt.oneDecimal(bmi) else "-")
                Txt(t("profile.bmi_label"), sty = Sty.CAPTION, color = Lux.textDim)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BigValue(p.weightKg.toString(), t("profile.u.kg"))
                Txt(t("profile.edit.weight"), sty = Sty.CAPTION, color = Lux.textDim)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BigValue(p.heightCm.toString(), t("profile.u.cm"))
                Txt(t("profile.edit.height"), sty = Sty.CAPTION, color = Lux.textDim)
            }
        }
        if (bmiOk) {
            VGap(8.dp)
            Txt(t(bmiKey(Body.bmiClass(bmi))), sty = Sty.SMALL, color = Lux.gold, align = TextAlign.Center)
        }
    }
}

/** Aile hekimi karti (PanelCard kabugu): ad ve telefon, "Ara" (ACTION_DIAL ile arama ekranini acar), duzenleme. */
@Composable
internal fun FamilyDoctorCard(name: String, phone: String, onSave: (String, String) -> Unit) {
    val ctx = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var draftName by remember(name) { mutableStateOf(name) }
    var draftPhone by remember(phone) { mutableStateOf(phone) }
    PanelCard(icon = Ic.MEDICAL, titleKey = "profile.doc.title") {
        if (editing) {
            FieldBlock(label = t("profile.doc.name")) {
                LuxTextField(
                    value = draftName,
                    onValueChange = { draftName = it.take(60) },
                    placeholder = t("profile.doc.name"),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VGap(10.dp)
            FieldBlock(label = t("profile.doc.phone")) {
                LuxTextField(
                    value = draftPhone,
                    onValueChange = { draftPhone = it.filter { c -> c.isDigit() || c == '+' || c == ' ' }.take(20) },
                    placeholder = t("profile.doc.phone"),
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VGap(12.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GhostButton(
                    text = t("profile.edit.cancel"),
                    onClick = {
                        editing = false
                        draftName = name
                        draftPhone = phone
                    },
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = t("profile.edit.save"),
                    onClick = {
                        onSave(draftName.trim().take(60), draftPhone.trim())
                        editing = false
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            if (name.isBlank() && phone.isBlank()) {
                Txt(t("profile.doc.none"), sty = Sty.CAPTION, color = Lux.textFaint)
            } else {
                if (name.isNotBlank()) Txt(name, sty = Sty.BODY_STRONG, color = Lux.text)
                if (phone.isNotBlank()) Txt(phone, sty = Sty.CAPTION, color = Lux.textDim)
            }
            VGap(8.dp)
            Txt(t("profile.doc.hint"), sty = Sty.CAPTION, color = Lux.textFaint)
            VGap(12.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GhostButton(
                    text = t("profile.doc.edit"),
                    onClick = { editing = true },
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = t("profile.doc.call"),
                    onClick = { dialNumber(ctx, phone) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
