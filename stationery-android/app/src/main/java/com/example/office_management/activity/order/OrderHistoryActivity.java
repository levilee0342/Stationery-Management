package com.example.office_management.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.adapter.OrdersPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrdersPagerAdapter adapter;
    private ImageButton btnBack, btnCart, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btnHome);
        btnCart = findViewById(R.id.btnCart);

        adapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Complete"); break;
                case 1: tab.setText("Pending Payment"); break;
                case 2: tab.setText("Processing"); break;
                case 3: tab.setText("Shipping"); break;
                case 4: tab.setText("Canceled"); break;
            }
        }).attach();

        // ✅ Nhận tab index từ Intent và set tab tương ứng
        int selectedTab = getIntent().getIntExtra("selectedTab", 0); // mặc định là 0
        viewPager.setCurrentItem(selectedTab, false);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            intent.putExtra("openHome", true); // Gửi cờ mở Home
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            intent.putExtra("openCart", true); // Gửi thông tin muốn mở giỏ hàng
            startActivity(intent);
        });
    }
}
