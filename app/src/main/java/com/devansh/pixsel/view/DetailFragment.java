package com.devansh.pixsel.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devansh.pixsel.R;
import com.devansh.pixsel.model.imageModel;
import com.devansh.pixsel.util.Util;
import com.devansh.pixsel.viewmodel.DetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailFragment extends Fragment {


    private int imageidvalue;
    DetailViewModel viewModel;

    @BindView(R.id.image_detail)
    ImageView imageDetail;

    @BindView(R.id.name_detail)
    TextView imageName;

    @BindView(R.id.image_purpose)
    TextView imagePurpose;

    @BindView(R.id.image_temperament)
    TextView imageTemperament;

    @BindView(R.id.image_lifespan)
    TextView imageLifeSpan;



    public DetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()!= null){
            imageidvalue = DetailFragmentArgs.fromBundle(getArguments()).getImageid();    // for getting image id value
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.fetch(imageidvalue);
        observerViewModel();

    }

    private  void observerViewModel(){
        viewModel.imageLiveData.observe(this,imageModels -> {
            if(imageModels != null && imageModels instanceof imageModel & getContext()!= null){
                imageName.setText(imageModels.imageName);
                imagePurpose.setText(imageModels.imageSize);
                imageTemperament.setText(imageModels.temperament);
                imageLifeSpan.setText(imageModels.imageDate);
                Util.loadImage(imageDetail,imageModels.imageUrl, new CircularProgressDrawable(getContext()));

            }
        });
    }


}
