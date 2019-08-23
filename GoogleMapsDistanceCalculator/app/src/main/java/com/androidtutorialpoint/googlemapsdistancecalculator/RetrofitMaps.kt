package com.androidtutorialpoint.googlemapsdistancecalculator

import com.androidtutorialpoint.googlemapsdistancecalculator.POJO.Example

import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

/**
 * Created by navneet on 17/7/16.
 */
interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json?key=AIzaSyC22GfkHu9FdgT9SwdCWMwKX1a4aohGifM")
    fun getDistanceDuration(@Query("units") units: String, @Query("origin") origin: String, @Query("destination") destination: String, @Query("mode") mode: String): Call<Example>

}
