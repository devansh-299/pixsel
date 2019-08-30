package com.devansh.pixsel.model;

public class imageModel {

    public String imageName;
    public String imageId ;
    public String imageDate ;
    public String imageSize ;
    public String imageUrl;
    public String uui ;                      // id assigned by database to our element

    public imageModel(String imageName,String imageId, String imageDate, String imageSize, String imageUrl) {
        this.imageName = imageName;
        this.imageId = imageId;
        this.imageDate = imageDate;
        this.imageSize = imageSize;
        this.imageUrl = imageUrl;
    }
}
