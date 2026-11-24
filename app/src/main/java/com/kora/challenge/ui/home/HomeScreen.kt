package com.kora.challenge.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kora.challenge.data.Game
import com.kora.challenge.data.Games
import com.kora.challenge.data.KoraStore
import com.kora.challenge.ui.theme.LocalKoraColors

@Composable
fun HomeScreen(dark: Boolean, onToggleTheme: () -> Unit, onOpenGame: (String) -> Unit) {
    // المرحلة 3: هتتزامن مع Firebase
    val gamesPlayed = KoraStore.stats.gamesPlayed
    val totalPoints = KoraStore.stats.totalPoints

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TopBar(dark, onToggleTheme) }
        item { Hero(onStart = { onOpenGame("trivia") }) }
        item { StatsRow(gamesPlayed, totalPoints) }
        item { DailyBanner(Games.daily) { onOpenGame(Games.daily.id) } }
        item {
            Text("التحديات الكروية", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
        }
        items(Games.all, key = { it.id }) { GameCard(it) { onOpenGame(it.id) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { MoreRow(onOpenGame) }
        item { Spacer(Modifier.navigationBarsPadding().height(16.dp)) }
    }
}

@Composable
private fun TopBar(dark: Boolean, onToggleTheme: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("KORA CHALLENGE", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        TextButton(onClick = onToggleTheme) { Text(if (dark) "☀️ فاتح" else "🌙 داكن") }
    }
}

@Composable
private fun Hero(onStart: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("عالم التحديات الكروية", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        Text("إنت فاهم كورة ولا مجرد مشجع؟", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        Text("اختبر معلوماتك الكروية، اكتشف مستواك، وتحدى نفسك في مجموعة من أقوى ألعاب كرة القدم.",
            color = LocalKoraColors.current.muted)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onStart, shape = RoundedCornerShape(14.dp)) { Text("ابدأ التحدي") }
    }
}

@Composable
private fun StatsRow(gamesPlayed: Int, totalPoints: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatBox("جولات لعبتها", gamesPlayed.toString(), Modifier.weight(1f))
        StatBox("مجموع النقاط", totalPoints.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, LocalKoraColors.current.border)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 12.sp, color = LocalKoraColors.current.muted)
        }
    }
}

@Composable
private fun DailyBanner(game: Game, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LocalKoraColors.current.surface2),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(game.emoji, fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(game.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(game.description, fontSize = 12.sp, color = LocalKoraColors.current.muted)
            }
            Text(game.cta, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GameCard(game: Game, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, LocalKoraColors.current.border)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(game.emoji, fontSize = 30.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(game.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Text(game.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                if (game.online) Text("ONLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Text(game.description, fontSize = 13.sp, color = LocalKoraColors.current.muted)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                game.chips.forEach { Chip(it) }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick, shape = RoundedCornerShape(12.dp)) { Text(game.cta) }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Box(Modifier.background(LocalKoraColors.current.surface2, RoundedCornerShape(50))
        .padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, fontSize = 11.sp, color = LocalKoraColors.current.muted)
    }
}

@Composable
private fun MoreRow(onOpenGame: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Games.extras.forEach { g ->
            OutlinedButton(onClick = { onOpenGame(g.id) }, modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)) { Text("${g.emoji} ${g.title}") }
        }
    }
}
