package com.example.office_management.adapter;

import static com.example.office_management.activity.product.ProductDetailActivity.formatter;

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

import java.util.ArrayList;
import java.util.List;

public class FilterProductAdapter extends RecyclerView.Adapter<FilterProductAdapter.ProductViewHolder> {
    private List<ProductResponse> productList;
    private Context context;

    public FilterProductAdapter(Context context) {
        this.context = context;
        this.productList = new ArrayList<>();
    }

    public void setData(List<ProductResponse> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        Log.d("FilterAdapter", "Data updated, item count: " + productList.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        ProductDetailResponse detail = product.getProductDetail();
        Log.d("FilterAdapter", "Binding product: " + product.getName() + ", Position: " + position);

        // Set product name
        holder.tvProductName.setText(product.getName() != null ? product.getName() : "N/A");

        // Set price (use discountPrice if available, else originalPrice)
        holder.tvProductPrice.setText(String.format("%sđ", formatter.format(
                detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice()
        )));

        // Set original price with strikethrough
        String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
        SpannableString spannableString = new SpannableString(originalPriceText);
        spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvOldPrice.setText(spannableString);

        // Set rating
        holder.tvProductRating.setText(TextUtils.getStarRatingText(product.getTotalRating()));

        // Set sold quantity
        holder.tvSold.setText("Sold: " + String.valueOf(product.getSoldQuantity()));

        // Calculate and set discount percentage
        int discountPercent = 0;
        if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
            discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
        }
        holder.tvDiscount.setText("-" + discountPercent + "%");

        // Load product image
        String imageUrl = detail.getThumbnail() != null ? detail.getThumbnail() : product.getImg();
        Log.d("Glide", "Loading image: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_box)
                .into(holder.imgProduct);

        // Set click listener to navigate to ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("slug", detail.getSlug());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvOldPrice, tvDiscount, tvProductRating, tvSold;

        ProductViewHolder(View itemView) {
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

    public static class TextUtils {
        public static SpannableString getStarRatingText(double rating) {
            String star = "★";
            String ratingText = star + " " + rating;
            SpannableString spannable = new SpannableString(ratingText);

            // Apply gold color to star symbol
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#FFD700")),
                    0, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            return spannable;
        }
    }
}