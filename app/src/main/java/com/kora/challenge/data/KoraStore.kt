package com.kora.challenge.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

data class Stats(val gamesPlayed: Int = 0, val totalPoints: Int = 0, val bestScore: Int = 0)
data class Streak(val days: Int, val lastDate: String, val playedToday: Boolean)
data class DailyState(
    val date: String, val picks: List<String> = emptyList(), val done: Boolean = false,
    val pts: Int = 0, val bonus: Int = 0, val right: Int = 0
)

/** التخزين المحلي (بديل localStorage في الموقع). المرحلة 3: هنربطه بـ Firebase. */
object KoraStore {
    private lateinit var sp: SharedPreferences
    var stats by mutableStateOf(Stats())
        private set

    fun init(ctx: Context) {
        if (::sp.isInitialized) return
        sp = ctx.applicationContext.getSharedPreferences("kora", Context.MODE_PRIVATE)
        stats = Stats(sp.getInt("games", 0), sp.getInt("points", 0), sp.getInt("best", 0))
    }

    fun todayKey(): String = LocalDate.now().toString() // yyyy-MM-dd بتوقيت الجهاز

    private fun saveStats(s: Stats) {
        stats = s
        sp.edit().putInt("games", s.gamesPlayed).putInt("points", s.totalPoints).putInt("best", s.bestScore).apply()
    }

    // ---------- السلسلة (streak.js) ----------
    private fun dayNo(s: String): Long? = runCatching { LocalDate.parse(s).toEpochDay() }.getOrNull()

    fun streak(): Streak {
        val days = sp.getInt("streak_days", 0)
        val last = sp.getString("streak_last", "") ?: ""
        if (days == 0) return Streak(0, last, false)
        val a = dayNo(todayKey()); val b = dayNo(last)
        if (a == null || b == null) return Streak(days, last, true)
        val diff = a - b
        if (diff < 0) return Streak(days, last, true) // ساعة الجهاز اتغيرت
        if (diff > 1) return Streak(0, last, false)
        return Streak(days, last, diff == 0L)
    }

    fun touchStreak(): Streak {
        val cur = streak()
        if (cur.playedToday && cur.days > 0) return cur
        val today = todayKey()
        val next = cur.days + 1
        sp.edit().putInt("streak_days", next).putString("streak_last", today).apply()
        return Streak(next, today, true)
    }

    // ---------- تحدي اليوم ----------
    fun loadDaily(today: String): DailyState {
        val raw = sp.getString("daily", null) ?: return DailyState(today)
        return runCatching {
            val o = JSONObject(raw)
            if (o.getString("date") != today) return DailyState(today)
            val p = o.getJSONArray("picks")
            DailyState(today, List(p.length()) { p.getString(it) }, o.optBoolean("done"),
                o.optInt("pts"), o.optInt("bonus"), o.optInt("right"))
        }.getOrDefault(DailyState(today))
    }

    fun saveDaily(s: DailyState) {
        val o = JSONObject().put("date", s.date).put("picks", JSONArray(s.picks))
            .put("done", s.done).put("pts", s.pts).put("bonus", s.bonus).put("right", s.right)
        sp.edit().putString("daily", o.toString()).apply()
    }

    /** بتحسب النتيجة وتزود النقاط مرة واحدة بس في اليوم. */
    fun finishDaily(today: String, answers: List<String>, perQ: Int, allBonus: Int, streakPer: Int, streakMax: Int): DailyState {
        val s = loadDaily(today)
        if (s.done) return s
        val right = s.picks.withIndex().count { (i, p) -> answers.getOrNull(i) == p }
        val st = touchStreak()
        var pts = right * perQ
        if (right == answers.size) pts += allBonus
        val bonus = if (right > 0) minOf(st.days * streakPer, streakMax) else 0
        pts += bonus
        val done = s.copy(done = true, pts = pts, bonus = bonus, right = right)
        saveDaily(done)
        saveStats(Stats(stats.gamesPlayed + 1, stats.totalPoints + pts, maxOf(stats.bestScore, pts)))
        return done
    }

    // ---------- تحدي المعلومات ----------
    fun triviaSeen(): Set<Int> =
        (sp.getString("trivia_seen", "") ?: "").split(",").mapNotNull { it.toIntOrNull() }.toSet()

    fun setTriviaSeen(s: Set<Int>) {
        sp.edit().putString("trivia_seen", s.toList().takeLast(250).joinToString(",")).apply()
    }

    /** بترجّع النقاط اللي اتضافت فعلًا (فيه حد يومي 500). */
    fun addTriviaPoints(raw: Int, dailyCap: Int = 500): Int {
        val day = todayKey()
        val used = if (sp.getString("trivia_day", "") == day) sp.getInt("trivia_pts", 0) else 0
        val ok = maxOf(0, minOf(raw, dailyCap - used))
        sp.edit().putString("trivia_day", day).putInt("trivia_pts", used + ok).apply()
        saveStats(stats.copy(totalPoints = stats.totalPoints + ok, bestScore = maxOf(stats.bestScore, ok)))
        return ok
    }
}
