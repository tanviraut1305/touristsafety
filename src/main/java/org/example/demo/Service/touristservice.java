package org.example.demo.Service;

import org.example.demo.Entity.Tourist;
import org.example.demo.dto.createaccountreq;
import org.example.demo.dto.verifyreq;
import org.example.demo.dto.loginreq;
import org.example.demo.dto.loginresponse;
import org.example.demo.dto.profile;

public interface touristservice{

    void sendOtp(createaccountreq request);
    void verifyOtp(verifyreq request);
    loginresponse login(loginreq request);
    profile getProfile(String mobileNumber);
    profile updateProfile(String mobileNumber, profile request);

    Tourist getTouristByEmail(String email);
}