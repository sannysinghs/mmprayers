package com.muslimlab.prayers.ui.prayers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import com.muslimlab.prayers.R
import com.muslimlab.prayers.ui.prayers.model.PrayerItem
import java.util.*

interface PrayerAlarmManager {
    fun setAlarm(item: PrayerItem)
    fun cancelAlarm(item: PrayerItem)
}

private val ALARM_REQUEST_CODE = 100

class PrayerAlarmManagerImpl(context: Context) : PrayerAlarmManager {

    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val alarmIntent: PendingIntent by lazy {
        Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, 0)
        }
    }

    override fun setAlarm(item: PrayerItem) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            item.timeInMills,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    override fun cancelAlarm(item: PrayerItem) {
        alarmManager.cancel(alarmIntent)
    }

}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MediaPlayer.create(context, R.raw.adhan).apply {
            start()
        }
    }
}
