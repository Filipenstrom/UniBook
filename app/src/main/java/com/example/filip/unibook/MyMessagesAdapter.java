package com.example.filip.unibook;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.sql.Blob;
import java.util.Date;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class MyMessagesAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] chatids;
    Blob[] blobs;
    Context context;


    public MyMessagesAdapter(Context c, String[] items, String[] chatids, Blob[] blobs) {
        this.items = items;
        this.chatids = chatids;
        if(blobs != null) {
            this.blobs = blobs;
        }
        this.context = c;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.mymessages_listview, null);
        TextView nameTxt = v.findViewById(R.id.txtUserTalkingTo);
        TextView chatId = v.findViewById(R.id.txtChatId);

        String name = items[i];
        String ids = chatids[i];

        nameTxt.setText(name);
        chatId.setText(ids);


        return v;

        //adsPic.setImageBitmap(BitmapFactory.decodeByteArray(pics, 0, pics.length));
    }
}
