package com.example.office_management.decoration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.address.AddressActivity;
import com.example.office_management.adapter.AddressAdapter;
import com.example.office_management.api.AddressApi;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    private AddressAdapter adapter;
    private AddressApi addressApi;
    private Context context;
    private String authToken;

    public SwipeHelper(AddressAdapter adapter, AddressApi addressApi, Context context, String authToken) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.addressApi = addressApi;
        this.context = context;
        this.authToken = authToken;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        AddressResponse address = adapter.getItem(position);
        if (direction == ItemTouchHelper.RIGHT) {
            // Xóa
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setTitle("Xóa địa chỉ")
                    .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        deleteAddress(position, address.getAddressId());
                    })
                    .setNegativeButton("Không", null)
                    .show();
        } else if (direction == ItemTouchHelper.LEFT) {
            // Chỉnh sửa
            Intent intent = new Intent(context, AddressActivity.class);
            intent.putExtra("address", address);
            intent.putExtra("isEdit", true);
            context.startActivity(intent);
            adapter.notifyItemChanged(position); // Đặt lại trạng thái vuốt
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();

        // Kéo sang phải (Xóa)
        if (dX > 0) {
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.RED);
            RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
            c.drawRect(background, backgroundPaint);

            Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);
            int iconMargin = (itemHeight - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemHeight - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(c);
        }
        // Kéo sang trái (Chỉnh sửa)
        else if (dX < 0) {
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.parseColor("#FFA500")); // Màu cam
            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            c.drawRect(background, backgroundPaint);

            Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_edit);
            int iconMargin = (itemHeight - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemHeight - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconRight = itemView.getRight() - iconMargin;
            int iconLeft = iconRight - icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(c);
        }
    }

    private void deleteAddress(int position, String id) {
        Call<ApiResponse<String>> call = addressApi.deleteAddress("Bearer " + authToken, id);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.removeItem(position);
                    Toast.makeText(context, "Đã xóa địa chỉ thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position); // Khôi phục item nếu thất bại
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position); // Khôi phục item nếu thất bại
            }
        });
    }
}