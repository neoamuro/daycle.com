package com.daycle.daycleapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class UIHelper {

    public enum ScaleMode{
        SMALLER, BIGGER
    }

    public static int getTotalHeightofListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        return totalHeight  + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
    }

    // 키보드 가리기 (메인 레이아웃 바인딩 시키면 됨)
    public static void hideKeyboard(final Context context, final View view) {
        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardFromView(context, view);
                return false;
            }
        });
    }

    public static void hideKeyboardFromView(Context context, View view){
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // 레이아웃 뷰의 칠드런 가져오기
    public static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

//            ArrayList<View> viewArrayList = new ArrayList<View>();
//            viewArrayList.add(v);
//            viewArrayList.addAll(getAllChildren(child));
//
//            result.addAll(viewArrayList);
            result.add(child);
        }
        return result;
    }

    // 픽셀로 변환해 주는 유틸
    public static int changeToPixel(Context context, int dip){
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return  px;
    }

    // 스크린 사이즈 구하기
    public static int[] getDeviceScreenSize(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        return new int[]{width, height};
    }
//
//    // 스케일 애니메이션 리턴
//    public static Animation getScaleAnimation(Context context, ScaleMode mode, final AnimationCallback callback){
//        Animation animation = AnimationUtils.loadAnimation(context, mode == ScaleMode.SMALLER ? R.anim.smaller_view : R.anim.bigger_view);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if(callback != null){
//                    callback.AnimationEnd(animation);
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//        });
//
//        animation.setFillAfter(mode == ScaleMode.SMALLER);
//
//        return animation;
//    }

    // 애니메이션 리스너에서 End만 쓰고 싶은데 콜백이 너무 지저분해서 따로 구현
    public static Animation loadAnimation(Context context, int resid, final AnimationCallback callback){
        Animation animation = AnimationUtils.loadAnimation(context, resid);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                if(callback != null)
                    callback.AnimationEnd(animation);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        return animation;
    }

    public static void setEnabledEditText(EditText editText, boolean able) {
        editText.setClickable(able);
        editText.setEnabled(able);
        editText.setFocusable(able);
        editText.setFocusableInTouchMode(able);
    }

    public static void pauseWebview(final WebChromeClient client, final WebView webView, final Context context){

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // 앱이 Stop 됐을때 동영상 정지시키는 코드 (onPause로 안됨)
                ((AudioManager)context.getSystemService(
                        Context.AUDIO_SERVICE)).requestAudioFocus(
                        new AudioManager.OnAudioFocusChangeListener() {
                            @Override
                            public void onAudioFocusChange(int focusChange) {}
                        }, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if(client != null)
                    client.onHideCustomView();

                webView.onPause();
            }
        });
    }

    public static void pauseWebview(WebView webView, Context context){
        pauseWebview(null, webView, context);
    }

    public static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
}
