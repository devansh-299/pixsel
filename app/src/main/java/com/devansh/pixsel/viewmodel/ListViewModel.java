package com.devansh.pixsel.viewmodel;

import android.app.Application;

import com.devansh.pixsel.model.imageModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ListViewModel extends AndroidViewModel {

    // androidView model allows us to use the "context " part of android
    // without making any connection b/w viewmodel and android app

    public MutableLiveData<List<imageModel>> images = new MutableLiveData<List<imageModel>>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh(){

        imageModel image1 = new imageModel("Devansh","1","28/08/19","10Mb","Lite");
        imageModel image2 = new imageModel("Nonu","2","28/08/19","15Mb","Lite");
        imageModel image3 = new imageModel("Devansh","3","28/08/19","20Mb","Lite");
        imageModel image4 = new imageModel("Dhruv","4","29/08/19","10Mb","Lite");
        imageModel image5 = new imageModel("Nonu","5","30/08/19","10Mb","Lite");
        imageModel image6 = new imageModel("Dhruv","6","30/08/19","10Mb","Lite");
        imageModel image7 = new imageModel("Devansh","7","31/08/19","15Mb","Lite");

        ArrayList<imageModel> imagesList = new ArrayList<>();
        imagesList.add(image1);
        imagesList.add(image2);
        imagesList.add(image3);
        imagesList.add(image4);
        imagesList.add(image5);
        imagesList.add(image6);
        imagesList.add(image7);


        images.setValue(imagesList);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }
}
