package com.androidtutorialpoint.googlemapsdistancecalculator.POJO

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OverviewPolyline {

    /**
     *
     * @return
     * The points
     */
    /**
     *
     * @param points
     * The points
     */
    @SerializedName("points")
    @Expose
    var points: String? = null

}
