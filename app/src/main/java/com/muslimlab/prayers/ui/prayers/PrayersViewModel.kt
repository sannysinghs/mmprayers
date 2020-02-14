package com.muslimlab.prayers.ui.prayers

import android.content.SharedPreferences
import androidx.databinding.ObservableField
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import com.muslimlab.prayers.ui.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PrayersViewModel(
    private val repository: PrayersRepository,
    private val sharePref: SharedPreferences,
    private val prayerAlarmManager: PrayerAlarmManager
) {

    private val prayerDisposable: MutableList<Disposable> = ArrayList(5)
    val today = ObservableField<String>()
    val dayOfTheWeek = ObservableField<String>()

    val alarmPref: MutableMap<PrayerName, Boolean> = mutableMapOf()

    fun onViewCreated() {
        Single.fromCallable { Calendar.getInstance().time }
            .map {
                Pair(
                    FORMAT_PRAYER_TODAY.format(it).run {
                        "${split(":")[0].toBurmeseDay()}.${split(":")[1].toBurmeseMonth()}"
                    },
                    FORMAT_PRAYER_WEEK_OF_DAY.format(it).toBurmeseDayOfWeek()
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                today.set(it.first)
                dayOfTheWeek.set(it.second)
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
            .doOnNext {
                it.data.timings.run {
                    imsakPrayer.set(
                        PrayerItem(
                            name = PrayerName.imsak,
                            time = "${BURMESE_DAY_TIME.MORNING.value} ${imsak.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.imsak] ?: false,
                            timeInMills = imsak.toMillis()
                        )
                    )

                    fajirPrayer.set(
                        PrayerItem(
                            name = PrayerName.fajr,
                            time = "${BURMESE_DAY_TIME.MORNING.value} ${fajir.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.fajr] ?: false,
                            timeInMills = fajir.toMillis()
                        ).apply {
                            if (isAlarmOn)
                                prayerAlarmManager.setAlarm(this)
                        }
                    )

                    dhuhrPrayer.set(
                        PrayerItem(
                            name = PrayerName.duhur,
                            time = "${BURMESE_DAY_TIME.MORNING.value} ${dhuhr.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.duhur] ?: false,
                            timeInMills = dhuhr.toMillis()
                        ).apply {
                            if (isAlarmOn)
                                prayerAlarmManager.setAlarm(this)
                        }
                    )

                    asrPrayer.set(
                        PrayerItem(
                            name = PrayerName.asr,
                            time = "${BURMESE_DAY_TIME.EVENING.value} ${asr.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.asr] ?: false,
                            timeInMills = asr.toMillis()
                        ).apply {
                            if (isAlarmOn)
                                prayerAlarmManager.setAlarm(this)
                        }
                    )

                    ishaPrayer.set(
                        PrayerItem(
                            name = PrayerName.isha,
                            time = "${BURMESE_DAY_TIME.NIGHT.value} ${isha.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.isha] ?: false,
                            timeInMills = isha.toMillis()
                        ).apply {
                            if (isAlarmOn)
                                prayerAlarmManager.setAlarm(this)
                        }
                    )

                    maghribPrayer.set(
                        PrayerItem(
                            name = PrayerName.maghrib,
                            time = "${BURMESE_DAY_TIME.EVENING.value} ${maghrib.to12HourFormat().toBurmeseNumber()}",
                            isAlarmOn = alarmPref[PrayerName.maghrib] ?: false,
                            timeInMills = maghrib.toMillis()
                        ).apply {
                            if (isAlarmOn)
                                prayerAlarmManager.setAlarm(this)
                        }
                    )
                }


            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { println(it) }
            .subscribe()
            .toDisposable()
    }


    fun Disposable.toDisposable(): Unit {
        prayerDisposable.add(this)
    }

    val ishaPrayer = ObservableField<PrayerItem>()
    val maghribPrayer = ObservableField<PrayerItem>()
    val asrPrayer = ObservableField<PrayerItem>()
    val dhuhrPrayer = ObservableField<PrayerItem>()
    val fajirPrayer = ObservableField<PrayerItem>()
    val imsakPrayer = ObservableField<PrayerItem>()


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
            set(item.copy(isAlarmOn = toggle))
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

val FORMAT_PRAYER_TIME_12 = SimpleDateFormat("hh:mm", Locale.ENGLISH)
private fun String.to12HourFormat(): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR, split(":").first().toInt())
        set(Calendar.MINUTE, split(":").last().toInt())
    }

    return FORMAT_PRAYER_TIME_12.format(cal.time)
}


private fun String.toMillis() : Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR, split(":").first().toInt())
        set(Calendar.MINUTE, split(":").last().toInt())
    }

    return cal.timeInMillis
}