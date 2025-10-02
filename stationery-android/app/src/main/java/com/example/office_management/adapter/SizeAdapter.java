package com.example.office_management.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.office_management.R;
import com.example.office_management.dto.response.colorSize.SizeSlugResponse;

import java.util.ArrayList;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private List<SizeSlugResponse> sizes;
    private int selectedPosition = -1;
    private OnSizeClickListener onSizeClickListener;
    private boolean isEnabled = true; // Trạng thái để kiểm soát khả năng click

    public interface OnSizeClickListener {
        void onSizeClick(SizeSlugResponse size);
    }

    public SizeAdapter(List<SizeSlugResponse> sizeList) {
        this.sizes = new ArrayList<>(sizeList);
    }

    public void updateSizes(List<SizeSlugResponse> newSizes) {
        sizes.clear();
        sizes.addAll(newSizes);
        selectedPosition = -1; // Reset vị trí chọn khi cập nhật danh sách
        notifyDataSetChanged();
    }

    public void setOnSizeClickListener(OnSizeClickListener listener) {
        this.onSizeClickListener = listener;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        notifyDataSetChanged(); // Cập nhật giao diện để phản ánh trạng thái enabled
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        SizeSlugResponse size = sizes.get(position);
        holder.sizeText.setText(size.getSize());

        holder.sizeText.setSelected(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (!isEnabled) {
                // Không cho phép click nếu adapter bị vô hiệu hóa
                return;
            }
            int previous = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previous);
            notifyItemChanged(position);
            if (onSizeClickListener != null) {
                onSizeClickListener.onSizeClick(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView sizeText;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeText = itemView.findViewById(R.id.sizeText);
        }
    }
}