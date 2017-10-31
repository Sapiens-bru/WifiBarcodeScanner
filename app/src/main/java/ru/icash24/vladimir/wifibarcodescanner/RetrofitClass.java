package ru.icash24.vladimir.wifibarcodescanner;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Vladimir on 25.10.2017.
 */

public class RetrofitClass {
    public static final String BASE_URL = "http://192.168.1.44:4242";

    public static RetrofitIterface getApi() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();

        RetrofitIterface RetInt = retrofit.create(RetrofitIterface.class);
        return RetInt;}
}
