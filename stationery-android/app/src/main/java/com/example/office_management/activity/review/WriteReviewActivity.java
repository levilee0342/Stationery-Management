package com.example.office_management.activity.review;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.office_management.R;
import com.example.office_management.adapter.ReviewImageAdapter;
import com.example.office_management.api.ReviewApi;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.review.ReviewRequest;
import com.example.office_management.dto.request.review.UpdateReviewRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ReviewResponse;
import com.example.office_management.dto.response.UserResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.utils.ImageCompressor;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton btnBack;
    private TextView tvLabelRating, tvCharCount, edtFullName;
    private RatingBar ratingBarInput;
    private EditText edtContent;
    private Button btnSendReview, btnSelectImage;
    private RecyclerView reviewImageList;
    private ConstraintLayout formContainer;
    private ReviewApi reviewApi;
    private UserApi userApi;
    private String authHeader, productId, parentId, replyOnUser, commentId;
    private boolean isReplyMode, isUpdateMode;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private ReviewImageAdapter imageAdapter;
    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        initView();

        // Khởi tạo Retrofit API service
        reviewApi = BaseURL.getUrl(this).create(ReviewApi.class);
        userApi = BaseURL.getUrl(this).create(UserApi.class);
        authHeader = "Bearer " + getAuthToken();
        productId = getIntent().getStringExtra("productId");
        String action = getIntent().getStringExtra("action");

        if ("reply".equals(action)) {
            isReplyMode = true;
            parentId = getIntent().getStringExtra("parentReviewId");
            replyOnUser = getIntent().getStringExtra("replyOnUserName");
        } else if ("edit".equals(action)) {
            isUpdateMode = true;
            commentId = getIntent().getStringExtra("reviewId");
            String content = getIntent().getStringExtra("content");
            int rating = getIntent().getIntExtra("rating", -1); // rating = -1 nghĩa là không có rating => reply

            ReviewResponse mockReview = new ReviewResponse();
            mockReview.setContent(content);

            if (rating >= 0) {
                mockReview.setRating(rating);
                isReplyMode = false; // là review chính
            } else {
                isReplyMode = true;  // là reply
            }

            loadReviewData(mockReview);
        }

        // Thiết lập toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Thiết lập giao diện dựa trên chế độ
        setupUI();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý đếm ký tự
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                tvCharCount.setText("(" + length + " characters)");
                if (length > 256) {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnSendReview.setEnabled(false);
                } else {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    btnSendReview.setEnabled(true);
                }
            }
        });

        // Xử lý nút Send
        btnSendReview.setOnClickListener(v -> {
            if (isUpdateMode) {
                updateReview();
            } else {
                createReviewOrReply();
            }
        });

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        // Khởi tạo RecyclerView cho ảnh
        imageAdapter = new ReviewImageAdapter(imageUrls, imageUrl -> {
            showImageDialog(WriteReviewActivity.this, imageUrl);
        });
        reviewImageList.setAdapter(imageAdapter);

        reviewImageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        reviewImageList.setAdapter(imageAdapter);
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btn_back);
        tvLabelRating = findViewById(R.id.tvLabelRating);
        ratingBarInput = findViewById(R.id.ratingBarInput);
        edtFullName = findViewById(R.id.edtFullName);
        edtContent = findViewById(R.id.edtContent);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnSendReview = findViewById(R.id.btnSendReview);
        reviewImageList = findViewById(R.id.review_image_list);
        formContainer = findViewById(R.id.formContainer);
        btnSelectImage = findViewById(R.id.select_image_button);
    }

    private void setupUI() {
        if (isReplyMode) {
            // Chế độ reply: ẩn rating và ảnh
            tvLabelRating.setVisibility(View.GONE);
            ratingBarInput.setVisibility(View.GONE);
            reviewImageList.setVisibility(View.GONE);
            btnSendReview.setText("Send Reply");
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Reply to Review");
        } else if (isUpdateMode) {
            // Chế độ update: lấy dữ liệu review hiện tại
            btnSendReview.setText("Update Review");
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Edit Review");
        } else {
            // Chế độ tạo review
            btnSendReview.setText("Send Review");
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Product Review");
        }
        loadUserFullName();
    }

    private void loadUserFullName() {
        // Giả sử gọi API để lấy thông tin người dùng
        userApi.getUserInfo(authHeader).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    edtFullName.setText("Name: " + response.body().getResult().getFullName());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(WriteReviewActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewData(ReviewResponse reviewResponse) {
        // Điền nội dung review
        if (reviewResponse.getContent() != null) {
            edtContent.setText(reviewResponse.getContent());
        }

        // Nếu đang trong chế độ chỉnh sửa (update)
        if (isUpdateMode) {
            // Nếu là đánh giá chính (không phải trả lời comment)
            if (!isReplyMode) {
                if (reviewResponse.getRating() != null) {
                    ratingBarInput.setRating(reviewResponse.getRating());
                }
                if (reviewResponse.getReviewImage() != null && !reviewResponse.getReviewImage().isEmpty()) {
                    imageUrls.clear();
                    imageUrls.addAll(reviewResponse.getReviewImage());
                    imageAdapter.notifyDataSetChanged();
                } else {
                    reviewImageList.setVisibility(View.GONE);
                }
            } else {
                // Là chỉnh sửa trả lời comment (reply)
                ratingBarInput.setVisibility(View.GONE);
                tvLabelRating.setVisibility(View.GONE);
                reviewImageList.setVisibility(View.GONE); // thường reply không có ảnh
            }
        }
    }

    // Chọn ảnh từ gallery
    private void openImagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGES_REQUEST) {
                if (data != null) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                        }
                    } else if (data.getData() != null) {
                        selectedImageUris.add(data.getData());
                    }
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                if (cameraImageUri != null) {
                    selectedImageUris.add(cameraImageUri);
                }
            }
            loadSelectedImages();
        }
    }

    // Load ảnh preview
    private void loadSelectedImages() {
        imageUrls.clear();
        for (Uri uri : selectedImageUris) {
            imageUrls.add(uri.toString());
        }
        imageAdapter.notifyDataSetChanged();
    }

    private void createReviewOrReply() {
        String content = edtContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length() > 256) {
            Toast.makeText(this, "Content exceeds 256 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewRequest request = new ReviewRequest();
        request.setProductId(productId);
        request.setContent(content);

        if (isReplyMode) {
            request.setParentId(parentId);
            request.setReplyOnUser(replyOnUser);
            Log.d("DEBUG_REQUEST", "ReviewRequest (Reply Mode): " + new Gson().toJson(request));
            sendCreateReview(request);
        } else {
            int rating = (int) ratingBarInput.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setRating(rating);

            if (!selectedImageUris.isEmpty()) {
                uploadAllImages(selectedImageUris, uploadedUrls -> {
                    request.setReviewImage(uploadedUrls);

                    // Log full request trước khi gửi
                    Log.d("DEBUG_REQUEST", "ReviewRequest (With Images): " + new Gson().toJson(request));

                    sendCreateReview(request);
                });
            } else {
                // Log nếu không có ảnh
                Log.d("DEBUG_REQUEST", "ReviewRequest (No Images): " + new Gson().toJson(request));
                sendCreateReview(request);
            }
        }
    }


    private void sendCreateReview(ReviewRequest request) {
        reviewApi.createReview(authHeader, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(WriteReviewActivity.this, isReplyMode ? "Reply sent" : "Review sent", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Error sending review";
                    Toast.makeText(WriteReviewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(WriteReviewActivity.this, "Failed to send: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReview() {
        String content = edtContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length() > 256) {
            Toast.makeText(this, "Content exceeds 256 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setCommentId(commentId);
        request.setContent(content);

        if (!isReplyMode) {
            int rating = (int) ratingBarInput.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setRating(rating);
            if (!selectedImageUris.isEmpty()) {
                uploadAllImages(selectedImageUris, uploadedUrls -> {
                    request.setReviewImage(uploadedUrls);
                    sendUpdateReview(request);
                });
            } else {
                sendUpdateReview(request);
            }
        } else {
            sendUpdateReview(request);
        }
    }

    private void sendUpdateReview(UpdateReviewRequest request) {
        reviewApi.updateReview(authHeader, commentId, request).enqueue(new Callback<ApiResponse<ApiResponse<Void>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiResponse<Void>>> call, Response<ApiResponse<ApiResponse<Void>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(WriteReviewActivity.this, "Review updated", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Error updating review";
                    Toast.makeText(WriteReviewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiResponse<Void>>> call, Throwable t) {
                Toast.makeText(WriteReviewActivity.this, "Failed to update: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAllImages(List<Uri> imageUris, OnUploadCompleteListener listener) {
        List<String> uploadedUrls = new ArrayList<>();
        uploadSingleImageRecursive(imageUris, 0, uploadedUrls, listener);
    }

    private void uploadSingleImageRecursive(List<Uri> imageUris, int index, List<String> uploadedUrls, OnUploadCompleteListener listener) {
        if (index >= imageUris.size()) {
            listener.onComplete(uploadedUrls);
            return;
        }

        Uri imageUri = imageUris.get(index);

        try {
            // trước khi đọc bytes
            byte[] fileBytes;
            fileBytes = ImageCompressor.compressImage(this, imageUri, 1_048_576);
            Log.d("UPLOAD_DEBUG", "Preparing image idx=" + index + ", size=" + fileBytes.length + " bytes, url ="+imageUri);


            RequestBody requestFile = RequestBody.create(fileBytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image_" + index + ".jpg", requestFile);

            userApi.uploadImage(body).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedUrls.add(response.body().getResult());
                        uploadSingleImageRecursive(imageUris, index + 1, uploadedUrls, listener);
                    } else {
                        String errorBody = "null";
                        try {
                            errorBody = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "errorBody()==null";
                        } catch (IOException e) {
                            Log.e("UPLOAD_DEBUG", "read errorBody failed", e);
                        }
                        Log.e("UPLOAD_DEBUG",
                                "UPLOAD FAILED → HTTP " + response.code() +
                                        "\nerrorBody: " + errorBody);
                        Toast.makeText(WriteReviewActivity.this,
                                "Upload failed, see logcat UPLOAD_DEBUG",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Log.e("UPLOAD_DEBUG", "onFailure: ", t);
                    Toast.makeText(WriteReviewActivity.this,
                            "Image upload failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getAuthToken() {
        return getSharedPreferences("auth", MODE_PRIVATE).getString("token", "");
    }

    private interface OnUploadCompleteListener {
        void onComplete(List<String> uploadedUrls);
    }

    private void showImageDialog(Context context, String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);

        ImageView imageView = dialog.findViewById(R.id.fullImageView);
        Glide.with(context).load(imageUrl).into(imageView);

        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}