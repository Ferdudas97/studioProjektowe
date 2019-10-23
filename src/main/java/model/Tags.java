package model;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class Tags {
    @SerializedName("type")
    private final String type;

    @SerializedName("amenity")
    private final String amenity;

    @SerializedName("name")
    private final String name;

    @SerializedName("phone")
    private final String phone;

    @SerializedName("contact:email")
    private final String contactEmail;

    @SerializedName("website")
    private final String website;

    @SerializedName("addr:city")
    private final String addressCity;

    @SerializedName("addr:postcode")
    private final String addressPostCode;

    @SerializedName("addr:street")
    private final String addressStreet;

    @SerializedName("addr:housenumber")
    private final String addressHouseNumber;

    @SerializedName("wheelchair")
    private final String wheelchair;

    @SerializedName("wheelchair:description")
    private final String wheelchairDescription;

    @SerializedName("opening_hours")
    private final String openingHours;

    @SerializedName("internet_access")
    private final String internetAccess;

    @SerializedName("fee")
    private final String fee;

    @SerializedName("operator")
    private final String operator;
}
