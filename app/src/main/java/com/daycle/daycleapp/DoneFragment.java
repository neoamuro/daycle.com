package com.daycle.daycleapp;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;
import com.daycle.daycleapp.custom.CustomSimpleSwipeUndoAdapter;
import com.daycle.daycleapp.custom.swipelistview.ArrayAdapter;
import com.daycle.daycleapp.custom.swipelistview.DoneListViewAdapter;
import com.daycle.daycleapp.custom.swipelistview.appearance.simple.AlphaInAnimationAdapter;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.DynamicListView;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.OnDismissCallback;
import com.daycle.daycleapp.models.ActionBarModel;
import com.daycle.daycleapp.models.AttendanceDayModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.utils.L;

/**
 * A placeholder fragment containing a simple view.
 */
public class DoneFragment extends BaseFragment {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_done;
    private static final int INITIAL_DELAY_MILLIS = 300;

    ArrayAdapter<AttendanceModel> adapter;

    public DoneFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // UI 인스턴스 가져오기
        DynamicListView listView = (DynamicListView) mainView.findViewById(R.id.dynamicListView); // 스와이프 리스트 뷰

        // 액션바 설정
        ActionBarModel actionBarModel = new ActionBarModel(getString(R.string.menu_done));
        actionBarModel.backgroundColorResId = R.color.colorDone;
        fragmentCallback.setActionBar(actionBarModel);

        // 완료된 리스트 가져오기
        ArrayList<AttendanceModel> items = AttendanceModel.selectAll(AttendanceModel.SelectMode.DONE, false);

//        날짜 표시하기
//        1. 현재 체크목록의 날짜를 가져온다
//        2. 날짜 목록이 없으면 표시안함
//        3. 년도를 체크
//        4. 년도가 틀린게 있으면 가장 작은 날짜와 가장 큰 날짜를 가져와서 표시
//        5. 년도가 같으면 월이 틀린게 있는지 체크
//        6. 월이 틀린게 있으면 가장 작은 날짜와 가장 큰 날짜를 가져와서 표시
//        7. 월이 다른게 없으면 그 월만 표시



        for (AttendanceModel item : items) {
            ArrayList<AttendanceDayModel> days = AttendanceDayModel.selectAll(item.id, AttendanceDayModel.SelectMode.DONE);

            for (AttendanceDayModel m : days
                    ) {
                L.d("month: " + m.day);
            }

            // 날짜별 정렬
            Collections.sort(days, new Comparator<AttendanceDayModel>() {
                @Override
                public int compare(AttendanceDayModel item1, AttendanceDayModel item2) {
                    return item1.day.compareTo(item2.day);
                }
            });

            if(days.size() == 0){
                item.day = "";
            }else{
                TreeMap<String, List<String>> years = new TreeMap<>();
                for (AttendanceDayModel day : days) {
                    String[] splitDay = day.day.split("-");
                    String year = splitDay[0];
                    String month = splitDay[1];
                    if (!years.containsKey(year)) {
                        List<String> list = new ArrayList<String>();
                        list.add(month);

                        years.put(year, list);
                    } else {
                        // month는 중복 안되게
                        if(!years.get(year).toString().matches("\\[.*\\b" + month + "\\b.*]")){
                            years.get(year).add(month);
                        }
                    }
                }

                // 시작 년도와 시작 월
                Map.Entry<String,List<String>> firstYear = years.firstEntry();
                String minYear = firstYear.getKey();
                String minMonth = firstYear.getValue().get(0);

                // 끝난 년도와 끝난 월
                Map.Entry<String,List<String>> lastYear = years.lastEntry();
                String maxYear = lastYear.getKey();
                String maxMonth = lastYear.getValue().get(lastYear.getValue().size() - 1);

                // 년도가 같으면 월이 틀린게 있는지 체크
                if(years.size() == 1){
                    Set<String> keySet = years.keySet();
                    Map.Entry<String,List<String>> y = years.entrySet().iterator().next();
                    List<String> months = y.getValue();

                    // 월이 다른게 없으면 그 월만 표시
                    if(months.size() == 1){
                        item.day = minYear + "." + minMonth;
                    }else{

                        // 월이 틀린게 있으면 가장 작은 날짜와 가장 큰 날짜를 가져와서 표시
                        item.day = minYear + "." + minMonth + "-" + maxMonth;
                    }

                }else{
                    // 년도가 틀린게 있으면 가장 작은 날짜와 가장 큰 날짜를 가져와서 표시
                    item.day = minYear + "." + minMonth + "-" + maxYear + "." + maxMonth;
                }
            }
        }

        // 리스트뷰 어댑터
        adapter = new DoneListViewAdapter(getContext(), items);
        CustomSimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new CustomSimpleSwipeUndoAdapter(adapter, null);

        // 리스트 보여줄때 애니메이션 효과
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(listView);
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animAdapter);

        /* Enable swipe to dismiss */
        // 스와이프 완료 됐을때...
        listView.enableSwipeToDismiss(R.color.colorGray, new OnDismissCallback() {

            // 왼쪽이나 오른쪽으로 플링 했음
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions, boolean flingToRight) {
                int position = reverseSortedPositions[0];
                AttendanceModel item = adapter.getItem(position);

                if(flingToRight){
                    // 오른쪽으로 플링 했으면 디비에서 삭제
                    AttendanceModel.delete(item.id);
                }else{
                    // 왼쪽으로 플링 했으면 출석 진행중으로 복구
                    item.done = false;
                    AttendanceModel.update(item);
                }

                // 어쨌든 UI 목록에서 삭제
                adapter.remove(position);
            }
        });

        // 아이템 클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", (AttendanceModel)adapterView.getItemAtPosition(i));
                bundle.putBoolean("done", true);

                // 캘린더 프래그먼트 생성
                App.startFragment(getFragmentManager(), new CalendarFragment(), bundle, FragmentTag.CALENDAR.name());
            }
        });

        return mainView;
    }
}
