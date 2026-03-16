package com.example.ziniraproject.create;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ziniraproject.R;
import com.example.ziniraproject.until.SectionsPagerAdapter1;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private int task;

    private Context context = CreateActivity.this;
    private ViewPager2 viewPager2;
    private String imageType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Log.d(TAG, "onCreate: started.");
        task = getIntent().getIntExtra("task", 0);

        if (hasPermissions()) {
            setupViewPager();
        } else {
            requestPermissions();
        }




    }


    private void setupViewPager() {
        Log.d(TAG, "setupViewPager: setting up ViewPager2.");
        viewPager2 = findViewById(R.id.viewpager_container);
        SectionsPagerAdapter1 adapter = new SectionsPagerAdapter1(this);


        adapter.addFragment(new GalleryFragment(), "Gallery", R.drawable.ic_photo_camera);
        adapter.addFragment(new PhotoFragment(), "Photo", R.drawable.ic_gallery_icon);

        viewPager2.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(adapter.getTabTitle(position));
        }).attach();
    }

    public int getTask() {
        return getIntent().getIntExtra("task", 0);
    }



    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA
                    },
                    VERIFY_PERMISSIONS_REQUEST);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    VERIFY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (hasPermissions()) {
                setupViewPager();
            } else {
                Toast.makeText(this, "Permissions are required to access gallery or camera.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public int getCurrentTabNumber() {
        return viewPager2.getCurrentItem();
    }


}