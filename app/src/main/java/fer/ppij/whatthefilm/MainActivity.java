package fer.ppij.whatthefilm;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fer.ppij.whatthefilm.adapters.DrawerAdapter;
import fer.ppij.whatthefilm.adapters.MovieAdapter;
import fer.ppij.whatthefilm.api.TMDbAPI;
import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.ResultsPage;
import fer.ppij.whatthefilm.util.DrawerItem;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "4c769c0e240a8d828da99a1019da67a8";
    private Retrofit retrofit;

    private ListView moviesListView;

    private MovieAdapter adapter;

    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DrawerAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bindViews();

        initRetrofit();
        updateMoviesList();

        addDrawerItems();
        setupDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.log_out) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void bindViews() {
        moviesListView = findViewById(R.id.moviesListView);
        mDrawerList = findViewById(R.id.navList);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void addDrawerItems() {
        List<DrawerItem> data = new ArrayList<>();
        data.add(new DrawerItem("Discover"));
        data.add(new DrawerItem("Watchlist"));
        data.add(new DrawerItem("Recommendations"));
        mDrawerAdapter = new DrawerAdapter(this, R.layout.item_drawer, data);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(MainActivity.this, DiscoverActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, WatchlistActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, RecommendationsActivity.class));
                break;
            default:
                break;
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("WhatTheFilm");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle("WhatTheFilm");
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void updateMoviesList() {
        TMDbAPI api = retrofit.create(TMDbAPI.class);
        Single<ResultsPage> singlePage = api.getPopularMovies(1, API_KEY);
        singlePage.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResultsPage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("MAIN", "onSubscribe");
                    }

                    @Override
                    public void onSuccess(ResultsPage resultsPage) {
                        Log.d("MAIN", "onSuccess");
                        adapter = new MovieAdapter(resultsPage.getResults(), getApplicationContext());
                        moviesListView.setAdapter(adapter);
                        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Movie movie = adapter.getItem(position);
                                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                                intent.putExtra("id", movie.getId());
                                intent.putExtra("originalTitle", movie.getOriginalTitle());
                                intent.putExtra("poster", movie.getPosterPath());
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
