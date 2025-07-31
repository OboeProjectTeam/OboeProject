package com.example.Oboe.DTOs;

public class FirebaseLoginRequest {
    private String idToken;

    public FirebaseLoginRequest() {}

    public FirebaseLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}