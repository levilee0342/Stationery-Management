package com.example.office_management.activity.address;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.checkout.PaymentActivity;
import com.example.office_management.adapter.AddressAdapter;
import com.example.office_management.api.AddressApi;
import com.example.office_management.decoration.SwipeHelper;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnShipConfirm;
    private AddressAdapter addressAdapter;
    private AddressApi addressApi;
    private List<AddressResponse> addressList = new ArrayList<>();
    private TextView tvShipAddress;
    private ItemTouchHelper itemTouchHelper;
    private List<CartResponse> selectedItems;
    private int totalPrice;
    private EditText edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        recyclerView = findViewById(R.id.recyclerView_address);
        btnShipConfirm = findViewById(R.id.btnShipAddress);
        ImageButton btnBack = findViewById(R.id.btn_back);
        tvShipAddress = findViewById(R.id.tvShipAddress);
        edtNote = findViewById(R.id.edt_customer_note);

        addressApi = BaseURL.getUrl(this).create(AddressApi.class);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String authToken = prefs.getString("token", null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedItems = (List<CartResponse>) intent.getSerializableExtra("selectedItems");
        totalPrice = intent.getIntExtra("totalPrice", 0);

        // Khởi tạo AddressAdapter
        addressAdapter = new AddressAdapter(this, addressList);
        recyclerView.setAdapter(addressAdapter);

        // Gắn ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelper(addressAdapter, addressApi, this, authToken));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Xử lý sự kiện chọn địa chỉ
        addressAdapter.setOnItemClickListener(address -> {
            // Lưu địa chỉ được chọn (có thể không cần nếu bạn chỉ cần địa chỉ tại thời điểm xác nhận)
        });

        btnShipConfirm.setOnClickListener(v -> {
            // Lấy địa chỉ được chọn
            AddressResponse selectedAddress = null;
            if (!addressList.isEmpty() && addressAdapter.getSelectedPosition() >= 0) {
                selectedAddress = addressAdapter.getItem(addressAdapter.getSelectedPosition());
            }
            String note = edtNote.getText().toString().trim();
            // Tạo Intent để chuyển sang PaymentActivity
            Intent paymentIntent = new Intent(ShippingActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("selectedItems", (Serializable) selectedItems);
            paymentIntent.putExtra("totalPrice", totalPrice);
            paymentIntent.putExtra("selectedAddress", (Serializable) selectedAddress);
            paymentIntent.putExtra("customer_note", note);
            startActivity(paymentIntent);
        });

        tvShipAddress.setOnClickListener(v -> {
            Intent addressIntent = new Intent(ShippingActivity.this, AddressActivity.class);
            startActivity(addressIntent);
        });

        btnBack.setOnClickListener(v -> onBackPressed());

        getAddress();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAddress();
    }

    private void getAddress(){
        SharedPreferences prefs = ShippingActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        Log.d("Shipping Activity", "Token: " + token);
        if (token == null) {
            Toast.makeText(ShippingActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String authHeader = "Bearer " + token;

        addressApi.getAllMyAddresses(authHeader).enqueue(new Callback<ApiResponse<List<AddressResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AddressResponse>>> call, Response<ApiResponse<List<AddressResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AddressResponse> newList = response.body().getResult();
                    addressAdapter.updateData(newList); // chỉ cập nhật dữ liệu

                } else {
                    Toast.makeText(ShippingActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressResponse>>> call, Throwable t) {
                Toast.makeText(ShippingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
