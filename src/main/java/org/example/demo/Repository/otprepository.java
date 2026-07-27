package org.example.demo.Repository;

import org.example.demo.Entity.otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface otprepository extends JpaRepository<otp, Long> {

    Optional<otp> findTopByMobileNumberOrderByExpiryTimeDesc(String mobileNumber);

}