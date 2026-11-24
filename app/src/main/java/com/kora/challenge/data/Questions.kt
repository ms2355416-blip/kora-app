package com.kora.challenge.data

import android.content.Context
import org.json.JSONArray

/** الأسئلة بتتحمل من assets (daily.json / trivia.json).
 *  كل سؤال: [النص، الإجابة الصح، غلط1، غلط2، ...] — زوّد أسئلة بتعديل الملفات دي. */
object Questions {
    private var daily: List<List<String>>? = null
    private var trivia: List<List<String>>? = null

    fun daily(ctx: Context) = daily ?: load(ctx, "daily.json").also { daily = it }
    fun trivia(ctx: Context) = trivia ?: load(ctx, "trivia.json").also { trivia = it }

    private fun load(ctx: Context, file: String): List<List<String>> {
        val text = ctx.assets.open(file).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val arr = JSONArray(text)
        return List(arr.length()) { i ->
            val q = arr.getJSONArray(i)
            List(q.length()) { j -> q.getString(j) }
        }
    }
}
