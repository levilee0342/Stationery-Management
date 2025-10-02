package com.example.office_management.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.office_management.R;
import com.example.office_management.dto.response.colorSize.ColorSizeSlugResponse;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private List<ColorSizeSlugResponse> colors;
    private int selectedPosition = -1;
    private OnColorClickListener onColorClickListener;

    public interface OnColorClickListener {
        void onColorClick(ColorSizeSlugResponse color);
    }

    public ColorAdapter(List<ColorSizeSlugResponse> colorList) {
        this.colors = colorList;
    }

    public void setOnColorClickListener(OnColorClickListener listener) {
        this.onColorClickListener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorSizeSlugResponse color = colors.get(position);

        // Tạo GradientDrawable mới
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        try {
            String hex = color.getHex();
            if (hex != null && !hex.isEmpty()) {
                gradientDrawable.setColor(Color.parseColor(hex));
            } else {
                gradientDrawable.setColor(Color.GRAY);
            }
        } catch (IllegalArgumentException e) {
            gradientDrawable.setColor(Color.GRAY);
            Log.e("ColorAdapter", "Invalid hex color: " + color.getHex());
        }

        // Kiểm tra nếu màu là trắng hoặc gần trắng
        boolean isWhite = color.getHex() != null && (
                color.getHex().equalsIgnoreCase("#FFFFFF") ||
                        color.getHex().equalsIgnoreCase("#FFF") ||
                        color.getHex().equalsIgnoreCase("#FFFF") ||
                        color.getHex().equalsIgnoreCase("#FFFFFF00")
        );

        // Áp dụng viền
        if (selectedPosition == position) {
            gradientDrawable.setStroke(10, Color.parseColor("#CCCCCC")); // Viền xám cho màu được chọn
        } else if (isWhite) {
            gradientDrawable.setStroke(1, Color.BLACK); // Viền đen cho màu trắng không được chọn
        } else {
            gradientDrawable.setStroke(0, 0); // Không viền
        }

        // Áp dụng GradientDrawable cho colorView
        holder.colorView.setBackground(gradientDrawable);

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previous);
            notifyItemChanged(position);
            if (onColorClickListener != null) {
                onColorClickListener.onColorClick(color);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorView);
        }
    }
}