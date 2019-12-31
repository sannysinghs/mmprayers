package com.muslimlab.prayers.ui.prayers

import com.muslimlab.prayers.model.PrayerResult
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import io.reactivex.Observable
import io.reactivex.Single

interface PrayersRepository {
    fun todayPrayers(city: String = "yangon", country: String = "mm", method: Int = 1): Observable<PrayerResult>
    fun getAlarmPref(): Single<Map<PrayerName, Boolean>>
    fun saveAlarmPref(name: Boolean?)
}
