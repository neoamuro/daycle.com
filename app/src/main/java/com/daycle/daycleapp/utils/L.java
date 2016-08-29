package com.daycle.daycleapp.utils;

import android.util.Log;

import com.daycle.daycleapp.applications.App;

/**
 * Created by neoam on 2016-08-17.
 * 로그 유틸
 */
public final class L {

    public final static void d(String m){
        if(App.isLogging){
            if(m != null){
                Log.d(App.TAG, m);
            }
        }
    }
}
