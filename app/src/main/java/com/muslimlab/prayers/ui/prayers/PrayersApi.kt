package com.muslimlab.prayers.ui.prayers

import com.muslimlab.prayers.model.PrayerResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PrayersApi {
    @Headers(
        "x-rapidapi-host: aladhan.p.rapidapi.com",
        "x-rapidapi-key: BXMQ6lzcnJmshgz0MhJIH9AI6lbOp1UWeX4jsnqFcRQvKzBqJP"
    )
    @GET("/timingsByCity")
    fun todayPrayers(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int
    ): Single<PrayerResult>
}
