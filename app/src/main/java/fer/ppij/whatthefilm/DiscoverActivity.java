package fer.ppij.whatthefilm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import fer.ppij.whatthefilm.adapters.GenreAdapter;
import fer.ppij.whatthefilm.model.Genre;

public class DiscoverActivity extends AppCompatActivity {

    private ListView mGenresListView;
//    private ListView mYearsListView;

    private GenreAdapter mGenreAdapter;
    private Button btnDiscover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        bindViews();
        initAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "Search requested", Toast.LENGTH_LONG).show();
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bindViews() {
        mGenresListView = findViewById(R.id.genresListView);
//        mYearsListView = findViewById(R.id.yearsListView);
        btnDiscover = findViewById(R.id.btnDiscover);

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                List<Genre> genres = mGenreAdapter.getCheckedGenres();
                StringJoiner sb = new StringJoiner(",");
                for (Genre g: genres) {
                    sb.add(String.valueOf(g.getId()));
                }
                Intent intent = new Intent(DiscoverActivity.this, SearchResultsActivity.class);
                intent.putExtra("id", sb.toString());
                startActivity(intent);
            }
        });
    }

    private void initAdapter() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(12, "Adventure"));
        genres.add(new Genre(14, "Fantasy"));
        genres.add(new Genre(16, "Animation"));
        genres.add(new Genre(18, "Drama"));
        genres.add(new Genre(27, "Horror"));
        genres.add(new Genre(28, "Action"));
        genres.add(new Genre(35, "Comedy"));
        genres.add(new Genre(36, "History"));
        genres.add(new Genre(53, "Thriller"));
        genres.add(new Genre(80, "Crime"));
        genres.add(new Genre(99, "Documentary"));
        genres.add(new Genre(878, "Science Fiction"));
        genres.add(new Genre(9648, "Mystery"));
        genres.add(new Genre(10402, "Music"));
        genres.add(new Genre(10749, "Romance"));
        genres.add(new Genre(10751, "Family"));
        genres.add(new Genre(10752, "War"));


        mGenreAdapter = new GenreAdapter(genres, this);
        mGenresListView.setAdapter(mGenreAdapter);
//        mGenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //
//            }
//        });
    }

}
