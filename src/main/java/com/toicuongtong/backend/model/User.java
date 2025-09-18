package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", schema = "tct")
@Data
@NoArgsConstructor
// Thêm "implements UserDetails" để tuân thủ "khuôn mẫu" của Spring Security
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // Giữ lại annotation này
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.PLAYER;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public enum UserRole {
        ADMIN, PLAYER
    }

    // --- CÁC HÀM BẮT BUỘC CỦA USERDETAILS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về quyền (role) của người dùng
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        // Trả về mật khẩu đã được mã hóa
        return passwordHash;
    }

    @Override
    public String getUsername() {
        // Spring Security dùng "username" để định danh, chúng ta sẽ dùng email
        return email;
    }

    // Các hàm sau đây dùng để kiểm tra trạng thái tài khoản.
    // Hiện tại chúng ta mặc định là true (tài khoản luôn hợp lệ).
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}