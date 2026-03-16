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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    //firebase
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;

    private Context context;
    private EditText mfirstname,mlastname;
    private TextView mPleaseWait;
    private ProgressBar mprogressBar;

    private  FirebaseFirestore db;
    private Button mContinue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        alreadyHaveAccount();
        iniWidgets();
        init();
        setupFirebaseAuth();

        db = FirebaseFirestore.getInstance();
    }

    private  void init(){
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = mfirstname.getText().toString();
                String lastname = mlastname.getText().toString();

                if (checkInput(firstname, lastname)){
                    mprogressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(RegisterActivity.this, RegisterNextActivity.class);
                    intent.putExtra("firstname", firstname);
                    intent.putExtra("lastname", lastname);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean checkInput(String firstname, String lastname){
        Log.d(TAG, "checkInput: checking inputs for null values");

        if (firstname.isEmpty() || lastname.isEmpty()){
            mfirstname.setError("Field cannot be empty");
            mlastname.setError("Field cannot be empty");
            Toast.makeText(RegisterActivity.this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;


    }
    /**
     * responsible for initializing activity widgets
     */
    private  void iniWidgets(){
        Log.d(TAG, "iniWidgets: initializing widgets");
        mfirstname = findViewById(R.id.inputFirstname);
        mlastname = findViewById(R.id.inputLastname);
        mPleaseWait = findViewById(R.id.pleaseWaitId);
        mprogressBar = findViewById(R.id.registerRequestLoadingProgressbar);
        mContinue = findViewById(R.id.continueButton);
        mprogressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

    }

    private  void alreadyHaveAccount(){
        TextView haveAccount = findViewById(R.id.haveAccount);
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to login activity");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


            }
        });

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
