package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.ActivityDTO;
import com.example.Oboe.DTOs.StatisticalUserDTOs;
import com.example.Oboe.DTOs.StatisticalUserDetailDTO;
import com.example.Oboe.Service.StatisticalUserSerivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticalUserController {

    private final StatisticalUserSerivce statisticalUserSerivce;

    @Autowired
    public StatisticalUserController(StatisticalUserSerivce statisticalUserSerivce) {
        this.statisticalUserSerivce = statisticalUserSerivce;
    }

    @GetMapping("/me")
    public ResponseEntity<StatisticalUserDTOs> getMyStatistics(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        StatisticalUserDTOs stats = statisticalUserSerivce.countUserContent(userId);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getUserActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            @RequestParam(defaultValue = "all") String type
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityDTO> activityPage = statisticalUserSerivce.getUserActivities(userId, pageable);
        //tạo một đối tượng Map, trong đó (Key (khóa) là String (: "content", "page", "totalElements",...).
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", activityPage.getContent());
        response.put("page", activityPage.getNumber());
        response.put("size", activityPage.getSize());
        response.put("totalElements", activityPage.getTotalElements());
        response.put("totalPages", activityPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

}
