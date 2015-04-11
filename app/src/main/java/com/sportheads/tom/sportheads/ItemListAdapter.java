package com.sportheads.tom.sportheads;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        ((TextView) (convertView.findViewById(R.id.item_card).findViewById(R.id.item_title)))
                .setText(ItemsContent.ITEMS.get(position).getmTitle());

        ((TextView) (convertView.findViewById(R.id.item_card).findViewById(R.id.item_desc)))
                .setText(ItemsContent.ITEMS.get(position).getmDesc());

        return convertView;
    }
}
