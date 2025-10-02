package com.example.office_management.api;

import com.example.office_management.dto.response.address.ApiAddressResponse;
import com.example.office_management.dto.response.address.DistrictResponse;
import com.example.office_management.dto.response.address.ProvinceResponse;
import com.example.office_management.dto.response.address.WardResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VietnamAddressApi {
    @GET("provinces/getAll")
    Call <ApiAddressResponse<ProvinceResponse>> getProvinces(@Query("limit") int limit);

    @GET("districts/getByProvince")
    Call<ApiAddressResponse<DistrictResponse>> getDistricts(@Query("provinceCode") String provinceCode, @Query("limit") int limit);

    @GET("wards/getByDistrict")
    Call<ApiAddressResponse<WardResponse>> getWards(@Query("districtCode") String districtCode, @Query("limit") int limit);
}
