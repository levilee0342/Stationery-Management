package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Address;
import com.project.stationery_be_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {

    List<Address> findByUser(User user);
    Optional<Address> findByAddressId(String address);
}
