package com.example.android.pokemon.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.pokemon.R;
import com.example.android.pokemon.adapter.SliderAdapter;
import com.example.android.pokemon.model.Pokemon;
import com.example.android.pokemon.viewmodel.PokemonViewModel;

import java.util.List;

public class SliderFragment extends Fragment {

    private ViewPager2 viewPager2;
    private PokemonViewModel pokemonViewModel;
    private SliderAdapter sliderAdapter;
    private Handler sliderHandler = new Handler();

    public SliderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_slider,container,false);
        viewPager2 = viewRoot.findViewById(R.id.viewPager);

        sliderAdapter = new SliderAdapter(getActivity(),R.layout.slider_container,viewPager2);
        viewPager2.setAdapter(sliderAdapter);

        setupSlider();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(runnable);
                sliderHandler.postDelayed(runnable,3000);
            }
        });
        return viewRoot;
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() +1);
        }
    };
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pokemonViewModel = new ViewModelProvider(getActivity()).get(PokemonViewModel.class);
        pokemonViewModel.getFavPokemons();
        pokemonViewModel.getFavPokemonList().observe(getActivity(), new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                sliderAdapter.setDataSet(pokemons);
                sliderAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(runnable,3000);
    }

    private void setupSlider(){
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(5));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r *0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

    }
}
