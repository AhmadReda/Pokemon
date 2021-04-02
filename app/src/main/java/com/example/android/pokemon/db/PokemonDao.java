package com.example.android.pokemon.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.pokemon.model.Pokemon;

import java.util.List;

@Dao
public interface PokemonDao {
    @Insert
    void insertPokemon(Pokemon pokemon);
    @Query("delete from fav_table where id=:pokemonId")
    void deletePokemon(int pokemonId);
    @Query("delete from fav_table where name=:pokemonName")
    void deletePokemonByName(String pokemonName);
    @Query("select * from fav_table")
    LiveData<List<Pokemon>> getPokemons();
}
