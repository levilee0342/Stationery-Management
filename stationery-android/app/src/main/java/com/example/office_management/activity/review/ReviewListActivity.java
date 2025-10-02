package com.example.office_management.activity.review;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.adapter.ReviewAdapter;
import com.example.office_management.api.ReviewApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ReviewResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewListActivity extends AppCompatActivity {
    private TextView tvRatingSummary, tvReviewsCount;
    private RatingBar ratingBar;
    private Button btnReview;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ReviewApi reviewApi;
    private String slug, productId;
    private int totalReviews;
    private float averageRating;
    private int[] ratingCounts;
    private ProgressBar[] progressBars = new ProgressBar[5];
    private TextView[] percentTexts = new TextView[5];
    private boolean hasReviewed = false;
    private ImageButton btnBack, btnCart, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_list);

        initView();

        recyclerView.setLayoutManager(new LinearLayoutManager(ReviewListActivity.this));
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>(), false);
        recyclerView.setAdapter(reviewAdapter);

        reviewApi = BaseURL.getUrl(this).create(ReviewApi.class);

        slug = getIntent().getStringExtra("slug");
        productId = getIntent().getStringExtra("productId");

        fetchReviews(slug);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewListActivity.this, MainActivity.class);
            intent.putExtra("openHome", true); // Gửi cờ mở Home
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewListActivity.this, MainActivity.class);
            intent.putExtra("openCart", true); // Gửi thông tin muốn mở giỏ hàng
            startActivity(intent);
        });

        btnReview.setOnClickListener(v -> {
            if (hasReviewed) {
                Toast.makeText(ReviewListActivity.this, "Bạn đã đánh giá sản phẩm này rồi", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ReviewListActivity.this, WriteReviewActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchReviews(slug);
    }

    private void initView(){
        recyclerView = findViewById(R.id.reviews_recycler_view);
        btnReview = findViewById(R.id.write_review_button);
        tvRatingSummary = findViewById(R.id.rating_summary);
        tvReviewsCount = findViewById(R.id.reviews_count);
        ratingBar = findViewById(R.id.ratingBarInput);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btnHome);
        btnCart = findViewById(R.id.btnCart);

        int[] progressIds = {
                R.id.progressBar5, R.id.progressBar4, R.id.progressBar3, R.id.progressBar2, R.id.progressBar1
        };
        int[] percentTextIds = {
                R.id.percentText5, R.id.percentText4, R.id.percentText3, R.id.percentText2, R.id.percentText1
        };

        for (int i = 0; i < 5; i++) {
            progressBars[i] = findViewById(progressIds[i]);
            percentTexts[i] = findViewById(percentTextIds[i]);
        }
    }

    private void updateRatingUI() {
        tvRatingSummary.setText(String.format("%.1f/5", averageRating));
        tvReviewsCount.setText("(" + totalReviews + " reviews)");
        ratingBar.setRating(averageRating);

        for (int i = 0; i < 5; i++) {
            int star = 5 - i;
            int count = ratingCounts[star - 1];
            int percent = totalReviews > 0 ? (int) ((count * 100f) / totalReviews) : 0;

            progressBars[i].setProgress(percent);
            percentTexts[i].setText(percent + "%");
        }
    }
    private void fetchReviews(String slug) {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        Call<ApiResponse<List<ReviewResponse>>> call = reviewApi.getReviewByProductId(slug);
        call.enqueue(new Callback<ApiResponse<List<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewResponse>>> call, Response<ApiResponse<List<ReviewResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewResponse> reviews = response.body().getResult();

                    ratingCounts = new int[5]; // index 0 -> 1★, index 4 -> 5★
                    float totalRating = 0;

                    for (ReviewResponse review : reviews) {
                        int rating = review.getRating(); // assume rating is from 1 to 5
                        if (rating >= 1 && rating <= 5) {
                            ratingCounts[rating - 1]++;
                            totalRating += rating;
                        }
                        // Kiểm tra đã đánh giá chưa
                        if (review.getUser().getUserId().equals(userId) && rating > 0) {
                            hasReviewed = true;
                        }
                    }

                    totalReviews = reviews.size();
                    averageRating = totalReviews > 0 ? totalRating / totalReviews : 0f;
                    // Cập nhật giao diện sau khi có dữ liệu
                    updateRatingUI();
                    // Gán vào adapter
                    reviewAdapter.setReviews(reviews);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ReviewResponse>>> call, Throwable t) {
                Toast.makeText(ReviewListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}