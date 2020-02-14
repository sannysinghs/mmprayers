package com.muslimlab.prayers

import android.app.Application
import com.google.gson.Gson
import com.muslimlab.prayers.ui.prayers.AdhanNotificationManager
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PrayersApplication: Application() {


    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()

        retrofit = Retrofit.Builder()
            .baseUrl("https://aladhan.p.rapidapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        AdhanNotificationManager.createNotificationChannel(context = this)
    }

    fun getRetrofitInstance() = retrofit
}