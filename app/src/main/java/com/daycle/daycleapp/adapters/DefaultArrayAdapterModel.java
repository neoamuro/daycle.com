package com.daycle.daycleapp.adapters;

/**
 * Created by neoam on 2016-08-24.
 */
public class DefaultArrayAdapterModel {
    public String code;
    public String title;
    public boolean isChecked;
    public Object tag;

    public DefaultArrayAdapterModel(String code, String title){
        this.code = code;
        this.title = title;
    }

    public DefaultArrayAdapterModel(String code, String title, boolean isChecked){
        this.code = code;
        this.title = title;
        this.isChecked = isChecked;
    }

    public DefaultArrayAdapterModel(String code, String title, boolean isChecked, Object tag){
        this.code = code;
        this.title = title;
        this.isChecked = isChecked;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return title;
    }
}
