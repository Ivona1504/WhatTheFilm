package fer.ppij.whatthefilm;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import fer.ppij.whatthefilm.model.Movie;

// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
public class MovieAdapter extends ArrayAdapter<Movie> implements View.OnClickListener {
    private List<Movie> dataSet;
    private Context mContext;

    public MovieAdapter(List<Movie> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }


    @Override
    public void onClick(View view) {

    }
}
