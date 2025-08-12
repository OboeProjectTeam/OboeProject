package com.example.Oboe.Service;

import com.example.Oboe.Entity.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import com.example.Oboe.DTOs.UserDTOs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FirebaseService {

    @Autowired
    private UserService userService;

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User processFirebaseUser(FirebaseToken decodedToken) {
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();
        String picture = decodedToken.getPicture();
        String providerId = decodedToken.getUid();
        
        // Xác định provider dựa trên firebase provider
        Object firebaseObj = decodedToken.getClaims().get("firebase");
        AuthProvider authProvider = AuthProvider.GOOGLE; // Default
        
        if (firebaseObj != null) {
            Map<String, Object> firebaseData = (Map<String, Object>) firebaseObj;
            Object identitiesObj = firebaseData.get("identities");
            if (identitiesObj != null) {
                Map<String, Object> identities = (Map<String, Object>) identitiesObj;
                if (identities.containsKey("facebook.com")) {
                    authProvider = AuthProvider.FACEBOOK;
                } else if (identities.containsKey("google.com")) {
                    authProvider = AuthProvider.GOOGLE;
                }
            }
        }
        
        // Tách tên thành firstName và lastName
        String firstName = "";
        String lastName = "";
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }

        // Kiểm tra user đã tồn tại chưa
        var existingUsers = userService.findByUserNameAndAuthProvider(email, authProvider);
        if (!existingUsers.isEmpty()) {
            User existingUser = existingUsers.get(0);

            if (existingUser.getStatus() == Status.BAN) {
                throw new RuntimeException("User is banned");
            }
            if (picture != null && !picture.isEmpty()) {
                existingUser.setAvatarUrl(picture);
                return userService.saveUser(existingUser);
            }
            return existingUser;
        }

        // Tạo user mới
        UserDTOs userDTO = new UserDTOs();
        userDTO.setUserName(email);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setAuthProvider(authProvider);
        userDTO.setProviderId(providerId);
        userDTO.setVerified(true); // Firebase users are already verified
        userDTO.setPassWord(null); // No password for OAuth users

        User newUser = userService.addUser(userDTO);
        
        // Set avatar URL if available
        if (picture != null && !picture.isEmpty()) {
            newUser.setAvatarUrl(picture);
            return userService.saveUser(newUser);
        }
        
        return newUser;
    }
}