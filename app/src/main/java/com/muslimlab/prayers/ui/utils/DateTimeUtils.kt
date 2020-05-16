package com.muslimlab.prayers.ui.utils

import java.text.SimpleDateFormat
import java.util.*

val FORMAT_PRAYER_TIME_12 = SimpleDateFormat("hh:mm", Locale.ENGLISH)
val FORMAT_PRAYER_WEEK_OF_DAY = SimpleDateFormat("E", Locale.ENGLISH)
val FORMAT_PRAYER_TODAY = SimpleDateFormat("d:MMMM:yyyy", Locale.ENGLISH)
val FORMAT_PRAYER_TODAY_BURMESE = SimpleDateFormat("d(ရက်နေ့) MMMM(လ) yyyy", Locale("my", "MY"))
val FORMAT_PRAYER_WEEK_OF_DAY_BURMESE = SimpleDateFormat("E", Locale("my", "MY"))