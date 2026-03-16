package com.example.ziniraproject.until;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ziniraproject.create.CreateActivity;
import com.example.ziniraproject.message.MessageActivity;
import com.example.ziniraproject.notification.NotificationActivity;
import com.example.ziniraproject.profile.ProfileActivity;
import com.example.ziniraproject.R;
import com.example.ziniraproject.search.SearchActivity;
import com.example.ziniraproject.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHelper";

    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView.setItemHorizontalTranslationEnabled(false);
        bottomNavigationView.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setItemIconTintList(null);

    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;


                    case R.id.nav_message:
                        Intent intent2 = new Intent(context, MessageActivity.class);
                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;


                    case R.id.nav_search:
                        Intent intent3  = new Intent(context, SearchActivity.class);
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.nav_create:
                        Intent intent4 = new Intent(context, CreateActivity.class);
                        context.startActivity(intent4);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.nav_notification:
                        Intent intent5 = new Intent(context, NotificationActivity.class);
                        context.startActivity(intent5);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.nav_profile:
                        Intent intent6 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent6);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                }


                return false;
            }
        });
    }


}


