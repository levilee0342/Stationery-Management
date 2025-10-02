package com.example.office_management.activity.product;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.search.ProductSearchActivity;
import com.example.office_management.activity.search.SearchActivity;
import com.example.office_management.adapter.FilterProductAdapter;
import com.example.office_management.adapter.ProductCategoryAdapter;
import com.example.office_management.api.CategoryApi;
import com.example.office_management.api.ProductApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.category.CategoryProductResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.model.Category;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView rvProductCategories, rvProductAds;
    private TextView tvPopular, tvBestSelling, tvNewArrivals, tvPrice, tvFilter, tvDeliveryAddress;
    private Button btnToggleCategories;
    private TextInputEditText searchEditText;
    private TextInputLayout searchBarLayout;
    private ProgressBar productProgressBar;
    private ProductCategoryAdapter categoryAdapter;
    private FilterProductAdapter productAdapter;
    private ProductApi productApi;
    private CategoryApi categoryApi;
    private List<Category> categoryList = new ArrayList<>();
    private List<ProductResponse> lastSuccessfulProductList = new ArrayList<>(); // Store last successful product list
    private boolean isExpanded = false;
    private boolean isPriceAscending = true;
    private String currentSortBy = "popular";
    private String currentCategoryId = "";
    private String currentSearch = "";
    private String currentMinPrice = "";
    private String currentMaxPrice = "";
    private String currentTotalRating = "";
    private final Map<String, String> sortByMapping = new HashMap<>();
    private static final float MAX_PRICE_LIMIT = 10000000; // 10 million

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Initialize sort mapping
        initializeSortMapping();

        // Initialize views
        rvProductCategories = findViewById(R.id.rv_sidebar_categories);
        rvProductAds = findViewById(R.id.rvProductAds);
        tvPopular = findViewById(R.id.tvPopular);
        tvBestSelling = findViewById(R.id.tvBestSelling);
        tvNewArrivals = findViewById(R.id.tvNewArrivals);
        tvPrice = findViewById(R.id.tvPrice);
        tvFilter = findViewById(R.id.tvFilter);
        btnToggleCategories = findViewById(R.id.btnToggleCategories);
        searchEditText = findViewById(R.id.searchEditText);
        searchBarLayout = findViewById(R.id.searchBarLayout);
        productProgressBar = findViewById(R.id.productProgressBar);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Initialize APIs
        productApi = BaseURL.getUrl(this).create(ProductApi.class);
        categoryApi = BaseURL.getUrl(this).create(CategoryApi.class);

        // Handle incoming intent data
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("keyword")) {
                currentSearch = intent.getStringExtra("keyword");
                searchEditText.setText(currentSearch);
            }
            if (intent.hasExtra("categoryId")) {
                currentCategoryId = intent.getStringExtra("categoryId");
                Log.d("ProductActivity", "Received categoryId: " + currentCategoryId);
            }
        }

        btnBack.setOnClickListener(v -> { onBackPressed(); });

        ImageView btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            Intent scanIntent = new Intent(ProductActivity.this, ProductSearchActivity.class);
            startActivity(scanIntent);
        });

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup listeners
        setupFilterListeners();
        setupSearchListener();

        // Initial fetch
        fetchProducts(currentSortBy);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra("categoryId")) {
            currentCategoryId = intent.getStringExtra("categoryId");
            Log.d("ProductActivity", "New intent with categoryId: " + currentCategoryId);
            fetchProducts(currentSortBy);
        }
    }

    private void initializeSortMapping() {
        sortByMapping.put("popular", "soldQuantity");
        sortByMapping.put("best_selling", "-soldQuantity");
        sortByMapping.put("new_arrivals", "-createdAt");
        sortByMapping.put("price_asc", "minPrice");
        sortByMapping.put("price_desc", "-minPrice");
    }

    private void setupRecyclerViews() {
        // Setup Product Categories RecyclerView
        loadCategories();

        // Setup toggle button
        btnToggleCategories.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            if (categoryAdapter != null) {
                categoryAdapter.setDisplayItemCount(isExpanded ? 16 : 4);
                btnToggleCategories.setText(isExpanded ? "Thu gọn" : "Xem thêm");
            }
        });

        // Setup Product Ads RecyclerView
        productAdapter = new FilterProductAdapter(this);
        rvProductAds.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductAds.setAdapter(productAdapter);
    }

    private void loadCategories() {
        productProgressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<List<CategoryProductResponse>>> call = categoryApi.apiAllCategory();
        call.enqueue(new Callback<ApiResponse<List<CategoryProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryProductResponse>>> call, Response<ApiResponse<List<CategoryProductResponse>>> response) {
                productProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    categoryList = response.body().getResult().stream()
                            .map(apiCategory -> new Category(
                                    apiCategory.getCategoryId(),
                                    apiCategory.getCategoryName(),
                                    apiCategory.getIcon(),
                                    apiCategory.getBgColor()
                            ))
                            .collect(Collectors.toList());
                    categoryAdapter = new ProductCategoryAdapter(ProductActivity.this, categoryList, category -> {
                        currentCategoryId = category.getCategoryId();
                        currentSearch = ""; // Clear search to avoid conflicts
                        searchEditText.setText(category.getCategoryName()); // Set category name in search bar
                        Log.d("ProductActivity", "Category clicked: " + category.getCategoryName() + ", ID: " + currentCategoryId);
                        fetchProducts(currentSortBy); // Fetch products for selected category
                    });
                    categoryAdapter.setDisplayItemCount(isExpanded ? 16 : 4);
                    rvProductCategories.setLayoutManager(new GridLayoutManager(ProductActivity.this, 4));
                    rvProductCategories.setAdapter(categoryAdapter);
                    tvFilter.setEnabled(true);
                } else {
                    Toast.makeText(ProductActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryProductResponse>>> call, Throwable t) {
                productProgressBar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Lỗi tải danh mục: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterListeners() {
        tvPopular.setOnClickListener(v -> {
            currentSortBy = "popular";
            sortProducts(currentSortBy);
            resetFilterStyles();
            tvPopular.setTextColor(getResources().getColor(R.color.primary_blue));
        });

        tvBestSelling.setOnClickListener(v -> {
            currentSortBy = "best_selling";
            sortProducts(currentSortBy);
            resetFilterStyles();
            tvBestSelling.setTextColor(getResources().getColor(R.color.primary_blue));
        });

        tvNewArrivals.setOnClickListener(v -> {
            currentSortBy = "new_arrivals";
            sortProducts(currentSortBy);
            resetFilterStyles();
            tvNewArrivals.setTextColor(getResources().getColor(R.color.primary_blue));
        });

        tvPrice.setOnClickListener(v -> {
            isPriceAscending = !isPriceAscending;
            tvPrice.setText(isPriceAscending ? "Giá ↑" : "Giá ↓");
            currentSortBy = isPriceAscending ? "price_asc" : "price_desc";
            sortProducts(currentSortBy);
            resetFilterStyles();
            tvPrice.setTextColor(getResources().getColor(R.color.primary_blue));
        });

        tvFilter.setEnabled(false);
        tvFilter.setOnClickListener(v -> {
            if (!categoryList.isEmpty()) {
                showPriceFilterDialog();
            } else {
                Toast.makeText(ProductActivity.this, "Danh mục đang tải", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchListener() {
        // Handle keyboard search action
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
                        Log.e("ProductActivity", "Error encoding search query: " + e.getMessage());
                    }
                    currentCategoryId = ""; // Clear category to prioritize search
                    Log.d("ProductActivity", "Search query (keyboard): " + currentSearch);
                    fetchProducts(currentSortBy);
                } else {
                    Toast.makeText(ProductActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        // Handle search/clear icon click
        searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
        searchBarLayout.setEndIconOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                query = query.replaceAll("\\s+", " ").trim();
                try {
                    currentSearch = URLEncoder.encode(query, "UTF-8");
                } catch (Exception e) {
                    currentSearch = query;
                    Log.e("ProductActivity", "Error encoding search query: " + e.getMessage());
                }
                currentCategoryId = ""; // Clear category to prioritize search
                Log.d("ProductActivity", "Search query (icon): " + currentSearch);
                fetchProducts(currentSortBy);
            } else if (!currentSearch.isEmpty() || !currentCategoryId.isEmpty()) {
                // Clear search and category
                currentSearch = "";
                currentCategoryId = "";
                searchEditText.setText("");
                searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
                Log.d("ProductActivity", "Search and category cleared");
                fetchProducts(currentSortBy);
            } else {
                Toast.makeText(ProductActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        // Update icon based on text input
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Show clear icon if text is present, search icon otherwise
                searchBarLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_search));
            }
        });
    }

    private void resetFilterStyles() {
        tvPopular.setTextColor(getResources().getColor(R.color.gray));
        tvBestSelling.setTextColor(getResources().getColor(R.color.gray));
        tvNewArrivals.setTextColor(getResources().getColor(R.color.gray));
        tvPrice.setTextColor(getResources().getColor(R.color.gray));
    }

    private void sortProducts(String sortType) {
        fetchProducts(sortType);
    }

    private void fetchProducts(String sortBy) {
        String backendSortBy = sortByMapping.getOrDefault(sortBy, "soldQuantity");
        Log.d("API", "Request params: sortBy=" + backendSortBy +
                ", minPrice=" + currentMinPrice + ", maxPrice=" + currentMaxPrice +
                ", categoryId=" + currentCategoryId + ", search=" + currentSearch +
                ", totalRating=" + currentTotalRating);

        productProgressBar.setVisibility(View.VISIBLE);
        rvProductAds.setOnClickListener(null); // Reset retry listener

        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                backendSortBy, currentMinPrice, currentMaxPrice, currentCategoryId, currentSearch, currentTotalRating,0,10
        );

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                productProgressBar.setVisibility(View.GONE);
                Log.d("API", "Response code: " + response.code() + ", Message: " + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<ResultResponse> apiResponse = response.body();
                        List<ProductResponse> fetchedList = apiResponse.getResult().getContent();
                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            // Update last successful product list
                            lastSuccessfulProductList = new ArrayList<>(fetchedList);
                            productAdapter.setData(fetchedList);
                            productAdapter.notifyDataSetChanged(); // Ensure UI updates
                            Log.d("API", "Products received: " + fetchedList.size());
                        } else {
                            Log.d("API", "Empty or null product list for categoryId: " + currentCategoryId + ", search: " + currentSearch);
                            String message = "Không tìm thấy sản phẩm";
                            if (!currentSearch.isEmpty()) {
                                message += " cho từ khóa \"" + currentSearch + "\".";
                            } else if (!currentCategoryId.isEmpty()) {
                                message += " trong danh mục này.";
                            } else {
                                message += ".";
                            }
                            if (!currentTotalRating.isEmpty()) {
                                message += " Hãy thử giảm mức đánh giá.";
                            } else if (!currentMinPrice.isEmpty() || !currentMaxPrice.isEmpty()) {
                                message += " Hãy thử mở rộng khoảng giá.";
                            }
                            Toast.makeText(ProductActivity.this, message, Toast.LENGTH_LONG).show();
                            // Fallback to last successful product list
                            if (!lastSuccessfulProductList.isEmpty()) {
                                productAdapter.setData(lastSuccessfulProductList);
                                productAdapter.notifyDataSetChanged();
                                Log.d("API", "Falling back to last successful product list: " + lastSuccessfulProductList.size());
                            } else {
                                // If no last successful list, fetch all products without filters
                                fetchDefaultProducts();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("API", "JSON parsing error: " + e.getMessage(), e);
                        Toast.makeText(ProductActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        // Fallback to last successful product list
                        if (!lastSuccessfulProductList.isEmpty()) {
                            productAdapter.setData(lastSuccessfulProductList);
                            productAdapter.notifyDataSetChanged();
                        } else {
                            fetchDefaultProducts();
                        }
                    }
                } else {
                    String errorDetails = "Code: " + response.code() + ", Message: " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorDetails += ", Error Body: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("API", "Error reading errorBody: " + e.getMessage(), e);
                    }
                    Log.e("API", "API request failed: " + errorDetails);
                    String message = "Lỗi tải sản phẩm: " + response.code();
                    if (response.code() == 400) {
                        message = "Tham số không hợp lệ. Vui lòng thử lại.";
                    } else if (response.code() == 404) {
                        message = "Danh mục hoặc sản phẩm không tồn tại.";
                    } else if (response.code() == 500) {
                        message = "Lỗi máy chủ. Vui lòng thử lại sau.";
                    }
                    Toast.makeText(ProductActivity.this, message, Toast.LENGTH_LONG).show();
                    // Fallback to last successful product list
                    if (!lastSuccessfulProductList.isEmpty()) {
                        productAdapter.setData(lastSuccessfulProductList);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        fetchDefaultProducts();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                productProgressBar.setVisibility(View.GONE);
                Log.e("API", "Network failure: " + t.getMessage(), t);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối internet. Nhấn để thử lại.";
                    rvProductAds.setOnClickListener(v -> fetchProducts(currentSortBy));
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Hết thời gian chờ. Nhấn để thử lại.";
                    rvProductAds.setOnClickListener(v -> fetchProducts(currentSortBy));
                }
                Toast.makeText(ProductActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                // Fallback to last successful product list
                if (!lastSuccessfulProductList.isEmpty()) {
                    productAdapter.setData(lastSuccessfulProductList);
                    productAdapter.notifyDataSetChanged();
                } else {
                    fetchDefaultProducts();
                }
            }
        });
    }

    private void fetchDefaultProducts() {
        // Fetch all products without filters
        productProgressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                "soldQuantity", "", "", "", "", "",1,10
        );

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                productProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<ResultResponse> apiResponse = response.body();
                        List<ProductResponse> fetchedList = apiResponse.getResult().getContent();
                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            lastSuccessfulProductList = new ArrayList<>(fetchedList);
                            productAdapter.setData(fetchedList);
                            productAdapter.notifyDataSetChanged();
                            Log.d("API", "Default products received: " + fetchedList.size());
                        } else {
                            Toast.makeText(ProductActivity.this, "Không có sản phẩm nào để hiển thị.", Toast.LENGTH_LONG).show();
                            productAdapter.setData(new ArrayList<>());
                            productAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Log.e("API", "JSON parsing error in default fetch: " + e.getMessage(), e);
                        Toast.makeText(ProductActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        productAdapter.setData(new ArrayList<>());
                        productAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API", "Default fetch failed: Code " + response.code());
                    Toast.makeText(ProductActivity.this, "Lỗi tải sản phẩm mặc định: " + response.code(), Toast.LENGTH_LONG).show();
                    productAdapter.setData(new ArrayList<>());
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                productProgressBar.setVisibility(View.GONE);
                Log.e("API", "Default fetch network failure: " + t.getMessage(), t);
                Toast.makeText(ProductActivity.this, "Lỗi kết nối khi tải sản phẩm mặc định: " + t.getMessage(), Toast.LENGTH_LONG).show();
                productAdapter.setData(new ArrayList<>());
                productAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showPriceFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_price_filter, null);
        builder.setView(dialogView);

        RadioGroup rgRating = dialogView.findViewById(R.id.rgRating);
        ChipGroup chipGroupSortBy = dialogView.findViewById(R.id.chipGroupSortBy);
        EditText etMinPrice = dialogView.findViewById(R.id.etMinPrice);
        EditText etMaxPrice = dialogView.findViewById(R.id.etMaxPrice);
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
            // Rating filter
            int checkedRatingId = rgRating.getCheckedRadioButtonId();
            if (checkedRatingId == R.id.rbRating3) {
                currentTotalRating = "3"; // ≥3 stars
            } else if (checkedRatingId == R.id.rbRating4) {
                currentTotalRating = "4"; // ≥4 stars
            } else if (checkedRatingId == R.id.rbRating5) {
                currentTotalRating = "5"; // Exactly 5 stars
            } else {
                currentTotalRating = "";
            }

            // Sort by
            int checkedChipId = chipGroupSortBy.getCheckedChipId();
            if (checkedChipId == R.id.chipPopular) currentSortBy = "popular";
            else if (checkedChipId == R.id.chipBestSelling) currentSortBy = "best_selling";
            else if (checkedChipId == R.id.chipNewArrivals) currentSortBy = "new_arrivals";
            else {
                currentSortBy = "popular";
                chipPopular.setChecked(true);
            }

            // Price range
            String minPriceStr = etMinPrice.getText().toString().trim();
            String maxPriceStr = etMaxPrice.getText().toString().trim();
            if (!minPriceStr.isEmpty() || !maxPriceStr.isEmpty()) {
                try {
                    float minPrice = minPriceStr.isEmpty() ? 0 : Float.parseFloat(minPriceStr);
                    float maxPrice = maxPriceStr.isEmpty() ? 1000000 : Float.parseFloat(maxPriceStr);
                    if (minPrice < 0 || maxPrice < 0) {
                        Toast.makeText(ProductActivity.this, "Giá không được âm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (minPrice > maxPrice) {
                        Toast.makeText(ProductActivity.this, "Giá tối thiểu phải nhỏ hơn hoặc bằng giá tối đa", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (minPrice > MAX_PRICE_LIMIT || maxPrice > MAX_PRICE_LIMIT) {
                        Toast.makeText(ProductActivity.this, "Giá không được vượt quá " + MAX_PRICE_LIMIT, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentMinPrice = minPriceStr;
                    currentMaxPrice = maxPriceStr;
                } catch (NumberFormatException e) {
                    Toast.makeText(ProductActivity.this, "Vui lòng nhập số hợp lệ cho giá", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                currentMinPrice = "";
                currentMaxPrice = "";
            }

            resetFilterStyles();
            if (currentSortBy.equals("popular")) tvPopular.setTextColor(getResources().getColor(R.color.primary_blue));
            else if (currentSortBy.equals("best_selling")) tvBestSelling.setTextColor(getResources().getColor(R.color.primary_blue));
            else if (currentSortBy.equals("new_arrivals")) tvNewArrivals.setTextColor(getResources().getColor(R.color.primary_blue));
            else if (currentSortBy.equals("price_asc") || currentSortBy.equals("price_desc")) {
                tvPrice.setTextColor(getResources().getColor(R.color.primary_blue));
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
            currentCategoryId = "";
            searchEditText.setText("");

            resetFilterStyles();
            tvPopular.setTextColor(getResources().getColor(R.color.primary_blue));
            fetchProducts(currentSortBy);
            dialog.dismiss();
        });

        dialog.show();
    }
}