package com.kyee.iwis.nana;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CommonViewPagerAdapter extends FragmentPagerAdapter {

    private final FragmentManager fragmetnmanager;
    private final List<Fragment> listfragment;

    public CommonViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fragmetnmanager = fm;
        this.listfragment = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return listfragment.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listfragment.size();
    }


}