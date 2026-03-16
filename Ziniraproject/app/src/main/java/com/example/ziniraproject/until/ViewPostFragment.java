package com.example.ziniraproject.until;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.Like;
import com.example.ziniraproject.models.Notification;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    public  interface  OnCommentThreadSelectedListener{
        void onCommentThreadSelected(Photo photo);
    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewPostFragment() {
        super();
        Log.d(TAG, "ViewPostFragment: started");
        setArguments(new Bundle());
    }

    //widget
    private SquareImageView mPostImage;
    private BottomNavigationView bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, usernameUnderPost,mComments;
    private ImageView mBackArrow, mEllipses, mHeartRed, mHeartWhite, mProfileImage, mComment;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FireBaseMethod mfireBaseMethod;
    private String mLikesStrings;


    //vars

    private Photo mphoto;
    private String photoUsername = "";
    private String photoUrl = "";
    private int mActivityNumber = 0;
    private UserAccountSettings mUserAccountSettings;

    private GestureDetector mGestureDetector;
    private Heart mHeart;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private User mCurrentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mBackLabel = (TextView) view.findViewById(R.id.BackLabel);
        mEllipses = (ImageView) view.findViewById(R.id.ellipsesId);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mUsername = (TextView) view.findViewById(R.id.usernameText);
        mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
        mHeartRed = (ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) view.findViewById(R.id.image_heart_white);
        mProfileImage = (ImageView) view.findViewById(R.id.profileImageId);
        mLikes = (TextView) view.findViewById(R.id.image_likes);
        mComments=(TextView) view.findViewById(R.id.image_comment_link);
        usernameUnderPost = view.findViewById(R.id.image_username);
        mComment = view.findViewById(R.id.speech_bubble);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        mHeart = new Heart(mHeartWhite, mHeartRed);




        setupFirebaseAuth();
        setupBottomNavigationView();


        return view;
    }

//    private void  init(){
//
//
//        try {
////            mphoto = getPhotoFromBundle();
//            ImageLoader.loadImage(getPhotoFromBundle().getImage_path(), mPostImage, null);
//            mActivityNumber = getActivityNumFromBundle();
//            String photo_id = getPhotoFromBundle().getPhoto_id();
//            Query query = FirebaseDatabase.getInstance().getReference()
//                    .child(getString(R.string.dbname_photos))
//                    .orderByChild(getString(R.string.field_photo_id))
//                    .equalTo(photo_id);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                        Photo newPhoto = new Photo();
//                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
//                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
//                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
//                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
//                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
//                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
//
//
//                        List<Comment> commentList = new ArrayList<Comment>();
//                        for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
//                            Comment comment = new Comment();
//                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                            comment.setVoice_url(dSnapshot.getValue(Comment.class).getVoice_url());
//                            commentList.add(comment);
//
//
//                        }
//                        newPhoto.setComments(commentList);
//                        mphoto = newPhoto;
//                        getCurrentUser();
//                        getPhotoDetails();
////                        getLikesStrings();
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//
//
////            getPhotoDetails();
////            getLikesStrings();
//
//        } catch (NullPointerException e) {
//            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
//        }
//
//
//    }

private void init() {
    try {
        mphoto = getPhotoFromBundle();
        if (mphoto == null || mphoto.getPhoto_id() == null) {
            Log.e(TAG, "init: Photo or photo_id is null");
            return;
        }
        ImageLoader.loadImage(mphoto.getImage_path(), mPostImage, null);
        mActivityNumber = getActivityNumFromBundle();
        String photo_id = mphoto.getPhoto_id();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_photo_id))
                .equalTo(photo_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "No photo found for photo_id: " + photo_id);
                    return;
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Photo newPhoto = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    newPhoto.setCaption(getSafeString(objectMap, R.string.field_caption));
                    newPhoto.setTags(getSafeString(objectMap, R.string.field_tags));
                    newPhoto.setPhoto_id(getSafeString(objectMap, R.string.field_photo_id));
                    newPhoto.setUser_id(getSafeString(objectMap, R.string.field_user_id));
                    newPhoto.setDate_created(getSafeString(objectMap, R.string.field_date_created));
                    newPhoto.setImage_path(getSafeString(objectMap, R.string.field_image_path));

                    List<Comment> commentList = new ArrayList<>();
                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
                        Comment comment = dSnapshot.getValue(Comment.class);
                        if (comment != null) {
                            commentList.add(comment);
                        }
                    }
                    newPhoto.setComments(commentList);
                    mphoto = newPhoto;
                    getCurrentUser(); // Calls getLikesStrings() and getPhotoDetails()
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    } catch (NullPointerException e) {
        Log.e(TAG, "init: NullPointerException: " + e.getMessage());
    }
}

    private String getSafeString(Map<String, Object> map, int keyResId) {
        Object value = map.get(getString(keyResId));
        return value != null ? value.toString() : "";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()){
            init();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();

        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }



    }

    private void getLikesStrings() {
        Log.d(TAG, "getLikesStrings: gettings likes string");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: found like" + singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");

                            }
                            String[] splitUsers = mUsers.toString().split(",");
                            if (mUsers.toString().contains(mCurrentUser.getUsername() + ",")) {
                                mLikedByCurrentUser = true;

                            } else {
                                mLikedByCurrentUser = false;
                            }
                            int length = splitUsers.length;
                            if (length == 1) {
                                mLikesStrings = "Liked by " + splitUsers[0];

                            } else if (length == 2) {
                                mLikesStrings = "Liked by " + splitUsers[0] + " and " + splitUsers[1];


                            } else if (length == 3) {

                                mLikesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " and " + splitUsers[2];


                            } else if (length == 4) {

                                mLikesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " , " + splitUsers[2] + " and " + splitUsers[3];


                            } else if (length > 4) {
                                mLikesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " , " + splitUsers[2] + " and " + (splitUsers.length - 3) + "others";

                            }

//                            setupWidgets();
                            getUserAccountSettingsAndSetupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                if (!dataSnapshot.exists()) {
                    mLikesStrings = "";
                    mLikedByCurrentUser = false;
                    getUserAccountSettingsAndSetupWidgets();
//                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mCurrentUser = singleSnapshot.getValue(User.class);
                }
                getLikesStrings();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });


    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: tapped");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mphoto.getPhoto_id())
                    .child(getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        String KeyID = singleSnapshot.getKey();

                        //case1 user already like the photo
                        if (mLikedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            mRef.child(getString(R.string.dbname_photos))
                                    .child(mphoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(KeyID)
                                    .removeValue();

                            mRef.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mphoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(KeyID)
                                    .removeValue();

                            mHeart.toggleLike();
                            getLikesStrings();

                        }

                        //case 2 the user has not like the photo
                        else if (!mLikedByCurrentUser) {
                            addNewLike();
                            break;

                        }

                    }
                    if (!dataSnapshot.exists()) {
                        addNewLike();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return true;

        }

//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            Log.d(TAG, "onDoubleTap: double tap detected ");
//
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//            Query query = reference
//                    .child(getString(R.string.dbname_photos))
//                    .orderByChild(mphoto.getPhoto_id())
//                    .equalTo(getString(R.string.field_likes));
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//
//                        String KeyID = singleSnapshot.getKey();
//
//                        //case1 user already like the photo
//                        if (mLikedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                            mRef.child(getString(R.string.dbname_photos))
//                                    .child(mphoto.getPhoto_id())
//                                    .child(getString(R.string.field_likes))
//                                    .child(KeyID)
//                                    .removeValue();
//
//                            mRef.child(getString(R.string.dbname_user_photos))
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .child(mphoto.getPhoto_id())
//                                    .child(getString(R.string.field_likes))
//                                    .child(KeyID)
//                                    .removeValue();
//
//                            mHeart.toggleLike();
//                            getLikesStrings();
//
//                        }
//
//                        //case 2 the user has not like the photo
//                        else if (!mLikedByCurrentUser) {
//                            addNewLike();
//                            break;
//
//                        }
//
//                    }
//                    if (!dataSnapshot.exists()) {
//                        addNewLike();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//            return true;
//        }

    }

    private void addNewLike() {
        Log.d(TAG, "addNewLike: adding new like");


        String newLikeID = mRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRef.child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mRef.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        getLikesStrings();
        Log.d(TAG, "addNewLike: like added");


        String targetUserId = mphoto.getUser_id();
        String photoId = mphoto.getPhoto_id();

        addLikeNotification(targetUserId, photoId);

    }


    private void addLikeNotification(String targetUserId, String photoId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(targetUserId)) {
            return;
        }

        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_notifications))
                .child(targetUserId);

        String notificationId = notificationsRef.push().getKey();

        Notification notification = new Notification(
                "like",
                currentUserId,
                System.currentTimeMillis(),
                false
        );

        notificationsRef.child(notificationId).setValue(notification);
    }


    private void getPhotoDetails() {
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mphoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }
    private void getUserAccountSettingsAndSetupWidgets() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_user_account_settings))
                .child(mphoto.getUser_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUserAccountSettings = snapshot.getValue(UserAccountSettings.class);
                        if (mUserAccountSettings != null) {
                            setupWidgets();
                        } else {
                            Log.e(TAG, "UserAccountSettings is null, can't setup widgets.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                    }
                });
    }



    private void setupWidgets() {


        String timestampDiff = getTimestampDifference();
        mTimestamp.setText(timestampDiff.toUpperCase());

        ImageLoader.loadImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null);

        mUsername.setText(mUserAccountSettings.getUsername());
        usernameUnderPost.setText(mUserAccountSettings.getUsername());
        mLikes.setText(mLikesStrings);
        mCaption.setText(mphoto.getCaption());

        if (mphoto.getComments().size() >0){
            mComments.setText("View all comments " + mphoto.getComments().size() + " comments");
        }else {
            mComments.setText("");
        }

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to command thread ");
                mOnCommentThreadSelectedListener.onCommentThreadSelected(mphoto);
            }
        });
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: back arrow clicked");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: back arrow clicked");
                mOnCommentThreadSelectedListener.onCommentThreadSelected(mphoto);
            }
        });

        if (mLikedByCurrentUser) {
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        } else {
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);

            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch:  white heart touch");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }





    }


    /**
     * Returns a string representing the number of days ago the post was made
     *
     * @return
     */

    private String getTimestampDifference() {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String result = "Just now";
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        final String photoTimestamp = mphoto.getDate_created();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        try {
            Date photoTime = sdf.parse(photoTimestamp);
            if (photoTime != null) {
                long diffMillis = now.getTime() - photoTime.getTime();

                long diffSeconds = diffMillis / 1000;
                long diffMinutes = diffSeconds / 60;
                long diffHours = diffMinutes / 60;
                long diffDays = diffHours / 24;

                if (diffSeconds < 60) {
                    result = "Just now";
                } else if (diffMinutes < 60) {
                    result = diffMinutes + " minutes ago";
                } else if (diffHours < 24) {
                    result = diffHours + " hours ago";
                } else {
                    result = diffDays + " days ago";
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
        }

        return result;
    }


    /**
     * retrieve the activity  number from the incoming bundle from profileActivity interface
     *
     * @return
     */
    private int getActivityNumFromBundle() {
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));

        } else {
            return 0;
        }

    }


    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));

        } else {
            return null;
        }

    }


    /**
     * setup bottom navigation view
     */

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
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
