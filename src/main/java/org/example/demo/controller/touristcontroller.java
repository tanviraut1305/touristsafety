package org.example.demo.controller;

import org.example.demo.Service.touristservice;
import org.example.demo.dto.createaccountreq;
import org.example.demo.dto.loginreq;
import org.example.demo.dto.loginresponse;
import org.example.demo.dto.verifyreq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tourist")
public class touristcontroller{

    @Autowired
    private touristservice touristService;

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody createaccountreq request) {

        touristService.sendOtp(request);

        return "OTP Sent Successfully";
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody verifyreq request) {
        touristService.verifyOtp(request);
        return "OTP Verified Successfully";
    }
    @PostMapping("/login")
    public loginresponse login(@RequestBody loginreq request) {

        return touristService.login(request);

    }
}