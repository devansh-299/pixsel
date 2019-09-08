package com.devansh.pixsel.viewmodel;

import android.app.Application;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.devansh.pixsel.model.ImageApiService;
import com.devansh.pixsel.model.ImageDao;
import com.devansh.pixsel.model.ImageDatabase;
import com.devansh.pixsel.model.ImageApi;
import com.devansh.pixsel.model.ImageApiService;
import com.devansh.pixsel.model.imageModel;
import com.devansh.pixsel.util.NotificationsHelper;
import com.devansh.pixsel.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {

    // androidView model allows us to use the "context " part of android
    // without making any connection b/w viewmodel and android app

    public MutableLiveData<List<imageModel>> images = new MutableLiveData<List<imageModel>>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    private ImageApiService imagesApiService = new ImageApiService();

    private CompositeDisposable disposable = new CompositeDisposable();

    private  AsyncTask<List<imageModel>, Void, List<imageModel>> insertTask;      // declaring the object of async class

    private AsyncTask<Void,Void,List<imageModel>>  retrieveTask;

    private SharedPreferencesHelper prefHelper = SharedPreferencesHelper.getInstance(getApplication());
    private long refreshTime = 5*60*1000*1000*1000L ;         // time in namo seconds! bcz system clock works  in nanoseconds
                                                             // this time decides how much time after last data retrieved from database we have to
                                                            // retrieve data from database!

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {

        // we have two ways of retriving data , one by database and other by using the backend server
        // a GOOD implementation of these data retrieval method ensures that we use these resources carefully , this involves Cache Implementation

        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if (updateTime != 0  && currentTime - updateTime <refreshTime){
            fetchFromDatabase();
        }
        else {
            fetchFromRemote();
        }


    }

    public void refreshByPassCache(){
        // this  function handles the refresh spinner  , it forces to use retrieve from remote
        fetchFromRemote();
    }

    private void fetchFromDatabase(){
        loading.setValue(true);
        retrieveTask = new RetrieveImageTask();
        retrieveTask.execute();
    }

    private void fetchFromRemote() {
        loading.setValue(true);
        disposable.add(

                imagesApiService.getImages()                   // this method returns the Single

                        .subscribeOn(Schedulers.newThread())           // here we are linking our new thread to the backend to get data
                        // NOTE  android doesnot allow to link our main thread to link to server
                        // because we dont know how much time it can take to get data #maybe forever

                        .observeOn(AndroidSchedulers.mainThread())    // now we shift to main thread after getting data bcz background threads cannot show data to user


                        .subscribeWith(new DisposableSingleObserver<List<imageModel>>() {       // now assigning an observer to the Single (an observable)


                            @Override
                            public void onSuccess(List<imageModel> imageModels) {
                                insertTask  = new InsertImageTask();
                                insertTask.execute(imageModels);
                                Toast.makeText(getApplication(),"Data retrieved from remote",Toast.LENGTH_SHORT).show();
                                NotificationsHelper.getInstance(getApplication()).createNotification();
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                imageLoadError.setValue(true);
                            }
                        })
        );
    }

    private void imagesRetrieved(List<imageModel> imageModels) {
        images.setValue(imageModels);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }

    public void checkCacheDuration(){
        String cachePreference = prefHelper.getCacheDuration();

        if(!cachePreference.equals("")){
            try {
                int cahcePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cahcePreferenceInt*1000*1000*1000L;

            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        if(insertTask != null){
            insertTask.cancel(true);            // done because of memory caches!!
            insertTask = null;
        }
        if(retrieveTask!= null){
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

                                                     //input      progress   output
    private class InsertImageTask extends AsyncTask<List<imageModel>, Void, List<imageModel>> {

        @Override
        protected List<imageModel> doInBackground(List<imageModel>... lists) {
            List<imageModel> list = lists[0];

            ImageDao dao = ImageDatabase.getInstance(getApplication()).imageDao();      // because of this context we used AndroidVIewModel and not ViewModel

            dao.deleteAllImages();
            ArrayList<imageModel> newList = new ArrayList<>(list);
            List<Long> result = dao.insertAll(newList.toArray(new imageModel[0]));   //why parameters passed in this way is not clear!

            int i = 0;
            while (i < list.size()) {
                list.get(i).uuid = result.get(i).intValue();         // updating the uuid in our objects
                ++i;
            }
            return list;

        }

        @Override
        protected void onPostExecute(List<imageModel> imageModels) {
            imagesRetrieved(imageModels);
            prefHelper.saveUpdateTime(System.nanoTime());
        }
    }

    private class RetrieveImageTask extends AsyncTask <Void ,Void ,List<imageModel>>{

        @Override
        protected List<imageModel> doInBackground(Void... voids) {
            return ImageDatabase.getInstance(getApplication()).imageDao().getAllImages();
        }

        @Override
        protected void onPostExecute(List<imageModel> imageModels) {
            imagesRetrieved(imageModels);
            Toast.makeText(getApplication(),"Data retrieved from database",Toast.LENGTH_SHORT).show();
        }
    }
}
