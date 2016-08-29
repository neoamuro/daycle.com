package com.daycle.daycleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daycle.daycleapp.adapters.InfinitePagerAdapter;
import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.applications.FragmentTag;
import com.daycle.daycleapp.custom.CustomViewPager;
import com.daycle.daycleapp.custom.StickChart;
import com.daycle.daycleapp.custom.SwipeDirection;
import com.daycle.daycleapp.custom.calendar.CustomCalendar;
import com.daycle.daycleapp.models.AttendanceDayModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends BaseFragment {

    private int mSelect = -1;

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_calendar;

    AttendanceModel item;
    boolean done = false;
    String lastDay;
    ArrayList<AttendanceDayModel> attendanceDays;

    TextView monthText; // 현재 달력 문자열 출력

    StickChart stick1;
    StickChart stick2;
    StickChart stick3;


    CustomViewPager viewPager;
    private static final int PAGE_MIDDLE = 1;
    private int pageSelectIndex = 1;
    public Calendar currCalendar;
    InfinitePagerAdapter monthPageAdapter;
    private ArrayList<CustomCalendar> customCalendars;

    public CalendarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        item = bundle.getParcelable("item"); // 출석 아이템
        done = item.done; // 끝난 아이템인가?

        attendanceDays = AttendanceDayModel.selectAll(item.id, done ? AttendanceDayModel.SelectMode.DONE : AttendanceDayModel.SelectMode.DOING);

        // 미래의 출석 체크가 지난거 삭제
        AttendanceDayModel.deleteFutureDay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // UI 인스턴스 가져오기
        // 상단 타이틀
        TextView itemTextView = (TextView)mainView.findViewById(R.id.itemTextView);
        itemTextView.setText(item.title);

        // 캐린더 뷰 페이저
        viewPager = (CustomViewPager) mainView.findViewById(R.id.viewPager);
        monthText = (TextView)mainView.findViewById(R.id.CalendarMonthTxt);

        // 액션바 설정
        fragmentCallback.setActionBar(getString(R.string.menu_calendar), false, true);

        // 현재 날짜로 설정
        currCalendar = Calendar.getInstance();

        if(savedInstanceState != null){

            // 현재 날짜 복구
            int y = savedInstanceState.getInt("y");
            int m = savedInstanceState.getInt("m");
            int d = savedInstanceState.getInt("d");
            currCalendar.set(y, m, d);
        }

        // 끝난 타이틀이면 색상 그레이로
        if(done){
            itemTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

            // 끝난날이 없으면 done_day로 설정
            if(attendanceDays.size() == 0){
                lastDay = item.done_day;
            }else{
                AttendanceDayModel lastItem = attendanceDays.get(attendanceDays.size() -1);
                lastDay = lastItem.day;
            }

            // 끝났으면 마지막 쳌크한 날짜로...
            if(currCalendar == null){
                String[] splitDay = lastDay.split("-");
                int year = Integer.parseInt(splitDay[0]);
                int month = Integer.parseInt(splitDay[1]) -1;
                currCalendar.set(Calendar.YEAR, year);
                currCalendar.set(Calendar.MONTH, month);
                viewPager.setAllowedSwipeDirection(SwipeDirection.left);
            }
        }

        if(currCalendar == null){
        }

        // 캘린더 뷰 페이저 리스트
        customCalendars = getCalendarFragmentList();
        monthPageAdapter = new InfinitePagerAdapter(getChildFragmentManager(), customCalendars);
        CustomCalendar calendar = (CustomCalendar)monthPageAdapter.getItem(1);
        monthText.setText(calendar.getCurrentDateString());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {

                if(state != ViewPager.SCROLL_STATE_IDLE){
                    return;
                }

                onViewPagerChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                pageSelectIndex = arg0;
            }});
        viewPager.setAdapter(monthPageAdapter);
        viewPager.setCurrentItem(1, false);

        ImageButton btnMPrev = (ImageButton)mainView.findViewById(R.id.btn_calendar_prevmonth); // 왼쪽 달
        MonthClick monthClick = new MonthClick();
        btnMPrev.setOnClickListener(monthClick);
        ImageButton btnMNext = (ImageButton)mainView.findViewById(R.id.btn_calendar_nextmonth);
        btnMNext.setOnClickListener(monthClick);

        Calendar today = Calendar.getInstance();

        // 저저번달
        Calendar twoMonthAgo = Calendar.getInstance();
        twoMonthAgo.set(Calendar.MONTH, today.get(Calendar.MONTH) - 2);

        // 저번달
        Calendar lastMonth = Calendar.getInstance();
        lastMonth.set(Calendar.MONTH, today.get(Calendar.MONTH) - 1);

        int twoAgoItemCount = AttendanceDayModel.selectCountByMonth(item.id, CalendarUtil.getDayString(twoMonthAgo));
        int rightNowItemCount = AttendanceDayModel.selectCountByMonth(item.id, CalendarUtil.getDayString(today));
        int lastMonthItemCount = AttendanceDayModel.selectCountByMonth(item.id, CalendarUtil.getDayString(lastMonth));

        // 하단 스틱형 차트 뷰
        stick1 = (StickChart)mainView.findViewById(R.id.stick1);
        stick1.setMaxAmount(twoMonthAgo.getMaximum(Calendar.DAY_OF_MONTH));
        stick1.setAmount(twoAgoItemCount);
        stick1.setBottomText(twoMonthAgo.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        stick2 = (StickChart)mainView.findViewById(R.id.stick2);
        stick2.setMaxAmount(lastMonth.getMaximum(Calendar.DAY_OF_MONTH));
        stick2.setAmount(lastMonthItemCount);
        stick2.setBottomText(lastMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        stick3 = (StickChart)mainView.findViewById(R.id.stick3);
        stick3.setMaxAmount(today.getMaximum(Calendar.DAY_OF_MONTH));
        stick3.setAmount(rightNowItemCount);
        stick3.setRecFillColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        stick3.setBottomText(today.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        return mainView;
    }

    // 뷰 페이지 변경시(스와이프) 호출되는 이벤트
    private void onViewPagerChanged(){
        if (pageSelectIndex == 0) {

            // 현재 달력에서 한달 빼기
            currCalendar.add(Calendar.MONTH, -1);

            // 왼쪽으로 스와이프 했으면 일단 스와이프 무조건 허용
            viewPager.setAllowedSwipeDirection(SwipeDirection.all);

            customCalendars = new ArrayList<>();

            // 첫번째 것에는 새로 삽입한다.
            Calendar preMonth = (Calendar) currCalendar.clone();
            preMonth.set(Calendar.MONTH, preMonth.get(Calendar.MONTH) - 1);
            customCalendars.add(CustomCalendar.newInstance(item, preMonth));

            // 기준페이지를 첫번째로 덮어 씌운다
            customCalendars.add(CustomCalendar.newInstance(item, currCalendar));

            // 세번째 것을 두번째로 덮어 씌운다
            Calendar nextMonth = (Calendar) currCalendar.clone();
            nextMonth.set(Calendar.MONTH, nextMonth.get(Calendar.MONTH) + 1);
            customCalendars.add(CustomCalendar.newInstance(item, nextMonth));

            // 업데이트
            monthPageAdapter.updateFragList(customCalendars);

        } else if (pageSelectIndex == 2) {

            // 현재 달력에서 한달 더하기
            currCalendar.add(Calendar.MONTH, 1);

            // 다음 달력이 제한되면 오른쪽 스와이프 막음
            if(preventNextMonth()){
                viewPager.setAllowedSwipeDirection(SwipeDirection.left);
            }

            customCalendars = new ArrayList<>();

            // 첫번째것을 두번째로 덮어 씌운다
            Calendar preMonth = (Calendar) currCalendar.clone();
            preMonth.set(Calendar.MONTH, preMonth.get(Calendar.MONTH) - 1);
            customCalendars.add(CustomCalendar.newInstance(item, preMonth));

            // 세번째 것을 두번째로 덮어 씌운다
            customCalendars.add(CustomCalendar.newInstance(item, currCalendar));

            // 세번째 것에는 새로 삽입한다.
            Calendar nextMonth = (Calendar) currCalendar.clone();
            nextMonth.set(Calendar.MONTH, nextMonth.get(Calendar.MONTH) + 1);
            customCalendars.add(CustomCalendar.newInstance(item, nextMonth));

            // 업데이트
            monthPageAdapter.updateFragList(customCalendars);
        }


        // 항상 가운데 있는것을 보여준다.
        viewPager.setCurrentItem(1, false);

        // 상단 데이터 세팅
        CustomCalendar calendar = customCalendars.get(1);
        monthText.setText(calendar.getCurrentDateString());
    }

    // 초기 뷰페이저 프래그먼트 세팅
    public ArrayList<CustomCalendar> getCalendarFragmentList(){

        ArrayList<CustomCalendar> items = new ArrayList<>();
        Calendar prevMonth, nextMonth;
        prevMonth = (Calendar) currCalendar.clone();
        nextMonth = (Calendar) currCalendar.clone();
        prevMonth.set(Calendar.MONTH, prevMonth.get(Calendar.MONTH) - 1);
        nextMonth.set(Calendar.MONTH, nextMonth.get(Calendar.MONTH) + 1);
        items.add(CustomCalendar.newInstance(item, prevMonth));
        items.add(CustomCalendar.newInstance(item, currCalendar));
        items.add(CustomCalendar.newInstance(item, nextMonth));
        return items;
    }

    // 완료된 출석 아이템의 뷰페이저 데이터 생성
    public ArrayList<CustomCalendar> getDonCalendarFragmentList(){

        currCalendar = Calendar.getInstance();
        String[] splitDay = lastDay.split("-");
        int year = Integer.parseInt(splitDay[0]);
        int month = Integer.parseInt(splitDay[1]) -1;
        currCalendar.set(Calendar.YEAR, year);
        currCalendar.set(Calendar.MONTH, month);

        ArrayList<CustomCalendar> items = new ArrayList<>();
        Calendar prevMonth;
        prevMonth = (Calendar) currCalendar.clone();
        prevMonth.set(Calendar.MONTH, prevMonth.get(Calendar.MONTH) - 1);
        items.add(CustomCalendar.newInstance(item, prevMonth));
        items.add(CustomCalendar.newInstance(item, currCalendar));
        return items;
    }

    // 다음달로 넘어가는거 막기
    private boolean preventNextMonth(){
        if(done){
            String[] splitDay = lastDay.split("-");
            int year = Integer.parseInt(splitDay[0]);
            int month = Integer.parseInt(splitDay[1]) - 1;
            if(currCalendar.get(Calendar.YEAR) == year && currCalendar.get(Calendar.MONTH) == month){
                return true;
            }
        }

        return false;
    }

    // 왼쪽 / 오른쪽 화살표 클릭
    private class MonthClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId())
            {
                case R.id.btn_calendar_prevmonth:

                    viewPager.setCurrentItem(0);

                    break;

                case R.id.btn_calendar_nextmonth:

                    // 완료된 아이템이면 달력을 마지막 체크일까지만 넘길수 있게 제한함
                    if(preventNextMonth()){
                        break;
                    }

                    viewPager.setCurrentItem(2);

                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // 현재 달력 상태 저장
        if(currCalendar != null){
            int y = currCalendar.get(Calendar.YEAR);
            int m = currCalendar.get(Calendar.MONTH);
            int d = currCalendar.get(Calendar.DAY_OF_MONTH);
            outState.putInt("y", y);
            outState.putInt("m", m);
            outState.putInt("d", d);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBack() {
        fragmentCallback.unCheckNavMenu();

        Fragment fragment = done ? new DoneFragment() : new AttendanceFragment();
        App.startFragment(getFragmentManager(), fragment, done ? FragmentTag.DONE.name() : FragmentTag.ATTENDANCE.name());
    }
}
