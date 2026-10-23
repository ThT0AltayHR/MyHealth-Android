package com.myhealth.app.core.strings

import com.myhealth.app.core.Row

/**
 * Tum ceviri tablolarinin toplandigi yer. Her ozellik KENDI tablo dosyasini doldurur.
 * Ek tablo dosyasi gerekiyorsa yalnizca kendi satirinin sonuna addAll(...) ekleyin.
 * Ayni anahtar iki tabloda tekrarlanamaz (JVM testi bunu denetler).
 */
object AllStrings {
    fun rows(): List<Row> = buildList {
        addAll(StrCommon.rows)
        addAll(StrOnboarding.rows)
        addAll(StrSystem.rows)
        addAll(StrHealth.rows)
        addAll(StrSports.rows)
        addAll(StrMap.rows)
        addAll(StrWeather.rows)
        addAll(StrMind.rows)
        addAll(StrAssistant.rows)
        addAll(StrKnowledge.rows)
        addAll(StrMusic.rows)
        addAll(StrNotes.rows)
        addAll(StrAlarm.rows)
        addAll(StrFasting.rows)
        addAll(StrMeals.rows)
        addAll(StrFoods.rows)
        addAll(StrStats.rows)
        addAll(StrSettings.rows)
        addAll(StrPerms.rows)
        addAll(StrHome.rows)
        addAll(StrAi.rows)
        addAll(StrInsights.rows)
        addAll(StrAiInsights.rows)
        addAll(StrKbNutrition.rows)
        addAll(StrKbFitness.rows)
        addAll(StrKbHealth.rows)
        addAll(StrMusicEngine.rows)
        addAll(StrPipeline.rows)
        addAll(StrSportLive.rows)
        addAll(StrProfile.rows)
        addAll(StrQuiz.rows)
        addAll(StrMeds.rows)
        addAll(StrReport.rows)
    }
}
