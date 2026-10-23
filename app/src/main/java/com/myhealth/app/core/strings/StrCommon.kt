package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Gezinme, ortak butonlar, birimler, durum metinleri, gun adlari. */
object StrCommon {
    val rows: List<Row> = listOf(
        // --- alt gezinme ---
        row("nav.home", "Ana Sayfa", "Home", "Главная", "الرئيسية", "Start", "홈"),
        row("nav.sports", "Spor", "Sports", "Спорт", "الرياضة", "Sport", "운동"),
        row("nav.assistant", "Asistan", "Assistant", "Ассистент", "المساعد", "Assistent", "어시스턴트"),
        row("nav.music", "Müzik", "Music", "Музыка", "الموسيقى", "Musik", "음악"),
        row("nav.more", "Daha", "More", "Ещё", "المزيد", "Mehr", "더보기"),

        // --- ortak butonlar ---
        row("common.next", "İleri", "Next", "Далее", "التالي", "Weiter", "다음"),
        row("common.back", "Geri", "Back", "Назад", "رجوع", "Zurück", "뒤로"),
        row("common.skip", "Atla", "Skip", "Пропустить", "تخطي", "Überspringen", "건너뛰기"),
        row("common.save", "Kaydet", "Save", "Сохранить", "حفظ", "Speichern", "저장"),
        row("common.cancel", "İptal", "Cancel", "Отмена", "إلغاء", "Abbrechen", "취소"),
        row("common.done", "Tamam", "Done", "Готово", "تم", "Fertig", "완료"),
        row("common.edit", "Düzenle", "Edit", "Изменить", "تعديل", "Bearbeiten", "편집"),
        row("common.delete", "Sil", "Delete", "Удалить", "حذف", "Löschen", "삭제"),
        row("common.add", "Ekle", "Add", "Добавить", "إضافة", "Hinzufügen", "추가"),
        row("common.close", "Kapat", "Close", "Закрыть", "إغلاق", "Schließen", "닫기"),
        row("common.start", "Başlat", "Start", "Старт", "ابدأ", "Start", "시작"),
        row("common.pause", "Duraklat", "Pause", "Пауза", "إيقاف مؤقت", "Pause", "일시정지"),
        row("common.resume", "Devam Et", "Resume", "Продолжить", "استئناف", "Fortsetzen", "계속"),
        row("common.stop", "Durdur", "Stop", "Стоп", "إيقاف", "Stopp", "정지"),
        row("common.finish", "Bitir", "Finish", "Завершить", "إنهاء", "Beenden", "종료"),
        row("common.search", "Ara", "Search", "Поиск", "بحث", "Suchen", "검색"),
        row("common.on", "Açık", "On", "Вкл.", "مفعّل", "An", "켜짐"),
        row("common.off", "Kapalı", "Off", "Выкл.", "متوقف", "Aus", "꺼짐"),
        row("common.enable", "Etkinleştir", "Enable", "Включить", "تفعيل", "Aktivieren", "활성화"),
        row("common.allow", "İzin Ver", "Allow", "Разрешить", "سماح", "Erlauben", "허용"),
        row("common.later", "Sonra", "Later", "Позже", "لاحقًا", "Später", "나중에"),
        row("common.today", "Bugün", "Today", "Сегодня", "اليوم", "Heute", "오늘"),
        row("common.none", "Hiçbiri", "None", "Нет", "لا شيء", "Keine", "없음"),
        row("common.optional", "İsteğe bağlı", "Optional", "Необязательно", "اختياري", "Optional", "선택 사항"),
        row("common.loading", "Yükleniyor...", "Loading...", "Загрузка...", "جارٍ التحميل...", "Wird geladen...", "불러오는 중..."),
        row("common.retry", "Tekrar Dene", "Try Again", "Повторить", "إعادة المحاولة", "Erneut versuchen", "다시 시도"),
        row("common.settings", "Ayarlar", "Settings", "Настройки", "الإعدادات", "Einstellungen", "설정"),
        row("common.open_settings", "Ayarları Aç", "Open Settings", "Открыть настройки", "فتح الإعدادات", "Einstellungen öffnen", "설정 열기"),
        row("common.yes", "Evet", "Yes", "Да", "نعم", "Ja", "예"),
        row("common.no", "Hayır", "No", "Нет", "لا", "Nein", "아니요"),

        // --- birimler ---
        row("unit.km", "km", "km", "км", "كم", "km", "km"),
        row("unit.m", "m", "m", "м", "م", "m", "m"),
        row("unit.kcal", "kcal", "kcal", "ккал", "سعرة", "kcal", "kcal"),
        row("unit.min", "dk", "min", "мин", "د", "Min", "분"),
        row("unit.h", "sa", "h", "ч", "س", "Std", "시간"),
        row("unit.kg", "kg", "kg", "кг", "كغ", "kg", "kg"),
        row("unit.cm", "cm", "cm", "см", "سم", "cm", "cm"),
        row("unit.bpm", "bpm", "bpm", "уд/мин", "نبضة/د", "bpm", "bpm"),
        row("unit.steps", "adım", "steps", "шагов", "خطوة", "Schritte", "걸음"),
        row("unit.kmh", "km/sa", "km/h", "км/ч", "كم/س", "km/h", "km/h"),
        row("unit.years", "yaş", "years", "лет", "سنة", "Jahre", "세"),
        row("unit.glass", "bardak", "glasses", "стаканов", "كوب", "Gläser", "잔"),

        // --- bağlantı durumu ve dinamik ada ---
        row("net.online", "ÇEVRİMİÇİ", "ONLINE", "В СЕТИ", "متصل", "ONLINE", "온라인"),
        row("net.offline", "ÇEVRİMDIŞI", "OFFLINE", "НЕ В СЕТИ", "غير متصل", "OFFLINE", "오프라인"),
        row("island.hint", "Hava durumu bilgilerini görüntülemek için tıklayınız", "Tap to view weather details", "Нажмите, чтобы посмотреть погоду", "اضغط لعرض معلومات الطقس", "Tippen, um das Wetter anzuzeigen", "날씨 정보를 보려면 누르세요"),

        // --- karşılama ---
        row("greet.hey", "Merhaba, {0}", "Hey, {0}", "Привет, {0}", "مرحبًا، {0}", "Hallo, {0}", "안녕하세요, {0}님"),
        row("greet.sub", "Bugün sana nasıl yardımcı olabilirim?", "How may I help you today?", "Чем могу помочь сегодня?", "كيف يمكنني مساعدتك اليوم؟", "Wie kann ich dir heute helfen?", "오늘 무엇을 도와드릴까요?"),

        // --- gün adları (1 = Pazartesi ... 7 = Pazar) ---
        row("day.s1", "Pzt", "Mon", "Пн", "الاثنين", "Mo", "월"),
        row("day.s2", "Sal", "Tue", "Вт", "الثلاثاء", "Di", "화"),
        row("day.s3", "Çar", "Wed", "Ср", "الأربعاء", "Mi", "수"),
        row("day.s4", "Per", "Thu", "Чт", "الخميس", "Do", "목"),
        row("day.s5", "Cum", "Fri", "Пт", "الجمعة", "Fr", "금"),
        row("day.s6", "Cmt", "Sat", "Сб", "السبت", "Sa", "토"),
        row("day.s7", "Paz", "Sun", "Вс", "الأحد", "So", "일"),
        row("day.l1", "Pazartesi", "Monday", "Понедельник", "الاثنين", "Montag", "월요일"),
        row("day.l2", "Salı", "Tuesday", "Вторник", "الثلاثاء", "Dienstag", "화요일"),
        row("day.l3", "Çarşamba", "Wednesday", "Среда", "الأربعاء", "Mittwoch", "수요일"),
        row("day.l4", "Perşembe", "Thursday", "Четверг", "الخميس", "Donnerstag", "목요일"),
        row("day.l5", "Cuma", "Friday", "Пятница", "الجمعة", "Freitag", "금요일"),
        row("day.l6", "Cumartesi", "Saturday", "Суббота", "السبت", "Samstag", "토요일"),
        row("day.l7", "Pazar", "Sunday", "Воскресенье", "الأحد", "Sonntag", "일요일")
    )
}
