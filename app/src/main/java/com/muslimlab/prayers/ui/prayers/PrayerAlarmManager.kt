package com.muslimlab.prayers.ui.prayers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import com.muslimlab.prayers.ui.prayers.model.PrayerName

interface PrayerAlarmManager {
    fun setAlarm(item: PrayerItem)
    fun cancelAlarm(item: PrayerItem)
}

private val ALARM_REQUEST_CODE = 100

class PrayerAlarmManagerImpl(private val context: Context) : PrayerAlarmManager {

    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val mapOfAlarms: MutableMap<PrayerName, PendingIntent> = mutableMapOf()

    override fun setAlarm(item: PrayerItem) {
        val alarmIntent = Intent(context, PrayerAlarmBroadcastReceiver::class.java).let { intent ->
            intent.putExtra("name", item.name.name)
            intent.putExtra("content", "Prayer will start 15 minutes")
            PendingIntent.getBroadcast(context, item.name.hashCode(), intent, 0)
        }

        alarmManager.cancel(alarmIntent)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            item.timeInMills,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

        mapOfAlarms[item.name] = alarmIntent
    }

    override fun cancelAlarm(item: PrayerItem) {
        mapOfAlarms[item.name]?.let {
            alarmManager.cancel(it)
        }
    }

}
