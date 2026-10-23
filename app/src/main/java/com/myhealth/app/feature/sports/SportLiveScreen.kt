package com.myhealth.app.feature.sports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.feature.health.ExerciseFinishFlow
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.ConfirmDialog
import com.myhealth.app.ui.kit.EmptyState
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.IconBtn
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.SegmentedTabs
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.t
import com.myhealth.app.util.CrashLog
import com.myhealth.app.util.Perms
import kotlinx.coroutines.delay

/** Canli spor ekrani (Route.SportLive). Imza SABIT. Baslat/duraklat/devam/bitir, sure hedefi, harita/3B, motivasyon. */
@Composable
fun SportLiveScreen(sportId: String) {
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val sport = remember(sportId) { SportsCatalog.byId(sportId) }
    val st by WorkoutSession.state.collectAsState()
    val route by WorkoutSession.route.collectAsState()
    val target by WorkoutSession.targetSec.collectAsState()
    val profile by Repo.profile.collectAsState()
    val name = profile.name.trim()
    val live = st.active && st.sportId == sportId
    val otherActive = st.active && !live
    val view = if (live) st else WorkoutState()
    val viewRoute = if (live) route else emptyList()
    val viewTarget = if (live) target else 0

    var mapTab by remember { mutableStateOf(true) }
    var athleteFailed by remember { mutableStateOf(false) }
    var showDuration by remember { mutableStateOf(false) }
    var confirmFinish by remember { mutableStateOf(false) }
    var motivN by remember { mutableStateOf(0) }
    var backup by remember { mutableStateOf<WkBackup?>(null) }
    var finished by remember { mutableStateOf<WorkoutRecord?>(null) }

    LaunchedEffect(sportId) {
        backup = WorkoutSession.pendingBackup()?.takeIf { it.sportId == sportId }
    }

    // Motivasyon: canli ve duraklatilmamis iken ilk mesaj 90 sn sonra, sonra her 150 sn'de bir.
    LaunchedEffect(live, st.paused) {
        if (!live || st.paused) return@LaunchedEffect
        delay(90_000L)
        while (true) {
            motivN = WkMotivation.next(motivN, name.isNotEmpty())
            delay(150_000L)
        }
    }

    fun startSession(sec: Int) {
        backup = null
        if (WorkoutSession.start(ctx, sportId, sec)) {
            WorkoutService.start(ctx)
        }
    }

    fun finishSession() {
        val rec = WorkoutSession.finish()
        WorkoutService.stop(ctx)
        if (rec == null) {
            nav.pop()
            return
        }
        // Kayit, akis gosterilmeden ONCE yazilir (cokme/cikis kaybi olmasin).
        try {
            Repo.addWorkout(rec)
        } catch (e: Exception) {
            CrashLog.note(ctx, "sportlive_save", e)
        }
        finished = rec
    }

    val done = finished
    if (done != null) {
        ExerciseFinishFlow(
            exerciseId = sportId,
            durationSec = done.durationSec,
            summaryLines = sportLiveSummary(done),
            onDone = { nav.pop() },
            closingMessage = t("sportlive.closing")
        )
        return
    }

    val title = if (sport != null) t(sport.nameKey) else t("sportlive.title")
    ScreenScaffold(
        title = title,
        onBack = { nav.pop() },
        trailing = {
            if (live) {
                IconBtn(icon = Ic.MINIMIZE, onClick = { nav.pop() }, description = t("sportlive.minimize"))
            }
        }
    ) {
        if (sport == null) {
            EmptyState(icon = Ic.WARNING, title = t("sportlive.unknown"))
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (otherActive) {
                    SportLiveNotice(
                        text = t("sportlive.other"),
                        actionText = t("sportlive.back"),
                        onAction = { nav.pop() }
                    )
                    VGap(12.dp)
                }
                val b = backup
                if (b != null && !st.active) {
                    SportLiveBackupCard(
                        b = b,
                        onRestore = {
                            if (WorkoutSession.restore(ctx)) {
                                backup = null
                                WorkoutService.start(ctx)
                            }
                        },
                        onDiscard = {
                            WorkoutSession.discardBackup()
                            backup = null
                        }
                    )
                    VGap(12.dp)
                }
                if (sport.gps && !otherActive && !Perms.hasLocation(ctx)) {
                    SportLiveNotice(
                        text = t("sportlive.noloc"),
                        actionText = t("sportlive.grant"),
                        onAction = { nav.push(Route.Permissions) }
                    )
                    VGap(12.dp)
                }
                if (sport.gps) {
                    SegmentedTabs(
                        options = listOf(t("sportlive.tab.map"), t("sportlive.tab.3d")),
                        selected = if (mapTab) 0 else 1,
                        onSelect = { mapTab = it == 0 },
                        modifier = Modifier.fillMaxWidth()
                    )
                    VGap(12.dp)
                }
                SportLiveVisual(
                    sport = sport,
                    st = view,
                    route = viewRoute,
                    showMap = sport.gps && mapTab,
                    athleteFailed = athleteFailed,
                    onAthleteUnavailable = { athleteFailed = true }
                )
                VGap(16.dp)
                SportLiveStats(st = view, target = viewTarget, showDist = sport.gps)
                VGap(14.dp)
                if (live && motivN > 0) {
                    SportLiveMotiv(n = motivN, name = name)
                    VGap(12.dp)
                }
                SportLiveWeather()
                VGap(16.dp)
                if (!otherActive) {
                    SportLiveControls(
                        st = view,
                        onStart = { showDuration = true },
                        onPause = { WorkoutSession.pause() },
                        onResume = { WorkoutSession.resume() },
                        onFinish = { confirmFinish = true },
                        onTarget = { showDuration = true }
                    )
                }
                VGap(24.dp)
            }
        }
    }

    if (showDuration) {
        SportLiveDurationDialog(
            current = viewTarget,
            onDismiss = { showDuration = false },
            onConfirm = { sec ->
                showDuration = false
                if (live) WorkoutSession.setTarget(sec) else startSession(sec)
            }
        )
    }
    if (confirmFinish) {
        ConfirmDialog(
            title = t("sportlive.finish.title"),
            message = t("sportlive.finish.msg"),
            confirmText = t("sportlive.finish.yes"),
            onConfirm = {
                confirmFinish = false
                finishSession()
            },
            onDismiss = { confirmFinish = false }
        )
    }
}
