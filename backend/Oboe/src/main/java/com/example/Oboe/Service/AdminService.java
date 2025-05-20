package com.example.Oboe.Service;


import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User changeUserRole(String username, String roleStr) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        Role newRole;
        try {
            newRole = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không hợp lệ");
        }

        user.setRole(newRole);

        return userRepository.save(user);
    }
    public void deleteUserByUserId(Long userId) {
        if (!userRepository.existsByUserName(userId)) {
            throw new IllegalArgumentException("User not found with username: " + userId);
        }

        userRepository.deleteByUserName(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
