package com.example.ziniraproject.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ziniraproject.R;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.until.FireBaseMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

/**
 *
 */
public class RegisterNextActivity extends AppCompatActivity {

    private static final String TAG = "RegisterNextActivity";

    private FireBaseMethod fireBaseMethod;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private Context context;

    private EditText memail, mpassword, musername;
    private TextView mPleaseWait;
    private ProgressBar mprogressBar;
    private CheckBox isCheck;
    private Button mContinue;

    private String email, username, password;
    private String firstname, lastname;

    private String append = "";


    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next1);

        context = RegisterNextActivity.this;
        fireBaseMethod = new FireBaseMethod(context);

        initWidgets();
        setupFirebaseAuth();
        setupContinueButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
    }

    private void initWidgets() {
        memail = findViewById(R.id.inputEmail);
        musername = findViewById(R.id.inputUsername);
        mpassword = findViewById(R.id.inputPassword);
        mPleaseWait = findViewById(R.id.pleaseWaitId);
        mprogressBar = findViewById(R.id.registerRequestLoadingProgressbar);
        isCheck = findViewById(R.id.isCheckId);
        mContinue = findViewById(R.id.buttonCreate);

        mprogressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
    }

    private void setupContinueButton() {
        mContinue.setOnClickListener(v -> {
            email = memail.getText().toString().trim();
            password = mpassword.getText().toString().trim();
            username = musername.getText().toString().trim();
            boolean isChecked = isCheck.isChecked();

            if (checkInput(email, password, username, isChecked)) {
                mprogressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                checkEmailExists(email, password, username);
            }
        });
    }

    private boolean checkInput(String email, String password, String username, boolean isChecked) {
        boolean isValid = true;

        if (email.isEmpty()) {
            memail.setError("Enter Email to continue");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            memail.setError("Please enter a valid email");
            isValid = false;
        } else if (email.length() > 254) {
            memail.setError("Email too long (max 254 characters)");
            isValid = false;
        }

        if (password.isEmpty()) {
            mpassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            if (!password.matches(".*[A-Z].*")) {
                mpassword.setError("Password must contain at least 1 uppercase letter");
                isValid = false;
            } else if (!password.matches(".*[a-z].*")) {
                mpassword.setError("Password must contain at least 1 lowercase letter");
                isValid = false;
            } else if (!password.matches(".*\\d.*")) {
                mpassword.setError("Password must contain at least 1 number");
                isValid = false;
            } else if (!password.matches(".*[!@#$%^&*()_+].*")) {
                mpassword.setError("Password must contain at least 1 special character");
                isValid = false;
            }
        }

        if (!isChecked) {
            Toast.makeText(this, "You must accept terms and conditions", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (username.isEmpty()) {
            musername.setError("Username cannot be empty");
            isValid = false;
        } else if (username.length() < 3 || username.length() > 15) {
            musername.setError("Username must be 3-15 characters long");
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9]+$")) {
            musername.setError("Username can only contain letters and numbers");
            isValid = false;
        }

        return isValid;
    }

    private void checkEmailExists(String email, String password, String username) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            handleEmailExists();
                        } else {
                            registerUser(email, password, username);
                        }
                    } else {
                        handleEmailCheckError();
                    }
                });
    }

    private void handleEmailExists() {
        mprogressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
        memail.setError("Email already registered");
        memail.requestFocus();
    }

    private void handleEmailCheckError() {
        mprogressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
        Toast.makeText(this, "Error checking email availability", Toast.LENGTH_SHORT).show();
    }

    private void registerUser(String email, String password, String username) {
        fireBaseMethod.registerNewUser(email, password, username, firstname, lastname, new FireBaseMethod.RegistrationCallback() {
            @Override
            public void onSuccess(String userId) {
                mprogressBar.setVisibility(View.GONE);
                mPleaseWait.setVisibility(View.GONE);


                fireBaseMethod.addNewUser(email, username, "", "","");
                Intent intent = new Intent(RegisterNextActivity.this, RegisterDetailsActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);

            }

            @Override
            public void onFailure(String errorMessage) {
                mprogressBar.setVisibility(View.GONE);
                mPleaseWait.setVisibility(View.GONE);
                if (errorMessage.contains("email address is already in use")) {
                    memail.setError("Email already registered");
                    memail.requestFocus();
                } else {
                    Toast.makeText(RegisterNextActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    ----------------------------------------set firebase auth---------------------------------------------------------
     */


    /**
     * checking if username already exist in the database
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
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String finalUsername = "";

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkingIfUsernameExists : FOUND A MATCH:" + singleSnapshot.getValue(User.class).getUsername());
                        append = myRef.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: username already exists, APPENDING RANDOM STRING TO NAME: " + append);
                        finalUsername = username + append;
                    }
                }

                // Use the finalUsername (either original or modified)
                fireBaseMethod.addNewUser(email, finalUsername,  "","","");
                Toast.makeText(context, "Signup successful verification email", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
    }




    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mfirebaseDatabase.getReference();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "setupFirebaseAuth: signed in: " + user.getUid());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        checkIfUsernameExists(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Firebase error: " + databaseError.getMessage());
                    }

                });
                finish();
            } else {
                //user is sign out
                Log.d(TAG, "onAuthStateChange: signed_out ");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
