package com.myhealth.app.feature.meals

import com.myhealth.app.core.Lang

/**
 * YEMEK VERITABANI ARAYUZU (SABIT SOZLESME). Meals ajani govdeyi ve 300+ yemegi doldurur;
 * asistan (beyin) ve diger moduller yalnizca bu imzalari kullanir.
 * Yemek adi ceviri anahtari: "food.<id>", kategori adi: "foodcat.<id>" (StrFoods tablosu).
 */
enum class FoodCategory(val id: String) {
    GRAINS("grains"), BREAD("bread"), MEAT("meat"), POULTRY("poultry"), FISH("fish"),
    DAIRY("dairy"), EGGS("eggs"), VEGETABLES("vegetables"), FRUITS("fruits"), LEGUMES("legumes"),
    NUTS("nuts"), SWEETS("sweets"), DRINKS("drinks"), SOUPS("soups"), DISHES("dishes"),
    FASTFOOD("fastfood"), SNACKS("snacks"), OILS("oils");

    val nameKey: String get() = "foodcat.$id"
}

/** Degerler 100 g (icecekler icin 100 ml) basinadir; portionG varsayilan bir porsiyonun gramidir. */
data class Food(
    val id: String,
    val category: FoodCategory,
    val kcal: Int,
    val protein: Double,
    val carb: Double,
    val fat: Double,
    val portionG: Int
) {
    val nameKey: String get() = "food.$id"
}

object FoodDb {
    val all: List<Food> = emptyList()

    fun byId(id: String): Food? = all.firstOrNull { it.id == id }

    fun byCategory(c: FoodCategory): List<Food> = all.filter { it.category == c }

    /** Verilen dildeki ve diger dillerdeki adlarda (kucuk harf, aksan duyarsiz) arar. */
    fun search(query: String, lang: Lang, limit: Int = 40): List<Food> = emptyList()
}
