package com.example.android.pokemon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.android.pokemon.R;
import com.example.android.pokemon.model.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    List<Pokemon> dataSet = new ArrayList<>();
    private Context context;
    private int itemView;
    private ViewPager2 viewPager2;

    public SliderAdapter(Context context, int itemView,ViewPager2 viewPager2) {
        this.context = context;
        this.itemView = itemView;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemView, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(dataSet.get(position).getUrl())
                .into(holder.imageView);
        if(position == dataSet.size() -2){
            viewPager2.post(runnable);

        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setDataSet(List<Pokemon> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dataSet.addAll(dataSet);
            notifyDataSetChanged();
        }
    };
}
