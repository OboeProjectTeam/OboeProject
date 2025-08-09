package com.example.Oboe.Service;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserProfileDTOwithStatistical;
import com.example.Oboe.DTOs.*;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class UserService implements UserDetailsService {
    @Value("${app.default-avatar}")
    private String defaultAvatar;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final FlashCardRepository flashCardRepository;
    @Value("${app.domain}")
    private String domain;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService ,BlogRepository blogRepository,CommentRepository commentRepository,
                       FlashCardRepository flashCardRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.blogRepository =blogRepository;
        this.commentRepository =commentRepository;
        this.flashCardRepository =flashCardRepository;

    }
    @Autowired
    private S3Service s3Service;

    public void registerWithEmail(UserDTOs userDTOs) {
        String username = userDTOs.getUserName();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }

        if (isValidEmail(username)) {
            // Gửi email xác minh
            String verificationToken = UUID.randomUUID().toString();
            VerificationHolder.getInstance().addToken(verificationToken, userDTOs);

            String verificationLink = domain+"/api/auth/verify?token=" + verificationToken;

            mailService.sendMail(username, "Xác minh tài khoản",
                    "Click vào liên kết để xác minh tài khoản của bạn: " + verificationLink);
        } else if (isValidPhone(username)) {
            // Không cần gửi email, xác minh luôn
            userDTOs.setVerified(true);
            addUser(userDTOs);
        } else {
            throw new IllegalArgumentException("Tên đăng nhập phải là email hoặc số điện thoại hợp lệ.");
        }
    }



    public User verifyAccount(String token) {
        UserDTOs signupRequest = VerificationHolder.getInstance().getSignupRequest(token);
        if (signupRequest == null) throw new IllegalArgumentException("Invalid or expired verification token.");
        signupRequest.setVerified(true);
        User user = addUser(signupRequest);
        VerificationHolder.getInstance().removeToken(token);
        return user;
    }

    public User addUser(UserDTOs userDTOs) {
        AuthProvider provider = userDTOs.getAuthProvider();
        String username = userDTOs.getUserName();

        // Cho phép trùng username nếu khác provider
        List<User> existingUsers = userRepository.findAllByUserNameAndAuthProvider(username, provider);
        if (!existingUsers.isEmpty()) {
            if (provider == AuthProvider.EMAIL) {
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            } else {
                return existingUsers.get(0); // Google/Facebook → dùng lại
            }
        }

        // Tạo mới nếu chưa tồn tại
        User user = new User();
        user.setUserName(username);
        user.setAuthProvider(provider);
        user.setFirstName(userDTOs.getFirstName());
        user.setLastName(userDTOs.getLastName());
        user.setDay_of_birth(userDTOs.getDay_of_birth());
        user.setAddress(userDTOs.getAddress());
        user.setRole(Role.ROLE_USER);
        user.setVerified(userDTOs.isVerified());
        user.setAccountType(AccountType.FREE);
        user.setProviderId(userDTOs.getProviderId());
        user.setCreate_at(LocalDateTime.now());
        user.setUpdate_at(LocalDateTime.now());
        user.setAvatarUrl(defaultAvatar);
        if (provider == AuthProvider.EMAIL) {
            if (userDTOs.getPassWord() == null || userDTOs.getPassWord().length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
            }
            user.setPassWord(passwordEncoder.encode(userDTOs.getPassWord()));
        } else {
            user.setPassWord(null);
        }

        return userRepository.save(user);
    }


    public List<User> findByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }


    public List<User> findByUserNameAndAuthProvider(String userName, AuthProvider provider) {
        return userRepository.findAllByUserNameAndAuthProvider(userName, provider);
    }

    public UserProfileDTOwithStatistical getUserByIds(UUID userId) {
        User user = userRepository.findByUser_id(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Thống kê số lượng nội dung của user
        long blogCount = blogRepository.countBlogsByUserId(userId);
        long commentCount = commentRepository.countCommentsByUserId(userId);
        long flashCardCount = flashCardRepository.countFlashCardByUserId(userId);

        // Tạo DTO với cả thông tin người dùng và thống kê
        UserProfileDTOwithStatistical dto = new UserProfileDTOwithStatistical(user);
        dto.setBlogCount((int) blogCount);
        dto.setCommentCount((int) commentCount);
        dto.setFlashCardCount((int) flashCardCount);

        return dto;
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findByUser_id(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Xóa comment của user
        commentRepository.deleteUserbyComment(userId);

        // Xóa blog của user
        blogRepository.deleteBlogsbyUser(userId);

        // Xóa flashcard của user
        flashCardRepository.deleteUser(userId);

        // xóa user
        userRepository.delete(user);
    }



    public List<User> findAllByUserName(String userName) {
        return userRepository.searchUsers(userName);
    }

    public UserDetails loadUserByUsername(String username) {
        List<User> users = userRepository.findAllByUserNameAndAuthProvider(username, AuthProvider.EMAIL);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found (EMAIL)");
        }
        if (users.size() > 1) {
            throw new UsernameNotFoundException("Tồn tại nhiều người dùng trùng thông tin đăng nhập.");
        }

        User user = users.get(0);

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("Tài khoản chưa xác minh email.");
        }

        if (user.getStatus() != null && user.getStatus().toString().equalsIgnoreCase("BANNED")) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa.");
        }

        return buildPrincipal(user);
    }


    public UserDetails loadUserByUsernameAndProvider(String username, AuthProvider provider) {
        List<User> users = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found (" + provider + ")");
        }
        if (users.size() > 1) {
            throw new UsernameNotFoundException("Tồn tại nhiều người dùng trùng thông tin đăng nhập.");
        }

        User user = users.get(0);

        if (user.getStatus() != null && user.getStatus().toString().equalsIgnoreCase("BANNED")) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa.");
        }

        return buildPrincipal(user);
    }


    private UserDetails buildPrincipal(User user) {
        String password = user.getPassWord();

        if (user.getAuthProvider() == AuthProvider.EMAIL && (password == null || password.isBlank())) {
            throw new UsernameNotFoundException("Password is missing for email login.");
        }

        return new CustomUserDetails(user);
    }


    public User updateMyOwnProfile(String username, AuthProvider authProvider, UserDTOs userDTOs) {
        List<User> users = userRepository.findAllByUserNameAndAuthProvider(username, authProvider);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        if (users.size() > 1) {
            throw new UsernameNotFoundException("Trùng thông tin tài khoản.");
        }

        User user = users.get(0);

        user.setFirstName(userDTOs.getFirstName());
        user.setLastName(userDTOs.getLastName());
        user.setAddress(userDTOs.getAddress());
        user.setDay_of_birth(userDTOs.getDay_of_birth());
        user.setUpdate_at(LocalDateTime.now());

        return userRepository.save(user);
    }



    public void changePassword(String username, PassWordChangeDTOs passWordChange) {
        List<User> users = userRepository.findAllByUserNameAndAuthProvider(username, AuthProvider.EMAIL);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (users.size() > 1) {
            throw new IllegalStateException("Tồn tại nhiều người dùng với cùng username.");
        }

        User user = users.get(0);

        if (user.getAuthProvider() != AuthProvider.EMAIL) {
            throw new IllegalArgumentException("Không thể đổi mật khẩu với tài khoản Google/Facebook.");
        }

        if (!passwordEncoder.matches(passWordChange.getOldPassword(), user.getPassWord())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(passWordChange.getNewPassword());

        user.setPassWord(passwordEncoder.encode(passWordChange.getNewPassword()));
        userRepository.save(user);
    }


//    public UserDTOs convertOAuthToDTO(String email, String firstName, String lastName, AuthProvider provider) {
//        UserDTOs dto = new UserDTOs();
//        dto.setUserName(email);
//        dto.setFirstName(firstName);
//        dto.setLastName(lastName);
//        dto.setVerified(true);
//        dto.setPassWord(null);
//        dto.setRole(Role.ROLE_USER);
//        dto.setAccountType(AccountType.FREE);
//        dto.setCreate_at(LocalDateTime.now());
//        dto.setUpdate_at(LocalDateTime.now());
//        dto.setAuthProvider(provider);
//        return dto;
//    }



    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException("Password is too weak");
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()].*");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{10,15}$");
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }


    public User uploadAvatarForUser(String username, AuthProvider provider, MultipartFile file) throws Exception {
        List<User> users = userRepository.findAllByUserNameAndAuthProvider(username, provider);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        if (users.size() > 1) {
            throw new IllegalStateException("Tồn tại nhiều người dùng trùng username + provider.");
        }

        User user = users.get(0);
        String avatarUrl = s3Service.uploadFile(file, "upload-avatar/");
        user.setAvatarUrl(avatarUrl);
        user.setUpdate_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


}