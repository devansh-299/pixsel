package com.devansh.pixsel.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import com.devansh.pixsel.R;
import com.devansh.pixsel.model.ImageApiService;
import com.devansh.pixsel.model.ImageDao;
import com.devansh.pixsel.model.ImageDatabase;
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

    public MutableLiveData<List<imageModel>> images = new MutableLiveData<List<imageModel>>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    private ImageApiService imagesApiService = new ImageApiService();

    private CompositeDisposable disposable = new CompositeDisposable();

    private  AsyncTask<List<imageModel>, Void, List<imageModel>> insertTask;

    private AsyncTask<Void,Void,List<imageModel>>  retrieveTask;

    private SharedPreferencesHelper prefHelper =
            SharedPreferencesHelper.getInstance(getApplication());
    /*
        Points:
            - Time in nano seconds because system clock works in nanoseconds
            - Specifies time interval in fetching data from local database
     */
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L ;

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {

        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if (updateTime != 0  && currentTime - updateTime <refreshTime) {
            fetchFromDatabase();
        } else {
            fetchFromRemote();
        }
    }

    /**
     * This function handles the swipe to refresh, forces to retrieve data from remote
     */
    public void refreshByPassCache() {
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
                imagesApiService.getImages()
                        // off-loading work on background thread
                        .subscribeOn(Schedulers.newThread())
                        // observing changes on main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<imageModel>>() {
                            @Override
                            public void onSuccess(List<imageModel> imageModels) {
                                insertTask  = new InsertImageTask();
                                insertTask.execute(imageModels);
                                Toast.makeText(
                                        getApplication(),
                                        R.string.toast_data_remote,
                                        Toast.LENGTH_SHORT).show();
                                NotificationsHelper.getInstance(getApplication()).createNotification();
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                imageLoadError.setValue(true);
                            }
                        }));
    }

    private void imagesRetrieved(List<imageModel> imageModels) {
        images.setValue(imageModels);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }

    public void checkCacheDuration() {
        String cachePreference = prefHelper.getCacheDuration();

        if(!cachePreference.equals("")) {
            try {
                int cachePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        if(insertTask != null) {
            insertTask.cancel(true);
            insertTask = null;
        }
        if(retrieveTask!= null) {
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    private class InsertImageTask extends AsyncTask<List<imageModel>, Void, List<imageModel>> {

        @Override
        protected List<imageModel> doInBackground(List<imageModel>... lists) {
            List<imageModel> list = lists[0];

            // Need application context, hence AndroidViewModel was used
            ImageDao dao = ImageDatabase.getInstance(getApplication()).imageDao();

            dao.deleteAllImages();
            ArrayList<imageModel> newList = new ArrayList<>(list);
            List<Long> result = dao.insertAll(newList.toArray(new imageModel[0]));

            int i = 0;
            while (i < list.size()) {
//                updating the uuid in objects
                list.get(i).uuid = result.get(i).intValue();
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

    private class RetrieveImageTask extends AsyncTask <Void, Void, List<imageModel>> {

        @Override
        protected List<imageModel> doInBackground(Void... voids) {
            return ImageDatabase.getInstance(getApplication()).imageDao().getAllImages();
        }

        @Override
        protected void onPostExecute(List<imageModel> imageModels) {
            imagesRetrieved(imageModels);
            Toast.makeText(
                    getApplication(),
                    R.string.toast_data_retrieved_database,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
