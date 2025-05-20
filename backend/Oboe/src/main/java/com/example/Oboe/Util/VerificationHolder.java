package com.example.Oboe.Util;

import com.example.Oboe.DTOs.UserDTOs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationHolder {

    private static volatile VerificationHolder instance;
    private final Map<String, UserDTOs> verificationTokens = new ConcurrentHashMap<>();

    private VerificationHolder() {}

    public static VerificationHolder getInstance() {
        if (instance == null) {
            synchronized (VerificationHolder.class) {
                if (instance == null) {
                    instance = new VerificationHolder();
                }
            }
        }
        return instance;
    }

    public void addToken(String token, UserDTOs userDTOs) {
        verificationTokens.put(token, userDTOs);
    }

    public UserDTOs getSignupRequest(String token) {
        return verificationTokens.get(token);
    }

    public void removeToken(String token) {
        verificationTokens.remove(token);
    }
}
