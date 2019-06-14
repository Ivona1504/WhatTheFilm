package fer.ppij.whatthefilm;

import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import fer.ppij.whatthefilm.api.TMDbAPI;
import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.User;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsActivity extends AppCompatActivity {
    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = "4c769c0e240a8d828da99a1019da67a8";
    private Retrofit retrofit;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mCurrentUserDatabaseReference;

    private TextView movieTitleTextView;
    private TextView genresTextView;
    private TextView runtimeTextView;
    private TextView overviewTextView;

    private ImageView imageView;
    private Button btnAddToWatchlist;
    private Button btnAddToWatched;

    private User mUser;

    private int id;

    private ImageButton[] btn = new ImageButton[10];
    private int[] btn_id = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        bindViews();
        movieTitleTextView = findViewById(R.id.details_movie_title);
        genresTextView = findViewById(R.id.details_genresTextView);
        runtimeTextView = findViewById(R.id.details_runtimeTextView);
        overviewTextView = findViewById(R.id.details_overviewTextView);
        imageView = findViewById(R.id.details_movie_poster);

        initRetrofit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            movieTitleTextView.setAutoSizeTextTypeUniformWithConfiguration(
                    1, 36, 1, TypedValue.COMPLEX_UNIT_DIP);

            genresTextView.setAutoSizeTextTypeUniformWithConfiguration(
                    1, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
        }

        btnAddToWatchlist = findViewById(R.id.btnWatchlist);
        btnAddToWatched = findViewById(R.id.btnWatched);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            String poster = extras.getString("poster");
            String title = extras.getString("originalTitle");

            movieTitleTextView.setText(title);

            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);


            Glide.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w1280/"+poster).apply(options).into(imageView);
            updateUI(id);
        }

        initFirebase();

        btnAddToWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser.getWatchlist() == null) {
                    mUser.setWatchlist(new ArrayList<Integer>());
                }
                if (!mUser.getWatchlist().contains(id)) {
                    mUser.getWatchlist().add(id);
                    btnAddToWatchlist.setText("Remove from watchlist");
                    btnAddToWatchlist.setBackgroundColor(getResources().getColor(R.color.removeWatchlistButton));
                    mCurrentUserDatabaseReference.child("watchlist").setValue(mUser.getWatchlist());
                } else {
                    mUser.getWatchlist().remove(new Integer(id));
                    btnAddToWatchlist.setText("Add to watchlist");
                    btnAddToWatchlist.setBackgroundColor(getResources().getColor(R.color.addWatchlistButton));
                    mCurrentUserDatabaseReference.child("watchlist").setValue(mUser.getWatchlist());
                }
            }
        });

        btnAddToWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser.getWatched() == null) {
                    mUser.setWatched(new ArrayList<Integer>());
                }
                if (!mUser.getWatched().contains(id)) {
                    mUser.getWatched().add(id);
                    btnAddToWatched.setText("Remove from watched");
                    btnAddToWatched.setBackgroundColor(getResources().getColor(R.color.removeWatchlistButton));
                    mCurrentUserDatabaseReference.child("watched").setValue(mUser.getWatched());
                } else {
                    mUser.getWatched().remove(new Integer(id));
                    btnAddToWatched.setText("Add to watched");
                    btnAddToWatched.setBackgroundColor(getResources().getColor(R.color.addWatchlistButton));
                    mCurrentUserDatabaseReference.child("watched").setValue(mUser.getWatched());
                }
            }
        });
    }

    private void updateUI(int id) {
        TMDbAPI api = retrofit.create(TMDbAPI.class);
        Single<Movie> singleMovie = api.getMovie(id, API_KEY);
        singleMovie.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Movie>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(Movie movie) {
                        genresTextView.setText(movie.getGenresList());
                        overviewTextView.setText(movie.getOverview());
                        runtimeTextView.setText(movie.getReleaseDate() + " • " + movie.getRuntime() + "'");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mCurrentUserDatabaseReference = mUsersDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mCurrentUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                if (mUser.getWatchlist() != null && mUser.getWatchlist().contains(id)) {
                    btnAddToWatchlist.setText("Remove from watchlist");
                    btnAddToWatchlist.setBackgroundColor(getResources().getColor(R.color.removeWatchlistButton));
                }
                if (mUser.getWatched() != null && mUser.getWatched().contains(id)) {
                    btnAddToWatched.setText("Remove from watched");
                    btnAddToWatched.setBackgroundColor(getResources().getColor(R.color.removeWatchlistButton));
                }
                if (mUser.getRatings() != null && mUser.getRatings().containsKey(String.valueOf(id))) {
                    setFocus(mUser.getRatings().get(String.valueOf(id))-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bindViews() {
        for(int i = 0; i < btn.length; i++){
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.not_vote);
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn0 :
                            setFocus(0);
                            break;

                        case R.id.btn1 :
                            setFocus(1);
                            break;

                        case R.id.btn2 :
                            setFocus(2);
                            break;

                        case R.id.btn3 :
                            setFocus(3);
                            break;

                        case R.id.btn4 :
                            setFocus(4);
                            break;

                        case R.id.btn5 :
                            setFocus(5);
                            break;

                        case R.id.btn6 :
                            setFocus(6);
                            break;

                        case R.id.btn7 :
                            setFocus(7);
                            break;

                        case R.id.btn8 :
                            setFocus(8);
                            break;

                        case R.id.btn9 :
                            setFocus(9);
                            break;
                    }
                }
            });
        }
    }

    private void setFocus(int vote){
        if (mUser.getWatched() != null && mUser.getWatched().contains(id)) {
            for(int i = 0; i <= vote; i++){
                btn[i].setBackgroundResource(R.drawable.favorite);
            }
            for(int i = vote+1; i < btn.length; i++) {
                btn[i].setBackgroundResource(R.drawable.not_vote);
            }
            if (mUser.getRatings() == null) {
                mUser.setRatings(new HashMap<String, Integer>());
            }
            mUser.getRatings().put(String.valueOf(id), vote+1);
            mCurrentUserDatabaseReference.child("ratings").child(String.valueOf(id)).setValue(mUser.getRatings().get(String.valueOf(id)));
        } else {
            Toast.makeText(this, "You have to add movie to watched!", Toast.LENGTH_LONG).show();
        }
    }

}
