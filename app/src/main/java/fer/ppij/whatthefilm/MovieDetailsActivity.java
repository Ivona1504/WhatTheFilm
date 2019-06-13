package fer.ppij.whatthefilm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView movieTitleTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieTitleTextView = findViewById(R.id.details_movie_title);
        imageView = findViewById(R.id.details_movie_poster);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String originalTitle = extras.getString("originalTitle");
            movieTitleTextView.setText(originalTitle);

            String poster = extras.getString("poster");

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);


            Glide.with(this).load("http://image.tmdb.org/t/p/w1280/"+poster).apply(options).into(imageView);
        }
    }
}
