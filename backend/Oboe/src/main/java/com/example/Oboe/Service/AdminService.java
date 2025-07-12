package com.example.Oboe.Service;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Autowired
    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    //  Tạo tài khoản mới (có thể là ADMIN hoặc USER)
    public User createUser(UserDTOs dto) {
        if (dto.getUserName() == null || dto.getUserName().isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }

        Optional<User> existingUser = userRepository.findByUserNameAndAuthProvider(dto.getUserName(), dto.getAuthProvider());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("Tài khoản đã tồn tại.");
        }

        if (dto.getRole() == Role.ROLE_USER) {
            String token = UUID.randomUUID().toString();
            VerificationHolder.getInstance().addToken(token, dto);

            String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;
            mailService.sendMail(dto.getUserName(), "Xác minh tài khoản",
                    "Vui lòng xác minh tài khoản tại: " + verifyLink);
            return null; // chưa tạo ngay
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);

        if (dto.getAuthProvider() == AuthProvider.EMAIL) {
            if (dto.getPassWord() == null || dto.getPassWord().length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
            }
            user.setPassWord(passwordEncoder.encode(dto.getPassWord()));
        } else {
            user.setPassWord(null);
        }

        user.setVerified(true);
        user.setCreate_at(LocalDateTime.now());
        user.setUpdate_at(LocalDateTime.now());
        user.setStatus(Status.ACTION);
        return userRepository.save(user);
    }

    //  Sửa thông tin người dùng
    public User updateUser(UUID id, UserDTOs dto) {
        User user = getUserById(id);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setDay_of_birth(dto.getDay_of_birth());
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    //  Xoá người dùng
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng.");
        }
        userRepository.deleteById(id);
    }

    // Đổi role
    public User changeRole(UUID id, Role newRole) {
        User user = getUserById(id);
        user.setRole(newRole);
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Ban / Unban
    public User updateStatus(UUID id, Status status) {
        User user = getUserById(id);
        user.setStatus(status);
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Đổi mật khẩu (Admin cấp lại password)
    public User resetPassword(UUID id, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự");
        }
        User user = getUserById(id);
        user.setPassWord(passwordEncoder.encode(newPassword));
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + id));
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findAllByUserName(keyword);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
