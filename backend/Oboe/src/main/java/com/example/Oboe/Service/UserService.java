package com.example.Oboe.Service;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public void registerWithEmail(UserDTOs userDTOs) {
        if (!isValidEmail(userDTOs.getUserName())) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }

        String verificationToken = UUID.randomUUID().toString();
        VerificationHolder.getInstance().addToken(verificationToken, userDTOs);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        mailService.sendMail(userDTOs.getUserName(), "Please verify your email",
                "Click the link to verify your account: " + verificationLink);
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

        // ✅ Cho phép trùng username nếu khác provider
        Optional<User> existingOpt = userRepository.findByUserNameAndAuthProvider(username, provider);
        if (existingOpt.isPresent()) {
            if (provider == AuthProvider.EMAIL) {
                // Nếu là đăng ký bằng email thì chặn lại
                throw new IllegalStateException("Tài khoản email đã được sử dụng.");
            } else {
                // Nếu là Google/Facebook → trả về user đã có (đăng nhập lại)
                return existingOpt.get();
            }
        }

        // Nếu chưa tồn tại → tạo mới
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

        // Nếu là EMAIL thì mã hóa mật khẩu
        if (provider == AuthProvider.EMAIL) {
            if (userDTOs.getPassWord() == null || userDTOs.getPassWord().length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải ít nhất 8 ký tự.");
            }
            user.setPassWord(passwordEncoder.encode(userDTOs.getPassWord()));
        } else {
            user.setPassWord(null); // Google/Facebook không cần mật khẩu
        }

        return userRepository.save(user);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> findByUserNameAndAuthProvider(String userName, AuthProvider provider) {
        return userRepository.findByUserNameAndAuthProvider(userName, provider);
    }

    public List<User> findAllByUserName(String userName) {
        return userRepository.findAllByUserName(userName);
    }

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository
                .findByUserNameAndAuthProvider(username, AuthProvider.EMAIL)
                .orElseThrow(() -> new UsernameNotFoundException("User not found (EMAIL)"));

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("Tài khoản chưa xác minh email.");
        }

        return buildPrincipal(user);
    }

    public UserDetails loadUserByUsernameAndProvider(String username, AuthProvider provider) {
        User user = userRepository
                .findByUserNameAndAuthProvider(username, provider)
                .orElseThrow(() -> new UsernameNotFoundException("User not found (" + provider + ")"));

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
        User user = userRepository.findByUserNameAndAuthProvider(username, authProvider)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(userDTOs.getFirstName());
        user.setLastName(userDTOs.getLastName());
        user.setAddress(userDTOs.getAddress());
        user.setDay_of_birth(userDTOs.getDay_of_birth());
        user.setUpdate_at(LocalDateTime.now());

        return userRepository.save(user);
    }


    public void changePassword(String username, PassWordChangeDTOs passWordChange) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(passWordChange.getOldPassword(), user.getPassWord())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        validatePassword(passWordChange.getNewPassword());

        user.setPassWord(passwordEncoder.encode(passWordChange.getNewPassword()));
        userRepository.save(user);
    }

    public UserDTOs convertOAuthToDTO(String email, String firstName, String lastName, AuthProvider provider) {
        UserDTOs dto = new UserDTOs();
        dto.setUserName(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setVerified(true);
        dto.setPassWord(null);
        dto.setRole(Role.ROLE_USER);
        dto.setAccountType(AccountType.FREE);
        dto.setCreate_at(LocalDateTime.now());
        dto.setUpdate_at(LocalDateTime.now());
        dto.setAuthProvider(provider);
        return dto;
    }

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

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

}
