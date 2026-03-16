package com.example.ziniraproject.create;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.example.ziniraproject.database.CloudinaryUploader;
import com.example.ziniraproject.share.NextActivity;
import com.example.ziniraproject.until.FireBaseMethod;

public class PhotoFragment extends Fragment {

    private static final String TAG = "PhotoFragment";
    private static final int PHOTO_FRAGMENT = 1;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private int task = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started");

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            task = intent.getIntExtra("task", 0);
        }

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Log.d(TAG, "Camera result received successfully.");

                        Bundle extras = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");

                        if (bitmap != null) {
                            if (isProfileUpload()) {
                                Toast.makeText(getActivity(), "Uploading profile image...", Toast.LENGTH_SHORT).show();

                                CloudinaryUploader.uploadBitmap(bitmap, new CloudinaryUploader.UploadCallback() {
                                    @Override
                                    public void onSuccess(String imageUrl) {
                                        FireBaseMethod firebaseMethod = new FireBaseMethod(getActivity());
                                        firebaseMethod.setProfilePhoto(imageUrl);
                                        Toast.makeText(getActivity(), "Profile updated completed", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getActivity(), "Upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                // Upload post
                                Intent postIntent = new Intent(getActivity(), NextActivity.class);
                                postIntent.putExtra(getString(R.string.selected_bitmap), bitmap);
                                startActivity(postIntent);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Button cameraActivate = view.findViewById(R.id.camerId);
        cameraActivate.setOnClickListener(v -> {
            Log.d(TAG, "onClick: camera button pressed");

            int currentTab = ((CreateActivity) getActivity()).getCurrentTabNumber();
            if (currentTab == PHOTO_FRAGMENT) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onClick: starting camera.");
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                } else {
                    Log.d(TAG, "onClick: camera permission denied. Relaunching CreateActivity.");
                    Intent relaunch = new Intent(getActivity(), CreateActivity.class);
                    relaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(relaunch);
                }
            }
        });

        return view;
    }

    private boolean isProfileUpload() {
        return task == 1;
    }
}
