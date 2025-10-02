package com.example.office_management.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.model.Promotion;
import com.example.office_management.model.UserPromotion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {
    private List<UserPromotion> coupons;
    private Context context;
    private OnApplyClickListener onApplyClickListener;
    private int totalPrice;
    private int selectedPosition = RecyclerView.NO_POSITION; // Lưu vị trí đã chọn

    public interface OnApplyClickListener {
        void onApplyClicked(UserPromotion userPromotion);
    }

    public CouponAdapter(Context context, List<UserPromotion> coupons, int totalPrice, OnApplyClickListener listener) {
        this.context = context;
        this.coupons = coupons;
        this.totalPrice = totalPrice;
        this.onApplyClickListener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        UserPromotion userPromotion = coupons.get(position);
        Promotion promo = userPromotion.getPromotion();

        // Tính giảm giá thực tế
        int actualDiscount = 0;
        if (promo.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
            int percentDiscount = (totalPrice * promo.getDiscountValue()) / 100;
            actualDiscount = (promo.getMaxValue() != null)
                    ? Math.min(percentDiscount, promo.getMaxValue())
                    : percentDiscount;
        } else {
            actualDiscount = promo.getDiscountValue();
        }

        // Hiển thị thông tin mã giảm giá
        String discountText = promo.getDiscountType() == Promotion.DiscountType.PERCENTAGE
                ? promo.getDiscountValue() + "% off (maximum " +
                (promo.getMaxValue() != null ? promo.getMaxValue() + "đ" : "Unlimited") + ") - " + promo.getPromoCode()
                : "Discount " + promo.getDiscountValue() + "đ off - " + promo.getPromoCode();
        holder.couponName.setText(discountText);

        holder.description.setText("Applicable to orders from " + promo.getMinOrderValue() + "đ");

        // Hiển thị hạn sử dụng
        String dateStr = promo.getEndDate().split("T")[0];
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateStr);
            String formattedDate = outputFormat.format(date);
            holder.couponExpiry.setText("EXP: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.couponExpiry.setText("EXP: " + dateStr);
        }

        // Kiểm tra điều kiện áp dụng
        boolean isValid = totalPrice >= promo.getMinOrderValue();
        holder.applyButton.setEnabled(isValid);

        // Đổi giao diện nếu đã được chọn
        if (position == selectedPosition) {
            holder.applyButton.setText("Applied");
            holder.applyButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        } else {
            holder.applyButton.setText("Apply");
            holder.applyButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_blue));
        }

        // Xử lý sự kiện click
        holder.applyButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            // Chỉ xử lý nếu chọn khác cái hiện tại
            if (selectedPosition != adapterPosition) {
                int previousSelected = selectedPosition;
                selectedPosition = adapterPosition;

                notifyItemChanged(previousSelected); // reset cái cũ
                notifyItemChanged(selectedPosition); // đánh dấu cái mới

                if (onApplyClickListener != null) {
                    onApplyClickListener.onApplyClicked(coupons.get(selectedPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView couponName, description, couponExpiry;
        Button applyButton;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            couponName = itemView.findViewById(R.id.coupon_name);
            description = itemView.findViewById(R.id.description);
            couponExpiry = itemView.findViewById(R.id.coupon_expiry);
            applyButton = itemView.findViewById(R.id.apply_button);
        }
    }
}
