package com.daycle.daycleapp.custom;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.daycle.daycleapp.utils.L;

/**
 * Created by neoam on 2016-08-23.
 * 거지같은 뷰페이저가 높이를 지멋대로 match_parent로 설정해서 커스텀으로 만들어서 높이를 재설정 해야함
 */

public class CustomViewPager extends ViewPager {

    private View mCurrentView;
    private float initialXValue;
    private SwipeDirection direction;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.direction = SwipeDirection.all;
    }



    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;
//        if(wrapHeight){
//            int width = getMeasuredWidth(), height = getMeasuredHeight();
//
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//
//            if(getChildCount() > 0){
//                View firstChid = getChildAt(0);
//                firstChid.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
//                height = firstChid.getMeasuredHeight();
//            }
//
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//            L.d("heightMeasureSpec: " + height);
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }

        int height = 0;
        if (getChildCount() != 0) {
            View child = getChildAt(0);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height){
                height = h;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        // super has to be called in the beginning so the child views can be
        // initialized.
        // 부모의 onMeasure는 자식 뷰들이 초기화 되기전에 반드시 불러져야 한다.
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        // 자식이 없으면 그대로 리턴
//        if (getChildCount() <= 0)
//            return;
//
//        // wrap_content인지 체크
//        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;
//
//        int width = getMeasuredWidth();
//
//        View firstChild = getChildAt(0);
//
//        // Initially set the height to that of the first child - the
//        // PagerTitleStrip (since we always know that it won't be 0).
//        int height = firstChild.getMeasuredHeight();
//
//        if (wrapHeight) {
//            // Keep the current measured width.
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        }
//
//        int fragmentHeight = 0;
//        fragmentHeight = measureFragment(((Fragment) getAdapter().instantiateItem(this, getCurrentItem())).getView());
//
//        // Just add the height of the fragment:
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(fragmentHeight, MeasureSpec.EXACTLY);
//
//        // super has to be called again so the new specs are treated as
//        // exact measurements.
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    private boolean IsSwipeAllowed(MotionEvent event) {
        if(this.direction == SwipeDirection.all) return true;

        if(direction == SwipeDirection.none )//disable any swipe
            return false;

        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if(event.getAction()==MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0 && direction == SwipeDirection.right ) {
                    // swipe from left to right detected
                    return false;
                }else if (diffX < 0 && direction == SwipeDirection.left ) {
                    // swipe from right to left detected
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }


    public void setAllowedSwipeDirection(SwipeDirection direction) {
        this.direction = direction;
    }
}
