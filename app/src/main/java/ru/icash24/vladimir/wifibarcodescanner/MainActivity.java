package ru.icash24.vladimir.wifibarcodescanner;


import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_HOST = "192.168.0.1";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    private RetrofitIterface retInt;
    private String url;
    private String host_ip;
    EditText host_v;

    SharedPreferences mSettings;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);
        setTitle("Wifi Barcode Scanner");
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_HOST)) {
            host_ip = mSettings.getString(APP_PREFERENCES_HOST, "");
            host_v=(EditText) findViewById(R.id.host);
            host_v.setText(host_ip);
        }
    }



    public void ocl_scan(View v) {
        host_v=(EditText) findViewById(R.id.host);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_HOST, host_v.getText().toString());
        editor.apply();
        launchActivity(ScalingScannerActivity.class);
    }
    public void ocl_check(View v) {
        host_v=(EditText) findViewById(R.id.host);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_HOST, host_v.getText().toString());
        editor.apply();
        final TextView status_v=(TextView) findViewById(R.id.serverstatus);
        status_v.setText("Connecting please wait....");
        retInt = RetrofitClass.getApi();
        url = "http://"+host_v.getText()+":4242/";
        retInt.getData(url,"test").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    status_v.setText("Barcode server ready!");
                    status_v.setBackgroundColor(Color.GREEN);
                }else{
                    status_v.setText("Error code: "+response.code());
                    status_v.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                status_v.setText("Error:"+t.getMessage());
                status_v.setTextColor(Color.BLACK);
                status_v.setBackgroundColor(Color.RED);

            }
        });

    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }
}