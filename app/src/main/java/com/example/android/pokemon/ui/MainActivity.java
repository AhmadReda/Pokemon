package com.example.android.pokemon.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

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
    private NavController navController;
    private NavHostFragment navHostFragment;
    private View fragment;

    // Views

    private ConstraintLayout rootLayout;
    private static BottomNavigationView navigationView;
    private FloatingActionButton favUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkLiveData = new MutableLiveData<Boolean>();

        //for hiding status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();

        // Setup Navigation Component
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.getPreviousBackStackEntry();
        NavigationUI.setupWithNavController(navigationView,navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()){
                    case R.id.pokemonFragment:
                        if(mainOffline){
                            vInternet.setVisibility(View.VISIBLE);
                            fragment.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.favFragment:
                        if(mainOffline){
                            fragment.setVisibility(View.VISIBLE);
                            vInternet.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });

        handler = new Handler();
        handler.postDelayed(runnable,5400);

        setupAnim();

        navigationView.setItemIconTintList(null);


        if(isNetworkAvailable()){
            vInternet.setVisibility(View.GONE);
            mainOffline = false;
        }else {
            mainOffline = true;
            vInternet.setVisibility(View.VISIBLE);
            fragment.setVisibility(View.GONE);
        }
        // Observer Network State
        registerNetwork();
        networkLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && mainOffline){
                    vInternet.setVisibility(View.GONE);
                    fragment.setVisibility(View.VISIBLE);
                    //NavigationUI.setupWithNavController(navigationView,navController);
                    mainOffline = false;
                    Snackbar.make( rootLayout,"You are online !", Snackbar.LENGTH_LONG)
                            .setAction("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setAnchorView(R.id.bottom_nav)
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .show();
                    navController.popBackStack();
                    navController.navigate(R.id.pokemonFragment);
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
                }else if(aBoolean && !mainOffline){
                    Snackbar.make( rootLayout,"You are online !", Snackbar.LENGTH_LONG)
                            .setAction("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setAnchorView(R.id.bottom_nav)
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .show();
                }
            }
        });

    }
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
        fvSlider = findViewById(R.id.slider_fragment);
        favUp = findViewById(R.id.fab_up);
        fragment = findViewById(R.id.nav_host_fragment);

    }
    private void setupAnim(){
        ivSplash.animate().translationY(-2000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        tvSplash.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
    }
    public boolean isNetworkAvailable(){
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
        try{

            pokemonFragment.scrollViewPokemon.fullScroll(View.FOCUS_UP);
            favFragment.fvScrollView.fullScroll(View.FOCUS_UP);
        }
        catch (NullPointerException e){
            Log.d("Main", "goUP: "+e);
        }

    }
    private void setAnimForFragment(){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit,R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }
}