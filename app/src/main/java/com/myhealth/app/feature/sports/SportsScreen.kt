package com.myhealth.app.feature.sports

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.Repo
import com.myhealth.app.data.Sport
import com.myhealth.app.data.SportCategory
import com.myhealth.app.data.SportsCatalog
import com.myhealth.app.nav.LocalNav
import com.myhealth.app.nav.Route
import com.myhealth.app.ui.kit.Chip
import com.myhealth.app.ui.kit.EmptyState
import com.myhealth.app.ui.kit.GhostButton
import com.myhealth.app.ui.kit.Ic
import com.myhealth.app.ui.kit.LocalBottomClearance
import com.myhealth.app.ui.kit.LuxTextField
import com.myhealth.app.ui.kit.ScreenScaffold
import com.myhealth.app.ui.kit.SectionTitle
import com.myhealth.app.ui.kit.VGap
import com.myhealth.app.ui.theme.Dim
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt
import com.myhealth.app.ui.t

/** Sporlar sekmesi: arama, kategori cipleri, canli antrenman karti, son yapilanlar ve 57 sporluk katalog. */
@Composable
fun SportsScreen() {
    val nav = LocalNav.current
    val records by Repo.workouts.collectAsState()
    val live by WorkoutSession.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf<SportCategory?>(null) }

    val needle = query.trim().lowercase()
    val shown = SportsCatalog.all.filter { s ->
        (cat == null || s.category == cat) &&
            (needle.isEmpty() || sportDisplayName(s.id).lowercase().contains(needle))
    }
    val groups = shown.groupBy { it.category }
    val recent = recentSportCounts(records)
    // DOGRULA: WorkoutState.sportId alani var mi (ad veya tip farkliysa burayi duzeltin)
    val liveId: String? = live.sportId
    val liveSport: Sport? = if (live.active) liveId?.let { SportsCatalog.byId(it) } else null

    ScreenScaffold(clearBottomNav = true) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dim.pad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Txt(text = t("sports.title"), sty = Sty.H2)
            GhostButton(text = t("sports.history"), onClick = { nav.push(Route.SportHistory) }, icon = Ic.CALENDAR)
        }
        VGap(12.dp)
        LuxTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = t("sports.search"),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dim.pad),
            leadingIcon = Ic.SEARCH
        )
        VGap(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = Dim.pad),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(text = t("sports.all"), selected = cat == null, onClick = { cat = null })
            for (c in SportCategory.values()) {
                Chip(text = t(c.nameKey), selected = cat == c, onClick = { cat = c })
            }
        }
        VGap(12.dp)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(start = Dim.pad, end = Dim.pad, bottom = LocalBottomClearance.current + 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (liveSport != null) {
                item(key = "live") {
                    LiveResumeCard(sport = liveSport, onOpen = { nav.push(Route.SportLive(liveSport.id)) })
                }
            }
            if (needle.isEmpty() && cat == null && recent.isNotEmpty()) {
                item(key = "recent") {
                    RecentSportsStrip(items = recent, onOpen = { id -> nav.push(Route.SportLive(id)) })
                }
            }
            if (shown.isEmpty()) {
                item(key = "none") {
                    EmptyState(
                        icon = Ic.SEARCH,
                        title = t("sports.none"),
                        modifier = Modifier.fillMaxWidth(),
                        subtitle = t("sports.none.sub")
                    )
                }
            }
            for ((c, list) in groups) {
                item(key = "h_" + c.id) { SectionTitle(text = t(c.nameKey)) }
                items(list, key = { it.id }) { s ->
                    CatalogSportRow(sport = s, onOpen = { nav.push(Route.SportLive(s.id)) })
                }
            }
        }
    }
}
