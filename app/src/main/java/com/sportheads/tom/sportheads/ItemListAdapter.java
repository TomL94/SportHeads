package com.sportheads.tom.sportheads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Tom on 04/04/2015.
 */
public class ItemListAdapter extends ArrayAdapter<ItemsContent.Item> {

    public ItemListAdapter(Context context, int resource, List<ItemsContent.Item> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
        }

        ((TextView) (convertView.findViewById(R.id.item_title)))
                .setText(ItemsContent.ITEMS.get(position).getmTitle());

        ((TextView) (convertView.findViewById(R.id.item_desc)))
                .setText(ItemsContent.ITEMS.get(position).getmDesc());

        ((TextView) (convertView.findViewById(R.id.item_date)))
                .setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(ItemsContent.ITEMS.get(position).getmPubDate()));

        ImageLoader.getInstance().displayImage(ItemsContent.ITEMS.get(position).getmImageLink(),
                (ImageView) (convertView.findViewById(R.id.item_image)));

        ((TextView) (convertView.findViewById(R.id.item_image_desc)))
                .setText(ItemsContent.ITEMS.get(position).getmImageDesc());

        return convertView;
    }
}
