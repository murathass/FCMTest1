package com.biotek.mobil.fcmtest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context c;
    private List<MMessage> list;
    private LayoutInflater inflater;

    public CustomAdapter(List<MMessage> list, Context c){
        this.list = list;
        this.c = c;
        this.inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = inflater.inflate(R.layout.custom_list_item,null);

        MMessage message = list.get(position);

        TextView author  = v.findViewById(R.id.author);
        TextView time = v.findViewById(R.id.time);
        TextView content = v.findViewById(R.id.content);

        author.setText(message.getAuthor());
        time.setText(message.getDate());
        content.setText(message.getContent());

        return v;

    }
}
