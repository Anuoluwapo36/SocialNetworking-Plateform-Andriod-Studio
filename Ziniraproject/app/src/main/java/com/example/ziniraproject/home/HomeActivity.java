package com.example.ziniraproject.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ziniraproject.R;
import com.example.ziniraproject.login.LoginActivity;
import com.example.ziniraproject.message.MessageFragment;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.UserAccountSettings;
import com.example.ziniraproject.until.BottomNavigationViewHelper;
import com.example.ziniraproject.until.MainFeedAdapter;
import com.example.ziniraproject.until.SectionsPagerAdapter1;
import com.example.ziniraproject.until.ViewCommentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements  MainFeedAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if (fragment != null){
            fragment.displayMorePhotos();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private static  final String TAG="HomeActivity";

    private Context context = HomeActivity.this;
    private static  final  int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 0;
    private ProgressBar mProgressBar;
    private ShapeableImageView mProfilePhoto;

    //firebase
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private  ViewPager2 mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected  void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting");
        mViewPager = (ViewPager2)findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);
        setupBottomNavigationView();
//        setProfileImage();
        setupViewPager();
        setupFirebaseAuth();
//        mAuth.signOut();


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mFrameLayout.getVisibility() == View.VISIBLE) {
                    showLayout();
                } else {
                    setEnabled(false); // re-enable default behavior
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });




    }
    public  void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");
        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putString(getString(R.string.home_activity),getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }


//    /**
//     * responsible for setting profile image
//     */
//    private void setProfileImage() {
//        Log.d(TAG, "setProfileImage: Setting profile image");
//
//        ImageLoader.loadIntoShapeAble(
//                "https://www.gstatic.com/webp/gallery/1.jpg",
//                mProfilePhoto,
//                mProgressBar // Your ProgressBar variable
//        );
//
//
//    }

    public   void  hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");

        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (mFrameLayout.getVisibility()== VISIBLE){
//            showLayout();
//
//        }
//    }


    public   void  showLayout(){
        Log.d(TAG, "hideLayout: showing layout");

        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(GONE);
    }

    /**
     * responsible for adding tabs can store many fragment
     */
    private  void setupViewPager(){
        //ViewPager2 viewPager = findViewById(R.id.container);

        SectionsPagerAdapter1 adapter = new SectionsPagerAdapter1(this);
        adapter.addFragment(new HomeFragment(),"home",R.drawable.ic_home);
        adapter.addFragment(new  MessageFragment(),"message", R.drawable.ic_message);
//        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.container);

        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        new TabLayoutMediator(tabLayout, mViewPager,(tab, position) -> {
            tab.setText(adapter.getTabTitle(position));
        }).attach();

//what i learn so far is to known which dependency to use i'm
// currency using new version , easy to update than old library


    }


    /**
     * responsible for setting up bottom navigation view
     */

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(context, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }







    /*
     * --------------------------------------firebase------------------------------------------------------------
     *
     *
     */
    private  void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        if (user == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }

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

                //check current user that login
                checkCurrentUser(user);

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
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());

    }

    @Override
    public  void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }



}
