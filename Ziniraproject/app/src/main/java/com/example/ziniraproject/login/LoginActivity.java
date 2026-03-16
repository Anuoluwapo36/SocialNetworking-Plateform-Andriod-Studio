package com.example.ziniraproject.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ziniraproject.R;
import com.example.ziniraproject.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";


    //firebase
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = findViewById(R.id.loginRequestLoadingProgressbar);
        mEmail = findViewById(R.id.inputUsername);
        mPassword = findViewById(R.id.inputPassword);
        mPleaseWait = findViewById(R.id.pleasewaitId);
        mContext= LoginActivity.this;
        Log.d(TAG, "onCreate: started.");
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        setupFirebaseAuth();
        init();
        registerLink();

    }

    /**
     *
     * @param string
     * @return
     */
    private boolean isStringNull(String string) {
        return string == null || string.trim().isEmpty();
    }

    /*
     * --------------------------------------firebase------------------------------------------------------------
     *
     *
     */

    /**
    login action here
     */
    private void init() {
        Button login = findViewById(R.id.loginId);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) || isStringNull(password)) {
                    mEmail.setError("Field cannot be empty");
                    mPassword.setError("Field cannot be empty");
                    Toast.makeText(LoginActivity.this, "You need to fill all the fields", Toast.LENGTH_SHORT).show();
                    return; // Added return to stop execution
                }

                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                mProgressBar.setVisibility(View.GONE);
                                mPleaseWait.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(mContext, "Failed to Authenticate ", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "signInWithEmail:failed");
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);

                                } else {
                                    try {
                                        if (user.isEmailVerified()){
                                            Log.e(TAG, "onComplete: success. email is verified." );
                                            navigateToHome();


                                        }else {
                                            Toast.makeText(mContext, "Email is not verified \n check your email inbox and verified", Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                            mPleaseWait.setVisibility(View.GONE);
                                            mAuth.signOut();
                                        }
                                    }catch (NullPointerException e){
                                        Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                    }
                                }
                            }
                        });
            }
        });

        // here if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            navigateToHome();
        }
    }

    //navigate user to home activity after complete authentication
    private void navigateToHome() {
        Log.d(TAG, "navigateToHome: navigating to home activity");
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * signup link to navigate to register activity
     */
    private  void registerLink(){
        TextView redirectToRegister = (TextView) findViewById(R.id.registerLink);
        redirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class );
                startActivity(intent);
                finish();
            }
        });
    }




    /**
     * responsible for setting up firebase auth
     */
    private  void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user !=null){
                    Log.d(TAG, "onAuthStateChanged: user signed in" + user.getUid());

                }else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");

                }
            }
        };
    }

    @Override
    public  void  onStart(){

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public  void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

}
