package com.example.office_management.fragment.auth;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.office_management.R;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.RegisterApi;
import com.example.office_management.dto.request.RegisterRequest;
import com.example.office_management.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    ImageView ivTogglePassword;
    EditText etFirstName, etLastName, etEmail, etPassword;
    TextView tvLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy các EditText và TextView từ layout
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        tvLogin = view.findViewById(R.id.btnLogin);
        ivTogglePassword = view.findViewById(R.id.ivTogglePassword);

        // Xử lý sự kiện khi người dùng click vào nút đăng nhập
        tvLogin.setOnClickListener(v -> {
            // Chuyển đến màn hình đăng nhập
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
            bottomNav.setSelectedItemId(R.id.login);
        });

        // Xử lý đăng ký khi người dùng click vào nút đăng ký
        view.findViewById(R.id.btnRegister).setOnClickListener(v -> handleRegister());

        ivTogglePassword.setOnClickListener(v -> {
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                // Hide password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            // Move cursor to end
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void handleRegister() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng RegisterRequest
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);

        // Gọi API
        RegisterApi service = BaseURL.getUrl(getContext()).create(RegisterApi.class);
        Call<User> call = service.register(request);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Vui lòng nhập OTP được gửi tới email!", Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);

                    OtpVerificationFragment otpFragment = new OtpVerificationFragment();
                    otpFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, otpFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(getContext(), "Đăng ký thất bại: " + error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Đăng ký thất bại: Không rõ lỗi", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
