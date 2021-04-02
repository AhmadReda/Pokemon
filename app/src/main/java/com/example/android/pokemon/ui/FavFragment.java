package com.example.android.pokemon.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pokemon.R;
import com.example.android.pokemon.adapter.FavAdapter;
import com.example.android.pokemon.model.Pokemon;
import com.example.android.pokemon.viewmodel.PokemonViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavFragment extends Fragment {
    private PokemonViewModel pokemonViewModel;
    private RecyclerView recyclerView;
    private FavAdapter pokemonAdapter;
    public static NestedScrollView scrollView;

    private static FavFragment instance = new FavFragment();
    public FavFragment() {
    }

    public static FavFragment getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pokemon_fav,container,false);
        recyclerView = rootView.findViewById(R.id.rv_pokemonList);

        pokemonAdapter =  FavAdapter.getInstance(getActivity());
        recyclerView.setAdapter(pokemonAdapter);
        scrollView = rootView.findViewById(R.id.nested_scroll_view);
        setupSwipe();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pokemonViewModel = new ViewModelProvider(getActivity()).get(PokemonViewModel.class);
        pokemonViewModel.getFavPokemons();
        pokemonViewModel.getFavPokemonList().observe(getActivity(), new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                pokemonAdapter.setDataSet(pokemons);
                pokemonAdapter.notifyDataSetChanged();
            }
        });

        // hit the api with every 10 pokemons are displayed
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean protect = true;
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //check condition
//                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                    // when reach last item position
//                    // increase page size
//                    page += 10;
//                    //progressBar.setVisibility(View.VISIBLE);
//                    pokemonViewModel.getPokemons(page, limit);
//                    Log.d(TAG, "TAG onScrollChange: inside Scroll");
//                }
                if(scrollY > oldScrollY && protect){
                    MainActivity.getNavigationView().animate().translationY(MainActivity.getNavigationView().getHeight());
                    protect = false;
                }if(scrollY < oldScrollY && !protect){
                    MainActivity.getNavigationView().animate().translationY(0);
                    protect = true;
                }
            }
        });
    }

    private void setupSwipe() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPokemonPos = viewHolder.getAdapterPosition();
                Pokemon pokemon = pokemonAdapter.getPokemonAt(swipedPokemonPos);
                pokemonViewModel.deletePokemon(pokemon.getId());
                pokemonAdapter.notifyDataSetChanged();

                Snackbar.make(recyclerView,"pokemon deleted from database",Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pokemonViewModel.insertPokemon(pokemon);
                                pokemonAdapter.notifyDataSetChanged();
                            }
                        })
                        .setAnchorView(R.id.bottom_nav)
                        .show();
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor
                                (getActivity(),R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic__delete_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}
