package fer.ppij.whatthefilm;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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

import fer.ppij.whatthefilm.model.User;

public class MovieDetailsActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mCurrentUserDatabaseReference;

    private TextView movieTitleTextView;
    private ImageView imageView;
    private ImageButton btnAdd;
    private User mUser;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieTitleTextView = findViewById(R.id.details_movie_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            movieTitleTextView.setAutoSizeTextTypeUniformWithConfiguration(
                    1, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
        }
        imageView = findViewById(R.id.details_movie_poster);
//        btnAdd = findViewById(R.id.btnAddToWatchlist);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            String originalTitle = extras.getString("originalTitle");
            movieTitleTextView.setText(originalTitle);

            String poster = extras.getString("poster");

            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);


            Glide.with(this).load("http://image.tmdb.org/t/p/w1280/"+poster).apply(options).into(imageView);
        }

        initFirebase();

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mUser.getWatchlist() == null) {
//                    mUser.setWatchlist(new ArrayList<Integer>());
//                }
//                if (!mUser.getWatchlist().contains(id)) {
//                    mUser.getWatchlist().add(id);
//                    mCurrentUserDatabaseReference.child("watchlist").setValue(mUser.getWatchlist());
//                }
//            }
//        });
    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mCurrentUserDatabaseReference = mUsersDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mCurrentUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
