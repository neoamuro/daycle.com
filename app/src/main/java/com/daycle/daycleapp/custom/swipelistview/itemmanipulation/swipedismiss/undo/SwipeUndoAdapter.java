/*
 * Copyright 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.daycle.daycleapp.custom.swipelistview.BaseAdapterDecorator;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.DynamicListView;
import com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.DismissableManager;
import com.daycle.daycleapp.custom.swipelistview.util.ListViewWrapper;

/**
 * Adds swipe-undo behaviour to the {@link android.widget.AbsListView}, using a {@link SwipeUndoTouchListener}.
 */
public abstract class SwipeUndoAdapter extends BaseAdapterDecorator {

    /**
     * The {@link SwipeUndoTouchListener} that is set to the {@link android.widget.AbsListView}.
     */
    @Nullable
    private SwipeUndoTouchListener mSwipeUndoTouchListener;

    /**
     * The {@link com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback} that is used.
     */
    @NonNull
    private com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback mUndoCallback;

    private int itemViewResid = 0;

    /**
     * Create a new {@code SwipeUndoAdapter}, decorating given {@link BaseAdapter}.
     *
     * @param baseAdapter  the {@link BaseAdapter} to decorate.
     * @param undoCallback the {@link com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback} that is used.
     */
    protected SwipeUndoAdapter(@NonNull final BaseAdapter baseAdapter, @NonNull final com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback undoCallback) {
        super(baseAdapter);
        mUndoCallback = undoCallback;
    }

    @Override
    public void setListViewWrapper(@NonNull final ListViewWrapper listViewWrapper) {
        super.setListViewWrapper(listViewWrapper);
        mSwipeUndoTouchListener = new SwipeUndoTouchListener(listViewWrapper, mUndoCallback);

        if (!(listViewWrapper.getListView() instanceof DynamicListView)) {
            listViewWrapper.getListView().setOnTouchListener(mSwipeUndoTouchListener);
        }
    }

    /**
     * Sets the {@link DismissableManager} to specify which views can or cannot be swiped.
     *
     * @param dismissableManager {@code null} for no restrictions.
     */
    public void setDismissableManager(@Nullable final DismissableManager dismissableManager) {
        if (mSwipeUndoTouchListener == null) {
            throw new IllegalStateException("You must call setAbsListView() first.");
        }
        mSwipeUndoTouchListener.setDismissableManager(dismissableManager);
    }

    public void setSwipeUndoTouchListener(@NonNull final SwipeUndoTouchListener swipeUndoTouchListener) {
        mSwipeUndoTouchListener = swipeUndoTouchListener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        if (getListViewWrapper() == null) {
            throw new IllegalArgumentException("Call setAbsListView() on this SwipeUndoAdapter before setAdapter()!");
        }
        return super.getView(position, convertView, parent);
    }

    /**
     * Sets the {@link com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback} to use.
     */
    public void setUndoCallback(@NonNull final com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback undoCallback) {
        mUndoCallback = undoCallback;
    }

    @NonNull
    public com.daycle.daycleapp.custom.swipelistview.itemmanipulation.swipedismiss.undo.UndoCallback getUndoCallback() {
        return mUndoCallback;
    }

    /**
     * Performs the undo animation and restores the original state for given {@link View}.
     *
     * @param view the parent {@code View} which contains both primary and undo {@code View}s.
     */
    public void undo(@NonNull final View view) {
        mSwipeUndoTouchListener.undo(view);
    }

    /**
     * Dismisses the {@link View} corresponding to given position. Calling this method has the same effect as manually swiping an item off the screen.
     *
     * @param position the position of the item in the {@link android.widget.ListAdapter}. Must be visible.
     */
    public void dismiss(final int position) {
        mSwipeUndoTouchListener.dismiss(position);
    }
}
