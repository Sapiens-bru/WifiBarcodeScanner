package ru.icash24.vladimir.wifibarcodescanner;

import retrofit2.Retrofit;

/**
 * Created by Vladimir on 25.10.2017.
 */

public class RetrofitClass {
    static final String BASE_URL = "http://37.193.129.153/service/hs/";

    public static RetrofitIterface getApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        RetrofitIterface RetInt = retrofit.create(RetrofitIterface.class);
        return RetInt;}
}
