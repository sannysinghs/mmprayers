package com.muslimlab.prayers.ui.prayers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muslimlab.prayers.R

class PrayerAlarmBroadcastReceiver: BroadcastReceiver()  {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.run {
            AdhanPlayer.start(this)

            val title =
                intent?.extras?.getString("name") ?: context.getString(R.string.app_name)

            val content =
                intent?.extras?.getString("content") ?: context.getString(R.string.app_name)

            AdhanNotificationManager.show(this, title, content)
        }

    }

}