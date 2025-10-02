package com.example.office_management.activity.checkout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.adapter.CartAdapter;
import com.example.office_management.api.OrderApi;
import com.example.office_management.dto.request.order.PurchaseOrderProductRequest;
import com.example.office_management.dto.request.order.PurchaseOrderRequest;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.dto.response.MomoResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalAmount;
    private Button btnConfirm;
    private List<CartResponse> selectedItems;
    private AddressResponse selectedAddress;
    private OrderApi orderApi;
    private int totalAmount;
    private String userPromotionId, note, lastOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_order);

        recyclerView = findViewById(R.id.recyclerView_product_list);
        ImageButton btnBack = findViewById(R.id.btn_back);
        tvTotalAmount = findViewById(R.id.total_amount);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedItems = (List<CartResponse>) intent.getSerializableExtra("selectedItems");
        selectedAddress = (AddressResponse) intent.getSerializableExtra("selectedAddress");
        note = intent.getStringExtra("customer_note");

        totalAmount = intent.getIntExtra("totalAmount", 0);
        userPromotionId = intent.getStringExtra("userPromotionId");
        String paymentMethod = intent.getStringExtra("paymentMethod");

        // Hiển thị danh sách sản phẩm lên RecyclerView với VIEW_MODE
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckOrderActivity.this));
        CartAdapter adapter = new CartAdapter(CheckOrderActivity.this, selectedItems, CartAdapter.VIEW_MODE);
        recyclerView.setAdapter(adapter);

        // Hiển thị totalAmount
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalAmount.setText(String.format("%s đ", formatter.format(totalAmount)));

        setBackground();

        orderApi = BaseURL.getUrl(CheckOrderActivity.this).create(OrderApi.class);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnConfirm.setOnClickListener(v -> {
            if ("MOMO".equalsIgnoreCase(paymentMethod)) {
                createMomoOrderAndRedirect();
            } else if ("CASH".equalsIgnoreCase(paymentMethod)) {
                createCashOrder();
            } else {
                Toast.makeText(this, "Phương thức thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastOrderId != null && !lastOrderId.isEmpty()) {
            checkMomoTransactionStatus(lastOrderId);
        }
    }

    private void setBackground(){
        findViewById(R.id.step1Icon).setVisibility(View.VISIBLE);
        findViewById(R.id.step1Number).setVisibility(View.GONE);

        // Bước 2
        findViewById(R.id.step2Icon).setVisibility(View.VISIBLE);
        findViewById(R.id.step2Number).setVisibility(View.GONE);
        findViewById(R.id.step2Frame).setBackgroundResource(R.drawable.bg_step_active);

        // Bước 3
        findViewById(R.id.step3Icon).setVisibility(View.GONE);
        findViewById(R.id.step3Frame).setBackgroundResource(R.drawable.bg_step_active);

        // Line màu xanh
        findViewById(R.id.line1).setBackgroundColor(Color.parseColor("#4CAF50"));
        findViewById(R.id.line2).setBackgroundColor(Color.parseColor("#4CAF50"));
    }

    private PurchaseOrderRequest buildPurchaseOrderRequest() {
        List<PurchaseOrderProductRequest> orderDetails = new ArrayList<>();
        for (CartResponse item : selectedItems) {
            String productPromotionId = null;
            if (item.getProductPromotion() != null && !item.getProductPromotion().isEmpty()) {
                productPromotionId = item.getProductPromotion().get(0).getProductPromotionId();
            }
            orderDetails.add(new PurchaseOrderProductRequest(
                    item.getProductDetailId(),
                    item.getQuantity(),
                    productPromotionId
            ));
        }

        return new PurchaseOrderRequest(
                orderDetails,
                userPromotionId,
                selectedAddress.getRecipient(),
                selectedAddress.getAddressId(),
                note,
                null
        );
    }

    private void createCashOrder() {
        PurchaseOrderRequest request = buildPurchaseOrderRequest();
        Gson gson = new Gson();
        String json = gson.toJson(request);
        Log.d("PurchaseOrderRequest", json);

        String authToken = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", "");

        Call<ApiResponse<Void>> call = orderApi.createOrder(authToken, request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(CheckOrderActivity.this, "Đặt hàng thành công (Thanh toán tiền mặt)!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckOrderActivity.this, MainActivity.class);
                    intent.putExtra("openHome", true); // Gửi cờ mở Home
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckOrderActivity.this, "Lỗi đặt hàng (CASH): " + (response.body() != null ? response.body().getMessage() : "Không rõ lỗi"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(CheckOrderActivity.this, "Lỗi kết nối khi đặt hàng tiền mặt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMomoOrderAndRedirect() {
        PurchaseOrderRequest request = buildPurchaseOrderRequest();

        // Log request nếu cần debug
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        Log.d("MOMO_API", "Request JSON: " + jsonRequest);

        String authToken = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", "");

        Call<ApiResponse<MomoResponse>> call = orderApi.createOrderWithMomo(authToken, request);
        call.enqueue(new Callback<ApiResponse<MomoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MomoResponse>> call, Response<ApiResponse<MomoResponse>> response) {
                btnConfirm.setEnabled(true);
                Log.d("MOMO_API", "Response code: " + response.code());
                if (response.body() != null) {
                    Log.d("MOMO_API", "Response body: " + gson.toJson(response.body()));
                } else {
                    Log.e("MOMO_API", "Body is null");
                }

                if (response.isSuccessful() && response.body() != null) {
                    MomoResponse momoResponse = response.body().getResult();
                    if (momoResponse.getResultCode() == 0) {
                        lastOrderId = momoResponse.getOrderId();
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit().putString("lastOrderId", lastOrderId).apply();
                        openMomoPayUrl(momoResponse);
                    } else {
                        String message = momoResponse.getMessage();
                        if (momoResponse.getResultCode() == 1000) {
                            message = "Lỗi hệ thống MoMo, vui lòng thử lại sau";
                        } else if (momoResponse.getResultCode() == 1006) {
                            message = "Giao dịch đã bị hủy";
                        }
                        Toast.makeText(CheckOrderActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CheckOrderActivity.this, "Lỗi khi gọi API MoMo", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MomoResponse>> call, Throwable t) {
                btnConfirm.setEnabled(true);
                Toast.makeText(CheckOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMomoPayUrl(MomoResponse momoResponse) {
        if (momoResponse.getPayUrl() == null || momoResponse.getPayUrl().isEmpty()) {
            Toast.makeText(this, "Không thể mở liên kết thanh toán. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thanh toán MoMo");
        builder.setMessage("Bạn có muốn mở trang thanh toán MoMo?");

        builder.setPositiveButton("Mở liên kết", (dialog, which) -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(momoResponse.getPayUrl()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void checkMomoTransactionStatus(String orderId) {
        String authToken = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", "");

        Call<ApiResponse<MomoResponse>> call = orderApi.transactionStatus(authToken, orderId, null);
        call.enqueue(new Callback<ApiResponse<MomoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MomoResponse>> call, Response<ApiResponse<MomoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MomoResponse result = response.body().getResult();
                    if (result.getResultCode() == 0 ) {
                        Toast.makeText(CheckOrderActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        // Điều hướng về trang xác nhận đơn hàng hoặc home
                        Intent intent = new Intent(CheckOrderActivity.this, MainActivity.class);
                        intent.putExtra("openHome", true); // Gửi cờ mở Home
                        intent.putExtra("orderId", orderId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        // Xóa lastOrderId sau khi thành công
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit().remove("lastOrderId").apply();
                    } else {

                        Toast.makeText(CheckOrderActivity.this, "Thanh toán chưa thành công: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckOrderActivity.this, "Không kiểm tra được trạng thái thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MomoResponse>> call, Throwable t) {
                Toast.makeText(CheckOrderActivity.this, "Lỗi kết nối khi kiểm tra giao dịch", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
