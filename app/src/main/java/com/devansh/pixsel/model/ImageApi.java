package com.devansh.pixsel.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ImageApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    Single<List<imageModel>> getImages();
}
