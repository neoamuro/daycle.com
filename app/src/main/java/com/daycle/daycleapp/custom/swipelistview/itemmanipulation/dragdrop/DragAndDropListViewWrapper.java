package com.daycle.daycleapp.custom.swipelistview.itemmanipulation.dragdrop;

import android.widget.AbsListView;

import com.daycle.daycleapp.custom.swipelistview.util.ListViewWrapper;

public interface DragAndDropListViewWrapper extends ListViewWrapper {

    void setOnScrollListener(AbsListView.OnScrollListener onScrollListener);

    int pointToPosition(int x, int y);

    int computeVerticalScrollOffset();

    int computeVerticalScrollExtent();

    int computeVerticalScrollRange();
}
