package com.example.Oboe.Service;
import com.example.Oboe.DTOs.PassWordChangeDTOs;
import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.Entity.Role;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.VerificationHolder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        String verificationToken = UUID.randomUUID().toString();

        VerificationHolder.getInstance().addToken(verificationToken, userDTOs);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        String emailSubject = "Please verify your email";
        String emailBody = "Click the link to verify your account: " + verificationLink;

        mailService.sendMail(userDTOs.getUserName(), emailSubject, emailBody);
    }

    public User verifyAccount(String token) {
        UserDTOs signupRequest = VerificationHolder.getInstance().getSignupRequest(token);

        if (signupRequest == null) {
            throw new IllegalArgumentException("Invalid or expired verification token.");
        }

        signupRequest.setVerified(true);
        User user = addUser(signupRequest);
        VerificationHolder.getInstance().removeToken(token);

        return user;
    }

    public User addUser(UserDTOs userDTOs) {
        User user = new User();
        user.setUserName(userDTOs.getUserName());
        user.setFirstName(userDTOs.getFirstName());
        user.setLastName(userDTOs.getLastName());
        if (!isStrongPassword(userDTOs.getPassWord())) {
            throw new IllegalArgumentException("New password is too weak");
        }
        user.setPassWord(passwordEncoder.encode(userDTOs.getPassWord()));
        user.setRole(Role.ROLE_USER);
        user.setAddress(userDTOs.getAddress());
        user.setDay_of_birth(userDTOs.getDay_of_birth());
        user.setVerified(userDTOs.isVerified());
        user.setCreate_at(userDTOs.getCreate_at());
        user.setUpdate_at(userDTOs.getUpdate_at());

        return userRepository.save(user);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassWord(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    public User updateMyOwnProfile(String username, UserDTOs userDTOs) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(userDTOs.getFirstName());
        user.setLastName(userDTOs.getLastName());
        user.setAddress(userDTOs.getAddress());
        user.setDay_of_birth(userDTOs.getDay_of_birth());
        user.setUpdate_at(userDTOs.getUpdate_at());

        return userRepository.save(user);
    }

    public void changePassword(String username, PassWordChangeDTOs passWordChange) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(passWordChange.getOldPassword(), user.getPassWord())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!isStrongPassword(passWordChange.getNewPassword())) {
            throw new IllegalArgumentException("New password is too weak");
        }

        user.setPassWord(passwordEncoder.encode(passWordChange.getNewPassword()));
        userRepository.save(user);
    }
    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && //Toi thieu 8 ki tu
                password.matches(".*[A-Z].*") && //it nhat 1 chu in hoa
                password.matches(".*[a-z].*") && //it nhat 1 chu in thuong
                password.matches(".*\\d.*") && //it nhat 1 so
                password.matches(".*[!@#$%^&*()].*"); // it nhat 1 ki tu dac biet
    }
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
