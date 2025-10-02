package com.example.office_management.retrofit2;

import android.content.Context;

import com.example.office_management.api.ChatApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class BaseURL {
    //private static final String BASE_URL = "http://192.168.100.41:8080/api/";
    private static final String BASE_URL = "http://192.168.100.228:8080/api/";
    //private static final String BASE_URL = "http://192.168.100.242:8080/api/";
    //private static final String BASE_URL = "http://192.168.1.21:8080/api/";

    private static final String CHAT_BASE_URL = "http://192.168.100.228:5000/";
    public static final String DETECTION_BASE_URL = "http://192.168.100.228:5050/";
    private static Retrofit retrofit;
    private static Retrofit chatRetrofit;

    public static Retrofit getUrl(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    public static ChatApi getChatService() {
        if (chatRetrofit == null) {
            // Tạo HttpLoggingInterceptor để log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Tạo OkHttpClient có interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Khởi tạo Retrofit với client custom
            chatRetrofit = new Retrofit.Builder()
                    .baseUrl(CHAT_BASE_URL)
                    .client(client) // truyền client vào đây
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return chatRetrofit.create(ChatApi.class);
    }
    

}
