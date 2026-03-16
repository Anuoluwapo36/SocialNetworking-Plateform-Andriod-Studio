package com.example.ziniraproject.until;

import android.os.Build;
import android.Manifest;

public class Permissions {

    // For devices below Android 13
    public static final String[] LEGACY_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // Only camera
    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    // Optional write storage, though this is same as camera in your code
    public static final String[] WRITE_STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Dynamically set based on Android version
    public static final String[] PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
            };
        } else {
            PERMISSIONS = LEGACY_PERMISSIONS;
        }
    }
}
