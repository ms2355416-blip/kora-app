package com.kora.challenge.ui.daily

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import com.kora.challenge.util.SeededRandom
import java.time.LocalDate

/** إعدادات تحدي اليوم (نفس قيم الموقع) */
object Daily {
    const val COUNT = 3
    const val PTS = 10
    const val ALL_BONUS = 10
    const val STREAK_PER_DAY = 2
    const val STREAK_MAX = 20
}

data class DailyQ(val q: String, val opts: List<String>, val answer: String)

/** نفس خوارزمية الموقع: نفس الأسئلة لكل الناس في اليوم. */
fun todaysQuestions(ctx: android.content.Context, today: String): List<DailyQ> {
    val qs = Questions.daily(ctx)
    val dayNo = LocalDate.parse(today).toEpochDay()
    val perCycle = (qs.size / Daily.COUNT).toLong()
    val cycle = Math.floorDiv(dayNo, perCycle).toInt()
    val order = SeededRandom("cycle-$cycle").shuffle(qs.indices.toList())
    val slot = Math.floorMod(dayNo, perCycle).toInt()
    return order.drop(slot * Daily.COUNT).take(Daily.COUNT).map { qi ->
        val q = qs[qi]
        DailyQ(q[0], SeededRandom("$today-$qi").shuffle(q.drop(1)), q[1])
    }
}

@Composable
fun DailyScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val today = remember { KoraStore.todayKey() }
    val todays = remember { todaysQuestions(ctx, today) }
    var picks by remember { mutableStateOf(KoraStore.loadDaily(today).picks) }
    var revealed by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding().padding(16.dp)
    ) {
        ScreenHeader("تحدي اليوم", onBack)
        val finished = !revealed && picks.size >= Daily.COUNT
        if (finished) {
            DailyResult(today, todays, onBack)
        } else {
            val i = if (revealed) picks.size - 1 else picks.size
            val t = todays[i]
            val score = picks.indices.count { picks[it] == todays[it].answer } * Daily.PTS
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("السؤال ${i + 1} من ${Daily.COUNT}", color = LocalKoraColors.current.muted)
                Text("النقاط: $score", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { i.toFloat() / Daily.COUNT }, modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary, trackColor = LocalKoraColors.current.border
            )
            Spacer(Modifier.height(20.dp))
            Text(t.q, fontSize = 21.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp)
            Spacer(Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                t.opts.forEach { o ->
                    val st = when {
                        !revealed -> OptState.Idle
                        o == t.answer -> OptState.Correct
                        o == picks[i] -> OptState.Wrong
                        else -> OptState.Dim
                    }
                    OptionButton(o, st, enabled = !revealed) {
                        picks = picks + o
                        KoraStore.saveDaily(KoraStore.loadDaily(today).copy(picks = picks))
                        revealed = true
                    }
                }
            }
            if (revealed) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = { revealed = false }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)) {
                    Text(if (picks.size >= Daily.COUNT) "شوف النتيجة" else "السؤال الجاي")
                }
            }
        }
    }
}

@Composable
private fun DailyResult(today: String, todays: List<DailyQ>, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val res = remember {
        KoraStore.finishDaily(today, todays.map { it.answer }, Daily.PTS, Daily.ALL_BONUS,
            Daily.STREAK_PER_DAY, Daily.STREAK_MAX)
    }
    val streak = remember { KoraStore.streak().days }
    val k = LocalKoraColors.current

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            when { res.right == Daily.COUNT -> "كله صح!"; res.right > 0 -> "كويس!"; else -> "مش النهارده" },
            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold
        )
        Text("${res.pts}", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary)
        Text("جاوبت ${res.right} من ${Daily.COUNT}", color = k.muted)
        Text((if (res.bonus > 0) "مكافأة السلسلة +${res.bonus} • " else "") + "سلسلتك: $streak يوم",
            color = k.muted, fontSize = 13.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))

        val picks = KoraStore.loadDaily(today).picks
        todays.forEachIndexed { i, t ->
            val ok = picks.getOrNull(i) == t.answer
            val c = if (ok) MaterialTheme.colorScheme.primary else KoraRed
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, c)) {
                Column(Modifier.padding(12.dp)) {
                    Text(t.q, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text((if (ok) "إجابتك صح: " else "الصح: ") + t.answer, color = c, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val text = "Kora Challenge — تحدي اليوم $today\nجاوبت ${res.right} من ${Daily.COUNT} • سلسلتي $streak يوم"
                val send = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }
                ctx.startActivity(Intent.createChooser(send, null))
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)
        ) { Text("شارك نتيجتك") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("الرئيسية")
        }
    }
}
