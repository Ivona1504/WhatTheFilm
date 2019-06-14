package fer.ppij.whatthefilm;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fer.ppij.whatthefilm.adapters.MovieAdapter;
import fer.ppij.whatthefilm.api.TMDbAPI;
import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.ResultsPage;
import fer.ppij.whatthefilm.model.User;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WatchlistActivity extends AppCompatActivity {

    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "4c769c0e240a8d828da99a1019da67a8";
    private Retrofit retrofit;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mCurrentUserDatabaseReference;
    private ListView mMoviesListView;
    private MovieAdapter adapter;

    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        initRetrofit();
        initAdapter();
        initFirebase();
    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mCurrentUserDatabaseReference = mUsersDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mCurrentUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getWatchlist() != null) {
                    for (Integer id : user.getWatchlist()) {
                        TMDbAPI api = retrofit.create(TMDbAPI.class);
                        Single<Movie> singleMovie = api.getMovie(id, API_KEY);
                        singleMovie.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Movie>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d("MAIN", "onSubscribe");
                                    }

                                    @Override
                                    public void onSuccess(Movie movie) {
                                        Log.i("MAIN", "onSuccess");
                                        adapter.add(movie);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void initAdapter() {
        mMoviesListView = findViewById(R.id.watchlist);
        movies = new ArrayList<>();
        adapter = new MovieAdapter(movies, this);
        mMoviesListView.setAdapter(adapter);
        mMoviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(WatchlistActivity.this, MovieDetailsActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("originalTitle", movie.getOriginalTitle());
                intent.putExtra("poster", movie.getPosterPath());
                intent.putExtra("overview", movie.getOverview());
                intent.putExtra("releaseDate", movie.getReleaseDate());
                intent.putExtra("runtime", movie.getRuntime());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.putExtra("genres", movie.getGenresList());
                }
                startActivity(intent);
            }
        });
    }
}
