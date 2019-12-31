package com.muslimlab.prayers.ui.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.muslimlab.prayers.R
import com.muslimlab.prayers.ui.prayers.model.PrayerItem

@BindingAdapter("app:prayerAlarmIcon")
fun bindPrayerAlarmIcon(view: ImageView, isAlarmOn: Boolean) {
    view.setImageResource(
        if (isAlarmOn) R.drawable.ic_sound_on
        else R.drawable.ic_sound_off
    )
}