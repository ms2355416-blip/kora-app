package com.kora.challenge.util

/** نفس مولّد الموقع (daily.js) عشان نفس اليوم يدي نفس الأسئلة. */
class SeededRandom(seed: String) {
    private var a: Int

    init {
        var h = 1779033703 xor seed.length
        for (c in seed) {
            h = (h xor c.code) * 3432918353L.toInt()
            h = (h shl 13) or (h ushr 19)
        }
        a = h
    }

    fun next(): Double {
        a += 0x6D2B79F5
        var t = (a xor (a ushr 15)) * (1 or a)
        t = (t + (t xor (t ushr 7)) * (61 or t)) xor t
        return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL) / 4294967296.0
    }

    fun <T> shuffle(list: List<T>): List<T> {
        val arr = list.toMutableList()
        for (i in arr.size - 1 downTo 1) {
            val j = (next() * (i + 1)).toInt()
            val tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp
        }
        return arr
    }
}
