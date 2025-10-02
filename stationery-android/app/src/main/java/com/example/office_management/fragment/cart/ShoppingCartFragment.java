
package com.example.office_management.fragment.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.office_management.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.office_management.activity.MainActivity;
import com.example.office_management.activity.address.ShippingActivity;
import com.example.office_management.activity.product.ProductDetailActivity;
import com.example.office_management.adapter.CartAdapter;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.CartApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private static List<CartResponse> cartList = new ArrayList<>();
    private CartApi cartApi;
    private TextView totalPrice;
    private static TextView textSelectAll;
    private CheckBox checkBoxSelectAll;
    private Button btnCheckout;
    private NestedScrollView scrollView;
    private LinearLayout emptyCartLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_cart);
        totalPrice = rootView.findViewById(R.id.text_total_price);
        checkBoxSelectAll = rootView.findViewById(R.id.checkBox_select_all);
        textSelectAll = rootView.findViewById(R.id.text_select_all);
        btnCheckout = rootView.findViewById(R.id.button_checkout);
        scrollView = rootView.findViewById(R.id.scrollView);
        emptyCartLayout = rootView.findViewById(R.id.empty_cart_layout);
        // Thêm nút Checkout
        btnCheckout.setOnClickListener(v -> {
            // Lấy danh sách các mục được chọn
            List<CartResponse> selectedItems = new ArrayList<>();
            for (CartResponse cart : cartList) {
                if (cart.isSelected()) {
                    selectedItems.add(cart);
                }
            }
            if (selectedItems.isEmpty()) return;

            // Lấy tổng giá
            int total = adapter.calculateTotal();

            // Tạo Intent để chuyển sang CheckoutActivity
            Intent intent = new Intent(getContext(), ShippingActivity.class);
            intent.putExtra("selectedItems", (Serializable) selectedItems);
            intent.putExtra("totalPrice", total);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartApi = BaseURL.getUrl(requireContext()).create(CartApi.class);

        getCart();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCart();
        updateCartBadgeInActivity();
    }

    private void getCart() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        String authHeader = "Bearer " + token;

        cartApi.viewCart(authHeader).enqueue(new Callback<ApiResponse<List<CartResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CartResponse>>> call, Response<ApiResponse<List<CartResponse>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    cartList = response.body().getResult();
                    updateCartBadgeInActivity();

                    if (cartList.isEmpty()) {
                        scrollView.setVisibility(View.GONE);
                        emptyCartLayout.setVisibility(View.VISIBLE);
                    } else {
                        scrollView.setVisibility(View.VISIBLE);
                        emptyCartLayout.setVisibility(View.GONE);

                        adapter = new CartAdapter(requireContext(), cartList);

                        adapter.setOnItemClickListener(cart -> {
                            if (cart != null && cart.getSlug() != null) {
                                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                                intent.putExtra("slug", cart.getSlug());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                            }
                        });

                        adapter.setOnCartCheckedChangeListener(total -> {
                            updateTotalPrice();
                            toggleCheckoutButton();
                        });

                        adapter.setOnSelectAllStateChangeListener(isAllSelected -> checkBoxSelectAll.setChecked(isAllSelected));
                        recyclerView.setAdapter(adapter);

                        updateTotalPrice();

                        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            for (CartResponse cart : cartList) {
                                cart.setSelected(isChecked);
                            }
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                            toggleCheckoutButton();
                        });

                        updateSelectAllText();
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CartResponse>>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi khi kết nối server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalPrice() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (adapter != null && cartList != null && !cartList.isEmpty()) {
            int total = adapter.calculateTotal();
            totalPrice.setText(String.format("Total: %sđ", formatter.format(total)));
        }
    }

    public static void updateSelectAllText() {
        String text = "Select all ( " + cartList.size() + " items )";
        textSelectAll.setText(text);
    }

    private void toggleCheckoutButton() {
        boolean hasSelected = false;
        for (CartResponse c : cartList) {
            if (c.isSelected()) {
                hasSelected = true;
                break;
            }
        }
        btnCheckout.setEnabled(hasSelected);
    }

    private void updateCartBadgeInActivity() {
        if (getActivity() instanceof MainActivity) {
            int totalQuantity = cartList.size();
            ((MainActivity) getActivity()).updateCartBadge(totalQuantity);
        }
    }

}
