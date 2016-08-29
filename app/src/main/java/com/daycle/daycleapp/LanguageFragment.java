package com.daycle.daycleapp;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.daycle.daycleapp.models.ActionBarModel;
import com.daycle.daycleapp.models.SettingsModel;
import com.daycle.daycleapp.utils.NeoUtil;

import java.util.ArrayList;

public class LanguageFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_language;

    private ArrayList<DefaultArrayAdapterModel> items;

    public LanguageFragment() {

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
        ActionBarModel actionBarModel = new ActionBarModel(getString(R.string.menu_lang));
        actionBarModel.backgroundColorResId = R.color.colorPreference;
        fragmentCallback.setActionBar(actionBarModel);

        items = new ArrayList<>();
        items.add(new DefaultArrayAdapterModel("en", "English"));
        items.add(new DefaultArrayAdapterModel("ko", "한국어"));
//        items.add(new DefaultArrayAdapterModel("zh", "中文"));
//        items.add(new DefaultArrayAdapterModel("es", "Español"));
//        items.add(new DefaultArrayAdapterModel("ar", "العربية"));
//        items.add(new DefaultArrayAdapterModel("pt", "Português"));
//        items.add(new DefaultArrayAdapterModel("ru", "Pусский язык"));
        items.add(new DefaultArrayAdapterModel("ja", "日本語"));
//        items.add(new DefaultArrayAdapterModel("de", "Deutsch"));
//        items.add(new DefaultArrayAdapterModel("vi", "Tiếng Việt"));
//        items.add(new DefaultArrayAdapterModel("fr", "Français"));
//        items.add(new DefaultArrayAdapterModel("it", "Italiano"));
//        items.add(new DefaultArrayAdapterModel("tr", "Türkçe"));
//        items.add(new DefaultArrayAdapterModel("pl", "Polski"));
//        items.add(new DefaultArrayAdapterModel("id", "Indonesia"));
//        items.add(new DefaultArrayAdapterModel("ro", "Română"));
//        items.add(new DefaultArrayAdapterModel("nl", "Nederlands"));
//        items.add(new DefaultArrayAdapterModel("th", "ภาษาไทย"));
//        items.add(new DefaultArrayAdapterModel("hu", "Magyar"));
//        items.add(new DefaultArrayAdapterModel("cs", "Čeština"));
//        items.add(new DefaultArrayAdapterModel("da", "Dansk"));
//        items.add(new DefaultArrayAdapterModel("no", "Norsk"));
//        items.add(new DefaultArrayAdapterModel("sk", "Slovenčina"));
//        items.add(new DefaultArrayAdapterModel("fi", "Suomi"));
//        items.add(new DefaultArrayAdapterModel("sv", "Svenska"));
//        items.add(new DefaultArrayAdapterModel("bg", "Български"));
//        items.add(new DefaultArrayAdapterModel("el", "Ελληνική"));


        ArrayAdapter<DefaultArrayAdapterModel> adapter = new CustomRadioListViewAdapter(getContext(), items);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DefaultArrayAdapterModel item = (DefaultArrayAdapterModel)parent.getItemAtPosition(position);
                item.isChecked = true;
                //((RadioButton)view.findViewById(R.id.itemRadio)).setChecked(true);

                // 헌재 로케일 수정
                NeoUtil.setLocaleLanguage(getContext(), item.code);

                // 로케일 세팅값 영구 저장
                SettingsModel settings = App.getSettings(getContext());
                settings.language = item.code;
                App.setSettings(getContext(), settings);

                App.alert(getString(R.string.app_reset_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
            }
        });
        listView.setAdapter(adapter);
        String langCode = NeoUtil.getLocaleLanguage(getContext());
        for (DefaultArrayAdapterModel item : items) {

            if(item.code.equals(langCode)){
                int idx = items.indexOf(item);
                listView.setItemChecked(idx, true);
                break;
            }
        }

        return mainView;
    }
}
