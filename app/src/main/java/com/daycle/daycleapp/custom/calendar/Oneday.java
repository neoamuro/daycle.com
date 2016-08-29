package com.daycle.daycleapp.custom.calendar;

/**
 * Created by neoam on 2016-08-21.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.UIHelper;

import java.util.Calendar;

public class Oneday extends View {

    private int year;
    private int month;
    private int day;
    private int dayOfWeek;

    private String textDay;
    private String textActCnt;

    private Paint bgDayPaint;
    private Paint bgSelectedDayPaint;
    private Paint bgActcntPaint;
    private Paint bgTodayPaint;
    private Paint textDayPaint;
    private Paint textActcntPaint;
    private Paint circlePaint;

    private int textDayTopPadding;
    private int textDayLeftPadding;
    private int textActcntTopPadding;
    private int textActcntLeftPadding;

    private Paint mPaint;

    private boolean mSelected;
    public boolean isToday = false;

    public Oneday(Context context , android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Oneday(Context context) {
        super(context);
        init();

    }

    private void init()
    {
        bgDayPaint = new Paint();
        bgSelectedDayPaint = new Paint();
        bgActcntPaint = new Paint();
        textDayPaint = new Paint();
        textActcntPaint = new Paint();
        bgTodayPaint = new Paint();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 안티 얼라이싱을 준다.
        bgDayPaint.setColor(Color.WHITE);
        bgActcntPaint.setColor(Color.YELLOW);
        textDayPaint.setColor(Color.WHITE);
        textDayPaint.setAntiAlias(true);
        textActcntPaint.setColor(Color.WHITE);
        textActcntPaint.setAntiAlias(true);
        bgTodayPaint.setColor(Color.GREEN);
        circlePaint.setColor(Color.BLACK);
        rect = new RectF();

        setTextDayTopPadding(0);
        setTextDayLeftPadding(0);

        setTextActcntTopPadding(0);
        setTextActcntLeftPadding(0);

        mPaint = new Paint();

        mSelected = false;
    }

    RectF rect;
    @Override
    protected void onDraw(Canvas canvas) {

        if(isToday){
            //canvas.drawPaint(bgTodayPaint);
        } else {

        }

        canvas.drawPaint(bgDayPaint);

        if(mSelected){
            int x = getWidth();
            int y = getHeight();
            //int radius = UIHelper.changeToPixel(getContext(), 50);
            int radius = x / 3; // 반지름의 크기는 사각형 넓이의 1/3
            Paint paint = circlePaint;
            //paint.setStyle(Paint.Style.FILL);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(UIHelper.changeToPixel(getContext(), 2));
            canvas.drawCircle(x / 2, y / 2, radius, paint);
//            L.d("서클 그리기 " + x + " " + y);
        }

        int width = this.getWidth()/2;
        int height = this.getHeight()/2;

        // 날짜 숫자 출력
        int textDaysize = (int)textDayPaint.measureText(getTextDay()) / 2;
        int textActsize = (int)textActcntPaint.measureText(getTextActCnt()) / 2;

        if(isToday){
            textDayPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        }else{
        }

        canvas.drawText(getTextDay(), width - textDaysize + getTextDayLeftPadding(), height + getTextDayTopPadding(), textDayPaint);

        canvas.drawText(getTextActCnt(), width - textActsize + getTextActcntLeftPadding(), height + getTextActcntTopPadding(), textActcntPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#e5e5e5"));

        // 달력 칸막이 라인 그리기
        canvas.drawLine(0, this.getHeight()-1, this.getWidth()-1, this.getHeight()-1, mPaint); // 가로 줄

        // 세로줄은 요일이 아닌 경우에만 그린다.
        if(!isWeekBox){
            canvas.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight()-1, mPaint); // 세로 줄
        }
    }
    public int getTextDayTopPadding() {
        return this.textDayTopPadding;
    }
    public int getTextDayLeftPadding() {
        return this.textDayLeftPadding;
    }
    public void setTextDayTopPadding(int top){
        this.textDayTopPadding = top;
    }
    public void setTextDayLeftPadding(int left){
        this.textDayLeftPadding = left;
    }

    public int getTextActcntTopPadding() {
        return this.textActcntTopPadding;
    }
    public int getTextActcntLeftPadding() {
        return this.textActcntLeftPadding;
    }
    public void setTextActcntTopPadding(int top){
        this.textActcntTopPadding = top;
    }
    public void setTextActcntLeftPadding(int left){
        this.textActcntLeftPadding = left;
    }

    public void setBgTodayPaint(int color){
        this.bgTodayPaint.setColor(color);
    }
    public void setBgDayPaint(int color){
        this.bgDayPaint.setColor(color);
    }
    public void setBgSelectedDayPaint(int color){
        this.bgSelectedDayPaint.setColor(color);
    }
    public void setBgActcntPaint(int color){
        this.bgActcntPaint.setColor(color);
    }
    public void setSelected(boolean selected){
        this.mSelected = selected;
    }
    public boolean getSelected() {
        return this.mSelected;
    }

    public boolean isWeekBox = false;

    /**
     * 일자에 표시된 글 리턴
     * @return
     */
    public String getTextDay() {
        return this.textDay;
    }
    /**
     * 일자에 표시할 글 입력
     * @param string
     */
    public void setTextDay(String string) {
        this.textDay = string;
    }

    /**
     *  부가 설명에 표시된 글 리턴
     * @return
     */
    public String getTextActCnt(){
        return this.textActCnt;
    }
    /**
     * 부가 설명에 표시할 글 입력
     * @param string
     */
    public void setTextActCnt(String string){
        this.textActCnt = string;
    }
    /**
     * 일자 글씨 색상
     * @param color
     */
    public void setTextDayColor(int color){
        this.textDayPaint.setColor(color);
    }
    /**
     * 일자 글씨 크기
     * @param size
     */
    public void setTextDaySize(int size){
        this.textDayPaint.setTextSize(size);
    }

    /**
     *  부가 설명 글자 색상
     * @param color
     */
    public void setTextActcntColor(int color){
        this.textActcntPaint.setColor(color);
    }

    public void setCircleColor(int color){
        this.circlePaint.setColor(color);
    }

    /**
     * 부가 설명 글자 크기
     * @param size
     */
    public void setTextActcntSize(int size){
        this.textActcntPaint.setTextSize(size);
    }

    /**
     * 년도
     * @param _year
     */
    public void setYear(int _year){
        year = _year;
    }
    /**
     * @return 년도
     */
    public int getYear(){
        return year;
    }

    /**
     *  월
     *
     * @param _month 0~11, Calendar.JANUARY ~ Calendar.DECEMBER
     */
    public void setMonth(int _month){
        month = Math.min(Calendar.DECEMBER, Math.max(Calendar.JANUARY, _month));
        month = _month;
    }
    /**
     * @return 월 0~11, Calendar.JANUARY ~ Calendar.DECEMBER
     */
    public int getMonth(){
        return month;
    }

    /**
     * 일 1~31
     */
    public void setDay(int _day){
        day = Math.min(31, Math.max(1, _day));
        day = _day;
    }
    /**
     * @return 일 1~31
     */
    public int getDay(){
        return day;
    }
    /**
     * 요일 1~7<br/>
     * Calendar.SUNDAY ~ Calendar.SATURDAY
     */
    public void setDayOfWeek(int _dayOfWeek){
        dayOfWeek = Math.min(Calendar.SATURDAY, Math.max(Calendar.SUNDAY, _dayOfWeek));
        dayOfWeek = _dayOfWeek;
    }

    /**
     * @return 요일 1~7, Calendar.SUNDAY ~ Calendar.SATURDAY
     */
    public int getDayOfWeek(){
        return dayOfWeek;
    }

    /**
     * 해당 요일을 한글로 리턴
     * @return "일", "월", "화", "수", "목", "금", "토"
     */
    public String getDayOfWeekKorean(){
        final String[]korean = {"오류", "일", "월", "화", "수", "목", "금", "토"};
        return korean[dayOfWeek];
    }

    /**
     * 해당 요일을 영어로 리턴
     * @return "Sun", "Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur"
     */
    public String getDayOfWeekEnglish(){
        final String[]korean = {"E", "Sun", "Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur"};
        return korean[dayOfWeek];
    }

    /**
     * 기본 정보 복사
     * @param srcDay
     */
    public void copyData(Oneday srcDay){
        setYear(srcDay.getYear());
        setMonth(srcDay.getMonth());
        setDay(srcDay.getDay());
        setDayOfWeek(srcDay.getDayOfWeek());
        setTextDay(srcDay.getTextDay());
        setTextActCnt(srcDay.getTextActCnt());
    }

}
