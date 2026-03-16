package com.example.ziniraproject.until;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//import com.example.ziniraproject.profile.EditProfileFragment;
import com.example.ziniraproject.profile.SignOutFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * class for storing fragment for tabs
 */
public class SectionsPagerAdapter1 extends FragmentStateAdapter {

    private static  final String TAG ="SectionsPagerAdapter";
    private  final List<Fragment> mfragmentList = new ArrayList<>();
    private  final  List<String> titles = new ArrayList<>();
    private  final  List<Integer> icons = new ArrayList<>();
//    private final HashMap<Fragment, Integer> mFragments =  new HashMap<>();
//    private final HashMap<String, Integer> mFragmentNumbers =  new HashMap<>();
//    private final HashMap<Integer, String> mFragmentNames =  new HashMap<>();

    public SectionsPagerAdapter1(@NonNull  FragmentActivity fa){
        super(fa);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mfragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mfragmentList.size();
    }
    public  void  addFragment(Fragment fragment, String title, @DrawableRes int iconRes){
        mfragmentList.add(fragment);
        titles.add(title);
        icons.add(iconRes);

    }
    public Fragment getFragment(int position) {
        return mfragmentList.get(position);
    }
    public String getTabTitle(int position) {
        return titles.get(position);
    }
    public  int getIcon(int position){
        return  icons.get(position);
    }


//
//    /**
//     * return the fragment with the name
//     * @param fragmentName
//     * @return
//     */
//    public  Integer getFragmentNumber(String fragmentName){
//        if (mFragmentNumbers.containsKey(fragmentName)){
//            return  mFragmentNumbers.get(fragmentName);
//        }else{
//            return null;
//        }
//    }
//
//    /**
//     * return the fragment with the name
//     * @param fragment
//     * @return
//     */
//    public  Integer getFragmentNumber(Fragment fragment){
//        if (mFragmentNumbers.containsKey(fragment)){
//            return  mFragmentNumbers.get(fragment);
//        }else{
//            return null;
//        }
//    }
//
//    /**
//     * return the fragment with the name
//     * @param fragmentNumber
//     * @return
//     */
//    public  Integer getFragmentName(Integer fragmentNumber){
//        if (mFragmentNames.containsKey(fragmentNumber)){
//            return  mFragmentNumbers.get(fragmentNumber);
//        }else{
//            return null;
//        }
//    }
//
//    public void addFragment2(Fragment fragment, String fragmentName) {
//        mFragments.put(fragment,mfragmentList.size()-1);
//        mFragmentNumbers.put(fragmentName,mfragmentList.size()-1);
//        mFragmentNames.put(mfragmentList.size()-1,fragmentName);
//    }
}
