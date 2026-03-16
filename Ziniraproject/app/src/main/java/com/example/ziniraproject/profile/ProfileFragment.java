package com.example.ziniraproject.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.example.ziniraproject.create.CreateActivity;
import com.example.ziniraproject.create.GalleryFragment;
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.Like;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.settings.AccountSettingsActivity;
import com.example.ziniraproject.until.BottomNavigationViewHelper;
import com.example.ziniraproject.until.FireBaseMethod;
import com.example.ziniraproject.until.GridViewAdapter;
import com.example.ziniraproject.until.ImageLoader;
import com.example.ziniraproject.models.UserSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * profile fragment
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener {
        Void onGridImageSelected(Photo photo, int activityNumber);
    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private TextView mPost, mFollowers, mFollowing, mUsername, mDisplayName, mAboutMe;
    private ProgressBar mprogressBar;
    private ShapeableImageView mProfilePhoto;

    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu, coverImg, coverPhoto;
    private BottomNavigationView bottomNavigationView;


    private Context context;
    private static final int ACTIVITY_NUM = 5;
    private static final int NUM_GRID_COLUMNS = 3;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FireBaseMethod mfireBaseMethod;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mPost = view.findViewById(R.id.Post);
        mFollowers = view.findViewById(R.id.followers);
        mFollowing = view.findViewById(R.id.following);
        mUsername = view.findViewById(R.id.userName);
        mDisplayName = view.findViewById(R.id.DisplayName);
        mAboutMe = view.findViewById(R.id.bioId);
        mprogressBar = view.findViewById(R.id.profileProgressBar);
        mProfilePhoto = view.findViewById(R.id.profileImageId);
        gridView = view.findViewById(R.id.gridView);
        toolbar = view.findViewById(R.id.profileToolBar);
        profileMenu = view.findViewById(R.id.settingsMenu);
        coverImg = view.findViewById(R.id.cameraCover);
        coverPhoto = view.findViewById(R.id.coverPicture);
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        context = getActivity();
        mfireBaseMethod = new FireBaseMethod(getActivity());
        Log.d(TAG, "onCreateView: started");

        setupBottomNavigationView();
        settingsDisplay();
        setupFirebaseAuth();
        setupGridView();
        getFollowersCount();
        getFollowingCount();
        getPostCount();

        Button buttonEditProfile = view.findViewById(R.id.editProfile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to: " + context.getString(R.string.edit_profile_fragment));

                Intent intent = new Intent(context, AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) context;

        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());

        }
        super.onAttach(context);
    }


    private void setupGridView() {
        Log.d(TAG, "setupGridView: setting up image grid");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_date_created));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Collect photos in ascending order
                ArrayList<Photo> tempPhotos = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                        if (objectMap == null) {
                            Log.e(TAG, "onDataChange: Null objectMap for photo ID: " + singleSnapshot.getKey());
                            continue;
                        }

                        photo.setCaption(getSafeString(objectMap, R.string.field_caption));
                        photo.setTags(getSafeString(objectMap, R.string.field_tags));
                        photo.setPhoto_id(getSafeString(objectMap, R.string.field_photo_id));
                        photo.setUser_id(getSafeString(objectMap, R.string.field_user_id));
                        photo.setDate_created(getSafeString(objectMap, R.string.field_date_created));
                        photo.setImage_path(getSafeString(objectMap, R.string.field_image_path));
                        if (photo.getImage_path().isEmpty()) {
                            Log.w(TAG, "onDataChange: Skipping photo with empty image_path, ID: " + singleSnapshot.getKey());
                            continue;
                        }

                        ArrayList<Comment> comments = new ArrayList<>();
                        DataSnapshot commentsSnapshot = singleSnapshot.child(getString(R.string.field_comments));
                        if (commentsSnapshot.exists()) {
                            for (DataSnapshot dSnapshot : commentsSnapshot.getChildren()) {
                                Comment comment = dSnapshot.getValue(Comment.class);
                                if (comment != null) {
                                    comment.setUser_id(comment.getUser_id() != null ? comment.getUser_id() : "");
                                    comment.setComment(comment.getComment() != null ? comment.getComment() : "");
                                    comment.setDate_created(comment.getDate_created() != null ? comment.getDate_created() : "");
                                    comment.setVoice_url(comment.getVoice_url() != null ? comment.getVoice_url() : "");
                                    comments.add(comment);
                                }
                            }
                        }
                        photo.setComments(comments);

                        List<Like> likeList = new ArrayList<>();
                        DataSnapshot likesSnapshot = singleSnapshot.child(getString(R.string.field_likes));
                        if (likesSnapshot.exists()) {
                            for (DataSnapshot dSnapshot : likesSnapshot.getChildren()) {
                                Like like = dSnapshot.getValue(Like.class);
                                if (like != null && like.getUser_id() != null) {
                                    likeList.add(like);
                                }
                            }
                        }
                        photo.setLikes(likeList);

                        tempPhotos.add(photo);
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: Error processing photo ID: " + singleSnapshot.getKey(), e);
                    }
                }

                for (int i = tempPhotos.size() - 1; i >= 0; i--) {
                    photos.add(tempPhotos.get(i));
                }
                Log.d(TAG, "onDataChange: Loaded " + photos.size() + " photos");

                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<>();
                for (Photo photo : photos) {
                    String imagePath = photo.getImage_path();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        imgUrls.add(imagePath);
                        Log.d(TAG, "onDataChange: image url: " + imagePath + ", date: " + photo.getDate_created());
                    }
                }
                GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.layout_grid_image_view, "", imgUrls);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Query failed: ", error.toException());
                Toast.makeText(context, "Failed to load photos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSafeString(Map<String, Object> map, int keyResId) {
        Object value = map.get(getString(keyResId));
        return value != null ? value.toString() : "";
    }


    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getSettings().getUsername());
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getSettings().getAbout());
        Log.d(TAG, "Cover Photo URL from Firebase: " + userSettings.getSettings().getCover_photo());



//        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        ImageLoader.loadImage(settings.getProfile_photo(), mProfilePhoto, null);
        ImageLoader.loadImage(settings.getCover_photo(),coverPhoto, null);
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mAboutMe.setText(settings.getAbout());


        mprogressBar.setVisibility(View.GONE);
        coverImg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateActivity.class);
            intent.putExtra("task", 3); // cover photo task
            startActivity(intent);


        });

        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("image_type", "cover");
        fragment.setArguments(args);




    }


    /**
     * settings display for menu
     */
    public void settingsDisplay() {
        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(context, AccountSettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }


    /**
     * setup bottom navigation view
     */

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(context, getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void getPostCount() {
        mPostsCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int postCount = 0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    if (objectMap != null && !getSafeString(objectMap, R.string.field_image_path).isEmpty()) {
                        postCount++;
                    } else {
                        Log.w(TAG, "getPostCount: Skipping invalid photo ID: " + singleSnapshot.getKey());
                    }
                }
                mPost.setText(String.valueOf(postCount));
                Log.d(TAG, "getPostCount: Posts: " + postCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getPostCount: Query failed: ", error.toException());
            }
        });
    }

    private void getFollowersCount() {
        mFollowersCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followersCount = 0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    followersCount++;
                }
                mFollowers.setText(String.valueOf(followersCount));
                Log.d(TAG, "getFollowersCount: Followers: " + followersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getFollowersCount: Query failed: ", error.toException());
            }
        });
    }

    private void getFollowingCount() {
        mFollowingCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followingCount = 0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    followingCount++;
                }
                mFollowing.setText(String.valueOf(followingCount));
                Log.d(TAG, "getFollowingCount: Following: " + followingCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getFollowingCount: Query failed: ", error.toException());
            }
        });
    }

    /*
     * --------------------------------------firebase------------------------------------------------------------
     *
     *
     */


    /**
     * responsible for setting up firebase auth
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
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
                //retrieve data from firebase
                //set profile widgets
                setProfileWidgets(mfireBaseMethod.getUserSettings(dataSnapshot));


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
