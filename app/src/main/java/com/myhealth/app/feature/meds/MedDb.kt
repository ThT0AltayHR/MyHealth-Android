package com.myhealth.app.feature.meds

import com.myhealth.app.core.Lang
import com.myhealth.app.data.UserProfile

/*
 * ILAC VERITABANI ARAYUZU (SABIT SOZLESME; govdeyi Ilac ajani doldurur). Halk arasinda cok bilinen
 * 120+ ilac (Turkiye pazari: Parol, Majezik, Apranax, Buscopan, Andolor, Augmentin, Amoklavin,
 * Nexium, Lansor, Gaviscon, Zyrtec, Ventolin, Euthyrox, Glucophage, Coraspin, D vitamini damlalari...).
 * Marka adi (brand) ozel isimdir, cevrilmez. Kisa amac metni ceviri anahtari: med.<id> (6 dil, tek cumle);
 * kategori adi: medcat.<id>. Bilgi amaclidir; doz onerisi VERILMEZ.
 * flags sozlugu (Ilac ajani genisletebilir): gi_risk, asthma_risk, kidney_risk, liver_risk, bp_raise,
 * drowsy, pregnancy_caution, penicillin, alcohol_avoid, bleeding_risk, driving_caution.
 */
enum class MedCategory(val id: String) {
    PAIN_FEVER("pain_fever"), NSAID("nsaid"), ANTIBIOTIC("antibiotic"), GASTRO("gastro"), COLD_FLU("cold_flu"),
    ALLERGY("allergy"), RESPIRATORY("respiratory"), CARDIO("cardio"), DIABETES("diabetes"), THYROID("thyroid"),
    VITAMIN("vitamin"), SLEEP_CALM("sleep_calm"), ANTISPASMODIC("antispasmodic"), TOPICAL("topical"), OTHER("other");

    val nameKey: String get() = "medcat.$id"
}

data class Med(
    val id: String,
    val brand: String,
    val generic: String,
    val category: MedCategory,
    val flags: Set<String> = emptySet()
) {
    val purposeKey: String get() = "med.$id"
}

/** level: 1 = bilgi, 2 = dikkat, 3 = doktora/eczaciya danisin. text zaten yerellestirilmistir. */
data class MedWarning(val medId: String, val text: String, val level: Int)

object MedDb {
    val all: List<Med> = emptyList()

    fun byId(id: String): Med? = all.firstOrNull { it.id == id }

    /** Marka, etken madde ve (aksan duyarsiz) kategori adinda arar. */
    fun search(query: String, limit: Int = 50): List<Med> = emptyList()

    /** Kullanicinin hastalik/alerjilerine ve ilaclarin kendi aralarindaki ortak risklere gore uyarilar. */
    fun warnings(p: UserProfile, medIds: Collection<String>, lang: Lang): List<MedWarning> = emptyList()
}
