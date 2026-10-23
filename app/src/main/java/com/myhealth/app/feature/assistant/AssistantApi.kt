package com.myhealth.app.feature.assistant

import com.myhealth.app.core.Lang
import com.myhealth.app.data.MemoryFact
import com.myhealth.app.data.UserProfile
import com.myhealth.app.data.WeatherNow
import com.myhealth.app.data.WorkoutRecord
import com.myhealth.app.data.WorkoutState
import com.myhealth.app.feature.health.HealthEntry

/*
 * ASISTAN MODULU ARAYUZLERI (SABIT SOZLESME).
 *
 * Beyin ajani LocalEngine.kt ve TopicGuard.kt dosyalarini yazar; arayuz ajani AssistantContext'i
 * olusturur, Groq istemcisini, bellegi, modu ve ekrani yazar. Bu dosyadaki veri sinifi imzalari
 * degistirilemez. Hicbir fonksiyon istisna firlatmaz.
 */

/** Asistanin bildigi anlik bilgiler (arayuz ajani Repo/WorkoutSession'dan doldurur). */
data class AssistantContext(
    val lang: Lang,
    val profile: UserProfile,
    val nowMs: Long,
    /** En yeni 10 antrenman (yeniden eskiye). */
    val recentWorkouts: List<WorkoutRecord>,
    /** Anlik antrenman; live.active=false ise yok sayilir. */
    val live: WorkoutState,
    /** Anlik sporun yerellestirilmis adi (live.active degilse bos). */
    val liveSportName: String,
    val weather: WeatherNow?,
    val memory: List<MemoryFact>,
    val todayKcal: Int,
    val todaySteps: Int,
    val stepGoal: Int,
    /** Bugun icilen bardak sayisi. */
    val waterGlasses: Int,
    /** Son 60 gunun egzersiz sonu saglik cevaplari (yeniden eskiye). */
    val healthEntries: List<HealthEntry>,
    /** Duzenli / ara sira kullanilan ilaclarin MedDb kimlikleri. */
    val medsRegular: List<String>,
    val medsOccasional: List<String>,
    val doctorName: String,
    val doctorPhone: String,
    /** Cihaz cevrimici mi (internet aramasi yapilabilir mi). */
    val online: Boolean,
    /** Oruc bilgisi (FastingPlan.today): Ramazan mi, bugun oruc tutuluyor mu, imsak/iftar (gece yarisindan dakika). */
    val ramadan: Boolean = false,
    val fastingToday: Boolean = false,
    val imsakMin: Int = 0,
    val iftarMin: Int = 0
)

/**
 * text: kullaniciya gosterilecek yanit (uygulama dilinde).
 * onTopic=false: konu disi; text zaten zorunlu "uzgunum, sadece ..." mesajidir.
 * learned: kullanici "sunu hatirla ..." dediyse bellege eklenecek kisa olgu (yoksa null).
 */
data class LocalReply(val text: String, val onTopic: Boolean = true, val learned: String? = null)
