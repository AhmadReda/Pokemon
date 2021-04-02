package com.example.android.pokemon.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.pokemon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MutableLiveData<Boolean> networkLiveData;
    // Splash Screen Members
    private Handler handler;
    private ImageView ivSplash;
    private TextView tvSplash;
    private LottieAnimationView lottieAnimationView;
    // Internet Connection
    private View vInternet;
    private boolean mainOffline;
    private NetworkRequest networkRequest;
    // Fragment Members
    private FrameLayout frameLayout;
    private PokemonFragment pokemonFragment;
    private FavFragment favFragment;
    private FragmentManager fragmentManager;
    private FragmentContainerView fvSlider;
    private FragmentContainerView fvPokemon;
    private FragmentContainerView fvFav;
    // Views

    private ConstraintLayout rootLayout;
    private static BottomNavigationView navigationView;
    private FloatingActionButton favUp;
    ///Comment for Pull Request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkLiveData = new MutableLiveData<Boolean>();

        //for hiding status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();

        handler = new Handler();
        handler.postDelayed(runnable,5400);

        setupAnim();

        pokemonFragment = PokemonFragment.getInstance();
        favFragment = FavFragment.getInstance();
//        fragmentManager = getSupportFragmentManager();

        navigationView.setItemIconTintList(null);
        navigationView.setOnNavigationItemSelectedListener(navListner);

//        SliderFragment sliderFragment = new SliderFragment();
//        fragmentManager.beginTransaction()
//                .add(R.id.fragment_frame_slider,sliderFragment)
//                .commit();
        if(isNetworkAvailable()){
            vInternet.setVisibility(View.GONE);
            mainOffline = false;
            fvPokemon.setVisibility(View.VISIBLE);
//            fragmentManager.beginTransaction()
////                    .add(R.id.fragment_frame,pokemonFragment)
////                    .commit();
        }else {
            mainOffline = true;
            vInternet.setVisibility(View.VISIBLE);
        }
        // Observer Network State
        registerNetwork();
        networkLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && mainOffline){
                    vInternet.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    mainOffline = false;
//                    Fragment fragment = PokemonFragment.getInstance();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.fragment_frame,fragment)
//                            .commit();
                    Snackbar.make( rootLayout,"You are online !", Snackbar.LENGTH_LONG)
                            .setAction("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setAnchorView(R.id.bottom_nav)
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .show();
                }else if(!aBoolean && !mainOffline){
                    Snackbar.make( rootLayout,"You are offline !", Snackbar.LENGTH_LONG)
                            .setAction("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setAnchorView(R.id.bottom_nav)
                            .setActionTextColor(getResources().getColor(R.color.red))
                            .show();
                }
            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFrag = null;
                    switch (item.getItemId()){
                        case R.id.btn_pokemon:
                            if(isNetworkAvailable()){
                                vInternet.setVisibility(View.GONE);
                                mainOffline = false;
//                                selectedFrag = pokemonFragment;
                                fvPokemon.setVisibility(View.VISIBLE);
                                fvFav.setVisibility(View.GONE);
                                setAnimForFragment();
                            }
                            else if(mainOffline){
                                vInternet.setVisibility(View.VISIBLE);
                                return true;
                            }else {
                                vInternet.setVisibility(View.GONE);

                                mainOffline = false;
                                //selectedFrag = pokemonFragment;
                                //frameLayout.setVisibility(View.VISIBLE);
                                fvPokemon.setVisibility(View.VISIBLE);
                                fvFav.setVisibility(View.GONE);
                                setAnimForFragment();
                            }
                        break;
                        case R.id.btn_fav:
                            if(mainOffline){
                                vInternet.setVisibility(View.GONE);
                                //fragContainer.setVisibility(View.GONE);
                                fvPokemon.setVisibility(View.GONE);
                                fvFav.setVisibility(View.VISIBLE);
                                setAnimForFragment();
                            }
                            fvPokemon.setVisibility(View.GONE);
                            fvFav.setVisibility(View.VISIBLE);
                            setAnimForFragment();
                            //selectedFrag = favFragment;

                        break;
                    }
//                    fragmentManager.beginTransaction()
//                            .add(R.id.fragment_frame,favFragment)
//                            .commit();
                    return true;
                }
            };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ivSplash.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.GONE);
            tvSplash.setVisibility(View.GONE);
            favUp.setVisibility(View.VISIBLE);
            navigationView.setVisibility(View.VISIBLE);
            if(!isNetworkAvailable()){
                vInternet.setVisibility(View.VISIBLE);
            }
        }
    };
    private void initViews(){
        navigationView = findViewById(R.id.bottom_nav);
        rootLayout = findViewById(R.id.root_layout);
        ivSplash = findViewById(R.id.splash_background);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        tvSplash = findViewById(R.id.splash_txt);
        vInternet = findViewById(R.id.iv_internet);
        fvPokemon = findViewById(R.id.pokemon_fragment);
        fvFav = findViewById(R.id.fav_fragment);
        fvSlider = findViewById(R.id.slider_fragment);
        favUp = findViewById(R.id.fab_up);
    }
    private void setupAnim(){
        ivSplash.animate().translationY(-2000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        tvSplash.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
    }
    private boolean isNetworkAvailable(){
        try{
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            // NetworkCapabilities obj = manager.getNetworkCapabilities(manager.getActiveNetwork());

            NetworkInfo networkInfo = null;
            if(manager !=null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo !=null && networkInfo.isConnected();
        }
        catch (NullPointerException e){
            return false;
        }
    }
    private void registerNetwork() {

        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerNetworkCallback(networkRequest,
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        postValue(true);
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        postValue(false);
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        postValue(false);
                    }
                });
    }
    //Set Value for LiveData
    private void postValue(boolean b){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                networkLiveData.setValue(b);
            }
        });
    }
    public static BottomNavigationView getNavigationView() {
        return navigationView;
    }
    public void goUP(View view) {
        //scrollTo(0,0)
        pokemonFragment.scrollView.fullScroll(View.FOCUS_UP);
        favFragment.scrollView.fullScroll(View.FOCUS_UP);
    }
    private void setAnimForFragment(){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit,R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }
}