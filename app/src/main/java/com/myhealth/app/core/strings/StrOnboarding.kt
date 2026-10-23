package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** İlk giriş akışı: dil, karşılama, cinsiyet, ad, yaş, boy, kilo, hastalık, alerji, yapay zeka. */
object StrOnboarding {
    val rows: List<Row> = part1() + part2() + part3() + part4()

    private fun part1(): List<Row> = listOf(
        row("ob.lang.title", "Dilini seç", "Choose your language", "Выберите язык", "اختر لغتك", "Sprache wählen", "언어를 선택하세요"),
        row("ob.lang.sub", "Uygulama bu dilde açılacak. Daha sonra ayarlardan değiştirebilirsin.", "The app will open in this language. You can change it later in settings.", "Приложение откроется на этом языке. Позже его можно изменить в настройках.", "سيُفتح التطبيق بهذه اللغة. يمكنك تغييرها لاحقًا من الإعدادات.", "Die App öffnet in dieser Sprache. Du kannst sie später in den Einstellungen ändern.", "앱이 이 언어로 열립니다. 나중에 설정에서 변경할 수 있어요."),

        row("ob.welcome.tag", "Asistanın", "Your Assistant", "Ваш ассистент", "مساعدك", "Dein Assistent", "나의 어시스턴트"),
        row("ob.welcome.title", "En İyi Halini Keşfet", "Discover Your Best Self", "Откройте лучшую версию себя", "اكتشف أفضل نسخة منك", "Entdecke dein bestes Ich", "나의 최고를 발견하세요"),
        row("ob.welcome.sub", "Spor, nefes, beslenme ve yapay zeka asistanın tek uygulamada.", "Sports, breathing, nutrition and your AI assistant in one app.", "Спорт, дыхание, питание и ИИ-ассистент в одном приложении.", "الرياضة والتنفس والتغذية ومساعد الذكاء الاصطناعي في تطبيق واحد.", "Sport, Atmung, Ernährung und dein KI-Assistent in einer App.", "운동, 호흡, 영양, AI 어시스턴트를 하나의 앱에서."),
        row("ob.welcome.start", "Başla", "Get Started", "Начать", "ابدأ الآن", "Los geht's", "시작하기"),

        row("ob.step", "{0}/{1}", "{0}/{1}", "{0}/{1}", "{0}/{1}", "{0}/{1}", "{0}/{1}"),
        row("ob.go", "Devam", "Go", "Далее", "متابعة", "Weiter", "계속"),

        row("ob.gender.title", "Cinsiyetin nedir?", "What is your gender?", "Какой у вас пол?", "ما جنسك؟", "Was ist dein Geschlecht?", "성별이 어떻게 되세요?"),
        row("ob.gender.sub", "Kalori ve nabız hesaplarını sana göre yapacağız.", "We will tailor calorie and heart rate estimates to you.", "Мы подстроим расчёт калорий и пульса под вас.", "سنضبط حسابات السعرات ونبض القلب وفقًا لك.", "Wir passen Kalorien- und Pulsberechnungen an dich an.", "칼로리와 심박수 계산을 맞춤으로 해드릴게요."),
        row("ob.gender.male", "Erkek", "Male", "Мужской", "ذكر", "Männlich", "남성"),
        row("ob.gender.female", "Kadın", "Female", "Женский", "أنثى", "Weiblich", "여성"),

        row("ob.name.title", "Sana nasıl hitap edelim?", "What should we call you?", "Как к вам обращаться?", "بماذا نناديك؟", "Wie dürfen wir dich nennen?", "어떻게 불러 드릴까요?"),
        row("ob.name.sub", "Asistan seninle bu isimle konuşacak.", "Your assistant will address you by this name.", "Ассистент будет обращаться к вам по этому имени.", "سيخاطبك المساعد بهذا الاسم.", "Dein Assistent spricht dich mit diesem Namen an.", "어시스턴트가 이 이름으로 부를 거예요."),
        row("ob.name.hint", "Adın", "Your name", "Ваше имя", "اسمك", "Dein Name", "이름"),

        row("ob.age.title", "Kaç yaşındasın?", "How old are you?", "Сколько вам лет?", "كم عمرك؟", "Wie alt bist du?", "나이가 어떻게 되세요?"),
        row("ob.height.title", "Boyun kaç?", "How tall are you?", "Какой у вас рост?", "ما طولك؟", "Wie groß bist du?", "키가 어떻게 되세요?"),
        row("ob.weight.title", "Kilon kaç?", "What is your weight?", "Сколько вы весите?", "كم وزنك؟", "Wie viel wiegst du?", "몸무게가 어떻게 되세요?"),
        row("ob.scroll.hint", "Listeyi kaydırarak seç", "Scroll the list to choose", "Прокрутите список, чтобы выбрать", "مرّر القائمة للاختيار", "Liste scrollen zum Auswählen", "목록을 스크롤해서 선택하세요"),

        row("ob.cond.title", "Bir sağlık durumun var mı?", "Do you have any health conditions?", "Есть ли у вас проблемы со здоровьем?", "هل لديك حالة صحية؟", "Hast du gesundheitliche Beschwerden?", "건강 상태에 해당하는 것이 있나요?"),
        row("ob.cond.sub", "Birden fazla seçebilirsin. Sonradan düzenleyebilirsin.", "You can pick several. You can edit this later.", "Можно выбрать несколько. Позже это можно изменить.", "يمكنك اختيار أكثر من واحدة وتعديلها لاحقًا.", "Mehrfachauswahl möglich. Du kannst es später ändern.", "여러 개 선택할 수 있고 나중에 수정할 수 있어요."),
        row("ob.cond.search", "Durum ara", "Search conditions", "Поиск состояния", "ابحث عن حالة", "Beschwerden suchen", "건강 상태 검색"),

        row("ob.allergy.title", "Alerjin var mı?", "Do you have any allergies?", "Есть ли у вас аллергии?", "هل لديك حساسية؟", "Hast du Allergien?", "알레르기가 있나요?"),
        row("ob.allergy.sub", "Yemek önerilerinde bunlara dikkat edeceğiz.", "We will take these into account in food suggestions.", "Мы будем учитывать это в рекомендациях по питанию.", "سنراعي ذلك في اقتراحات الطعام.", "Wir berücksichtigen sie bei Essensvorschlägen.", "음식 추천에 이를 반영할게요."),
        row("ob.allergy.search", "Alerji ara", "Search allergies", "Поиск аллергии", "ابحث عن حساسية", "Allergien suchen", "알레르기 검색"),

        row("ob.ai.title", "Yapay zeka asistanın", "Your AI assistant", "Ваш ИИ-ассистент", "مساعدك الذكي", "Dein KI-Assistent", "나의 AI 어시스턴트"),
        row("ob.ai.sub", "İstersen Groq API anahtarını gir; atlarsan uygulamanın kendi sağlık modeli seninle konuşur.", "Enter a Groq API key if you like; if you skip, the app's own health model will chat with you.", "При желании введите ключ Groq API; если пропустите, с вами будет общаться встроенная модель здоровья.", "أدخل مفتاح Groq API إن أردت، وإن تخطيته فسيتحدث معك نموذج الصحة المدمج في التطبيق.", "Gib bei Bedarf einen Groq-API-Schlüssel ein; überspringst du ihn, antwortet das App-eigene Gesundheitsmodell.", "원하면 Groq API 키를 입력하세요. 건너뛰면 앱 자체 건강 모델이 대화해요."),
        row("ob.ai.key", "Groq API anahtarı", "Groq API key", "Ключ Groq API", "مفتاح Groq API", "Groq-API-Schlüssel", "Groq API 키"),
        row("ob.ai.skip", "Atla, yerel modeli kullan", "Skip, use the local model", "Пропустить, использовать локальную модель", "تخطَّ واستخدم النموذج المحلي", "Überspringen, lokales Modell nutzen", "건너뛰고 로컬 모델 사용"),

        row("ob.ready.title", "Hazırsın, {0}!", "You're all set, {0}!", "Всё готово, {0}!", "أنت جاهز، {0}!", "Alles bereit, {0}!", "준비 완료, {0}님!"),
        row("ob.ready.sub", "Profilin kaydedildi. İstediğin zaman ayarlardan düzenleyebilirsin.", "Your profile is saved. You can edit it anytime in settings.", "Профиль сохранён. Его можно изменить в настройках в любое время.", "تم حفظ ملفك الشخصي. يمكنك تعديله في أي وقت من الإعدادات.", "Dein Profil ist gespeichert. Du kannst es jederzeit in den Einstellungen ändern.", "프로필이 저장되었어요. 언제든 설정에서 수정할 수 있어요."),
        row("ob.ready.cta", "Uygulamaya Geç", "Enter the App", "Войти в приложение", "ادخل التطبيق", "App öffnen", "앱으로 이동")
    )

    /** Ortak düğmeler, ölçü birimleri, ilaç ve aile hekimi adımları. */
    private fun part2(): List<Row> = listOf(
        row("ob.back", "Geri", "Back", "Назад", "رجوع", "Zurück", "뒤로"),
        row("ob.next", "Devam", "Continue", "Продолжить", "متابعة", "Weiter", "계속"),
        row("ob.skip", "Atla", "Skip", "Пропустить", "تخطَّ", "Überspringen", "건너뛰기"),
        row("ob.none", "Yok", "None", "Нет", "لا شيء", "Keine", "없음"),
        row("ob.selected", "{0} seçildi", "{0} selected", "Выбрано: {0}", "تم اختيار {0}", "{0} ausgewählt", "{0}개 선택됨"),
        row("ob.search.empty", "Sonuç bulunamadı", "No results found", "Ничего не найдено", "لا توجد نتائج", "Keine Ergebnisse gefunden", "검색 결과가 없어요"),
        row("ob.unit.cm", "cm", "cm", "см", "سم", "cm", "cm"),
        row("ob.unit.kg", "kg", "kg", "кг", "كجم", "kg", "kg"),
        row("ob.unit.age", "yaş", "years", "лет", "سنة", "Jahre", "세"),
        row("ob.age.sub", "Hedeflerini ve nabız aralıklarını yaşına göre ayarlarız.", "We tune your goals and heart rate zones to your age.", "Мы подберём цели и пульсовые зоны под ваш возраст.", "سنضبط أهدافك ونطاقات نبض القلب وفقًا لعمرك.", "Wir passen deine Ziele und Pulsbereiche an dein Alter an.", "나이에 맞춰 목표와 심박수 구간을 조정해 드려요."),
        row("ob.height.sub", "Adım uzunluğu ve vücut kitle indeksi için kullanılır.", "Used for step length and body mass index.", "Используется для длины шага и индекса массы тела.", "تُستخدم لحساب طول الخطوة ومؤشر كتلة الجسم.", "Wird für Schrittlänge und Body-Mass-Index verwendet.", "보폭과 체질량지수 계산에 사용돼요."),
        row("ob.weight.sub", "Kalori, su ihtiyacı ve ideal kilo aralığı için kullanılır.", "Used for calories, water needs and healthy weight range.", "Используется для расчёта калорий, потребности в воде и здорового веса.", "تُستخدم لحساب السعرات والحاجة إلى الماء ونطاق الوزن الصحي.", "Wird für Kalorien, Wasserbedarf und gesunden Gewichtsbereich verwendet.", "칼로리, 물 섭취량, 건강 체중 범위 계산에 사용돼요."),

        row("ob.meds.title", "Kullandığın ilaçlar", "Your medications", "Ваши лекарства", "أدويتك", "Deine Medikamente", "복용 중인 약"),
        row("ob.meds.sub", "İlaçlarını “sıklıkla” ve “ara sıra” kutularına ekle. Öneriler buna göre dikkatli olur; sonradan değiştirebilirsin.", "Add your medications to the “regularly” and “occasionally” boxes. Suggestions will take them into account; you can change this later.", "Добавьте лекарства в поля «регулярно» и «время от времени». Рекомендации будут это учитывать; позже можно изменить.", "أضف أدويتك إلى مربعي «بانتظام» و«أحيانًا». ستراعي الاقتراحات ذلك، ويمكنك التعديل لاحقًا.", "Füge deine Medikamente den Feldern „regelmäßig“ und „gelegentlich“ hinzu. Vorschläge berücksichtigen sie; du kannst es später ändern.", "복용 약을 ‘꾸준히’와 ‘가끔’ 상자에 추가하세요. 추천에 반영되며 나중에 바꿀 수 있어요."),
        row("ob.meds.none", "İlaç kullanmıyorum", "I don't take any medication", "Я не принимаю лекарства", "لا أستخدم أي أدوية", "Ich nehme keine Medikamente", "복용 중인 약이 없어요"),

        row("ob.doctor.title", "Aile hekimin", "Your family doctor", "Ваш семейный врач", "طبيب أسرتك", "Dein Hausarzt", "나의 가정의"),
        row("ob.doctor.sub", "İstersen adını ve telefonunu ekle; ihtiyaç olduğunda tek dokunuşla arayabilirsin.", "Add a name and phone number if you like, so you can call with one tap when needed.", "При желании добавьте имя и телефон, чтобы позвонить одним касанием.", "أضف الاسم ورقم الهاتف إن أردت لتتصل بنقرة واحدة عند الحاجة.", "Füge bei Bedarf Name und Telefonnummer hinzu, um mit einem Tipp anzurufen.", "원하면 이름과 전화번호를 추가해 한 번에 전화할 수 있게 하세요."),
        row("ob.doctor.name", "Hekimin adı (isteğe bağlı)", "Doctor's name (optional)", "Имя врача (необязательно)", "اسم الطبيب (اختياري)", "Name des Arztes (optional)", "의사 이름 (선택)"),
        row("ob.doctor.phone", "Telefon (isteğe bağlı)", "Phone (optional)", "Телефон (необязательно)", "الهاتف (اختياري)", "Telefon (optional)", "전화번호 (선택)"),
        row("ob.doctor.note", "Belirtilerin sürerse veya ağırlaşırsa asistan seni önce aile hekimine yönlendirir.", "If symptoms persist or get worse, the assistant will point you to your family doctor first.", "Если симптомы сохраняются или усиливаются, ассистент в первую очередь направит вас к семейному врачу.", "إذا استمرت الأعراض أو ازدادت سوءًا فسيوجّهك المساعد أولًا إلى طبيب أسرتك.", "Halten Beschwerden an oder verschlimmern sie sich, verweist dich der Assistent zuerst an deinen Hausarzt.", "증상이 계속되거나 심해지면 어시스턴트가 먼저 가정의 상담을 안내해요.")
    )

    /** Izin tanitim ekranlari (ob.perm.<id>.title / .sub). */
    private fun part3(): List<Row> = listOf(
        row("ob.perm.loc.title", "Konum", "Location", "Геолокация", "الموقع", "Standort", "위치"),
        row("ob.perm.loc.sub", "Hava durumunu ve açık hava antrenmanlarının rotasını gösterebilmek için konumuna ihtiyacımız var.", "We need your location to show the weather and the route of your outdoor workouts.", "Нужна геолокация, чтобы показывать погоду и маршрут уличных тренировок.", "نحتاج إلى موقعك لعرض الطقس ومسار تمارينك في الهواء الطلق.", "Wir brauchen deinen Standort, um Wetter und Strecke deiner Outdoor-Trainings anzuzeigen.", "날씨와 야외 운동 경로를 보여 드리려면 위치 정보가 필요해요."),

        row("ob.perm.notif.title", "Bildirimler", "Notifications", "Уведомления", "الإشعارات", "Benachrichtigungen", "알림"),
        row("ob.perm.notif.sub", "Alarmların, öğün hatırlatmaların ve asistan ipuçların zamanında gelsin.", "So your alarms, meal reminders and assistant tips arrive on time.", "Чтобы будильники, напоминания о еде и советы ассистента приходили вовремя.", "لتصل المنبهات وتذكيرات الوجبات ونصائح المساعد في وقتها.", "Damit Wecker, Mahlzeiten-Erinnerungen und Tipps des Assistenten pünktlich ankommen.", "알람, 식사 알림, 어시스턴트 팁을 제때 받을 수 있어요."),

        row("ob.perm.bt.title", "Bluetooth ve yakındaki cihazlar", "Bluetooth and nearby devices", "Bluetooth и устройства рядом", "البلوتوث والأجهزة القريبة", "Bluetooth und Geräte in der Nähe", "블루투스 및 주변 기기"),
        row("ob.perm.bt.sub", "Kalp atış ölçer ve akıllı saat gibi cihazları bulup bağlayabilmek için.", "To find and connect devices such as heart-rate monitors and smartwatches.", "Чтобы находить и подключать пульсометры и умные часы.", "للعثور على أجهزة مثل مقاييس نبض القلب والساعات الذكية وربطها.", "Um Geräte wie Pulsgurte und Smartwatches zu finden und zu verbinden.", "심박계나 스마트워치 같은 기기를 찾아 연결하기 위해서요."),

        row("ob.perm.act.title", "Hareket algılama", "Activity recognition", "Распознавание активности", "التعرّف على النشاط", "Bewegungserkennung", "활동 인식"),
        row("ob.perm.act.sub", "Adımlarını ve hareketlerini sayabilmek için.", "So we can count your steps and movement.", "Чтобы считать шаги и движения.", "لنتمكن من إحصاء خطواتك وحركتك.", "Damit wir deine Schritte und Bewegungen zählen können.", "걸음 수와 움직임을 세기 위해서요."),

        row("ob.perm.audio.title", "Müzik dosyaları", "Music files", "Музыкальные файлы", "ملفات الموسيقى", "Musikdateien", "음악 파일"),
        row("ob.perm.audio.sub", "Telefondaki müziklerini antrenman sırasında çalabilmek için.", "So you can play the music on your phone during workouts.", "Чтобы включать музыку с телефона во время тренировок.", "لتشغيل الموسيقى المحفوظة على هاتفك أثناء التمرين.", "Damit du deine Musik vom Telefon während des Trainings abspielen kannst.", "운동 중에 휴대폰의 음악을 재생할 수 있어요."),

        row("ob.perm.bg.title", "Arka plan ve otomatik başlatma", "Background and autostart", "Фон и автозапуск", "التشغيل في الخلفية والتشغيل التلقائي", "Hintergrund und Autostart", "백그라운드 및 자동 시작"),
        row("ob.perm.bg.sub", "Üretici ayarlarında uygulamanın arka planda çalışmasına ve açılışta başlamasına izin verirsen alarmlar ve sayaçlar güvenilir çalışır. Ayarlar ekranı açılacak.", "If you allow the app to run in the background and start automatically in the manufacturer's settings, alarms and counters work reliably. The settings screen will open.", "Если разрешить приложению работать в фоне и запускаться автоматически в настройках производителя, будильники и счётчики будут работать надёжно. Откроется экран настроек.", "إذا سمحت للتطبيق بالعمل في الخلفية والتشغيل تلقائيًا من إعدادات الشركة المصنّعة، ستعمل المنبهات والعدادات بشكل موثوق. وستُفتح شاشة الإعدادات.", "Erlaubst du der App in den Einstellungen des Herstellers, im Hintergrund zu laufen und automatisch zu starten, funktionieren Wecker und Zähler zuverlässig. Der Einstellungsbildschirm wird geöffnet.", "제조사 설정에서 앱의 백그라운드 실행과 자동 시작을 허용하면 알람과 측정이 안정적으로 작동해요. 설정 화면이 열립니다.")
    )

    /** Izin dugmeleri ve Groq yardim metni. */
    private fun part4(): List<Row> = listOf(
        row("ob.perm.allow", "İzin ver", "Allow", "Разрешить", "السماح", "Erlauben", "허용"),
        row("ob.perm.later", "Şimdi değil", "Not now", "Не сейчас", "ليس الآن", "Nicht jetzt", "나중에"),
        row("ob.perm.open", "Ayarları aç", "Open settings", "Открыть настройки", "فتح الإعدادات", "Einstellungen öffnen", "설정 열기"),
        row("ob.perm.granted", "İzin verildi", "Allowed", "Разрешено", "تم السماح", "Erlaubt", "허용됨"),
        row("ob.groq.free", "Groq anahtarı ücretsizdir: groq.com adresinde hesap aç, konsoldaki API Keys bölümünden yeni anahtar oluşturup buraya yapıştır.", "A Groq key is free: create an account at groq.com, make a new key in the API Keys section of the console and paste it here.", "Ключ Groq бесплатный: зарегистрируйтесь на groq.com, создайте новый ключ в разделе API Keys консоли и вставьте его сюда.", "مفتاح Groq مجاني: أنشئ حسابًا على groq.com، ثم أنشئ مفتاحًا جديدًا من قسم API Keys في اللوحة والصقه هنا.", "Ein Groq-Schlüssel ist kostenlos: Erstelle ein Konto auf groq.com, erzeuge im Bereich API Keys der Konsole einen neuen Schlüssel und füge ihn hier ein.", "Groq 키는 무료예요. groq.com에서 계정을 만든 뒤 콘솔의 API Keys에서 새 키를 만들어 여기에 붙여 넣으세요.")
    )
}
