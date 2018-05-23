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

import java.util.Date;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class MessageAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] id;
    Date[] date;
    String[] userids;
    Resources res;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public MessageAdapter(Context c, String[] items, String[] id, Date[] date, String[] userids){
        this.items = items;
        this.id = id;
        res = c.getResources();
        if(date != null) {
            this.date = date;
            this.userids = userids;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.message_list_item, null);
        TextView dateTxt = v.findViewById(R.id.txtMessageTime);
        TextView dateTxtSender = v.findViewById(R.id.txtMessageTimeSender);
        TextView  nameTxtSender = v.findViewById(R.id.txtMessageUserSender);
        TextView nameTxt = v.findViewById(R.id.txtMessageUser);

        String message = items[i];
        String name = id[i];

        RelativeLayout relativeLayoutsender = v.findViewById(R.id.message_content_sender);
        RelativeLayout relativeLayout = v.findViewById(R.id.message_content);
        TextView messageTxt;

        if(userids != null) {
            String Uid = userids[i];
            String[] dateTextSplit = date[i].toString().split(" ");
            String dateText = dateTextSplit[3];

            if (Uid.equals(user.getUid().toString())) {
                Drawable drawable = res.getDrawable(R.drawable.bubblesenderorange);
                relativeLayoutsender.setBackground(drawable);
                relativeLayout.setVisibility(View.INVISIBLE);
                nameTxt.setVisibility(View.INVISIBLE);
                dateTxt.setVisibility(View.INVISIBLE);
                messageTxt = v.findViewById(R.id.txtMessageTextSender);
                messageTxt.setText(message);
                nameTxtSender.setText(name);
                if(i == getCount()-1) {
                    dateTxtSender.setText("Skickat: " + dateText);
                }

            } else {
                Drawable drawable = res.getDrawable(R.drawable.bubble);
                relativeLayout.setBackground(drawable);
                relativeLayoutsender.setVisibility(View.INVISIBLE);
                nameTxtSender.setVisibility(View.INVISIBLE);
                dateTxtSender.setVisibility(View.INVISIBLE);
                messageTxt = v.findViewById(R.id.txtMessageText);
                messageTxt.setText(message);
                nameTxt.setText(name);
                if(i == getCount()-1) {
                    dateTxt.setText("Skickat: " + dateText);
                }
            }
        }
        else{
            messageTxt = v.findViewById(R.id.txtMessageText);
            nameTxt = v.findViewById(R.id.txtMessageUser);
            nameTxt.setText(name);
            messageTxt.setText(message);
        }

        return v;
    }
}