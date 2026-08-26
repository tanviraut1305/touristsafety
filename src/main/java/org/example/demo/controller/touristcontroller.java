package org.example.demo.controller;

import org.example.demo.Service.touristservice;
import org.example.demo.dto.createaccountreq;
import org.example.demo.dto.loginreq;
import org.example.demo.dto.loginresponse;
import org.example.demo.dto.verifyreq;
import org.example.demo.dto.profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tourist")
public class touristcontroller {

    @Autowired
    private touristservice touristService;


    // =========================
    // SEND OTP
    // =========================
    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody createaccountreq request) {

        touristService.sendOtp(request);

        return "OTP Sent Successfully";
    }


    // =========================
    // VERIFY OTP
    // =========================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody verifyreq request) {

        touristService.verifyOtp(request);

        return "OTP Verified Successfully";
    }


    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public loginresponse login(@RequestBody loginreq request) {

        return touristService.login(request);
    }


    // =========================
    // GET PROFILE
    // =========================
    @GetMapping("/profile")
    public profile getProfile(Authentication authentication) {

        String mobileNumber = authentication.getName();

        return touristService.getProfile(mobileNumber);
    }


    // =========================
    // UPDATE PROFILE
    // =========================
    @PutMapping("/profile")
    public profile updateProfile(
            Authentication authentication,
            @RequestBody profile request) {

        String mobileNumber = authentication.getName();

        return touristService.updateProfile(
                mobileNumber,
                request
        );
    }
}