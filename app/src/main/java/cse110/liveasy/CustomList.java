package cse110.liveasy;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;



/**
 * Class used by the list view home page to set up each list item view
 */
public class CustomList extends ArrayAdapter<String>{

    private final String[] URLs;
    private final String[] numbers;
    private final String[] emails;
    private final Activity context;
    private final String[] usr;

    public CustomList(Activity context, String[] usr, String[] URLs, String[] numbers, String[] emails) {
        super(context, R.layout.fragment_home5more, usr);
        this.context = context;
        this.usr = usr;
        this.URLs = URLs;
        this.emails = emails;
        this.numbers = numbers;

    }

    // override to get the view to return a picture of the person and their respective name
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_content, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.usr);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(usr[position]);

        Picasso.with(context)
                .load(URLs[position])
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(imageView);


        return rowView;
    }

}