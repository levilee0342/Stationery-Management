package com.example.office_management.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.LoginApi;
import com.example.office_management.dto.request.ForgotPasswordRequest;
import com.example.office_management.dto.request.OtpVerificationRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtInput, edtNewPassword;
    private TextView tvSendOtp;
    private LoginApi apiService;
    private Button btnConfirm;
    private ImageButton btnBack;
    private EditText[] otpInputs;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = BaseURL.getUrl(this).create(LoginApi.class);

        edtInput = findViewById(R.id.edtInput);
        tvSendOtp = findViewById(R.id.tvSendOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btn_back);

        otpInputs = new EditText[] {
                findViewById(R.id.otp1),
                findViewById(R.id.otp2),
                findViewById(R.id.otp3),
                findViewById(R.id.otp4),
                findViewById(R.id.otp5),
                findViewById(R.id.otp6)
        };
        setupOtpInputs();

        tvSendOtp.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                return;
            }

            String input = edtInput.getText().toString().trim();
            if (!input.isEmpty()) {
                forgotPasswordEmail(input);
            } else {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirm.setEnabled(false); // Prevent double submission

            String input = edtInput.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                btnConfirm.setEnabled(true);
                return;
            }

            StringBuilder otpBuilder = new StringBuilder();
            for (EditText otpInput : otpInputs) {
                otpBuilder.append(otpInput.getText().toString().trim());
            }
            String otpString = otpBuilder.toString();

            if (input.isEmpty() || newPassword.isEmpty() || otpString.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                btnConfirm.setEnabled(true);
                return;
            }

            try {
                int otp = Integer.parseInt(otpString);
                resetPasswordEmail(input, otp, newPassword);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                btnConfirm.setEnabled(true);
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void forgotPasswordEmail(String email) {
        tvSendOtp.setEnabled(false);
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);
        apiService.forgotPassword(forgotPasswordRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tvSendOtp.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi email khôi phục mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Gửi email thất bại", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Response code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tvSendOtp.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng khi gửi email", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void resetPasswordEmail(String email, int otp, String newPassword) {
        OtpVerificationRequest request = new OtpVerificationRequest(email, otp);
        apiService.resetPassword(request, newPassword).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnConfirm.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("goToLogin", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Response code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnConfirm.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng khi đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;

            otpInputs[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpInputs[index].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpInputs[index].getText().toString().isEmpty() && index > 0) {
                        otpInputs[index - 1].requestFocus();
                        otpInputs[index - 1].setText("");
                    }
                }
                return false;
            });

            otpInputs[index].setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    ((EditText) v).setSelection(((EditText) v).getText().length());
                }
            });
        }
    }

    private void clearOtpFields() {
        for (EditText otpInput : otpInputs) {
            otpInput.setText("");
        }
        otpInputs[0].requestFocus();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}