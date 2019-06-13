package fer.ppij.whatthefilm.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import fer.ppij.whatthefilm.R;
import fer.ppij.whatthefilm.model.Genre;
import fer.ppij.whatthefilm.model.Movie;

// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
public class GenreAdapter extends ArrayAdapter<Genre> {
    private List<Genre> dataSet;
    private Context mContext;
    private List<Genre> checkedGenres;

    public GenreAdapter(List<Genre> data, Context context) {
        super(context, R.layout.row_item_genre, data);
        this.dataSet = data;
        this.mContext = context;
        checkedGenres = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Genre genre = dataSet.get(position);

        View rowView = inflater.inflate(R.layout.row_item_genre, parent, false);
        TextView textView = rowView.findViewById(R.id.firstLine);
        CheckBox checkBox = rowView.findViewById(R.id.checkbox);
        textView.setText(genre.getName());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedGenres.add(genre);
                } else {
                    checkedGenres.remove(genre);
                }
            }
        });
        return rowView;
    }

    public List<Genre> getCheckedGenres() {
        return checkedGenres;
    }
}
