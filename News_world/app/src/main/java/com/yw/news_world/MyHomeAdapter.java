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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */
public class MyHomeAdapter extends RecyclerView.Adapter<MyHomeAdapter.MyViewHolder> {
    OnItemClickLitener onItemClickLitener;
    Context context;
    List<Jsoup_data> list;

    public MyHomeAdapter(Context context, List<Jsoup_data> list) {
        super();
        this.context = context;
        this.list = list;
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
                context).inflate(R.layout.item, parent,
                false));
        return holder;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (list.get(position).title.length()>5){
            holder.tv.setText(list.get(position).title);
        }
        if (list.get(position).imageView!=null) {
            holder.layout.setBackground(new BitmapDrawable(context.getResources(), list.get(position).getBitmap()));
        }else{
            holder.layout.setBackgroundResource(R.mipmap.news_back);
        }
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

    class MyViewHolder extends ViewHolder {

        TextView tv;
        RelativeLayout layout;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_title);
            layout = (RelativeLayout) view.findViewById(R.id.item_layout);
        }
    }
}

