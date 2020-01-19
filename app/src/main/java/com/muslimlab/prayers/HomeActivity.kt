package com.muslimlab.prayers

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.muslimlab.prayers.R
import com.muslimlab.prayers.ui.prayers.AdhanNotificationManager
import com.muslimlab.prayers.ui.prayers.AdhanPlayer
import com.muslimlab.prayers.ui.prayers.PrayersFragment

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, PrayersFragment.newInstance())
            .commit()

    }

    override fun onStart() {
        super.onStart()
        AdhanPlayer.stop()
    }
}
