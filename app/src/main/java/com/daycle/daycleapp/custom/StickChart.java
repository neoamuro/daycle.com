package com.daycle.daycleapp.custom;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.UIHelper;

/**
 * Created by neoam on 2016-08-22.
 */
public class StickChart extends View {

    private Paint paint;
    private int amount; // 사각형의 양
    private int maxAmount; // 사각형의 최대 양
    private int recFillColor; // 사각형의 컬러값
    private String bottomText; // 하단 텍스트
    private int defaultColor;
    private int defaultBottomMargin = 10; // 하단 여백

    public StickChart(Context context) {
        super(context);
        init();
    }

    public StickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        defaultColor = Color.DKGRAY;
        recFillColor = R.color.colorDone;
        maxAmount = 10;
        bottomText = "";
    }

    public void setAmount(int amount){
        this.amount = amount;
    }
    public void setMaxAmount(int maxAmount){
        this.maxAmount = maxAmount;
    }
    public void setRecFillColor(int recFillColor){
        this.recFillColor = recFillColor;
    }
    public void setBottomText(String bottomText){
        this.bottomText = bottomText;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getWidth();
        int h = getHeight() - UIHelper.changeToPixel(getContext(), defaultBottomMargin); // 기본 높이를 좀 작게 줘서 하단에 여백이 생기도록...

        // 텍스트 사이즈 설정
        int textSize = UIHelper.changeToPixel(getContext(), 12);
        paint.setTextSize(textSize);

        // 하단 라인 아래에 글자 표시하기
        paint.setColor(defaultColor);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        //paint.setTextAlign(Paint.Align.LEFT);

        Rect monthTextBound = new Rect();
        String text = bottomText;
        paint.getTextBounds(text, 0, text.length(), monthTextBound);

        int stickTopMargin = h / 3; // 상단 텍스트가 들어갈 영역 높이

        // 하단 텍스트가 들어갈 영역 높이
        //int bottomMargin = (int)((monthTextBound.height()) * 1.5);
        int bottomMargin = UIHelper.changeToPixel(getContext(), 20);
        int stickBoxH = h - (stickTopMargin + bottomMargin); // 박스가 그려지는 영역 높이

        L.d("monthTextBound: " + monthTextBound.height());

        // 사각형의 높이 설정
        // 높이 = (뷰의 높이 / 31) * amount
        int rectH = (stickBoxH / maxAmount) * amount;
        int rectW = getWidth() / 2; // 뷰 크기의 1/3 정도로 사각형 넓이를 설정
        int startX = rectW / 2;
        int startRectTop = h - (rectH + bottomMargin); // 사각형의 상단 시작 위치

        // 도형이 그려지는 모든 시작점 높이
        int startY = h - bottomMargin;

        // 하단 텍스트 그리기
        canvas.drawText(text, (float)(startX * 1.5) - (monthTextBound.width() / 2), startY + bottomMargin, paint);

        // 하단 라인 그리기
        paint.setColor(defaultColor);
        canvas.drawLine(0, startY, this.getWidth()-1, startY, paint); // 가로 줄

        // 사각형 그리기
        paint.setColor(recFillColor);
        canvas.drawRect(new Rect(startX, startRectTop, rectW, startY), paint);

        // 사각형 위에 숫자 표시하기
        paint.setColor(defaultColor);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        Rect amountTextBound = new Rect();
        String amountText = String.valueOf(amount);
        paint.getTextBounds(amountText, 0, amountText.length(), amountTextBound);
        canvas.drawText(String.valueOf(amount), (float)(startX * 1.5) - (amountTextBound.width() / 2),
                startRectTop - (float)(amountTextBound.height()), paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = UIHelper.changeToPixel(getContext(), 100);

        // 최대 사이즈 지정 portrait 모드일때 (하단에 스크롤이 안생기게 하기 위해...)
        int[] actualScreenSize = UIHelper.getDeviceScreenSize(getContext());
        int actualScreenHeight = actualScreenSize[1];
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            actualScreenHeight = actualScreenSize[0];
        }
        int maxHeight = actualScreenHeight / 9;
        //int maxHeight = UIHelper.changeToPixel(getContext(), 80);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        if (widthMode != MeasureSpec.EXACTLY) {
//            throw new IllegalStateException("Width must have an exact value or MATCH_PAR``ENT");
//        } else if (heightMode != MeasureSpec.EXACTLY) {
//            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
//        }

        int width;
        int height = 0;


        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {

            // 미리 지정된 사이즈보다 커질순 없다.
            width = Math.min(desiredWidth, widthSize);
        } else {
            // 미리 지정된 사이즈보다 커질순 없다.
            width = desiredWidth;
        }


        // match_parent
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
            L.d("heightMode: MeasureSpec.EXACTLY " + height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            L.d("heightMode: MeasureSpec.AT_MOST");
            // wrap_content
            height = Math.min(maxHeight, heightSize);
        } else {

            // 스크롤 뷰 안에서는 스크린 높이가 많이 작을 경우 뷰가 안보이므로 높이를 강제 지정
            // 가로 모드일 경우에만 기본 높이 보여주기
//            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//                height = desiredHeight;
//            }

            //height = desiredHeight;
            if(height == 0){
                height = Math.max(maxHeight, heightSize);
            }
            //height = Math.min(desiredHeight, heightSize);
        }

        ///L.d("height: " + height);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        setMeasuredDimension(width, getDefaultSize(this.getSuggestedMinimumHeight(), heightMeasureSpec));

        L.d("last: " + heightMeasureSpec);
        L.d("last height: " + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
