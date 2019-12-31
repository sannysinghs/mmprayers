package com.muslimlab.prayers.ui.prayers

import com.google.gson.Gson
import com.muslimlab.prayers.model.PrayerResult
import com.muslimlab.prayers.model.Prayers
import io.reactivex.Observable
import io.reactivex.Single
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

const val FILE_PRAYERS = "/prayers.json"
val PRAYERS_API_READABLE_FORMAT = SimpleDateFormat("DD-MM-YYYY", Locale.ENGLISH)

fun InputStream.asString(): String {
    val reader = BufferedReader(InputStreamReader(this))
    val sb = StringBuilder()
    var line: String? = ""
    try {
        do {
            line = reader.readLine()
            line?.run {
                sb.append(this)
                    .append("\n")
            }

        } while (line != null)

    } catch (e: IOException) {
        e.printStackTrace()
    }

    try {
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return sb.toString()
}