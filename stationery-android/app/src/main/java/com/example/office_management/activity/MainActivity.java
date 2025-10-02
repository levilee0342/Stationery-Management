package com.example.office_management.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.widget.Toast;

import com.example.office_management.R;
import com.example.office_management.activity.search.SearchActivity;
import com.example.office_management.adapter.ViewPagerAdapter;
import com.example.office_management.api.CartApi;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.DeviceTokenRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private SharedPreferences sharedPreferences;
    private CartApi cartApi;
    private UserApi userApi;
    private static final int REQUEST_CODE_NOTIFICATION = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        cartApi = BaseURL.getUrl(this).create(CartApi.class);
        userApi = BaseURL.getUrl(MainActivity.this).create(UserApi.class);
        fetchCartAndUpdateBadge();
        // Lấy token từ SharedPreferences
        String token = sharedPreferences.getString("token", null);
        Log.d("AuthInterceptor", "Token: " + token);

        requestNotificationPermission();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String deviceToken = task.getResult();
                        if (deviceToken == null || deviceToken.trim().isEmpty()) {
                            Log.w("FCM", "Received null or empty token");
                            return;
                        }
                        Log.d("FCM Token", deviceToken);
                        Log.d("FCM", "Token length: " + deviceToken.length());
                        sendTokenToServer(deviceToken);
                    } else {
                        Log.e("FCM", "Failed to get token", task.getException());
                    }
                });

        // Gán trạng thái đăng nhập dựa vào token
        boolean isLoggedIn = (token != null);
        Log.d("AuthInterceptor", "Login: " + isLoggedIn);

        // Khởi tạo adapter với trạng thái đăng nhập
        viewPagerAdapter = new ViewPagerAdapter(this, isLoggedIn);
        viewPager.setAdapter(viewPagerAdapter);

        // Nếu chưa đăng nhập, chuyển đến tab profile (AccountFragment hiển thị LoginFragment)
        // Phải gọi sau khi setAdapter
        if (token == null) {
            // Ép cập nhật adapter để phản ánh trạng thái chưa đăng nhập
            viewPagerAdapter.setLoggedIn(false);
            viewPagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(4, false);
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            viewPager.setCurrentItem(0, false);
        }

        // Xử lý chọn item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.categories:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.chatbot:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.shopping:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.profile:
                    if (isLoggedIn) {
                        viewPager.setCurrentItem(4); // Hiển thị UserInfoFragment
                    } else {
                        viewPager.setCurrentItem(4); // Hiển thị AccountFragment (login)
                    }
                    return true;
            }
            return false;
        });


        // Đồng bộ hóa khi vuốt ngang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.categories);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.chatbot);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.shopping);
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.profile);
                        break;
                }
            }
        });

        Intent intent = getIntent();
        if (intent.getBooleanExtra("openCart", false)) {
            bottomNavigationView.setSelectedItemId(R.id.shopping);
        } else if (intent.getBooleanExtra("openHome", false)) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home); // Mặc định mở Home
        }
    }

    // Phương thức gọi khi đăng nhập thành công
    public void onLoginSuccess(String token) {
        // Lưu trạng thái đăng nhập
        sharedPreferences.edit().putString("token", token).apply(); // Lưu token (giả định bạn có token khi đăng nhập thành công)
        // Cập nhật trạng thái đăng nhập
        viewPagerAdapter.setLoggedIn(true);
        viewPagerAdapter.notifyDataSetChanged();
        // Nếu đang ở trang profile, làm mới nó ngay lập tức
        if (viewPager.getCurrentItem() == 4) {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(4, false);  // giữ ở Profile tab
        }
    }

    // Phương thức gọi khi đăng xuất
    public void onLogout() {
        // Xóa token và trạng thái đăng nhập
        sharedPreferences.edit().remove("token").apply();
        // Cập nhật trạng thái đăng xuất
        viewPagerAdapter.setLoggedIn(false);
        viewPagerAdapter.notifyDataSetChanged();
        // Nếu đang ở trang profile, làm mới nó ngay lập tức
        if (viewPager.getCurrentItem() == 4) {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(4, false);  // quay lại trang login (AccountFragment)
        }
    }

    private void fetchCartAndUpdateBadge() {
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) return;

        String authHeader = "Bearer " + token;

        cartApi.viewCart(authHeader).enqueue(new Callback<ApiResponse<List<CartResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CartResponse>>> call, Response<ApiResponse<List<CartResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartResponse> cartList = response.body().getResult();
                    int totalQuantity = cartList.size();
                    updateCartBadge(totalQuantity); // Gọi hàm cập nhật badge
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CartResponse>>> call, Throwable t) {
                Log.e("Cart", "Error loading cart", t);
            }
        });
    }

    public void updateCartBadge(int totalQuantity) {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.shopping);
        badge.setVisible(totalQuantity > 0);
        badge.setNumber(totalQuantity);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION);
            }
        }
    }

    private void sendTokenToServer(String deviceToken) {
        String userId = sharedPreferences.getString("userId", null);
        String token = sharedPreferences.getString("token", null);
        String authHeader = "Bearer " + token;
        Log.d("FCM", "JWT token: " + token);

        if (userId == null) {
            Log.w("FCM", "User ID not found in SharedPreferences");
            return;
        }
        Log.d("FCM", "Sending token for userId: " + userId);

        if (deviceToken.length() < 100) {
            Log.w("FCM", "Invalid token length: " + deviceToken.length());
            return;
        }

        DeviceTokenRequest request = new DeviceTokenRequest(deviceToken);
        Call<Void> call = userApi.updateDeviceToken(authHeader, userId, request);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted");
            } else {
                Log.w("Permission", "Notification permission denied");
                Toast.makeText(this, "Vui lòng cấp quyền thông báo để nhận cập nhật", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Phương thức này có thể được gọi từ các Fragment khác để kiểm tra trạng thái đăng nhập
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    // Phương thức để hiển thị SearchFragment
    public void showSearchFragment() {
        // Sử dụng Intent để mở SearchActivity
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }


    // Phương thức để ẩn SearchFragment
    public void hideSearchFragment() {
        View overlayContainer = findViewById(R.id.overlay_container);
        if (overlayContainer != null) {
            overlayContainer.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        }
    }
}