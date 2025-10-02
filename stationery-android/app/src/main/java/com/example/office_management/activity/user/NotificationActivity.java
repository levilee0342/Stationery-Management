package com.example.office_management.activity.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.office_management.R;
import com.example.office_management.adapter.NotificationAdapter;
import com.example.office_management.api.NotificationApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.NotificationResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationResponse> notifications = new ArrayList<>();
    private NotificationApi notificationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerView_notifications);
        adapter = new NotificationAdapter(this, notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        notificationApi = BaseURL.getUrl(NotificationActivity.this).create(NotificationApi.class);
        loadNotifications();

    }

    private void loadNotifications() {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        Log.d("DEBUG", "Token: " + token);

        notificationApi.getUserNotifications(authHeader).enqueue(new Callback<ApiResponse<List<NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationResponse>>> call, Response<ApiResponse<List<NotificationResponse>>> response) {
                Log.d("DEBUG", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DEBUG", "Response body: " + new Gson().toJson(response.body()));

                    notifications.clear();
                    notifications.addAll(response.body().getResult());
                    adapter.notifyDataSetChanged();

                    Log.d("DEBUG", "Notifications loaded: " + notifications.size());
                } else {
                    Log.e("DEBUG", "Failed to load notifications. Response code: " + response.code());
                    Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationResponse>>> call, Throwable t) {
                Log.e("DEBUG", "Error onFailure: " + t.getMessage(), t);
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
