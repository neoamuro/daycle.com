package com.daycle.daycleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_about;

    public AboutFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // 액션바 설정
        fragmentCallback.setActionBar(getString(R.string.menu_about), false, true);

        return mainView;
    }
}
