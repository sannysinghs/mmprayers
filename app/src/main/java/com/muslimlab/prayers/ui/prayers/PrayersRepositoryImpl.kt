package com.muslimlab.prayers.ui.prayers

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muslimlab.prayers.model.PrayerResult
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit

const val KEY_TODAY_PRAYERS_DATA = "today_prayers_data"
const val PREF_KEY_PRAYERS_ALARM_DATA = "prayers_alarm_data"

class PrayersRepositoryImpl(
    private val prayersApi: PrayersApi,
    private val sharePref: SharedPreferences
) : PrayersRepository {
    override fun getAlarmPref(): Single<List<AlarmPref>> = Single.fromCallable {
        val prefTypeToken = object : TypeToken<List<String>>() {}.type

        sharePref.getString(PREF_KEY_PRAYERS_ALARM_DATA, null)?.let {
            Gson().fromJson<List<AlarmPref>>(it, prefTypeToken)
        } ?: AlarmPref.DEFAULT
    }

    override fun todayPrayers(
        city: String,
        country: String,
        method: Int
    ): Observable<PrayerResult> = Observable.fromCallable {
        sharePref.getString(KEY_TODAY_PRAYERS_DATA, null)?.let {
            Gson().fromJson(it, PrayerResult::class.java)
        } ?: PrayerResult.EMPTY
    }.mergeWith(
        prayersApi.todayPrayers(city, country, method)
            .map {
                sharePref.edit {
                    putString(KEY_TODAY_PRAYERS_DATA, Gson().toJson(it) )
                }
                it
            }
    ).filter {
        it != PrayerResult.EMPTY
    }
}


object PrayerRepositoryFactory {
    fun createNetworkPrayerRepository(
        sharePref: SharedPreferences,
        retrofit: Retrofit
    ): PrayersRepository =
        PrayersRepositoryImpl(retrofit.create(PrayersApi::class.java), sharePref)
}