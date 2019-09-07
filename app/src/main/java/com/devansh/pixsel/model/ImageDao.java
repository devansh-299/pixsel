package com.devansh.pixsel.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ImageDao {
    @Insert
    List<Long> insertAll(imageModel... dogs);

    @Query("SELECT * FROM imageModel")
    List<imageModel> getAllImages();

    @Query("SELECT * FROM imageModel WHERE uuid = :imageId")
    imageModel getImage(int imageId);

    @Query("DELETE FROM imageModel")
    void deleteAllImages();
}
