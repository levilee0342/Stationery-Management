package com.example.office_management.activity.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.office_management.R;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.request.UpdateUserRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.UserResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.office_management.utils.ImageCompressor;

public class UserInforActivity extends AppCompatActivity {
    private EditText textFirstName, textLastName,textEmail, textPhone, textDob;
    private Button  btnUpdateProfile;
    private UserApi apiService;
    private ImageButton btnBack, btnChangeImage;
    private CircleImageView imageAvatar;
//    private ImageView imageAvatar;
    private Uri selectedImageUri; // Lưu URI của ảnh được chọn
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor);
        textFirstName = findViewById(R.id.text_first_name);
        textLastName = findViewById(R.id.text_last_name);
        textEmail = findViewById(R.id.text_email);
        textPhone = findViewById(R.id.text_phone);
        textDob = findViewById(R.id.text_dob);
        btnChangeImage = findViewById(R.id.btn_change_image);
        btnUpdateProfile = findViewById(R.id.button_update);
        btnBack = findViewById(R.id.btn_back);
        imageAvatar = findViewById(R.id.profile_image);



        apiService = BaseURL.getUrl(this).create(UserApi.class);

        loadUserInfo();

        // Thiết lập sự kiện
        btnBack.setOnClickListener(v -> onBackPressed());
        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        imageAvatar.setOnClickListener(v -> openImagePicker());
        btnChangeImage.setOnClickListener(v -> openImagePicker());
        textDob.setOnClickListener(v -> showDatePickerDialog());
    }
    private void openImagePicker() {
        mGetContent.launch("image/*");
    }
    private void loadUserInfo() {
        Log.d("DEBUG_USER_INFO", "loadUserInfo() được gọi");
        SharedPreferences prefs = UserInforActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        apiService.getUserInfo(authHeader).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("USER_INFO_DEBUG", "Response: " + new Gson().toJson(response.body()));

                    UserResponse user = response.body().getResult(); // LẤY result
                    textFirstName.setText(user.getFirstName());
                    textLastName.setText(user.getLastName());
                    textEmail.setText(user.getEmail());
                    textPhone.setText(user.getPhone());
                    Date dob = user.getDob();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    textDob.setText(dob != null ? formatter.format(dob) : "");

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        // ĐOẠN CODE MỚI
                        Glide.with(UserInforActivity.this)
                                .load(user.getAvatar())
                                .thumbnail(0.1f)

                                // YÊU CẦU GLIDE BIẾN ẢNH THÀNH HÌNH TRÒN
                                .override(120, 120)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.ic_user_placeholder)
                                .error(R.drawable.ic_user_placeholder)
                                .into(imageAvatar);
                    }
                } else {
                    Toast.makeText(UserInforActivity.this, "Unable to get user information", Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Log.e("API_ERROR", "Error: " + response.code() + ", errorBody: " + errorJson);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(UserInforActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        // Lấy token
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String authHeader = "Bearer " + token;

        String firstName = textFirstName.getText().toString().trim();
        String lastName = textLastName.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        String dobStr = textDob.getText().toString().trim();
        String dobFormattedForApi = "";

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            if(!dobStr.isEmpty()){
                Date dob = displayFormat.parse(dobStr);
                dobFormattedForApi = apiFormat.format(dob);
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2. Tạo đối tượng Request và chuyển thành JSON
        // Định dạng lại Date thành String theo định dạng chuẩn
        UpdateUserRequest request =  new UpdateUserRequest(firstName, lastName, email, phone, dobFormattedForApi);
        Gson gson = new Gson();
        String userJson = gson.toJson(request);
        // 3. Tạo RequestBody cho phần JSON
        RequestBody documentBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), userJson);
        // 4. Tạo MultipartBody.Part cho phần file (nếu người dùng đã chọn ảnh mới)
        Call<ApiResponse<UserResponse>> call;
        // 3. Kiểm tra xem người dùng có chọn ảnh mới không
        MultipartBody.Part filePart = null; // Khởi tạo filePart là null
        if (selectedImageUri != null) {
            // Trường hợp: Cập nhật CẢ THÔNG TIN VÀ ẢNH
            try {
                // Giới hạn upload 1 MB
                final long MAX_UPLOAD_SIZE = 1_048_576;

                byte[] fileBytes;
                fileBytes = ImageCompressor.compressImage(this, selectedImageUri, MAX_UPLOAD_SIZE);

                String mimeType = getContentResolver().getType(selectedImageUri);

                RequestBody fileRequestBody = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                // Tạo MultipartBody.Part cho file ảnh
                // "file" là tên key mà backend mong đợi. Hãy thay đổi nếu cần.
                filePart = MultipartBody.Part.createFormData("file", "profile.jpg", fileRequestBody);

                // Gọi API updateUserWithAvatar (phiên bản multipart)
                call = apiService.updateUser(authHeader, documentBody, filePart);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error reading image file", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Trường hợp: CHỈ CẬP NHẬT THÔNG TIN (không có ảnh mới)
            // Gọi API updateUser (phiên bản chỉ có JSON)
            call = apiService.updateUser(authHeader, documentBody,filePart);
        }
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserInforActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    // Tùy chọn: Tải lại thông tin người dùng sau khi cập nhật thành công
                    loadUserInfo();
                } else {
                    Toast.makeText(UserInforActivity.this, "Update error", Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null) {
                            Log.e("UPDATE_ERROR", "Error: " + response.code() + ", Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(UserInforActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UPDATE_FAILURE", "Error", t);
            }
        });
    }
    // Trình khởi chạy để chọn ảnh từ thư viện
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Hiển thị ảnh vừa chọn lên ImageView
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop() // Biến ảnh thành hình tròn
                            .into(imageAvatar);
                }
            }
    );


    // Hàm tiện ích để chuyển InputStream thành byte array
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



// Thêm phương thức này vào class
            private void showDatePickerDialog() {
                // Lấy ngày tháng năm hiện tại để làm giá trị mặc định cho lịch
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Tạo một DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // Context (chính là Activity này)
                        this,
                        // Listener được gọi khi người dùng chọn xong ngày
                        (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                            // Tạo một Calendar mới với ngày người dùng đã chọn
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth);

                            // Định dạng ngày đã chọn thành chuỗi "dd/MM/yyyy"
                            String formattedDate = displayFormat.format(selectedDate.getTime());

                            // Gán chuỗi ngày đã định dạng vào EditText
                            textDob.setText(formattedDate);
                        },
                        // Năm, tháng, ngày mặc định khi lịch hiện ra
                        year, month, day);

                // Hiển thị hộp thoại lịch
                datePickerDialog.show();
            }

}