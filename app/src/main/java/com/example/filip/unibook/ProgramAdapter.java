package com.example.filip.unibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by filip on 2018-03-14.
 */

public class ProgramAdapter extends BaseAdapter{

    LayoutInflater mInflator;
    String[] items;
    String[] id;

    public ProgramAdapter(Context c, String[] items, String[] id){
        this.items = items;
        this.id = id;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        View view = mInflator.inflate(R.layout.program_layout,null);
        TextView program = (TextView) view.findViewById(R.id.txtProgram);

        String programNamn = items[i];
        program.setText(programNamn);

        return view;
    }
}
