package com.myhealth.app.feature.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.OnResume
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

// Izin ekrani: durumlar her on plana gelisteyenle yenilenir (OnResume). Imza SABIT.
// DOGRULA: androidx.activity-compose bagimliligi (rememberLauncherForActivityResult) projede mevcut olmali.
@Composable
fun PermissionsScreen() {
    val nav = LocalNav.current
    val ctx = LocalContext.current
    var version by remember { mutableStateOf(0) }
    OnResume { version++ }
    val list = remember(version) { permItems(ctx) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        version++
    }
    ScreenScaffold(title = t("perm.title"), onBack = { nav.pop() }) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = LocalBottomClearance.current + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "perm_intro") {
                Txt(t("perm.intro"), sty = Sty.SMALL, color = Lux.textDim)
            }
            items(items = list, key = { it.id }) { p ->
                PermCard(
                    item = p,
                    onAllow = {
                        Repo.kvPutBool("perm.asked." + p.id, true)
                        launcher.launch(p.runtime)
                    },
                    onSettings = { openPermSettings(ctx, p) }
                )
            }
        }
    }
}
