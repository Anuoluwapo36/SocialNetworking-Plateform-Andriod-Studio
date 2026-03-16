package com.example.ziniraproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ziniraproject.create.CreateActivity;
import com.example.ziniraproject.create.GalleryFragment;
import com.example.ziniraproject.dialog.ConfirmPasswordDialog;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.models.UserSettings;
import com.example.ziniraproject.share.ShareActivity;
import com.example.ziniraproject.until.FireBaseMethod;
import com.example.ziniraproject.until.ImageLoader;
//import com.example.ziniraproject.until.UniversalImageLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {


    private String mAppend;

    @Override
    public void onConfirmPassword(String password) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No authenticated user found.");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String newEmail = mEmail.getText().toString().trim();
        if (newEmail.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

        // Re-authenticate user
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User re-authenticated.");

                        // Check if the new email is already in use
                        mAuth.fetchSignInMethodsForEmail(newEmail)
                                .addOnCompleteListener(methodsTask -> {
                                    if (methodsTask.isSuccessful()) {
                                        SignInMethodQueryResult result = methodsTask.getResult();

                                        // Check if email already exists
                                        if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                            // Email already in use — stop here
                                            Log.d(TAG, "Email is already in use.");
                                            Toast.makeText(getActivity(), "Email is already in use", Toast.LENGTH_SHORT).show();
                                            return;  // IMPORTANT: return here to stop execution inside this callback
                                        }

                                        // Email is NOT in use, proceed to update
                                        currentUser.updateEmail(newEmail)
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d(TAG, "User email address updated.");
                                                        Toast.makeText(getActivity(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                        mfireBaseMethod.updateEmail(newEmail);
                                                    } else {
                                                        Log.e(TAG, "Error updating email: " + updateTask.getException());
                                                        Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        Log.e(TAG, "Failed to check email availability: " + methodsTask.getException());
                                        Toast.makeText(getActivity(), "Error checking email availability", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Log.e(TAG, "Re-authentication failed: " + task.getException());
                        Toast.makeText(getActivity(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FireBaseMethod mfireBaseMethod;


    //firebase
    private FirebaseAuth mAuth;


    private ProgressBar mProgressBar;

    private ImageButton mBackArrow, savedProfileButton;
    //edit profile fragment widgets
    private EditText mUsername, mAboutMe, mEmail, mPhone, mDisplayName;
    private TextView mChangeProfile;
    private ShapeableImageView mProfilePhoto;


    //global variable
    private String userID;
    //variable
    private UserSettings muserSettings;


    private static final String TAG = "EditProfileFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = view.findViewById(R.id.profileImageId);
        mChangeProfile = view.findViewById(R.id.changeProfilePhoto);
        mDisplayName = view.findViewById(R.id.DisplayName);
        mAboutMe = view.findViewById(R.id.myBio);
        mUsername = view.findViewById(R.id.username);

        mEmail = view.findViewById(R.id.emailId);
        mPhone = view.findViewById(R.id.Number);
        mfireBaseMethod = new FireBaseMethod(getActivity());

        setupFirebaseAuth();

//        setProfileImage();
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to profile activity.");
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);


            }
        });

        savedProfileButton = view.findViewById(R.id.saveChange);
        savedProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: attempting to save changes.");

                saveProfileSettings();

            }
        });

        return view;
    }




    /**
     * Retrieves the data contained in the widget and submit it to database
     * before doing so it checks to make sure username chosen is unique
     */
    private void saveProfileSettings() {

        final String display_name = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String about = mAboutMe.getText().toString();
        final String email = mEmail.getText().toString();
        final long phone = Long.parseLong(mPhone.getText().toString());


        // case1: if the user made a change to their username

        if (!muserSettings.getUser().getUsername().equals(username)) {

            checkIfUsernameExists(username);
        }
        //case2: if the user made a change to their email
        if (!muserSettings.getUser().getEmail().equals(email)) {
            // step 1) Reauthenticate
            // --- confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);


            //step2) check if the email already is register
            //  -- fetchproviderforemail(string email)
            //step3) change the email
            //   ---submit the new email to the database and authenticate

        }

        /**
         * change the rest of he settings that do not required uniqueness
         */
        if (!muserSettings.getSettings().getDisplay_name().equals(display_name)) {
            //update display name
            mfireBaseMethod.updateUserAccountSettings(display_name, null, 0);
        }
        if (!muserSettings.getSettings().getAbout().equals(about)) {

            //update about
            mfireBaseMethod.updateUserAccountSettings(null, about, 0);

        }

        if (!muserSettings.getSettings().getProfile_photo().equals(phone)) {
            //update phone number
            mfireBaseMethod.updateUserAccountSettings(null, null, phone);
        }


    }


    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getSettings().getUsername());

        muserSettings = userSettings;
        UserAccountSettings settings = userSettings.getSettings();
        ImageLoader.loadImage(settings.getProfile_photo(), mProfilePhoto, null);
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mAboutMe.setText(settings.getAbout());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhone.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateActivity.class);
            intent.putExtra("task", 1); // Profile photo task
            startActivity(intent);


        });

        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("image_type", "profile");
        fragment.setArguments(args);



    }

    /**
     * checking if username already exist in the database
     * and modifying username
     *
     * @param username
     */

    private void checkIfUsernameExists(final String username) {

        Log.d(TAG, "checkIfUsernameExists: checking if " + username + "already exists");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            //modify username and if username already exits
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    //add the username
                    mfireBaseMethod.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username. ", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkingIfUsernameExists : FOUND A MATCH:" + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "that username already exists ", Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void setProfileWidgets(UserSettings userSettings) {
//        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.toString());
//        Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving fro firebase database: " + userSettings.getUser().toString());
//
//        muserSettings = userSettings;
//
////        User user = userSettings.getUser();
//        UserAccountSettings settings = userSettings.getSettings();
//        ImageLoader.loadImage(settings.getProfile_photo(), mProfilePhoto, null);
//        mDisplayName.setText(settings.getDisplay_name());
//        mUsername.setText(settings.getUsername());
//        mAboutMe.setText(settings.getAbout());
//        mEmail.setText(userSettings.getUser().getEmail());
//        mPhone.setText(String.valueOf(userSettings.getUser().getPhone_number()));
//
//
//    }



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
        userID = mAuth.getCurrentUser().getUid();
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
