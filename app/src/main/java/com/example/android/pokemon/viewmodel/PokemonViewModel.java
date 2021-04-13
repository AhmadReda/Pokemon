package com.example.android.pokemon.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.pokemon.model.Pokemon;
import com.example.android.pokemon.model.PokemonResponse;
import com.example.android.pokemon.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class PokemonViewModel extends ViewModel {

    private Repository repository;
    private MutableLiveData<ArrayList<Pokemon>> liveData = new MutableLiveData<>();
    private LiveData<List<Pokemon>> favList = null;
    private int page=0;
    private int scrollY = 0;

    @ViewModelInject
    public PokemonViewModel(Repository repository) {
        this.repository = repository;
    }
    public MutableLiveData<ArrayList<Pokemon>> getPokemonList(){
        return liveData;
    }
    public void getPokemons(int page,int limit){
        if(page != this.page || this.page == 0){
            repository.getPokemons(page,limit).subscribeOn(Schedulers.io())
                    .map(new Function<PokemonResponse, ArrayList<Pokemon>>() {
                        @Override
                        public ArrayList<Pokemon> apply(PokemonResponse pokemonResponse) throws Throwable {
                            ArrayList<Pokemon> list = pokemonResponse.getResults();
                            for(Pokemon pokemon : list){
                                String [] pokemonIndex = pokemon.getUrl().split("/");
                                pokemon.setUrl("https://pokeres.bastionbot.org/images/pokemon/"
                                        +pokemonIndex[pokemonIndex.length-1]+".png");
                            }
                            return list;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res->liveData.setValue(res),
                            err-> Log.e("ViewModel", err.getMessage() ));
        }
    }
    // Room
    public void insertPokemon(Pokemon pokemon){
        repository.insertPokemon(pokemon);
    }
    public void deletePokemon(int pokemonId){
        repository.deletePokemon(pokemonId);
    }
    public void deletePokemonByName(String pokemonName){
        repository.deletePokemonByName(pokemonName);
    }
    public void getFavPokemons(){
        favList = repository.getFavPokemons();
    }
    public LiveData<List<Pokemon>> getFavPokemonList(){
        return favList;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getScrollY() {
        return scrollY;
    }
    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

}
