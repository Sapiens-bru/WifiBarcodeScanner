package ru.icash24.vladimir.wifibarcodescanner;

import retrofit2.Retrofit;

/**
 * Created by Vladimir on 25.10.2017.
 */

public class RetrofitClass {
    public static final String BASE_URL = "http://192.168.1.44:4242";

    public static RetrofitIterface getApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        RetrofitIterface RetInt = retrofit.create(RetrofitIterface.class);
        return RetInt;}
}
