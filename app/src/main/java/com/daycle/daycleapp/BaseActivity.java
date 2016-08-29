package com.daycle.daycleapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.NeoUtil;

public class BaseActivity extends AppCompatActivity {

    private View decorView;
    private int uiOption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 어플리케이션 전역변수에 현재 액티비티 세팅
        App.setCurrentActivity(this);


        NeoUtil.setLocaleLanguage(this, App.settings.language);
        L.d("언어코드: " + App.settings.language);

        decorView = getWindow().getDecorView();
        uiOption = decorView.getSystemUiVisibility();
//        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
//            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
//            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
//            uiOption |=              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//        }

//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;

    }

    protected void requestFullScreen(){

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(decorView != null){
//                    decorView.setSystemUiVisibility(uiOption);
//                }
//            }
//        }, 300);
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            requestFullScreen();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // 토스트 메시지 보여주기
    protected void showToast(String msg){
        App.showToast(msg);
    }
    protected void showToast(int resId){
        App.showToast(resId);
    }

    public interface onKeyBackPressedListener {
        void onBack();
    }
    private onKeyBackPressedListener onKeyBackPressedListener;

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        onKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (onKeyBackPressedListener != null) {
            onKeyBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }

    public interface onActivityResultListener {
        void onResult(int requestCode, int resultCode, Intent data);
    }
    private onActivityResultListener onActivityResultListener;

    public void setOnActivityResultListener(onActivityResultListener listener) {
        onActivityResultListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onActivityResultListener != null) {
            onActivityResultListener.onResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        App.dismissProgressBar();
    }

    @Override
    protected void onStop() {
        super.onStop();

        App.dismissProgressBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.dismissProgressBar();
    }
}

