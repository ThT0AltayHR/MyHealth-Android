package com.myhealth.app.core.strings

import com.myhealth.app.core.Row
import com.myhealth.app.core.row

/** Tum yapay zeka yuzeylerinin paylastigi metinler (ai.*). Sahibi: entegrator; DEGISTIRMEYIN. */
object StrAi {
    val rows: List<Row> = listOf(
        row("ai.disclaimer",
            "Yapay zekâ hiçbir zaman gerçek bir sağlık sorununu teşhis edemez. En doğru bilgiyi almak ve sağlık sorunlarınızı gidermek için lütfen en yakın aile hekimine veya uzmanına danışın. Ayrıca uygulamadaki İstatistikler bölümünden PDF raporunu alıp doktorunuza göstermeyi unutmayın: egzersizlerde verdiğiniz yanıtlar yapay zekâ tarafından değerlendirilmiş ve doğrudan yanıtlarınızı içerir; bu, belirtilerinizin daha kolay değerlendirilmesine yardımcı olabilir.",
            "AI can never diagnose a real health problem. For the most accurate information and to resolve any health concern, please consult your nearest family physician or a specialist. Also remember to take the PDF report from the Statistics section to your doctor: it contains your own answers from exercises, evaluated by AI, and can help your symptoms be assessed more easily.",
            "Искусственный интеллект никогда не сможет поставить диагноз при реальной проблеме со здоровьем. Чтобы получить самую точную информацию и решить проблемы со здоровьем, обратитесь к ближайшему семейному врачу или специалисту. Не забудьте также взять PDF-отчёт в разделе «Статистика» и показать его врачу: в нём ваши ответы после тренировок, оценённые ИИ, что поможет легче оценить ваши симптомы.",
            "لا يمكن للذكاء الاصطناعي أبدًا تشخيص مشكلة صحية حقيقية. للحصول على المعلومات الأدق وحل مشكلاتك الصحية، يُرجى استشارة أقرب طبيب أسرة أو أخصائي. ولا تنسَ أيضًا أخذ تقرير PDF من قسم الإحصائيات وإظهاره لطبيبك: فهو يتضمن إجاباتك بعد التمارين وقد قيّمها الذكاء الاصطناعي، مما قد يسهّل تقييم أعراضك.",
            "Künstliche Intelligenz kann niemals ein echtes Gesundheitsproblem diagnostizieren. Für die genauesten Informationen und zur Klärung gesundheitlicher Beschwerden wenden Sie sich bitte an Ihre nächste Hausarztpraxis oder an einen Facharzt. Denken Sie außerdem daran, den PDF-Bericht aus dem Bereich Statistiken Ihrem Arzt zu zeigen: Er enthält Ihre Antworten nach dem Training, von der KI ausgewertet, und kann die Einschätzung Ihrer Beschwerden erleichtern.",
            "인공지능은 실제 건강 문제를 절대 진단할 수 없습니다. 가장 정확한 정보를 얻고 건강 문제를 해결하려면 가까운 가정의학과 의사나 전문의와 상담하세요. 또한 통계 화면에서 PDF 보고서를 받아 의사에게 보여 주세요. 운동 후 답변이 AI로 평가되어 그대로 담겨 있어 증상을 더 쉽게 파악하는 데 도움이 될 수 있습니다."),
        row("ai.disclaimer.short",
            "Yapay zekâ teşhis koyamaz; lütfen aile hekiminize veya uzmanınıza danışın.",
            "AI cannot diagnose; please consult your family physician or a specialist.",
            "ИИ не ставит диагнозы; обратитесь к семейному врачу или специалисту.",
            "الذكاء الاصطناعي لا يشخّص؛ يُرجى استشارة طبيب الأسرة أو أخصائي.",
            "KI kann keine Diagnose stellen; bitte wenden Sie sich an Ihre Hausarztpraxis oder einen Facharzt.",
            "AI는 진단할 수 없습니다. 가정의학과 의사나 전문의와 상담하세요."),
        row("ai.badge",
            "Yapay zekâ yorumu", "AI comment", "Комментарий ИИ", "تعليق الذكاء الاصطناعي", "KI-Einschätzung", "AI 코멘트"),
        row("ai.sources",
            "Kaynaklar", "Sources", "Источники", "المصادر", "Quellen", "출처")
    )
}
