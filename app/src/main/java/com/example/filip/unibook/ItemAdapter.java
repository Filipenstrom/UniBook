package com.example.filip.unibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.filip.unibook.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    List<String> items;
    List<String> prices;

    public ItemAdapter(Context c, List<String> i, List<String> p){
        items = i;
        prices = p;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.my_listview_ad, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.txtAdTitle);
        TextView priceTextView = (TextView) v.findViewById(R.id.txtAdPris);

        String name = items.get(i);
        String cost = prices.get(i);

        nameTextView.setText(name);
        priceTextView.setText(cost);

        return v;
    }
}
