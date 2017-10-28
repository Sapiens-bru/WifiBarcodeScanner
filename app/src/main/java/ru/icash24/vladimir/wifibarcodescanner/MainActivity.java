package ru.icash24.vladimir.wifibarcodescanner;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_HOST = "192.168.0.1";

    private ZXingScannerView mScannerView;
    private RetrofitIterface retInt;
    private String url;
    private String host_ip;
    EditText host_v;

    SharedPreferences mSettings;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, 0);
        }
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(R.layout.activity_main);                // Set the scanner view as the content view
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_HOST)) {
            host_ip = mSettings.getString(APP_PREFERENCES_HOST, "");
            host_v=(EditText) findViewById(R.id.host);
            host_v.setText(host_ip);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        final Activity CurrentActivity=this;
        retInt = RetrofitClass.getApi();

        retInt.getData(url,rawResult.getText()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                   // Toast.makeText(CurrentActivity, "Ушло в 1С: " + rawResult.getText(), Toast.LENGTH_LONG).show();
                } else {
                  //  Toast.makeText(CurrentActivity, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(CurrentActivity, "Исключительная ошибка", Toast.LENGTH_SHORT).show();

            }
        });
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);

    }

    public void ocl_button(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, 0);
        }
        host_v=(EditText) findViewById(R.id.host);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_HOST, host_v.getText().toString());
        editor.apply();
        url = "http://"+host_v.getText().toString()+":4242/";
        setContentView(mScannerView);

    }
}