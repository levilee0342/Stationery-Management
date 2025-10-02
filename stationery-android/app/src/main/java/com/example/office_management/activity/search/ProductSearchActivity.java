package com.example.office_management.activity.search;


import static com.example.office_management.retrofit2.BaseURL.DETECTION_BASE_URL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.activity.product.ProductActivity;
import com.example.office_management.adapter.ProductAdapter;
import com.example.office_management.api.DetectionApi;
import com.example.office_management.api.ProductApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.DetectionResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.utils.TFLiteImageClassifier;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductSearchActivity extends AppCompatActivity {
    private static final String TAG = "TEST_SCAN";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private RecyclerView searchResultsRecyclerView;
    private TextView tvSearchResults;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private AlertDialog cameraDialog, loadingDialog;
    private List<ProductResponse> productDatabase;
    private ProductAdapter productAdapter;
    private List<ProductResponse> searchResultList;
    private ProductApi productApi;
    private TFLiteImageClassifier classifier;
    private DetectionApi detectionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        Retrofit retrofitDetection = new Retrofit.Builder()
                .baseUrl(DETECTION_BASE_URL)  // <== chỉnh URL đúng của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        detectionApi = retrofitDetection.create(DetectionApi.class);

        cameraExecutor = Executors.newSingleThreadExecutor();
        productApi = BaseURL.getUrl(this).create(ProductApi.class);


        initViews();
        fetchProductList(null);
    }

    private void initViews() {
        ImageView btnScan = findViewById(R.id.btnScan);
        if (btnScan == null) {
            showToast("Cannot find btnScan in layout");
            return;
        }
        btnScan.setOnClickListener(v -> checkCameraPermission());

        tvSearchResults = findViewById(R.id.tvSearchResults);
        searchResultsRecyclerView = findViewById(R.id.rvSearchResults);
        if (tvSearchResults == null || searchResultsRecyclerView == null) {
            showToast("Cannot find tvSearchResults or rvSearchResults in layout");
            return;
        }

        searchResultList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, searchResultList);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(productAdapter);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCameraDialog();
        }
    }

    private void fetchProductList(String searchQuery) {
        showLoadingDialog();
        Log.d(TAG, "Fetching products with query: " + (searchQuery != null ? searchQuery : "null"));

        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                "name", null, null, null, searchQuery, null,0,10);

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call,
                                   Response<ApiResponse<ResultResponse>> response) {
                dismissLoadingDialog();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ResultResponse> apiResponse = response.body();
                    Log.d(TAG, "API Response - Code: " + apiResponse.getCode() +
                            ", Message: " + apiResponse.getMessage());

                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        productDatabase = apiResponse.getResult().getContent();
                        displaySearchResults();
                    } else {
                        showToast("Empty API data");
                    }
                } else {
                    showToast("API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                dismissLoadingDialog();
                Log.e(TAG, "API call failed", t);
                showToast("Connection error: " + t.getMessage());
            }
        });
    }

    private void openCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_camera, null);
        previewView = dialogView.findViewById(R.id.previewView);
        ImageView captureButton = dialogView.findViewById(R.id.captureButton);

        if (previewView == null || captureButton == null) {
            showToast("Cannot find previewView or captureButton in dialog layout");
            return;
        }

        builder.setView(dialogView);
        cameraDialog = builder.create();
        cameraDialog.show();

        startCamera();
        captureButton.setOnClickListener(v -> takePhoto());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Log.e(TAG, "Camera setup error", e);
                showToast("Camera error: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) {
            showToast("ImageCapture not initialized");
            return;
        }

        showLoadingDialog();
        imageCapture.takePicture(
                cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bitmap = imageProxyToBitmap(image);
                        image.close();

                        if (cameraDialog != null && cameraDialog.isShowing()) {
                            cameraDialog.dismiss();
                        }

                        if (bitmap != null) {
                            Log.d(TAG, "Captured image size: " +
                                    bitmap.getWidth() + "x" + bitmap.getHeight());
                            processImage(bitmap);
                        } else {
                            dismissLoadingDialog();
                            showToast("Cannot process image");
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Capture error", exception);
                        dismissLoadingDialog();
                        showToast("Capture error: " + exception.getMessage());
                    }
                }
        );
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        try {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Image conversion error", e);
            return null;
        }
    }

    private boolean isImageTooDark(Bitmap bitmap) {
        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int totalBrightness = 0;

        for (int x = 0; x < bitmap.getWidth(); x += 10) {
            for (int y = 0; y < bitmap.getHeight(); y += 10) {
                int pixel = bitmap.getPixel(x, y);
                int brightness = (int) (0.299 * ((pixel >> 16) & 0xFF) +
                        0.587 * ((pixel >> 8) & 0xFF) +
                        0.114 * (pixel & 0xFF));
                totalBrightness += brightness;
            }
        }

        int averageBrightness = totalBrightness / (pixelCount / 100);
        return averageBrightness < 50;
    }

    private void processImage(Bitmap bitmap) {
        if (isImageTooDark(bitmap)) {
            runOnUiThread(() -> {
                dismissLoadingDialog();
                showDetectionResult("Ảnh quá tối, vui lòng chụp lại ở nơi có ánh sáng tốt.");
            });
            return;
        }

        // Resize ảnh về chiều rộng 640px
        int targetWidth = 640;
        int targetHeight = (int) (bitmap.getHeight() * (targetWidth / (float) bitmap.getWidth()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

        // Crop ảnh thành hình vuông ở giữa (3/4 chiều dài ngắn nhất)
        int cropSize = Math.min(resizedBitmap.getWidth(), resizedBitmap.getHeight()) * 3 / 4;
        int cropX = (resizedBitmap.getWidth() - cropSize) / 2;
        int cropY = (resizedBitmap.getHeight() - cropSize) / 2;
        Bitmap croppedBitmap = Bitmap.createBitmap(resizedBitmap, cropX, cropY, cropSize, cropSize);

        // Convert ảnh sang byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Chuẩn bị multipart request
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

        // Gửi ảnh đến server detection
        Log.d(TAG, "Sending image to detection API");

        Call<DetectionResponse> call = detectionApi.detectProduct(body);
        call.enqueue(new Callback<DetectionResponse>() {
            @Override
            public void onResponse(Call<DetectionResponse> call, Response<DetectionResponse> response) {
                dismissLoadingDialog();

                if (response.isSuccessful() && response.body() != null) {
                    DetectionResponse detectionResponse = response.body();
                    Log.d(TAG, "Detection result: " + new Gson().toJson(detectionResponse));

                    if (detectionResponse.getLabel() != null) {
                        // Gọi hàm fetchProductList để tìm sản phẩm phù hợp
                        fetchProductList(detectionResponse.getLabel());
                        // Navigate to ProductActivity with the selected keyword
                        Intent intent = new Intent(ProductSearchActivity.this, ProductActivity.class);
                        intent.putExtra("keyword", detectionResponse.getLabel());
                        startActivity(intent);

                    } else {
                        showDetectionResult("Không nhận diện được sản phẩm");
                    }
                } else {
                    Log.d(TAG, "Raw detection response: " + (response.body() == null ? "null" : response.body().toString()));
                    showDetectionResult("Không nhận được kết quả từ server");
                }
            }

            @Override
            public void onFailure(Call<DetectionResponse> call, Throwable t) {
                dismissLoadingDialog();
                Log.e(TAG, "Detection API call failed", t);
                showDetectionResult("Lỗi kết nối: " + t.getMessage());
            }
        });
    }




    private MultipartBody.Part prepareImagePart(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        byte[] imageBytes = bos.toByteArray();

        RequestBody requestFile = RequestBody.create(
                imageBytes,
                MediaType.parse("image/jpeg")
        );

        return MultipartBody.Part.createFormData("image", "photo.jpg", requestFile);
    }

    private void showDetectionResult(String message) {
        runOnUiThread(() -> {
            dismissLoadingDialog();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            if (message.contains("too dark") || message.contains("No label") || message.contains("error")) {
                openCameraDialog();
            }
        });
    }

    private void displaySearchResults() {
        runOnUiThread(() -> {
            if (productDatabase == null || productDatabase.isEmpty()) {
                showToast("No matching products");
                tvSearchResults.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.GONE);
                return;
            }

            searchResultList.clear();
            searchResultList.addAll(productDatabase);
            tvSearchResults.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            productAdapter.notifyDataSetChanged();

            Log.d(TAG, "Displayed " + searchResultList.size() + " products");
        });
    }

    private void showLoadingDialog() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_loading);
            builder.setCancelable(false);
            loadingDialog = builder.create();
            loadingDialog.show();
        });
    }

    private void dismissLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraDialog();
            } else {
                showToast("Camera permission required");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (classifier != null) {
            classifier.close();
        }
    }
}