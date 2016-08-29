package com.daycle.daycleapp.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.daycle.daycleapp.R;

/**
 * Created by neoam on 2016-08-24.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    final String NS = "http://com.daycleapp.custom";
    final String ATTR = "checkable";

    int checkableId;
    Checkable checkable;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkableId = attrs.getAttributeResourceValue(NS, ATTR, 0);
    }

    @Override
    public boolean isChecked() {
        checkable = (Checkable) findViewById(checkableId);
        if(checkable == null)
            return false;
        return checkable.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked){
            this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        }else{
            this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDefaultBackground));
        }
        checkable = (Checkable) findViewById(checkableId);
        if(checkable == null)
            return;
        checkable.setChecked(checked);
    }

    @Override
    public void toggle() {
        checkable = (Checkable) findViewById(checkableId);
        if(checkable == null)
            return;
        checkable.toggle();
    }
}
