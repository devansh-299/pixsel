package com.devansh.pixsel.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.devansh.pixsel.R;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SMS = 299;
    private NavController navController;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        setMyActionBar();
        navController = Navigation.findNavController(this, R.id.fragment);
//        for getting back button on top bar
        NavigationUI.setupActionBarWithNavController(this, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }

    /*
        Permission is needed for detail fragment but since fragment cannot ask permission, the
        enclosing activity needs to ask the permission
     */
    public void checkSmsPermission() {
        /*
            Theory:
                - In android either one can directly ask permission or show RATIONALE
                - Majorly one shows a rationale only when permission was once asked but denied and
                  now there's a need to ask again
         */
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_sms_permission)
                        .setMessage(R.string.message_sms_permission)
                        .setPositiveButton(R.string.btn_positive_sms_permission,
                                (dialog, which) -> requestPermission())
                        .setNegativeButton(R.string.btn_negative_sms_permission,
                                ((dialogInterface, i) -> notifyDetailFragment(false)))
                        .show();
            } else {
                requestPermission();
            }

        } else {
            notifyDetailFragment(true);
        }
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            // TODO: implement functionality for other requests
            case REQUEST_CODE_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true);
                } else {
                    notifyDetailFragment(false);
                }
                break;
            }
        }
    }

    private void notifyDetailFragment(Boolean permissionGranted) {

        Fragment activeFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (activeFragment instanceof DetailFragment) {
            ((DetailFragment) activeFragment).onPermissionResult(permissionGranted);
        }
    }

    public void setMyActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        getSupportActionBar().setTitle(R.string.application_name);
    }
}
