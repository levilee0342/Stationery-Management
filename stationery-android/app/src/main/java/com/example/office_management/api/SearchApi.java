package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SearchApi {
    @GET("search/top-keywords")
    Call<ApiResponse<List<String>>> apiTopKeyWords();
}
