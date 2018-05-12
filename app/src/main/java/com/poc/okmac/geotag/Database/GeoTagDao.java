package com.poc.okmac.geotag.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.poc.okmac.geotag.fragments.GeoTag;

import java.util.List;

@Dao
public interface GeoTagDao {

    @Insert
    void insert(GeoTag word);

    @Query("SELECT * from geo_tag_table")
    List<GeoTag> getAllTags();
}