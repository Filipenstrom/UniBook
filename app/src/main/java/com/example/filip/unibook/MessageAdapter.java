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

public class MessageAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] id;
    String[] date;

    public MessageAdapter(Context c, String[] items, String[] id, String[] date){
        this.items = items;
        this.id = id;
        if(date != null) {
            this.date = date;
        }
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
        View v = mInflator.inflate(R.layout.message_list_item, null);
        TextView messageTxt = v.findViewById(R.id.txtMessageText);
        TextView nameTxt = v.findViewById(R.id.txtMessageUser);
        TextView dateTxt = v.findViewById(R.id.txtMessageTime);
        //ImageView adsPic = (ImageView) v.findViewById(R.id.ivAdsListPicture);


        String message = items[i];
        String name = id[i];

            messageTxt.setText(message);
            nameTxt.setText(name);
            if(date != null) {
                String dateText = date[i];
                dateTxt.setText(dateText);
            }

            return v;

        //adsPic.setImageBitmap(BitmapFactory.decodeByteArray(pics, 0, pics.length));
    }
}

