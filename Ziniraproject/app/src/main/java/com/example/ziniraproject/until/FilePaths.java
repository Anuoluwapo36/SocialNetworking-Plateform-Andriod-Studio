package com.example.ziniraproject.until;

import android.os.Environment;

public class FilePaths {

    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();


    public  String PICTURES = ROOT_DIR + "/Pictures";

    public  String CAMERA_DIR = ROOT_DIR + "/DCIM/camera";



    public String FIREBASE_STORAGE_IMAGES = "photos/users/";
}
