package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// Import lớp User từ package của bạn
import QuanLyThuVien.User; 

public class UserDAO {

    /**
     * Phương thức quan trọng nhất: Kiểm tra đăng nhập
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau Mật khẩu (dạng plain text)
     * @return Đối tượng User nếu thành công, null nếu thất bại
     */
    public User checkLogin(String tenDangNhap, String matKhau) {
        // LƯU Ý BẢO MẬT: Đây là cách làm không an toàn (so sánh plain text).
        // Cách làm đúng là hash mật khẩu.
        String sql = "SELECT * FROM NGUOIDUNG WHERE tenDangNhap = ? AND matKhau = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, matKhau);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Đăng nhập thành công, trả về đối tượng User
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Đăng nhập thất bại
        return null;
    }

    // ===================================================================
    // === PHƯƠNG THỨC MỚI: KIỂM TRA MẬT KHẨU CŨ (ĐỂ KHÔNG BỊ LỖI) ===
    // ===================================================================
    /**
     * Kiểm tra mật khẩu cũ có khớp với mật khẩu trong DB không.
     * @param tenDangNhap Tên đăng nhập
     * @param oldPassword Mật khẩu cũ (plain text)
     * @return true nếu mật khẩu cũ khớp, false nếu không khớp hoặc lỗi.
     */
    public boolean kiemTraMatKhauCu(String tenDangNhap, String oldPassword) {
        String sql = "SELECT tenDangNhap FROM NGUOIDUNG WHERE tenDangNhap = ? AND matKhau = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, oldPassword);
            
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next(); // Nếu tìm thấy một hàng, mật khẩu cũ là đúng
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ===================================================================
    // === PHƯƠNG THỨC MỚI: ĐỔI MẬT KHẨU (ĐỂ KHÔNG BỊ LỖI) ===
    // ===================================================================
    /**
     * Cập nhật mật khẩu mới cho người dùng.
     * @param tenDangNhap Tên đăng nhập
     * @param newPassword Mật khẩu mới (plain text)
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean doiMatKhau(String tenDangNhap, String newPassword) {
        String sql = "UPDATE NGUOIDUNG SET matKhau = ? WHERE tenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword); 
            pstmt.setString(2, tenDangNhap); 

            return pstmt.executeUpdate() > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Thêm một tài khoản người dùng mới
     */
    public boolean themUser(User user) {
        // Nên mã hóa user.getMatKhau() trước khi lưu
        String sql = "INSERT INTO NGUOIDUNG(tenDangNhap, matKhau, vaiTro, maLienKet) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getTenDangNhap());
            pstmt.setString(2, user.getMatKhau()); // LƯU Ý BẢO MẬT
            pstmt.setString(3, user.getVaiTro());
            pstmt.setString(4, user.getMaLienKet());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin tài khoản (thường là đổi mật khẩu hoặc vai trò)
     */
    public boolean suaUser(User user) {
        String sql = "UPDATE NGUOIDUNG SET matKhau = ?, vaiTro = ?, maLienKet = ? WHERE tenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getMatKhau()); // LƯU Ý BẢO MẬT
            pstmt.setString(2, user.getVaiTro());
            pstmt.setString(3, user.getMaLienKet());
            pstmt.setString(4, user.getTenDangNhap()); // Mệnh đề WHERE

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một tài khoản người dùng
     */
    public boolean xoaUser(String tenDangNhap) {
        String sql = "DELETE FROM NGUOIDUNG WHERE tenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy thông tin chi tiết của một User theo tên đăng nhập
     */
    public User getUserByUsername(String tenDangNhap) {
        String sql = "SELECT * FROM NGUOIDUNG WHERE tenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy tất cả tài khoản
     */
    public List<User> getAllUsers() {
        List<User> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NGUOIDUNG";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                danhSach.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Hàm tiện ích: Map 1 hàng CSDL (ResultSet) sang 1 đối tượng User
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setTenDangNhap(rs.getString("tenDangNhap"));
        user.setMatKhau(rs.getString("matKhau")); // Lấy cả mật khẩu (đã hash)
        user.setVaiTro(rs.getString("vaiTro"));
        user.setMaLienKet(rs.getString("maLienKet"));
        return user;
    }
}
