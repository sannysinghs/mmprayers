package com.muslimlab.prayers.ui.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.muslimlab.prayers.R
import com.muslimlab.prayers.ui.prayers.model.PrayerItem

@BindingAdapter("app:prayerTime")
fun bindPrayerTime(view: TextView, item: PrayerItem?) {
    item?.run {
        view.text = this.time
    }
}

@BindingAdapter("app:prayerAlarmIcon")
fun bindPrayerAlarmIcon(view: ImageView, item: PrayerItem?) {
    item?.let {
        view.setImageResource(
            if (item.isAlarmOn) R.drawable.ic_sound
            else R.drawable.ic_song_off
        )
    }

}