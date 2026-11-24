package com.kora.challenge.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kora.challenge.ui.theme.LocalKoraColors

val KoraRed = Color(0xFFE5484D)

enum class OptState { Idle, Selected, Correct, Wrong, Dim }

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack, modifier = Modifier.width(88.dp)) { Text("→ رجوع") }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f))
        Spacer(Modifier.width(88.dp))
    }
}

@Composable
fun OptionButton(
    text: String, state: OptState, enabled: Boolean, letter: String? = null, tag: String? = null,
    onClick: () -> Unit
) {
    val k = LocalKoraColors.current
    val green = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val (bc, bg) = when (state) {
        OptState.Idle, OptState.Dim -> k.border to surface
        OptState.Selected -> green to k.surface2
        OptState.Correct -> green to green.copy(alpha = .18f)
        OptState.Wrong -> KoraRed to KoraRed.copy(alpha = .18f)
    }
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().alpha(if (state == OptState.Dim) .55f else 1f)
            .clip(shape).background(bg).border(1.5.dp, bc, shape)
            .clickable(enabled = enabled, onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (letter != null) {
            Text(letter, color = green, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
        }
        Text(text, fontSize = 15.sp, modifier = Modifier.weight(1f))
        if (tag != null) Text(tag, fontSize = 14.sp)
    }
}
