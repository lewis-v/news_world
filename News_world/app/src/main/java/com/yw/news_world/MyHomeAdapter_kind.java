package com.yw.news_world;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */
public class MyHomeAdapter_kind extends RecyclerView.Adapter<MyHomeAdapter_kind.MyViewHolder> {
    OnItemClickLitener onItemClickLitener;
    Context context;
    List<String> list;

    public MyHomeAdapter_kind(Context context, List<String> list) {
        super();
        this.context = context;
        this.list = new ArrayList<>(list);
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
    public void setOnItemClikListener(OnItemClickLitener onItemClikListener){
        this.onItemClickLitener = onItemClikListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.kind_item, parent,
                false));
        return holder;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.tv.setText(list.get(position));

        if (onItemClickLitener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLitener.onItemClick(v,holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickLitener.onItemLongClick(v,holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addData(int position,String s) {
        list.add(position, s);
        notifyItemInserted(position);
    }

    public String removeData(int position) {
        String string = list.get(position);
        list.remove(position);
        notifyItemRemoved(position);
        return string;
    }

    class MyViewHolder extends ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.kind_name);
        }
    }
}

