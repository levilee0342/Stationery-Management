package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.AddressRequest;
import com.project.stationery_be_server.dto.response.AddressResponse;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(addressRequest))
                .build();
    }
    @GetMapping
    public ApiResponse<List<AddressResponse>> getAllMyAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAllMyAddresses())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable String id, @RequestBody AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(id, addressRequest))
                .build();
    }
    @PutMapping("/default/{id}")
    public ApiResponse<AddressResponse> setDefaultAddress(@PathVariable String id) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.setDefaultAddress(id))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ApiResponse.<String>builder()
                .result("Address deleted successfully")
                .build();
    }
}

