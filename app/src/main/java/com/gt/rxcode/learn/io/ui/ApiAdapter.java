package com.gt.rxcode.learn.io.ui;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAdapter {
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.connectTimeout(1, TimeUnit.MINUTES);
            httpClient.writeTimeout(1, TimeUnit.MINUTES);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.END_POINT)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

}
