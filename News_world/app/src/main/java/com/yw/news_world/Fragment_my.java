package com.yw.news_world;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_my extends Fragment {
    TextView my_id;
    ListView my_set;
    ArrayAdapter adapter;
    String [] set = {"我的关注","我的收藏","听新闻发音人设置"};
    public Fragment_my() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_fragment_my, container, false);
        my_set = (ListView)view.findViewById(R.id.my_set);

        adapter = new ArrayAdapter(getActivity(),R.layout.list_my,set);
        my_set.setAdapter(adapter);

        my_set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://我的关注
                        Intent intent = new Intent(getActivity(),Kind_set.class);
                        startActivity(intent);
                        break;
                    case 1://我的收藏
                        Intent intent1 = new Intent(getActivity(),Mycollect.class);
                        startActivity(intent1);
                        break;
                    case 2://发音人设置
                        Intent intent2 = new Intent(getActivity(),Set_listen.class);
                        startActivity(intent2);
                        break;

                }
            }
        });


        return view;
    }

}
