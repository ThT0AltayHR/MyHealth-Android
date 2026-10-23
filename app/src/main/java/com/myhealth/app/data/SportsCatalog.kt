package com.myhealth.app.data

enum class SportCategory(val id: String) {
    CARDIO("cardio"), STRENGTH("strength"), FLEXIBILITY("flex"), OUTDOOR("outdoor"),
    COMBAT("combat"), WATER("water"), BALL("ball"), WINTER("winter");

    /** Ceviri anahtari: cat.<id> */
    val nameKey: String get() = "cat.$id"
}

/**
 * motion: 3B sahnedeki hareket kimligi (assets/web3d/athlete.js ile birebir ayni adlar).
 * prop:   sahnede gosterilecek ekipman kimligi ("none" = yok).
 * gps:    konum takibi ve harita kullanir.
 */
data class Sport(
    val id: String,
    val category: SportCategory,
    val met: Double,
    val gps: Boolean,
    val motion: String,
    val prop: String = "none"
) {
    /** Ceviri anahtari: sport.<id> */
    val nameKey: String get() = "sport.$id"
}

/** MET degerleri: Compendium of Physical Activities. 57 aktivite, 8 kategori. */
object SportsCatalog {
    val all: List<Sport> = listOf(
        // --- kardiyo ---
        Sport("run", SportCategory.CARDIO, 9.8, true, "run"),
        Sport("treadmill", SportCategory.CARDIO, 8.3, false, "run", "treadmill"),
        Sport("walk", SportCategory.CARDIO, 3.8, true, "walk"),
        Sport("cycling", SportCategory.CARDIO, 7.5, true, "cycle", "bike"),
        Sport("spinning", SportCategory.CARDIO, 6.8, false, "cycle", "bike"),
        Sport("jump_rope", SportCategory.CARDIO, 11.0, false, "jumprope", "rope"),
        Sport("elliptical", SportCategory.CARDIO, 5.0, false, "walk"),
        Sport("rowing_machine", SportCategory.CARDIO, 7.0, false, "rowmachine", "rower"),
        Sport("stair_climb", SportCategory.CARDIO, 8.8, false, "stairs", "stairs"),
        Sport("hiit", SportCategory.CARDIO, 8.0, false, "jacks"),
        Sport("aerobics", SportCategory.CARDIO, 6.5, false, "jacks"),
        Sport("dance", SportCategory.CARDIO, 5.5, false, "dance"),
        // --- guc ---
        Sport("weights", SportCategory.STRENGTH, 6.0, false, "press", "barbell"),
        Sport("bodyweight", SportCategory.STRENGTH, 4.5, false, "squat"),
        Sport("crossfit", SportCategory.STRENGTH, 8.0, false, "swing", "kettlebell"),
        Sport("kettlebell", SportCategory.STRENGTH, 9.8, false, "swing", "kettlebell"),
        Sport("powerlifting", SportCategory.STRENGTH, 6.0, false, "deadlift", "barbell"),
        Sport("calisthenics", SportCategory.STRENGTH, 5.0, false, "pullup", "bar"),
        Sport("pushup", SportCategory.STRENGTH, 3.8, false, "pushup"),
        Sport("plank", SportCategory.STRENGTH, 3.0, false, "plank"),
        // --- esneklik ---
        Sport("yoga", SportCategory.FLEXIBILITY, 2.5, false, "yoga", "mat"),
        Sport("pilates", SportCategory.FLEXIBILITY, 3.0, false, "pilates", "mat"),
        Sport("stretching", SportCategory.FLEXIBILITY, 2.3, false, "stretch", "mat"),
        Sport("tai_chi", SportCategory.FLEXIBILITY, 3.0, false, "taichi"),
        Sport("barre", SportCategory.FLEXIBILITY, 3.5, false, "dance"),
        // --- acik hava ---
        Sport("hiking", SportCategory.OUTDOOR, 6.0, true, "walk", "poles"),
        Sport("trail_run", SportCategory.OUTDOOR, 10.0, true, "run"),
        Sport("mtb", SportCategory.OUTDOOR, 8.5, true, "cycle", "bike"),
        Sport("climbing", SportCategory.OUTDOOR, 8.0, false, "climb", "wall"),
        Sport("inline_skating", SportCategory.OUTDOOR, 7.0, true, "skate", "skates"),
        Sport("skateboard", SportCategory.OUTDOOR, 5.0, true, "skateboard", "board"),
        Sport("golf", SportCategory.OUTDOOR, 4.8, true, "golf", "club"),
        // --- dovus ---
        Sport("boxing", SportCategory.COMBAT, 9.0, false, "box"),
        Sport("kickboxing", SportCategory.COMBAT, 10.0, false, "kick"),
        Sport("muay_thai", SportCategory.COMBAT, 10.0, false, "kick"),
        Sport("karate", SportCategory.COMBAT, 10.0, false, "karate"),
        Sport("judo", SportCategory.COMBAT, 10.0, false, "karate"),
        Sport("taekwondo", SportCategory.COMBAT, 10.0, false, "kick"),
        Sport("wrestling", SportCategory.COMBAT, 6.0, false, "karate"),
        // --- su ---
        Sport("swimming", SportCategory.WATER, 8.0, false, "swim", "pool"),
        Sport("open_water", SportCategory.WATER, 9.0, true, "swim", "pool"),
        Sport("kayak", SportCategory.WATER, 5.0, true, "paddle", "kayak"),
        Sport("surf", SportCategory.WATER, 3.0, true, "surf", "surfboard"),
        Sport("rowing", SportCategory.WATER, 6.0, true, "paddle", "rowboat"),
        Sport("water_polo", SportCategory.WATER, 10.0, false, "swim", "pool"),
        // --- top sporlari ---
        Sport("football", SportCategory.BALL, 7.0, true, "football", "football"),
        Sport("basketball", SportCategory.BALL, 6.5, false, "dribble", "basketball"),
        Sport("volleyball", SportCategory.BALL, 4.0, false, "volley", "volleyball"),
        Sport("tennis", SportCategory.BALL, 7.3, false, "tennis", "racket"),
        Sport("table_tennis", SportCategory.BALL, 4.0, false, "tennis", "paddle"),
        Sport("badminton", SportCategory.BALL, 5.5, false, "tennis", "racket"),
        Sport("handball", SportCategory.BALL, 8.0, false, "dribble", "handball"),
        // --- kis sporlari ---
        Sport("skiing", SportCategory.WINTER, 7.0, true, "ski", "skis"),
        Sport("snowboard", SportCategory.WINTER, 5.3, true, "ski", "snowboard"),
        Sport("xc_ski", SportCategory.WINTER, 9.0, true, "ski", "skis"),
        Sport("ice_skating", SportCategory.WINTER, 7.0, false, "skate", "skates"),
        Sport("hockey", SportCategory.WINTER, 8.0, false, "skate", "stick")
    )

    private val index: Map<String, Sport> = all.associateBy { it.id }

    fun byId(id: String): Sport? = index[id]

    fun byCategory(c: SportCategory): List<Sport> = all.filter { it.category == c }

    /** Kosu, yuruyus ve bisiklet: hareket algilama bildirimi bu ucuyle eslesir. */
    val runId = "run"
    val walkId = "walk"
    val cycleId = "cycling"
}
