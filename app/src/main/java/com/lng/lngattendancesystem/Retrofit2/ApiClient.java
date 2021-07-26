package com.lng.lngattendancesystem.Retrofit2;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

//    // PRODUCTION SERVER
     private static String BASE_URL = "http://52.183.137.54:8080/attnd-api-gateway-service/api/";

    // DEVELOPMENT SERVER
//    private static String BASE_URL = "http://36.255.87.28:8080/attnd-api-gateway-service/api/";
//    private static String BASE_URL = "http://122.166.248.191/attnd-api-gateway-service/api/";
//private static String BASE_URL = "http://122.166.248.191:8080/attnd-api-gateway-service/api/";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    /***
     * OFFICE Dev API
     */

    private static Retrofit retrofit = null;

    public static Retrofit getApiClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;
    }
}
