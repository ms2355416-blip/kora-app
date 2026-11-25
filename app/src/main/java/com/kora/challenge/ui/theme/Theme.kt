package com.kora.challenge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** ألوان إضافية مش موجودة في Material، كلها من style.css في الموقع */
data class KoraColors(
    val dark: Boolean,
    val muted: Color, val border: Color, val surface2: Color, val greenDark: Color,
    val eyebrowBg: Color, val eyebrowBorder: Color, val statIconBg: Color,
    val btnBg: Color, val btnBorder: Color, val btnText: Color,
    val bannerStart: Color, val bannerBorder: Color, val metaText: Color
)

val Gold = Color(0xFFF6C445)

private val DarkExtra = KoraColors(
    true, DarkMuted, DarkBorder, DarkSurface2, DarkGreenDark,
    Color(0xFF10251A), Color(0xFF1E4930), Color(0xFF1A3022),
    Color(0xFF14251A), Color(0xFF2B5237), DarkGreen,
    Color(0xFF132B1C), Color(0xFF285237), Color(0xFFB4C5B9)
)
private val LightExtra = KoraColors(
    false, LightMuted, LightBorder, LightSurface2, LightGreenDark,
    Color(0xFFE4F5E9), Color(0xFFC4E9D0), Color(0xFFE4F5E9),
    Color(0xFFEAF7EE), Color(0xFFC5E7D0), Color(0xFF08783D),
    Color(0xFFE4F6E9), Color(0xFFC5E7D0), Color(0xFF4F6358)
)

val LocalKoraColors = staticCompositionLocalOf { DarkExtra }

private val DarkScheme = darkColorScheme(
    primary = DarkGreen, onPrimary = Color(0xFF06140B),
    background = DarkBg, onBackground = DarkText,
    surface = DarkSurface, onSurface = DarkText
)
private val LightScheme = lightColorScheme(
    primary = LightGreen, onPrimary = Color.White,
    background = LightBg, onBackground = LightText,
    surface = LightSurface, onSurface = LightText
)

@Composable
fun KoraTheme(dark: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalKoraColors provides (if (dark) DarkExtra else LightExtra),
        LocalLayoutDirection provides LayoutDirection.Rtl // الموقع كله RTL
    ) {
        MaterialTheme(colorScheme = if (dark) DarkScheme else LightScheme, content = content)
    }
}
