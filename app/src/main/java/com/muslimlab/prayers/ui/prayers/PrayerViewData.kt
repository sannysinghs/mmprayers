package com.muslimlab.prayers.ui.prayers

import com.muslimlab.prayers.ui.prayers.model.PrayerName

data class PrayerViewItem(
    val prayerCode: String,
    val name: String,
    var isAlarmOn: Boolean
)

data class AlarmPref (val prayerCode: PrayerName, val isAlarm : Boolean) {
    companion object {
        val DEFAULT = listOf(
            AlarmPref(PrayerName.FAJR, false),
            AlarmPref(PrayerName.DHUHR, false),
            AlarmPref(PrayerName.ASR, false),
            AlarmPref(PrayerName.MAGHRIB, false),
            AlarmPref(PrayerName.ISHA, false),
            AlarmPref(PrayerName.IMSAK, false)
        )
    }
}
