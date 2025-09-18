// File: src/main/java/com/toicuongtong/backend/repository/UserRepository.java
package com.toicuongtong.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toicuongtong.backend.model.User;

// --- GIẢI THÍCH ---

// "interface" là một bản hợp đồng, nó định nghĩa các hành động có thể làm
// mà không cần biết chi tiết cách làm.
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long>: Đây là phần "phép màu".
    // - User: Chúng ta báo cho Spring biết Repository này làm việc với đối tượng "User".
    // - Long: Chúng ta báo cho Spring biết kiểu dữ liệu của khóa chính (@Id) trong User là "Long".
    //
    // Chỉ cần extends JpaRepository, Spring sẽ tự động cung cấp cho chúng ta các hàm
    // cơ bản như: save(), findById(), findAll(), delete(), count()...

    // --- CÁC HÀM TÌM KIẾM TÙY CHỈNH ---
    // Spring Data JPA rất thông minh. Nếu chúng ta định nghĩa một hàm theo một quy tắc đặt tên
    // nhất định, nó sẽ tự hiểu và tạo ra câu lệnh SQL tương ứng.

    /**
     * Tìm kiếm một User dựa trên email.
     * Tên hàm "findByEmail" sẽ được Spring tự động dịch thành câu lệnh SQL:
     * "SELECT * FROM users WHERE email = ?"
     * Optional<User>: Đây là một cách xử lý an toàn trong Java. Thay vì trả về null
     * nếu không tìm thấy, nó sẽ trả về một "hộp rỗng", giúp tránh lỗi.
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra xem một email đã tồn tại trong database hay chưa.
     * Tên hàm "existsByEmail" sẽ được dịch thành câu lệnh SQL hiệu quả hơn:
     * "SELECT count(*) FROM users WHERE email = ?"
     */
    Boolean existsByEmail(String email);
}