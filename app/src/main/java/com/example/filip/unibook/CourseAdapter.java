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

public class CourseAdapter extends BaseAdapter{

    LayoutInflater mInflator;
    String[] items;
    String[] ids;
    String[] courseCodes;


    public CourseAdapter(Context c,  String[] items, String[] ids, String[] courseCodes){
        this.items = items;
        this.ids = ids;
        this.courseCodes = courseCodes;
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
        View view = mInflator.inflate(R.layout.courses,null);
        TextView courseName = (TextView) view.findViewById(R.id.textViewCourses);

        String kursNamn = items[i];
        courseName.setText(kursNamn);
        return view;
    }
}
