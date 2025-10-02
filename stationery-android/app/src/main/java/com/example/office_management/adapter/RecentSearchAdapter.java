package com.example.office_management.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
    private List<String> keywords;
    private OnKeywordClickListener listener;

    public interface OnKeywordClickListener {
        void onKeywordClick(String keyword);
    }

    public RecentSearchAdapter(List<String> keywords, OnKeywordClickListener listener) {
        this.keywords = keywords;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String keyword = keywords.get(position);
        holder.tvKeyword.setText(keyword);
        holder.itemView.setOnClickListener(v -> listener.onKeywordClick(keyword));
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tvKeyword);
        }
    }
}
