package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Util {

    public String encode(String password){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(password.getBytes());
    }
    public String decode(String password){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(password);
        return new String(decode);

    }
}
