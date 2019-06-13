package fer.ppij.whatthefilm;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fer.ppij.whatthefilm.adapters.MovieAdapter;
import fer.ppij.whatthefilm.api.TMDbAPI;
import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.ResultsPage;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultsActivity extends AppCompatActivity {
    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "4c769c0e240a8d828da99a1019da67a8";

    private ListView moviesListView;
    private MovieAdapter mSearchAdapter;
    private List<Movie> movies;

    private Retrofit retrofit;

    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        initAdapter();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doQuerySearch(query);
        } else {
            String genres = intent.getStringExtra("id");
            doGenreSearch(genres);
        }
    }

    private void initAdapter() {
        moviesListView = findViewById(R.id.searchMoviesListView);
        movies = new ArrayList<>();
        mSearchAdapter = new MovieAdapter(movies, this);
        moviesListView.setAdapter(mSearchAdapter);
    }

    private void doQuerySearch(final String query) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.searching));
        dialog.setMessage(getString(R.string.finding_users));
        dialog.setCancelable(false);
        dialog.show();

        initRetrofit();
        updateMoviesList(query);

        dialog.dismiss();
    }


    private void doGenreSearch(String genres) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.searching));
        dialog.setMessage(getString(R.string.finding_users));
        dialog.setCancelable(false);
        dialog.show();

        initRetrofit();
        updateMoviesListGenres(genres);

        dialog.dismiss();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void updateMoviesListGenres(String genres) {
        TMDbAPI api = retrofit.create(TMDbAPI.class);
        Single<ResultsPage> singlePage = api.genreDiscover(genres, 1, API_KEY);
        updateMoviesList(singlePage);
    }

    private void updateMoviesList(String query) {
        TMDbAPI api = retrofit.create(TMDbAPI.class);
        Single<ResultsPage> singlePage = api.searchMovies(query, 1, API_KEY);
        updateMoviesList(singlePage);
    }

    private void updateMoviesList(Single<ResultsPage> singlePage) {
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
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Movie movie = adapter.getItem(position);
                                Toast.makeText(getApplicationContext(), movie.getOriginalTitle(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SearchResultsActivity.this, MovieDetailsActivity.class);
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
}
