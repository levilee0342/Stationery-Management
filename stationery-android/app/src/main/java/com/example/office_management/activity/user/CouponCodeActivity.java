package com.example.office_management.activity.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.office_management.R;
import com.example.office_management.adapter.CouponAdapter;
import com.example.office_management.api.UserPromotionApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.model.Promotion;
import com.example.office_management.model.UserPromotion;
import com.example.office_management.retrofit2.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponCodeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnApply;
    private EditText edtCode;
    private List<UserPromotion> couponList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);
        recyclerView = findViewById(R.id.recyclerView_promo_code);
        btnApply = findViewById(R.id.btnApply);
        edtCode = findViewById(R.id.edtCode);
        ImageButton btnBack = findViewById(R.id.btn_back);

        loadUserPromotions();

        btnBack.setOnClickListener(v -> onBackPressed());
        btnApply.setOnClickListener(v -> {
            String code = edtCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(CouponCodeActivity.this, "Please enter a code", Toast.LENGTH_SHORT).show();
            } else {
                applyPromoCode(code);
            }
        });

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
                    couponList = response.body().getResult();

                    int totalPrice = getIntent().getIntExtra("totalPrice", 0);
                    CouponAdapter adapter = new CouponAdapter(CouponCodeActivity.this, couponList, totalPrice, userPromotion -> {
                        // Xử lý khi nhấn nút Apply
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedPromotion", userPromotion);
                        resultIntent.putExtra("userPromotionId", userPromotion.getUserPromotionId());
                        setResult(RESULT_OK, resultIntent);
                        finish();

                        //Toast.makeText(CouponCodeActivity.this, "Đã áp dụng mã: " + userPromotion.getPromotion().getPromoCode(), Toast.LENGTH_SHORT).show();
                    });

                    // Thiết lập RecyclerView ngoài callback của nút Apply
                    recyclerView.setLayoutManager(new LinearLayoutManager(CouponCodeActivity.this));
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(CouponCodeActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserPromotion>>> call, Throwable t) {
                Toast.makeText(CouponCodeActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyPromoCode(String code) {
        for (UserPromotion userPromotion : couponList) {
            Promotion promo = userPromotion.getPromotion();
            if (promo.getPromoCode().equalsIgnoreCase(code)) {
                // Mã hợp lệ
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedPromotion", userPromotion);
                resultIntent.putExtra("userPromotionId", userPromotion.getUserPromotionId());
                setResult(RESULT_OK, resultIntent);
                finish();

                //Toast.makeText(this, "Áp dụng mã: " + code, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // Không tìm thấy mã hợp lệ
        Toast.makeText(this, "Mã khuyến mãi không hợp lệ", Toast.LENGTH_SHORT).show();
    }

}