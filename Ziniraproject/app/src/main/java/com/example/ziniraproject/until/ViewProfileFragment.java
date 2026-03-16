package com.example.ziniraproject.until;

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
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.Like;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.models.UserSettings;
import com.example.ziniraproject.profile.ProfileActivity;
import com.example.ziniraproject.settings.AccountSettingsActivity;
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
public class ViewProfileFragment extends Fragment {
    private static final String TAG = "ViewProfileFragment";


    public interface OnGridImageSelectedListener {
        Void onGridImageSelected(Photo photo, int activityNumber);
    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private TextView mPost, mFollowers, mFollowing, mUsername, mDisplayName, mAboutMe;
    private Button buttonEditProfile;
    private Button mFollow, mUnfollow;
    private ProgressBar mprogressBar;
    private ShapeableImageView mProfilePhoto;

    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu, BackArrow;
    private BottomNavigationView bottomNavigationView;


    private Context context;
    private static final int ACTIVITY_NUM = 5;
    private static final int NUM_GRID_COLUMNS = 3;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    //vars
    private User mUser;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

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
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        mFollow = (Button) view.findViewById(R.id.follow);
        mUnfollow = (Button) view.findViewById(R.id.Unfollow);
        buttonEditProfile = (Button) view.findViewById(R.id.editProfile);
        BackArrow = view.findViewById(R.id.backArrow);


        context = getActivity();

        Log.d(TAG, "onCreateView: started");

        try {
            mUser = getUserFromBundle();
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (mUser.getUser_id().equals(currentUserId)) {
                // This is the current user's own profile
                mFollow.setVisibility(View.GONE);
                mUnfollow.setVisibility(View.GONE);
                buttonEditProfile.setVisibility(View.VISIBLE);
                profileMenu.setVisibility(View.VISIBLE);
            } else {
                // This is someone else's profile
                isFollowing(); // only check if following for others
            }

            init();

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: NullPointerException " + e.getMessage());
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();

        }

        setupBottomNavigationView();
        settingsDisplay();
        setupFirebaseAuth();
//        isFollowing();
        getFollowingCount();
        getFollowersCount();
        getPostCount();



        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: " + mUser.getUsername());
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String targetUserId = mUser.getUser_id();
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(currentUserId)
                        .child(targetUserId)
                        .child(getString(R.string.field_user_id))
                        .setValue(targetUserId);
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(targetUserId)
                        .child(currentUserId)
                        .child(getString(R.string.field_user_id))
                        .setValue(currentUserId);
                mFollowersCount++;
                mFollowers.setText(String.valueOf(mFollowersCount));
                setFollowing();
                sendFollowNotification(targetUserId);



            }
        });




        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String targetUserId = mUser.getUser_id();
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(currentUserId)
                        .child(targetUserId)
                        .removeValue();
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(targetUserId)
                        .child(currentUserId)
                        .removeValue();
                mFollowersCount = Math.max(0, mFollowersCount - 1);
                mFollowers.setText(String.valueOf(mFollowersCount));
                setUnfollowing();
            }
        });



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
    private void sendFollowNotification(String toUserId) {
        String fromUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_notifications))
                .child(toUserId)
                .push();

        Map<String, Object> notifData = new HashMap<>();
        notifData.put("type", "follow");
        notifData.put("from_user_id", fromUserId);
        notifData.put("timestamp", System.currentTimeMillis());
        notifData.put("read", false);

        notifRef.setValue(notifData);
        Log.d("NotificationDebug", "Notification created for " + toUserId);
    }





    private void isFollowing() {
        Log.d(TAG, "isFollowing: checking if following this user is ture");
        setUnfollowing();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user " + singleSnapshot.getValue());
                    setFollowing();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void getFollowersCount() {
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getFollowersCount: dataSnapshot = " + dataSnapshot.toString());
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String userId = singleSnapshot.child("user_id").getValue(String.class);
                    Log.d(TAG, "onDataChange: found follower user_id = " + userId);
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getFollowersCount: onCancelled: ", databaseError.toException());
                mprogressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getFollowingCount() {
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPostCount() {
        mPostsCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found  post:  " + singleSnapshot.getValue());
                    mPostsCount++;


                }
                mPost.setText(String.valueOf(mPostsCount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setFollowing() {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        buttonEditProfile.setVisibility(View.GONE);
        profileMenu.setVisibility(View.GONE);


    }

    private void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        buttonEditProfile.setVisibility(View.GONE);
        profileMenu.setVisibility(View.GONE);


    }

    private void setCurrentUserProfile() {
        Log.d(TAG, "setFollowing: updating UI for showing this user their own profile");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.GONE);
        buttonEditProfile.setVisibility(View.VISIBLE);
        profileMenu.setVisibility(View.VISIBLE);


    }


//    private void init() {
//        Log.d(TAG, "init: initializing");
//
//        //set the profile widgets
//        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
//        Query query1 = reference1.child(getString(R.string.dbname_user_account_settings))
//                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    Log.d(TAG, "onDataChange: found user " + singleSnapshot.getValue(UserAccountSettings.class).toString());
//
//                    UserSettings settings = new UserSettings();
//                    settings.setUser(mUser);
//                    settings.setSettings(singleSnapshot.getValue(UserAccountSettings.class));
//                    setProfileWidgets(settings);
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//        //get the user profile photo
//
//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
//        Query query2 = reference2
//                .child(getString(R.string.dbname_user_photos))
//                .child(mUser.getUser_id());
//        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<Photo> photos = new ArrayList<Photo>();
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//
//                    Photo photo = new Photo();
//                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
//                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
//                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
//                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
//                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
//                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
//
//                    ArrayList<Comment> comments = new ArrayList<Comment>();
//                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
//                        Comment comment = new Comment();
//                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                        comment.setVoice_url(dSnapshot.getValue(Comment.class).getVoice_url());
//                        comments.add(comment);
//
//
//                    }
//                    photo.setComments(comments);
//
//                    List<Like> likeList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_likes)).getChildren()) {
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likeList.add(like);
//
//
//                    }
//                    photo.setLikes(likeList);
//                    photos.add(photo);
//
//
//                }
//                setupImageGrid(photos);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG, "onCancelled: query cancel");
//
//            }
//        });
//
//
//    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // Set the profile widgets
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded() || getView() == null) {
                    Log.w(TAG, "Fragment is not attached or view is null");
                    return;
                }
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "No user account settings found for user_id: " + mUser.getUser_id());
                    mprogressBar.setVisibility(View.GONE);
                    return;
                }
                Log.d(TAG, "onDataChange: DataSnapshot=" + dataSnapshot.getValue());
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    UserAccountSettings settings = singleSnapshot.getValue(UserAccountSettings.class);
                    if (settings != null) {
                        Log.d(TAG, "onDataChange: found user, username=" + settings.getUsername() +
                                ", display_name=" + settings.getDisplay_name() +
                                ", about=" + settings.getAbout());
                        UserSettings userSettings = new UserSettings();
                        userSettings.setUser(mUser);
                        userSettings.setSettings(settings);
                        setProfileWidgets(userSettings);
                    } else {
                        Log.w(TAG, "UserAccountSettings is null for snapshot: " + singleSnapshot.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
                mprogressBar.setVisibility(View.GONE);
            }
        });

        // Get the user profile photos
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2.child(getString(R.string.dbname_user_photos))
                .child(mUser.getUser_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Photo> photos = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    photo.setCaption(getSafeString(objectMap, R.string.field_caption));
                    photo.setTags(getSafeString(objectMap, R.string.field_tags));
                    photo.setPhoto_id(getSafeString(objectMap, R.string.field_photo_id));
                    photo.setUser_id(getSafeString(objectMap, R.string.field_user_id));
                    photo.setDate_created(getSafeString(objectMap, R.string.field_date_created));
                    photo.setImage_path(getSafeString(objectMap, R.string.field_image_path));

                    ArrayList<Comment> comments = new ArrayList<>();
                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
                        Comment comment = dSnapshot.getValue(Comment.class);
                        if (comment != null) {
                            comments.add(comment);
                        }
                    }
                    photo.setComments(comments);

                    List<Like> likeList = new ArrayList<>();
                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_likes)).getChildren()) {
                        Like like = dSnapshot.getValue(Like.class);
                        if (like != null) {
                            likeList.add(like);
                        }
                    }
                    photo.setLikes(likeList);
                    photos.add(photo);
                }
                setupImageGrid(photos);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private String getSafeString(Map<String, Object> map, int keyResId) {
        Object value = map.get(getString(keyResId));
        return value != null ? value.toString() : "";
    }

    private void setupImageGrid(final ArrayList<Photo> photos) {
        //setup image grid

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        ArrayList<String> imgUrls = new ArrayList<String>();
        for (int i = 0; i < photos.size(); i++) {
            imgUrls.add(photos.get(i).getImage_path());
            Log.d(TAG, "onDataChange: image url: " + imgUrls.get(i));

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

    private User getUserFromBundle() {
        Log.d(TAG, "getUserFromBundle: argument " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.intent_user));

        } else {
            return null;
        }
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


    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getSettings().getUsername());
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getSettings().getAbout());


//        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        ImageLoader.loadImage(settings.getProfile_photo(), mProfilePhoto, null);
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mAboutMe.setText(settings.getAbout());
        mPost.setText(String.valueOf(settings.getPost()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mprogressBar.setVisibility(View.GONE);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to screen");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();

            }
        });


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
