package fer.ppij.whatthefilm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView movieTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieTitleTextView = findViewById(R.id.details_movie_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String originalTitle = extras.getString("originalTitle");
            movieTitleTextView.setText(originalTitle);
        }
    }
}
