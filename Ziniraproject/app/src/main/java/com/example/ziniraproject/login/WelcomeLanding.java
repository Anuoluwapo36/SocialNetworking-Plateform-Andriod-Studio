package com.example.ziniraproject.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ziniraproject.R;
import com.example.ziniraproject.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeLanding extends AppCompatActivity {
    private Button start;

    private ProgressBar progressDialog;
    private String userId;
    private TextView mwaiting;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);
        progressDialog=findViewById(R.id.startLoadingProgressbar);
        mwaiting = findViewById(R.id.pleaseWaitStart);
        progressDialog.setVisibility(View.GONE);
        mwaiting.setVisibility(View.GONE);

        Start();
        Auth();

    }

    private void Start(){
        start=findViewById(R.id.buttonDone);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeLanding.this, ProfileActivity.class);
                progressDialog.setVisibility(View.VISIBLE);
                mwaiting.setVisibility(View.VISIBLE);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * check if it current user and if not return back to login page
     */
    private void  Auth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "You are not signed in. Please login first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }





}
