package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Sistem metinleri: bildirim kanali adlari. */
object StrSystem {
    val rows: List<Row> = listOf(
        row("notif.ch.weather", "Hava durumu", "Weather", "Погода", "الطقس", "Wetter", "날씨"),
        row("notif.ch.activity", "Hareket algılama", "Activity detection", "Определение активности", "اكتشاف النشاط", "Aktivitätserkennung", "활동 감지"),
        row("notif.ch.meal", "Öğün hatırlatıcıları", "Meal reminders", "Напоминания о еде", "تذكيرات الوجبات", "Mahlzeiten-Erinnerungen", "식사 알림"),
        row("notif.ch.alarm", "Alarmlar", "Alarms", "Будильники", "المنبّهات", "Wecker", "알람"),
        row("notif.ch.fasting", "Sahur ve iftar", "Suhoor and iftar", "Сухур и ифтар", "السحور والإفطار", "Sahur und Iftar", "수후르와 이프타르"),
        row("notif.ch.workout", "Antrenman", "Workout", "Тренировка", "التمرين", "Training", "운동"),
        row("notif.ch.music", "Müzik", "Music", "Музыка", "الموسيقى", "Musik", "음악"),
        row("notif.ch.general", "Genel", "General", "Общие", "عام", "Allgemein", "일반"),
        row("notif.ch.ai", "Yapay zekâ önerileri", "AI suggestions", "Подсказки ИИ", "اقتراحات الذكاء الاصطناعي", "KI-Vorschläge", "AI 제안")
    )
}
