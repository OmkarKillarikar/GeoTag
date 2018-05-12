package com.poc.okmac.geotag.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.poc.okmac.geotag.fragments.GeoTag;

@Database(entities = {GeoTag.class}, version = 1)
public abstract class GeoTagDatabase extends RoomDatabase {
    public abstract GeoTagDao geoTagDao();

    private static GeoTagDatabase INSTANCE;

    public static GeoTagDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GeoTagDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GeoTagDatabase.class, "geo_tag_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public void insertTag(final GeoTag geoTag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                geoTagDao().insert(geoTag);
            }
        }).start();
    }
}