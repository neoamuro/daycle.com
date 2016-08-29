package com.daycle.daycleapp.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.custom.swipelistview.ArrayAdapter;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoAdapter;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.models.SettingsModel;

/**
 * Created by neoam on 2016-08-19.
 * 출석 아이템 리스트
 */
public class AttendanceListViewAdapter extends ArrayAdapter<AttendanceModel> implements UndoAdapter {

    private final Context mContext;
    private SettingsModel.AttendanceMode attendanceMode;

    public AttendanceListViewAdapter(final Context context, ArrayList<AttendanceModel> items, SettingsModel.AttendanceMode attendanceMode) {
        mContext = context;
        this.attendanceMode = attendanceMode;

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
            view = LayoutInflater.from(mContext).inflate(R.layout.attendance_list_row, parent, false);
        }

        AttendanceModel item = getItem(position);

        // 타이틀
        TextView itemTextView = (TextView)view.findViewById(R.id.itemTextView);
        if(itemTextView != null)
            itemTextView.setText(item.title);

        // 출석 카운트 보여주는 조건
        // 디스플레이 모드가 not 이 아니면 보여줌
        if(attendanceMode != SettingsModel.AttendanceMode.NO_DISPLAY){
            TextView itemCountTextView = (TextView)view.findViewById(R.id.itemCountTextView);
            if(itemCountTextView != null)
                itemCountTextView.setText(String.valueOf(item.cnt));
        }

        return view;
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

}
