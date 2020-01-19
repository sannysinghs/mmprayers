package com.muslimlab.prayers.ui.prayers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import java.util.*

interface PrayerAlarmManager {
    fun setAlarm(item: PrayerItem)
    fun cancelAlarm(item: PrayerItem)
}

private val ALARM_REQUEST_CODE = 100

class PrayerAlarmManagerImpl(private val context: Context) : PrayerAlarmManager {

    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private lateinit var alarmIntent : PendingIntent

    override fun setAlarm(item: PrayerItem) {
        alarmIntent = Intent(context, PrayerAlarmBroadcastReceiver::class.java).let { intent ->
            intent.putExtra("name", item.name.name)
            intent.putExtra("content", "Prayer will start 15 minutes")
            PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, 0)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + 5 * 1000,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    override fun cancelAlarm(item: PrayerItem) {
        alarmManager.cancel(alarmIntent)
    }

}
