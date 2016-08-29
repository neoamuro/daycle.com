package com.daycle.daycleapp.custom.swipelistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.daycle.daycleapp.models.AttendanceModel;

import java.util.ArrayList;

/**
 * Created by neoam on 2016-08-19.
 * 출석 완료 아이템 리스트
 */
public class DoneListViewAdapter extends ArrayAdapter<AttendanceModel> {

    private final Context mContext;

    public DoneListViewAdapter(final Context context, ArrayList<AttendanceModel> items) {
        mContext = context;

        for (AttendanceModel item : items) {
            add(item);
        }
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.done_list_row, parent, false);
        }

        AttendanceModel item = getItem(position);

        // 타이틀
        TextView itemTextView = (TextView)view.findViewById(R.id.itemTextView);
        if(itemTextView != null)
            itemTextView.setText(item.title);

        // 출석 카운트
        TextView itemCountTextView = (TextView)view.findViewById(R.id.itemCountTextView);
        if(itemCountTextView != null)
            itemCountTextView.setText(String.valueOf(item.cnt));

        // 출석 날짜 값
        TextView itemDayTextView = (TextView)view.findViewById(R.id.itemDayTextView);
        if(itemDayTextView != null)
            itemDayTextView.setText(String.valueOf(item.day));

        return view;
    }
}
