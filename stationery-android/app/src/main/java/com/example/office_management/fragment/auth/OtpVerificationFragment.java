package com.example.office_management.fragment.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.office_management.R;
import com.example.office_management.api.RegisterApi;
import com.example.office_management.dto.request.OtpVerificationRequest;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationFragment extends Fragment {

    private EditText[] otpInputs = new EditText[6];
    private String email;
    private TextView tvCountdown, tvResendOtp;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_TIME = 5 * 60 * 1000; // 5 phút

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = getArguments() != null ? getArguments().getString("email") : "";

        otpInputs[0] = view.findViewById(R.id.etOtp1);
        otpInputs[1] = view.findViewById(R.id.etOtp2);
        otpInputs[2] = view.findViewById(R.id.etOtp3);
        otpInputs[3] = view.findViewById(R.id.etOtp4);
        otpInputs[4] = view.findViewById(R.id.etOtp5);
        otpInputs[5] = view.findViewById(R.id.etOtp6);

        tvCountdown = view.findViewById(R.id.tvCountdown);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);

        setupOtpInputs();
        startCountdown();

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> handleOtpVerification());
        tvResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void handleOtpVerification() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText et : otpInputs) {
            String digit = et.getText().toString();
            if (digit.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            otpBuilder.append(digit);
        }

        int otp = Integer.parseInt(otpBuilder.toString());

        OtpVerificationRequest request = new OtpVerificationRequest(email, otp);

        RegisterApi service = BaseURL.getUrl(getContext()).create(RegisterApi.class);
        Call<Void> call = service.verifyOTP(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Xác thực thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
                    bottomNav.setSelectedItemId(R.id.login);
                } else {
                    Toast.makeText(getContext(), "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendOtp() {
        // Gửi yêu cầu gửi lại OTP
        OtpVerificationRequest request = new OtpVerificationRequest(email, 0); // OTP để 0 vì backend chỉ cần email
        RegisterApi service = BaseURL.getUrl(getContext()).create(RegisterApi.class);
        Call<Void> call = service.resendOtp(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show();
                    resetCountdown(); // Reset lại thời gian đếm ngược
                } else {
                    Toast.makeText(getContext(), "Gửi lại OTP thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvCountdown.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("00:00");
            }
        }.start();
    }

    private void resetCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startCountdown();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;

            otpInputs[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
