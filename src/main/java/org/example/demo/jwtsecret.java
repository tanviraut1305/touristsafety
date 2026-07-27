package org.example.demo;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class jwtsecret{

    public static void main(String[] args) throws Exception {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        keyGenerator.init(256);

        SecretKey secretKey = keyGenerator.generateKey();

        String secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println(secret);
    }
}