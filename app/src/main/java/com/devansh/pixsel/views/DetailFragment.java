package com.devansh.pixsel.views;


import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devansh.pixsel.R;
import com.devansh.pixsel.model.ImagePalette;
import com.devansh.pixsel.model.SmsInfo;
import com.devansh.pixsel.model.imageModel;
import com.devansh.pixsel.util.Util;
import com.devansh.pixsel.viewmodel.DetailViewModel;

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

    private Boolean sendSmsStarted = false;
    private  imageModel currentImage;

    View view;

    public DetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()!= null){
            imageidvalue = DetailFragmentArgs
                    .fromBundle(getArguments())
//                    for getting image id value
                    .getImageid();
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.fetch(imageidvalue);
        observerViewModel();

    }

    public void setColor(int color, int actionColor) {

        View root = view.getRootView();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        String hexColor = String.format("#%06X", (0xFFFFFF & actionColor));
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        }
        root.setBackgroundColor(color);
    }

    private  void observerViewModel() {
        viewModel.imageLiveData.observe(this,imageModels -> {
            if(imageModels != null && imageModels instanceof imageModel && getContext() != null) {

                // saving this in currentImage variable for sms part
                currentImage = imageModels;

                imageName.setText(imageModels.imageName);
                imagePurpose.setText(imageModels.imageSize);
                imageTemperament.setText(imageModels.temperament);
                imageLifeSpan.setText(imageModels.imageDate);
                Util.loadImage(
                        imageDetail,
                        imageModels.imageUrl,
                        new CircularProgressDrawable(getContext()));
                if (imageModels.imageUrl != null) {
                    setupBackgroundColor(imageModels.imageUrl);
                }
            }
        });
    }

    private void setupBackgroundColor(String url) {

        /*
            By using glide we are now retrieving image Bitmap from image url
            because palette can work on bitmap not url
         */
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            int imageColor = palette.getLightMutedSwatch().getRgb();
                            int actionBarColor = palette.getMutedSwatch().getRgb();
                            imageName.setBackgroundColor(imageColor);
                            ImagePalette myPalette = new ImagePalette(imageColor);
                            setColor(imageColor, actionBarColor);
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_sms: {
                if(!sendSmsStarted) {
                    sendSmsStarted = true;
                    ((MainActivity)getActivity()).checkSmsPermission();
                }
                break;
            }
            case R.id.action_share: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Checkout this Image");
                intent.putExtra(Intent.EXTRA_TEXT,
                        currentImage.imageName + " lifespan " + currentImage.imageDate);

                /*
                    we have to pass Bitmap here instead of Image url to get it working
                    for DO IT LATER
                 */
                intent.putExtra(Intent.EXTRA_STREAM, currentImage.imageUrl);
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPermissionResult(Boolean permissionGranted) {

        if (isAdded() && sendSmsStarted && permissionGranted) {
            SmsInfo smsInfo = new SmsInfo(
                    "",
                    currentImage.imageName + " lifespan " + currentImage.imageDate,currentImage.imageUrl);

            SendSmsDialogBinding dialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.dialog_send_sms,
                    null,
                    false
            );

            new AlertDialog.Builder(getContext())
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton("Send SMS", ((dialog, which) -> {
                        if(!dialogBinding.smsDestination.getText().toString().isEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.getText().toString();
                            sendSms(smsInfo);
                        }
                    }))
                    .setNegativeButton("Cancel", ((dialog, which) -> {}))
                    .show();

//            for resetting the process
            sendSmsStarted = false;
            dialogBinding.setSmsInfo(smsInfo);
        }
    }

    private void sendSms(SmsInfo smsInfo) {

        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null);
    }
}
