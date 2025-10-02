package com.example.office_management.fragment.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.office_management.R;
import com.example.office_management.fragment.auth.LoginFragment;
import com.example.office_management.fragment.auth.RegisterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        super(R.layout.fragment_account); // Layout chứa BottomNavigationView + FrameLayout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.account_bottom_navigation);

        // Kiểm tra đăng nhập
        if (!isLoggedIn()) {
            loadFragment(new LoginFragment());
            bottomNavigationView.setSelectedItemId(R.id.login);
        } else {
            // Nếu cần, bạn có thể hiển thị fragment khác khi đã login
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.login) {
                selectedFragment = new LoginFragment();
            } else if (itemId == R.id.register) {
                selectedFragment = new RegisterFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }
}
