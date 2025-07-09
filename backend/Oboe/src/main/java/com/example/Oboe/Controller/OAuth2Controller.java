package com.example.Oboe.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/success")
    public ResponseEntity<?> success(@RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "OAuth2 login successful");
        return ResponseEntity.ok(response);
    }
} 