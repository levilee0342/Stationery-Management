package com.example.office_management.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.ChangePasswordRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.retrofit2.BaseURL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isRepeatPasswordVisible = false;
    private EditText edtOldPassword, edtNewPassword, etPassword;
    private ImageView ivTogglePassword, ivToggleOldPassword, ivToggleNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initView();
        Button btnConfirm = findViewById(R.id.btnConfirm);
        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> { onBackPressed(); });

        ivToggleOldPassword.setOnClickListener(v -> {
            isOldPasswordVisible = !isOldPasswordVisible;
            togglePasswordVisibility(edtOldPassword, ivToggleOldPassword, isOldPasswordVisible);
        });

        ivToggleNewPassword.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            togglePasswordVisibility(edtNewPassword, ivToggleNewPassword, isNewPasswordVisible);
        });

        ivTogglePassword.setOnClickListener(v -> {
            isRepeatPasswordVisible = !isRepeatPasswordVisible;
            togglePasswordVisibility(etPassword, ivTogglePassword, isRepeatPasswordVisible);
        });


        btnConfirm.setOnClickListener(v -> {
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            String oldPassword = edtOldPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String repeatPassword = etPassword.getText().toString().trim();

            // Validate
            if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(repeatPassword)) {
                Toast.makeText(this, "New password and repeat password do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API
            ChangePasswordRequest request = new ChangePasswordRequest(email, oldPassword, newPassword);
            callChangePasswordApi(request);
        });
    }

    private void initView(){
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        etPassword = findViewById(R.id.etPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleOldPassword = findViewById(R.id.ivToggleOldPassword);
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open); // icon con mắt mở
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed); // icon con mắt đóng
        }
        // Di chuyển con trỏ về cuối chuỗi khi đổi type
        editText.setSelection(editText.getText().length());
    }

    private void callChangePasswordApi(ChangePasswordRequest request) {
        UserApi apiService = BaseURL.getUrl(ChangePasswordActivity.this).create(UserApi.class);

        Call<ApiResponse<String>> call = apiService.changePassword(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    Toast.makeText(ChangePasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình sau khi đổi mật khẩu thành công
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Change password failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}