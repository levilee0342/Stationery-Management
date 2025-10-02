package com.example.office_management.firebase;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.office_management.R;
import com.example.office_management.activity.user.NotificationActivity;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.DeviceTokenRequest;
import com.example.office_management.retrofit2.BaseURL;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.pm.PackageManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String deviceToken) {
        super.onNewToken(deviceToken);
        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            Log.w("FCM", "Received null or empty token");
            return;
        }
        Log.d("FCM", "New token: " + deviceToken);
        Log.d("FCM", "Token length: " + deviceToken.length());
        sendTokenToServer(deviceToken);
    }

    private void sendTokenToServer(String deviceToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String authHeader = "Bearer " + token;
        Log.d("FCM", "JWT token: " + token);

        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            Log.w("FCM", "User ID not found in SharedPreferences");
            return;
        }
        Log.d("FCM", "Sending token for userId: " + userId);

        if (deviceToken.length() < 100) {
            Log.w("FCM", "Invalid token length: " + deviceToken.length());
            return;
        }

        UserApi userApi = BaseURL.getUrl(this).create(UserApi.class);
        DeviceTokenRequest request = new DeviceTokenRequest(deviceToken);
        Call<Void> call = userApi.updateDeviceToken(authHeader ,userId, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Token sent to server successfully for userId: " + userId);
                } else {
                    Log.w("FCM", "Failed to send token: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.w("FCM", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("FCM", "Error reading response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Error sending token", t);
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Thông báo";
        String message = "Nội dung";

        // Xử lý notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }
        // Xử lý data payload
        else if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
        }

        showNotification(title, message);
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // ✅ Gắn PendingIntent vào đây

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Permission", "Notification permission not granted");
                return;
            }
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

}