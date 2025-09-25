// File: src/main/java/com/toicuongtong/backend/config/DataInitializer.java
package com.toicuongtong.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Component: Đánh dấu đây là một "linh kiện" của Spring, để Spring quản lý nó.
@Component
@ConditionalOnProperty(name = "app.data.initializer.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j // Lombok: Tự động tạo một logger để chúng ta có thể in thông báo ra console.
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // CommandLineRunner là một giao diện đặc biệt của Spring Boot.
    // Bất kỳ code nào trong hàm run() sẽ được thực thi một lần
    // ngay sau khi ứng dụng đã khởi động xong.
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@tct.local";

        // Chỉ tạo Admin nếu email đó chưa tồn tại
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setDisplayName("Admin");
            // QUAN TRỌNG: Đặt vai trò là ADMIN
            adminUser.setRole(User.UserRole.ADMIN);
            // Đặt một mật khẩu mặc định mạnh và an toàn
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));

            userRepository.save(adminUser);

            // In ra console để chúng ta biết tài khoản đã được tạo
            log.info("Tài khoản Admin mặc định đã được tạo: {}", adminEmail);
        } else {
            log.info("Tài khoản Admin đã tồn tại, không cần tạo mới.");
        }
    }
}