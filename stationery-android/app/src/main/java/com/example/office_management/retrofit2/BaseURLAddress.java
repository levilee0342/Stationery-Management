package com.example.office_management.retrofit2;

import com.example.office_management.api.VietnamAddressApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseURLAddress {
    private static final String BASE_URL = "https://vn-public-apis.fpo.vn/";
    private static Retrofit retrofit;

    public static VietnamAddressApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(VietnamAddressApi.class);
    }
}

