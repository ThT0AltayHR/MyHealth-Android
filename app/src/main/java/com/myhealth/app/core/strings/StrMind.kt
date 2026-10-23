package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Nefes ekrani metinleri (mind.*). */
object StrMind {
    val rows: List<Row> = listOf(
        row("mind.breath.title",
            "Nefes", "Breathing", "Дыхание", "التنفس", "Atmung", "호흡"),
        row("mind.breath.done",
            "Mükemmel, aynen böyle devam et, yarın seni yine bekliyorum",
            "Excellent, keep it up just like this, I will be waiting for you again tomorrow",
            "Отлично, продолжай в том же духе, завтра я снова тебя жду",
            "ممتاز، واصل هكذا تمامًا، سأكون بانتظارك مرة أخرى غدًا",
            "Hervorragend, mach genau so weiter, morgen erwarte ich dich wieder",
            "훌륭해요, 이대로 계속하세요. 내일 또 기다릴게요"),
        row("mind.breath.speed.slow",
            "Yavaş", "Slow", "Медленно", "بطيء", "Langsam", "느리게"),
        row("mind.breath.speed.mid",
            "Orta", "Medium", "Средне", "متوسط", "Mittel", "보통"),
        row("mind.breath.speed.fast",
            "Hızlı", "Fast", "Быстро", "سريع", "Schnell", "빠르게"),
        row("mind.breath.min",
            "{0} dk", "{0} min", "{0} мин", "{0} د", "{0} Min.", "{0}분"),
        row("mind.breath.in",
            "nefes al", "breathe in", "вдох", "شهيق", "einatmen", "들이마시기"),
        row("mind.breath.out",
            "nefes ver", "breathe out", "выдох", "زفير", "ausatmen", "내쉬기"),
        row("mind.breath.ready",
            "Hazır olduğunda başla", "Start when you are ready", "Начните, когда будете готовы", "ابدأ عندما تكون جاهزًا", "Starte, wenn du bereit bist", "준비되면 시작하세요"),
        row("mind.breath.hint",
            "Kalbin ritmine eşlik et: kalp büyürken nefes al, küçülürken nefes ver.",
            "Follow your heart's rhythm: breathe in as the heart grows, breathe out as it shrinks.",
            "Следуй ритму сердца: вдох, когда сердце растёт, выдох, когда оно уменьшается.",
            "تابع إيقاع قلبك: استنشق حين يكبر القلب، وازفر حين يصغر.",
            "Folge dem Rhythmus deines Herzens: Einatmen, wenn das Herz wächst, Ausatmen, wenn es kleiner wird.",
            "심장의 리듬에 맞추세요. 하트가 커질 때 들이마시고, 작아질 때 내쉬세요."),
        row("mind.breath.start",
            "Başla", "Start", "Начать", "ابدأ", "Starten", "시작"),
        row("mind.breath.stop",
            "Durdur", "Stop", "Стоп", "إيقاف", "Stoppen", "멈추기"),
        row("mind.breath.sum.time",
            "Süre", "Duration", "Длительность", "المدة", "Dauer", "시간"),
        row("mind.breath.sum.speed",
            "Hız", "Pace", "Темп", "الإيقاع", "Tempo", "속도")
    )
}
