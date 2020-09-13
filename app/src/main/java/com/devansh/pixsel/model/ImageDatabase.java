package com.devansh.pixsel.model;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {imageModel.class}, version = 2, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {

    private static ImageDatabase instance;

    public static ImageDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                ImageDatabase.class,
                "imagedatabase")
                .build();
        }
        return instance;
    }

    public abstract ImageDao imageDao();
}