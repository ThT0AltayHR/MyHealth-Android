package com.myhealth.app.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.myhealth.app.R
import com.myhealth.app.core.Lang
import com.myhealth.app.ui.LocalLang

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val InterDisplayFamily = FontFamily(
    Font(R.font.inter_display_semibold, FontWeight.SemiBold),
    Font(R.font.inter_display_bold, FontWeight.Bold)
)

/**
 * Yazi stilleri. Basliklar Poppins (Rusca'da Kiril destekli Inter Display), govde Inter.
 * Arapca/Korece glifler icin sistem yedegi otomatik devreye girer.
 */
enum class Sty(val size: Float, val weight: FontWeight, val heading: Boolean, val line: Float, val spacing: Float) {
    DISPLAY(36f, FontWeight.Medium, true, 42f, -0.6f),
    H1(28f, FontWeight.Medium, true, 34f, -0.4f),
    H2(22f, FontWeight.Medium, true, 28f, -0.2f),
    H3(18f, FontWeight.Medium, true, 24f, 0f),
    NUMBER(32f, FontWeight.Medium, true, 38f, -0.5f),
    TITLE(16f, FontWeight.SemiBold, false, 22f, 0f),
    BODY(14f, FontWeight.Normal, false, 21f, 0f),
    BODY_STRONG(14f, FontWeight.Medium, false, 21f, 0f),
    LABEL(13f, FontWeight.Medium, false, 18f, 0.1f),
    BUTTON(15f, FontWeight.SemiBold, false, 20f, 0.2f),
    SMALL(12f, FontWeight.Normal, false, 17f, 0f),
    CAPTION(11f, FontWeight.Medium, false, 15f, 0.3f)
}

fun familyFor(sty: Sty, lang: Lang): FontFamily =
    if (sty.heading) (if (lang == Lang.RU) InterDisplayFamily else PoppinsFamily) else InterFamily

/** TextStyle gereken yerler (BasicTextField vb.) icin. */
fun luxTextStyle(sty: Sty, lang: Lang, color: Color): TextStyle = TextStyle(
    color = color,
    fontSize = sty.size.sp,
    fontWeight = sty.weight,
    fontFamily = familyFor(sty, lang),
    lineHeight = sty.line.sp,
    // Arapca'da sifir olmayan harf araligi baglantili yazimi bozar.
    letterSpacing = if (lang.rtl) 0.sp else sty.spacing.sp
)

/** Uygulamanin tek metin bileseni. */
@Composable
fun Txt(
    text: String,
    modifier: Modifier = Modifier,
    sty: Sty = Sty.BODY,
    color: Color = Lux.text,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val lang = LocalLang.current
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = sty.size.sp,
        fontWeight = sty.weight,
        fontFamily = familyFor(sty, lang),
        lineHeight = sty.line.sp,
        letterSpacing = if (lang.rtl) 0.sp else sty.spacing.sp,
        textAlign = align,
        maxLines = maxLines,
        overflow = overflow
    )
}
