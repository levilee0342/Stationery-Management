package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.request.AddressRequest;
import com.project.stationery_be_server.dto.response.AddressResponse;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.Address;
import com.project.stationery_be_server.entity.Color;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.repository.AddressRepository;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressServiceImpl implements AddressService {
     AddressRepository addressRepository;
     UserRepository userRepository;

    @Override
    public AddressResponse createAddress(AddressRequest addressRequest) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu là địa chỉ mặc định, cập nhật lại các địa chỉ khác
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
            unsetDefaultAddressForUser(user);
        }

        Address address = new Address();
        address.setAddressName(addressRequest.getAddressName());
        address.setPhone(addressRequest.getPhone());
        address.setRecipient(addressRequest.getRecipient());
        address.setDefault(addressRequest.getIsDefault() != null && addressRequest.getIsDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return AddressResponse.builder()
                .addressId(savedAddress.getAddressId())
                .addressName(savedAddress.getAddressName())
                .recipient(savedAddress.getRecipient())
                .phone(savedAddress.getPhone())
                .isDefault(savedAddress.isDefault())
                .build();
    }

    @Override
    public List<AddressResponse> getAllMyAddresses() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(address -> AddressResponse.builder()
                        .addressId(address.getAddressId())
                        .addressName(address.getAddressName())
                        .phone(address.getPhone())
                        .recipient(address.getRecipient())
                        .isDefault(address.isDefault())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse updateAddress(String id, AddressRequest addressRequest) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));

        // Nếu request yêu cầu đặt lại mặc định
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
            unsetDefaultAddressForUser(user);
        }

        address.setAddressName(addressRequest.getAddressName());
        address.setPhone(addressRequest.getPhone());
        address.setDefault(addressRequest.getIsDefault() != null && addressRequest.getIsDefault());
        address.setUser(user);

        Address updatedAddress = addressRepository.save(address);

        return AddressResponse.builder()
                .addressId(updatedAddress.getAddressId())
                .addressName(updatedAddress.getAddressName())
                .phone(updatedAddress.getPhone())
                .isDefault(updatedAddress.isDefault())
                .build();
    }
    @Override
    public void deleteAddress(String id) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));

        // Kiểm tra xem địa chỉ có thuộc về người dùng hiện tại không
        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this address");
        }

        addressRepository.delete(address);
    }

    private void unsetDefaultAddressForUser(User user) {
        List<Address> userAddresses = addressRepository.findByUser(user);
        for (Address addr : userAddresses) {
            if (Boolean.TRUE.equals(addr.isDefault())) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        }
    }
    @Override
    public AddressResponse setDefaultAddress(String addressId) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

        // Gỡ bỏ default hiện tại (nếu có)
        unsetDefaultAddressForUser(user);

        // Đặt địa chỉ này thành default
        address.setDefault(true);
        Address saved = addressRepository.save(address);

        return AddressResponse.builder()
                .addressId(saved.getAddressId())
                .addressName(saved.getAddressName())
                .phone(saved.getPhone())
                .isDefault(saved.isDefault())
                .build();
    }
}
