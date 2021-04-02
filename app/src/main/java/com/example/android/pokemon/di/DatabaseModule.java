package com.example.android.pokemon.di;

import android.app.Application;

import androidx.room.Room;

import com.example.android.pokemon.db.*;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static PokemonDB provideDB(Application application){
        return Room.databaseBuilder(application,PokemonDB.class,"fav_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
    @Provides
    @Singleton
    public static PokemonDao provideDao(PokemonDB pokemonDB){
        return pokemonDB.pokemonDao();
    }

}
