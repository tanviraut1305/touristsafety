package org.example.demo.Service;

import org.example.demo.Entity.Tourist;
import org.example.demo.Entity.otp;
import org.example.demo.Repository.TouristRepository;
import org.example.demo.Repository.otprepository;
import org.example.demo.dto.createaccountreq;
import org.example.demo.dto.loginreq;
import org.example.demo.dto.loginresponse;
import org.example.demo.dto.verifyreq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TouristServiceImpl implements touristservice {

    @Autowired
    private TouristRepository touristRepository;

    @Autowired
    private otprepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private jwtservice jwtService;

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
        otpEntity.setMobileNumber(request.getMobileNumber());
        otpEntity.setOtp(generatedOtp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        // Step 6 : Save OTP
        otpRepository.save(otpEntity);

        // Step 7 : Save Tourist
        Tourist tourist = Tourist.builder()
                .touristType(request.getTouristType())
                .aadhaarNumber(request.getAadhaarNumber())
                .passportNumber(request.getPassportNumber())
                .country(request.getCountry())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .verified(false)
                .build();

        touristRepository.save(tourist);

        System.out.println("OTP Saved Successfully");
    }

    @Override
    public void verifyOtp(verifyreq request) {

        Optional<otp> optionalOtp =
                otpRepository.findTopByMobileNumberOrderByExpiryTimeDesc(
                        request.getMobileNumber());

        if (optionalOtp.isEmpty()) {
            throw new RuntimeException("OTP not found.");
        }

        otp otpEntity = optionalOtp.get();

        if (LocalDateTime.now().isAfter(otpEntity.getExpiryTime())) {
            throw new RuntimeException("OTP has expired.");
        }

        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        Tourist tourist = touristRepository
                .findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new RuntimeException("Tourist not found."));

        tourist.setVerified(true);

        touristRepository.save(tourist);

        otpRepository.delete(otpEntity);

        System.out.println("OTP Verified Successfully");
    }
    @Override
    public loginresponse login(loginreq request) {

        Tourist tourist = touristRepository
                .findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() ->
                        new RuntimeException("Tourist not found."));

        if (!tourist.getVerified()) {
            throw new RuntimeException("Account not verified.");
        }

        if (!passwordEncoder.matches(request.getPassword(),
                tourist.getPassword())) {

            throw new RuntimeException("Invalid Password.");
        }

        String token = jwtService.generateToken(tourist);

        return new loginresponse(
                token,
                "Login Successful"
        );

    }
    private String generateOtp() {

        int otp = 100000 + new Random().nextInt(900000);

        return String.valueOf(otp);
    }
}