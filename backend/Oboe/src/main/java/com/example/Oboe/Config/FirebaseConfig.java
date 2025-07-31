package com.example.Oboe.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                
                // Thử sử dụng service account key từ resources trước
                try {
                    ClassPathResource resource = new ClassPathResource("firebase/serviceAccountKey.json");
                    if (resource.exists()) {
                        InputStream serviceAccount = resource.getInputStream();
                        FirebaseOptions options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .build();
                        FirebaseApp.initializeApp(options);
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Service account key not found in resources: " + e.getMessage());
                }
                
                // Fallback: Sử dụng default credentials từ environment
                try {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
                    FirebaseApp.initializeApp(options);
                    return;
                } catch (IOException e) {
                    System.out.println("Default credentials not available: " + e.getMessage());
                }
                
                // Final fallback: Khởi tạo với project ID only (cho development)
                FirebaseOptions options = FirebaseOptions.builder()
                        .setProjectId("oboe-28622") // Project ID từ Firebase config
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Không thể khởi tạo Firebase: " + e.getMessage());
            e.printStackTrace();
            // Trong trường hợp này, Firebase authentication sẽ không hoạt động
        }
    }
}