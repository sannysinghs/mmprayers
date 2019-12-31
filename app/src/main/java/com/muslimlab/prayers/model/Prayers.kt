package com.muslimlab.prayers.model

import com.google.gson.annotations.SerializedName

data class PrayerResult(
    @SerializedName("data") val data: PrayersData
) {
    companion object {
        val EMPTY = PrayerResult(
            PrayersData(Prayers("", "", "", "", "", "", "", ""),
                PrayersDate(0, ""))
        )
    }
}

data class PrayersData(
    val timings: Prayers,
    val date: PrayersDate
)

data class PrayersDate(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("readable") val readable: String

)

data class Prayers(
    @SerializedName("Fajr") val fajir: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Sunset") val sunset: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
    @SerializedName("Imsak") val imsak: String
)