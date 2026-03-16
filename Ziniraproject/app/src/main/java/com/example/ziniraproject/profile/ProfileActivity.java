package com.example.ziniraproject.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.ziniraproject.R;
import com.example.ziniraproject.until.ViewCommentFragment;
import com.example.ziniraproject.until.ViewPostFragment;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.until.ViewProfileFragment;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener{
    private static final String TAG = "ProfileActivity";


    @Override
    public void onCommentThreadSelected(Photo photo) {
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");
        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public Void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber );
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containers, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

        return null;
    }
    private Context context = ProfileActivity.this;

    private static  final  int NUM_GRID_COLUMNS = 3;

    private ShapeableImageView mProfilePhoto;

    private ProgressBar mprogressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG , "onCreate: started.");
        init();


    }
    private  void init(){
        Log.d(TAG, "init: initializing: " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");

            if (intent.hasExtra(getString(R.string.intent_user))){
                Log.d(TAG, "init: inflating view profile");


                ViewProfileFragment fragment = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.intent_user),intent.getParcelableExtra(getString(R.string.intent_user)));
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.containers,fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();



            }else {

                Log.d(TAG, "init: intent does not contain user object");
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();


            }



        }else {
            Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containers,fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }


     }



}