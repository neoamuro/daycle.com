package com.daycle.daycleapp.applications;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.daycle.daycleapp.DBHelper;
import com.daycle.daycleapp.R;
import com.daycle.daycleapp.custom.CustomProgressDialog;
import com.daycle.daycleapp.models.SettingsModel;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.NeoUtil;
import com.daycle.daycleapp.utils.NetworkUtil;
import com.google.gson.Gson;

public class App extends Application {

    public interface AppCallback{
        void defaultCallback();
    }

    public static boolean isLogging = false; // 로깅 여부
    public static final String TAG = "DAYCLE_LOG";  // 로그 태그
    private static DBHelper db; // 디비 헬퍼
    private static Activity currentActivity; // 현재 액티비티 저장
    private static android.support.v4.app.Fragment currentFragment; // 현재 프래그먼트 저장
    private static AppStatus appStatus; // 현재 어플리케이션 상태 정보
    private static CustomProgressDialog loading;
    private static int deviceId = 0; // 디바이스 고유 아이디
    private static FragmentTag lastFragmentName;
    public static SettingsModel settings;
    public static int loadingCnt = 0;

    public App() {
    }

    // 어플 실행시 초기에 한번만 실행됨
    @Override
    public void onCreate() {
        super.onCreate();

        // 로깅 체크
        isLogging = ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) != 0; // 디버그 모드면 로깅

        // 어플리케이션 상태 초기화
        appStatus = new AppStatus();

        // 네트워크 연결 체크
        appStatus.isNetworkEnabled = NetworkUtil.CheckNetwork(this);

        // 디비 초기화
        db = new DBHelper(this);
        appStatus.isFirst = db.isFirst(this); // 처음 접속인지 체크

        // 출석 카운트 기본 설정값 저장
        SharedPreferences storage = getSharedPreferences("daycle", App.MODE_PRIVATE);
        String attendanceMode = storage.getString("attendance_mode", null);

        settings = getSettings(this);
        if(settings == null){
            settings = new SettingsModel(SettingsModel.AttendanceMode.ALL, NeoUtil.getLocaleLanguage(this));
            setSettings(this, settings);
        }

        // 저장된 세팅값대로 로케일 설정 변경
        NeoUtil.setLocaleLanguage(this, settings.language);

    }

    // 디바이스 설정 변경시 호출
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        L.d("언어코드 변경됨");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            settings.language = getResources().getConfiguration().getLocales().get(0).getLanguage();
//        } else {
//            settings.language = getResources().getConfiguration().locale.getLanguage();
//        }
//
//        setSettings(this, settings);
    }

    @Override
    public void onTerminate() {
        L.d("onTerminate");
        super.onTerminate();
    }

    // 세팅값 가져오기
    public static SettingsModel getSettings(Context context){
        SharedPreferences storage = context.getSharedPreferences("daycle", App.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = storage.getString(context.getString(R.string.localstorage_settings_key), null);
        if(json == null)
            return null;

        return gson.fromJson(json, SettingsModel.class);
    }

    // 세팅값 저장하기
    public static void setSettings(Context context, SettingsModel settings){
        SharedPreferences storage = context.getSharedPreferences("daycle", App.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();
        Gson gson = new Gson();
        String json = gson.toJson(settings);
        editor.putString(context.getString(R.string.localstorage_settings_key), json);
        editor.apply();

        App.settings = settings;
    }

    // 디비 객체 가져오는 get 메서드
    public static DBHelper getDataBase(){
        return db;
    }

    // 어플리케이션 상태 가져오는 get 메서드
    public static AppStatus getApplicationStatus(){
        return appStatus;
    }

    // 프로그레스바 보여주기
    public static void showProgressBar(){
        if(currentActivity != null && currentActivity.isFinishing())
            return;

        if(loading != null){
            loadingCnt++;
            loading.show();
        }
    }

    // 프로그레스바 감추기
    public static void hideProgressBar(){

        if(currentActivity.isFinishing()){
            loadingCnt = 0;
            return;
        }

        // 0.5 초 정도는 딜레이 줘서 갑자기 나타났다 사라지는거 방지
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loading != null && loading.isShowing())
                    if(--loadingCnt <= 0){
                        loadingCnt = 0;
                        loading.hide();
                    }
            }}, 700);
    }

    // 프로그레스바 다이알로그 디스미스
    public static void dismissProgressBar(){

        if(loading != null && loading.isShowing()){
            loadingCnt = 0;
            loading.dismiss();
        }
    }

    // 토스트 보여주기
    public static void showToast(String message){
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(int resid){
        Toast.makeText(currentActivity, resid, Toast.LENGTH_SHORT).show();
    }

    public static android.support.v4.app.Fragment getCurrentFragment() {
        return currentFragment;
    }

    // 프래그먼트 저장
    public static void setCurrentFragment(android.support.v4.app.Fragment fragment) {

        currentFragment = fragment;
    }

    public static FragmentTag getLastFragmentName(){
        return lastFragmentName;
    }

    // 현재 액티비티 가져오기
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity activity)
    {
        currentActivity = activity;

        loading = new CustomProgressDialog(activity);
        loadingCnt = 0;

        // 뺑글뱅이 도는 지역의 배경 없앰
        loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    // fragment 시작
    public static void startFragment(FragmentManager manager, android.support.v4.app.Fragment fragment, Bundle bundle, boolean isAnimation, String tag){

        if(bundle != null){
            fragment.setArguments(bundle);
        }

        if(manager == null)
            return;

        FragmentTransaction transaction = manager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment, tag);

        //manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // 백스택에 삽입
        //transaction.addToBackStack(tag);

        if(isAnimation)
            transaction = transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

        //transaction.commit();
        transaction.commitAllowingStateLoss();

        loadingCnt = 0;

        currentFragment = fragment;
        if(tag != null)
            lastFragmentName = Enum.valueOf(FragmentTag.class, tag);

    }

    public static void startFragment(FragmentManager manager, android.support.v4.app.Fragment fragment, String tag){
        startFragment(manager, fragment, null, true, tag);
    }

    public static void startFragment(FragmentManager manager, android.support.v4.app.Fragment fragment, Bundle bundle, String tag){
        startFragment(manager, fragment, bundle, true, tag);
    }

    public static void startFragment(FragmentManager manager, android.support.v4.app.Fragment fragment, boolean isAnimation, String tag){
        startFragment(manager, fragment, null, isAnimation , tag);
    }

    public static void startFragment(FragmentManager manager, android.support.v4.app.Fragment fragment, Bundle bundle, boolean isAnimation){
        startFragment(manager, fragment, bundle, isAnimation);
    }

    // 매니페스트 값 가져오기
    public String getMetadataFromManifest(Context context, String key)
    {
        String val = null;
        try {
            String packageName = context.getPackageName();
            L.d("getPackageName: " + packageName);
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(((Activity)context).getComponentName(), PackageManager.GET_META_DATA);
            Bundle bundle = activityInfo.metaData; if(bundle != null) {
                val = bundle.getString(key);
            }
        } catch (Exception ex) {
            L.d("Meta data 값 취득 실패: " + ex.getMessage());
        }
        return val;
    }

    // alert
    public static void alert(String msg, DialogInterface.OnClickListener positiveListener) {
        showDialog(currentActivity.getString(R.string.dialog_default_title_string), msg, positiveListener, null);
    }

    // confirm
    public static void confirm(String msg, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        showDialog(currentActivity.getString(R.string.dialog_default_title_string), msg, positiveListener, negativeListener);
    }

    // 타이틀이 있는 컨펌
    public static void showDialog(String title, String msg, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
                .setTitle(currentActivity.getString(R.string.dialog_default_title_string))
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)

                // Yes 버튼
                .setPositiveButton(currentActivity.getString(R.string.dialog_button_yes_string), positiveListener);

        if(negativeListener != null)
            builder.setNegativeButton(currentActivity.getString(R.string.dialog_button_no_string), negativeListener);

        builder.show();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
