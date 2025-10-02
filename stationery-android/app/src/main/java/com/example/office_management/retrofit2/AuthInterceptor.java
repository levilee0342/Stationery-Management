package com.example.office_management.retrofit2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.office_management.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        Log.d("AuthInterceptor", "Token: " + token);

        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(builder.build());

        if (response.code() == 401) {
            // Xoá token cũ
            prefs.edit().remove("token").apply();

            // Xử lý UI trên main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();

                // Tìm BottomNavigationView từ Activity và chuyển sang LoginFragment
                try {
                    BottomNavigationView nav = ((android.app.Activity) context).findViewById(R.id.account_bottom_navigation);
                    if (nav != null) {
                        nav.setSelectedItemId(R.id.login); // ID của tab login
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return response;
    }
}