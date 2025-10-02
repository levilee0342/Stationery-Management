package com.example.office_management.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.office_management.fragment.cart.ShoppingCartFragment;
import com.example.office_management.fragment.category.CategoryListFragment;
import com.example.office_management.fragment.chatbot.ChatBotFragment;
import com.example.office_management.fragment.user.AccountFragment;
import com.example.office_management.fragment.user.AccountUserFragment;
import com.example.office_management.fragment.user.HomeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private boolean isLoggedIn;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, boolean isLoggedIn) {
        super(fragmentActivity);
        this.isLoggedIn = isLoggedIn;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CategoryListFragment();
            case 4:

                // Kiểm tra trạng thái đăng nhập để quyết định hiển thị Fragment nào
                return isLoggedIn ? new AccountUserFragment() : new AccountFragment();
            case 2:
                return new ChatBotFragment();
            case 3:
                return new ShoppingCartFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    // Phương thức để cập nhật trạng thái đăng nhập
    public void setLoggedIn(boolean loggedIn) {
        if (this.isLoggedIn != loggedIn) {
            this.isLoggedIn = loggedIn;
            notifyDataSetChanged();
        }
    }

    // Phương thức này để tạo ID duy nhất cho mỗi fragment dựa trên vị trí và trạng thái đăng nhập
    @Override
    public long getItemId(int position) {
        if (position == 4) {
            return isLoggedIn ? 1001 : 1000; // ID khác nhau cho AccountFragment và UserInfoFragment
        }
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        if (itemId == 1000 || itemId == 1001) {
            return true; // Chấp nhận cả ID của AccountFragment và UserInfoFragment
        }
        return itemId >= 0 && itemId < 5; // Kiểm tra ID hợp lệ của các fragment
    }
}