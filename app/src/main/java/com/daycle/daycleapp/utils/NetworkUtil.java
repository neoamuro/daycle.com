package com.daycle.daycleapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.daycle.daycleapp.R;

/**
 * Created by neoam on 2016-08-17.
 */
public class NetworkUtil {

    // 인터넷 연결 확인
    public static boolean CheckNetwork(Context context)
    {
        if(context == null)
            return false;

        ConnectivityManager manager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // 네트워크가 연결되어 있지 않음
        if(networkInfo == null){
            Toast.makeText(context, context.getResources().getString(R.string.err_network), Toast.LENGTH_SHORT).show();
            return false;
        }

        String netname = networkInfo.getTypeName();
        if (netname.toUpperCase().equals("MOBILE") || netname.toUpperCase().equals("WIFI")) {
            return true;
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.err_network), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
