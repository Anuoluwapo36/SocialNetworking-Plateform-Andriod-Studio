package com.example.ziniraproject.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ziniraproject.R;

import com.example.ziniraproject.profile.EditProfileFragment;
import com.example.ziniraproject.profile.SignOutFragment;
import com.example.ziniraproject.until.BottomNavigationViewHelper;
import com.example.ziniraproject.until.FireBaseMethod;
import  com.example.ziniraproject.until.sectionStatePagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static  final  int ACTIVITY_NUM = 0;
    private Context context;
    private sectionStatePagerAdapter sectionStatePagerAdapter;
    private ViewPager2 viewPager2;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        context = AccountSettingsActivity.this;
        Log.d(TAG, "onCreated: started.");
        viewPager2 = findViewById(R.id.viewpager_container);
        relativeLayout = findViewById(R.id.relLayout1);
        setUpsettingsList();
        setupFragments2();
        setupBottomNavigationView();
        backArrow();
        getIncomingIntent();

    }

    private  void  getIncomingIntent(){
        Intent intent = getIntent();
        //if there is an imageUrl attached as an extra, than it was chosen from the gallery/photo fragment
        if (intent.hasExtra(getString(R.string.selected_image))) {
            String imageUrl = intent.getStringExtra(getString(R.string.selected_image));
            String imageType = intent.getStringExtra("image_type");

            if ("profile".equals(imageType)) {
                Log.d(TAG, "Updating profile image");
                FireBaseMethod fireBaseMethod = new FireBaseMethod(AccountSettingsActivity.this);
                fireBaseMethod.setProfilePhoto(imageUrl);
            }
        }




        if (intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: received intent from " + getString(R.string.profile_activity));
            setViewPager(sectionStatePagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }




    private void setupFragments2() {

        sectionStatePagerAdapter = new sectionStatePagerAdapter(this);
        sectionStatePagerAdapter.addFragment2(new EditProfileFragment(), getString(R.string.edit_profile_fragment));//fragment 0 by indel
        sectionStatePagerAdapter.addFragment2(new SignOutFragment(), getString(R.string.log_out_fragment));//fragment 1 by indel
    }
    private void setViewPager(int fragmentNumber) {
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #" + fragmentNumber);
        viewPager2.setAdapter(sectionStatePagerAdapter);
        viewPager2.setCurrentItem(fragmentNumber);
    }





    //setup for back arrow for navigating back to profile activity
    private void backArrow() {
        ImageButton backArrow = findViewById(R.id.backArrow);
        if (backArrow == null) {
            Log.e(TAG, "Back arrow view not found");
            return;
        }
        backArrow.setOnClickListener(v -> {
            if (isFinishing() || isDestroyed()) return;

            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(80)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(80);
                        Log.d(TAG, "Navigating back to previous activity");
                        finish();
                    }).start();
        });
    }






    public void setUpsettingsList() {
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list. ");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> option = new ArrayList<>();
        option.add(getString(R.string.general_settings));
        option.add(getString(R.string.edit_profile_fragment));//fragment 0 by indel
        option.add(getString(R.string.profile_image));
        option.add(getString(R.string.username));
        option.add(getString(R.string.cover_photo));
        option.add(getString(R.string.region_location));
        option.add(getString(R.string.account_settings));
        option.add(getString(R.string.email_address));
        option.add(getString(R.string.phone_number));
        option.add(getString(R.string.password_change));
        option.add(getString(R.string.two_fa_authentication));
        option.add(getString(R.string.privacy_security));
        option.add(getString(R.string.profile_visibility));
        option.add(getString(R.string.ip_location));
        option.add(getString(R.string.notification));
        option.add(getString(R.string.sound_vibration_settings));
        option.add(getString(R.string.content_preferences));
        option.add(getString(R.string.language_preferences));
        option.add(getString(R.string.appearance_customization));
        option.add(getString(R.string.theme_mode));
        option.add(getString(R.string.layout_preferences));
        option.add(getString(R.string.display_settings));
        option.add(getString(R.string.account_management));
        option.add(getString(R.string.request_verification));
        option.add(getString(R.string.download_data_history));
        option.add(getString(R.string.web_usage));
        option.add(getString(R.string.logout));
        option.add(getString(R.string.log_out_fragment));//fragment 1 by indel


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, option) {
            private final Set<Integer> headerPositions = new HashSet<>();
            {
                // Manually set which positions are headers
                headerPositions.add(0);  // General Settings
                headerPositions.add(6);  // Account Settings
                headerPositions.add(11); // Privacy and Security
                headerPositions.add(14); // Notification Settings
                headerPositions.add(16); // Content preferences
                headerPositions.add(18); // Appearance and customization
                headerPositions.add(22); // Account management
                headerPositions.add(26); // Logout
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                String item = getItem(position);

                if (isHeader(position)) {
                    textView.setTextColor(Color.BLACK);
                    textView.setTypeface(null, Typeface.BOLD);
                    view.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    textView.setPadding(dpToPx(5), dpToPx(1), dpToPx(10), dpToPx(0));
                } else {
                    textView.setTextColor(Color.DKGRAY);
                    textView.setTypeface(null, Typeface.NORMAL);
                    textView.setPadding(dpToPx(12), dpToPx(16), dpToPx(16), dpToPx(16));
                }
                return view;
            }

            private boolean isHeader(int position) {
                return headerPositions.contains(position);
            }

            @Override
            public boolean isEnabled(int position) {
                return !isHeader(position);
            }
        };
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment#:" + position);
                if (position == 1) {
                    setViewPager(0);
                } else if (position == 27) {
                    setViewPager(1);

                }

            }
        });
    }




    private  int dpToPx(int dp){
        return  (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(context, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
