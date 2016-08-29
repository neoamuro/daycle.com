package com.daycle.daycleapp;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daycle.daycleapp.adapters.CustomNavMenuListViewAdapter;
import com.daycle.daycleapp.adapters.NavMenuArrayAdapterModel;
import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;
import com.daycle.daycleapp.models.ActionBarModel;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.NeoUtil;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        IntroFragment.FragmentCallback,
        BaseFragment.FragmentCallback{

    ActionBar ab; // 액션바
    AppBarLayout abl; // 앱바 레이아웃
    FloatingActionButton fab; // 플로팅 액션 바
    Toolbar toolbar;
    TextView titleTextView; // 액션바 타이틀 뷰
    boolean showAddButton;
    Menu menu;
    NavigationView navigationView;
    DrawerLayout drawer;
    ListView navMenuListView;



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
      //  super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        L.d("MainActivity onCreate");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 액션바
        ab = getSupportActionBar();

        // 액션바 레이아웃
        abl = (AppBarLayout)findViewById(R.id.abl);

        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setCustomView(getLayoutInflater().inflate(R.layout.custom_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        // 플로팅 액션 버튼 초기화
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 네비게이션 드로워 초기화
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navMenuListView = (ListView)findViewById(R.id.navMenuListView);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setNavigationItemSelectedListener(this);


        navMenuListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header, null, false);
        navMenuListView.addHeaderView(headerView, null, false);

        setNavigationDrawerMenuItem();


        navMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                NavMenuArrayAdapterModel item = (NavMenuArrayAdapterModel)adapterView.getItemAtPosition(i);
                if(item == null)
                    return;

                String menuCode = item.code;
                if(menuCode.equals("nav_done")){
                    App.startFragment(getSupportFragmentManager(), new DoneFragment(), FragmentTag.DONE.name());
                }else if(menuCode.equals("nav_about")){
                    App.startFragment(getSupportFragmentManager(), new AboutFragment(), FragmentTag.ABOUT.name());
                }else if(menuCode.equals("nav_lang")){
                    App.startFragment(getSupportFragmentManager(), new LanguageFragment(), FragmentTag.LANGUAGE.name());
                }else if(menuCode.equals("nav_settings_attendance")){
                    App.startFragment(getSupportFragmentManager(), new SettingsAttendanceFragment(), FragmentTag.ATTENDANCE_SETTINGS.name());
                }else if(menuCode.equals("nav_tutorial")){
                    App.startFragment(getSupportFragmentManager(), new TutorialFragment(), FragmentTag.TUTORIAL.name());
                }
                else if(menuCode.equals("nav_terms")){
                    App.startFragment(getSupportFragmentManager(), new TermsFragment(), FragmentTag.TERMS.name());
                }
                else if(menuCode.equals("nav_backup")){
                    App.startFragment(getSupportFragmentManager(), new BackupFragment(), FragmentTag.BACKUP.name());
                }

                drawer.closeDrawer(GravityCompat.START);
            }
        });


        final View footerView = getLayoutInflater().inflate(R.layout.nav_footer, null);
        navMenuListView.addFooterView(footerView, null, false);

        // 버전 표시
        TextView versionTextView = (TextView)navigationView.findViewById(R.id.versionTextView);
        versionTextView.setText("v " + NeoUtil.getVersion(this));

        navMenuListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
//                int listViewHeight = navMenuListView.getHeight();
//                L.d("listViewHeight: " + listViewHeight);
//                ViewGroup.LayoutParams layoutParams = footerView.getLayoutParams();
//                layoutParams.height = 1000;
//                footerView.setLayoutParams(layoutParams);
            }
        });

        // listview의 총 높이 (헤더뷰 포함)
//        int navMenuListViewHeight = UIHelper.getTotalHeightofListView(navMenuListView);
//        int screenH = UIHelper.getDeviceScreenSize(this)[1];
//        int paddingH = screenH - (navMenuListViewHeight + headerView.getMeasuredHeight() + UIHelper.changeToPixel(this, (int)(getResources().getDimension(R.dimen.nav_menu_bottom_padding)))); // 스크린 높이에서 리스트뷰 높이를 뺀다.
//        int paddingTop = Math.max(0, paddingH); // 그러면 footer에 부여할 패딩값이 나온다.
//        footerView.setPadding(0, paddingTop, 0, 0);

        // 시작 프래그먼트 설정
        android.support.v4.app.Fragment fragment = null;
        String tag = null;

        // 데이터 복구
        if(savedInstanceState != null){

            // 프래그먼트 복구
            tag = savedInstanceState.getString("fragment_tag");
            if(tag != null){
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
            }

            // 액션바 보임 유무 복구
            boolean actionbarVisibility = savedInstanceState.getBoolean("actionbar_visibility");
            setActionBarVisibility(actionbarVisibility);
        }

        if(fragment == null){
            fragment = new IntroFragment();
            FragmentTag.INTRO.name();
            L.d("FragmentTag.INTRO.name(): " + FragmentTag.INTRO.name());
        }













        // 인트로 프래그먼트
        App.startFragment(getSupportFragmentManager(), fragment, false, tag);

        requestFullScreen();
    }

    private void setNavigationDrawerMenuItem(){
        ArrayList<NavMenuArrayAdapterModel> menuItems = new ArrayList<>();
        menuItems.add(new NavMenuArrayAdapterModel("nav_done", getString(R.string.menu_done), R.drawable.ic_menu_done));
        menuItems.add(new NavMenuArrayAdapterModel("nav_lang", getString(R.string.menu_lang)));
        menuItems.add(new NavMenuArrayAdapterModel("nav_settings_attendance", getString(R.string.menu_attendance_count)));
        menuItems.add(new NavMenuArrayAdapterModel("nav_about", getString(R.string.menu_about)));
        menuItems.add(new NavMenuArrayAdapterModel("nav_tutorial", getString(R.string.menu_tutorial)));
//        menuItems.add(new NavMenuArrayAdapterModel("nav_terms", getString(R.string.menu_terms)));
        menuItems.add(new NavMenuArrayAdapterModel("nav_backup", getString(R.string.menu_backup)));
        CustomNavMenuListViewAdapter adapter = new CustomNavMenuListViewAdapter(this, menuItems);
        navMenuListView.setAdapter(adapter);
    }

    private void setActionBarTitle(String title){
        if(abl == null)
            return;

        TextView titleTextView = (TextView)abl.findViewById(R.id.actionbarTitle);
        if(titleTextView == null){
            return;
        }

        titleTextView.setText(title);
    }

    private void setVisibleAddButton(){
        if(menu != null){
            MenuItem item =  menu.findItem(R.id.action_add);
            item.setVisible(showAddButton);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);

        setVisibleAddButton();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_add) {
            AttendanceFragment fragment = (AttendanceFragment)App.getCurrentFragment();
            fragment.showInput(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setActionBarVisibility(boolean isVisible) {
        if(abl == null){
            return;
        }

        abl.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        ViewGroup.LayoutParams params = abl.getLayoutParams();
        params.height = isVisible ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
        abl.setLayoutParams(params);
    }

    @Override
    public void setFabVisibility(boolean isVisible) {
        if(fab == null)
            return;

        fab.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setActionBar(ActionBarModel actionBarModel) {
        if(ab == null || toolbar == null)
            return;

        setActionBarVisibility(true);
        setActionBarTitle(actionBarModel.title);

        // 아이템 등록 메뉴 버튼 보임 유무
        this.showAddButton = showAddButton;
        setVisibleAddButton();

        ab.setDisplayHomeAsUpEnabled(actionBarModel.showHomeButton);
        if(actionBarModel.showHomeButton){

            if(actionBarModel.customHomeButton)
                ab.setHomeAsUpIndicator(R.drawable.ic_close);
            else{
                ab.setHomeAsUpIndicator(null);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }else{
            if(drawer != null){
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
            }
        }

        // 백그라운드 컬러 아이디
        int colorId = actionBarModel.backgroundColorResId;
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, colorId == 0 ? R.color.colorAccent : colorId)));
    }

    @Override
    public DrawerLayout getDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        return drawer;
    }


    @Override
    public void unCheckNavMenu() {

//        if(navigationView != null){
//            int size = navigationView.getMenu().size();
//            for (int i = 0; i < size; i++) {
//                navigationView.getMenu().getItem(i).setChecked(false);
//            }
//        }

        // 위에서 언체크가 됐어도 setCheckedItem으로 다른걸 지정하지 않으면 state가 살아 있어서
        // 가로,세로 모드로 변경시 복구되므로 보이지 않는 더미 아이템을 하나 넣어서 set 시킴
        //navigationView.setCheckedItem(R.id.nav_dummy);

        if(navMenuListView != null){
            navMenuListView.setItemChecked(-1, true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_done){
            App.startFragment(getSupportFragmentManager(), new DoneFragment(), FragmentTag.DONE.name());
        }else if(id == R.id.nav_about){
            App.startFragment(getSupportFragmentManager(), new AboutFragment(), FragmentTag.ABOUT.name());
        }else if(id == R.id.nav_lang){
            App.startFragment(getSupportFragmentManager(), new LanguageFragment(), FragmentTag.LANGUAGE.name());
        }else if(id == R.id.nav_settings_attendance){
            App.startFragment(getSupportFragmentManager(), new SettingsAttendanceFragment(), FragmentTag.ATTENDANCE_SETTINGS.name());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        L.d("onSaveInstanceState");

        // 현재 프래그먼트 저장
        android.support.v4.app.Fragment fragment = App.getCurrentFragment();
        if(fragment != null) {
            String tag = fragment.getTag();
            if(tag != null){
                outState.putString("fragment_tag", tag);
            }
        }

        // 상단 액션바 보임 유무 저장
        outState.putBoolean("actionbar_visibility", abl.getVisibility() == View.VISIBLE ? true : false);

        super.onSaveInstanceState(outState);
    }
}
