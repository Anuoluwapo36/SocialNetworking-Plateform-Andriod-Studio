package com.example.ziniraproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.example.ziniraproject.home.HomeActivity;
import com.example.ziniraproject.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutFragment extends Fragment {
    //firebase
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressBar;
    private TextView signOut;
    private Button  signOutButton;


    private  static  final  String TAG = "SignOutFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.fragment_signout,container,false);
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = view.findViewById(R.id.signOutProgressBar);
        signOut = view.findViewById(R.id.textSignOut);
        signOutButton = view.findViewById(R.id.confirmButtonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out");
                mAuth.signOut();
                getActivity().finish();
                mProgressBar.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.VISIBLE);
            }
        });
        mProgressBar.setVisibility(View.GONE);
        signOut.setVisibility(View.GONE);
        setupFirebaseAuth();
        return view;
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

                    Log.d(TAG, "onAuthStateChanged: navigation back to login activity");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

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
