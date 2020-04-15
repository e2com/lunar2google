package com.nari.lunar2google;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Activity activity;
    String TAG = "BackPressCloseHandler";

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
        backKeyPressedTime = 0;
    }

    public void onBackPressed() {

        Log.e(TAG, "onBackPressed() Start -------------------------------------" + System.currentTimeMillis()) ;
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
        }
        Log.e(TAG, "onBackPressed() End -------------------------------------" + System.currentTimeMillis()) ;
    }

    private void showGuide() {
        // activity 가 아닌 곳에서는 getResources 을 이렇게 사용해야 함.
        Toast.makeText(activity, activity.getResources().getString(R.string.label_finish_apps), Toast.LENGTH_LONG).show();
    }
}
