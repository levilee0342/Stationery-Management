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

public class ProductPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_SKELETON = 1;
    private final List<ProductResponse> products;
    private boolean isLoading = true;
    private final Context context;
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public ProductPagerAdapter(List<ProductResponse> products, Context context) {
        this.products = products != null ? products : new ArrayList<>();
        this.context = context;
    }

    public void setData(List<ProductResponse> newProducts) {
        products.clear();
        if (newProducts != null) {
            products.addAll(newProducts);
        }
        this.isLoading = false;
        Log.d("ProductPagerAdapter", "Data updated, item count: " + products.size());
        notifyDataSetChanged();
    }

    public void showLoading() {
        this.isLoading = true;
        notifyDataSetChanged();
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
                    .inflate(R.layout.item_flash_sale_skeleton, parent, false);
            return new SkeletonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_page, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            int startIndex = position * 2;

            // Reset visibility
            viewHolder.product1.setVisibility(View.GONE);
            viewHolder.product2.setVisibility(View.GONE);

            if (startIndex < products.size()) {
                bindProduct(viewHolder.product1, products.get(startIndex));
            }
            if (startIndex + 1 < products.size()) {
                bindProduct(viewHolder.product2, products.get(startIndex + 1));
            }
        }
        // Skeleton không cần bind dữ liệu
    }

    private void bindProduct(View container, ProductResponse product) {
        container.setVisibility(View.VISIBLE);
        ImageView imgProduct = container.findViewById(R.id.imgProduct);
        TextView tvProductName = container.findViewById(R.id.tvProductName);
        TextView tvProductPrice = container.findViewById(R.id.tvProductPrice);
        TextView tvOldPrice = container.findViewById(R.id.tvOldPrice);
        TextView tvDiscount = container.findViewById(R.id.tvDiscount);
        TextView tvProductRating = container.findViewById(R.id.productRating);
        TextView tvSold = container.findViewById(R.id.tvSold);

        ProductDetailResponse detail = product.getProductDetail();
        tvProductName.setText(product.getName() != null ? product.getName() : "N/A");

        long price = detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice();
        tvProductPrice.setText(String.format("%sđ", formatter.format(price)));

        String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
        SpannableString spannableString = new SpannableString(originalPriceText);
        spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvOldPrice.setText(spannableString);
        tvOldPrice.setVisibility(detail.getDiscountPrice() > 0 && detail.getOriginalPrice() > detail.getDiscountPrice() ? View.VISIBLE : View.GONE);

        int discountPercent = 0;
        if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
            discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
        }
        tvDiscount.setText("-" + discountPercent + "%");
        tvDiscount.setVisibility(discountPercent > 0 ? View.VISIBLE : View.GONE);

        tvProductRating.setText(getStarRatingText(product.getTotalRating()));
        tvSold.setText("Sold: " + product.getSoldQuantity());

        String imageUrl = detail.getThumbnail() != null ? detail.getThumbnail() : product.getImg();
        Log.d("Glide", "Loading image for product: " + product.getName() + ", URL: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_box)
                .into(imgProduct);

        container.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("slug", detail.getSlug());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return isLoading ? 3 : (int) Math.ceil(products.size() / 2.0); // 3 skeleton items khi đang tải
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View product1, product2;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            product1 = itemView.findViewById(R.id.product1);
            product2 = itemView.findViewById(R.id.product2);
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        SkeletonViewHolder(@NonNull View itemView) {
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