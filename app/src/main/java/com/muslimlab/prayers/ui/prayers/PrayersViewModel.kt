package com.muslimlab.prayers.ui.prayers

import android.content.SharedPreferences
import androidx.databinding.ObservableField
import com.muslimlab.prayers.model.PrayerResult
import com.muslimlab.prayers.model.Prayers
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import com.muslimlab.prayers.ui.prayers.model.PrayerTime
import com.muslimlab.prayers.ui.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PrayersViewModel(
    private val repository: PrayersRepository,
    private val sharePref: SharedPreferences,
    private val prayerAlarmManager: PrayerAlarmManager
) {

    val prayers =  BehaviorSubject.create<Map<PrayerName, PrayerItem>>()
    val todayDateAndTime =  BehaviorSubject.create<Pair<String, String>>()

    private val prayerDisposable: MutableList<Disposable> = ArrayList(5)
    val upcomingPrayer = ObservableField<PrayerName>()

    val alarmPref: MutableMap<PrayerName, Boolean> = mutableMapOf()

    fun onViewCreated() {
        Single.fromCallable { Calendar.getInstance().time }
            .map {
                Pair(
                    FORMAT_PRAYER_TODAY.format(it).run {
                        "${split(":")[0].toBurmeseDay()}.${split(":")[1].toBurmeseMonth()}"
                    },
                    FORMAT_PRAYER_TIME_12.format(it).toBurmeseDayOfWeek()
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                todayDateAndTime.onNext(it)
            }
            .subscribe()
            .toDisposable()

        repository.getAlarmPref()
            .toObservable()
            .map {
                alarmPref.putAll(it)
            }
            .flatMap {
                repository.todayPrayers()
            }
            .first(PrayerResult.EMPTY)
            .doOnSuccess {
                upcomingPrayer.set(getCurrentPrayer(it.data.timings))
                it.data.timings.run {
                    mapOf(
                        PrayerName.imsak to PrayerItem(
                            name = PrayerName.imsak,
                            time = "${BURMESE_DAY_TIME.MORNING.value} ${imsak.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.imsak] ?: false,
                            timeInMills = imsak.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.imsak)
                        ),

                        PrayerName.fajir to PrayerItem(
                            name = PrayerName.fajir,
                            time = "${BURMESE_DAY_TIME.MORNING.value} ${fajir.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.fajir] ?: false,
                            timeInMills = fajir.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.fajir)
                        ),

                        PrayerName.duhur to PrayerItem(
                            name = PrayerName.duhur,
                            time = "${BURMESE_DAY_TIME.NOON.value} ${dhuhr.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.duhur] ?: false,
                            timeInMills = dhuhr.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.duhur)
                        ),

                        PrayerName.asr to PrayerItem(
                            name = PrayerName.asr,
                            time = "${BURMESE_DAY_TIME.NOON.value} ${asr.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.asr] ?: false,
                            timeInMills = asr.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.asr)
                        ),

                        PrayerName.maghrib to PrayerItem(
                            name = PrayerName.maghrib,
                            time = "${BURMESE_DAY_TIME.EVENING.value} ${maghrib.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.maghrib] ?: false,
                            timeInMills = maghrib.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.maghrib)
                        ),

                        PrayerName.isha to PrayerItem(
                            name = PrayerName.isha,
                            time = "${BURMESE_DAY_TIME.NIGHT.value} ${isha.to12HourFormat()
                                .toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.isha] ?: false,
                            timeInMills = imsak.toMillis(),
                            isUpcoming = isUpcoming(PrayerName.isha)
                        )
                    ).run {
                        prayers.onNext(this)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { println(it) }
            .subscribe()
            .toDisposable()
    }

    private fun isUpcoming(name: PrayerName) = upcomingPrayer.get() == name


    private fun getCurrentPrayer(timings: Prayers): PrayerName {
        return (listOf(
            PrayerTime(PrayerName.imsak, timeInMills = timings.imsak.toMillis()),
            PrayerTime(PrayerName.fajir, timeInMills = timings.fajir.toMillis()),
            PrayerTime(PrayerName.duhur, timeInMills = timings.dhuhr.toMillis()),
            PrayerTime(PrayerName.asr, timeInMills = timings.asr.toMillis()),
            PrayerTime(PrayerName.maghrib, timeInMills = timings.maghrib.toMillis()),
            PrayerTime(PrayerName.isha, timeInMills = timings.isha.toMillis())
        ).firstOrNull {
            val currentTime = FORMAT_PRAYER_TIME_24.format(Calendar.getInstance().time)
            val prayerTime = FORMAT_PRAYER_TIME_24.format(Calendar.getInstance().apply {
                timeInMillis = it.timeInMills
            }.time)

             prayerTime > currentTime
        }?.name ?: PrayerName.fajir).also {
            println("[PRAYER]: current: $it")
        }
    }

    fun Disposable.toDisposable(): Unit {
        prayerDisposable.add(this)
    }

    val ishaPrayer = BehaviorSubject.create<PrayerItem>()
    val maghribPrayer = BehaviorSubject.create<PrayerItem>()
    val asrPrayer = BehaviorSubject.create<PrayerItem>()
    val dhuhrPrayer = BehaviorSubject.create<PrayerItem>()
    val fajirPrayer = BehaviorSubject.create<PrayerItem>()
    val imsakPrayer = BehaviorSubject.create<PrayerItem>()


    fun onViewDestroyed() {
        prayerDisposable.forEach {
            if (!it.isDisposed) it.dispose()
        }
    }

    fun toggleAlarmOn(item: PrayerItem) {
        val toggle = !item.isAlarmOn
        alarmPref[item.name] = toggle
        when (item.name) {
            PrayerName.isha -> ishaPrayer
            PrayerName.imsak -> imsakPrayer
            PrayerName.maghrib -> maghribPrayer
            PrayerName.duhur -> dhuhrPrayer
            PrayerName.asr -> asrPrayer
            else -> fajirPrayer
        }.run {
            onNext(item.copy(isAlarmOn = toggle))
            sharePref.edit().putBoolean(item.name.name, toggle).apply()
            if (toggle) {
                prayerAlarmManager.setAlarm(item)
            } else {
                prayerAlarmManager.cancelAlarm(item)
            }

        }
    }
}


private fun String.toBurmeseDayOfWeek(): String {
    return BURMESE_DAY_OF_WEEK[toLowerCase(Locale.ENGLISH)]?.plus(BUREMSE_DAY_STRING) ?: ""
}

private fun String.toBurmeseDay(): String =
    BURMESE_NUMBERS[this.toInt()].plus(BUREMSE_YAT_STRING).plus(
        BUREMSE_DAY_STRING
    )

private fun String.toBurmeseMonth(): String = BURMESE_MONTHS[this]?.plus(BUREMSE_MONTH_STRING) ?: ""


private fun String.toBurmeseNumber(): String {
    val builder = StringBuilder()
    forEach { value ->
        builder.append(
            if (value != ':') {
                BURMESE_NUMBERS[value - '0']
            } else {
                value
            }
        )
    }
    return builder.toString()
}

val FORMAT_PRAYER_TIME_24 = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val FORMAT_PRAYER_TIME_12 = SimpleDateFormat("hh:mm", Locale.ENGLISH)
private fun String.to12HourFormat(): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR, split(":").first().toInt())
        set(Calendar.MINUTE, split(":").last().toInt())
    }

    return FORMAT_PRAYER_TIME_12.format(cal.time)
}


private fun String.toMillis(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, split(":").first().toInt())
        set(Calendar.MINUTE, split(":").last().toInt())
    }

    return cal.timeInMillis
}