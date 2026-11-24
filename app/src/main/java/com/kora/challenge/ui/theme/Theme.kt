package com.kora.challenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection

/** ألوان إضافية مش موجودة في Material (الحدود والنص الباهت) */
data class KoraColors(val muted: Color, val border: Color, val surface2: Color, val greenDark: Color)

val LocalKoraColors = staticCompositionLocalOf {
    KoraColors(DarkMuted, DarkBorder, DarkSurface2, DarkGreenDark)
}

private val DarkScheme = darkColorScheme(
    primary = DarkGreen, onPrimary = DarkBg,
    background = DarkBg, onBackground = DarkText,
    surface = DarkSurface, onSurface = DarkText
)
private val LightScheme = lightColorScheme(
    primary = LightGreen, onPrimary = Color.White,
    background = LightBg, onBackground = LightText,
    surface = LightSurface, onSurface = LightText
)

@Composable
fun KoraTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val extra = if (dark) KoraColors(DarkMuted, DarkBorder, DarkSurface2, DarkGreenDark)
    else KoraColors(LightMuted, LightBorder, LightSurface2, LightGreenDark)
    CompositionLocalProvider(
        LocalKoraColors provides extra,
        LocalLayoutDirection provides LayoutDirection.Rtl // الموقع كله RTL
    ) {
        MaterialTheme(colorScheme = if (dark) DarkScheme else LightScheme, content = content)
    }
}
