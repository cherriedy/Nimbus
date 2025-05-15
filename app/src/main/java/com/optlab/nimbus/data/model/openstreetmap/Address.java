package com.optlab.nimbus.data.model.openstreetmap;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("suburb")
    protected String suburb;

    @SerializedName("city")
    protected String city;

    @SerializedName("country")
    protected String country;

    @SerializedName("country_code")
    protected String country_code;

    public String[] getComponents() {
        return new String[] {suburb, city, country, country_code};
    }
}
