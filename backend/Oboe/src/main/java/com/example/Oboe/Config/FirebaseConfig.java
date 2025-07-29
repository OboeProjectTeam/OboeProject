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
                // Sử dụng default credentials từ environment hoặc service account key
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            // Fallback: Khởi tạo Firebase mà không cần credentials cho development
            // Trong production, bạn nên cung cấp service account key
            try {
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setProjectId("oboe-28622") // Project ID từ Firebase config
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            } catch (Exception ex) {
                System.err.println("Không thể khởi tạo Firebase: " + ex.getMessage());
                // Trong trường hợp này, Firebase authentication sẽ không hoạt động
            }
        }
    }
}