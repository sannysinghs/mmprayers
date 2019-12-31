package com.muslimlab.prayers.ui.prayers

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muslimlab.prayers.model.PrayerResult
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit

const val KEY_TODAY_PRAYERS_DATA = "today_prayers_data"
const val PREF_KEY_PRAYERS_ALARM_DATA = "prayers_alarm_data"

class PrayersRepositoryImpl(
    private val prayersApi: PrayersApi,
    private val sharePref: SharedPreferences
) : PrayersRepository {
    override fun getAlarmPref(): Single<Map<PrayerName, Boolean>> = Single.fromCallable {
        mapOf(
            PrayerName.fajr to sharePref.getBoolean(PrayerName.fajr.name, false) ,
            PrayerName.asr to sharePref.getBoolean(PrayerName.asr.name, false),
            PrayerName.duhur to sharePref.getBoolean(PrayerName.duhur.name, false) ,
            PrayerName.maghrib to sharePref.getBoolean(PrayerName.maghrib.name, false) ,
            PrayerName.isha to sharePref.getBoolean(PrayerName.isha.name, false),
            PrayerName.imsak to sharePref.getBoolean(PrayerName.imsak.name, false)
        )

    }

    override fun saveAlarmPref(name: Boolean?) {

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
            .doOnError {
                println(it)
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