package com.example.Oboe.Service;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserProfileDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Transactional
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final FlashCardRepository flashCardRepository;
    @Value("${app.default-avatar}")
    private String defaultAvatar;

    @Value("${app.domain}")
    private String domain;

    @Autowired
    private UserRepositoryCustomImpl userRepositoryCustomImpl;
    @Autowired
    public AdminService(UserRepository userRepository, BlogRepository blogRepository, CommentRepository commentRepository,
                        FlashCardRepository flashCardRepository,
                        PasswordEncoder passwordEncoder,
                        MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.flashCardRepository = flashCardRepository;
    }

    // Tạo tài khoản mới (Admin hoặc User)
    public User createUser(UserDTOs userDTO) {
        AuthProvider provider = userDTO.getAuthProvider();
        String username = userDTO.getUserName();

        List<User> existingUsers = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (!existingUsers.isEmpty()) {
            if (provider == AuthProvider.EMAIL) {
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            }
            return existingUsers.get(0);
        }

        User user = buildNewUser(userDTO);

        if (provider == AuthProvider.EMAIL) {
            validatePassword(userDTO.getPassWord());
            user.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));
        } else {
            user.setPassWord(null);
        }

        return userRepository.save(user);
    }


    private User buildNewUser(UserDTOs dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setAuthProvider(dto.getAuthProvider());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDay_of_birth(dto.getDay_of_birth());
        user.setAddress(dto.getAddress());
        user.setRole(Role.ROLE_USER);
        user.setVerified(dto.isVerified());
        user.setAccountType(AccountType.FREE);
        user.setProviderId(dto.getProviderId());
        user.setCreate_at(LocalDateTime.now());
        user.setUpdate_at(LocalDateTime.now());
        user.setAvatarUrl(defaultAvatar);
        user.setVerified(true);
        return user;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
        }
    }



    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }


    private boolean isValidPhone(String phone) {
        String regex = "^\\+?[0-9]{10,15}$";
        return Pattern.matches(regex, phone);
    }

    // Cập nhật người dùng
    public User updateUser(UUID id, UserDTOs dto) {
        User user = getUserById(id);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setDay_of_birth(dto.getDay_of_birth());
        user.setAccountType(dto.getAccountType());
        user.setRole(dto.getRole());
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Xoá người dùng
//    public void deleteUser(UUID id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng."));
//
//        commentRepository.deleteUserbyComment(id);
//
//        blogRepository.deleteBlogsbyUser(id);
//
//
//        userRepository.delete(user);
//    }


    // Đổi role
    public User changeRole(UUID id, Role newRole) {
        User user = getUserById(id);
        user.setRole(newRole);
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Ban hoặc unban
    public User updateStatus(UUID id, Status status) {
        User user = getUserById(id);
        user.setStatus(status);
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Reset mật khẩu
    public User resetPassword(UUID id, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự");
        }
        User user = getUserById(id);
        user.setPassWord(passwordEncoder.encode(newPassword));
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Lấy toàn bộ người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy người dùng theo ID
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với id: " + id));
    }
    public List<UserProfileDTO> getAllUserProfiles() {
        return userRepository.findAll()
                .stream()
                .map(UserProfileDTO::new)
                .toList();
    }
    // Tìm kiếm theo từ khóa
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    public List<User> findByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }


    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public void deleteUser(UUID userId) {
        userRepositoryCustomImpl.deleteUserWithDependencies(userId);
    }
}
