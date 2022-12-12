package com.example.tykoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class GameAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Food> items;

    public GameAdapter(Context context, ArrayList<Food> items)
    {
        mContext = context;
        this.items = items;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // 리스트뷰에 들어갈 아이템의 xml
        View view = mLayoutInflater.inflate(R.layout.listview_item, null);

        Button btn = (Button) view.findViewById(R.id.serve_list);
        btn.setText(items.get(position).name);

        return view;
    }

    @Override
    public Food getItem(int i) {
        return items.get(i);
    }
    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
}
