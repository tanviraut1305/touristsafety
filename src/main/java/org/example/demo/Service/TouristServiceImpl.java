package org.example.demo.Service;

import org.example.demo.Entity.otp;
import org.example.demo.Repository.TouristRepository;
import org.example.demo.Repository.otprepository;
import org.example.demo.dto.createaccountreq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TouristServiceImpl implements touristservice {

    @Autowired
    private TouristRepository touristRepository;

    @Autowired
    private otprepository otpRepository;

    @Override
    public void sendOtp(createaccountreq request) {

        // Step 1 : Check Mobile Number
        if (touristRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new RuntimeException("Mobile Number already registered.");
        }

        // Step 2 : Check Tourist Type
        if (request.getTouristType().name().equals("INDIAN")) {

            if (touristRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                throw new RuntimeException("Aadhaar Number already registered.");
            }

        } else {

            if (touristRepository.existsByPassportNumber(request.getPassportNumber())) {
                throw new RuntimeException("Passport Number already registered.");
            }

        }

        // Step 3 : Generate OTP
        String generatedOtp = generateOtp();

        // Step 4 : Print OTP
        System.out.println("Generated OTP : " + generatedOtp);

        // Step 5 : Create OTP Object
        otp otpEntity = new otp();

        // Step 6 : Set Values
        otpEntity.setMobileNumber(request.getMobileNumber());
        otpEntity.setOtp(generatedOtp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        // Step 7 : Save OTP
        otpRepository.save(otpEntity);

        System.out.println("OTP Saved Successfully");

    }

    private String generateOtp() {

        int otp = 100000 + new Random().nextInt(900000);

        return String.valueOf(otp);

    }

}