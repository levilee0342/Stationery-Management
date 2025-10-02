package com.example.office_management.fragment.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.activity.auth.ChangePasswordActivity;
import com.example.office_management.activity.user.UserInforActivity;
import com.example.office_management.activity.order.OrderHistoryActivity;
import com.example.office_management.activity.user.VoucherActivity;
import com.example.office_management.api.LoginApi;
import com.example.office_management.api.OrderApi;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.UserResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.gson.Gson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountUserFragment extends Fragment {
    private TextView tvFullName;
    private ImageView imageAvatar, imagePending, imageProcessing, imageDelivery, imageComplete;
    private LinearLayout llPending, llProcessing, llDelivering, llComplete;
    private UserApi userApi;
    private LoginApi loginApi;
    private OrderApi orderApi;
    private Button buttonLogout, btnChangePassword;
    private RelativeLayout rlMyVoucher, rlWishList, rlAccountInformation, rlSupport, rlMyOrders;
    private String currentUserId, email;

    public AccountUserFragment() {
        super(R.layout.fragment_account_user);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFullName = view.findViewById(R.id.tvUserName);
        imageAvatar = view.findViewById(R.id.image_avatar);
        buttonLogout = view.findViewById(R.id.button_logout);
        btnChangePassword = view.findViewById(R.id.button_change_password);
        rlMyVoucher = view.findViewById(R.id.rl_my_voucher);
        rlWishList = view.findViewById(R.id.rl_wish_list);
        rlSupport = view.findViewById(R.id.rl_support);
        rlAccountInformation = view.findViewById(R.id.rl_account);
        rlMyOrders = view.findViewById(R.id.layout_my_oders);
        llPending = view.findViewById(R.id.ll_pending);
        llProcessing = view.findViewById(R.id.ll_processing);
        llDelivering = view.findViewById(R.id.ll_delivering);
        llComplete = view.findViewById(R.id.ll_complete);
        imagePending = view.findViewById(R.id.iv_pending_icon);
        imageProcessing = view.findViewById(R.id.iv_processing_icon);
        imageDelivery = view.findViewById(R.id.iv_delivery_icon);
        imageComplete = view.findViewById(R.id.iv_complete_icon);

        userApi = BaseURL.getUrl(getContext()).create(UserApi.class);
        loginApi = BaseURL.getUrl(getContext()).create(LoginApi.class);
        orderApi = BaseURL.getUrl(getContext()).create(OrderApi.class);

        loadUser();
        loadOrderStatusStatistics();

        buttonLogout.setOnClickListener(v -> {
            logout();
        });
        rlAccountInformation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserInforActivity.class);
            startActivity(intent);
        });
        rlMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OrderHistoryActivity.class);
            startActivity(intent);
        });
        rlMyVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VoucherActivity.class);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        llPending.setOnClickListener(v -> openOrderHistoryWithTab(1));
        llProcessing.setOnClickListener(v -> openOrderHistoryWithTab(2));
        llDelivering.setOnClickListener(v -> openOrderHistoryWithTab(3));
        llComplete.setOnClickListener(v -> openOrderHistoryWithTab(0));

    }
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi Fragment được hiển thị
        Log.d("AccountUserFragment", "onResume: Loading user data...");
        loadUser();
        loadOrderStatusStatistics();
    }
    private void loadUser() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        userApi.getUserInfo(authHeader).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("USER_INFO_DEBUG", "Response: " + new Gson().toJson(response.body()));

                    UserResponse user = response.body().getResult();
                    tvFullName.setText(user.getFullName());
                    currentUserId = user.getUserId();

                    Glide.with(requireContext())
                            .load(user.getAvatar())
                            .circleCrop() // <--- THÊM DÒNG NÀY
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(imageAvatar);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                Log.e("AccountUserFragment", "Error loading user", t);
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        loginApi = BaseURL.getUrl(getContext()).create(LoginApi.class);

        loginApi.logout(authHeader).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("token");
                editor.apply();

                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onLogout();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Logout error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOrderHistoryWithTab(int tabIndex) {
        Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
        intent.putExtra("selectedTab", tabIndex);
        startActivity(intent);
    }

    private void loadOrderStatusStatistics() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String userId = currentUserId;

        orderApi.getOrderStatusStatistics(token, userId).enqueue(new Callback<ApiResponse<Map<PurchaseOrderResponse.Status, Long>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<PurchaseOrderResponse.Status, Long>>> call, Response<ApiResponse<Map<PurchaseOrderResponse.Status, Long>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<PurchaseOrderResponse.Status, Long> stats = response.body().getResult();
                    if (stats != null) {
                        setBadgeCount(imagePending, stats.getOrDefault(PurchaseOrderResponse.Status.PENDING, 0L));
                        setBadgeCount(imageProcessing, stats.getOrDefault(PurchaseOrderResponse.Status.PROCESSING, 0L));
                        setBadgeCount(imageDelivery, stats.getOrDefault(PurchaseOrderResponse.Status.SHIPPING, 0L));
                        setBadgeCount(imageComplete, stats.getOrDefault(PurchaseOrderResponse.Status.COMPLETED, 0L));
                    }
                } else {
                    // Xử lý lỗi hoặc không có dữ liệu
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<PurchaseOrderResponse.Status, Long>>> call, Throwable t) {
                // Xử lý thất bại gọi API
            }
        });
    }

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    private void setBadgeCount(ImageView imageView, long count) {
        if (!isAdded()) return;
        BadgeDrawable badgeDrawable = BadgeDrawable.create(requireContext());
        if (count > 0) {
            badgeDrawable.setNumber((int) count);
            BadgeUtils.attachBadgeDrawable(badgeDrawable, imageView, null);
        } else {
            // Nếu số lượng 0, có thể remove badge hoặc không attach
            BadgeUtils.detachBadgeDrawable(badgeDrawable, imageView);
        }
    }

}