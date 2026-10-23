package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Ayarlar ekrani (settings.*) ve tanilama (diag.*). */
object StrSettings {
    val rows: List<Row> = part1() + part2() + part3() + part4() + part5() + part6()

    private fun part1(): List<Row> = listOf(
        row("settings.title", "Ayarlar", "Settings", "Настройки", "الإعدادات", "Einstellungen", "설정"),
        row("settings.section.doctor", "Aile hekimim", "My family doctor", "Мой семейный врач", "طبيب الأسرة", "Mein Hausarzt", "내 가정의학과 의사"),
        row("settings.doctor.hint", "Belirtilerinde hızlıca arayabilmen için hekiminin bilgilerini ekle.", "Add your doctor's details so you can call quickly when symptoms appear.", "Добавьте данные врача, чтобы быстро позвонить при появлении симптомов.", "أضف بيانات طبيبك لتتصل به بسرعة عند ظهور الأعراض.", "Hinterlege die Daten deines Arztes, damit du bei Beschwerden schnell anrufen kannst.", "증상이 나타나면 빠르게 전화할 수 있도록 의사 정보를 입력하세요."),
        row("settings.doctor.name", "Hekimin adı", "Doctor's name", "Имя врача", "اسم الطبيب", "Name des Arztes", "의사 이름"),
        row("settings.doctor.phone", "Telefon numarası", "Phone number", "Номер телефона", "رقم الهاتف", "Telefonnummer", "전화번호"),
        row("settings.doctor.save", "Kaydet", "Save", "Сохранить", "حفظ", "Speichern", "저장"),
        row("settings.doctor.call", "Ara", "Call", "Позвонить", "اتصال", "Anrufen", "전화"),
        row("settings.doctor.nophone", "Önce bir telefon numarası ekle.", "Add a phone number first.", "Сначала добавьте номер телефона.", "أضف رقم هاتف أولاً.", "Füge zuerst eine Telefonnummer hinzu.", "먼저 전화번호를 입력하세요."),
        row("settings.doctor.dialfail", "Arama ekranı açılamadı.", "Could not open the dialer.", "Не удалось открыть набор номера.", "تعذّر فتح شاشة الاتصال.", "Die Wählansicht konnte nicht geöffnet werden.", "전화 앱을 열 수 없습니다."),
        row("settings.doctor.always", "Belirtilerim sürerse asistan her zaman aile hekimime yönlendirsin", "If my symptoms persist, the assistant always directs me to my family doctor", "Если симптомы не проходят, ассистент всегда направляет меня к семейному врачу", "إذا استمرت أعراضي، يوجّهني المساعد دائمًا إلى طبيب الأسرة", "Wenn meine Beschwerden anhalten, soll mich der Assistent immer an meinen Hausarzt verweisen", "증상이 계속되면 도우미가 항상 내 가정의학과 의사에게 안내하도록 합니다"),
        row("settings.doctor.always.sub", "Açıkken belirtiler sürer veya ağırlaşırsa asistan hekimine başvurmanı vurgular.", "When on, the assistant stresses seeing your doctor if symptoms persist or worsen.", "Если включено, ассистент советует обратиться к врачу, когда симптомы сохраняются или усиливаются.", "عند التفعيل، يؤكد المساعد على مراجعة طبيبك إذا استمرت الأعراض أو ازدادت سوءًا.", "Wenn aktiv, betont der Assistent den Arztbesuch, wenn Beschwerden anhalten oder sich verschlimmern.", "켜져 있으면 증상이 지속되거나 악화될 때 도우미가 진료를 강조합니다."),
        row("settings.doctor.severe.title", "Aile hekiminle görüşmeni öneririm", "I recommend talking to your family doctor", "Рекомендую поговорить с семейным врачом", "أنصحك بالتحدث إلى طبيب الأسرة", "Ich empfehle, mit deinem Hausarzt zu sprechen", "가정의학과 의사와 상담하시길 권합니다"),
        row("settings.doctor.severe.body", "Son 14 günde şiddetli belirtiler kaydettin. Bunları hekiminle paylaş ve doktor raporunu yanında götür.", "You logged severe symptoms in the last 14 days. Share them with your doctor and bring the doctor report.", "За последние 14 дней вы отметили сильные симптомы. Покажите их врачу и возьмите отчёт для врача.", "سجّلتَ أعراضًا شديدة خلال آخر 14 يومًا. شاركها مع طبيبك وخذ معك تقرير الطبيب.", "Du hast in den letzten 14 Tagen starke Beschwerden erfasst. Teile sie mit deinem Arzt und nimm den Arztbericht mit.", "최근 14일 동안 심한 증상을 기록했습니다. 의사와 공유하고 의사 보고서를 가져가세요."),
        row("settings.section.health", "Sağlık", "Health", "Здоровье", "الصحة", "Gesundheit", "건강"),
        row("settings.health.log", "Sağlık günlüğü", "Health log", "Журнал здоровья", "سجل الصحة", "Gesundheitstagebuch", "건강 기록"),
        row("settings.health.log.sub", "Egzersiz sonrası verdiğin sağlık yanıtları", "Your health answers after exercise", "Ваши ответы о самочувствии после тренировки", "إجاباتك الصحية بعد التمرين", "Deine Gesundheitsangaben nach dem Training", "운동 후 입력한 건강 응답"),
        row("settings.meds", "İlaçlarım", "My medications", "Мои лекарства", "أدويتي", "Meine Medikamente", "내 약"),
        row("settings.meds.count", "{0} ilaç kayıtlı", "{0} medicines saved", "Сохранено лекарств: {0}", "{0} دواء محفوظ", "{0} Medikamente gespeichert", "저장된 약 {0}개"),
        row("settings.section.report", "Doktor raporu", "Doctor report", "Отчёт для врача", "تقرير الطبيب", "Arztbericht", "의사 보고서"),
        row("settings.saved", "Kaydedildi", "Saved", "Сохранено", "تم الحفظ", "Gespeichert", "저장됨")
    )

    private fun part2(): List<Row> = listOf(
        row("settings.section.ai", "Yapay zekâ", "AI", "Искусственный интеллект", "الذكاء الاصطناعي", "KI", "인공지능"),
        row("settings.ai.mode", "Çalışma modu", "Operating mode", "Режим работы", "وضع التشغيل", "Betriebsmodus", "작동 방식"),
        row("settings.ai.mode.local", "Yerel", "Local", "Локальный", "محلي", "Lokal", "로컬"),
        row("settings.ai.mode.groq", "Groq (bulut)", "Groq (cloud)", "Groq (облако)", "Groq (سحابي)", "Groq (Cloud)", "Groq (클라우드)"),
        row("settings.ai.groq.key", "Groq anahtarı", "Groq key", "Ключ Groq", "مفتاح Groq", "Groq-Schlüssel", "Groq 키"),
        row("settings.ai.groq.hint", "Groq API anahtarını gir", "Enter your Groq API key", "Введите ключ API Groq", "أدخل مفتاح واجهة Groq", "Groq-API-Schlüssel eingeben", "Groq API 키를 입력하세요"),
        row("settings.ai.groq.hide", "Anahtarı gizle", "Hide key", "Скрыть ключ", "إخفاء المفتاح", "Schlüssel ausblenden", "키 숨기기"),
        row("settings.ai.groq.show", "Anahtarı göster", "Show key", "Показать ключ", "إظهار المفتاح", "Schlüssel anzeigen", "키 표시"),
        row("settings.ai.groq.save", "Anahtarı kaydet", "Save key", "Сохранить ключ", "حفظ المفتاح", "Schlüssel speichern", "키 저장"),
        row("settings.ai.followup", "Egzersiz sonrası destek bildirimi", "Post-workout support notification", "Уведомление поддержки после тренировки", "إشعار دعم بعد التمرين", "Unterstützungs-Benachrichtigung nach dem Training", "운동 후 지원 알림"),
        row("settings.ai.followup.sub", "Egzersizden yaklaşık 10-15 dakika sonra asistan seni yoklar.", "About 10 to 15 minutes after a workout, the assistant checks in on you.", "Примерно через 10–15 минут после тренировки ассистент проверит ваше самочувствие.", "بعد التمرين بنحو 10 إلى 15 دقيقة، يتابع المساعد حالتك.", "Etwa 10 bis 15 Minuten nach dem Training meldet sich der Assistent bei dir.", "운동 후 약 10~15분 뒤에 도우미가 안부를 묻습니다."),
        row("settings.ai.dailytip", "Günlük yapay zekâ ipucu", "Daily AI tip", "Ежедневный совет ИИ", "نصيحة الذكاء الاصطناعي اليومية", "Täglicher KI-Tipp", "일일 AI 팁"),
        row("settings.ai.dailytip.sub", "Günde bir kez sağlıklı yaşam ipucu bildirimi.", "One healthy-living tip notification a day.", "Одно уведомление с советом о здоровом образе жизни в день.", "إشعار واحد يوميًا بنصيحة لنمط حياة صحي.", "Einmal täglich eine Benachrichtigung mit einem Tipp für gesunde Lebensweise.", "하루에 한 번 건강한 생활 팁 알림을 보냅니다.")
    )

    private fun part3(): List<Row> = listOf(
        row("settings.section.notif", "Bildirimler", "Notifications", "Уведомления", "الإشعارات", "Benachrichtigungen", "알림"),
        row("settings.notif.weather", "Hava durumu bildirimi", "Weather notification", "Уведомления о погоде", "إشعارات الطقس", "Wetterbenachrichtigung", "날씨 알림"),
        row("settings.notif.weather.sub", "Yerel hava durumunu düzenli olarak bildirir.", "Regularly sends the local weather.", "Регулярно присылает местную погоду.", "يرسل حالة الطقس المحلية بانتظام.", "Informiert regelmäßig über das lokale Wetter.", "현지 날씨를 주기적으로 알려 줍니다."),
        row("settings.notif.every", "Sıklık", "Frequency", "Частота", "التكرار", "Häufigkeit", "빈도"),
        row("settings.unit.min", "{0} dk", "{0} min", "{0} мин", "{0} د", "{0} Min.", "{0}분"),
        row("settings.unit.hour", "{0} sa", "{0} h", "{0} ч", "{0} س", "{0} Std.", "{0}시간"),
        row("settings.notif.activity", "Hareket algılama", "Movement detection", "Обнаружение активности", "رصد الحركة", "Bewegungserkennung", "움직임 감지"),
        row("settings.notif.activity.sub", "Uzun süre hareketsiz kaldığında hatırlatır.", "Reminds you after you have been inactive for a long time.", "Напоминает, если вы долго не двигались.", "يذكّرك عند البقاء ساكنًا لفترة طويلة.", "Erinnert dich, wenn du lange still gesessen hast.", "오랫동안 움직이지 않으면 알려 줍니다."),
        row("settings.notif.meal", "Öğün hatırlatıcıları", "Meal reminders", "Напоминания о приёмах пищи", "تذكيرات الوجبات", "Mahlzeiten-Erinnerungen", "식사 알림"),
        row("settings.notif.meal.sub", "Kahvaltı, öğle ve akşam yemeği saatlerinde hatırlatır.", "Reminds you at breakfast, lunch and dinner times.", "Напоминает о завтраке, обеде и ужине.", "يذكّرك بأوقات الفطور والغداء والعشاء.", "Erinnert zu Frühstücks-, Mittags- und Abendessenszeiten.", "아침, 점심, 저녁 식사 시간에 알려 줍니다."),
        row("settings.notif.meal.breakfast", "Kahvaltı", "Breakfast", "Завтрак", "الفطور", "Frühstück", "아침"),
        row("settings.notif.meal.lunch", "Öğle yemeği", "Lunch", "Обед", "الغداء", "Mittagessen", "점심"),
        row("settings.notif.meal.dinner", "Akşam yemeği", "Dinner", "Ужин", "العشاء", "Abendessen", "저녁"),
        row("settings.notif.fast.auto", "Oruç dönemini otomatik algıla", "Detect fasting periods automatically", "Автоматически определять периоды поста", "رصد فترات الصيام تلقائيًا", "Fastenzeiten automatisch erkennen", "단식 기간 자동 감지"),
        row("settings.notif.fast.auto.sub", "Ramazan gibi dönemlerde öğün saatlerini oruca göre ayarlar.", "Adjusts meal times to fasting periods such as Ramadan.", "Подстраивает время приёмов пищи под периоды поста, например Рамадан.", "يضبط أوقات الوجبات وفق فترات الصيام مثل رمضان.", "Passt die Essenszeiten an Fastenzeiten wie Ramadan an.", "라마단 같은 기간에 맞춰 식사 시간을 조정합니다."),
        row("settings.notif.fast.force", "Oruç modunu zorla", "Force fasting mode", "Принудительный режим поста", "فرض وضع الصيام", "Fastenmodus erzwingen", "단식 모드 강제 적용"),
        row("settings.notif.fast.force.sub", "Açıkken oruç modu tarih farkı gözetmeden uygulanır.", "When on, fasting mode applies regardless of the date.", "Если включено, режим поста применяется независимо от даты.", "عند التفعيل يُطبَّق وضع الصيام بغض النظر عن التاريخ.", "Wenn aktiv, gilt der Fastenmodus unabhängig vom Datum.", "켜면 날짜와 관계없이 단식 모드가 적용됩니다."),
        row("settings.notif.sahur", "Sahur alarmı", "Suhoor alarm", "Будильник на сухур", "منبه السحور", "Sahur-Wecker", "사후르 알람"),
        row("settings.notif.sahur.sub", "Sahur vaktinde uyandırmak için alarm kurar.", "Sets an alarm to wake you for suhoor.", "Ставит будильник, чтобы разбудить на сухур.", "يضبط منبهًا لإيقاظك للسحور.", "Stellt einen Wecker, der dich zum Sahur weckt.", "사후르 시간에 깨우도록 알람을 설정합니다."),
        row("settings.notif.sahur.offset", "Alarm, vakitten kaç dakika önce çalsın", "How many minutes before the time the alarm rings", "За сколько минут до времени звонит будильник", "كم دقيقة قبل الوقت يُرنّ المنبه", "Wie viele Minuten vorher der Wecker klingelt", "시간 몇 분 전에 알람이 울릴지"),
        row("settings.notif.iftar", "İftar alarmı", "Iftar alarm", "Будильник на ифтар", "منبه الإفطار", "Iftar-Wecker", "이프타르 알람"),
        row("settings.notif.iftar.sub", "İftar vaktinde haber verir.", "Notifies you at iftar time.", "Сообщит о времени ифтара.", "ينبّهك عند وقت الإفطار.", "Benachrichtigt dich zur Iftar-Zeit.", "이프타르 시간에 알려 줍니다.")
    )

    private fun part4(): List<Row> = listOf(
        row("settings.section.goals", "Hedefler", "Goals", "Цели", "الأهداف", "Ziele", "목표"),
        row("settings.goal.steps", "Günlük adım hedefi", "Daily step goal", "Ежедневная цель по шагам", "هدف الخطوات اليومي", "Tägliches Schrittziel", "일일 걸음 목표"),
        row("settings.goal.steps.sub", "Her gün ulaşmak istediğin adım sayısı.", "The number of steps you want to reach each day.", "Количество шагов, которое вы хотите проходить каждый день.", "عدد الخطوات الذي تريد بلوغه كل يوم.", "Die Schrittzahl, die du jeden Tag erreichen möchtest.", "매일 달성하고 싶은 걸음 수입니다."),
        row("settings.goal.value", "{0} adım", "{0} steps", "{0} шагов", "{0} خطوة", "{0} Schritte", "{0}걸음"),
        row("settings.goal.less", "Hedefi azalt", "Decrease goal", "Уменьшить цель", "تقليل الهدف", "Ziel verringern", "목표 줄이기"),
        row("settings.goal.more", "Hedefi artır", "Increase goal", "Увеличить цель", "زيادة الهدف", "Ziel erhöhen", "목표 늘리기"),
        row("settings.music.source", "Müzik kaynağı", "Music source", "Источник музыки", "مصدر الموسيقى", "Musikquelle", "음악 소스"),
        row("settings.music.audius", "Audius", "Audius", "Audius", "Audius", "Audius", "Audius"),
        row("settings.music.youtube", "YouTube tarzı (deneysel)", "YouTube-style (experimental)", "В стиле YouTube (экспериментально)", "على غرار YouTube (تجريبي)", "YouTube-ähnlich (experimentell)", "YouTube 방식 (실험적)"),
        row("settings.music.tos", "YouTube tarzı kaynak deneyseldir ve hizmet koşullarına aykırı olabilir. Sorun yaşarsan Audius'a geç.", "The YouTube-style source is experimental and may conflict with the terms of service. If you run into problems, switch to Audius.", "Источник в стиле YouTube экспериментальный и может противоречить условиям использования. При проблемах переключитесь на Audius.", "مصدر YouTube التجريبي قد يخالف شروط الخدمة. إذا واجهتك مشكلات فانتقل إلى Audius.", "Die YouTube-ähnliche Quelle ist experimentell und kann gegen die Nutzungsbedingungen verstoßen. Bei Problemen wechsle zu Audius.", "YouTube 방식 소스는 실험적이며 이용 약관에 어긋날 수 있습니다. 문제가 생기면 Audius로 바꾸세요."),
        row("settings.section.backup", "Yedek", "Backup", "Резервная копия", "النسخ الاحتياطي", "Sicherung", "백업"),
        row("settings.section.perms", "İzinler", "Permissions", "Разрешения", "الأذونات", "Berechtigungen", "권한"),
        row("settings.perms", "İzinleri yönet", "Manage permissions", "Управление разрешениями", "إدارة الأذونات", "Berechtigungen verwalten", "권한 관리"),
        row("settings.perms.sub", "Konum, bildirim, Bluetooth ve pil ayarlarının durumu.", "Status of location, notification, Bluetooth and battery settings.", "Состояние геолокации, уведомлений, Bluetooth и батареи.", "حالة إعدادات الموقع والإشعارات والبلوتوث والبطارية.", "Status von Standort, Benachrichtigungen, Bluetooth und Akku.", "위치, 알림, 블루투스, 배터리 설정 상태입니다.")
    )

    private fun part5(): List<Row> = listOf(
        row("settings.section.lang", "Dil", "Language", "Язык", "اللغة", "Sprache", "언어"),
        row("settings.section.about", "Hakkında", "About", "О приложении", "حول", "Über", "정보"),
        row("settings.about.version", "Sürüm", "Version", "Версия", "الإصدار", "Version", "버전"),
        row("settings.about.privacy", "Gizlilik", "Privacy", "Конфиденциальность", "الخصوصية", "Datenschutz", "개인정보"),
        row("settings.about.privacy.sub", "Verilerin bu cihazda saklanır.", "Your data is stored on this device.", "Ваши данные хранятся на этом устройстве.", "بياناتك مخزّنة على هذا الجهاز.", "Deine Daten werden auf diesem Gerät gespeichert.", "데이터는 이 기기에 저장됩니다."),
        row("settings.about.privacy.body", "Verilerin bu cihazda saklanır. Hava durumu için Open-Meteo, müzik için Audius gibi hizmetlere istek gönderilir. Groq modu seçilirse yazdıkların Groq'a iletilir.", "Your data is stored on this device. Requests go to services such as Open-Meteo for weather and Audius for music. If Groq mode is selected, what you write is sent to Groq.", "Ваши данные хранятся на этом устройстве. Запросы отправляются таким сервисам, как Open-Meteo (погода) и Audius (музыка). Если выбран режим Groq, введённый текст передаётся в Groq.", "بياناتك مخزّنة على هذا الجهاز. تُرسل طلبات إلى خدمات مثل Open-Meteo للطقس وAudius للموسيقى. وإذا اخترت وضع Groq فسيُرسل ما تكتبه إلى Groq.", "Deine Daten werden auf diesem Gerät gespeichert. Anfragen gehen an Dienste wie Open-Meteo (Wetter) und Audius (Musik). Wird der Groq-Modus gewählt, werden deine Eingaben an Groq übermittelt.", "데이터는 이 기기에 저장됩니다. 날씨는 Open-Meteo, 음악은 Audius 같은 서비스로 요청이 전송됩니다. Groq 모드를 선택하면 입력한 내용이 Groq로 전송됩니다."),
        row("settings.about.licenses", "Lisanslar ve atıflar", "Licenses and credits", "Лицензии и благодарности", "التراخيص والإسناد", "Lizenzen und Danksagungen", "라이선스 및 출처"),
        row("settings.about.lic.fonts", "Poppins ve Inter yazı tipleri: SIL Open Font License (OFL).", "Poppins and Inter fonts: SIL Open Font License (OFL).", "Шрифты Poppins и Inter: SIL Open Font License (OFL).", "خطوط Poppins وInter: رخصة SIL المفتوحة للخطوط (OFL).", "Schriftarten Poppins und Inter: SIL Open Font License (OFL).", "Poppins 및 Inter 글꼴: SIL Open Font License (OFL)."),
        row("settings.about.lic.osm", "Harita verileri © OpenStreetMap katkıcıları (ODbL).", "Map data © OpenStreetMap contributors (ODbL).", "Картографические данные © участники OpenStreetMap (ODbL).", "بيانات الخريطة © مساهمو OpenStreetMap (ODbL).", "Kartendaten © OpenStreetMap-Mitwirkende (ODbL).", "지도 데이터 © OpenStreetMap 기여자 (ODbL)."),
        row("settings.about.lic.meteo", "Hava durumu: Open-Meteo.", "Weather: Open-Meteo.", "Погода: Open-Meteo.", "الطقس: Open-Meteo.", "Wetter: Open-Meteo.", "날씨: Open-Meteo."),
        row("settings.about.lic.audius", "Müzik: Audius.", "Music: Audius.", "Музыка: Audius.", "الموسيقى: Audius.", "Musik: Audius.", "음악: Audius."),
        row("settings.about.lic.wiki", "İçerik kaynağı: Wikipedia (CC BY-SA).", "Content source: Wikipedia (CC BY-SA).", "Источник материалов: Википедия (CC BY-SA).", "مصدر المحتوى: ويكيبيديا (CC BY-SA).", "Inhaltsquelle: Wikipedia (CC BY-SA).", "콘텐츠 출처: Wikipedia (CC BY-SA)."),
        row("settings.about.star", "Depoya yıldız ver", "Star the repository", "Поставить звезду репозиторию", "ضع نجمة للمستودع", "Repository mit Stern markieren", "저장소에 별표 주기")
    )

    private fun part6(): List<Row> = listOf(
        row("settings.section.diag", "Teşhis kaydı", "Diagnostic log", "Журнал диагностики", "سجل التشخيص", "Diagnoseprotokoll", "진단 기록"),
        row("settings.diag.hint", "Çökme ve hata kayıtlarını destek ekibiyle paylaşabilirsin.", "You can share crash and error records with support.", "Можно поделиться записями о сбоях и ошибках со службой поддержки.", "يمكنك مشاركة سجلات الأعطال والأخطاء مع الدعم.", "Du kannst Absturz- und Fehlerprotokolle mit dem Support teilen.", "충돌 및 오류 기록을 지원팀과 공유할 수 있습니다."),
        row("settings.diag.has", "Kayıtlı çökme veya hata var.", "There is a saved crash or error.", "Есть сохранённый сбой или ошибка.", "يوجد عطل أو خطأ محفوظ.", "Es gibt ein gespeichertes Absturz- oder Fehlerprotokoll.", "저장된 충돌 또는 오류가 있습니다."),
        row("settings.diag.none", "Kayıt yok.", "No records.", "Записей нет.", "لا توجد سجلات.", "Keine Einträge.", "기록이 없습니다."),
        row("settings.diag.share", "Paylaş", "Share", "Поделиться", "مشاركة", "Teilen", "공유"),
        row("settings.diag.clear", "Temizle", "Clear", "Очистить", "مسح", "Leeren", "지우기"),
        row("settings.diag.cleared", "Teşhis kaydı temizlendi.", "Diagnostic log cleared.", "Журнал диагностики очищен.", "تم مسح سجل التشخيص.", "Diagnoseprotokoll geleert.", "진단 기록을 지웠습니다."),
        row("settings.diag.sharefail", "Paylaşım ekranı açılamadı.", "Could not open the share sheet.", "Не удалось открыть окно отправки.", "تعذّر فتح نافذة المشاركة.", "Das Teilen-Fenster konnte nicht geöffnet werden.", "공유 화면을 열 수 없습니다."),
        row("settings.section.danger", "Tehlikeli bölge", "Danger zone", "Опасная зона", "منطقة الخطر", "Gefahrenbereich", "위험 구역"),
        row("settings.danger.wipe", "Verilerimi sil", "Delete my data", "Удалить мои данные", "حذف بياناتي", "Meine Daten löschen", "내 데이터 삭제"),
        row("settings.danger.wipe.sub", "Uygulamadaki tüm kayıtlar silinir. Bu işlem geri alınamaz.", "All records in the app are deleted. This cannot be undone.", "Все записи в приложении будут удалены. Это нельзя отменить.", "سيتم حذف جميع السجلات في التطبيق. لا يمكن التراجع عن ذلك.", "Alle Einträge in der App werden gelöscht. Dies kann nicht rückgängig gemacht werden.", "앱의 모든 기록이 삭제됩니다. 이 작업은 되돌릴 수 없습니다."),
        row("settings.danger.title", "Tüm veriler silinsin mi?", "Delete all data?", "Удалить все данные?", "حذف جميع البيانات؟", "Alle Daten löschen?", "모든 데이터를 삭제할까요?"),
        row("settings.danger.body", "Antrenmanlar, notlar, alarmlar, öğünler, ilaçlar, sohbet, ayarlar ve profil silinecek. Bu işlem geri alınamaz.", "Workouts, notes, alarms, meals, medications, chat, settings and profile will be deleted. This cannot be undone.", "Будут удалены тренировки, заметки, будильники, приёмы пищи, лекарства, чат, настройки и профиль. Это нельзя отменить.", "ستُحذف التمارين والملاحظات والمنبهات والوجبات والأدوية والمحادثة والإعدادات والملف الشخصي. لا يمكن التراجع عن ذلك.", "Trainings, Notizen, Wecker, Mahlzeiten, Medikamente, Chat, Einstellungen und Profil werden gelöscht. Dies kann nicht rückgängig gemacht werden.", "운동, 메모, 알람, 식사, 약, 대화, 설정, 프로필이 삭제됩니다. 이 작업은 되돌릴 수 없습니다."),
        row("settings.danger.confirm", "Evet, sil", "Yes, delete", "Да, удалить", "نعم، احذف", "Ja, löschen", "예, 삭제합니다"),
        row("settings.danger.done", "Veriler silindi.", "Data deleted.", "Данные удалены.", "تم حذف البيانات.", "Daten gelöscht.", "데이터가 삭제되었습니다.")
    )
}
