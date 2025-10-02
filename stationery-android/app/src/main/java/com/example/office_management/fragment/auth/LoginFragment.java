package com.example.office_management.fragment.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.fragment.app.Fragment;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.activity.auth.ForgotPasswordActivity;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.DeviceTokenRequest;
import com.example.office_management.dto.response.auth.LoginGoogleResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.LoginApi;
import com.example.office_management.dto.request.GoogleLoginRequest;
import com.example.office_management.dto.request.LoginRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.LoginResponse;

import com.example.office_management.utils.DialogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.MessageDigest;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private LinearLayout btnLogin, btnGoogleSignIn, btnFingerprintLogin;
    private LoginApi apiService;
    private TextView tvForgotPassword, tvRegister;
    private ImageView ivTogglePassword;
    private CheckBox checkboxRemember;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private ActivityResultLauncher<Intent> resetPasswordLauncher;
    private CredentialManager credentialManager;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private SharedPreferences securePrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = BaseURL.getUrl(requireContext()).create(LoginApi.class);
        credentialManager = CredentialManager.create(requireContext());

        // Initialize EncryptedSharedPreferences
        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            securePrefs = EncryptedSharedPreferences.create(
                    requireContext(),
                    "SecurePrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("LoginFragment", "Failed to initialize EncryptedSharedPreferences", e);
            Toast.makeText(requireContext(), "Error initializing secure storage", Toast.LENGTH_SHORT).show();
            securePrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }

        etEmail = view.findViewById(R.id.email);
        etPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogleSignIn = view.findViewById(R.id.btnLoginGg);
        btnFingerprintLogin = view.findViewById(R.id.btnFingerprintLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        tvRegister = view.findViewById(R.id.btnRegister);
        ivTogglePassword = view.findViewById(R.id.ivTogglePassword);
        checkboxRemember = view.findViewById(R.id.checkboxRemember);

        // Check if biometric authentication is available
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            btnFingerprintLogin.setVisibility(View.GONE);
        }

        // Initialize BiometricPrompt
        biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String savedToken = securePrefs.getString("token", null);
                        if (savedToken != null) {
                            // Validate token with server (optional) or proceed directly
                            validateTokenAndProceed(savedToken);
                        } else {
                            Toast.makeText(requireContext(), "No saved session. Please log in first.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(requireContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        btnLogin.setOnClickListener(v -> login(etEmail.getText().toString(), etPassword.getText().toString()));

        btnGoogleSignIn.setOnClickListener(v -> {
            if (checkPlayServices()) {
                signInWithGoogle();
            } else {
                Toast.makeText(requireContext(), "Google Play Services not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnFingerprintLogin.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

        tvForgotPassword.setOnClickListener(v -> resetPasswordLauncher.launch(new Intent(getContext(), ForgotPasswordActivity.class)));

        tvRegister.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
            bottomNav.setSelectedItemId(R.id.register);
        });

        resetPasswordLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("gotoLogin", false)) {
                            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
                            bottomNav.setSelectedItemId(R.id.login);
                        }
                    }
                }
        );

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> btnGoogleSignIn.setEnabled(true)
        );

        ivTogglePassword.setOnClickListener(v -> {
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Load saved email if "remember me" is checked
        boolean isRemembered = securePrefs.getBoolean("remember_me", false);
        if (isRemembered) {
            String savedEmail = securePrefs.getString("saved_email", "");
            etEmail.setText(savedEmail);
            checkboxRemember.setChecked(true);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            return false;
        }
        return true;
    }

    private void login(String email, String password) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        SharedPreferences.Editor editor = securePrefs.edit();
                        if (checkboxRemember.isChecked()) {
                            editor.putBoolean("remember_me", true);
                            editor.putString("saved_email", email);
                        } else {
                            editor.remove("remember_me");
                            editor.remove("saved_email");
                        }
                        editor.putString("token", token);
                        editor.apply();

                        if (isAdded()) {
                            DialogUtils.showSuccessToast(requireContext(), "Login successful!");
                            onLoginSuccess(token);
                            String userId = response.body().getResult().getUserData().getUserId();
                            Log.d("FCM Login", "UserId: " + userId);
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        String deviceToken = task.getResult();
                                        Log.d("FCM", "Device Token: " + deviceToken);
                                        if (isAdded()) {
                                            sendDeviceTokenToServer(userId, deviceToken);
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed: Invalid token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateTokenAndProceed(String token) {
        if (token != null && !token.isEmpty()) {
            if (isAdded()) {
                DialogUtils.showSuccessToast(requireContext(), "Đăng nhập bằng vân tay thành công!");
                onLoginSuccess(token);
            }
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy phiên đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            securePrefs.edit().remove("token").apply();
        }
    }

    private void signInWithGoogle() {
        String nonce = UUID.randomUUID().toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(nonce.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            nonce = hexString.toString();
        } catch (Exception ignored) {}

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.google_client_id))
                .setNonce(nonce)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                Runnable::run,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleSignInResult(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        if (!(e instanceof GetCredentialCancellationException)) {
                            Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void handleGoogleSignInResult(GetCredentialResponse result) {
        if (result.getCredential() instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) result.getCredential();
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                if (credential.getIdToken() != null) {
                    loginWithGoogle(credential.getIdToken());
                } else {
                    Toast.makeText(requireContext(), "Google Sign-In failed: No ID token", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithGoogle(String idToken) {
        apiService.loginWithGoogle(new GoogleLoginRequest(idToken)).enqueue(new Callback<ApiResponse<LoginGoogleResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginGoogleResponse>> call, Response<ApiResponse<LoginGoogleResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    String token = response.body().getResult().getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        securePrefs.edit().putString("token", token).apply();
                        if (isAdded()) {
                            DialogUtils.showSuccessToast(requireContext(), "Google Sign-In successful!");
                            onLoginSuccess(token);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Google Sign-In failed: Invalid token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginGoogleResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoginSuccess(String token) {
        Log.d("Login Success", "Token: " + token);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onLoginSuccess(token);
        } else {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("default_page", 0);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void sendDeviceTokenToServer(String userId, String deviceToken) {
        String token = securePrefs.getString("token", null);
        String authHeader = "Bearer " + token;
        Log.d("FCM", "JWT token: " + token);

        UserApi apiService = BaseURL.getUrl(requireContext()).create(UserApi.class);
        DeviceTokenRequest request = new DeviceTokenRequest(deviceToken);
        apiService.updateDeviceToken(authHeader, userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("DeviceToken", "Device token updated successfully");
                } else {
                    Log.e("DeviceToken", "Failed to update device token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DeviceToken", "Error: " + t.getMessage());
            }
        });
    }
}