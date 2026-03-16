package com.example.ziniraproject.until;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class sectionStatePagerAdapter extends FragmentStateAdapter {


    private final List<Fragment> fragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments =  new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers =  new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames =  new HashMap<>();

    public  sectionStatePagerAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
    public   void addFragment2(Fragment fragment, String fragmentName){
        fragmentList.add(fragment);
        mFragments.put(fragment,fragmentList.size()-1);
        mFragmentNumbers.put(fragmentName,fragmentList.size()-1);
        mFragmentNames.put(fragmentList.size()-1,fragmentName);

    }

    /**
     * return the fragment with the name
     * @param fragmentName
     * @return
     */
    public  Integer getFragmentNumber(String fragmentName){
        if (mFragmentNumbers.containsKey(fragmentName)){
            return  mFragmentNumbers.get(fragmentName);
        }else{
            return null;
        }
    }

    /**
     * return the fragment with the name
     * @param fragment
     * @return
     */
    public  Integer getFragmentNumber(Fragment fragment){
        if (mFragmentNumbers.containsKey(fragment)){
            return  mFragmentNumbers.get(fragment);
        }else{
            return null;
        }
    }

    /**
     * return the fragment with the name
     * @param fragmentNumber
     * @return
     */
    public  String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return String.valueOf(mFragmentNumbers.get(fragmentNumber));
        }else{
            return null;
        }
    }


}
