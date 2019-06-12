package fer.ppij.whatthefilm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

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

public class MainActivity extends AppCompatActivity {
    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "4c769c0e240a8d828da99a1019da67a8";
    private Retrofit retrofit;

    private ListView moviesListView;

    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesListView = findViewById(R.id.moviesListView);


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        TMDbAPI api = retrofit.create(TMDbAPI.class);
        Single<ResultsPage> singlePage = api.getPopularMovies(1, API_KEY);
        singlePage.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResultsPage>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final ResultsPage resultsPage) {
                        Toast.makeText(getApplicationContext(), resultsPage.getResults().size(), Toast.LENGTH_LONG).show();
                        adapter = new MovieAdapter(resultsPage.getResults(), getApplicationContext());
                        moviesListView.setAdapter(adapter);
                        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Movie movie = resultsPage.getResults().get(position);
                                Toast.makeText(getApplicationContext(), movie.getOriginalTitle(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                                intent.putExtra("id", movie.getId());
                                intent.putExtra("originalTitle", movie.getOriginalTitle());
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


}
