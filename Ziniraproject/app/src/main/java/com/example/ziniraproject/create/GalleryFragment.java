package com.example.ziniraproject.create;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ziniraproject.R;
import com.example.ziniraproject.database.CloudinaryUploader;
import com.example.ziniraproject.settings.AccountSettingsActivity;
import com.example.ziniraproject.share.NextActivity;
import com.example.ziniraproject.share.ShareActivity;
import com.example.ziniraproject.until.FilePaths;
import com.example.ziniraproject.until.FileSearch;
import com.example.ziniraproject.until.FireBaseMethod;
import com.example.ziniraproject.until.GridViewAdapter;
import com.example.ziniraproject.until.ImageLoader;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar progressBar;
    private Spinner directorySpinner;
    //vars
    private ArrayList<String> directories;
    private String mAppend = "file://";
    private String mSelectedImage;
    private static final int DEFAULT_IMAGE = R.drawable.z_logo_foreground;
    String imageType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = view.findViewById(R.id.galleryImageview);
        gridView = view.findViewById(R.id.gridViewGallery);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started ");


        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment");
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        Button nextAction = (Button) view.findViewById(R.id.nextButton);


//        nextAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigation to final screen or upload profile image");
//
//                if (!isRootTask()) {
//                    // Profile image upload
//                    Log.d(TAG, "onClick: Uploading profile image...");
//                    Toast.makeText(getActivity(), "Uploading profile image...", Toast.LENGTH_SHORT).show();
//
//                    CloudinaryUploader.uploadImage(mSelectedImage, new CloudinaryUploader.UploadCallback() {
//                        @Override
//                        public void onSuccess(String imageUrl) {
//                            FireBaseMethod firebaseMethod = new FireBaseMethod(getActivity());
//                            firebaseMethod.setProfilePhoto(imageUrl);
//                            Toast.makeText(getActivity(), "Profile updated completed", Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
//                        }
//
//                        @Override
//                        public void onFailure(String errorMessage) {
//                            Toast.makeText(getActivity(), "Upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                }
//                else {
//                    // Post upload
//                    Intent intent = new Intent(getActivity(), NextActivity.class);
//                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
//                    startActivity(intent);
//                }
//            }
//        });

        nextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to final screen or upload profile image");

                int task = ((CreateActivity) getActivity()).getTask();

                if (task == 1) {
                    // Profile image upload
                    Log.d(TAG, "onClick: Uploading profile image...");
                    Toast.makeText(getActivity(), "Uploading profile image...", Toast.LENGTH_SHORT).show();

                    CloudinaryUploader.uploadImage(mSelectedImage, new CloudinaryUploader.UploadCallback() {
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

                } else if (task == 3) {
                    // Cover image upload
                    Log.d(TAG, "onClick: Uploading cover image...");
                    Toast.makeText(getActivity(), "Uploading cover image...", Toast.LENGTH_SHORT).show();

                    CloudinaryUploader.uploadImage(mSelectedImage, new CloudinaryUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            FireBaseMethod firebaseMethod = new FireBaseMethod(getActivity());
                            firebaseMethod.setCoverPhoto(imageUrl);  // ✅ Make sure this method exists
                            Toast.makeText(getActivity(), "Cover photo updated", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(getActivity(), "Upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    // Post upload
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }
            }
        });



        init();


        return view;
    }


    private boolean isRootTask() {
        return ((CreateActivity) getActivity()).getTask() == 0;
    }

    private void init() {
        FilePaths filePaths = new FilePaths();

        // Get directories
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        // Always add camera directory
        directories.add(filePaths.CAMERA_DIR);

        ArrayList<String> directoriesNames = new ArrayList<>();
        for (String dir : directories) {
            int index = dir.lastIndexOf("/");
            String name = dir.substring(index).replace("/", "");
            directoriesNames.add(name);
        }

        // Set up spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, directoriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        // Set up listener
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 🔁 Automatically find the first folder with at least one image
        for (int i = 0; i < directories.size(); i++) {
            ArrayList<String> images = FileSearch.getFilePaths(directories.get(i));
            if (images != null && !images.isEmpty()) {
                setupGridView(directories.get(i));        // Load images
                directorySpinner.setSelection(i);         // Select folder in spinner
                break;
            }
        }
    }



    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directories chosen");
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        // If no images found, don't proceed
        if (imgURLs == null || imgURLs.isEmpty()) {
            Log.w(TAG, "setupGridView: No images found in the selected directory.");
            galleryImage.setImageResource(DEFAULT_IMAGE);  // Optional: show a default image
            return;
        }

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        // Use the grid adapter to adapter the image URLs into the grid view
        GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.layout_grid_image_view, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        // set teh first image to the be display when the activity fragment view is inflated

        try {

            loadImage(imgURLs.get(0), galleryImage, progressBar);
            mSelectedImage = imgURLs.get(0);


        }catch (ArrayIndexOutOfBoundsException e){
            Log.d(TAG, "setupGridView: ArrayIndexOutOfBoundsException:  " + e.getMessage());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected image at position " + position);
                loadImage(imgURLs.get(position), galleryImage, progressBar);
                mSelectedImage = imgURLs.get(position);
            }
        });
    }


    public static void loadImage(String url, ImageView imageView, ProgressBar mprogressBar) {
        Context context = imageView.getContext();

        // Check if context is valid and not destroyed
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return; // Don't load image into a destroyed activity
            }
        }

        if (mprogressBar != null) {
            mprogressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_IMAGE)
                .error(DEFAULT_IMAGE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (mprogressBar != null) {
                            mprogressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, @NonNull Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        if (mprogressBar != null) {
                            mprogressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }
}
