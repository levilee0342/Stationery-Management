package com.example.office_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.office_management.R;
import com.example.office_management.activity.product.ProductDetailActivity;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.product.ProductResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_SKELETON = 1;
    private List<ProductResponse> productList;
    private boolean isLoading = true;
    private final Context context;
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public ProductAdapter(Context context, List<ProductResponse> productList) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
    }

    public void setData(List<ProductResponse> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        this.isLoading = false;
        Log.d("ProductAdapter", "Data updated, item count: " + productList.size());
        notifyDataSetChanged();
    }

    public void showLoading() {
        this.isLoading = true;
        notifyDataSetChanged();
    }
    public void addData(List<ProductResponse> newProducts) {
        if (newProducts != null && !newProducts.isEmpty()) {
            int startPosition = this.productList.size(); // Vị trí bắt đầu thêm
            this.productList.addAll(newProducts);
            // Thông báo cho adapter rằng có item mới được thêm vào, hiệu quả hơn notifyDataSetChanged()
            notifyItemRangeInserted(startPosition, newProducts.size());
            Log.d("Adapter", "Data added, new item count: " + productList.size());
        }
    }
    @Override
    public int getItemViewType(int position) {
        return isLoading ? TYPE_SKELETON : TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SKELETON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_skeleton, parent, false);
            return new SkeletonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            ProductResponse product = productList.get(position);
            ProductDetailResponse detail = product.getProductDetail();
            ProductViewHolder productHolder = (ProductViewHolder) holder;

            // Bind tên sản phẩm
            productHolder.tvProductName.setText(product.getName() != null ? product.getName() : "N/A");

            // Bind giá sản phẩm
            long price = detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice();
            productHolder.tvProductPrice.setText(String.format("%sđ", formatter.format(price)));

            // Bind giá gốc với gạch ngang
            String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
            SpannableString spannableString = new SpannableString(originalPriceText);
            spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productHolder.tvOldPrice.setText(spannableString);
            productHolder.tvOldPrice.setVisibility(detail.getDiscountPrice() > 0 && detail.getOriginalPrice() > detail.getDiscountPrice() ? View.VISIBLE : View.GONE);

            // Bind phần trăm giảm giá
            int discountPercent = 0;
            if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
                discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
            }
            productHolder.tvDiscount.setText("-" + discountPercent + "%");
            productHolder.tvDiscount.setVisibility(discountPercent > 0 ? View.VISIBLE : View.GONE);

            // Bind rating
            productHolder.tvProductRating.setText(getStarRatingText(product.getTotalRating()));

            // Bind số lượng đã bán
            productHolder.tvSold.setText("Sold: " + product.getSoldQuantity());

            // Bind hình ảnh
            String imageUrl = detail.getThumbnail() != null ? detail.getThumbnail() : product.getImg();
            Log.d("Glide", "Loading image: " + imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .placeholder(R.drawable.ic_box)
                    .error(R.drawable.ic_box)
                    .into(productHolder.imgProduct);

            // Sự kiện click
            productHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("slug", detail.getSlug());
                context.startActivity(intent);
            });
        }
        // Skeleton không cần bind dữ liệu
    }

    @Override
    public int getItemCount() {
        return isLoading ? 6 : productList.size(); // Hiển thị 6 skeleton items khi đang tải
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvOldPrice, tvDiscount, tvProductRating, tvSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvProductRating = itemView.findViewById(R.id.productRating);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }

    public static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private SpannableString getStarRatingText(double rating) {
        String star = "★";
        String ratingText = star + " " + String.format("%.1f", rating);
        SpannableString spannable = new SpannableString(ratingText);
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FFD700")), // Màu vàng
                0, 1, // Chỉ ký tự "★"
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannable;
    }
}