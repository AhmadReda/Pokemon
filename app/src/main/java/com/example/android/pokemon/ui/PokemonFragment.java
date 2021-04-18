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
    private int page = 0;
    private final int limit = 10;
    public static NestedScrollView scrollViewPokemon;
    private static final String TAG = "PokemonFragment";
    public ShimmerFrameLayout shimmerFrameLayout;
    public Observer observer = new Observer<ArrayList<Pokemon>>() {
        @Override
        public void onChanged(ArrayList<Pokemon> pokemons) {
            //configShimmerVisibility();
            progressBar.setVisibility(View.GONE);
            pokemonAdapter.setDataSet(pokemons);
            Log.d(TAG, "TAG onChanged: set Data inside on LiveData Changed");
        }
    };
    public  Observer netWorkObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (aBoolean) {
                Log.d(TAG, "TAG onChanged: inside on Resue NA "+page);
                Log.d(TAG, "TAG Page  "+pokemonViewModel.getPage());
                pokemonViewModel.getPokemons(page, limit);

                pokemonViewModel.getPokemonList().observe(getActivity(), observer);
                if (page == 0){
                    page += 10;
                    pokemonViewModel.setPage(page);
                }
            }
        }
    };



    public PokemonFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pokemon, container, false);
        initViews(rootView);
        Log.d(TAG, "TAG onCreateView: ");

        return rootView;
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
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "TAG onStop: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Start shimmer
        //shimmerFrameLayout.startShimmer();

        pokemonViewModel = new ViewModelProvider(getActivity()).get(PokemonViewModel.class);
        pokemonAdapter = new PokemonAdapter(getActivity());

        page = pokemonViewModel.getPage();
        Log.d(TAG, "TAG onViewCreated: Page Start Num " + page);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(pokemonAdapter);

        ((MainActivity) getActivity()).networkLiveData.observe(getViewLifecycleOwner(), netWorkObserver);


        setupSwipe();
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
                    Log.d(TAG, "TAG onScrollChange: inside Scroll" + " Page num" + pokemonViewModel.getPage());

                }
                if (scrollY > oldScrollY && protect) {
                    MainActivity.getNavigationView().animate().translationY(MainActivity.getNavigationView().getHeight());
                    protect = false;
                }
                if (scrollY < oldScrollY && !protect) {
                    MainActivity.getNavigationView().animate().translationY(0);
                    protect = true;
                }
            }
        });
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
        pokemonViewModel.getPokemonList().removeObserver(observer);
        ((MainActivity) getActivity()).networkLiveData.removeObserver(netWorkObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        try{
            ((MainActivity) getActivity()).networkLiveData.removeObserver(netWorkObserver);
        }catch (NullPointerException e){
            Log.d(TAG, "TAG onDetach: "+e);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        try{
            ((MainActivity) getActivity()).networkLiveData.removeObserver(netWorkObserver);
        }catch (NullPointerException e){
            Log.d(TAG, "TAG onAttach: "+e);
        }
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
