package com.example.office_management.fragment.orderstatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.checkout.CheckOrderActivity;
import com.example.office_management.adapter.CartAdapter;
import com.example.office_management.adapter.PurchaseOrderAdapter;
import com.example.office_management.api.OrderApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;
    private RecyclerView recyclerView;
    private PurchaseOrderAdapter adapter;
    private List<PurchaseOrderResponse> orderList = new ArrayList<>();
    private OrderApi orderApi;
    private String authToken;

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderApi = BaseURL.getUrl(getContext()).create(OrderApi.class);

        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            authToken = token;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PurchaseOrderAdapter(orderList, orderApi, authToken);
        recyclerView.setAdapter(adapter);

        getOrdersByStatus(status);
        return view;
    }

    private void getOrdersByStatus(String status) {
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        orderApi.getUserOrdersByStatus(authHeader, status).enqueue(new Callback<ApiResponse<List<PurchaseOrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PurchaseOrderResponse>>> call, Response<ApiResponse<List<PurchaseOrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PurchaseOrderResponse> orders = response.body().getResult();
                    orderList.clear();
                    orderList.addAll(orders);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PurchaseOrderResponse>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi tải đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
