package com.devansh.pixsel.viewmodel;



import com.devansh.pixsel.model.imageModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {         // we dont need the context for this so used ViewModel

    public MutableLiveData<imageModel> imageLiveData = new MutableLiveData<>();

    public void fetch(){
        imageModel image1 = new imageModel("Devansh","1","28/08/19","10Mb","Lite");
        imageLiveData.setValue(image1);
    }


}
