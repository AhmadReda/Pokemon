package com.example.android.pokemon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.pokemon.R;
import com.example.android.pokemon.model.Pokemon;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.ArrayList;
import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {
    List<Pokemon> dataSet = new ArrayList<Pokemon>();
    private Context context;
    private static FavAdapter instance;
    private FavAdapter (Context context){
        this.context = context;
    }
    public static FavAdapter getInstance(Context context){
        instance = new FavAdapter(context);
        return instance;
    }
    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_item, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        // Initialize shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(context.getResources().getColor(R.color.highlightShimmer))
                .setBaseAlpha(1)
                .setHighlightColor(context.getResources().getColor(R.color.shimmer))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        // Initialize shimmer drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        // Set shimmer
        shimmerDrawable.setShimmer(shimmer);

        Glide.with(context).load(dataSet.get(position).getUrl())
                .placeholder(shimmerDrawable)
                .into(holder.imageView);

        holder.textView.setText(dataSet.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setDataSet(List<Pokemon> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }
    public Pokemon getPokemonAt(int position){
        return dataSet.get(position);
    }
    public class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_pokemonImage);
            textView = itemView.findViewById(R.id.tv_pokemonName);
        }
    }
}
