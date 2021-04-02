package com.example.android.pokemon.repository;


import androidx.lifecycle.LiveData;

import com.example.android.pokemon.db.PokemonDao;
import com.example.android.pokemon.model.Pokemon;
import com.example.android.pokemon.model.PokemonResponse;
import com.example.android.pokemon.network.PokemonApiService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class Repository {
    private PokemonApiService apiService;
    private PokemonDao pokemonDao;
    @Inject
    public Repository(PokemonApiService apiService,PokemonDao pokemonDao) {
        this.apiService = apiService;
        this.pokemonDao = pokemonDao;
    }
    // Get data from API
    public Observable<PokemonResponse> getPokemons(int page, int limit){
        return apiService.getPokemons(page,limit);
    }
    // Get data from DB
    public void insertPokemon(Pokemon pokemon){
        pokemonDao.insertPokemon(pokemon);
    }
    public void deletePokemon(int pokemonId){
        pokemonDao.deletePokemon(pokemonId);
    }
    public void deletePokemonByName(String pokemonName){
        pokemonDao.deletePokemonByName(pokemonName);
    }
    public LiveData<List<Pokemon>> getFavPokemons(){
        return pokemonDao.getPokemons();
    }
}
