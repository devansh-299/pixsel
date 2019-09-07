package com.devansh.pixsel.model;


import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageApiService {

    private static final String BASE_URL = "https://raw.githubusercontent.com";

    private ImageApi api;

    public ImageApiService() {
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ImageApi.class);
    }

    public Single<List<imageModel>> getImages() {
        return api.getImages();
    }
}