package com.example.office_management.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.office_management.R;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderDetailResponse;

import java.text.DecimalFormat;
import java.util.List;

public class PurchaseOrderDetailAdapter extends RecyclerView.Adapter<PurchaseOrderDetailAdapter.ViewHolder> {
    private List<ProductDetailResponse> productList;
    private List<PurchaseOrderDetailResponse> orderDetails;
    public PurchaseOrderDetailAdapter(List<ProductDetailResponse> productList, List<PurchaseOrderDetailResponse> orderDetails) {
        this.productList = productList;
        this.orderDetails = orderDetails;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtProductPrice, txtPriceOriginal, txtColor, txtSize, txtQuantity;
        ImageView imgProduct;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.text_product_name);
            txtProductPrice = view.findViewById(R.id.text_product_price);
            txtPriceOriginal = view.findViewById(R.id.text_product_original_price);
            txtColor = view.findViewById(R.id.text_product_color);
            txtSize = view.findViewById(R.id.text_product_size);
            txtQuantity = view.findViewById(R.id.text_quantity);
            imgProduct = view.findViewById(R.id.image_product);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase_product_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (productList == null || orderDetails == null) return;
        if (position >= productList.size() || position >= orderDetails.size()) return;

        ProductDetailResponse product = productList.get(position);
        PurchaseOrderDetailResponse orderDetailResponse = orderDetails.get(position);

        holder.txtName.setText(product.getName());

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.txtProductPrice.setText(formatter.format(product.getDiscountPrice()) + "đ");
        holder.txtPriceOriginal.setText(formatter.format(product.getOriginalPrice()) + "đ");

        if (product.getColor() != null) {
            holder.txtColor.setText(product.getColor().getName());
        }

        if (product.getSize() != null) {
            holder.txtSize.setText(product.getSize().getName());
        }

        holder.txtQuantity.setText(String.valueOf(orderDetailResponse.getQuantity()));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImages().get(0).getUrl())
                    .into(holder.imgProduct);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }
}