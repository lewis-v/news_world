package com.yw.news_world;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_home extends Fragment {
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageButton add;

    public Fragment_home() {
        // Required empty public constructor
        fragmentList.clear();
        Mydata.kind = Mydata.have_kind.get(0);
        Bundle bundle = new Bundle();
        bundle.putString("kind", Mydata.have_kind.get(0));
        Fragment_home_change fragment = new Fragment_home_change();
        fragment.setArguments(bundle);
        fragmentList.add(fragment);
        for (int i= 1;i<Mydata.have_kind.size();i++) {
            Mydata.kind = Mydata.have_kind.get(i);
            bundle = new Bundle();
            bundle.putString("kind", Mydata.have_kind.get(i));
            Fragment_home_default fragmentHomeDefault = new Fragment_home_default();
            fragmentHomeDefault.setArguments(bundle);
            fragmentList.add(fragmentHomeDefault);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_fragment_home, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.home_view);
        tabLayout = (TabLayout)view.findViewById(R.id.home_tab);
        add = (ImageButton)view.findViewById(R.id.addkind);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Kind_set.class);
                startActivity(intent);
            }
        });
        if (Mydata.have_kind.size()>4) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else{
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        viewPager.setAdapter(new MyFragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList,Mydata.have_kind));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(Mydata.have_kind.size());//pager缓存view数量

        return view;
    }
}
