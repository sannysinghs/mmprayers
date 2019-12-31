package com.muslimlab.prayers.ui.prayers

import com.muslimlab.prayers.ui.prayers.model.PrayerName

data class AlarmPref (val prayerCode: PrayerName, val isAlarm : Boolean) {
    companion object {
        val DEFAULT = listOf(
            AlarmPref(PrayerName.fajr, false),
            AlarmPref(PrayerName.duhur, false),
            AlarmPref(PrayerName.asr, false),
            AlarmPref(PrayerName.maghrib, false),
            AlarmPref(PrayerName.isha, false),
            AlarmPref(PrayerName.imsak, false)
        )
    }
}
