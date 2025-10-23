// Đảm bảo file này cũng nằm trong package QuanLyThuVien
// hoặc import đúng lớp DatabaseConnection
package QuanLyThuVien; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import QuanLyThuVien.DocGia; 

public class DocGiaDAO {
    public boolean themDocGia(DocGia docGia) {
        // SQL khớp với 7 thuộc tính (bao gồm diaChi)
        String sql = "INSERT INTO DOCGIA(maDocGia, hoTen, ngaySinh, diaChi, email, sdt, isBlocked) VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection(); // Giả sử lớp này tồn tại
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, docGia.getMaDocGia());
            pstmt.setString(2, docGia.getHoTen());
            
            if (docGia.getNgaySinh() != null) {
                pstmt.setDate(3, new java.sql.Date(docGia.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }
            
            pstmt.setString(4, docGia.getDiaChi()); // Đã thêm diaChi
            pstmt.setString(5, docGia.getEmail());
            pstmt.setString(6, docGia.getSdt());
            pstmt.setBoolean(7, docGia.isBlocked()); // Khớp với thuộc tính 'blocked'

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean suaDocGia(DocGia docGia) {
        String sql = "UPDATE DOCGIA SET hoTen = ?, ngaySinh = ?, diaChi = ?, email = ?, sdt = ?, isBlocked = ? WHERE maDocGia = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, docGia.getHoTen());
            
            if (docGia.getNgaySinh() != null) {
                pstmt.setDate(2, new java.sql.Date(docGia.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.DATE);
            }
            
            pstmt.setString(3, docGia.getDiaChi()); // Đã thêm diaChi
            pstmt.setString(4, docGia.getEmail());
            pstmt.setString(5, docGia.getSdt());
            pstmt.setBoolean(6, docGia.isBlocked());
            pstmt.setString(7, docGia.getMaDocGia()); // Mệnh đề WHERE

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một độc giả
     */
    public boolean xoaDocGia(String maDocGia) {
        String sql = "DELETE FROM DOCGIA WHERE maDocGia = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maDocGia);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm độc giả (Ví dụ: theo Tên hoặc Email)
     */
    public List<DocGia> timKiemDocGia(String keyword) {
        List<DocGia> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DOCGIA WHERE hoTen LIKE ? OR email LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(mapRowToDocGia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Khóa hoặc Mở khóa tài khoản độc giả
     * @param maDocGia Mã độc giả
     * @param blockStatus Trạng thái muốn set: true (Khóa), false (Mở)
     */
    public boolean khoaMoKhoaDocGia(String maDocGia, boolean blockStatus) {
        String sql = "UPDATE DOCGIA SET isBlocked = ? WHERE maDocGia = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, blockStatus); 
            pstmt.setString(2, maDocGia);
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy tất cả độc giả
     */
    public List<DocGia> getAllDocGia() {
        List<DocGia> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DOCGIA";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                danhSach.add(mapRowToDocGia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }


    /**
     * Hàm tiện ích: Map 1 hàng CSDL (ResultSet) sang 1 đối tượng DocGia
     */
    private DocGia mapRowToDocGia(ResultSet rs) throws SQLException {
        DocGia dg = new DocGia(); // Dùng constructor rỗng
        
        // Dùng setters để gán giá trị
        dg.setMaDocGia(rs.getString("maDocGia"));
        dg.setHoTen(rs.getString("hoTen"));
        dg.setNgaySinh(rs.getDate("ngaySinh"));
        dg.setDiaChi(rs.getString("diaChi")); // Đã thêm diaChi
        dg.setEmail(rs.getString("email"));
        dg.setSdt(rs.getString("sdt"));
        dg.setBlocked(rs.getBoolean("isBlocked")); // Khớp với tên cột 'isBlocked'
        
        return dg;
    }
    
    /**
     * Lấy thông tin chi tiết của 1 độc giả bằng Mã
     */
    public DocGia getDocGiaByMaDocGia(String maDocGia) {
        String sql = "SELECT * FROM DOCGIA WHERE maDocGia = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maDocGia);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Sử dụng hàm mapRowToDocGia đã có
                return mapRowToDocGia(rs); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy
    }
}