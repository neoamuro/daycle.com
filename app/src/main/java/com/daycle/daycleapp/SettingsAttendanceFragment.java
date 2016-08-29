package com.daycle.daycleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daycle.daycleapp.adapters.CustomRadioListViewAdapter;
import com.daycle.daycleapp.adapters.DefaultArrayAdapterModel;
import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.models.SettingsModel;

import java.util.ArrayList;

public class SettingsAttendanceFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_settings_attendance;

    private ArrayList<DefaultArrayAdapterModel> items;

    public SettingsAttendanceFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // UI 인스턴스
        ListView listView = (ListView)mainView.findViewById(R.id.listView);

        // 액션바 설정
        fragmentCallback.setActionBar(getString(R.string.menu_attendance_count), false, true);

        items = new ArrayList<>();
        items.add(new DefaultArrayAdapterModel(SettingsModel.AttendanceMode.ALL.name(), getString(R.string.attendance_count_settings1)));
        items.add(new DefaultArrayAdapterModel(SettingsModel.AttendanceMode.ONLY_MONTH.name(), getString(R.string.attendance_count_settings2)));
        items.add(new DefaultArrayAdapterModel(SettingsModel.AttendanceMode.NO_DISPLAY.name(), getString(R.string.attendance_count_settings3)));

        ArrayAdapter<DefaultArrayAdapterModel> adapter = new CustomRadioListViewAdapter(getContext(), items);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DefaultArrayAdapterModel item = (DefaultArrayAdapterModel)parent.getItemAtPosition(position);
                item.isChecked = true;

                // 세팅값 수정
                SettingsModel settings = App.getSettings(getContext());
                settings.attendance_mode = SettingsModel.AttendanceMode.valueOf(item.code);
                App.setSettings(getContext(), settings);
            }
        });
        listView.setAdapter(adapter);

        // 리스트뷰 초기화시 모드 설정 적용
        SettingsModel settings = App.getSettings(getContext());
        int selectedIdx = settings.attendance_mode.getValue();
        listView.setItemChecked(selectedIdx, true);

        return mainView;
    }
}
