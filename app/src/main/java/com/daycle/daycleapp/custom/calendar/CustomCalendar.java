package com.daycle.daycleapp.custom.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.models.AttendanceDayModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.utils.CalendarUtil;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.UIHelper;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CustomCalendar extends Fragment {

    private GregorianCalendar gCal;
    TableLayout tl;
    private int currYear = 0;
    private int currMonth = 0;

    private int startDayOfweek = 0;
    private int maxDay = 0;
    private int dayW =0;
    private int dayH =0;

    private int during;

    ArrayList<String> daylist; //일자 목록을 가지고 있는다. 1,2,3,4,.... 28?30?31?
    ArrayList<String> actlist; //일자에 해당하는 활동내용을 가지고 있는다.
    String currDateString; // 현재 달의 날짜 텍스트

    private int dayCnt;
    private int mSelect = -1;

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.custom_calendar_view;

    // 스크린의 실제 사이즈
    int[] actualScreenSize;

    // 출석일
    public ArrayList<AttendanceDayModel> adays;
    public Calendar rightNow;
    private int circleColor;
    private boolean clickAble = true;
    private AttendanceModel aitem;
    private String[] weekdays;

    public CustomCalendar() {
    }

    public static CustomCalendar newInstance(AttendanceModel aitem, Calendar rightNow){
        CustomCalendar calendar = new CustomCalendar();

        calendar.aitem = aitem;
        calendar.rightNow = rightNow;
        calendar.circleColor = Color.parseColor("#5f6061");
        calendar.clickAble = !aitem.done;

        return calendar;
    }

    public void setCircleColor(int color){
        circleColor = color;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            return;
        }

        // 현재 달력 복구
        if(rightNow == null){
            int y = savedInstanceState.getInt("y");
            int m = savedInstanceState.getInt("m");
            int d = savedInstanceState.getInt("d");
            rightNow = Calendar.getInstance();
            rightNow.set(y, m, d);
            L.d("rightNow 복구");
        }

        // 출석 아이템 복구
        if(aitem == null){
            aitem = savedInstanceState.getParcelable("aitem");

            // 달력 아이템 클릭 가능한지 복구
            if(aitem != null)
            clickAble = !aitem.done;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(FRAGMENT_MAIN_VIEW_RES_ID, container, false); // 메인뷰

        gCal = new GregorianCalendar();
        if(rightNow == null){
            L.d("rightNow == null");
            return null;
        }

        // 달력 데이터가 삽입되는 테이블 레이아웃
        tl = (TableLayout)mainView.findViewById(R.id.tl_calendar_monthly);

        // 스크린의 실제 사이즈
        actualScreenSize = UIHelper.getDeviceScreenSize(getContext());

        // 출석 날짜 가져오기
        adays = AttendanceDayModel.selectAll(aitem.id, clickAble ? AttendanceDayModel.SelectMode.DOING : AttendanceDayModel.SelectMode.DONE);

        // 로케일에 맞는 Weekday 배열 넣기
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        weekdays = CalendarUtil.getWeekdayDisplayNames(true);

        // 캘린더 생성
        makeCalendar();

        return mainView;
    }

    // 상태 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {

        // 현재 달력 저장
        if(rightNow != null){
            int y = rightNow.get(Calendar.YEAR);
            int m = rightNow.get(Calendar.MONTH);
            int d = rightNow.get(Calendar.DAY_OF_MONTH);
            outState.putInt("y", y);
            outState.putInt("m", m);
            outState.putInt("d", d);
            L.d("rightNow 저장");
        }

        // 현재 출석 아이템 저장
        if(aitem != null){
            outState.putParcelable("aitem", aitem);
        }

        super.onSaveInstanceState(outState);
    }

    //달력의 일자를 표시한다.
    public String getCurrentDateString()
    {
        return String.valueOf(rightNow.get(Calendar.YEAR)) + "." + CalendarUtil.doubleString(rightNow.get(Calendar.MONTH) + 1);
    }

    //달력에 표시할 일자를 배열에 넣어 구성한다.
    private void makeCalendar()
    {
     //   getCurrentDateString(String.valueOf(thisYear),String.valueOf(thisMonth+1));

        int thisYear = rightNow.get(Calendar.YEAR);
        int thisMonth = rightNow.get(Calendar.MONTH);
        currYear = thisYear;
        currMonth = thisMonth;

        L.d("캘린더를 그린다." + thisYear + "-" + thisMonth);

        rightNow.set(thisYear, thisMonth, 1);
        gCal.set(thisYear, thisMonth, 1);
        startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);

        maxDay = gCal.getActualMaximum ((Calendar.DAY_OF_MONTH));
        if(daylist==null)
            daylist = new ArrayList<String>();

        daylist.clear();

        if(actlist==null)
            actlist = new ArrayList<String>();
        actlist.clear();

//        daylist.add("일");actlist.add("");
//        daylist.add("월");actlist.add("");
//        daylist.add("화");actlist.add("");
//        daylist.add("수");actlist.add("");
//        daylist.add("목");actlist.add("");
//        daylist.add("금");actlist.add("");
//        daylist.add("토");actlist.add("");

//        daylist.add("Sun");actlist.add("");
//        daylist.add("Mon");actlist.add("");
//        daylist.add("Tue");actlist.add("");
//        daylist.add("Wed");actlist.add("");
//        daylist.add("Thu");actlist.add("");
//        daylist.add("Fri");actlist.add("");
//        daylist.add("Sat");actlist.add("");

//        Map<String, Integer> list = rightNow.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
//        for (String key :
//                list.keySet()) {
//
//            L.d(key);
//        }

//        daylist.add(rightNow.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));actlist.add("");
//        daylist.add("Mon");actlist.add("");
//        daylist.add("Tue");actlist.add("");
//        daylist.add("Wed");actlist.add("");
//        daylist.add("Thu");actlist.add("");
//        daylist.add("Fri");actlist.add("");
//        daylist.add("Sat");actlist.add("");

        // 0번째 배열에 빈값이 들어있다... 버그인가
        for (int i = 1; i < weekdays.length; i++) {
            daylist.add(weekdays[i]);actlist.add("");
        }

        if(startDayOfweek != 1) {
            gCal.set(thisYear, thisMonth-1, 1);
            int prevMonthMaximumDay = (gCal.getActualMaximum((Calendar.DAY_OF_MONTH))+2);
            for(int i=startDayOfweek;i>1;i--){
                daylist.add(Integer.toString(prevMonthMaximumDay-i));
                actlist.add("p");
            }
        }

        for(int i=1;i<=maxDay;i++) //일자를 넣는다.
        {
            daylist.add(Integer.toString(i));
            actlist.add("");
        }


        int dayDummy = (startDayOfweek-1)+maxDay;
//        if(dayDummy > 35)
//        {
//            dayDummy = 42 - dayDummy;
//        }else{
//            dayDummy = 35 - dayDummy;
//        }

        // 높이를 고정으로 가기 위해 더미 고정
        dayDummy = 42 - dayDummy;

        //자투리..그러니까 빈칸을 넣어 달력 모양을 이쁘게 만들어 준다.
        if(dayDummy != 0)
        {
            for(int i=1;i<=dayDummy;i++)
            {
                daylist.add(Integer.toString(i));
                actlist.add("n");
            }
        }

        makeCalendarMatrix();
    }

    // 달력을 한칸 한칸씩 그리기
    private void makeCalendarMatrix()
    {
        final Oneday[] oneday = new Oneday[daylist.size()];
        final Calendar today = Calendar.getInstance();
        tl.removeAllViews();

        dayCnt = 0;
        //int maxRow = ((daylist.size() > 42) ? 7:6);
        int maxRow = 7;
        int maxColumn = 7;

        int screenW = actualScreenSize[0];
        int screenH = actualScreenSize[1];
        dayW = (screenW / maxColumn)+1;
        //dayH = ((((screenH >= screenW)?screenH:screenW) - tl.getTop()) / (maxRow+1))-10;
        dayH = dayW; // 정사각형으로 만들기

        int dayListSize =daylist.size()-1;

        int weekFontSize = UIHelper.changeToPixel(getContext(), 12);    // 요일 글자 크기
        int weekCellPadding = UIHelper.changeToPixel(getContext(), 8); // cell padding
        int weekHeight = UIHelper.changeToPixel(getContext(), 35); // weekHeight

        int dayFontSize = UIHelper.changeToPixel(getContext(), 12);    // 이전 달 다음 달 글자 크기
        int cDayFontSize = UIHelper.changeToPixel(getContext(), 12); // 현재 달 글자 크기
        int dayTopPadding = UIHelper.changeToPixel(getContext(), 4); // 날짜의 Top padding

        for(int i=1;i<=maxRow;i++ )
        {
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            for(int j=1;j<=maxColumn;j++)
            {
                //calender_oneday를 생성해 내용을 넣는다.
                oneday[dayCnt] = new Oneday(getContext());
                Oneday day = oneday[dayCnt];


                //요일별 색상 정하기
                if((dayCnt % 7) == 0){
                    day.setTextDayColor(Color.RED);
                } else if((dayCnt % 7) == 6){
                    day.setTextDayColor(Color.GRAY);
                } else {
                    day.setTextDayColor(Color.BLACK);
                }

                //요일 표시줄 설정
                if(dayCnt >= 0 && dayCnt < 7)
                {
                    day.setBgDayPaint(Color.WHITE); //배경색상
                    day.setTextDayTopPadding(weekCellPadding); //일자표시 할때 top padding
                    day.setTextDayColor(Color.DKGRAY); //일자의 글씨 색상
                    day.setTextDaySize(weekFontSize); // 일자의 글씨크기
                    day.setLayoutParams(new TableRow.LayoutParams(dayW, weekHeight)); //일자 컨트롤 크기
                    day.isWeekBox = true; // 요일이라고 알림
                    day.isToday = false;

                }else{

                    day.isToday = false;
                    day.setDayOfWeek(dayCnt%7 + 1);
                    day.setDay(Integer.valueOf(daylist.get(dayCnt)).intValue());
                    day.setTextActcntSize(14);
                    day.setTextActcntColor(Color.BLACK);
                    day.setTextActcntTopPadding(18);
                    day.setBgSelectedDayPaint(Color.rgb(0, 162, 232));
                    day.setBgTodayPaint(Color.LTGRAY);
                    day.setBgActcntPaint(Color.rgb(251, 247, 176));
                    day.setLayoutParams(new TableRow.LayoutParams(dayW, dayH));

                    day.setTextDaySize(dayFontSize); // 일자 글자 크기
                    day.setTextDayTopPadding(dayTopPadding); // 날짜의 Top padding

                    //이전 달 블럭 표시
                    if(actlist.get(dayCnt).equals("p")){
                        actlist.set(dayCnt, "");

                        if(currMonth - 1 < Calendar.JANUARY){
                            day.setMonth(Calendar.DECEMBER);
                            day.setYear(currYear - 1);
                        }  else {
                            day.setMonth(currMonth - 1);
                            day.setYear(currYear);
                        }

                        // 다음 달 블럭 표시
                    } else if(actlist.get(dayCnt).equals("n")){
                        actlist.set(dayCnt, "");
                        if(currMonth + 1 > Calendar.DECEMBER){
                            day.setMonth(Calendar.JANUARY);
                            day.setYear(currYear + 1);
                        }  else {
                            day.setMonth(currMonth + 1);
                            day.setYear(currYear);
                        }
                        // 현재 달 블력 표시
                    }else{
                        day.setTextDaySize(cDayFontSize);
                        day.setYear(currYear);
                        day.setMonth(currMonth);

                        //오늘 표시
                        if(day.getDay() == today.get(Calendar.DAY_OF_MONTH)
                                && day.getMonth() == today.get(Calendar.MONTH)
                                && day.getYear() == today.get(Calendar.YEAR)){

                            day.isToday = true;
                            //actlist.set(dayCnt,"오늘");
                            day.invalidate();
                            mSelect = dayCnt;
                        }
                    }

                    // 출석 표시된 날짜에 동그라미
                    int y = day.getYear();
                    int m = day.getMonth() + 1;
                    int d = day.getDay();
                    String dayString = CalendarUtil.getDayString(y, m, d);
                    for (AttendanceDayModel item :
                            adays) {

                        if(item.day.equals(dayString)){

                            if(item.is_future){
                                day.setCircleColor(circleColor);
                            }
                            day.setSelected(true);
                            break;
                        }
                    }

                    // 롱클릭
                    day.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, currYear+"-"+currMonth+"-"+oneday[v.getId()].getTextDay(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });

                    // 터치
                    day.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if(!oneday[v.getId()].getTextDay().equals("") && event.getAction() == MotionEvent.ACTION_UP)
                            {
                                mSelect = v.getId();
                                onTouched(oneday[mSelect]);
                            }
                            return false;
                        }
                    });
                }


                day.setTextDay(daylist.get(dayCnt).toString()); //요일,일자 넣기
                day.setTextActCnt(actlist.get(dayCnt).toString());//활동내용 넣기
                day.setId(dayCnt); //생성한 객체를 구별할수 있는 id넣기
                day.invalidate();
                tr.addView(day);

                if(dayListSize != dayCnt)
                {
                    dayCnt++;
                }else{
                    break;
                }
            }
            tl.addView(tr,new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        }
    }




    /**
     * 해당 일이 기준일 범위 안에 있는지 검사
     * @param test 검사할 날짜
     * @param basis 기준 날짜
     * @param during 기간(일)
     * @return
     */
    protected boolean isInside(Oneday test, Oneday basis, int during){
        Calendar calbasis = Calendar.getInstance();
        calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
        calbasis.add(Calendar.DAY_OF_MONTH, during);

        Calendar caltest = Calendar.getInstance();
        caltest.set(test.getYear(), test.getMonth(), test.getDay());

        if(caltest.getTimeInMillis() < calbasis.getTimeInMillis()){
            return true;
        }
        return false;
    }

    /**
     *오늘 달력으로 이동
     */
    public void gotoToday(){
        final Calendar today = Calendar.getInstance();
        currYear = today.get(Calendar.YEAR);
        currMonth = today.get(Calendar.MONTH);
        makeCalendar();
    }

    /**
     * 서브 클래스에서 오버라이드 해서 터치한 날짜 입력 받기
     * @param touchedDay
     */
    protected void onTouched(Oneday touchedDay){

        if(!clickAble)
            return;

        // 날짜를 선택하면 삽입하거나 삭제
        final String day = CalendarUtil.getDayString(touchedDay.getYear(), touchedDay.getMonth() +1, touchedDay.getDay());
        AttendanceDayModel dayItem = AttendanceDayModel.selectOne(aitem.id, day);

        // 미래의 날짜인가?
        boolean isFuture = false;
        final Calendar today = Calendar.getInstance();
        int touchedDayInt = touchedDay.getYear() * 1000 + touchedDay.getMonth() * 100 + touchedDay.getDay();
        int todayInt = today.get(Calendar.YEAR) * 1000 + today.get(Calendar.MONTH) * 100 + today.get(Calendar.DAY_OF_MONTH);
        L.d(touchedDayInt + " " + todayInt);
        if(touchedDayInt > todayInt){

            isFuture = true;
            touchedDay.setCircleColor(circleColor);
        }

        if(dayItem == null){
            // 체크가 안되어 있으면 아이템 새로 생성
            dayItem = new AttendanceDayModel(aitem.id, day, isFuture);
            long insertId = AttendanceDayModel.insert(dayItem);
            dayItem.id = (int)insertId;
            adays.add(dayItem);
            touchedDay.setSelected(true);

            if(!isFuture){
                aitem.cnt = aitem.cnt + 1;
                // 출석 카운트 업데이트
                AttendanceModel.update(aitem);
            }
        }else{

            // 체크가 되어 있으면 삭제
            AttendanceDayModel.delete(dayItem.id);
            adays.remove(dayItem);
            touchedDay.setSelected(false);

            if(!isFuture){
                aitem.cnt = aitem.cnt - 1;
                // 출석 카운트 업데이트
                AttendanceModel.update(aitem);
            }
        }

        touchedDay.invalidate();
    }
}
