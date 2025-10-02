package com.example.office_management.adapter;

import static com.example.office_management.fragment.cart.ShoppingCartFragment.updateSelectAllText;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.office_management.R;
import com.example.office_management.activity.review.WriteReviewActivity;
import com.example.office_management.api.ReviewApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ReviewResponse;
import com.example.office_management.retrofit2.BaseURL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ReviewApi reviewApi;
    private Context context;
    private List<ReviewResponse> reviews;
    private boolean isReply; // Nếu là true => ẩn rating, ảnh, replies
    private static final int VISIBLE_REPLY_COUNT = 1; // Số phản hồi hiển thị ban đầu

    public ReviewAdapter(Context context, List<ReviewResponse> reviews, boolean isReply) {
        this.context = context;
        this.reviews = reviews;
        this.isReply = isReply;
        reviewApi = BaseURL.getUrl(context).create(ReviewApi.class);
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResponse review = reviews.get(position);
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("userId", null);
        holder.nameText.setText(review.getUser().getFullName());

        String content = review.getContent();
        SpannableString spannableContent;
        if (review.getReplyOnUser() != null) {
            String fullName = review.getReplyOnUser().getFirstName() + " " + review.getReplyOnUser().getLastName();
            String prefix = "Reply @" + fullName + ": ";
            String fullText = prefix + content;

            spannableContent = new SpannableString(fullText);

            // Tô màu phần "Reply @Full Name"
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue);
            spannableContent.setSpan(
                    new ForegroundColorSpan(color),
                    0,
                    prefix.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            spannableContent = new SpannableString(content);
        }
        holder.contentText.setText(spannableContent);

        String date = "N/A";
        if (review.getCreatedAt() != null) {
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(review.getCreatedAt());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.dateText.setText(date);

        Glide.with(holder.itemView.getContext())
                .load(review.getUser().getAvatar())
                .circleCrop()
                .into(holder.avatarImage);

        if (!isReply) {
            // Hiển thị Rating
            if (review.getRating() != null) {
                holder.ratingBar.setRating(review.getRating());
                holder.ratingBar.setVisibility(View.VISIBLE);
            } else {
                holder.ratingBar.setVisibility(View.GONE);
            }

            // Hiển thị ảnh
            if (review.getReviewImage() != null && !review.getReviewImage().isEmpty()) {
                holder.reviewImagesRecycler.setVisibility(View.VISIBLE);
                holder.reviewImagesRecycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), RecyclerView.HORIZONTAL, false));
                holder.reviewImagesRecycler.setAdapter(new ReviewImageAdapter(review.getReviewImage(), imageUrl -> {
                    showImageDialog(holder.itemView.getContext(), imageUrl);
                }));

            } else {
                holder.reviewImagesRecycler.setVisibility(View.GONE);
            }

            // Hiển thị replies
            if (review.getReplies() != null && !review.getReplies().isEmpty()) {
                holder.repliesRecyclerView.setVisibility(View.VISIBLE);
                holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

                // Tạo danh sách phản hồi giới hạn ban đầu
                List<ReviewResponse> visibleReplies = new ArrayList<>();
                boolean isExpanded = holder.isRepliesExpanded;
                int replyCount = review.getReplies().size();
                if (isExpanded) {
                    visibleReplies.addAll(review.getReplies());
                } else {
                    visibleReplies.addAll(review.getReplies().subList(0, Math.min(replyCount, VISIBLE_REPLY_COUNT)));
                }

                // Thiết lập adapter cho replies
                ReviewAdapter repliesAdapter = new ReviewAdapter(context, visibleReplies, true);
                holder.repliesRecyclerView.setAdapter(repliesAdapter);

                // Hiển thị/ẩn nút "Xem thêm"
                if (replyCount > VISIBLE_REPLY_COUNT && !isExpanded) {
                    holder.viewMoreRepliesButton.setVisibility(View.VISIBLE);
                    holder.viewMoreRepliesButton.setText("Xem thêm");
                } else {
                    holder.viewMoreRepliesButton.setVisibility(View.GONE);
                }

                // Xử lý sự kiện nhấn nút "Xem thêm"
                holder.viewMoreRepliesButton.setOnClickListener(v -> {
                    holder.isRepliesExpanded = !holder.isRepliesExpanded;
                    List<ReviewResponse> updatedReplies = new ArrayList<>();
                    if (holder.isRepliesExpanded) {
                        updatedReplies.addAll(review.getReplies());
                        holder.viewMoreRepliesButton.setText("Thu gọn");
                    } else {
                        updatedReplies.addAll(review.getReplies().subList(0, Math.min(replyCount, VISIBLE_REPLY_COUNT)));
                        holder.viewMoreRepliesButton.setText("Xem thêm");
                    }
                    repliesAdapter.setReviews(updatedReplies);
                    holder.viewMoreRepliesButton.setVisibility(replyCount > VISIBLE_REPLY_COUNT ? View.VISIBLE : View.GONE);
                });
            } else {
                holder.repliesRecyclerView.setVisibility(View.GONE);
                holder.viewMoreRepliesButton.setVisibility(View.GONE);
            }

        } else {
            // Là reply thì ẩn rating, ảnh, replies
            holder.ratingBar.setVisibility(View.GONE);
            holder.reviewImagesRecycler.setVisibility(View.GONE);
            holder.repliesRecyclerView.setVisibility(View.GONE);
            holder.viewMoreRepliesButton.setVisibility(View.GONE);
        }

        holder.btnReply.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), WriteReviewActivity.class);
            intent.putExtra("action", "reply");
            intent.putExtra("parentReviewId", review.getReviewId()); // ID review sẽ được reply
            intent.putExtra("replyOnUserName", review.getUser().getUserId()); // hiển thị tên người được reply
            holder.itemView.getContext().startActivity(intent);
        });

        if (review.getUser().getUserId().equals(currentUserId)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), WriteReviewActivity.class);
            intent.putExtra("action", "edit");
            intent.putExtra("reviewId", review.getReviewId()); // ID bài review cần sửa
            intent.putExtra("content", review.getContent());
            intent.putExtra("rating", review.getRating());
            // Nếu có ảnh review
            if (review.getReviewImage() != null && !review.getReviewImage().isEmpty()) {
                intent.putStringArrayListExtra("reviewImages", new ArrayList<>(review.getReviewImage()));
            }

            holder.itemView.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this comment?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteReview(review.getReviewId(), position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    private void showImageDialog(Context context, String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);

        ImageView imageView = dialog.findViewById(R.id.fullImageView);
        Glide.with(context).load(imageUrl).into(imageView);

        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void deleteReview(String reviewId, int position){
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        reviewApi.deleteReview(authHeader, reviewId).enqueue(new Callback<ApiResponse<ApiResponse<Void>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiResponse<Void>>> call, Response<ApiResponse<ApiResponse<Void>>> response) {
                if (response.isSuccessful()) {
                    if (position >= 0 && position < reviews.size()) {
                        reviews.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Removed review", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Can't be removed: Invalid location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Delete failed review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiResponse<Void>>> call, Throwable t) {
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nameText, contentText, dateText;
        RatingBar ratingBar;
        RecyclerView reviewImagesRecycler, repliesRecyclerView;
        Button viewMoreRepliesButton;
        boolean isRepliesExpanded; // Trạng thái mở rộng phản hồi
        LinearLayout btnEdit, btnReply, btnDelete;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.img_avatar);
            nameText = itemView.findViewById(R.id.txt_user_name);
            contentText = itemView.findViewById(R.id.txt_review_content);
            dateText = itemView.findViewById(R.id.txt_created_at);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            reviewImagesRecycler = itemView.findViewById(R.id.review_images_recycler);
            repliesRecyclerView = itemView.findViewById(R.id.replies_recycler_view);
            viewMoreRepliesButton = itemView.findViewById(R.id.viewMoreRepliesButton);
            isRepliesExpanded = false; // Mặc định là thu gọn
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnReply = itemView.findViewById(R.id.btn_reply);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

    }
}