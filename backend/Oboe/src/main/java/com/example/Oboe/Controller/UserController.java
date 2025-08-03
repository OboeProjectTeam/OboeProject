package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.UserProfileDTO;
import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


}
