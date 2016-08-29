package com.daycle.daycleapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.daycle.daycleapp.custom.calendar.CustomCalendar;

import java.util.ArrayList;

/**
 * Created by neoam on 2016-08-23.
 */
public class InfinitePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<CustomCalendar> items;

    public InfinitePagerAdapter(FragmentManager fm, ArrayList<CustomCalendar> items) {
        super(fm);
        this.items = items;
    }

    public void updateFragList(ArrayList<CustomCalendar> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
