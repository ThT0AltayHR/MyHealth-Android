package com.myhealth.app.data

import com.myhealth.app.core.bool
import com.myhealth.app.core.dbl
import com.myhealth.app.core.int
import com.myhealth.app.core.intList
import com.myhealth.app.core.list
import com.myhealth.app.core.long
import com.myhealth.app.core.str
import com.myhealth.app.core.strList

// ------------------------------------------------------------------ profil
enum class Gender(val id: String) {
    MALE("male"), FEMALE("female");

    companion object {
        fun from(id: String?): Gender = entries.firstOrNull { it.id == id } ?: MALE
    }
}

data class UserProfile(
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val age: Int = 30,
    val heightCm: Int = 175,
    val weightKg: Int = 70,
    val conditions: Set<String> = emptySet(),
    val allergies: Set<String> = emptySet(),
    val onboarded: Boolean = false
) {
    val bmi: Double
        get() {
            val m = heightCm / 100.0
            return if (m <= 0.0) 0.0 else weightKg / (m * m)
        }

    fun toJson(): Map<String, Any?> = mapOf(
        "name" to name, "gender" to gender.id, "age" to age, "heightCm" to heightCm,
        "weightKg" to weightKg, "conditions" to conditions.toList(), "allergies" to allergies.toList(),
        "onboarded" to onboarded
    )

    companion object {
        fun fromJson(m: Map<*, *>): UserProfile {
            val d = UserProfile()
            return UserProfile(
                name = m.str("name"),
                gender = Gender.from(m.str("gender")),
                age = m.int("age", d.age).coerceIn(5, 120),
                heightCm = m.int("heightCm", d.heightCm).coerceIn(50, 400),
                weightKg = m.int("weightKg", d.weightKg).coerceIn(1, 600),
                conditions = m.strList("conditions").toSet(),
                allergies = m.strList("allergies").toSet(),
                onboarded = m.bool("onboarded")
            )
        }
    }
}

// ------------------------------------------------------------------ ayarlar
enum class AiMode(val id: String) {
    LOCAL("local"), GROQ("groq");

    companion object {
        fun from(id: String?): AiMode = entries.firstOrNull { it.id == id } ?: LOCAL
    }
}

data class AppSettings(
    val langCode: String = "",
    val groqKey: String = "",
    val aiMode: AiMode = AiMode.LOCAL,
    val weatherNotif: Boolean = true,
    val weatherEveryMin: Int = 180,
    val activityNotif: Boolean = true,
    val mealNotif: Boolean = true,
    val breakfastMin: Int = 8 * 60 + 30,
    val lunchMin: Int = 13 * 60,
    val dinnerMin: Int = 19 * 60 + 30,
    val fastingAuto: Boolean = true,
    val fastingForce: Boolean = false,
    val sahurAlarm: Boolean = true,
    val sahurOffsetMin: Int = 45,
    val iftarAlarm: Boolean = true,
    val stepGoal: Int = 8000,
    val musicSource: String = "audius",
    /** Yapay zeka: egzersiz sonrasi 10-15 dk icinde destek bildirimi ve gunluk ipucu bildirimi. */
    val aiFollowUp: Boolean = true,
    val aiDailyTip: Boolean = true,
    /** Aile hekimi: acikken asistan belirtiler surerse/agirlasirsa aile hekimine yonlendirmeyi vurgular. */
    val doctorAlways: Boolean = true,
    val doctorName: String = "",
    val doctorPhone: String = ""
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "langCode" to langCode, "groqKey" to groqKey, "aiMode" to aiMode.id,
        "weatherNotif" to weatherNotif, "weatherEveryMin" to weatherEveryMin,
        "activityNotif" to activityNotif, "mealNotif" to mealNotif,
        "breakfastMin" to breakfastMin, "lunchMin" to lunchMin, "dinnerMin" to dinnerMin,
        "fastingAuto" to fastingAuto, "fastingForce" to fastingForce,
        "sahurAlarm" to sahurAlarm, "sahurOffsetMin" to sahurOffsetMin, "iftarAlarm" to iftarAlarm,
        "stepGoal" to stepGoal, "musicSource" to musicSource,
        "aiFollowUp" to aiFollowUp, "aiDailyTip" to aiDailyTip, "doctorAlways" to doctorAlways,
        "doctorName" to doctorName, "doctorPhone" to doctorPhone
    )

    companion object {
        fun fromJson(m: Map<*, *>): AppSettings {
            val d = AppSettings()
            return AppSettings(
                langCode = m.str("langCode"),
                groqKey = m.str("groqKey"),
                aiMode = AiMode.from(m.str("aiMode")),
                weatherNotif = m.bool("weatherNotif", d.weatherNotif),
                weatherEveryMin = m.int("weatherEveryMin", d.weatherEveryMin).coerceIn(15, 720),
                activityNotif = m.bool("activityNotif", d.activityNotif),
                mealNotif = m.bool("mealNotif", d.mealNotif),
                breakfastMin = m.int("breakfastMin", d.breakfastMin).coerceIn(0, 1439),
                lunchMin = m.int("lunchMin", d.lunchMin).coerceIn(0, 1439),
                dinnerMin = m.int("dinnerMin", d.dinnerMin).coerceIn(0, 1439),
                fastingAuto = m.bool("fastingAuto", d.fastingAuto),
                fastingForce = m.bool("fastingForce", d.fastingForce),
                sahurAlarm = m.bool("sahurAlarm", d.sahurAlarm),
                sahurOffsetMin = m.int("sahurOffsetMin", d.sahurOffsetMin).coerceIn(0, 180),
                iftarAlarm = m.bool("iftarAlarm", d.iftarAlarm),
                stepGoal = m.int("stepGoal", d.stepGoal).coerceIn(1000, 50000),
                musicSource = m.str("musicSource", d.musicSource),
                aiFollowUp = m.bool("aiFollowUp", d.aiFollowUp),
                aiDailyTip = m.bool("aiDailyTip", d.aiDailyTip),
                doctorAlways = m.bool("doctorAlways", d.doctorAlways),
                doctorName = m.str("doctorName"),
                doctorPhone = m.str("doctorPhone")
            )
        }
    }
}

// ------------------------------------------------------------------ hava durumu
enum class WeatherCondition(val id: String) {
    SUNNY("sunny"), CLOUDY("cloudy"), LIGHT_RAIN("light_rain"),
    HEAVY_RAIN("heavy_rain"), STORM("storm"), SNOW("snow");

    companion object {
        fun from(id: String?): WeatherCondition = entries.firstOrNull { it.id == id } ?: SUNNY
    }
}

data class WeatherNow(
    val city: String,
    val tempC: Int,
    val feelsC: Int,
    val humidity: Int,
    val windKmh: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val updatedMs: Long
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "city" to city, "tempC" to tempC, "feelsC" to feelsC, "humidity" to humidity,
        "windKmh" to windKmh, "condition" to condition.id, "isDay" to isDay, "updatedMs" to updatedMs
    )

    companion object {
        fun fromJson(m: Map<*, *>) = WeatherNow(
            m.str("city"), m.int("tempC"), m.int("feelsC"), m.int("humidity"), m.int("windKmh"),
            WeatherCondition.from(m.str("condition")), m.bool("isDay", true), m.long("updatedMs")
        )
    }
}

/** dayOfWeek: 1 = Pazartesi ... 7 = Pazar (java.time.DayOfWeek.value ile ayni). */
data class DayForecast(
    val dateIso: String,
    val dayOfWeek: Int,
    val minC: Int,
    val maxC: Int,
    val condition: WeatherCondition,
    val rainPct: Int
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "dateIso" to dateIso, "dayOfWeek" to dayOfWeek, "minC" to minC, "maxC" to maxC,
        "condition" to condition.id, "rainPct" to rainPct
    )

    companion object {
        fun fromJson(m: Map<*, *>) = DayForecast(
            m.str("dateIso"), m.int("dayOfWeek", 1), m.int("minC"), m.int("maxC"),
            WeatherCondition.from(m.str("condition")), m.int("rainPct")
        )
    }
}

// ------------------------------------------------------------------ antrenman
data class LatLng(val lat: Double, val lng: Double)

data class WorkoutRecord(
    val id: Long,
    val sportId: String,
    val startMs: Long,
    val durationSec: Int,
    val distanceM: Double,
    val calories: Int,
    val avgBpm: Int,
    val maxBpm: Int,
    val steps: Int = 0,
    val route: List<LatLng> = emptyList()
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "sportId" to sportId, "startMs" to startMs, "durationSec" to durationSec,
        "distanceM" to distanceM, "calories" to calories, "avgBpm" to avgBpm, "maxBpm" to maxBpm,
        "steps" to steps, "route" to route.map { listOf(it.lat, it.lng) }
    )

    companion object {
        fun fromJson(m: Map<*, *>): WorkoutRecord {
            val pts = m.list("route").mapNotNull { p ->
                val a = p as? List<*>
                val la = (a?.getOrNull(0) as? Number)?.toDouble()
                val ln = (a?.getOrNull(1) as? Number)?.toDouble()
                if (la != null && ln != null) LatLng(la, ln) else null
            }
            return WorkoutRecord(
                m.long("id"), m.str("sportId"), m.long("startMs"), m.int("durationSec"),
                m.dbl("distanceM"), m.int("calories"), m.int("avgBpm"), m.int("maxBpm"),
                m.int("steps"), pts
            )
        }
    }
}

// ------------------------------------------------------------------ notlar, alarmlar
data class Note(val id: Long, val title: String, val body: String, val updatedMs: Long) {
    fun toJson(): Map<String, Any?> = mapOf("id" to id, "title" to title, "body" to body, "updatedMs" to updatedMs)

    companion object {
        fun fromJson(m: Map<*, *>) = Note(m.long("id"), m.str("title"), m.str("body"), m.long("updatedMs"))
    }
}

/** days: 1 = Pazartesi ... 7 = Pazar; bos kume = tek seferlik. */
data class AlarmItem(
    val id: Int,
    val minuteOfDay: Int,
    val days: Set<Int>,
    val label: String,
    val enabled: Boolean,
    val vibrate: Boolean = true
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "minuteOfDay" to minuteOfDay, "days" to days.toList(), "label" to label,
        "enabled" to enabled, "vibrate" to vibrate
    )

    companion object {
        fun fromJson(m: Map<*, *>) = AlarmItem(
            m.int("id"), m.int("minuteOfDay").coerceIn(0, 1439),
            m.intList("days").filter { it in 1..7 }.toSet(), m.str("label"),
            m.bool("enabled", true), m.bool("vibrate", true)
        )
    }
}

// ------------------------------------------------------------------ ogun
enum class MealType(val id: String) {
    BREAKFAST("breakfast"), LUNCH("lunch"), DINNER("dinner"), SNACK("snack");

    companion object {
        fun from(id: String?): MealType = entries.firstOrNull { it.id == id } ?: SNACK
    }
}

data class MealEntry(
    val id: Long,
    val dateKey: String,
    val type: MealType,
    val foodId: String,
    val customName: String,
    val servings: Double,
    val kcal: Int,
    val proteinG: Int,
    val carbG: Int,
    val fatG: Int,
    val tsMs: Long
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "dateKey" to dateKey, "type" to type.id, "foodId" to foodId, "customName" to customName,
        "servings" to servings, "kcal" to kcal, "proteinG" to proteinG, "carbG" to carbG, "fatG" to fatG, "tsMs" to tsMs
    )

    companion object {
        fun fromJson(m: Map<*, *>) = MealEntry(
            m.long("id"), m.str("dateKey"), MealType.from(m.str("type")), m.str("foodId"), m.str("customName"),
            m.dbl("servings", 1.0), m.int("kcal"), m.int("proteinG"), m.int("carbG"), m.int("fatG"), m.long("tsMs")
        )
    }
}

// ------------------------------------------------------------------ yapay zeka
data class ChatMessage(val id: Long, val fromUser: Boolean, val text: String, val engine: String, val tsMs: Long) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "fromUser" to fromUser, "text" to text, "engine" to engine, "tsMs" to tsMs
    )

    companion object {
        fun fromJson(m: Map<*, *>) = ChatMessage(
            m.long("id"), m.bool("fromUser"), m.str("text"), m.str("engine"), m.long("tsMs")
        )
    }
}

data class MemoryFact(val text: String, val tsMs: Long) {
    fun toJson(): Map<String, Any?> = mapOf("text" to text, "tsMs" to tsMs)

    companion object {
        fun fromJson(m: Map<*, *>) = MemoryFact(m.str("text"), m.long("tsMs"))
    }
}

// ------------------------------------------------------------------ muzik
enum class TrackSource(val id: String) {
    LOCAL("local"), ONLINE("online");

    companion object {
        fun from(id: String?): TrackSource = entries.firstOrNull { it.id == id } ?: LOCAL
    }
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: String,
    val artUri: String,
    val source: TrackSource
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id, "title" to title, "artist" to artist, "album" to album, "durationMs" to durationMs,
        "uri" to uri, "artUri" to artUri, "source" to source.id
    )

    companion object {
        fun fromJson(m: Map<*, *>) = Track(
            m.str("id"), m.str("title"), m.str("artist"), m.str("album"), m.long("durationMs"),
            m.str("uri"), m.str("artUri"), TrackSource.from(m.str("source"))
        )
    }
}

data class Playlist(val id: Long, val name: String, val tracks: List<Track>) {
    fun toJson(): Map<String, Any?> = mapOf("id" to id, "name" to name, "tracks" to tracks.map { it.toJson() })

    companion object {
        fun fromJson(m: Map<*, *>) = Playlist(
            m.long("id"), m.str("name"),
            m.list("tracks").mapNotNull { (it as? Map<*, *>)?.let { x -> Track.fromJson(x) } }
        )
    }
}
