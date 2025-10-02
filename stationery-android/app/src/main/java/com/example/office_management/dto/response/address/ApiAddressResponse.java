package com.example.office_management.dto.response.address;

import java.util.List;

public class ApiAddressResponse<T> {
    private int exitcode;
    private DataResponse<T> data;

    public DataResponse<T> getData() {
        return data;
    }

    public static class DataResponse<T> {
        private List<T> data;

        public List<T> getData() { return data; }
    }
}

