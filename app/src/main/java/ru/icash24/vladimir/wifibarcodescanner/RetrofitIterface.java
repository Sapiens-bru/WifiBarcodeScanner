package ru.icash24.vladimir.wifibarcodescanner;

import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vladimir on 25.10.2017.
 */

public interface RetrofitIterface {
    @GET("scanner?")
    Call<ResponseBody> getData(@Query("barcode") String barcode);
}
