package com.kora.challenge.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kora.challenge.data.Games
import com.kora.challenge.ui.theme.LocalKoraColors

/** شاشة مؤقتة لحد ما كل لعبة تتحول في المراحل الجاية */
@Composable
fun ComingSoonScreen(gameId: String, onBack: () -> Unit) {
    val game = Games.byId(gameId)
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        KIcon(game?.icon ?: com.kora.challenge.R.drawable.ic_ball, 72.dp,
            tint = if (game?.tintIcon == false) null else MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        Text(game?.title ?: "لعبة", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "الشاشة دي لسه جاية في المرحلة الجاية",
            color = LocalKoraColors.current.muted, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) { Text("رجوع") }
    }
}


