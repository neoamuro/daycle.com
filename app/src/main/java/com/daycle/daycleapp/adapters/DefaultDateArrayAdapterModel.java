package com.daycle.daycleapp.adapters;

import java.util.Date;

/**
 * Created by neoam on 2016-08-24.
 */
public class DefaultDateArrayAdapterModel {
    public String code;
    public String title;
    public Date date;
    public Object tag;


    public DefaultDateArrayAdapterModel(String code, String title){
        this(code, title, null, null);
    }

    public DefaultDateArrayAdapterModel(String code, String title, Date date){
        this(code, title, date, null);
    }

    public DefaultDateArrayAdapterModel(String code, String title, Date date, Object tag){
        this.code = code;
        this.title = title;
        this.date = date;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return title;
    }
}
