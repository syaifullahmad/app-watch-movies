package com.lazday.youtubeplayer.retrofit;

import com.lazday.youtubeplayer.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;
    public static Retrofit getUrl(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.readTimeout(10, TimeUnit.SECONDS);
//        builder.connectTimeout(30, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

//        builder.addInterceptor(chain -> {
//            Request request = chain.request().newBuilder()
//                    .addHeader("userkey", BuildConfig.USER_KEY)
//                    .addHeader("passkey", BuildConfig.PASS_KEY)
//                    .build();
//            return chain.proceed(request);
//        });

        OkHttpClient client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

}
