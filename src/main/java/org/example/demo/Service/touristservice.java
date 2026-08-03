package org.example.demo.Service;

import org.example.demo.dto.createaccountreq;
import org.example.demo.dto.verifyreq;
import org.example.demo.dto.loginreq;
import org.example.demo.dto.loginresponse;

public interface touristservice{

    void sendOtp(createaccountreq request);
    void verifyOtp(verifyreq request);
    loginresponse login(loginreq request);
}