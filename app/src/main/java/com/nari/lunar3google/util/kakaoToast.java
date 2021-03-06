package com.nari.lunar3google.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nari.lunar3google.R;

/**
 * @author leoshin
 * Created by leoshin on 15. 6. 23..
 */
public class kakaoToast {

    static private AdView mAdView;
    static String TAG = "kakaoToast";


    public static void kakaoAlertDialog(Context context, String body) {

        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_toast, null);
        TextView text = v.findViewById(R.id.message);
        text.setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("1")
                .setMessage("2")
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.create();
        builder.show();
    }

    public static Toast makeToast(Context context, String body, int duration){
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_toast, null);
        TextView text = v.findViewById(R.id.message);
        text.setText(body);

        MobileAds.initialize(context, context.getResources().getString(R.string.admob_app_id));
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "onAdLoaded");
            }
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed");
            }

            @Override
            public void onAdOpened() {
                Log.e(TAG, "onAdOpened");
            }
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad=" + i);
            }
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e(TAG, "onAdClicked");
            }
            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(TAG, "onAdImpression");
            }
        });

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);
        toast.setDuration(duration);
        return toast;
    }
}