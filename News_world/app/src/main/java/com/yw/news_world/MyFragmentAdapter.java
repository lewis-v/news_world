package com.yw.news_world;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */
class MyFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList;
    List <String> string;
    MyFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> string){
        super(fm);
        this.fragmentList = fragmentList;
        this.string = string;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return string.get(position);
    }

}
