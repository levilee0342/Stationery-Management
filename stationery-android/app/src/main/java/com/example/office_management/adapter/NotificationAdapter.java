package com.example.office_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.dto.response.NotificationResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationResponse> notifications;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationResponse> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        NotificationResponse notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvContent.setText(notification.getMessage());

        String date = "N/A";
        if (notification.getCreatedAt() != null) {
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(notification.getCreatedAt());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.tvDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
