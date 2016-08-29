package com.daycle.daycleapp.custom;

import android.support.annotation.NonNull;
import android.widget.BaseAdapter;

import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.SwipeUndoAdapter;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.SwipeUndoTouchListener;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback;
import com.daycle.daycleapp.custom.swipelistview.util.ListViewWrapper;

/**
 * Created by neoam on 2016-08-19.
 */
public class CustomSimpleSwipeUndoAdapter extends SwipeUndoAdapter {

    public CustomSimpleSwipeUndoAdapter(@NonNull BaseAdapter adapter, @NonNull UndoCallback dismissCallback) {
        super(adapter, dismissCallback);

    }

    @Override
    public void setListViewWrapper(@NonNull ListViewWrapper listViewWrapper) {
        super.setListViewWrapper(listViewWrapper);
    }

    @Override
    public void setSwipeUndoTouchListener(@NonNull SwipeUndoTouchListener swipeUndoTouchListener) {
        super.setSwipeUndoTouchListener(swipeUndoTouchListener);
    }


}
