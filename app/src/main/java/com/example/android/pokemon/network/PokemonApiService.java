package com.example.android.pokemon.network;

import com.example.android.pokemon.model.PokemonResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PokemonApiService {
    @GET("pokemon")
    Observable<PokemonResponse> getPokemons(@Query("offset") int page,@Query("limit") int limit);
}
