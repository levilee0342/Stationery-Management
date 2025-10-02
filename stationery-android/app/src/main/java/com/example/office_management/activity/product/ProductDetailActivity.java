package com.example.office_management.activity.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.activity.address.ShippingActivity;
import com.example.office_management.activity.review.ReviewListActivity;
import com.example.office_management.activity.review.WriteReviewActivity;
import com.example.office_management.api.CartApi;
import com.example.office_management.api.ProductApi;
import com.example.office_management.api.ReviewApi;
import com.example.office_management.decoration.GridSpacingItemDecoration;
import com.example.office_management.dto.request.cart.AddCartRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.dto.response.ImageResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.ReviewResponse;
import com.example.office_management.dto.response.colorSize.ColorResponse;
import com.example.office_management.dto.response.colorSize.ColorSizeSlugResponse;
import com.example.office_management.dto.response.colorSize.SizeResponse;
import com.example.office_management.dto.response.colorSize.SizeSlugResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.adapter.*;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView productName, discountPrice, originalPrice, description, discountPercentage, rating, sold, imageCarouselIndicator, tvQuanlity, tvScoreCount;
    private RecyclerView sizeRecyclerView, colorRecyclerView, productRecyclerView;
    private LinearLayout sizeSection, colorSection, reviewEmptyGroup;
    private Button addToCartButton, buyNowButton, writeReview;
    private ImageButton btnBack, btnHome, btnCart, btnSearch, btnDecrement, btnIncrement;
    private androidx.appcompat.widget.SearchView searchView;
    private ConstraintLayout reviewSummaryGroup;
    private RatingBar ratingBarSummary;
    private ImagePagerAdapter imageAdapter;
    private ProductAdapter productAdapter;
    private List<ProductResponse> productList = new ArrayList<>();
    private ProductApi productApi;
    private CartApi cartApi;
    private ReviewApi reviewApi;
    private List<ImageResponse> images;
    private List<ReviewResponse> reviews = new ArrayList<>();
    private List<ColorSizeSlugResponse> colorList = new ArrayList<>();
    private SizeSlugResponse selectedSize;
    private ColorSizeSlugResponse selectedColor;
    private ProductResponse cachedProductResponse;
    private ColorResponse colorResponse;
    private SizeResponse sizeResponse;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    private int quantity = 1, original, discount, numberOfReviews = 0;
    private String productDetailId, slug, productId, name, image, color, size;
    public static final DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initViews();

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            intent.putExtra("openHome", true); // Gửi cờ mở Home
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            intent.putExtra("openCart", true); // Gửi thông tin muốn mở giỏ hàng
            startActivity(intent);
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    // Hiện thanh tìm kiếm
                    searchView.setVisibility(View.VISIBLE);
                    searchView.requestFocus();
                } else {
                    // Đóng và reset về ban đầu
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setVisibility(View.GONE);
                }
            }
        });

        btnIncrement.setOnClickListener(v -> {
            quantity++;
            updateQuantityText();
        });

        btnDecrement.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityText();
            }
        });

        slug = getIntent().getStringExtra("slug");
        Log.d("SLUG", "Truyền slug: " + slug);

        if (slug == null) {
            Toast.makeText(this, "No product slug", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        addToCartButton.setOnClickListener(v -> {
            if (productDetailId == null || productDetailId.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy mã sản phẩm chi tiết", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Add to cart", "Product Detail ID: " + productDetailId + ", Quantity: " + quantity);
            addItemToCart(productDetailId, quantity);
        });

        buyNowButton.setOnClickListener(v -> {
            List<CartResponse> selectedItems = new ArrayList<>();
            CartResponse item = new CartResponse();
            item.setProductDetailId(productDetailId);
            item.setProductId(productId);
            item.setProductName(name);
            item.setQuantity(quantity);
            item.setColorName(color);
            item.setSizeName(size);
            item.setSlug(slug);
            item.setDiscountPrice(discount);
            item.setOriginalPrice(original);
            item.setImageUrl(image);

            selectedItems.add(item);

            int totalPrice = discount * quantity; // tính giá sau giảm

            Intent intent = new Intent(ProductDetailActivity.this, ShippingActivity.class);
            intent.putExtra("selectedItems", (Serializable) selectedItems);
            intent.putExtra("totalPrice", totalPrice);
            startActivity(intent);
        });

        reviewSummaryGroup.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewListActivity.class);
            intent.putExtra("slug", slug);
            intent.putExtra("productId", productId);
            startActivity(intent);
        });

        writeReview.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailActivity.this, WriteReviewActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
        });

        productApi = BaseURL.getUrl(this).create(ProductApi.class);
        cartApi = BaseURL.getUrl(this).create(CartApi.class);
        reviewApi = BaseURL.getUrl(this).create(ReviewApi.class);

        // Gọi API lấy chi tiết sản phẩm và màu sắc/kích thước
        fetchProductDetails(slug);
        fetchColorSizeData(slug);

        // Khởi tạo RecyclerView
        productAdapter = new ProductAdapter(ProductDetailActivity.this, productList);
        Log.d("API", "Số lượng sản phẩm: " + productList.size());
        productRecyclerView.setAdapter(productAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = (int) (getResources().getDisplayMetrics().density * 8);
        productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        fetchProducts("-totalRating", "", "", "", "", "");


    }
    @Override
    public void onResume() {
        super.onResume();
        fetchProductDetails(slug);
        fetchColorSizeData(slug);
        fetchProducts("-totalRating", "", "", "", "", "");
    }

    private void updateQuantityText() {
        tvQuanlity.setText(String.valueOf(quantity));
    }
    private void initViews() {
        viewPager = findViewById(R.id.imageViewPager);
        imageCarouselIndicator = findViewById(R.id.imageCarouselIndicator);
        productName = findViewById(R.id.productName);
        discountPrice = findViewById(R.id.productPrice);
        originalPrice = findViewById(R.id.originalPrice);
        description = findViewById(R.id.productDescription);
        discountPercentage = findViewById(R.id.discountPercentage);
        rating = findViewById(R.id.productRating);
        sold = findViewById(R.id.tvSold);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyNowButton = findViewById(R.id.buyNowButton);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btnHome);
        btnCart = findViewById(R.id.btnCart);
        sizeSection = findViewById(R.id.sizeSection);
        colorSection = findViewById(R.id.colorSection);
        sizeRecyclerView = findViewById(R.id.sizeRecyclerView);
        colorRecyclerView = findViewById(R.id.colorRecyclerView);
        searchView = findViewById(R.id.searchView);
        btnSearch = findViewById(R.id.btnSearch);
        tvQuanlity = findViewById(R.id.quantity);
        btnIncrement = findViewById(R.id.incrementButton);
        btnDecrement = findViewById(R.id.decrementButton);
        reviewSummaryGroup = findViewById(R.id.reviewSummaryGroup);
        reviewEmptyGroup = findViewById(R.id.reviewEmptyGroup);
        ratingBarSummary = findViewById(R.id.ratingBarSummary);
        tvScoreCount = findViewById(R.id.tvScoreCount);
        productRecyclerView = findViewById(R.id.product_RecyclerView);
        writeReview = findViewById(R.id.writeReviewButton);
    }

    private void fetchProductDetails(String slug) {
        if (cachedProductResponse != null && slug.equals(this.slug)) {
            fetchReviews(cachedProductResponse.getProductDetail().getSlug(), () -> displayProductDetails(cachedProductResponse));
            return;
        }

        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(slug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    cachedProductResponse = response.body().getResult();
                    fetchReviews(cachedProductResponse.getProductDetail().getSlug(), () -> displayProductDetails(cachedProductResponse));
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Unable to load the product", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayProductDetails(ProductResponse productResponse) {
        runOnUiThread(() -> {
            productName.setText(productResponse.getName());
            // Lấy nội dung HTML từ response
            String htmlDescription = productResponse.getDescription();
            // Kiểm tra để đảm bảo nội dung không phải là null hoặc rỗng
            if (htmlDescription != null && !htmlDescription.isEmpty()) {
                // Sử dụng lớp Html để chuyển đổi chuỗi HTML thành văn bản có định dạng
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    description.setText(Html.fromHtml(htmlDescription, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    description.setText(Html.fromHtml(htmlDescription));
                }
            } else {
                // Nếu không có mô tả, hãy đặt thành chuỗi rỗng hoặc ẩn TextView đi
                description.setText("");
            }
            //rating.setText("★ " + productResponse.getTotalRating());
            sold.setText("Sold: " + productResponse.getSoldQuantity());
            rating.setText(FilterProductAdapter.TextUtils.getStarRatingText(productResponse.getTotalRating()));

            // ==== Xử lý review hiển thị ====
            double totalRating = productResponse.getTotalRating();

            int reviewCount = numberOfReviews;
            Log.d("ReviewCount", "Number of main reviews: " + reviewCount);

            if (reviewCount > 0) {
                reviewSummaryGroup.setVisibility(View.VISIBLE);
                reviewEmptyGroup.setVisibility(View.GONE);

                ratingBarSummary.setRating((float) productResponse.getTotalRating());
                tvScoreCount.setText(String.format("%.1f/5 (%d reviews)", totalRating, reviewCount));
            } else {
                reviewSummaryGroup.setVisibility(View.GONE);
                reviewEmptyGroup.setVisibility(View.VISIBLE);
            }

            ProductDetailResponse detail = productResponse.getProductDetail();
            Log.d("ProductDetailResponse", "Detail received: " + new Gson().toJson(detail));
            productDetailId = detail.getProductDetailId();
            productId = productResponse.getProductId();
            name = productResponse.getName();
            colorResponse = detail.getColor();
            color = (colorResponse != null) ? colorResponse.getName() : null;
            sizeResponse = detail.getSize();
            size = (sizeResponse != null) ? sizeResponse.getName() : null;
            discount = detail.getDiscountPrice();
            original = detail.getOriginalPrice();
            images = detail.getImages();
            image = detail.getThumbnail();
            updateImagesByColor(detail);

            discountPrice.setText(String.format("%sđ", formatter.format(
                    detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice()
            )));
            String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
            SpannableString spannableString = new SpannableString(originalPriceText);
            spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            originalPrice.setText(spannableString);

            int discountPercent = 0;
            if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
                discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
            }
            discountPercentage.setText("-" + discountPercent + "%");
        });
    }

    private void fetchColorSizeData(String slug) {
        Call<ApiResponse<List<ColorSizeSlugResponse>>> call = productApi.getColorSizeSlug(slug);
        call.enqueue(new Callback<ApiResponse<List<ColorSizeSlugResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ColorSizeSlugResponse>>> call, Response<ApiResponse<List<ColorSizeSlugResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<ColorSizeSlugResponse> colorSizeList = response.body().getResult();
                    Log.d("FetchColorSize", "Data received: " + colorSizeList.toString());
                    displayColorSizeData(colorSizeList);
                } else {
                    Log.e("FetchColorSize", "Failed to load data: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải màu sắc và kích thước", Toast.LENGTH_SHORT).show();
                    colorSection.setVisibility(View.GONE);
                    sizeSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ColorSizeSlugResponse>>> call, Throwable t) {
                Log.e("FetchColorSize", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                colorSection.setVisibility(View.GONE);
                sizeSection.setVisibility(View.GONE);
            }
        });
    }

    private void displayColorSizeData(final List<ColorSizeSlugResponse> colorSizeList) {
        if (colorSizeList == null || colorSizeList.isEmpty()) {
            Log.w("ColorSizeData", "Danh sách màu sắc và kích thước rỗng hoặc null");
            colorSection.setVisibility(View.GONE);
            sizeSection.setVisibility(View.GONE);
            // Trường hợp 4: Không có màu sắc và không có kích thước
            fetchProductDetails(slug);
            return;
        }

        colorList = new ArrayList<>();
        List<SizeSlugResponse> allSizes = new ArrayList<>();

        for (ColorSizeSlugResponse color : colorSizeList) {
            if (color != null && color.getHex() != null && !color.getHex().isEmpty()) {
                colorList.add(color);
            }
            if (color != null && color.getSizes() != null) {
                allSizes.addAll(color.getSizes());
            }
        }

        if (!colorList.isEmpty()) {
            // Trường hợp 1 và 2: Có màu sắc
            colorSection.setVisibility(View.VISIBLE);
            colorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ColorAdapter colorAdapter = new ColorAdapter(colorList);
            colorAdapter.setOnColorClickListener(color -> {
                selectedColor = color;
                selectedSize = null; // Reset kích thước khi chọn màu sắc mới
                // Kiểm tra xem có kích thước thực sự hay không
                boolean hasValidSize = color.getSizes() != null && color.getSizes().stream()
                        .anyMatch(size -> size.getSize() != null && !size.getSize().equals("null"));
                if (hasValidSize) {
                    // Trường hợp 1: Có màu sắc và có kích thước
                    updateSizeRecyclerView(color.getSizes());
                } else {
                    // Trường hợp 2: Có màu sắc nhưng không có kích thước
                    sizeSection.setVisibility(View.GONE);
                    // Lấy slug từ sizes của màu (nếu có)
                    String colorSlug = color.getSizes() != null && !color.getSizes().isEmpty()
                            ? color.getSizes().get(0).getSlug() : slug;
                    fetchProductDetailsByColor(colorSlug);
                }
            });
            colorRecyclerView.setAdapter(colorAdapter);

            // Chọn màu đầu tiên mặc định
            selectedColor = colorList.get(0);
            // Kiểm tra xem có kích thước thực sự hay không
            boolean hasValidSize = selectedColor.getSizes() != null && selectedColor.getSizes().stream()
                    .anyMatch(size -> size.getSize() != null && !size.getSize().equals("null"));
            if (hasValidSize) {
                // Trường hợp 1: Có màu sắc và có kích thước
                updateSizeRecyclerView(selectedColor.getSizes());
            } else {
                // Trường hợp 2: Có màu sắc nhưng không có kích thước
                sizeSection.setVisibility(View.GONE);
                // Lấy slug từ sizes của màu (nếu có)
                String colorSlug = selectedColor.getSizes() != null && !selectedColor.getSizes().isEmpty()
                        ? selectedColor.getSizes().get(0).getSlug() : slug;
                fetchProductDetailsByColor(colorSlug);
            }
        } else if (!allSizes.isEmpty()) {
            // Trường hợp 3: Không có màu sắc nhưng có kích thước
            colorSection.setVisibility(View.GONE);
            selectedColor = null;
            updateSizeRecyclerView(allSizes);
        } else {
            // Trường hợp 4: Không có màu sắc và không có kích thước
            colorSection.setVisibility(View.GONE);
            sizeSection.setVisibility(View.GONE);
            fetchProductDetails(slug);
        }
    }

    private void fetchProductDetailsByColor(String colorSlug) {
        if (colorSlug == null || colorSlug.isEmpty()) {
            Log.e("FetchProductDetailsByColor", "Color slug is null or empty");
            Toast.makeText(this, "Không thể tải ảnh sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("FetchProductDetailsByColor", "Fetching details for color slug: " + colorSlug);
        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(colorSlug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    ProductResponse productResponse = response.body().getResult();
                    Log.d("ProductDetailResponse", "Received: " + new Gson().toJson(productResponse));
                    // Cập nhật ảnh và các chi tiết khác
                    fetchReviews(productResponse.getProductDetail().getSlug(), () -> displayProductDetails(productResponse));
                } else {
                    Log.e("ProductDetailResponse", "Failed: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Log.e("ProductDetailResponse", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSizeRecyclerView(List<SizeSlugResponse> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            hideSizeSection();
            return;
        }

        // Lọc kích thước hợp lệ
        List<SizeSlugResponse> validSizes = new ArrayList<>();
        for (SizeSlugResponse s : sizes) {
            if (s != null && s.getSize() != null && !s.getSize().isEmpty() && s.getSlug() != null && !s.getSlug().isEmpty()) {
                validSizes.add(s);
            }
        }

        if (validSizes.isEmpty()) {
            hideSizeSection();
            return;
        }

        // Nếu có màu sắc nhưng chưa chọn, ẩn sizeSection
        if (!colorList.isEmpty() && selectedColor == null) {
            hideSizeSection();
            Toast.makeText(this, "Vui lòng chọn màu sắc trước", Toast.LENGTH_SHORT).show();
            return;
        }

        sizeSection.setVisibility(View.VISIBLE);

        // Tái sử dụng adapter nếu tồn tại
        SizeAdapter sizeAdapter = (SizeAdapter) sizeRecyclerView.getAdapter();
        if (sizeAdapter == null) {
            sizeAdapter = new SizeAdapter(validSizes);
            sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            sizeRecyclerView.setAdapter(sizeAdapter);
        } else {
            sizeAdapter.updateSizes(validSizes);
        }

        // Vô hiệu hóa sizeRecyclerView nếu màu sắc chưa được chọn
        sizeRecyclerView.setEnabled(colorList.isEmpty() || selectedColor != null);

        sizeAdapter.setOnSizeClickListener(size -> {
            if (!colorList.isEmpty() && selectedColor == null) {
                Toast.makeText(this, "Vui lòng chọn màu sắc trước", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSize = size;
            fetchProductDetailsWithSlug(size.getSlug());
        });

        // Chọn kích thước đầu tiên mặc định và gọi API
        selectedSize = validSizes.get(0);
        if (colorList.isEmpty() || selectedColor != null) {
            fetchProductDetailsWithSlug(selectedSize.getSlug());
        }
    }

    private void fetchProductDetailsWithSlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            Log.e("FetchProductDetails", "Slug is null or empty");
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("FetchProductDetails", "Fetching details for slug: " + slug);
        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(slug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    ProductResponse productResponse = response.body().getResult();
                    Log.d("ProductDetailResponse", "Received: " + new Gson().toJson(productResponse));
                    fetchReviews(productResponse.getProductDetail().getSlug(), () -> displayProductDetails(productResponse));
                } else {
                    Log.e("ProductDetailResponse", "Failed: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Log.e("ProductDetailResponse", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideSizeSection() {
        sizeSection.setVisibility(View.GONE);
    }

    private void updateImagesByColor(ProductDetailResponse detail) {
        List<String> imageUrls = new ArrayList<>();
        if (detail != null && detail.getImages() != null && !detail.getImages().isEmpty()) {
            for (ImageResponse img : detail.getImages()) {
                imageUrls.add(img.getUrl());
            }
        } else if (images != null && !images.isEmpty()) {
            for (ImageResponse img : images) {
                imageUrls.add(img.getUrl());
            }
        }

        // Cập nhật adapter hiện tại nếu có
        if (imageAdapter == null) {
            imageAdapter = new ImagePagerAdapter(imageUrls, ProductDetailActivity.this);
            viewPager.setAdapter(imageAdapter);
        } else {
            imageAdapter.updateImages(imageUrls);
        }

        if (imageUrls.size() > 1) {
            imageCarouselIndicator.setText("1/" + imageUrls.size());
            if (pageChangeCallback == null) {
                pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        imageCarouselIndicator.setText((position + 1) + "/" + imageUrls.size());
                    }
                };
                viewPager.registerOnPageChangeCallback(pageChangeCallback);
            }
        } else {
            imageCarouselIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pageChangeCallback != null) {
            viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        images = null; // Xóa để tránh rò rỉ bộ nhớ
    }

    private void addItemToCart(String productDetailId, int quantity) {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        AddCartRequest request = new AddCartRequest(productDetailId, quantity);
        cartApi.addItemCart(authHeader, request)
                .enqueue(new Callback<ApiResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartResponse cart = response.body().getResult();
                            Log.d("Cart Response", "Cart: " + new Gson().toJson(cart));
                            Toast.makeText(ProductDetailActivity.this, "Added to cart: " + cart.getProductName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Add failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchReviews(String slug, Runnable onReviewsFetched) {
        Log.d("DEBUG", "Inside fetchReviews with slug: " + slug);
        Call<ApiResponse<List<ReviewResponse>>> call = reviewApi.getReviewByProductId(slug);
        call.enqueue(new Callback<ApiResponse<List<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewResponse>>> call, Response<ApiResponse<List<ReviewResponse>>> response) {
                Log.d("DEBUG", "fetchReviews onResponse()");
                if (response.isSuccessful() && response.body() != null) {
                    numberOfReviews = response.body().getResult().size();
                    Log.d("ReviewList", "Số lượng đánh giá: " + numberOfReviews);
                } else {
                    Log.e("DEBUG", "Phản hồi không thành công hoặc body null");
                }
                if (onReviewsFetched != null) onReviewsFetched.run();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewResponse>>> call, Throwable t) {
                Log.e("DEBUG", "fetchReviews onFailure: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                if (onReviewsFetched != null) onReviewsFetched.run();
            }
        });
    }

    private void fetchProducts(String sortBy, String minPrice, String maxPrice, String categoryId, String search, String totalRating) {
        productApi = BaseURL.getUrl(ProductDetailActivity.this).create(ProductApi.class);
        Log.d("API", "Base URL: " + BaseURL.getUrl(ProductDetailActivity.this).baseUrl().toString());

        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                sortBy, minPrice, maxPrice, categoryId, search, totalRating,0,10
        );

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                Log.d("API", "API response received: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<ResultResponse> apiResponse = response.body();
                        List<ProductResponse> fetchedList = apiResponse.getResult().getContent();

                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            productAdapter.setData(fetchedList);
                            Log.d("API", "Số lượng sản phẩm nhận được: " + fetchedList.size());
                        } else {
                            Log.d("API", "Danh sách sản phẩm rỗng hoặc null");
                            Toast.makeText(ProductDetailActivity.this, "Empty Product List", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("API", "Lỗi phân tích JSON: " + e.getMessage(), e);
                        Toast.makeText(ProductDetailActivity.this, "Error JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "API request failed: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "API request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                Log.e("API", "API request failed: " + t.getMessage(), t);
                Toast.makeText(ProductDetailActivity.this, "Error network" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}