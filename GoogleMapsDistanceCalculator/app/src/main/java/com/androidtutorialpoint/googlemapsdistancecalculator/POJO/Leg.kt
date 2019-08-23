package com.androidtutorialpoint.googlemapsdistancecalculator.POJO

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class Leg {

    /**
     *
     * @return
     * The distance
     */
    /**
     *
     * @param distance
     * The distance
     */
    @SerializedName("distance")
    @Expose
    var distance: Distance? = null
    /**
     *
     * @return
     * The duration
     */
    /**
     *
     * @param duration
     * The duration
     */
    @SerializedName("duration")
    @Expose
    var duration: Duration? = null

}
