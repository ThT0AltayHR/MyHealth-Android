package com.myhealth.app.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.feature.report.ReportExportCard
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.Toasts
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.tr
import com.myhealth.app.util.CrashLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Profil ekrani (Route.Profile): avatar, kisisel bilgiler (duzenleme), aile hekimi, 24 saglik paneli ve rapor karti.
 * Imza sabit. Bos/eksik veride cokmez; Android cagrilari try/catch icindedir.
 */
@Composable
fun ProfileScreen() {
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    ObserveHealthData()
    val profile by Repo.profile.collectAsState()
    val settings by Repo.settings.collectAsState()
    var draft by remember { mutableStateOf<ProfileDraft?>(null) }
    var meds by remember {
        mutableStateOf(runCatching { medSets() }.getOrDefault(emptySet<String>() to emptySet<String>()))
    }
    // DOGRULA: rememberLauncherForActivityResult activity-compose ile gelir (setContent icin zaten gerekli); yeni bagimlilik degil
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val ok = withContext(Dispatchers.IO) { ProfilePhoto.saveFrom(ctx.applicationContext, uri) }
                    Toasts.show(tr(if (ok) "profile.photo.saved" else "profile.photo.err"))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CrashLog.note(ctx, "profile_photo_pick", e)
                }
            }
        }
    }

    fun pickPhoto() {
        try {
            // DOGRULA: PickVisualMedia.ImageOnly (activity-result 1.6+ API); desteklenmezse istisna try/catch ile yutulur
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (e: Exception) {
            CrashLog.note(ctx, "profile_pick", e)
        }
    }

    fun saveMeds(regular: Set<String>, occasional: Set<String>) {
        meds = regular to occasional
        profileIo(scope, ctx, "profile_meds") { saveMedSets(ctx, regular, occasional) }
    }

    fun saveDoctor(name: String, phone: String) {
        profileIo(scope, ctx, "doctor_save") {
            Repo.updateSettings { s -> s.copy(doctorName = name, doctorPhone = phone) }
        }
        Toasts.show(tr("profile.edit.saved"))
    }

    fun saveDraft(d: ProfileDraft) {
        if (applyDraft(profile, d) == null) {
            Toasts.show(tr("profile.edit.invalid"))
            return
        }
        profileIo(scope, ctx, "profile_save") {
            Repo.updateProfile { cur -> applyDraft(cur, d) ?: cur }
        }
        draft = null
        Toasts.show(tr("profile.edit.saved"))
    }

    ScreenScaffold(title = t("profile.title"), onBack = { nav.pop() }) {
        val p = profile
        val d = draft
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = LocalBottomClearance.current + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                ProfileHeader(p = p, onPhoto = { pickPhoto() })
            }
            item(key = "personal_head") {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Txt(t("profile.section.personal"), modifier = Modifier.weight(1f), sty = Sty.H3, color = Lux.text)
                    if (d == null) {
                        GhostButton(text = t("profile.edit.open"), onClick = { draft = draftOf(p) })
                    }
                }
            }
            if (d != null) {
                PersonalEditItems(
                    d = d,
                    valid = applyDraft(p, d) != null,
                    meds = meds,
                    onDraft = { draft = it },
                    onMeds = { r, o -> saveMeds(r, o) },
                    onSave = { saveDraft(d) },
                    onCancel = { draft = null }
                )
            }
            item(key = "doctor") {
                FamilyDoctorCard(
                    name = settings.doctorName,
                    phone = settings.doctorPhone,
                    onSave = { n, ph -> saveDoctor(n, ph) }
                )
            }
            ProfilePanelItems(p)
            item(key = "report") {
                ReportExportCard(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
