package com.myhealth.app.ui.kit

import com.myhealth.app.data.SportCategory

/**
 * 57 spor ve 8 kategori icin SVG yol verileri (24x24 goruntu kutusu, cizgi stili; Ic ile ayni dil).
 * LuxIcon(SportIcons.sport(id)) seklinde cizilir. Bilinmeyen kimlik icin genel bir ikon doner.
 * TASLAK: Ikon modulu gercek cizimleri doldurur; imzalar SABITTIR.
 */
object SportIcons {
    fun sport(id: String): String = Ic.SPORTS

    fun category(c: SportCategory): String = Ic.SPORTS
}
