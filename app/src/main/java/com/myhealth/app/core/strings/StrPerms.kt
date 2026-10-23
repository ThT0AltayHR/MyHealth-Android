package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Izin ekranlari (perm.*). */
object StrPerms {
    val rows: List<Row> = listOf(
        row("perm.title", "İzinler", "Permissions", "Разрешения", "الأذونات", "Berechtigungen", "권한"),
        row("perm.intro", "Uygulamanın özellikleri için gereken izinler aşağıdadır. Verdiğin izinleri istediğin zaman geri alabilirsin.", "Below are the permissions the app's features need. You can revoke any permission at any time.", "Ниже перечислены разрешения, необходимые функциям приложения. Любое разрешение можно отозвать в любое время.", "فيما يلي الأذونات التي تحتاجها ميزات التطبيق. يمكنك سحب أي إذن في أي وقت.", "Unten stehen die Berechtigungen, die die Funktionen der App benötigen. Du kannst jede Berechtigung jederzeit widerrufen.", "아래는 앱 기능에 필요한 권한입니다. 권한은 언제든지 취소할 수 있습니다."),
        row("perm.allow", "İzin ver", "Allow", "Разрешить", "سماح", "Erlauben", "허용"),
        row("perm.open", "Ayarlara git", "Open settings", "Открыть настройки", "فتح الإعدادات", "Einstellungen öffnen", "설정 열기"),
        row("perm.open.fail", "Ayar ekranı açılamadı.", "Could not open the settings screen.", "Не удалось открыть настройки.", "تعذّر فتح شاشة الإعدادات.", "Der Einstellungsbildschirm konnte nicht geöffnet werden.", "설정 화면을 열 수 없습니다."),
        row("perm.state.granted", "Verildi", "Granted", "Разрешено", "ممنوح", "Erteilt", "허용됨"),
        row("perm.state.denied", "Verilmedi", "Denied", "Отклонено", "مرفوض", "Verweigert", "거부됨"),
        row("perm.state.check", "Ayarlardan kontrol et", "Check in settings", "Проверьте в настройках", "تحقّق من الإعدادات", "In den Einstellungen prüfen", "설정에서 확인"),
        row("perm.loc.title", "Konum", "Location", "Геолокация", "الموقع", "Standort", "위치"),
        row("perm.loc.desc", "Hava durumu ve açık hava antrenmanlarının rotası için gerekir.", "Needed for weather and outdoor workout routes.", "Нужно для погоды и маршрутов тренировок на открытом воздухе.", "مطلوب لعرض الطقس ومسارات التمارين في الهواء الطلق.", "Wird für das Wetter und die Routen von Outdoor-Training benötigt.", "날씨와 야외 운동 경로에 필요합니다."),
        row("perm.notif.title", "Bildirimler", "Notifications", "Уведомления", "الإشعارات", "Benachrichtigungen", "알림"),
        row("perm.notif.desc", "Hatırlatıcılar, alarmlar ve hava durumu bildirimleri için gerekir.", "Needed for reminders, alarms and weather notifications.", "Нужно для напоминаний, будильников и уведомлений о погоде.", "مطلوب للتذكيرات والمنبهات وإشعارات الطقس.", "Wird für Erinnerungen, Wecker und Wetterhinweise benötigt.", "알림, 알람, 날씨 알림에 필요합니다."),
        row("perm.fsi.title", "Tam ekran bildirimler", "Full-screen notifications", "Полноэкранные уведомления", "إشعارات ملء الشاشة", "Vollbild-Benachrichtigungen", "전체 화면 알림"),
        row("perm.fsi.desc", "Alarm çalarken ekranda tam olarak gösterilmesi için gerekir (Android 14+).", "Needed to show alarms full-screen when they ring (Android 14+).", "Нужно, чтобы показывать будильник на весь экран (Android 14+).", "مطلوب لعرض المنبه بملء الشاشة عند رنينه (Android 14 فما فوق).", "Wird benötigt, damit Wecker beim Klingeln im Vollbild erscheinen (Android 14+).", "알람이 울릴 때 전체 화면으로 표시하려면 필요합니다 (Android 14 이상)."),
        row("perm.bt.title", "Yakındaki cihazlar", "Nearby devices", "Устройства поблизости", "الأجهزة القريبة", "Geräte in der Nähe", "주변 기기"),
        row("perm.bt.desc", "Bluetooth kalp ritmi ölçerlere ve kulaklıklara bağlanmak için gerekir (Android 12+).", "Needed to connect to Bluetooth heart-rate monitors and headphones (Android 12+).", "Нужно для подключения Bluetooth-датчиков пульса и наушников (Android 12+).", "مطلوب للاتصال بأجهزة قياس نبضات القلب وسماعات البلوتوث (Android 12 فما فوق).", "Wird benötigt, um sich mit Bluetooth-Pulsmessern und Kopfhörern zu verbinden (Android 12+).", "블루투스 심박계 및 헤드폰에 연결하려면 필요합니다 (Android 12 이상)."),
        row("perm.act.title", "Hareket algılama", "Activity recognition", "Распознавание активности", "التعرّف على النشاط", "Aktivitätserkennung", "활동 인식"),
        row("perm.act.desc", "Adım sayımı ve hareket algılama için gerekir.", "Needed for step counting and movement detection.", "Нужно для подсчёта шагов и распознавания движения.", "مطلوب لعدّ الخطوات ورصد الحركة.", "Wird für die Schrittzählung und Bewegungserkennung benötigt.", "걸음 수 측정과 움직임 감지에 필요합니다."),
        row("perm.audio.title", "Müzik ve ses dosyaları", "Music and audio files", "Музыка и аудиофайлы", "الموسيقى وملفات الصوت", "Musik und Audiodateien", "음악 및 오디오 파일"),
        row("perm.audio.desc", "Cihazındaki müzik dosyalarını çalmak için gerekir.", "Needed to play music files stored on your device.", "Нужно для воспроизведения музыкальных файлов на устройстве.", "مطلوب لتشغيل ملفات الموسيقى المخزّنة على جهازك.", "Wird benötigt, um Musikdateien auf deinem Gerät abzuspielen.", "기기에 저장된 음악 파일을 재생하려면 필요합니다."),
        row("perm.bat.title", "Pil ve arka plan", "Battery and background", "Батарея и фоновая работа", "البطارية والعمل في الخلفية", "Akku und Hintergrundbetrieb", "배터리 및 백그라운드"),
        row("perm.bat.desc", "Pil tasarrufu uygulamayı durdurmasın diye pil ayarlarında kısıtlama olmadığından emin ol.", "Make sure battery saving is not restricting the app, so it does not get stopped.", "Убедитесь, что экономия батареи не ограничивает приложение, иначе его могут остановить.", "تأكّد من أن توفير الطاقة لا يقيّد التطبيق حتى لا يتم إيقافه.", "Stelle sicher, dass die Akkuoptimierung die App nicht einschränkt, damit sie nicht beendet wird.", "배터리 절약이 앱을 제한해 종료되지 않도록 설정을 확인하세요."),
        row("perm.auto.title", "Otomatik başlatma", "Autostart", "Автозапуск", "التشغيل التلقائي", "Autostart", "자동 시작"),
        row("perm.auto.desc", "HyperOS/MIUI için: uygulamanın arka planda açılmasına izin ver.", "For HyperOS/MIUI: allow the app to start in the background.", "Для HyperOS/MIUI: разрешите приложению запускаться в фоновом режиме.", "لأجهزة HyperOS/MIUI: اسمح للتطبيق بالعمل في الخلفية.", "Für HyperOS/MIUI: Erlaube der App, im Hintergrund zu starten.", "HyperOS/MIUI: 앱이 백그라운드에서 시작되도록 허용하세요."),
        row("perm.asked.act", "Hareket algılama izni istendi", "Activity recognition permission requested", "Запрошено разрешение на распознавание активности", "تم طلب إذن التعرّف على النشاط", "Berechtigung für Aktivitätserkennung angefordert", "활동 인식 권한 요청됨"),
        row("perm.asked.audio", "Müzik ve ses dosyaları izni istendi", "Music and audio files permission requested", "Запрошено разрешение на музыку и аудиофайлы", "تم طلب إذن الموسيقى وملفات الصوت", "Berechtigung für Musik und Audiodateien angefordert", "음악 및 오디오 파일 권한 요청됨"),
        row("perm.asked.bt", "Yakındaki cihazlar izni istendi", "Nearby devices permission requested", "Запрошено разрешение на устройства поблизости", "تم طلب إذن الأجهزة القريبة", "Berechtigung für Geräte in der Nähe angefordert", "주변 기기 권한 요청됨"),
        row("perm.asked.loc", "Konum izni istendi", "Location permission requested", "Запрошено разрешение на геолокацию", "تم طلب إذن الموقع", "Standortberechtigung angefordert", "위치 권한 요청됨"),
        row("perm.asked.notif", "Bildirim izni istendi", "Notification permission requested", "Запрошено разрешение на уведомления", "تم طلب إذن الإشعارات", "Benachrichtigungsberechtigung angefordert", "알림 권한 요청됨")
    )
}
