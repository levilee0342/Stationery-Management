package com.example.office_management.activity.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.product.ProductActivity;
import com.example.office_management.adapter.CategoryAdapter;
import com.example.office_management.adapter.PopularSearchAdapter;
import com.example.office_management.adapter.RecentSearchAdapter;
import com.example.office_management.api.CategoryApi;
import com.example.office_management.api.SearchApi;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.UserResponse;
import com.example.office_management.dto.response.category.CategoryProductResponse;
import com.example.office_management.dto.response.search.SearchHistoryResponse;
import com.example.office_management.model.Category;
import com.example.office_management.retrofit2.BaseURL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView recentSearchRecyclerView, popularSearchRecyclerView, categoryRecyclerView;
    private LinearLayout searchBarLayout, recentSearchContainer;
    private ProgressBar progressBar;
    private CategoryApi categoryApi;
    private SearchApi searchApi;
    private UserApi userApi;
    private List<Category> categoryList = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        recentSearchRecyclerView = findViewById(R.id.rvRecentKeyword);
        popularSearchRecyclerView = findViewById(R.id.rvPopularSearch);
        categoryRecyclerView = findViewById(R.id.rvCategory);
        searchBarLayout = findViewById(R.id.searchBarLayout);
        recentSearchContainer = findViewById(R.id.recentSearchContainer);
        progressBar = findViewById(R.id.progressBar);
        searchEditText = findViewById(R.id.searchEditText);
        ImageButton btnBack = findViewById(R.id.btn_back);


        // Initialize API services
        categoryApi = BaseURL.getUrl(this).create(CategoryApi.class);
        searchApi = BaseURL.getUrl(this).create(SearchApi.class);
        userApi = BaseURL.getUrl(this).create(UserApi.class);

        btnBack.setOnClickListener(v -> { onBackPressed(); });

        ImageView btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, ProductSearchActivity.class);
            startActivity(intent);
        });

        // Set up search bar click listener
        searchBarLayout.setOnClickListener(v -> {
            Toast.makeText(SearchActivity.this, "Search bar clicked", Toast.LENGTH_SHORT).show();
        });

        // Set up end icon click listener for search
        ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.searchBarLayout))
                .setEndIconOnClickListener(v -> {
                    String query = searchEditText.getText().toString().trim();
                    if (!query.isEmpty()) {
                        // Navigate to ProductActivity with the search query
                        Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                        intent.putExtra("keyword", query);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                    }
                });

        // Load data
        loadRecentSearches();
        loadPopularSearches();
        loadCategories();
    }

    private void loadRecentSearches() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            recentSearchContainer.setVisibility(View.GONE);
            recentSearchRecyclerView.setVisibility(View.GONE);
            Log.d(TAG, "No token, hiding recent searches");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<UserResponse>> call = userApi.getUserInfo("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    UserResponse user = response.body().getResult();

                    // Sort by createdAt in descending order (newest first)
                    List<String> recentKeywords = user.getSearchHistory().stream()
                            .sorted((a, b) -> {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                    Date dateA = sdf.parse(a.getCreatedAt());
                                    Date dateB = sdf.parse(b.getCreatedAt());
                                    return dateB.compareTo(dateA); // Newest first
                                } catch (Exception e) {
                                    return 0;
                                }
                            })
                            .map(SearchHistoryResponse::getKeyword)
                            .distinct() // Avoid duplicate keywords
                            .limit(6)
                            .collect(Collectors.toList());

                    Log.d(TAG, "Recent searches loaded: " + recentKeywords.size());
                    RecentSearchAdapter adapter = new RecentSearchAdapter(recentKeywords, keyword -> {
                        // Navigate to ProductActivity with the selected keyword
                        Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                        intent.putExtra("keyword", keyword);
                        startActivity(intent);
                    });
                    recentSearchRecyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
                    recentSearchRecyclerView.setAdapter(adapter);
                    recentSearchRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Failed to load recent searches: " + response.code() + " - " + response.message());
                    if (response.code() != 401) {
                        Toast.makeText(SearchActivity.this, "Failed to load recent searches", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching recent searches: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularSearches() {
        progressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<List<String>>> call = searchApi.apiTopKeyWords();
        call.enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<String> popularKeywords = response.body().getResult().stream()
                            .limit(6)
                            .collect(Collectors.toList());

                    Log.d(TAG, "Popular searches loaded: " + popularKeywords.size());
                    PopularSearchAdapter adapter = new PopularSearchAdapter(popularKeywords, keyword -> {
                        // Navigate to ProductActivity with the selected keyword
                        Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                        intent.putExtra("keyword", keyword);
                        startActivity(intent);
                    });
                    popularSearchRecyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
                    popularSearchRecyclerView.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Failed to load popular searches: " + response.code() + " - " + response.message());
                    Toast.makeText(SearchActivity.this, "Failed to load popular searches", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching popular searches: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        Call<ApiResponse<List<CategoryProductResponse>>> call;

        progressBar.setVisibility(View.VISIBLE);
        if (token != null) {
            call = categoryApi.apiTopUserCategories("Bearer " + token);
        } else {
            call = categoryApi.apiAllCategory();
        }

        call.enqueue(new Callback<ApiResponse<List<CategoryProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryProductResponse>>> call, Response<ApiResponse<List<CategoryProductResponse>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<CategoryProductResponse> apiCategories = response.body().getResult().stream()
                            .limit(6)
                            .collect(Collectors.toList());

                    categoryList.clear();
                    for (CategoryProductResponse apiCategory : apiCategories) {
                        Log.d(TAG, "Category: " + apiCategory.getCategoryName() +
                                ", Icon: " + apiCategory.getIcon() +
                                ", BgColor: " + apiCategory.getBgColor());
                        categoryList.add(new Category(
                                apiCategory.getCategoryId(),
                                apiCategory.getCategoryName(),
                                apiCategory.getIcon(),
                                apiCategory.getBgColor()
                        ));
                    }

                    CategoryAdapter adapter = new CategoryAdapter(SearchActivity.this, categoryList, category -> {
                        // Navigate to ProductActivity with categoryId
                        Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                        intent.putExtra("categoryId", category.getCategoryId());
                        intent.putExtra("categoryName", category.getCategoryName()); // Optional: for display purposes
                        startActivity(intent);
                    }, true);
                    categoryRecyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 4));
                    categoryRecyclerView.setAdapter(adapter);
                    Log.d(TAG, "Categories loaded: " + categoryList.size());
                } else {
                    Log.e(TAG, "Failed to load categories: " + response.code() + " - " + response.message());
                    if (response.code() != 401) {
                        Toast.makeText(SearchActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryProductResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching categories: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}