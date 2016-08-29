package com.daycle.daycleapp.adapters;

/**
 * Created by neoam on 2016-08-24.
 */
public class NavMenuArrayAdapterModel {
    public String code;
    public String title;
    public boolean isChecked;
    public int icon;
    public Object tag;

    public NavMenuArrayAdapterModel(String code, String title){
        this(code, title, 0, false, null);
    }

    public NavMenuArrayAdapterModel(String code, String title, int icon){
        this(code, title, icon, false, null);
    }

    public NavMenuArrayAdapterModel(String code, String title, int icon, boolean isChecked){
        this(code, title, icon, isChecked, null);
    }

    public NavMenuArrayAdapterModel(String code, String title, int icon, boolean isChecked, Object tag){
        this.code = code;
        this.title = title;
        this.isChecked = isChecked;
        this.icon = icon;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return title;
    }
}
