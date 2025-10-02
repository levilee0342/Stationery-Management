package com.example.office_management.activity.order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.adapter.PurchaseOrderDetailAdapter;
import com.example.office_management.api.OrderApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderDetailResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private OrderApi orderApi;
    private TextView orderIdTextView, purchaseDateTextView, quantityTextView, totalTextView, receiverNameTextView,
            receiverPhoneTextView, receiverAddressTextView, paymentMethodTextView, noteTextView;
    private RecyclerView recyclerViewProduct;
    private PurchaseOrderDetailAdapter purchaseOrderDetailAdapter;
    private List<ProductDetailResponse> productDetail = new ArrayList<>();
    private List<PurchaseOrderDetailResponse> orderDetails = new ArrayList<>();
    private String purchaseOrderId;
    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

        initView();

        // Lấy ID đơn hàng từ Intent
        purchaseOrderId = getIntent().getStringExtra("purchaseOrderId");
        quantity = getIntent().getIntExtra("quantity", 0);
        if (purchaseOrderId == null || purchaseOrderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        orderApi = BaseURL.getUrl(this).create(OrderApi.class);
        loadPurchaseOrderDetails(purchaseOrderId);
    }

    private void initView(){
        orderIdTextView = findViewById(R.id.order_id_text_view);
        purchaseDateTextView = findViewById(R.id.purchase_date_text_view);
        quantityTextView = findViewById(R.id.quantity_text_view);
        totalTextView = findViewById(R.id.total_text_view);
        receiverNameTextView = findViewById(R.id.receiver_name_text_view);
        receiverPhoneTextView = findViewById(R.id.receiver_phone_text_view);
        receiverAddressTextView = findViewById(R.id.receiver_address_text_view);
        paymentMethodTextView = findViewById(R.id.payment_method_text_view);
        noteTextView = findViewById(R.id.note_text_view);
        recyclerViewProduct = findViewById(R.id.recyclerView_product);
    }

    private void loadPurchaseOrderDetails(String purchaseOrderId) {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;
        Call<ApiResponse<PurchaseOrderResponse>> call = orderApi.getPurchaseOrderDetails(authHeader, purchaseOrderId);
        call.enqueue(new Callback<ApiResponse<PurchaseOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PurchaseOrderResponse>> call, Response<ApiResponse<PurchaseOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PurchaseOrderResponse order = response.body().getResult();

                    // Hiển thị thông tin đơn hàng lên giao diện
                    displayOrderDetails(order);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không lấy được thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PurchaseOrderResponse>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi mạng hoặc server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderDetails(PurchaseOrderResponse order) {
        orderIdTextView.setText(order.getPurchaseOrderId());
        Date createdAt = order.getCreatedAt();
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(createdAt);
            purchaseDateTextView.setText(formattedTime);
        } else {
            purchaseDateTextView.setText("unknown");
        }

        // Format amount
        DecimalFormat formatter = new DecimalFormat("#,###");
        quantityTextView.setText(quantity + " item");
        totalTextView.setText(formatter.format(order.getAmount()) + " đ");

        noteTextView.setText(order.getNote());
        receiverNameTextView.setText(order.getAddress().getRecipient());
        receiverAddressTextView.setText(order.getAddress().getAddressName());
        receiverPhoneTextView.setText(order.getAddress().getPhone());

        //paymentMethodTextView;
        productDetail = order.getProductDetails();
        orderDetails = order.getOrderDetails();

        purchaseOrderDetailAdapter = new PurchaseOrderDetailAdapter(productDetail, orderDetails);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProduct.setAdapter(purchaseOrderDetailAdapter);
        purchaseOrderDetailAdapter.notifyDataSetChanged();
    }
}