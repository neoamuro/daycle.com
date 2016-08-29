package com.daycle.daycleapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;

/**
 * Created by neoam on 2016-08-24.
 * 프래그먼트 베이스 클래스
 */
public class BaseFragment extends Fragment implements BaseActivity.onKeyBackPressedListener, BaseActivity.onActivityResultListener {

    FragmentCallback fragmentCallback;
    View mainView;

    public interface FragmentCallback {
        void setActionBarVisibility(boolean isVisible);
        void setFabVisibility(boolean isVisible);
        void setActionBar(String title, boolean showAddButton, boolean showHomeButton);
        DrawerLayout getDrawer();
        void unCheckNavMenu();
    }

    // 기본 레이아웃 세팅
    public void setLayout(LayoutInflater inflater, ViewGroup container, int resid){
        mainView = inflater.inflate(resid, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnKeyBackPressedListener(this);
        ((MainActivity) context).setOnActivityResultListener(this);
        fragmentCallback = (FragmentCallback) context;
    }

    @Override
    public void onBack() {
        fragmentCallback.unCheckNavMenu();

//        if(getFragmentManager() == null){
//
//            App.startFragment(getFragmentManager(), new AttendanceFragment(), FragmentTag.ATTENDANCE.name());
//            return;
//        }
//        // 백스택이 0개이면 새로 생성
//        if(getFragmentManager().getBackStackEntryCount() == 0){
//            App.startFragment(getFragmentManager(), new AttendanceFragment(), FragmentTag.ATTENDANCE.name());
//            return;
//        }
//
//        // 백스택이 있으면 꺼내기
//        getFragmentManager().popBackStack();

        App.startFragment(getFragmentManager(), new AttendanceFragment(), FragmentTag.ATTENDANCE.name());
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {

    }
}
