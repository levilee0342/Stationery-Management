package com.example.office_management.adapter;

import android.support.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.office_management.fragment.orderstatus.OrderListFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrderListFragment.newInstance("COMPLETED");
            case 1:
                return OrderListFragment.newInstance("PENDING");
            case 2:
                return OrderListFragment.newInstance("PROCESSING");
            case 3:
                return OrderListFragment.newInstance("SHIPPING");
            case 4:
                return OrderListFragment.newInstance("CANCELED");
            default:
                return OrderListFragment.newInstance("COMPLETED");
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}


