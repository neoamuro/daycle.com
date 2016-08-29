package com.daycle.daycleapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

/**
 * Created by neoam on 2016-08-24.
 */
public class CustomRadioListViewAdapter extends ArrayAdapter<DefaultArrayAdapterModel> {
    /** Global declaration of variables. As there scope lies in whole class. */
    private Context context;
    private ArrayList<DefaultArrayAdapterModel> items;

    /** Constructor Class */
    public CustomRadioListViewAdapter(Context context, ArrayList<DefaultArrayAdapterModel> items) {
        //super(context, R.layout.custom_radio_listview, items);
        super(context, android.R.layout.simple_list_item_single_choice, items);
        this.context = context;
        this.items = items;
    }

    /** Implement getView method for customizing row of list view. */
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        // Creating a view of row.
        //View rowView = inflater.inflate(R.layout.custom_radio_listview, parent, false);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);

        // 데이터 아이템
        final DefaultArrayAdapterModel item = getItem(position);

        CheckedTextView radio = (CheckedTextView)rowView;
        radio.setText(item.title);

        // 데이터 아이템
//        final DefaultArrayAdapterModel item = getItem(position);
//
//        // UI 인스턴스
//        TextView itemTitleText = (TextView)rowView.findViewById(R.id.itemTitleText);
//        RadioButton radioButton = (RadioButton) rowView.findViewById(R.id.itemRadio);
//
//        // 텍스트 데이터 바인딩
//        itemTitleText.setText(item.title);
//
//        // 라디오 데이터 바인딩
//        radioButton.setChecked(item.isChecked);
//        radioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                item.isChecked = true;
//            }
//        });

        return rowView;
    }
}
