package com.androidtutorialpoint.googlemapsdistancecalculator.POJO

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Example {

    /**
     *
     * @return
     * The routes
     */
    /**
     *
     * @param routes
     * The routes
     */
    @SerializedName("routes")
    @Expose
    var routes: List<Route> = ArrayList()

}