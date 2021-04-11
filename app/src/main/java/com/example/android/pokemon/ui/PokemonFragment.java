package com.example.android.pokemon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pokemon.R;
import com.example.android.pokemon.adapter.PokemonAdapter;
import com.example.android.pokemon.model.Pokemon;
import com.example.android.pokemon.viewmodel.PokemonViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class PokemonFragment extends Fragment {

    private PokemonViewModel pokemonViewModel;
    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private ProgressBar progressBar;
    public static NestedScrollView scrollViewPokemon;
    private static final String TAG = "PokemonFragment";
    public ShimmerFrameLayout shimmerFrameLayout;

    private int page = 0;
    private final int limit = 10;


    public PokemonFragment() {}
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pokemon, container, false);
        initViews(rootView);
        Log.d(TAG, "TAG onCreateView: ");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Start shimmer
        //shimmerFrameLayout.startShimmer();
        Log.d(TAG, "TAG onActivityCreated: ");
        
        pokemonViewModel = new ViewModelProvider(getActivity()).get(PokemonViewModel.class);
        pokemonAdapter = new PokemonAdapter(getActivity());
        
        page = pokemonViewModel.getPage();
        Log.d(TAG, "TAG onActivityCreated: Page Start Num "+page);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(pokemonAdapter);
//        if(page >0){
//            pokemonViewModel.getPokemons(0, page);
//            Log.d(TAG, "inside new Call with page = "+page);
//        }


            pokemonViewModel.getPokemons(page, limit);


            if(page ==0){
                page+=10;
                pokemonViewModel.setPage(page);
            }
            pokemonViewModel.getPokemonList().observe(getActivity(), new Observer<ArrayList<Pokemon>>() {
                @Override
                public void onChanged(ArrayList<Pokemon> pokemons) {
                    //configShimmerVisibility();
                    Log.d(TAG, "TAG onChanged: After Progress Bar");

                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "TAG onChanged: Before Progress Bar");
                    pokemonAdapter.setDataSet(pokemons);
                    Log.d(TAG, "TAG onChanged: set Data inside on LiveData Changed");
                }
            });

        setupSwipe();

        // hit the api with every 10 pokemons are displayed
        scrollViewPokemon.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean protect = true;
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //check condition
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // when reach last item position
                    // increase page size
                    page += 10;

                    progressBar.setVisibility(View.VISIBLE);
                    pokemonViewModel.getPokemons(page, limit);
                    pokemonViewModel.setPage(page);
                    Log.d(TAG, "TAG onScrollChange: inside Scroll"+" Page num"+pokemonViewModel.getPage());

                }
                if(scrollY > oldScrollY && protect){
                    MainActivity.getNavigationView().animate().translationY(MainActivity.getNavigationView().getHeight());
                    protect = false;
                }if(scrollY < oldScrollY && !protect){
                    MainActivity.getNavigationView().animate().translationY(0);
                    protect = true;
                }
//                Log.d(TAG, "TAG onScrollChange: Y"+scrollY);
//                Log.d(TAG, "TAG onScrollChange: X"+scrollX);
//                if(scrollY !=0)
//                    pokemonViewModel.setScrollY(scrollY);
            }
        });
     }

    private void setupSwipe() {

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPokemonPos = viewHolder.getAdapterPosition();
                Pokemon pokemon = pokemonAdapter.getPokemonAt(swipedPokemonPos);
                try {
                    pokemonViewModel.insertPokemon(pokemon);

                    Snackbar.make(recyclerView, "pokemon added to database", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pokemonViewModel.deletePokemonByName(pokemon.getName());
                                }
                            })
                            .setAnchorView(R.id.bottom_nav)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Already Exist",
                            Toast.LENGTH_SHORT).show();
                }
                pokemonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor
                                (getActivity(), R.color.colorAccent))
                        .addSwipeRightActionIcon(R.drawable.ic_favorite_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "TAG onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "TAG onResume: ");
//        scrollViewPokemon.smoothScrollTo(0,pokemonViewModel.getScrollY());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "TAG onStop: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "TAG onStart: ");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_pokemonList);
        progressBar = view.findViewById(R.id.progress_bar);
        scrollViewPokemon = view.findViewById(R.id.nested_scroll_view_pokemon);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
    }
    private void configShimmerVisibility() {
        // Show recycler view
        recyclerView.setVisibility(View.VISIBLE);
        // Stop shimmer effect
        shimmerFrameLayout.stopShimmer();
        // Hide shimmer layout
        shimmerFrameLayout.setVisibility(View.GONE);
    }
}
