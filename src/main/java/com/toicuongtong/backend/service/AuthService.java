package com.toicuongtong.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toicuongtong.backend.dto.AuthResponse;
import com.toicuongtong.backend.dto.LoginRequest; // Thêm import này
import com.toicuongtong.backend.dto.RegisterRequest;
import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.repository.PlayerRepository;
import com.toicuongtong.backend.repository.UserRepository;
import com.toicuongtong.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // Thêm công cụ tạo token

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        log.info("Đang mã hóa mật khẩu cho user '{}'. Hash: {}", request.email(), encodedPassword);
        User user = new User();
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setPasswordHash(encodedPassword);
        User savedUser = userRepository.save(user);
        
        // KHÔNG tạo Player ngay khi đăng ký
        // Player sẽ được tạo sau khi user chọn avatar và công pháp trong trang create-character
        log.info("User {} đã đăng ký thành công. Player sẽ được tạo sau khi chọn avatar và công pháp.", request.email());
    }

    // HÀM LOGIN ĐẦY ĐỦ
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Sau khi xác thực thành công, Spring Security sẽ cung cấp cho chúng ta đối tượng User
        User user = (User) authentication.getPrincipal();

        // Tạo token từ thông tin user
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getId(), user.getDisplayName());
    }
}