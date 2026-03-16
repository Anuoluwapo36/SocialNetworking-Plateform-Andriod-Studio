package com.example.ziniraproject.until;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ziniraproject.R;
import com.example.ziniraproject.home.HomeActivity;
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.Like;
import com.example.ziniraproject.models.Notification;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.profile.ProfileActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainFeedAdapter extends ArrayAdapter<Photo> {
    public  interface  OnLoadMoreItemsListener {
        void onLoadMoreItems();
    }
        OnLoadMoreItemsListener mOnLoadMoreItemsListener;


    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername;

    public MainFeedAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }




    static class ViewHolder {
        ShapeableImageView mProfileImage;

        SquareImageView mPostImage;
        TextView mUsername, underProfileUsername, timeDelta, caption, likes, comments;
        ImageView heartRed, heartWhite, comment;

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        StringBuilder users;
        String likesStrings;
        boolean mLikeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProfileImage = convertView.findViewById(R.id.profileImageId);
            holder.mPostImage = convertView.findViewById(R.id.post_image);
            holder.mUsername = convertView.findViewById(R.id.usernameText);
            holder.underProfileUsername = convertView.findViewById(R.id.image_username);
            holder.heartRed = convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite = convertView.findViewById(R.id.image_heart_white);
            holder.comment = convertView.findViewById(R.id.speech_bubble);
            holder.likes = convertView.findViewById(R.id.image_likes);
            holder.comments = convertView.findViewById(R.id.image_comment_link);
            holder.timeDelta = convertView.findViewById(R.id.image_time_posted);
            holder.caption = convertView.findViewById(R.id.image_caption);
            holder.heart = new Heart(holder.heartWhite, holder.heartRed);
            holder.photo = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.users = new StringBuilder();
            convertView.setTag(holder);

            // Attach GestureDetector to views
            holder.mPostImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: post image touch at position " + position);
                    return holder.detector.onTouchEvent(event);
                }
            });
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch at position " + position);
                    return holder.detector.onTouchEvent(event);
                }
            });
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch at position " + position);
                    return holder.detector.onTouchEvent(event);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.photo = getItem(position); // Update photo for recycled views
            holder.detector = new GestureDetector(mContext, new GestureListener(holder)); // Reinitialize detector
            holder.mLikeByCurrentUser = false; // Reset like state
        }

        // ... Rest of getView remains unchanged (your original code)
        getCurrentUsername();
        getLikesStrings(holder);
        String caption = getItem(position).getCaption();
        Log.d(TAG, "Setting caption for photo_id " + getItem(position).getPhoto_id() + ": " + caption);
        holder.caption.setText(caption != null ? caption : "");
        List<Comment> comments = getItem(position).getComments();
        holder.comments.setText("See all  " + comments.size() + " comments ");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for " + getItem(position).getPhoto_id());
                ((HomeActivity) mContext).onCommentThreadSelected(getItem(position), mContext.getString(R.string.home_activity));
                ((HomeActivity) mContext).hideLayout();
            }
        });
        String timestampDifference = getTimestampDifference(getItem(position));
        holder.timeDelta.setText(timestampDifference);
        String imageUrl = getItem(position).getImage_path();
        ImageLoader.loadImage(imageUrl, holder.mPostImage, null);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user");
                    holder.mUsername.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.mUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of " + singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.underProfileUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of " + singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.underProfileUsername.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    ImageLoader.loadImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(), holder.mProfileImage, null);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of " + singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.settings = singleSnapshot.getValue(UserAccountSettings.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to comment activity");
                            ((HomeActivity) mContext).onCommentThreadSelected(getItem(position), mContext.getString(R.string.home_activity));
                            ((HomeActivity) mContext).hideLayout();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Query userQuery = mReference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());
                    holder.user = singleSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (reachedEndOfList(position)){
            loadMoreData();
        }
        return convertView;
    }


    private  boolean  reachedEndOfList(int position){
        return position == getCount() -1;

    }
    private  void  loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " + e.getMessage());
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: NullPointerException: " + e.getMessage());
        }
    }



    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        ViewHolder mHolder;

        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: tapped");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        String KeyID = singleSnapshot.getKey();

                        //case1 user already like the photo
                        if (mHolder.mLikeByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            mReference.child(mContext.getString(R.string.dbname_photos))
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(KeyID)
                                    .removeValue();

                            mReference.child(mContext.getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(KeyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikesStrings(mHolder);

                        }

                        //case 2 the user has not like the photo
                        else if (!mHolder.mLikeByCurrentUser) {
                            addNewLike(mHolder);
                            break;

                        }

                    }
                    if (!dataSnapshot.exists()) {
                        addNewLike(mHolder);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return true;

        }


    }

    private void addNewLike(final ViewHolder holder) {
        Log.d(TAG, "addNewLike: adding new like");


        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mReference.child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikesStrings(holder);

        Log.d(TAG, "addNewLike: like added");

        String targetUserId = holder.photo.getUser_id();
        String photoId = holder.photo.getPhoto_id();





    }




    private void getCurrentUsername() {
        Log.d(TAG, "getCurrentUsername: getting current username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void getLikesStrings(final ViewHolder holder) {
        Log.d(TAG, "getLikesStrings: getting likes string");

        try {


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(holder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.users = new StringBuilder();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(mContext.getString(R.string.dbname_users))
                                .orderByChild(mContext.getString(R.string.field_user_id))
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: found like" + singleSnapshot.getValue(User.class).getUsername());
                                    holder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                    holder.users.append(",");

                                }
                                String[] splitUsers = holder.users.toString().split(",");
                                if (holder.users.toString().contains(currentUsername + ",")) {
                                    holder.mLikeByCurrentUser = true;

                                } else {
                                    holder.mLikeByCurrentUser = false;
                                }
                                int length = splitUsers.length;
                                if (length == 1) {
                                    holder.likesStrings = "Liked by " + splitUsers[0];

                                } else if (length == 2) {
                                    holder.likesStrings = "Liked by " + splitUsers[0] + " and " + splitUsers[1];


                                } else if (length == 3) {

                                    holder.likesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " and " + splitUsers[2];


                                } else if (length == 4) {

                                    holder.likesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " , " + splitUsers[2] + " and " + splitUsers[3];


                                } else if (length > 4) {
                                    holder.likesStrings = "Liked by " + splitUsers[0] + " , " + splitUsers[1] + " , " + splitUsers[2] + " and " + (splitUsers.length - 3) + "others";

                                }
                                Log.d(TAG, "onDataChange: likes strings: " + holder.likesStrings);
                                //setup likes string
                                setupLikesString(holder, holder.likesStrings);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    if (!dataSnapshot.exists()) {
                        holder.likesStrings = "";
                        holder.mLikeByCurrentUser = false;
                        //setup likes string
                        setupLikesString(holder, holder.likesStrings);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "getLikesStrings: NullPointerException: " + e.getMessage());
            holder.likesStrings = "";
            holder.mLikeByCurrentUser = false;
            //setup like string
            setupLikesString(holder, holder.likesStrings);
        }

    }

    private void setupLikesString(final ViewHolder holder, String likesString) {
        Log.d(TAG, "setupLikesString: likes strings : " + holder.likesStrings);
        if (holder.mLikeByCurrentUser) {
            Log.d(TAG, "setupLikesString: photo was like by current user");
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        } else {
            Log.d(TAG, "setupLikesString: user did not liked by current user");
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartRed.setVisibility(View.GONE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });

        }
        holder.likes.setText(likesString);

    }

    /**
     * Returns a string representing the number of days ago the post was made
     *
     * @return
     */

    private String getTimestampDifference(Photo photo) {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String result = "Just now"; // default
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        final String photoTimestamp = photo.getDate_created();

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


}
