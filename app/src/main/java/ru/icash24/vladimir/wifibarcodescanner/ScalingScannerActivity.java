package ru.icash24.vladimir.wifibarcodescanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

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
    private long storedtime;
    private String storedbarcode = "0000000000000";
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scaling_scanner);
        setTitle("Wifi Barcode Scanner");
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
        List<BarcodeFormat> MY_FORMATS = new ArrayList();
        super.onResume();
        mScannerView.setResultHandler(this);
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        mScannerView.setAspectTolerance(0.2f);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        MY_FORMATS.clear();
        MY_FORMATS.add(BarcodeFormat.EAN_13);
        mScannerView.setFormats(MY_FORMATS);
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
        long currentTime= System.currentTimeMillis();
        String currentbarcode = rawResult.getText().toString();

//        if (currentbarcode.equals(storedbarcode)){
//            if (currentTime-storedtime<1000){
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mScannerView.resumeCameraPreview(ScalingScannerActivity.this);
//                    }
//                }, 0);
//                return;
//            }
//        }
        Log.v("frags",rawResult.getBarcodeFormat().toString());
        storedbarcode=currentbarcode;
        storedtime = currentTime;
        final MediaPlayer mp_scan = MediaPlayer.create(this, R.raw.beepscan);
        mp_scan.start();
        final MediaPlayer mp_sucsess = MediaPlayer.create(this, R.raw.beepsucsess);
        final MediaPlayer mp_error = MediaPlayer.create(this, R.raw.beeperror);

        retInt = RetrofitClass.getApi();
        url = "http://"+host_ip+":4242/";
        retInt.getData(url,currentbarcode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    mp_sucsess.start();
                }else {
                    mp_error.start();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mp_error.start();
            }
        });
        this.finish();


//      Handler handler = new Handler();
//      handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ScalingScannerActivity.this);
//            }
//        }, 0);
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }
}