package com.androidtutorialpoint.googlemapsdistancecalculator.POJO

/*
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duration {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private Integer value;


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public Integer getValue() {
        return value;
    }


    public void setValue(Integer value) {
        this.value = value;
    }

}
*/
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Duration {

    /**
     *
     * @return
     * The text
     */
    /**
     *
     * @param text
     * The text
     */
    @SerializedName("text")
    @Expose
    var text: String? = null
    /**
     *
     * @return
     * The value
     */
    /**
     *
     * @param value
     * The value
     */
    @SerializedName("value")
    @Expose
    var value: Int? = null

}