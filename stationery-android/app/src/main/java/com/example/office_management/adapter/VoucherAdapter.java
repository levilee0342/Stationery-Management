package com.example.office_management.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.model.Promotion;
import com.example.office_management.model.UserPromotion;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<UserPromotion> voucherList;
    private Context context;

    public VoucherAdapter(Context context, List<UserPromotion> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        UserPromotion userPromotion = voucherList.get(position);
        Promotion promo = userPromotion.getPromotion();

        // Hiển thị thông tin mã giảm giá
        String discountText = promo.getDiscountType() == Promotion.DiscountType.PERCENTAGE
                ? promo.getDiscountValue() + "% off (maximum " +
                (promo.getMaxValue() != null ? promo.getMaxValue() + "đ" : "Unlimited") + ") - " + promo.getPromoCode()
                : "Discount " + promo.getDiscountValue() + "đ off - " + promo.getPromoCode();
        holder.tvTitle.setText(discountText);
        holder.tvSub.setText("Applicable to orders from " + promo.getMinOrderValue() + "đ");
        holder.tvCode.setText(promo.getPromoCode());

        String dateStr = promo.getEndDate().split("T")[0]; // "2025-07-30"
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateStr);
            String formattedDate = outputFormat.format(date);
            holder.tvExpire.setText("EXP: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvExpire.setText("EXP: " + dateStr); // fallback
        }

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Promo Code", promo.getPromoCode());
            clipboard.setPrimaryClip(clip);
            //Toast.makeText(context, "Đã sao chép mã", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub, tvCode, tvExpire;
        MaterialButton btnCopy;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSub = itemView.findViewById(R.id.tvSub);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvExpire = itemView.findViewById(R.id.tvExpire);
            btnCopy = itemView.findViewById(R.id.btnCopy);
        }
    }
}

