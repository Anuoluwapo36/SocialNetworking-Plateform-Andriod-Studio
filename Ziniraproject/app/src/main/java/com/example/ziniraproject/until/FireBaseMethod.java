package com.example.ziniraproject.until;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ziniraproject.R;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.models.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;


public class FireBaseMethod {
    private static final String TAG = "FireBaseMethod";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;


    private Context mContext;
    private String userID;
    private FirebaseFirestore mFirestore;
    private double mPhotouploadprogress = 0;


    public FireBaseMethod(Context context) {
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mfirebaseDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
    }





    public void saveToRealtimeDatabase(Context context, String imageUrl, String caption, String tags, Runnable onSuccessCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        String newPhotoKey = myRef.child(context.getString(R.string.dbname_user_photos))
                .child(userId).push().getKey();
        if (newPhotoKey == null) return;

        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(imageUrl);
        photo.setTags(tags);
        photo.setUser_id(userId);
        photo.setPhoto_id(newPhotoKey);

        // Save to both user_photos and general photos
        myRef.child(context.getString(R.string.dbname_user_photos))
                .child(userId)
                .child(newPhotoKey)
                .setValue(photo);

        myRef.child(context.getString(R.string.dbname_photos))
                .child(newPhotoKey)
                .setValue(photo)
                .addOnSuccessListener(unused -> {
                    if (onSuccessCallback != null) onSuccessCallback.run();
                })
                .addOnFailureListener(e -> Log.e("RTDB", "Failed to save photo", e));
    }





    public void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);

    }


    private String getTimeStamp() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-'T'HH:mm:ss'Z'", Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        return sdf.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateCreated = sdf.format(new Date());

        return dateCreated;
    }




    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()
        ) {
            count++;
        }
        return count;
    }

    /**
     * updating user_account_settings node fpr the current user
     *
     * @param display_name
     * @param about_me
     * @param phone_number
     */
    public void updateUserAccountSettings(String display_name, String about_me, long phone_number) {
        Log.d(TAG, "updateUsername: updating user account settings : ");


        if (display_name != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(display_name);


        }

        if (about_me != null) {

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_about_me))
                    .setValue(about_me);


        }
        if (phone_number != 0) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phone_number);

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phone_number);


        }


    }


    /**
     * updating username
     *
     * @param username
     */
    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to: " + username);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * updating the email in the user node
     *
     * @param email
     */
    public void updateEmail(String email) {
        Log.d(TAG, "updateUsername: updating email to: " + email);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }


    /**
     * register new user with email and password to firebase Authentication
     *
     * @param email
     * @param password
     * @param firstname
     * @param lastname
     * @param username
     * @param callback
     */
    public void registerNewUser(final String email, String password, final String username, final String firstname, final String lastname, final RegistrationCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            //send verification email
                            sendVerificationEmail();
                            FirebaseUser user = mAuth.getCurrentUser();

                            userID = user.getUid();
                            savedUserToFirestore(email, username, firstname, lastname, callback);
                            addNewUser(email, username, "", "","");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());

                        }
                    }
                });
    }

    private void savedUserToFirestore(String email, String username, String firstname, String lastname, final RegistrationCallback callback) {
        Log.d(TAG, "savedUserToFirestore: saving user to firebase database");

        Map<String, Object> userdata = new HashMap<>();
        userdata.put("email", email);
        userdata.put("username", username);
        userdata.put("firstname", firstname);
        userdata.put("lastname", lastname);
        userdata.put("createdAt", FieldValue.serverTimestamp());
        userdata.put("userId", userID);


        mFirestore.collection("users").document(userID)
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(userID);

                        } else {
                            deleteAuthUser();
                            callback.onFailure("Failed to complete try again later:" + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    private void deleteAuthUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "deleteAuthUser: failed to delete user", task.getException());
                }
            });
        }
    }


    public interface RegistrationCallback {
        void onSuccess(String userId);

        void onFailure(String errorMessage);
    }


    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "verification email sent!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * add information to the users nodes
     * add information to the user_account_settings node
     *
     * @param email
     * @param username
     * @param about_me
     * @param profile_photo
     */

    public void addNewUser(String email,
                           String username,
                           String about_me,
                           String profile_photo,
                           String cover_photo
                           ) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();   // fresh fetch
        if (uid == null) {
            Log.e("addNewUser", "UID is null – user not authenticated");
            return;    // or show a message / trigger sign‑in flow
        }

        User user = new User(userID, 1, email,
                StringManipulation.condenseUsername(username));

        myRef.child("users")
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                about_me,
                username,
                0, 0, 0,
                profile_photo,
                cover_photo,
                StringManipulation.condenseUsername(username),
                userID

        );

        myRef.child("user_account_settings")
                .child(userID)
                .setValue(settings);
    }


    /**
     * Retrieve the account settings from the Firebase Database for current login user
     * Database: user_account_settings node
     *
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {

        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d(TAG, "getUserAccountSettings: dataSnapshot: " + ds);

            //making data visible in the application
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: data snapshot: " + ds);


                try {


                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()

                    );

                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()

                    );
                    settings.setAbout(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getAbout()
                    );


                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()

                    );
                    settings.setCover_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getCover_photo()

                    );


                    settings.setPost(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPost()

                    );

                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()

                    );

                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()

                    );


                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());

                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: user account settings is null: " + e.getMessage());
                }
            }

            //user node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: data snapshot: " + ds);

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()

                );

                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()

                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()

                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()

                );

                Log.d(TAG, "getUserAccountSettings: retrieved user information: " + user.toString());

            }


        }
        return new UserSettings(user, settings);


    }

    public void setCoverPhoto(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(user.getUid())
                    .child(mContext.getString(R.string.cover_photo))
                    .setValue(imageUrl);
        }
    }



}
