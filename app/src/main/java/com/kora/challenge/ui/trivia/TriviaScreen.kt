package com.kora.challenge.ui.trivia

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kora.challenge.data.KoraStore
import com.kora.challenge.data.Questions
import com.kora.challenge.ui.common.KoraRed
import com.kora.challenge.ui.common.OptState
import com.kora.challenge.ui.common.OptionButton
import com.kora.challenge.ui.common.ScreenHeader
import com.kora.challenge.ui.theme.LocalKoraColors
import kotlinx.coroutines.delay
import kotlin.random.Random

/** إعدادات اللعبة (نفس قيم trivia.js) */
object Trivia {
    const val TOTAL = 10
    const val PTS = 10
    const val ROUND_S = 50
    const val WARN_S = 5
    const val REVEAL_MS = 3200L
    const val SITE_OK = 2      // نقطة الرصيد لكل إجابة صح ضد الكمبيوتر
    const val SITE_WIN = 10    // مكافأة الفوز
    val LETTERS = listOf("أ", "ب", "ج", "د", "هـ")
}

enum class Level(val label: String, val p: Double, val minS: Int, val maxS: Int) {
    Easy("سهل", 0.45, 6, 30), Mid("متوسط", 0.65, 4, 24), Hard("صعب", 0.85, 3, 16)
}

data class TOpt(val text: String, val ok: Boolean)
data class TQ(val text: String, val opts: List<TOpt>)

private fun pickQuestions(ctx: android.content.Context): List<TQ> {
    val bank = Questions.trivia(ctx)
    var seen = KoraStore.triviaSeen()
    var pool = bank.indices.filter { it !in seen }
    if (pool.size < Trivia.TOTAL) { seen = emptySet(); pool = bank.indices.toList() }
    val chosen = pool.shuffled().take(Trivia.TOTAL)
    KoraStore.setTriviaSeen(seen + chosen)
    return chosen.map { qi ->
        val q = bank[qi]
        TQ(q[0], q.drop(1).mapIndexed { i, t -> TOpt(t, i == 0) }.shuffled())
    }
}

private enum class Phase { Lobby, Play, End }

@Composable
fun TriviaScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var phase by remember { mutableStateOf(Phase.Lobby) }
    var level by remember { mutableStateOf(Level.Mid) }
    var qs by remember { mutableStateOf<List<TQ>>(emptyList()) }
    var result by remember { mutableStateOf(0 to 0) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding().padding(16.dp)
    ) {
        ScreenHeader("تحدي المعلومات الكروية", onBack)
        when (phase) {
            Phase.Lobby -> Lobby(level, { level = it }) {
                qs = pickQuestions(ctx); phase = Phase.Play
            }
            Phase.Play -> TriviaPlay(qs, level) { me, cpu -> result = me to cpu; phase = Phase.End }
            Phase.End -> End(result.first, result.second, onAgain = { phase = Phase.Lobby }, onBack = onBack)
        }
    }
}

@Composable
private fun Lobby(level: Level, onLevel: (Level) -> Unit, onStart: () -> Unit) {
    val k = LocalKoraColors.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Text("🧠", fontSize = 64.sp)
        Text("العب ضد الكمبيوتر", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("${Trivia.TOTAL} أسئلة و${Trivia.ROUND_S} ثانية لكل سؤال. جاوب أسرع وأصح من الكمبيوتر.",
            color = k.muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Text("مستوى الكمبيوتر", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Level.entries.forEach { l ->
                FilterChip(selected = l == level, onClick = { onLevel(l) }, label = { Text(l.label) })
            }
        }
        Spacer(Modifier.height(28.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("ابدأ")
        }
        Spacer(Modifier.height(8.dp))
        Text("اللعب مع صاحبك بكود غرفة هيتضاف في مرحلة الأونلاين.", color = k.muted, fontSize = 12.sp,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun TriviaPlay(qs: List<TQ>, level: Level, onFinish: (Int, Int) -> Unit) {
    var n by remember { mutableIntStateOf(0) }
    var myChoice by remember { mutableStateOf<Int?>(null) }
    var botChoice by remember { mutableStateOf<Int?>(null) }
    var reveal by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(Trivia.ROUND_S) }
    var me by remember { mutableIntStateOf(0) }
    var cpu by remember { mutableIntStateOf(0) }

    LaunchedEffect(n) {
        val q = qs[n]
        myChoice = null; botChoice = null; reveal = false; timeLeft = Trivia.ROUND_S
        val right = q.opts.indexOfFirst { it.ok }
        // الكمبيوتر أحيانًا مبيلحقش يجاوب (4%)
        val skip = Random.nextDouble() < 0.04
        val delayMs = (minOf((Trivia.ROUND_S - 2).toDouble(),
            level.minS + Random.nextDouble() * (level.maxS - level.minS)) * 1000).toLong()
        val botPick = if (Random.nextDouble() < level.p) right
        else q.opts.indices.filter { it != right }.random()
        val t0 = SystemClock.elapsedRealtime()
        while (true) {
            delay(100)
            val el = SystemClock.elapsedRealtime() - t0
            timeLeft = ((Trivia.ROUND_S * 1000L - el + 999) / 1000).toInt().coerceAtLeast(0)
            if (!skip && botChoice == null && el >= delayMs) botChoice = botPick
            if ((myChoice != null && botChoice != null) || el >= Trivia.ROUND_S * 1000L) break
        }
        reveal = true
        if (myChoice == right) me += Trivia.PTS
        if (botChoice == right) cpu += Trivia.PTS
        delay(Trivia.REVEAL_MS)
        if (n + 1 >= Trivia.TOTAL) onFinish(me, cpu) else n++
    }

    val q = qs[n]
    val right = q.opts.indexOfFirst { it.ok }
    val k = LocalKoraColors.current
    val warn = timeLeft <= Trivia.WARN_S && !reveal

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("سؤال ${n + 1} من ${Trivia.TOTAL}", color = k.muted)
            Text("⏱ $timeLeft", fontWeight = FontWeight.Bold, color = if (warn) KoraRed else MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { timeLeft.toFloat() / Trivia.ROUND_S }, modifier = Modifier.fillMaxWidth(),
            color = if (warn) KoraRed else MaterialTheme.colorScheme.primary, trackColor = k.border
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("👤 أنت: $me", fontWeight = FontWeight.Bold)
            Text("🤖 الكمبيوتر: $cpu", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text(q.text, fontSize = 21.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            q.opts.forEachIndexed { i, o ->
                val st = when {
                    reveal -> when { i == right -> OptState.Correct; i == myChoice -> OptState.Wrong; else -> OptState.Dim }
                    myChoice == i -> OptState.Selected
                    else -> OptState.Idle
                }
                val tag = buildString {
                    if (reveal && myChoice == i) append("👤")
                    if (reveal && botChoice == i) append("🤖")
                }.ifEmpty { null }
                OptionButton(o.text, st, enabled = myChoice == null && !reveal,
                    letter = Trivia.LETTERS.getOrNull(i), tag = tag) { myChoice = i }
            }
        }
        Spacer(Modifier.height(10.dp))
        val hint = when {
            reveal -> if (myChoice == right) "إجابة صح! ✓" else if (myChoice == null) "الوقت خلص" else "إجابة غلط"
            botChoice != null && myChoice == null -> "الكمبيوتر جاوب، إنت لسه!"
            myChoice != null -> "مستني الكمبيوتر..."
            else -> ""
        }
        Text(hint, color = k.muted, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun End(me: Int, cpu: Int, onAgain: () -> Unit, onBack: () -> Unit) {
    val rightCount = me / Trivia.PTS
    // النقاط بتتضاف مرة واحدة بس لما الشاشة تظهر
    val got = remember {
        KoraStore.addTriviaPoints(rightCount * Trivia.SITE_OK + if (me > cpu) Trivia.SITE_WIN else 0)
    }
    val k = LocalKoraColors.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Text(if (me > cpu) "🏆" else if (me < cpu) "😕" else "🤝", fontSize = 64.sp)
        Text(
            if (me > cpu) "مبروك! فزت 🎉" else if (me < cpu) "للأسف خسرت" else "تعادل!",
            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
            color = if (me > cpu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$me", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text("أنت", color = k.muted)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$cpu", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                Text("الكمبيوتر", color = k.muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("$rightCount إجابة صح من ${Trivia.TOTAL}" + if (got > 0) " • اتضاف $got نقطة لرصيدك" else "",
            color = k.muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        Button(onClick = onAgain, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("العب تاني") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("الرئيسية") }
    }
}
