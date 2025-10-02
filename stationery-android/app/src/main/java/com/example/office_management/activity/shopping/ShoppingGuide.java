package com.example.office_management.activity.shopping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.office_management.R;
import com.example.office_management.adapter.ProductAdapter;
import com.example.office_management.adapter.VoucherAdapter;
import com.example.office_management.api.ProductApi;
import com.example.office_management.api.UserPromotionApi;
import com.example.office_management.databinding.ActivityShoppingGuideBinding;
import com.example.office_management.decoration.GridSpacingItemDecoration;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.model.UserPromotion;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingGuide extends AppCompatActivity {

    private ActivityShoppingGuideBinding binding;
    private ProductApi productApi;
    private String category;
    private ProductAdapter productAdapter;
    private String currentSortBy = "popular";
    private String currentMinPrice = "";
    private String currentMaxPrice = "";
    private String currentTotalRating = "";
    private String currentSearch = "";
    private final Map<String, String> sortByMapping = new HashMap<>();
    private static final float MAX_PRICE_LIMIT = 10000000; // 10 million

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sort mapping
        initializeSortMapping();

        // Get category from Intent
        category = getIntent().getStringExtra("category");
        if (category == null) category = "flash_sale"; // Default category

        // Initialize API
        productApi = BaseURL.getUrl(this).create(ProductApi.class);

        // Set up header
        setupHeader(category);

        // Set up banner
        setupBanner(category);

        // Set up voucher RecyclerView
        setupVoucherRecyclerView();

        // Set up RecyclerView for products
        setupRecyclerView();

        // Set up SVG images
        setupSvgImages();

        // Set up click listeners
        setupClickListeners();

        // Set up search and filter
        setupSearchListener();
        setupFilterListener();

        // Initial fetch
        fetchProducts(currentSortBy);
    }

    private void initializeSortMapping() {
        sortByMapping.put("popular", "soldQuantity");
        sortByMapping.put("best_selling", "-soldQuantity");
        sortByMapping.put("new_arrivals", "-createdAt");
        sortByMapping.put("price_asc", "minPrice");
        sortByMapping.put("price_desc", "-minPrice");
    }

    private void setupHeader(String category) {
        String title;
        switch (category) {
            case "voucher":
                title = "Vouchers";
                break;
            case "new_product":
                title = "New Products";
                break;
            case "price_support":
                title = "Price Support";
                break;
            case "wholesale":
                title = "Wholesale";
                break;
            case "flash_sale":
            default:
                title = "Flash Sale";
                break;
        }
        binding.tvTitle.setText(title);
    }

    private void setupBanner(String category) {
        int bannerResId;
        switch (category) {
            case "voucher":
                bannerResId = R.drawable.img_banner_1;
                break;
            case "new_product":
                bannerResId = R.drawable.img_banner_2;
                break;
            case "price_support":
                bannerResId = R.drawable.img_banner_3;
                break;
            case "wholesale":
                bannerResId = R.drawable.img_banner_4;
                break;
            case "flash_sale":
            default:
                bannerResId = R.drawable.img_banner_5;
                break;
        }
        binding.imgBanner.setImageResource(bannerResId);
    }

    private void setupVoucherRecyclerView() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        UserPromotionApi userPromotionApi = BaseURL.getUrl(this).create(UserPromotionApi.class);
        Call<ApiResponse<List<UserPromotion>>> call = userPromotionApi.getUserPromotions(authHeader);

        call.enqueue(new Callback<ApiResponse<List<UserPromotion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserPromotion>>> call, Response<ApiResponse<List<UserPromotion>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserPromotion> vouchers = response.body().getResult();
                    VoucherAdapter adapter = new VoucherAdapter(ShoppingGuide.this, vouchers);
                    binding.recyclerMainVouchers.setLayoutManager(new LinearLayoutManager(ShoppingGuide.this));
                    binding.recyclerMainVouchers.setAdapter(adapter);
                } else {
                    Toast.makeText(ShoppingGuide.this, "Không thể tải dữ liệu voucher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserPromotion>>> call, Throwable t) {
                Toast.makeText(ShoppingGuide.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingInPixels = (int) (getResources().getDisplayMetrics().density * 8);
        binding.recyclerProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        productAdapter = new ProductAdapter(this, new ArrayList<>());
        binding.recyclerProducts.setAdapter(productAdapter);
    }

    private void setupSvgImages() {
        try {
            SVG svg = SVG.getFromAsset(getAssets(), "svg_images/voucher_the_sale.svg");
            SVG svgFreeShip = SVG.getFromAsset(getAssets(), "svg_images/free_ship.svg");
            PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
            PictureDrawable drawableFreeShip = new PictureDrawable(svgFreeShip.renderToPicture());
            binding.imgSvgSale.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            binding.imgSvgSale.setImageDrawable(drawable);
            binding.imgSvgFreeShip.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            binding.imgSvgFreeShip.setImageDrawable(drawableFreeShip);
        } catch (SVGParseException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải hình ảnh SVG", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnShare.setOnClickListener(v -> shareContent(category));
    }

    private void setupSearchListener() {
        TextInputEditText searchEditText = findViewById(R.id.searchEditText);
        TextInputLayout searchBarLayout = findViewById(R.id.searchBarLayout);
        if (searchEditText == null || searchBarLayout == null) {
            return; // Skip if views are not present in layout
        }

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    query = query.replaceAll("\\s+", " ").trim();
                    try {
                        currentSearch = URLEncoder.encode(query, "UTF-8");
                    } catch (Exception e) {
                        currentSearch = query;
                    }
                    fetchProducts(currentSortBy);
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
        searchBarLayout.setEndIconOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                query = query.replaceAll("\\s+", " ").trim();
                try {
                    currentSearch = URLEncoder.encode(query, "UTF-8");
                } catch (Exception e) {
                    currentSearch = query;
                }
                fetchProducts(currentSortBy);
            } else if (!currentSearch.isEmpty()) {
                currentSearch = "";
                searchEditText.setText("");
                searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
                fetchProducts(currentSortBy);
            } else {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
            }
        });
    }

    private void setupFilterListener() {
        binding.tvDealTitle.setOnClickListener(v -> showPriceFilterDialog());
    }

    private void fetchProducts(String sortBy) {
        String backendSortBy = sortByMapping.getOrDefault(sortBy, "soldQuantity");
        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                backendSortBy, currentMinPrice, currentMaxPrice, "", currentSearch, currentTotalRating,1,10
        );

        binding.recyclerProducts.setOnClickListener(null); // Reset retry listener
        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> productList = response.body().getResult().getContent();
                    if (productList != null && !productList.isEmpty()) {
                        List<ProductResponse> filteredList = new ArrayList<>();
                        double priceThreshold = 100000; // Giá trị ngưỡng cho price_support

                        for (ProductResponse product : productList) {
                            ProductDetailResponse detail = product.getProductDetail();
                            if (detail == null) continue;
                            if (category.equals("flash_sale") && detail.getDiscountPrice() > 0) {
                                filteredList.add(product);
                            } else if (category.equals("new_product") && isProductNew(product)) {
                                filteredList.add(product);
                            } else if (category.equals("price_support") && detail.getOriginalPrice() <= priceThreshold) {
                                filteredList.add(product);
                            } else if (category.equals("wholesale") && isProductWholesale(product)) {
                                filteredList.add(product);
                            } else if (category.equals("voucher")) {
                                filteredList.add(product); // Hiển thị tất cả nếu là voucher
                            }
                        }

                        if (!filteredList.isEmpty()) {
                            productAdapter.setData(filteredList);
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ShoppingGuide.this, "Không có sản phẩm trong danh mục này", Toast.LENGTH_SHORT).show();
                            productAdapter.setData(new ArrayList<>());
                            productAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ShoppingGuide.this, "Danh sách sản phẩm trống", Toast.LENGTH_SHORT).show();
                        productAdapter.setData(new ArrayList<>());
                        productAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(ShoppingGuide.this, "Không thể tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                Toast.makeText(ShoppingGuide.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.recyclerProducts.setOnClickListener(v -> fetchProducts(currentSortBy));
            }
        });
    }

    private boolean isProductNew(ProductResponse product) {
        String createdAt = product.getCreatedAt();
        if (createdAt == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date createdDate = sdf.parse(createdAt);
            Date currentDate = new Date();
            long diffInMillies = Math.abs(currentDate.getTime() - createdDate.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return diffInDays <= 7;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isProductWholesale(ProductResponse product) {
        ProductDetailResponse detail = product.getProductDetail();
        return detail != null && detail.getStockQuantity() >= 100;
    }

    private void shareContent(String category) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this deal!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Explore amazing " + category.replace("_", " ") + " deals on our app!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void showPriceFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_price_filter, null);
        builder.setView(dialogView);

        RadioGroup rgRating = dialogView.findViewById(R.id.rgRating);
        ChipGroup chipGroupSortBy = dialogView.findViewById(R.id.chipGroupSortBy);
        TextInputEditText etMinPrice = dialogView.findViewById(R.id.etMinPrice);
        TextInputEditText etMaxPrice = dialogView.findViewById(R.id.etMaxPrice);
        RangeSlider rangeSliderPrice = dialogView.findViewById(R.id.rangeSliderPrice);
        Button btnReset = dialogView.findViewById(R.id.btnReset);
        Button btnApply = dialogView.findViewById(R.id.btnApply);

        // Set current rating
        if (!currentTotalRating.isEmpty()) {
            int rating = Integer.parseInt(currentTotalRating);
            if (rating == 3) rgRating.check(R.id.rbRating3);
            else if (rating == 4) rgRating.check(R.id.rbRating4);
            else if (rating == 5) rgRating.check(R.id.rbRating5);
        }

        Chip chipPopular = dialogView.findViewById(R.id.chipPopular);
        Chip chipBestSelling = dialogView.findViewById(R.id.chipBestSelling);
        Chip chipNewArrivals = dialogView.findViewById(R.id.chipNewArrivals);
        chipPopular.setChecked(currentSortBy.equals("popular"));
        chipBestSelling.setChecked(currentSortBy.equals("best_selling"));
        chipNewArrivals.setChecked(currentSortBy.equals("new_arrivals"));

        etMinPrice.setText(currentMinPrice);
        etMaxPrice.setText(currentMaxPrice);
        try {
            float minPrice = currentMinPrice.isEmpty() ? 0 : Float.parseFloat(currentMinPrice);
            float maxPrice = currentMaxPrice.isEmpty() ? 1000000 : Float.parseFloat(currentMaxPrice);
            rangeSliderPrice.setValues(minPrice, maxPrice);
        } catch (NumberFormatException e) {
            rangeSliderPrice.setValues(0f, 1000000f);
        }

        rangeSliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            etMinPrice.setText(String.valueOf(values.get(0).intValue()));
            etMaxPrice.setText(String.valueOf(values.get(1).intValue()));
        });

        etMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float minPrice = s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString());
                    float maxPrice = etMaxPrice.getText().toString().isEmpty() ? 1000000 : Float.parseFloat(etMaxPrice.getText().toString());
                    if (minPrice >= 0 && minPrice <= maxPrice && minPrice <= MAX_PRICE_LIMIT) {
                        rangeSliderPrice.setValues(minPrice, maxPrice);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        etMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float maxPrice = s.toString().isEmpty() ? 1000000 : Float.parseFloat(s.toString());
                    float minPrice = etMinPrice.getText().toString().isEmpty() ? 0 : Float.parseFloat(etMinPrice.getText().toString());
                    if (maxPrice >= 0 && minPrice <= maxPrice && maxPrice <= MAX_PRICE_LIMIT) {
                        rangeSliderPrice.setValues(minPrice, maxPrice);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        AlertDialog dialog = builder.create();

        btnApply.setOnClickListener(v -> {
            int checkedRatingId = rgRating.getCheckedRadioButtonId();
            if (checkedRatingId == R.id.rbRating3) {
                currentTotalRating = "3";
            } else if (checkedRatingId == R.id.rbRating4) {
                currentTotalRating = "4";
            } else if (checkedRatingId == R.id.rbRating5) {
                currentTotalRating = "5";
            } else {
                currentTotalRating = "";
            }

            int checkedChipId = chipGroupSortBy.getCheckedChipId();
            if (checkedChipId == R.id.chipPopular) currentSortBy = "popular";
            else if (checkedChipId == R.id.chipNewArrivals) currentSortBy = "new_arrivals";
            else if (checkedChipId == R.id.chipBestSelling) currentSortBy = "best_selling";

            String minPriceStr = etMinPrice.getText().toString().trim();
            String maxPriceStr = etMaxPrice.getText().toString().trim();
            if (!minPriceStr.isEmpty() || !maxPriceStr.isEmpty()) {
                try {
                    float minPrice = minPriceStr.isEmpty() ? 0 : Float.parseFloat(minPriceStr);
                    float maxPrice = maxPriceStr.isEmpty() ? 1000000 : Float.parseFloat(maxPriceStr);
                    if (minPrice < 0 || maxPrice < 0) {
                        Toast.makeText(ShoppingGuide.this, "Giá không được âm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (minPrice > maxPrice) {
                        Toast.makeText(ShoppingGuide.this, "Giá tối thiểu phải nhỏ hơn hoặc bằng giá tối đa", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (minPrice > MAX_PRICE_LIMIT || maxPrice > MAX_PRICE_LIMIT) {
                        Toast.makeText(ShoppingGuide.this, "Giá không được vượt quá " + MAX_PRICE_LIMIT, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentMinPrice = minPriceStr;
                    currentMaxPrice = maxPriceStr;
                } catch (NumberFormatException e) {
                    Toast.makeText(ShoppingGuide.this, "Vui lòng nhập số hợp lệ cho giá", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                currentMinPrice = "";
                currentMaxPrice = "";
            }

            fetchProducts(currentSortBy);
            dialog.dismiss();
        });

        btnReset.setOnClickListener(v -> {
            rgRating.clearCheck();
            chipGroupSortBy.check(R.id.chipPopular);
            etMinPrice.setText("");
            etMaxPrice.setText("");
            rangeSliderPrice.setValues(0f, 1000000f);
            currentTotalRating = "";
            currentSortBy = "popular";
            currentMinPrice = "";
            currentMaxPrice = "";
            currentSearch = "";
            fetchProducts(currentSortBy);
            dialog.dismiss();
        });

        dialog.show();
    }
}