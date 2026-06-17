package com.example.appshopbanhang.ui.adapter;


import com.example.appshopbanhang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HoTroAdapter  extends ArrayAdapter<String> {
    private Context context;
    private List<String> messages;

    public HoTroAdapter(Context context, List<String> messages) {
        super(context, android.R.layout.simple_list_item_1, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(messages.get(position));

        return convertView;
    }
}

