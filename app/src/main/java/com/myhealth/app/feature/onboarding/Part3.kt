package com.myhealth.app.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myhealth.app.data.HealthLists
import com.myhealth.app.ui.kit.*
import com.myhealth.app.ui.t
import com.myhealth.app.ui.theme.Lux
import com.myhealth.app.ui.theme.Sty
import com.myhealth.app.ui.theme.Txt

/** Arama kutusu, "Yok" satiri ve secim listesi (hastalik ve alerji adimlari ortak). */
@Composable
internal fun PickStep(
    title: String,
    subtitle: String,
    searchHint: String,
    ids: List<String>,
    keyOf: (String) -> String,
    selected: Set<String>,
    stepNo: Int,
    onChange: (Set<String>) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val q = query.trim()
    val shown = ids.filter { id -> q.isEmpty() || t(keyOf(id)).contains(q, ignoreCase = true) }
    val count = selected.count { it != ONB_NONE }
    OnbFrame(
        title = title,
        subtitle = subtitle,
        stepNo = stepNo,
        onBack = onBack,
        scroll = false,
        bottom = { OnbCta(t("ob.next"), onNext) }
    ) {
        LuxTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = searchHint,
            leadingIcon = Ic.SEARCH
        )
        VGap(10.dp)
        OnbCheckRow(
            text = t("ob.none"),
            selected = ONB_NONE in selected,
            onClick = { onChange(if (ONB_NONE in selected) emptySet() else setOf(ONB_NONE)) }
        )
        VGap(10.dp)
        if (count > 0) {
            Txt(t("ob.selected", count), sty = Sty.CAPTION, color = Lux.textDim)
            VGap(8.dp)
        }
        if (shown.isEmpty()) {
            EmptyState(icon = Ic.SEARCH, title = t("ob.search.empty"))
        } else {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shown, key = { it }) { id ->
                    OnbCheckRow(
                        text = t(keyOf(id)),
                        selected = id in selected,
                        onClick = { onChange(toggleSel(selected, id)) }
                    )
                }
            }
        }
    }
}

/** 7. adim: hastalik secimi (HealthLists.conditions; metinler health.cond.<id>). */
@Composable
internal fun StepConditions(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    PickStep(
        title = t("ob.cond.title"),
        subtitle = t("ob.cond.sub"),
        searchHint = t("ob.cond.search"),
        ids = HealthLists.conditions,
        keyOf = { HealthLists.conditionKey(it) },
        selected = draft.conditions,
        stepNo = stepNo,
        onChange = { draft.conditions = it },
        onBack = onBack,
        onNext = onNext
    )
}

/** 8. adim: alerji secimi (HealthLists.allergies; metinler health.allergy.<id>). */
@Composable
internal fun StepAllergies(draft: OnbDraft, stepNo: Int, onBack: () -> Unit, onNext: () -> Unit) {
    PickStep(
        title = t("ob.allergy.title"),
        subtitle = t("ob.allergy.sub"),
        searchHint = t("ob.allergy.search"),
        ids = HealthLists.allergies,
        keyOf = { HealthLists.allergyKey(it) },
        selected = draft.allergies,
        stepNo = stepNo,
        onChange = { draft.allergies = it },
        onBack = onBack,
        onNext = onNext
    )
}
