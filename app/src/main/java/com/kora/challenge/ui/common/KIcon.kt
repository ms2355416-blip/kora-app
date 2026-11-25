package com.kora.challenge.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

/** أيقونات الموقع (SVG) اتحولت لـ res/drawable. tint=null يعني ألوانها الأصلية. */
@Composable
fun KIcon(@DrawableRes id: Int, size: Dp, tint: Color? = null, modifier: Modifier = Modifier) {
    Icon(painterResource(id), contentDescription = null, modifier = modifier.size(size),
        tint = tint ?: Color.Unspecified)
}
