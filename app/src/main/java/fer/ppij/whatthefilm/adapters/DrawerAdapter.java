package fer.ppij.whatthefilm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fer.ppij.whatthefilm.R;
import fer.ppij.whatthefilm.util.DrawerItem;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    private Context mContext;
    private int layoutResourceId;
    private List<DrawerItem> data;

    public DrawerAdapter(Context mContext, int layoutResourceId, List<DrawerItem> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

//        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.drawerItemImageView);
        TextView textViewName = (TextView) listItem.findViewById(R.id.drawerItemTextView);

        DrawerItem folder = getItem(position);


//        imageViewIcon.setImageResource(folder.getIcon());
        textViewName.setText(folder.getName());

        return listItem;
    }
}
