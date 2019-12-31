package com.muslimlab.prayers.ui.prayers

import com.muslimlab.prayers.model.PrayerResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PrayersApi {
    @Headers(
        "x-rapidapi-host: aladhan.p.rapidapi.com",
        "x-rapidapi-key: 0f05591f0bmshf23f8e794fd1a34p189521jsn97589e304e47"
    )
    @GET("/timingsByCity")
    fun todayPrayers(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int
    ): Single<PrayerResult>
}
