package com.myhealth.app.data

/**
 * Hastalik ve alerji kimlikleri. Ceviri anahtarlari: health.cond.<id> ve health.allergy.<id>
 * (StrHealth tablosu her kimlik icin 6 dilde ad icermelidir). UserProfile.conditions / allergies
 * kumeleri bu kimlikleri tutar; asistan ve ayarlar da ayni kimlikleri kullanir.
 */
object HealthLists {
    val conditions: List<String> = listOf(
        "diabetes_t1", "diabetes_t2", "prediabetes", "hypertension", "hypotension",
        "high_cholesterol", "heart_disease", "arrhythmia", "asthma", "copd",
        "allergic_rhinitis", "hypothyroid", "hyperthyroid", "pcos", "obesity",
        "underweight", "anemia", "osteoporosis", "arthritis", "back_pain",
        "knee_problem", "migraine", "epilepsy", "depression", "anxiety",
        "insomnia", "ibs", "gastritis", "reflux", "celiac",
        "lactose_intolerance", "kidney_disease", "fatty_liver", "gout", "sleep_apnea",
        "eczema", "psoriasis", "vitamin_d_deficiency", "pregnancy", "gallstones"
    )

    val allergies: List<String> = listOf(
        "peanut", "tree_nuts", "milk", "egg", "wheat_gluten", "soy", "fish", "shellfish",
        "sesame", "mustard", "celery", "lupin", "sulfites", "corn", "strawberry", "tomato",
        "citrus", "honey", "kiwi", "banana", "pollen", "dust_mite", "pet_dander", "mold",
        "latex", "penicillin", "nickel", "insect_sting"
    )

    fun conditionKey(id: String): String = "health.cond.$id"

    fun allergyKey(id: String): String = "health.allergy.$id"
}
