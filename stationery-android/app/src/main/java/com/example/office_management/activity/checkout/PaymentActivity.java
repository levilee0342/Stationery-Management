package com.example.office_management.activity.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;
import com.example.office_management.activity.user.CouponCodeActivity;
import com.example.office_management.adapter.CouponAdapter;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.model.Promotion;
import com.example.office_management.model.UserPromotion;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Button btnCheckout;
    private LinearLayout ll_coupon_code, momoLayout, cashLayout;
    private RadioButton radioBtnMomo, radioBtnCash;
    private ImageButton btnBack;
    private TextView tvAmount, tvDiscount, tvTotalAmount;
    private List<CartResponse> selectedItems;
    private AddressResponse selectedAddress;
    private Promotion appliedPromotion;
    private int totalPrice;
    private String selectedPaymentMethod, note;
    private CouponAdapter adapter;
    private int discountAmount;
    private String userPromotionId;
    private final ActivityResultLauncher<Intent> couponLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    UserPromotion selectedUserPromotion = (UserPromotion) result.getData().getSerializableExtra("selectedPromotion");
                    Promotion selectedPromotion = selectedUserPromotion.getPromotion();
                    String userPromotionId = result.getData().getStringExtra("userPromotionId");

                    if (selectedPromotion != null && userPromotionId != null) {
                        this.userPromotionId = userPromotionId;
                        applyPromotion(selectedPromotion);
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedItems = (List<CartResponse>) intent.getSerializableExtra("selectedItems");
        totalPrice = intent.getIntExtra("totalPrice", 0);
        selectedAddress = (AddressResponse) intent.getSerializableExtra("selectedAddress");
        note = intent.getStringExtra("customer_note");

        setBackground();
        initViews();
        paymentMethob();
        updateTotalAmount();

        btnCheckout.setOnClickListener(v -> {
            // Truyền dữ liệu sang CheckOrderActivity
            Intent checkoutIntent = new Intent(PaymentActivity.this, CheckOrderActivity.class);
            checkoutIntent.putExtra("selectedItems", (Serializable) selectedItems);
            checkoutIntent.putExtra("selectedAddress", (Serializable) selectedAddress);
            checkoutIntent.putExtra("totalAmount", calculateTotalAmount());
            checkoutIntent.putExtra("paymentMethod", selectedPaymentMethod);
            checkoutIntent.putExtra("userPromotionId", userPromotionId);
            checkoutIntent.putExtra("customer_note", note);
            startActivity(checkoutIntent);
        });

        ll_coupon_code.setOnClickListener(v -> {
            Intent couponIntent = new Intent(PaymentActivity.this, CouponCodeActivity.class);
            couponIntent.putExtra("totalPrice", totalPrice);
            couponLauncher.launch(couponIntent);
        });

        btnBack.setOnClickListener(v -> { onBackPressed(); });

    }

    private void setBackground(){
        ((ImageView) findViewById(R.id.step1Icon)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.step1Number)).setVisibility(View.GONE);

        ((ImageView) findViewById(R.id.step2Icon)).setVisibility(View.GONE);
        findViewById(R.id.step2Frame).setBackgroundResource(R.drawable.bg_step_active);

        findViewById(R.id.line1).setBackgroundColor(Color.parseColor("#4CAF50"));
    }

    private void initViews() {
        btnCheckout = findViewById(R.id.btnCheckout);
        ll_coupon_code = findViewById(R.id.coupon_code);
        btnBack = findViewById(R.id.btn_back);
        radioBtnCash = findViewById(R.id.radioPayment);
        radioBtnMomo = findViewById(R.id.radioMomo);
        tvAmount = findViewById(R.id.amount);
        tvDiscount = findViewById(R.id.discount);
        tvTotalAmount = findViewById(R.id.total_amount);
        momoLayout = findViewById(R.id.layoutMomo);
        cashLayout = findViewById(R.id.layoutCash);
    }

    private void paymentMethob(){
        // Khai báo map giữa layout và radio button
        Map<LinearLayout, RadioButton> paymentOptions = new HashMap<>();

        radioBtnCash.setClickable(false);
        radioBtnCash.setFocusable(false);
        radioBtnMomo.setClickable(false);
        radioBtnMomo.setFocusable(false);

        // Ánh xạ layout với radio button tương ứng
        paymentOptions.put(cashLayout, radioBtnCash);
        paymentOptions.put(momoLayout, radioBtnMomo);

        // Chọn mặc định là MoMo
        radioBtnMomo.setChecked(true);
        selectedPaymentMethod = "MOMO";

        View.OnClickListener layoutClickListener = clickedLayout -> {
            for (Map.Entry<LinearLayout, RadioButton> entry : paymentOptions.entrySet()) {
                RadioButton rb = entry.getValue();
                boolean isSelected = entry.getKey().getId() == clickedLayout.getId();
                rb.setChecked(isSelected);
                if (isSelected) {
                    selectedPaymentMethod = rb == radioBtnCash ? "CASH" : "MOMO";
                }
            }
        };

        // Gán cùng một listener cho tất cả layout
        for (LinearLayout layout : paymentOptions.keySet()) {
            layout.setOnClickListener(layoutClickListener);
        }
    }
    // ✅ Áp dụng mã giảm giá
    private void applyPromotion(Promotion promotion) {
        this.appliedPromotion = promotion;
        this.discountAmount = 0; // reset trước

        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
            int calculatedDiscount = (totalPrice * promotion.getDiscountValue()) / 100;
            discountAmount = (promotion.getMaxValue() != null)
                    ? Math.min(calculatedDiscount, promotion.getMaxValue())
                    : calculatedDiscount;
        } else {
            discountAmount = promotion.getDiscountValue();
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        tvDiscount.setText(String.format("- %s đ", formatter.format(discountAmount)));

        updateTotalAmount();
    }

    // ✅ Tính và hiển thị tổng số tiền
    private void updateTotalAmount() {
        DecimalFormat formatter = new DecimalFormat("#,###");

        tvAmount.setText(String.format("%s đ", formatter.format(totalPrice)));
        tvTotalAmount.setText(String.format("%s đ", formatter.format(calculateTotalAmount())));
    }

    // ✅ Tính tổng số tiền đã áp dụng giảm giá (không dựa vào TextView)
    private int calculateTotalAmount() {
        int totalAmount = totalPrice - discountAmount;
        return Math.max(totalAmount, 0); // đảm bảo không âm
    }
}