package com.muslimlab.prayers.ui.prayers.model

data class PrayerItem(val name: PrayerName, val time: String, val isAlarmOn: Boolean)

enum class PrayerName {
    IMSAK,
    FAJR,
    SUNRISE,
    DHUHR,
    ASR,
    MAGHRIB,
    ISHA
}
/*


"Asr": "15:24 (+0630)",
"Sunset": "18:19 (+0630)",
"Maghrib": "18:19 (+0630)",
"Isha": "19:19 (+0630)",
"Imsak": "04:41 (+0630)",
"Midnight": "00:05 (+0630)"


        */
