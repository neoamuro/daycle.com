package com.daycle.daycleapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;

import com.daycle.daycleapp.R;
import com.daycle.daycleapp.applications.App;

/**
 * Created by neoam on 2016-08-17.
 */
public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.custom_progress_dialog); // 다이얼로그에 박을 레이아웃

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                App.loadingCnt = 0;
                //App.showToast("DialogCancel");
            }
        });
    }
}
