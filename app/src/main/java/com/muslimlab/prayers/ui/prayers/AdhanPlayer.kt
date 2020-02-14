package com.muslimlab.prayers.ui.prayers

import android.content.Context
import android.media.MediaPlayer
import com.muslimlab.prayers.R

object AdhanPlayer {
    private var adhanMediaPlayer :MediaPlayer? = null

    fun start(context: Context) {
        if (adhanMediaPlayer == null) {
            adhanMediaPlayer =  MediaPlayer.create(context, R.raw.adhan)
        }

        adhanMediaPlayer?.start()
    }

    fun stop() {
        adhanMediaPlayer?.run {
            if (isPlaying) {
                stop()
            }
        }
    }
}


