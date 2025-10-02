package com.example.office_management.fragment.user;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.office_management.R;
import com.example.office_management.activity.search.ProductSearchActivity;
import com.example.office_management.activity.search.SearchActivity;
import com.example.office_management.activity.shopping.ShoppingGuide;
import com.example.office_management.activity.user.NotificationActivity;
import com.example.office_management.adapter.BannerAdapter;
import com.example.office_management.adapter.MenuAdapter;
import com.example.office_management.adapter.ProductAdapter;
import com.example.office_management.adapter.ProductPagerAdapter;
import com.example.office_management.api.NotificationApi;
import com.example.office_management.api.ProductApi;
import com.example.office_management.databinding.FragmentHomeBinding;
import com.example.office_management.decoration.GridSpacingItemDecoration;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.model.MenuItem;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private CountDownTimer flashSaleTimer;
    private RecyclerView productRecyclerView;
    // Banner
    private final List<Integer> bannerList = Arrays.asList(
            R.drawable.img_banner_1,
            R.drawable.img_banner_3,
            R.drawable.img_banner_6,
            R.drawable.img_banner_4,
            R.drawable.img_banner_5,
            R.drawable.img_banner_3
    );
    private GridLayoutManager gridLayoutManager;
    // Product
    private ProductAdapter productAdapter;
    private final List<ProductResponse> productList = new ArrayList<>();

    // Flash Sale
    private ProductPagerAdapter flashSaleAdapter;
    private final List<ProductResponse> flashSaleList = new ArrayList<>();

    // Suggestion
    private ProductPagerAdapter suggestionAdapter;
    private final List<ProductResponse> suggestionList = new ArrayList<>();

    // APIs
    private ProductApi productApi;
    private NotificationApi notificationApi;



    //Danh mục
    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;

    // Snap helpers
    private final PagerSnapHelper flashSaleSnapHelper = new PagerSnapHelper();
    private final PagerSnapHelper suggestionSnapHelper = new PagerSnapHelper();
    //Sản phẩm Flash Sale
    // ================== BIẾN TRẠNG THÁI CHO PHÂN TRANG ==================
    private boolean isLoading = false; // <-- Cờ để biết có đang tải dữ liệu hay không
    private boolean isLastPage = false; // <-- Cờ để biết đã đến trang cuối cùng hay chưa
    private int currentPage = 0; // <-- Số trang hiện tại, API thường bắt đầu từ 0
    private final int PAGE_SIZE = 10; // <-- Số lượng sản phẩm tải mỗi lần (theo yêu cầu của

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize APIs
        productApi = BaseURL.getUrl(requireContext()).create(ProductApi.class);
        notificationApi = BaseURL.getUrl(requireContext()).create(NotificationApi.class);

        // Setup UI components
        setupBanner();
        setupMenu();

        setupProductRecyclerView();
        setupFlashSaleViewPager();
        setupSuggestionViewPager();
        setupSearchBar();
        setupScanButton();
        setupNavigationButtons();
        setupSvgImages();
        setupNotificationButton();
        showAdPopup();

        // Log visibility for debugging
        Log.d("HomeFragment", "flashSaleViewPager visibility: " + binding.flashSaleViewPager.getVisibility());
        Log.d("HomeFragment", "suggestionViewPager visibility: " + binding.suggestionViewPager.getVisibility());
        Log.d("HomeFragment", "productRecyclerView visibility: " + binding.productRecyclerView.getVisibility());

        // Fetch data
        fetchProducts();
        fetchFlashSaleProducts();
        fetchSuggestionProducts();
        loadUnreadNotificationCount();
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(bannerList);
        binding.bannerSlider.setAdapter(bannerAdapter);
        binding.bannerSlider.setPageTransformer((page, position) -> {
            page.setAlpha(0f);
            page.animate().alpha(1f).setDuration(500).start();
        });
        new TabLayoutMediator(binding.bannerIndicator, binding.bannerSlider, (tab, position) -> {
        }).attach();

        autoScrollRunnable = () -> {
            if (!isAdded() || binding == null) return;
            int currentItem = binding.bannerSlider.getCurrentItem();
            int nextItem = (currentItem + 1) % bannerList.size();
            binding.bannerSlider.setCurrentItem(nextItem, true);
            handler.postDelayed(autoScrollRunnable, 3000);
        };
        handler.postDelayed(autoScrollRunnable, 3000);
    }

    private void setupMenu() {
        binding.menuRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5));
        List<MenuItem> items = Arrays.asList(
                new MenuItem("Flash Sale", R.drawable.ic_flash_sale),
                new MenuItem("Voucher", R.drawable.ic_voucher),
                new MenuItem("New Product", R.drawable.ic_new),
                new MenuItem("Price Support", R.drawable.ic_discount_pd),
                new MenuItem("Wholesale", R.drawable.ic_wholesale)
        );

         menuAdapter = new MenuAdapter(items, item -> {
            String category;
            switch (item.getTitle()) {
                case "Voucher":
                    category = "voucher";
                    break;
                case "New Product":
                    category = "new_product";
                    break;
                case "Price Support":
                    category = "price_support";
                    break;
                case "Wholesale":
                    category = "wholesale";
                    break;
                case "Flash Sale":
                default:
                    category = "flash_sale";
                    break;
            }
            Intent intent = new Intent(requireActivity(), ShoppingGuide.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
        binding.menuRecyclerView.setAdapter(menuAdapter);
    }

    private void setupProductRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), productList);
        binding.productRecyclerView.setAdapter(productAdapter);
        binding.productRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacingInPixels = (int) (getResources().getDisplayMetrics().density * 8);
        binding.productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    private void setupFlashSaleViewPager() {
        flashSaleAdapter = new ProductPagerAdapter(flashSaleList, requireContext());
        binding.flashSaleViewPager.setAdapter(flashSaleAdapter);
        new TabLayoutMediator(binding.flashSaleIndicator, binding.flashSaleViewPager, (tab, position) -> {
        }).attach();

        binding.flashSaleViewPager.post(() -> {
            RecyclerView recyclerView = (RecyclerView) binding.flashSaleViewPager.getChildAt(0);
            if (recyclerView != null && recyclerView.getOnFlingListener() == null) {
                flashSaleSnapHelper.attachToRecyclerView(recyclerView);
            }
        });

        // Setup countdown timer
        TextView tvHours = binding.getRoot().findViewById(R.id.tvHours);
        TextView tvMinutes = binding.getRoot().findViewById(R.id.tvMinutes);
        TextView tvSeconds = binding.getRoot().findViewById(R.id.tvSeconds);

        if (tvHours != null && tvMinutes != null && tvSeconds != null) {
            flashSaleTimer = new CountDownTimer(3600000, 1000) { // 1 hour
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / 3600000;
                    long minutes = (millisUntilFinished % 3600000) / 60000;
                    long seconds = (millisUntilFinished % 60000) / 1000;
                    tvHours.setText(String.format("%02d", hours));
                    tvMinutes.setText(String.format("%02d", minutes));
                    tvSeconds.setText(String.format("%02d", seconds));
                }

                @Override
                public void onFinish() {
                    tvHours.setText("00");
                    tvMinutes.setText("00");
                    tvSeconds.setText("00");
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Flash Sale đã kết thúc!", Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        } else {
            Log.e("HomeFragment", "Timer TextViews are null");
        }
    }

    private void setupSuggestionViewPager() {
        suggestionAdapter = new ProductPagerAdapter(suggestionList, requireContext());
        binding.suggestionViewPager.setAdapter(suggestionAdapter);
        new TabLayoutMediator(binding.suggestionIndicator, binding.suggestionViewPager, (tab, position) -> {
        }).attach();

        binding.suggestionViewPager.post(() -> {
            RecyclerView recyclerView = (RecyclerView) binding.suggestionViewPager.getChildAt(0);
            if (recyclerView != null && recyclerView.getOnFlingListener() == null) {
                suggestionSnapHelper.attachToRecyclerView(recyclerView);
            }
        });
    }

    private void setupSearchBar() {
            View searchBar = binding.getRoot().findViewById(R.id.searchBar);
            if (searchBar != null) {
                searchBar.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), SearchActivity.class);
                    startActivity(intent);
                });
            }
        }


        private void setupNavigationButtons() {
            Button btnFlashSalePrev = binding.getRoot().findViewById(R.id.btnFlashSalePrev);
            Button btnFlashSaleNext = binding.getRoot().findViewById(R.id.btnFlashSaleNext);
            if (btnFlashSalePrev != null && btnFlashSaleNext != null) {
                btnFlashSalePrev.setOnClickListener(v -> navigateViewPager(binding.flashSaleViewPager, false));
                btnFlashSaleNext.setOnClickListener(v -> navigateViewPager(binding.flashSaleViewPager, true));
            }

            Button btnSuggestionPrev = binding.getRoot().findViewById(R.id.btnSuggestionPrev);
            Button btnSuggestionNext = binding.getRoot().findViewById(R.id.btnSuggestionNext);
            if (btnSuggestionPrev != null && btnSuggestionNext != null) {
                btnSuggestionPrev.setOnClickListener(v -> navigateViewPager(binding.suggestionViewPager, false));
                btnSuggestionNext.setOnClickListener(v -> navigateViewPager(binding.suggestionViewPager, true));
            }
        }

            private void setupSvgImages() {
                try {
                    SVG svg = SVG.getFromAsset(requireContext().getAssets(), "svg_images/banner_back_to_school.svg");
                    SVG svg1 = SVG.getFromAsset(requireContext().getAssets(), "svg_images/banner_suggestion.svg");
                    SVG svg2 = SVG.getFromAsset(requireContext().getAssets(), "svg_images/free_ship.svg");

                    PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                    PictureDrawable drawable1 = new PictureDrawable(svg1.renderToPicture());
                    PictureDrawable drawable2 = new PictureDrawable(svg2.renderToPicture());

                    binding.imgSvgBackToSchool.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    binding.imgSvgBackToSchool.setImageDrawable(drawable);

                    binding.imgSvgSuggestion.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    binding.imgSvgSuggestion.setImageDrawable(drawable1);

                    binding.imgSvgFreeShip.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    binding.imgSvgFreeShip.setImageDrawable(drawable2);
                } catch (SVGParseException | IOException e) {
                    Log.e("HomeFragment", "Error loading SVG images", e);
                }
            }

            private void setupNotificationButton() {
                FrameLayout notificationContainer = binding.getRoot().findViewById(R.id.notificationContainer);
                ImageView btnNotification = binding.getRoot().findViewById(R.id.btnNotification);
                TextView badgeNotification = binding.getRoot().findViewById(R.id.badgeNotification);
                if (btnNotification != null) {
                    btnNotification.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), NotificationActivity.class);
                        startActivity(intent);
                    });
                }
            }
            private void setupScanButton() {
                ImageView btnScan = binding.getRoot().findViewById(R.id.btnScan);
                if (btnScan != null) {
                    btnScan.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), ProductSearchActivity.class);
                        startActivity(intent);
                    });
                }
            }
    private void addScrollListener() {
        if (productRecyclerView == null || gridLayoutManager == null) {
            Log.e("HomeFragment", "Cannot add scroll listener: RecyclerView or LayoutManager is null");
            return;
        }

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (gridLayoutManager == null) {
                    Log.e("HomeFragment", "GridLayoutManager is null in onScrolled");
                    return;
                }

                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                Log.d("HomeFragment", "onScrolled: visibleItemCount=" + visibleItemCount
                        + ", totalItemCount=" + totalItemCount
                        + ", firstVisibleItemPosition=" + firstVisibleItemPosition
                        + ", isLoading=" + isLoading
                        + ", isLastPage=" + isLastPage
                        + ", currentPage=" + currentPage);

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        currentPage++;
                        fetchProducts();
                    }
                }
            }
        });
    }


    private void fetchProducts() {
        isLoading = true;
        Log.d("API", "Fetching products for page: " + currentPage);

        String sortBy = "createdAt";
        String minPrice = "";
        String maxPrice = "";
        String categoryId = "";
        String search = "";
        String totalRating = "";

        productApi = BaseURL.getUrl(requireContext()).create(ProductApi.class);
        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                sortBy, minPrice, maxPrice, categoryId, search, totalRating,
                currentPage, PAGE_SIZE
        );

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                Log.d("API", "API response received: " + response.code() + " for page: " + currentPage);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<ProductResponse> fetchedList = response.body().getResult().getContent();
                        Log.d("_____________", "onResponse: "+fetchedList.size() );
                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            if (currentPage == 0) {
                                productAdapter.setData(productList);
                            } else {
                                productAdapter.addData(fetchedList);
                            }

                            // Ensure RecyclerView is visible
                            binding.productRecyclerView.setVisibility(View.VISIBLE);

                            // Check if this is the last page
                            if (fetchedList.size() < PAGE_SIZE) {
                                isLastPage = true;
                                Log.d("API", "Last page reached.");
                            }
                        } else {
                            isLastPage = true;
                            Log.d("API", "Empty list received. This is the last page.");
                            if (currentPage > 0) {
                                Toast.makeText(requireContext(), "No more products", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("API", "Error parsing JSON: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Error parsing products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "API request failed: " + response.message());
                    Toast.makeText(requireContext(), "API request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                Log.e("API", "API request failed: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }
            private void navigateViewPager(ViewPager2 viewPager, boolean isNext) {
                if (viewPager == null || viewPager.getAdapter() == null) {
                    Log.e("HomeFragment", "ViewPager or Adapter is null");
                    return;
                }

                int itemCount = viewPager.getAdapter().getItemCount();
                if (itemCount <= 1) {
                    Button prevButton = viewPager.getId() == R.id.flashSaleViewPager ?
                            binding.getRoot().findViewById(R.id.btnFlashSalePrev) :
                            binding.getRoot().findViewById(R.id.btnSuggestionPrev);
                    Button nextButton = viewPager.getId() == R.id.flashSaleViewPager ?
                            binding.getRoot().findViewById(R.id.btnFlashSaleNext) :
                            binding.getRoot().findViewById(R.id.btnSuggestionNext);
                    if (prevButton != null && nextButton != null) {
                        prevButton.setEnabled(false);
                        nextButton.setEnabled(false);
                    }
                    return;
                }

                int currentItem = viewPager.getCurrentItem();
                int newItem = isNext ?
                        (currentItem + 1) % itemCount :
                        (currentItem - 1 + itemCount) % itemCount;
                viewPager.setCurrentItem(newItem, true);
            }
            private void showAdPopup() {
                Dialog dialog = new Dialog(requireActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.dialog_ad_popup);
                dialog.setCancelable(false);

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.dimAmount = 0.6f;
                    lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(lp);
                }

                ImageView imgAd = dialog.findViewById(R.id.imgAd);
                ImageView btnCloseAd = dialog.findViewById(R.id.btnCloseAd);

                String[] svgFiles = {
                        "svg_images/poster_quangcao_1.svg",
                        "svg_images/poster_quangcao_2.svg",
                        "svg_images/poster_quangcao_1.svg"
                };

                String selectedFile = svgFiles[new Random().nextInt(svgFiles.length)];
                try {
                    SVG svg = SVG.getFromAsset(requireContext().getAssets(), selectedFile);
                    PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                    if (imgAd != null) {
                        imgAd.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        imgAd.setImageDrawable(drawable);
                    }
                } catch (SVGParseException | IOException e) {
                    Log.e("HomeFragment", "Error loading ad SVG", e);
                }

                if (btnCloseAd != null) {
                    btnCloseAd.setOnClickListener(v -> dialog.dismiss());
                }

                dialog.show();
            }


    private void fetchFlashSaleProducts() {
        productApi = BaseURL.getUrl(requireContext()).create(ProductApi.class);
        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts("totalRating", "", "", "", "", "",1,10);
        Log.d("HomeFragment", "Sending API request for suggestion products");

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ResultResponse>> call, @NonNull Response<ApiResponse<ResultResponse>> response) {
                Log.d("HomeFragment", "Suggestion API response received: " + response.code());
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductResponse> fetchedList = response.body().getResult().getContent();
                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            List<ProductResponse> limitedList = fetchedList.subList(0, Math.min(fetchedList.size(), 10));
                            flashSaleList.clear();
                            flashSaleList.addAll(limitedList);
                            flashSaleAdapter.setData(limitedList); // Use setData for consistency
                            binding.flashSaleViewPager.setVisibility(View.VISIBLE);
                            binding.flashSaleViewPager.setVisibility(View.VISIBLE);
                            Log.d("HomeFragment", "Successfully loaded " + limitedList.size() + " suggested products");

                            // Update navigation buttons
                            Button btnSuggestionPrev = binding.getRoot().findViewById(R.id.btnFlashSalePrev);
                            Button btnSuggestionNext = binding.getRoot().findViewById(R.id.btnFlashSaleNext);
                            if (btnSuggestionPrev != null && btnSuggestionNext != null) {
                                boolean enabled = limitedList.size() > 1;
                                btnSuggestionPrev.setEnabled(enabled);
                                btnSuggestionNext.setEnabled(enabled);
                            }
                        } else {
                            Log.d("HomeFragment", "No suggested products found");
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No Suggested Products", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e("HomeFragment", "Suggestion API failed: " + response.message());
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Suggestion API failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing suggestion response: " + e.getMessage(), e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error parsing suggestions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ResultResponse>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Suggestion network error: " + t.getMessage(), t);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchSuggestionProducts() {
        productApi = BaseURL.getUrl(requireContext()).create(ProductApi.class);
        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts("totalRating", "", "", "", "", "",1,10);
        Log.d("HomeFragment", "Sending API request for suggestion products");

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ResultResponse>> call, @NonNull Response<ApiResponse<ResultResponse>> response) {
                Log.d("HomeFragment", "Suggestion API response received: " + response.code());
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductResponse> fetchedList = response.body().getResult().getContent();
                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            List<ProductResponse> limitedList = fetchedList.subList(0, Math.min(fetchedList.size(), 10));
                            suggestionList.clear();
                            suggestionList.addAll(limitedList);
                            suggestionAdapter.setData(limitedList); // Use setData for consistency
                            binding.suggestionViewPager.setVisibility(View.VISIBLE);
                            binding.suggestionIndicator.setVisibility(View.VISIBLE);
                            Log.d("HomeFragment", "Successfully loaded " + limitedList.size() + " suggested products");

                            // Update navigation buttons
                            Button btnSuggestionPrev = binding.getRoot().findViewById(R.id.btnSuggestionPrev);
                            Button btnSuggestionNext = binding.getRoot().findViewById(R.id.btnSuggestionNext);
                            if (btnSuggestionPrev != null && btnSuggestionNext != null) {
                                boolean enabled = limitedList.size() > 1;
                                btnSuggestionPrev.setEnabled(enabled);
                                btnSuggestionNext.setEnabled(enabled);
                            }
                        } else {
                            Log.d("HomeFragment", "No suggested products found");
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No Suggested Products", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e("HomeFragment", "Suggestion API failed: " + response.message());
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Suggestion API failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing suggestion response: " + e.getMessage(), e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error parsing suggestions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ResultResponse>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Suggestion network error: " + t.getMessage(), t);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadUnreadNotificationCount() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", requireContext().MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            Log.e("HomeFragment", "No token found for notifications");
            return;
        }
        String authHeader = "Bearer " + token;

        notificationApi.countUnreadNotifications(authHeader).enqueue(new Callback<ApiResponse<Long>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Long>> call, @NonNull Response<ApiResponse<Long>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    long unreadCount = response.body().getResult();
                    TextView badgeNotification = binding.getRoot().findViewById(R.id.badgeNotification);
                    if (badgeNotification != null) {
                        if (unreadCount > 0) {
                            badgeNotification.setVisibility(View.VISIBLE);
                            badgeNotification.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
                        } else {
                            badgeNotification.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Long>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Log.e("HomeFragment", "Notification count API error: " + t.getMessage(), t);
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScrollRunnable);
        if (flashSaleTimer != null) {
            flashSaleTimer.cancel();
        }
        binding = null;
    }

}