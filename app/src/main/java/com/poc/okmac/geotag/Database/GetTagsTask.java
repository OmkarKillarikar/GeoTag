package com.poc.okmac.geotag.Database;

import com.poc.okmac.geotag.fragments.GeoTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetTagsTask implements Callable<ArrayList<GeoTag>> {

    private GeoTagDatabase geoTagDatabase;
    public GetTagsTask(GeoTagDatabase geoTagDatabase){
        this.geoTagDatabase = geoTagDatabase;
    }

    public ArrayList<GeoTag> call() {
        List<GeoTag> geoTags = geoTagDatabase.geoTagDao().getAllTags();
        if (geoTags != null) {
            return (ArrayList<GeoTag>) geoTags;
        }
        return null;
    }
}
