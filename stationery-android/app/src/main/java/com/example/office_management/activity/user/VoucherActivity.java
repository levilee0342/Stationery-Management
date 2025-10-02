package com.example.office_management.activity.user;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.adapter.VoucherAdapter;
import com.example.office_management.api.UserPromotionApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.model.UserPromotion;
import com.example.office_management.retrofit2.BaseURL;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ImageButton btnBack, btnCart, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        recyclerView = findViewById(R.id.recyclerView_voucher);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btnHome);
        btnCart = findViewById(R.id.btnCart);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(VoucherActivity.this, MainActivity.class);
            intent.putExtra("openHome", true); // Gửi cờ mở Home
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(VoucherActivity.this, MainActivity.class);
            intent.putExtra("openCart", true); // Gửi thông tin muốn mở giỏ hàng
            startActivity(intent);
        });

        loadUserPromotions();
    }

    private void loadUserPromotions() {

        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        UserPromotionApi userPromotionApi = BaseURL.getUrl(this).create(UserPromotionApi.class);
        Call<ApiResponse<List<UserPromotion>>> call = userPromotionApi.getUserPromotions(authHeader);

        call.enqueue(new Callback<ApiResponse<List<UserPromotion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserPromotion>>> call, Response<ApiResponse<List<UserPromotion>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserPromotion> vouchers = response.body().getResult();

                    VoucherAdapter adapter = new VoucherAdapter(VoucherActivity.this, vouchers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(VoucherActivity.this));
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(VoucherActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserPromotion>>> call, Throwable t) {
                Toast.makeText(VoucherActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

}