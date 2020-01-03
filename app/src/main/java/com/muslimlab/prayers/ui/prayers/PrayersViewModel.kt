package com.muslimlab.prayers.ui.prayers

import androidx.databinding.ObservableField
import com.muslimlab.prayers.model.Prayers
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import com.muslimlab.prayers.ui.prayers.model.PrayerName
import com.muslimlab.prayers.ui.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PrayersViewModel(
    private val repository: PrayersRepository
) {

    val prayerDisposable: MutableList<Disposable> = ArrayList(5)
    val prayers = ObservableField<Prayers>()
    val today = ObservableField<String>()
    val dayOfTheWeek = ObservableField<String>()

    val prayer = ObservableField<MutableMap<String, PrayerItem>>()

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
            }.subscribe())

        prayerDisposable.add(
            repository.fetchPrayers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    it.data.timings.run {

                        val mapOfPrayer = mutableMapOf(
                            "imsak" to PrayerItem(
                                name = PrayerName.imsak,
                                time = "${BURMESE_DAY_TIME.MORNING.value} ${imsak.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            ),

                            "fajir" to PrayerItem(
                                name = PrayerName.fajr,
                                time = "${BURMESE_DAY_TIME.MORNING.value} ${fajir.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            ),

                            "dhuhr" to PrayerItem(
                                name = PrayerName.duhur,
                                time = "${BURMESE_DAY_TIME.MORNING.value} ${dhuhr.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            ),

                            "asr" to PrayerItem(
                                name = PrayerName.asr,
                                time = "${BURMESE_DAY_TIME.EVENING.value} ${asr.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            ),


                            "maghrib" to PrayerItem(
                                name = PrayerName.maghrib,
                                time = "${BURMESE_DAY_TIME.EVENING.value} ${maghrib.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            ),


                            "isha" to PrayerItem(
                                name = PrayerName.isha,
                                time = "${BURMESE_DAY_TIME.NIGHT.value} ${isha.to12HourFormat().toBurmeseNumber()}",
                                isAlarmOn = false
                            )
                        )

                        prayer.set(mapOfPrayer)
                    }

                }
                .doOnError { println(it) }
                .subscribe()
        )
    }

    fun onViewDestroyed() {
        prayerDisposable.forEach {
            if (!it.isDisposed) it.dispose()
        }
    }

    fun toggleAlarmOn(item: PrayerItem?) {
        if (item != null) {
            prayer.get()?.set(item.name.name, item.copy(isAlarmOn = !item.isAlarmOn))
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
