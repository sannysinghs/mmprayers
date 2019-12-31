package com.muslimlab.prayers.ui.prayers

import android.content.Context
import androidx.databinding.ObservableField
import com.muslimlab.prayers.model.PrayerResult
import com.muslimlab.prayers.model.Prayers
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import com.muslimlab.prayers.ui.utils.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.PipedReader
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PrayersViewModel(
    private val repository: PrayersRepository
) {

    val prayerDisposable: MutableList<Disposable> = ArrayList(5)
    val prayers = ObservableField<Prayers>()
    val today = ObservableField<String>()
    val dayOfTheWeek = ObservableField<String>()

    val ishaAalarm = ObservableField<Boolean>()

    val alarmPref: MutableMap<PrayerName, Boolean> = HashMap()

    val listOfPrayerItem: ArrayList<PrayerViewItem> = ArrayList()

    fun onViewCreated() {
        prayerDisposable.add(Single.fromCallable { Calendar.getInstance().time }
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
            }.subscribe()
        )

        prayerDisposable.add(
            repository.todayPrayers()
            .zipWith(repository.getAlarmPref()
                .toObservable()
                .map { lstOfAlarmPref ->
                    lstOfAlarmPref.forEach {
                        alarmPref[it.prayerCode] = it.isAlarm
                        if (it.prayerCode == PrayerName.ISHA) {
                            ishaAalarm.set(it.isAlarm)
                        }

                    }
                    alarmPref
                },
                BiFunction<PrayerResult, Map<PrayerName, Boolean>, List<PrayerViewItem>> { result, pref ->
                    val data: List<PrayerViewItem> = ArrayList(6)
                    result.data.timings.run {
                        listOf(
                           /* createPrayerViewItem(imsak, pref, PrayerName.IMSAK),
                            createPrayerViewItem(fajir, pref, PrayerName.FAJR),
                            createPrayerViewItem(dhuhr, pref, PrayerName.DHUHR),
                            createPrayerViewItem(asr, pref, PrayerName.ASR),
                            createPrayerViewItem(maghrib, pref, PrayerName.MAGHRIB),
                            createPrayerViewItem(isha, pref, PrayerName.ISHA)*/
                        )

                    }
                })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnNext {
                listOfPrayerItem.addAll(it)

            }
            .doOnError { println(it) }
            .subscribe()
        )
    }

    private fun createPrayerViewItem(prayerTime: String, pref: Map<PrayerName, Boolean>, prayerName: PrayerName): PrayerViewItem {
        return PrayerViewItem(
            prayerCode = prayerName.name,
            name =  "${BURMESE_DAY_TIME.MORNING.value} ${prayerTime.to12HourFormat().toBurmeseNumber()}",
            isAlarmOn = pref.getValue(PrayerName.IMSAK)
        )
    }

    fun onViewDestroyed() {
        prayerDisposable.forEach {
            if (!it.isDisposed) it.dispose()
        }
    }

    fun isAlarmTurnOn(prayerName: String): Boolean = listOfPrayerItem.filter {
        it.prayerCode.equals(prayerName, true)
    }.map {
        it.isAlarmOn
    }.first()


    fun toggleAlarm(prayerName: String) {
        alarmPref[PrayerName.ISHA]?.let {
            alarmPref[PrayerName.ISHA] = !it
            ishaAalarm.set(alarmPref[PrayerName.ISHA])
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
