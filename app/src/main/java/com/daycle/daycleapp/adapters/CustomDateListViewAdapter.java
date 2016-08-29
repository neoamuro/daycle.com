package com.daycle.daycleapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daycle.daycleapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by neoam on 2016-08-24.
 * 날짜와 같이 있는 UI를 구현하기 위한 어댑터
 */
public class CustomDateListViewAdapter extends ArrayAdapter<DefaultDateArrayAdapterModel> {

    private Context context;
    private ArrayList<DefaultDateArrayAdapterModel> items;

    public CustomDateListViewAdapter(Context context, ArrayList<DefaultDateArrayAdapterModel> items) {
        super(context, R.layout.custom_listview_item_date, items);
        this.context = context;
        this.items = items;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View rowView = inflater.inflate(R.layout.custom_listview_item_date, parent, false);

        // 데이터 아이템
        final DefaultDateArrayAdapterModel item = getItem(position);

        // UI 인스턴스
        TextView itemTitleTextView = (TextView)rowView.findViewById(R.id.itemTitleTextView);
        TextView itemDateTextView = (TextView)rowView.findViewById(R.id.itemDateTextView);

        // 텍스트 데이터 바인딩
        itemTitleTextView.setText(item.title);
        if(item.date != null){
            itemDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(item.date));
        }

        return rowView;
    }
}
