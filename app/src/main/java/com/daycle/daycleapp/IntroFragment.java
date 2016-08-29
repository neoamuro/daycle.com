package com.daycle.daycleapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;

public class IntroFragment extends Fragment implements BaseActivity.onKeyBackPressedListener {

    FragmentCallback fragmentCallback;

    public interface FragmentCallback {
        public void setActionBarVisibility(boolean isVisible);
    }

    // 인트로 닫히는 딜레이
    final int DELAY_TIME = 2000;

    public IntroFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 몇초후에 자동 닫힘
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // 메인 프레그먼트 실행
                App.startFragment(getFragmentManager(), new AttendanceFragment(), FragmentTag.ATTENDANCE.name());

                if(getActivity() == null){
                    return;
                }

                // 인트로 프래그먼트 제거
                //getActivity().getSupportFragmentManager().beginTransaction().remove(IntroFragment.this).commit();
            }}, DELAY_TIME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_intro, container, false);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnKeyBackPressedListener(this);
        fragmentCallback = (FragmentCallback) context;
    }

    @Override
    public void onBack() {

    }
}
