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
import java.util.ArrayList;

/**
 * Created by neoam on 2016-08-24.
 */
public class CustomNavMenuListViewAdapter extends ArrayAdapter<NavMenuArrayAdapterModel> {

    private Context context;
    private ArrayList<NavMenuArrayAdapterModel> items;

    public CustomNavMenuListViewAdapter(Context context, ArrayList<NavMenuArrayAdapterModel> items) {
        super(context, R.layout.custom_nav_menu_list, items);
        this.context = context;
        this.items = items;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View rowView = inflater.inflate(R.layout.custom_nav_menu_list, parent, false);

        // 데이터 아이템
        final NavMenuArrayAdapterModel item = getItem(position);

        // UI 인스턴스
        ImageView itemIconImageView = (ImageView)rowView.findViewById(R.id.itemIconImageView);
        TextView itemTitleTextView = (TextView)rowView.findViewById(R.id.itemTitleTextView);
        View divider = rowView.findViewById(R.id.divider);

        // 이미지 세팅
        if(item.icon == 0){
            // 아이콘이 없으면 아이콘 안보이게...
            itemIconImageView.setVisibility(View.GONE);
        }else{
            itemIconImageView.setImageResource(item.icon);
            itemIconImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }

        // 텍스트 데이터 바인딩
        itemTitleTextView.setText(item.title);

        // 첫번째 아이템만 구분선 주기
        if(position == 0){
            divider.setVisibility(View.VISIBLE);
        }


        // 라디오 데이터 바인딩
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
