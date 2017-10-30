package ru.icash24.vladimir.wifibarcodescanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ru.icash24.vladimir.wifibarcodescanner.MainActivity.APP_PREFERENCES;
import static ru.icash24.vladimir.wifibarcodescanner.MainActivity.APP_PREFERENCES_HOST;

public class ScalingScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";

    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private String host_ip;
    SharedPreferences mSettings;
    private RetrofitIterface retInt;
    private String url;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scaling_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_HOST)) {
            host_ip = mSettings.getString(APP_PREFERENCES_HOST, "");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        mScannerView.setAspectTolerance(0.2f);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void handleResult(Result rawResult) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        final Activity CurrentActivity=this;
        retInt = RetrofitClass.getApi();
        url = "http://"+host_ip+":4242/";
        retInt.getData(url,rawResult.getText()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScalingScannerActivity.this);
            }
        }, 2000);
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }
}