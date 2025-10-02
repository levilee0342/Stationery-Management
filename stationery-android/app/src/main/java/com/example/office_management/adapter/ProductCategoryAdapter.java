package com.example.office_management.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.model.Category;

import java.util.List;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;
    private int displayItemCount; // Number of items to display (4 or 16)

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public ProductCategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
        this.displayItemCount = 4; // Default to 1 row (4 items)
    }

    public void setDisplayItemCount(int count) {
        this.displayItemCount = count;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getCategoryName());

        // Handle icon: check if it's an emoji or image URL
        String icon = category.getIcon();
        if (!TextUtils.isEmpty(icon) && isEmoji(icon)) {
            // Display emoji in TextView
            holder.tvEmojiIcon.setVisibility(View.VISIBLE);
            holder.imgCategoryIcon.setVisibility(View.GONE);
            holder.tvEmojiIcon.setText(icon);
            // Apply circular background to emoji TextView
            holder.tvEmojiIcon.setBackgroundResource(R.drawable.bg_circle_category);
            String bgColor = category.getBgColor();
            if (!TextUtils.isEmpty(bgColor)) {
                try {
                    holder.tvEmojiIcon.getBackground().setTint(Color.parseColor(bgColor));
                } catch (IllegalArgumentException e) {
                    // Fallback to default color
                }
            }
        } else {
            // Fallback to default icon
            holder.tvEmojiIcon.setVisibility(View.GONE);
            holder.imgCategoryIcon.setVisibility(View.VISIBLE);
            holder.imgCategoryIcon.setImageResource(R.drawable.ic_book);
            holder.imgCategoryIcon.setBackgroundResource(R.drawable.bg_circle_category);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    // Simple check for emoji (can be enhanced for more complex cases)
    private boolean isEmoji(String text) {
        if (TextUtils.isEmpty(text)) return false;
        return text.length() <= 4 && text.codePointCount(0, text.length()) <= 2;
    }

    @Override
    public int getItemCount() {
        return Math.min(categoryList.size(), displayItemCount);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoryIcon;
        TextView tvCategoryName;
        TextView tvEmojiIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoryIcon = itemView.findViewById(R.id.imgCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvEmojiIcon = itemView.findViewById(R.id.tvEmojiIcon);
        }
    }
}