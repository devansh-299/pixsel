package com.devansh.pixsel.model;

import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class imageModel {

    @ColumnInfo(name = "image_name")
    @SerializedName("name")
    public String imageName;

    @ColumnInfo(name = "image_id")
    @SerializedName("id")
    public String imageId ;

    @ColumnInfo(name = "image_span")
    @SerializedName("life_span")

    public String imageDate ;

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    public String imageSize ;

    @ColumnInfo(name = "image_url")
    @SerializedName("url")
    public String imageUrl;

    public String temperament;

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    public String breedGroup;

//    id assigned by database to our element
    @PrimaryKey(autoGenerate = true)
    public int uuid ;

    public imageModel(String imageName,
                      String imageId,
                      String imageDate,
                      String imageSize,
                      String imageUrl,
                      String temperament,
                      String breedGroup) {
        this.imageName = imageName;
        this.imageId = imageId;
        this.imageDate = imageDate;
        this.imageSize = imageSize;
        this.imageUrl = imageUrl;
        this.temperament = temperament;
        this.breedGroup = breedGroup;
    }
}
