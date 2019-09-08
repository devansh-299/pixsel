package com.devansh.pixsel.view;

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
import android.os.Bundle;

import com.devansh.pixsel.R;
import com.devansh.pixsel.viewmodel.DetailViewModel;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SMS = 299;
    private NavController navController;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);       // for getting back button on top bar!

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }


    // We need permission for detail fragment but since fragment cannot ask permission we need enclosing activity to ask permission
    // But at a stage we need to check that we ask permission only when out fragment is Detail Fragment

    public void checkSmsPermission() {

        // In android either we can directly ask permission or we can show RATIONALE!
        // Majorly we show a rationale only when we asked the permission once but we were denied and now we want to ask a

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Send SMS Permission")
                        .setMessage("This app requires access to send SMS")
                        .setPositiveButton("AskMe", (dialog, which) -> {
                            requestPermission();
                        })
                        .setNegativeButton("No", ((dialogInterface, i) -> {
                            notifyDetailFragment(false);
                        }))
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
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

    private void notifyDetailFragment(Boolean permissionGrantted) {

        Fragment activeFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();  // basically we can be at this point be in

        // any fragment detail or list , thus by using this we are ensuring we are in Detail Fragment

        if (activeFragment instanceof DetailFragment) {
            ((DetailFragment) activeFragment).onPermissionResult(permissionGrantted);
        }


    }
}
