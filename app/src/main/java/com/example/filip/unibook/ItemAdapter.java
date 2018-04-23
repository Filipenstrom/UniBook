package com.example.filip.unibook;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filip.unibook.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] prices;
    String[] id;
    List<byte[]> bytes;

    public ItemAdapter(Context c, String[] i, String[] p, List<byte[]> b, String[] id){
        items = i;
        prices = p;
        bytes = b;
        this.id = id;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //Fyller listview med data om annonser, körs en gång för varje listitem.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.my_listview_ad, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.txtAdTitle);
        TextView priceTextView = (TextView) v.findViewById(R.id.txtAdPris);
        TextView idsTextView = (TextView) v.findViewById(R.id.txtAdID);
        //ImageView adsPic = (ImageView) v.findViewById(R.id.ivAdsListPicture);

        String name = items[i];
        String cost = prices[i];
        //byte[] pics = bytes.get(i);
        String adid = id[i];

        nameTextView.setText(name);
        priceTextView.setText(cost + ":-");
        idsTextView.setText(adid);

        //adsPic.setImageBitmap(BitmapFactory.decodeByteArray(pics, 0, pics.length));

        return v;
    }
}
