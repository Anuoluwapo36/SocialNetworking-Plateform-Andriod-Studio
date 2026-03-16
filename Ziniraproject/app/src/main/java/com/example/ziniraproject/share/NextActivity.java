package com.example.ziniraproject.share;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ziniraproject.R;
import com.example.ziniraproject.database.CloudinaryUploader;
import com.example.ziniraproject.home.HomeActivity;
import com.example.ziniraproject.until.FireBaseMethod;
import com.example.ziniraproject.until.ImageLoader;
import com.example.ziniraproject.until.ImageManger;

import com.example.ziniraproject.until.StringManipulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NextActivity extends AppCompatActivity {


    public interface FirestoreSaveCallback {
        void onSuccess(String documentId);

        void onFailure(String errorMessage);
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FireBaseMethod mfireBaseMethod;


    private String mAppend = "file://";
    private int imageCount = 0;
    //widgets
    private EditText mCaption;
    private String imgUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        mfireBaseMethod = new FireBaseMethod(NextActivity.this);
        mCaption = (EditText) findViewById(R.id.caption);
        Log.d(TAG, "onCreate: got to the chosen image" + getIntent().getStringExtra(getString(R.string.selected_image)));

        setupFirebaseAuth();
        setImage();


        /**
         * Sets up the back arrow (ImageView with ID `ivBackArrow`) to act as a navigation button.
         *
         * When the back arrow is clicked, this listener logs a debug message and finishes
         * the current activity, effectively navigating the user back to the previous screen.
         *
         * This is typically used in `NextActivity` to allow users to go back from the share screen.
         */
        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing activity");
                finish();
            }
        });




        /**
         * Handles the logic for the Share button.
         * Validates the caption and uploads the selected image to Cloudinary.
         * After a successful upload, it saves the photo info to Firebase Realtime Database
         * and redirects the user to the HomeActivity.
         *
         * This is usually called when the user clicks the "Share" button after selecting an image and entering a caption.
         *
         * @param view The view that triggered this method (e.g., the Share button).
         */

        Button shareAction = (Button) findViewById(R.id.shareButton);
        shareAction.setOnClickListener(v -> {
            Toast.makeText(NextActivity.this, "Uploading to Cloudinary...", Toast.LENGTH_SHORT).show();
            String caption = mCaption.getText().toString();

            Intent intent = getIntent();
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Bitmap bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));

            if (bitmap != null) {
                uploadBitmapToCloudinary(bitmap, caption);
            } else if (imgUrl != null) {
                // Use Glide to load image from URI or path into Bitmap
                Glide.with(NextActivity.this)
                        .asBitmap()
                        .load(imgUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                uploadBitmapToCloudinary(resource, caption);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
            }
        });



    }

    /**
     * Uploads a bitmap image to Cloudinary and saves the result to Firebase Realtime Database.
     *
     * @param bitmap  The bitmap image to be uploaded.
     * @param caption The caption provided by the user, which may contain hashtags to be saved as tags.
     */
    private void uploadBitmapToCloudinary(Bitmap bitmap, String caption) {
        CloudinaryUploader.uploadBitmap(bitmap, new CloudinaryUploader.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                // Extract tags from caption using your getTags method
                String tags = StringManipulation.getTags(caption);

                mfireBaseMethod.saveToRealtimeDatabase(NextActivity.this, imageUrl, caption, tags, () -> {
                    Intent intent = new Intent(NextActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(NextActivity.this, "Cloudinary upload failed: " + errorMessage, Toast.LENGTH_LONG).show()
                );
            }
        });
    }







    //user later when i change storage to firebase storage
    private void someMethod() {
        /**
         * step 1
         * create a data model for photos
         *
         *
         * step 2
         * add propertoies to rge photo objctes (caption data, inageurl,photo id, tag
         *
         * step3
         * count the number of photot the user alreayd has
         *
         * step4
         *a) upload the photo to firebase storage and
         * B)insert the photo node
         * C)insert into user_photo
         */


    }

    /**
     * Sets the image to be displayed in the ImageView (`R.id.imageShare`) based on the intent extras.
     *
     * This method first checks if a `Bitmap` object was passed via the intent using the key
     * `R.string.selected_bitmap`. If it exists, the image is directly set on the ImageView.
     *
     * If no bitmap is found, it tries to retrieve an image URL using the key
     * `R.string.selected_image`. If a URL is found, it loads the image using a custom image
     * loader utility (`ImageLoader.loadImage()`).
     *
     * If neither a bitmap nor an image URL is present, it logs an error message.
     *
     * Use this method during `onCreate()` in `NextActivity` to properly show the image the user selected.
     */
    private void setImage() {
        Intent intent = getIntent();
        ImageView imageView = findViewById(R.id.imageShare);

        // Try to get Bitmap first
        Bitmap bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        // Fallback to image URL
        imgUrl = intent.getStringExtra(getString(R.string.selected_image));
        if (imgUrl != null) {
            ImageLoader.loadImage(imgUrl, imageView, null);
        } else {
            Log.e(TAG, "No image or bitmap found in intent.");
        }
    }




    /*
     * --------------------------------------firebase------------------------------------------------------------
     *
     *
     */


    /**
     * Sets up Firebase Authentication and a ValueEventListener for tracking changes in image count.
     *
     * - Initializes FirebaseAuth and FirebaseDatabase references.
     * - Registers an AuthStateListener to monitor user sign-in and sign-out events.
     * - Adds a ValueEventListener to listen to any changes in the database,
     *   specifically to track how many images the user has uploaded.
     *
     * This method should be called during Activity or Fragment setup (e.g., in `onCreate()`).
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user signed in" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");

                }
            }
        };
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageCount = mfireBaseMethod.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count " + imageCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

}
