package com.example.office_management.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.order.OrderDetailActivity;
import com.example.office_management.api.OrderApi;
import com.example.office_management.dto.request.order.CancelOrderRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderResponse;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.OrderViewHolder> {
    private final List<PurchaseOrderResponse> orderList;
    private final OrderApi orderApi;
    private final String authToken;

    public interface OnOrderCanceledListener {
        void onOrderCanceled(String purchaseOrderId);
    }

    private OnOrderCanceledListener cancelListener;

    public void setOnOrderCanceledListener(OnOrderCanceledListener listener) {
        this.cancelListener = listener;
    }

    public PurchaseOrderAdapter(List<PurchaseOrderResponse> orderList, OrderApi orderApi, String authToken) {
        this.orderList = orderList;
        this.orderApi = orderApi;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        PurchaseOrderResponse order = orderList.get(position);
        Context context = holder.itemView.getContext();

        // Format order ID (first 10 characters)
        String orderId = order.getPurchaseOrderId();
        String shortOrderId = orderId.length() > 10 ? orderId.substring(0, 10) : orderId;
        holder.tvOrderId.setText("#" + shortOrderId);

        // Set order status with appropriate styling
        PurchaseOrderResponse.Status status = order.getStatus();
        holder.tvOrderStatus.setText(status.toString());

        // Set status background and text color based on status
        switch (status) {
            case COMPLETED:
                holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_order_status_completed);
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case CANCELED:
                holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_order_status_cancelled);
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case PROCESSING:
            case PENDING:
                holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_order_status_pending);
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            default:
                holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_order_status_pending);
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                break;
        }

        // Format item quantity
        int quantity = order.getOrderDetails() != null ? order.getOrderDetails().size() : 0;

        // Format amount with currency
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvTotal.setText(context.getString(
                R.string.order_total_format,
                quantity,
                (quantity != 1 ? "s" : ""),
                formatter.format(order.getAmount())
        ));

        // Format createdAt date
        Date createdAt = order.getCreatedAt();
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.tvCreateTime.setText(sdf.format(createdAt));
        } else {
            holder.tvCreateTime.setText("N/A");
        }

        // Button visibility and state
        switch (status) {
            case PROCESSING:
            case PENDING:
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnReorder.setVisibility(View.GONE);
                break;
            case CANCELED:
            case COMPLETED:
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
            default:
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReorder.setVisibility(View.GONE);
                break;
        }

        // Button actions
        holder.btnCancel.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            PurchaseOrderResponse currentOrder = orderList.get(pos);
            showCancelDialog(context, currentOrder, pos, holder);
        });

        holder.btnReorder.setOnClickListener(v -> {
            Toast.makeText(context, "Chức năng đặt lại đơn hàng sẽ sớm được triển khai", Toast.LENGTH_SHORT).show();
        });

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("purchaseOrderId", order.getPurchaseOrderId());
            intent.putExtra("quantity", quantity);
            context.startActivity(intent);
        });
    }

    private void showCancelDialog(Context context, PurchaseOrderResponse order, int position, OrderViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_order, null);
        builder.setView(dialogView);

        Spinner spinnerReasons = dialogView.findViewById(R.id.spinner_cancel_reason);
        EditText etOtherReason = dialogView.findViewById(R.id.et_other_reason);
        MaterialButton btnConfirmCancel = dialogView.findViewById(R.id.btn_confirm_cancel);
        MaterialButton btnDismiss = dialogView.findViewById(R.id.btn_dismiss);

        // Set up spinner with cancel reasons
        String[] cancelReasons = {
                "Thay đổi ý định",
                "Sai thông tin đơn hàng",
                "Sản phẩm không phù hợp",
                "Khác"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, cancelReasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReasons.setAdapter(adapter);

        // Show/hide other reason input based on spinner selection
        spinnerReasons.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                etOtherReason.setVisibility(position == cancelReasons.length - 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        AlertDialog dialog = builder.create();

        btnConfirmCancel.setOnClickListener(v -> {
            String selectedReason = spinnerReasons.getSelectedItem().toString();
            String cancelReason = selectedReason.equals("Khác") ?
                    etOtherReason.getText().toString().trim() : selectedReason;

            if (selectedReason.equals("Khác") && cancelReason.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show();
                return;
            }

            CancelOrderRequest request = new CancelOrderRequest();
            request.setCancelReason(cancelReason);

            holder.btnCancel.setEnabled(false);
            holder.btnCancel.setText("Đang hủy...");

            Call<ApiResponse<Void>> call = orderApi.cancelOrder(
                    "Bearer " + authToken,
                    order.getPurchaseOrderId(),
                    request
            );

            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    holder.btnCancel.setEnabled(true);
                    holder.btnCancel.setText("Hủy");

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(context, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                        order.setStatus(PurchaseOrderResponse.Status.CANCELED);
                        notifyItemChanged(position);

                        if (cancelListener != null) {
                            cancelListener.onOrderCanceled(order.getPurchaseOrderId());
                        }
                    } else {
                        Toast.makeText(context,
                                "Hủy đơn hàng thất bại: " + (response.errorBody() != null ?
                                        response.errorBody().toString() : "Lỗi không xác định"),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    holder.btnCancel.setEnabled(true);
                    holder.btnCancel.setText("Hủy");
                    Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        btnDismiss.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvTotal, tvCreateTime;
        MaterialButton btnCancel, btnReorder, btnDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}