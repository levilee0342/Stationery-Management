package com.example.office_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.api.AddressApi;

import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressResponse> addressList;
    private AddressApi addressApi;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(AddressResponse address);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AddressAdapter(Context context, List<AddressResponse> addressList) {
        this.context = context;
        this.addressList = addressList;
        addressApi = BaseURL.getUrl(context).create(AddressApi.class);

        for (int i = 0; i < addressList.size(); i++) {
            if (Boolean.TRUE.equals(addressList.get(i).getIsDefault())) {
                AddressResponse defaultAddress = addressList.remove(i);
                addressList.add(0, defaultAddress);
                selectedPosition = 0; // chọn địa chỉ mặc định
                break;
            }
        }
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        AddressResponse address = addressList.get(position);

        holder.tvNamePhone.setText(address.getRecipient() + " - " + address.getPhone());
        holder.tvAddresName.setText(address.getAddressName());

        holder.tvDefault.setVisibility(address.getIsDefault() ? View.VISIBLE : View.GONE);

        // Set trạng thái RadioButton
        holder.radioBtnAddress.setChecked(position == selectedPosition);

        // Bắt sự kiện click cho cả dòng hoặc riêng RadioButton
        View.OnClickListener clickListener = v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(address);
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.radioBtnAddress.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void removeItem(int position) {
        addressList.remove(position);
        notifyItemRemoved(position);
    }

    public AddressResponse getItem(int position) {
        return addressList.get(position);
    }

    public void updateData(List<AddressResponse> newData) {
        this.addressList.clear();
        this.addressList.addAll(newData);

        for (int i = 0; i < addressList.size(); i++) {
            if (Boolean.TRUE.equals(addressList.get(i).getIsDefault())) {
                AddressResponse defaultAddress = addressList.remove(i);
                addressList.add(0, defaultAddress);
                selectedPosition = 0;
                break;
            }
        }
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioBtnAddress;
        TextView tvNamePhone, tvAddresName, tvDefault;

        public AddressViewHolder(View itemView) {
            super(itemView);
            radioBtnAddress = itemView.findViewById(R.id.radio_selected_address);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvAddresName = itemView.findViewById(R.id.tvAddressName);
            tvDefault = itemView.findViewById(R.id.tvDefault);
        }
    }
}
