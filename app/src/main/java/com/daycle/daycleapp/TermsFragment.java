package com.daycle.daycleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daycle.daycleapp.models.ActionBarModel;

public class TermsFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_terms;

    public TermsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // 액션바 설정
        ActionBarModel actionBarModel = new ActionBarModel(getString(R.string.menu_terms));
        actionBarModel.backgroundColorResId = R.color.colorPreference;
        fragmentCallback.setActionBar(actionBarModel);

        return mainView;
    }
}
