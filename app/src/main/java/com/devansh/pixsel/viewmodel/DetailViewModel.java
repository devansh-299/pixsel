package com.devansh.pixsel.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import com.devansh.pixsel.model.ImageDatabase;
import com.devansh.pixsel.model.imageModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class DetailViewModel extends AndroidViewModel {

    public MutableLiveData<imageModel> imageLiveData = new MutableLiveData<>();

    private RetrieveImageTask task;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetch(int uuid){
        task = new RetrieveImageTask();
        task.execute(uuid);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(task != null){
            task.cancel(true);
            task= null;
        }
    }

    private class RetrieveImageTask extends AsyncTask<Integer, Void, imageModel> {

        @Override
        protected imageModel doInBackground(Integer... integers) {
            int uuid = integers[0];
            return ImageDatabase.getInstance(getApplication()).imageDao().getImage(uuid);
        }

        @Override
        protected void onPostExecute(imageModel imageModel) {
            imageLiveData.setValue(imageModel);
        }
    }

}
