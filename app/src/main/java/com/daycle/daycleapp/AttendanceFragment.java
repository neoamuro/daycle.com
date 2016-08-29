package com.daycle.daycleapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;
import com.daycle.daycleapp.custom.swipelistview.ArrayAdapter;
import com.daycle.daycleapp.custom.swipelistview.appearance.simple.AlphaInAnimationAdapter;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.DynamicListView;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.dragdrop.OnItemMovedListener;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.DismissableManager;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.OnDismissCallback;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback;

import com.daycle.daycleapp.custom.CustomSimpleSwipeUndoAdapter;
import com.daycle.daycleapp.custom.AttendanceListViewAdapter;
import com.daycle.daycleapp.models.ActionBarModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.models.SettingsModel;
import com.daycle.daycleapp.utils.CalendarUtil;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.UIHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class AttendanceFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_attendance;

    private static final int INITIAL_DELAY_MILLIS = 300;

    EditText itemEditText;
    View itemAddBox;
    boolean visibleInput = false;
    ArrayAdapter<AttendanceModel> adapter;

    public AttendanceFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        fragmentCallback.setActionBarVisibility(true);

        ActionBarModel actionBarModel = new ActionBarModel("Daycle");
        actionBarModel.showAddButton = true;
        actionBarModel.showHomeButton = false;
        //actionBarModel.customHomeButton = false;
        fragmentCallback.setActionBar(actionBarModel);

        // UI 인스턴스 가져오기
        DynamicListView listView = (DynamicListView) mainView.findViewById(R.id.dynamicListView); // 스와이프 리스트 뷰
        itemAddBox = mainView.findViewById(R.id.itemAddBox);                // 아이템 입력박스
        itemEditText = (EditText)mainView.findViewById(R.id.itemEditText);  // 아이템 입력 텍스트박스
        View itemCancelButton = mainView.findViewById(R.id.itemCancelButton); // 취소 버튼
        View itemAddButton = mainView.findViewById(R.id.itemAddButton); // 등록 버튼

        if(savedInstanceState != null){

            // 1. 인풋박스 열려있는 상태 복구
            boolean isVisible = savedInstanceState.getBoolean("inputbox_visibility");
            showInput(isVisible);
        }

        // 상단 헤더 삽입
        //listView.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.activity_dynamiclistview_header, listView, false));

        // 카운트 집계 세팅값
        SettingsModel settings = App.getSettings(getContext());

        // 진행중인 리스트 가져오기
        ArrayList<AttendanceModel> items = AttendanceModel.selectAll(AttendanceModel.SelectMode.DOING, settings.attendance_mode == SettingsModel.AttendanceMode.ONLY_MONTH);

        // 리스트뷰 어댑터
        adapter = new AttendanceListViewAdapter(getContext(), items, settings.attendance_mode);

        CustomSimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new CustomSimpleSwipeUndoAdapter(adapter, new MyOnDismissCallback());

        // 리스트 보여줄때 애니메이션 효과
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(listView);
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animAdapter);

        // 드래그 정렬 가능하게
        listView.enableDragAndDrop();
        listView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));

        /* Enable swipe to dismiss */
        // 스와이프만 가능하게 (undo는 없음)
        listView.enableSwipeToDismiss(R.color.colorDefaultBackground, new OnDismissCallback() {

            // 왼쪽이나 오른쪽으로 플링 했음
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions, boolean flingToRight) {
                int position = reverseSortedPositions[0];
                AttendanceModel item = adapter.getItem(position);

                if(flingToRight){
                    L.d("flingToRight");
                    // 오른쪽으로 플링 했으면 디비에서 삭제
                    AttendanceModel.delete(item.id);
                }else{
                    // 왼쪽으로 플링 했으면 done으로 변경
                    item.done = true;

                    // 현재 날짜를 done 날짜로 설정
                    Calendar today = Calendar.getInstance();
                    item.done_day = CalendarUtil.getDayString(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));

                    // 업데이트
                    AttendanceModel.update(item);
                }

                // 어쨌든 UI 목록에서 삭제
                adapter.remove(position);
            }
        });


        // 스와이프 가능하거나 불가능한 아이템 설정
        listView.setDismissableManager(new DismissableManager() {
            @Override
            public boolean isDismissable(long id, int position) {
                return true;
            }
        });

        // 아이템 클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showInput(false);

                Bundle bundle = new Bundle();
                bundle.putParcelable("item", (AttendanceModel)adapterView.getItemAtPosition(i));
                bundle.putBoolean("done", false);

                // 캘린더 프래그먼트 생성
                App.startFragment(getFragmentManager(), new CalendarFragment(), bundle, FragmentTag.CALENDAR.name()
                );
            }
        });

        // 엔터키 체크
        itemEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyCode == KeyEvent.KEYCODE_ENTER){

                    save();
                    return true;
                }

                return false;
            }
        });

        // 취소버튼 클릭 이벤트 핸들러
        itemCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput(false);
            }
        });

        // 등록버튼 클릭 이벤트 핸들러
        itemAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        // tint 설정
        UIHelper.setButtonTint((Button)itemAddButton, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));

        return mainView;
    }

    // 입력 박스 보이기
    public void showInput(boolean visible){

        visibleInput = visible;

        fragmentCallback.setActionBarVisibility(!visible); // 액션바 보임 유무
        itemAddBox.setVisibility(visible ? View.VISIBLE : View.GONE);
        itemEditText.setText("");

        if(visible){

            // 텍스트박스에 포커스 주고...
            if(itemEditText.requestFocus()){

                // 키보드 나오게...
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(itemEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }else{
            // 키보드 나오게...
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(itemEditText.getWindowToken(), 0);
        }
    }

    // 출석 아이템 등록
    private void save(){

        final String title = itemEditText.getText().toString();

        // 빈값 체크
        if(title.length() == 0){
            return;
        }

        AttendanceModel item = new AttendanceModel();
        item.title = title;
        item.done_day = "";
        item.order = 0;

        // 디비에 삽입
        long insertId = AttendanceModel.insert(item);
        item.id = (int)insertId; // 유니크 아이디 갱신

        // 리스트에 추가
        adapter.add(0, item);

        // 인풋값 감춤
        showInput(false);
    }

    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {

            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true;
        }
    }

    private class MyOnDismissCallback implements UndoCallback {
        @NonNull
        @Override
        public View getPrimaryView(@NonNull View view) {
            return null;
        }

        @NonNull
        @Override
        public View getUndoView(@NonNull View view) {
            return null;
        }

        @Override
        public void onUndoShown(@NonNull View view, int position) {

        }

        @Override
        public void onUndo(@NonNull View view, int position) {
            L.d("zzz");
        }

        @Override
        public void onDismiss(@NonNull View view, int position) {
            // UI에서 아이템 삭제된 후 호출
            L.d(position + "");
        }

        @Override
        public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions, boolean flingToRight) {
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final ArrayAdapter<AttendanceModel> mAdapter;

        private Toast mToast;

        MyOnItemMovedListener(final ArrayAdapter<AttendanceModel> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
//            if (mToast != null) {
//                mToast.cancel();
//            }
//
//            mToast = Toast.makeText(getApplicationContext(), getString(R.string.moved, mAdapter.getItem(newPosition), newPosition), Toast.LENGTH_SHORT);
//            mToast.show();

            // 아이템 위치가 변경됐으면 디비 순번 업데이트
            AttendanceModel.updateOrder(new ArrayList<AttendanceModel>(adapter.getItems()));
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private final DynamicListView mListView;

        MyOnItemClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
//            mListView.insert(position, getString(R.string.newly_added_item, mNewItemCount));
//            mNewItemCount++;
        }
    }

    // 프래그먼트 상태 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {

        // 1. 인풋박스 열려있는 상태 저장
        outState.putBoolean("inputbox_visibility", visibleInput);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBack() {

        DrawerLayout drawer = fragmentCallback.getDrawer();

        if(drawer.isDrawerOpen(GravityCompat.START)){
            // 창이 열려 있으면 닫기
            drawer.closeDrawer(GravityCompat.START);
        }else if(visibleInput){
            // 입력 박스가 열려 있으면 닫기
            showInput(false);
        }else{
            // 아무것도 아니면 앱 끝내기
            getActivity().finish();
        }
    }
}
