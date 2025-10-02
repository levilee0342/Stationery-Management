package com.example.office_management.adapter;

import static com.example.office_management.fragment.cart.ShoppingCartFragment.updateSelectAllText;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.office_management.R;

import java.text.DecimalFormat;
import java.util.List;

import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.CartApi;
import com.example.office_management.dto.request.cart.UpdateCartRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartResponse> cartList;
    private CartApi cartApi;
    private Context context;
    private OnCartCheckedChangeListener onCartCheckedChangeListener;
    private OnSelectAllStateChangeListener onSelectAllStateChangeListener;
    private OnItemClickListener onItemClickListener;
    private final int mode; // Chế độ hiển thị
    public static final int EDIT_MODE = 0; // Chế độ chỉnh sửa (ShoppingCartFragment)
    public static final int VIEW_MODE = 1; // Chế độ chỉ xem (CheckOrderActivity)
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public interface OnItemClickListener {
        void onItemClick(CartResponse cart);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnCartCheckedChangeListener {
        void onCheckedChanged(int total);
    }

    public void setOnCartCheckedChangeListener(OnCartCheckedChangeListener listener) {
        this.onCartCheckedChangeListener = listener;
    }

    public interface OnSelectAllStateChangeListener {
        void onStateChanged(boolean isAllSelected);
    }

    public void setOnSelectAllStateChangeListener(OnSelectAllStateChangeListener onSelectAllStateChangeListener) {
        this.onSelectAllStateChangeListener = onSelectAllStateChangeListener;
    }

    public CartAdapter(Context context, List<CartResponse> cartList, int mode) {
        this.context = context;
        this.cartList = cartList;
        this.mode = mode;
        cartApi = BaseURL.getUrl(context).create(CartApi.class);
    }

    public CartAdapter(Context context, List<CartResponse> cartList) {
        this(context, cartList, EDIT_MODE);
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartResponse cart = cartList.get(position);

        // Hiển thị các thành phần cần thiết
        holder.productName.setText(cart.getProductName());

        if (!TextUtils.isEmpty(cart.getColorName())) {
            holder.color.setVisibility(View.VISIBLE);
            holder.color.setText("Color: " + cart.getColorName());
        } else {
            holder.color.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(cart.getSizeName())) {
            holder.size.setVisibility(View.VISIBLE);
            holder.size.setText("Size: " + cart.getSizeName());
        } else {
            holder.size.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(cart.getColorName()) && TextUtils.isEmpty(cart.getSizeName())) {
            holder.layoutColorSize.setVisibility(View.GONE);
        } else {
            holder.layoutColorSize.setVisibility(View.VISIBLE);
        }

        holder.discountPrice.setText(String.format("%sđ", formatter.format(cart.getDiscountPrice())));

        // Áp dụng gạch bỏ cho giá gốc
        String originalPriceText = String.format("%sđ", formatter.format(cart.getOriginalPrice()));
        SpannableString spannableString = new SpannableString(originalPriceText);
        spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.originalPrice.setText(spannableString);

        if (cart.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(cart.getImageUrl()).into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder_image);
        }

        // Xử lý theo chế độ
        if (mode == VIEW_MODE) {
            // Ẩn các thành phần không cần thiết trong CheckOrderActivity
            holder.increaseBtn.setVisibility(View.GONE);
            holder.decreaseBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
            holder.quantity.setText(String.valueOf("Quantity: " + cart.getQuantity()));
        } else {
            // Hiển thị và xử lý các thành phần chỉnh sửa trong ShoppingCartFragment
            holder.increaseBtn.setVisibility(View.VISIBLE);
            holder.decreaseBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.quantity.setText(String.valueOf(cart.getQuantity()));

            holder.increaseBtn.setOnClickListener(v -> {
                int currentQuantity = cart.getQuantity();
                int newQuantity = currentQuantity + 1;
                updateQuantityItemFromCart(holder, cart, newQuantity);
            });

            holder.decreaseBtn.setOnClickListener(v -> {
                int currentQuantity = cart.getQuantity();
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    updateQuantityItemFromCart(holder, cart, newQuantity);
                }
            });

            holder.deleteBtn.setOnClickListener(v -> {
                removeItemFromCart(cart.getProductDetailId(), position);
            });

            holder.checkBox.setChecked(cart.isSelected());
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                cart.setSelected(isChecked);
                if (onCartCheckedChangeListener != null) {
                    onCartCheckedChangeListener.onCheckedChanged(calculateTotal());
                }
                if (onSelectAllStateChangeListener != null) {
                    onSelectAllStateChangeListener.onStateChanged(areAllItemsChecked());
                }
            });
        }

        // Sự kiện click item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cart);
            }
        });
    }

    public int calculateTotal() {
        int total = 0;
        for (CartResponse cart : cartList) {
            if (cart.isSelected()) {
                total += cart.getDiscountPrice() * cart.getQuantity();
            }
        }
        return total;
    }

    private boolean areAllItemsChecked() {
        for (CartResponse cart : cartList) {
            if (!cart.isSelected()) return false;
        }
        return true;
    }

    private void updateQuantityItemFromCart(CartViewHolder holder, CartResponse cart, int newQuantity) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        UpdateCartRequest request = new UpdateCartRequest(newQuantity);
        cartApi.updateItemCart(authHeader, cart.getProductDetailId(), request)
                .enqueue(new Callback<ApiResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartResponse updatedItem = response.body().getResult();
                            cart.setQuantity(updatedItem.getQuantity());
                            //holder.quantity.setText(String.valueOf(updatedItem.getQuantity()));
                            notifyItemChanged(holder.getAdapterPosition());

                            // Nếu sản phẩm đang được chọn thì cập nhật lại total
                            if (cart.isSelected() && onCartCheckedChangeListener != null) {
                                onCartCheckedChangeListener.onCheckedChanged(calculateTotal());
                            }
                            if (onSelectAllStateChangeListener != null) {
                                boolean isAllChecked = areAllItemsChecked();
                                onSelectAllStateChangeListener.onStateChanged(isAllChecked);
                            }
                        }
                        else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            Log.e("CartAdapter", "Response không hợp lệ: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("CartAdapter", "Cập nhật lõi: " + t.getMessage());
                    }
                });
    }

    private void removeItemFromCart(String productDetailId, int position) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        cartApi.removeItemCart(authHeader, productDetailId)
                .enqueue(new Callback<ApiResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                        if (response.isSuccessful()) {
                            if (position >= 0 && position < cartList.size()) {
                                cartList.remove(position);
                                notifyItemRemoved(position);
                                updateSelectAllText();
                                Toast.makeText(context, "Removed product", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Can't be removed: Invalid location", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Remove failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, color, size, quantity, discountPrice, originalPrice;
        ImageView productImage;
        ImageButton increaseBtn, decreaseBtn, deleteBtn;
        LinearLayout layoutColorSize;
        CheckBox checkBox;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.text_product_name);
            color = itemView.findViewById(R.id.text_product_color);
            size = itemView.findViewById(R.id.text_product_size);
            quantity = itemView.findViewById(R.id.text_quantity);
            discountPrice = itemView.findViewById(R.id.text_product_price);
            originalPrice = itemView.findViewById(R.id.text_product_original_price);
            productImage = itemView.findViewById(R.id.image_product);
            increaseBtn = itemView.findViewById(R.id.button_increase);
            decreaseBtn = itemView.findViewById(R.id.button_decrease);
            layoutColorSize = itemView.findViewById(R.id.layout_color_size);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
            checkBox = itemView.findViewById(R.id.checkBox_product);
        }
    }
}
