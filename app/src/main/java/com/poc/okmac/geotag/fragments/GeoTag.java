package com.poc.okmac.geotag.fragments;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "geo_tag_table")
public class GeoTag {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "imageName")
    private String imageName;
    private Double latitude;
    private Double longitude;
    private String address;


    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NonNull
    public String getImageName() {
        return imageName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}