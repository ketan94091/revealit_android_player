package com.Revealit.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    Context context;
    ArrayList<Fragment> fragments;

    public FragmentAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;

    }


    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

}
