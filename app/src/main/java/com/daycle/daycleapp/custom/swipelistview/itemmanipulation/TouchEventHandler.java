package com.daycle.daycleapp.custom.swipelistview.itemmanipulation;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

public interface TouchEventHandler {

    boolean onTouchEvent(@NonNull MotionEvent event);

    boolean isInteracting();

}
