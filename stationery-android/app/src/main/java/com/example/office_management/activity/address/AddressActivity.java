package com.example.office_management.activity.address;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;
import com.example.office_management.api.AddressApi;
import com.example.office_management.api.VietnamAddressApi;
import com.example.office_management.dto.request.AddressRequest;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.address.ApiAddressResponse;
import com.example.office_management.dto.response.address.DistrictResponse;
import com.example.office_management.dto.response.address.ProvinceResponse;
import com.example.office_management.dto.response.address.WardResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.retrofit2.BaseURLAddress;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {
    private boolean isEditMode = false;
    private AddressResponse currentAddress;
    private EditText edtName, edtPhone, edtAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private List<ProvinceResponse> provinceList = new ArrayList<>();
    private List<DistrictResponse> districtList = new ArrayList<>();
    private List<WardResponse> wardList = new ArrayList<>();
    private VietnamAddressApi vietnamAddressApi;
    private AddressApi addressApi;
    private ArrayAdapter<String> provinceAdapter, districtAdapter, wardAdapter;
    private Button btnConfirm;
    private ImageButton btnBack;
    private TextView tvTitle;
    private CheckBox checkBoxDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);

        addressApi = BaseURL.getUrl(this).create((AddressApi.class));

        initViews();

        loadProvinces();

        Intent intent = getIntent();
        currentAddress = (AddressResponse) intent.getSerializableExtra("address");
        isEditMode = intent.getBooleanExtra("isEdit", false); // Sử dụng extra isEdit
        if (currentAddress != null && isEditMode) {
            populateAddressData(currentAddress);
            tvTitle.setText("Edit shipping address");
        } else {
            tvTitle.setText("Add shipping address");
        }

        btnConfirm.setOnClickListener(v -> {
            if (isEditMode) {
                updateAddress(currentAddress.getAddressId());
            } else {
                addAddress();
            }
        });

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String provinceCode = provinceList.get(position).getCode();

                BaseURLAddress.getApi().getDistricts(provinceCode, -1).enqueue(new Callback<ApiAddressResponse<DistrictResponse>>() {
                    @Override
                    public void onResponse(Call<ApiAddressResponse<DistrictResponse>> call, Response<ApiAddressResponse<DistrictResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            districtList.clear();
                            districtList.addAll(response.body().getData().getData());

                            List<String> districtNames = new ArrayList<>();
                            for (DistrictResponse district : districtList) {
                                districtNames.add(district.getName_with_type());
                            }
                            districtAdapter.clear();
                            districtAdapter.addAll(districtNames);
                            districtAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(Call<ApiAddressResponse<DistrictResponse>> call, Throwable t) {
                        Toast.makeText(AddressActivity.this, "Lỗi tải quận", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String districtCode = districtList.get(position).getCode();

                BaseURLAddress.getApi().getWards(districtCode, -1).enqueue(new Callback<ApiAddressResponse<WardResponse>>() {
                    @Override
                    public void onResponse(Call<ApiAddressResponse<WardResponse>> call, Response<ApiAddressResponse<WardResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            wardList.clear();
                            wardList.addAll(response.body().getData().getData());

                            List<String> wardNames = new ArrayList<>();
                            for (WardResponse ward : wardList) {
                                wardNames.add(ward.getName_with_type());
                            }
                            wardAdapter.clear();
                            wardAdapter.addAll(wardNames);
                            wardAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiAddressResponse<WardResponse>> call, Throwable t) {
                        Toast.makeText(AddressActivity.this, "Lỗi tải phường", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initViews(){
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        btnConfirm = findViewById(R.id.btnConfirmAddress);
        edtAddress = findViewById(R.id.edtAddress);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        checkBoxDefault = findViewById(R.id.checkboxDefault);
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btn_back);

        // Khởi tạo adapter trống ban đầu
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);
    }

    private void loadProvinces() {
        BaseURLAddress.getApi().getProvinces(-1).enqueue(new Callback<ApiAddressResponse<ProvinceResponse>>() {
            @Override
            public void onResponse(Call<ApiAddressResponse<ProvinceResponse>> call, Response<ApiAddressResponse<ProvinceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinceList.clear();
                    provinceList.addAll(response.body().getData().getData());

                    List<String> provinceNames = new ArrayList<>();
                    for (ProvinceResponse province : provinceList) {
                        provinceNames.add(province.getName());
                    }
                    provinceAdapter.clear();
                    provinceAdapter.addAll(provinceNames);
                    provinceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiAddressResponse<ProvinceResponse>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi tải tỉnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AddressRequest getAddressRequestFromInput() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return null;
        }

        String province = spinnerProvince.getSelectedItem().toString();
        String district = spinnerDistrict.getSelectedItem().toString();
        String ward = spinnerWard.getSelectedItem().toString();
        String fullAddress = address + ", " + ward + ", " + district + ", " + province;

        boolean isDefault = checkBoxDefault.isChecked();

        Log.d("AddressRequest", "Name: " + name + ", Phone: " + phone + ", FullAddress: " + fullAddress + ", IsDefault: " + isDefault);
        return new AddressRequest(
                null,           // ID sẽ do create/update xử lý
                fullAddress,
                phone,
                name,
                isDefault
        );
    }

    private void addAddress() {
        AddressRequest request = getAddressRequestFromInput();
        if (request == null) return; // Nếu thiếu thông tin thì không tiếp tục

        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        addressApi.createAddress(authHeader, request).enqueue(new Callback<ApiResponse<AddressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressResponse>> call, Response<ApiResponse<AddressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressResponse>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddress(String id) {
        Log.d("UpdateAddress", "Address ID: " + id);
        AddressRequest request = getAddressRequestFromInput();
        if (request == null) return;

        request.setAddressId(id); // Gán ID cho request nếu cập nhật

        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        addressApi.updateAddress(authHeader, id, request).enqueue(new Callback<ApiResponse<AddressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressResponse>> call, Response<ApiResponse<AddressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UpdateAddress", "Response Body: " + new Gson().toJson(response.body()));
                    Toast.makeText(AddressActivity.this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this, "Cập nhật địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressResponse>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateAddressData(AddressResponse address) {
        edtName.setText(address.getRecipient());
        edtPhone.setText(address.getPhone());
        edtAddress.setText(address.getAddressName().split(",")[0]); // chỉ lấy phần chi tiết
        checkBoxDefault.setChecked(address.getIsDefault());

        // Tạm thời lưu trữ full địa chỉ để chọn đúng tỉnh/quận/phường sau khi tải xong spinner
        String[] parts = address.getAddressName().split(", ");
        String wardName = parts[1];
        String districtName = parts[2];
        String provinceName = parts[3];

        // Sau khi load province xong, chọn đúng item
        spinnerProvince.post(() -> {
            for (int i = 0; i < provinceList.size(); i++) {
                if (provinceList.get(i).getName().equals(provinceName)) {
                    spinnerProvince.setSelection(i);
                    break;
                }
            }

            // loadDistrict & chọn đúng district
            spinnerDistrict.postDelayed(() -> {
                for (int i = 0; i < districtList.size(); i++) {
                    if (districtList.get(i).getName_with_type().equals(districtName)) {
                        spinnerDistrict.setSelection(i);
                        break;
                    }
                }

                // loadWard & chọn đúng ward
                spinnerWard.postDelayed(() -> {
                    for (int i = 0; i < wardList.size(); i++) {
                        if (wardList.get(i).getName_with_type().equals(wardName)) {
                            spinnerWard.setSelection(i);
                            break;
                        }
                    }
                }, 300);

            }, 300);
        });
    }

}