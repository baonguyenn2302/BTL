package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DocGiaDAO {

    /**
     * Helper: Ánh xạ một hàng ResultSet sang đối tượng DocGia
     */
    private DocGia mapRowToDocGia(ResultSet rs) throws SQLException {
        DocGia dg = new DocGia();
        dg.setMaDocGia(rs.getString("maDocGia"));
        dg.setHoTen(rs.getString("hoTen"));
        
        // Lấy ngày sinh (có thể là null)
        Timestamp ts = rs.getTimestamp("ngaySinh");
        if (ts != null) {
            dg.setNgaySinh(new Date(ts.getTime()));
        } else {
            dg.setNgaySinh(null);
        }
        
        dg.setEmail(rs.getString("email"));
        dg.setDiaChi(rs.getString("diaChi"));
        dg.setSdt(rs.getString("sdt"));
        dg.setBlocked(rs.getBoolean("blocked"));
        return dg;
    }

    /**
     * Lấy toàn bộ danh sách độc giả
     */
    public List<DocGia> getAllDocGia() {
        List<DocGia> ds = new ArrayList<>();
        String sql = "SELECT * FROM DOCGIA ORDER BY hoTen";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRowToDocGia(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy thông tin 1 độc giả bằng Mã
     */
    public DocGia getDocGiaByMaDocGia(String maDocGia) {
        String sql = "SELECT * FROM DOCGIA WHERE maDocGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maDocGia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDocGia(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm độc giả mới (Tự động gán blocked = 0)
     */
    public boolean themDocGia(DocGia dg) {
        String sql = "INSERT INTO DOCGIA(maDocGia, hoTen, ngaySinh, email, diaChi, sdt, blocked) " +
                     "VALUES(?, ?, ?, ?, ?, ?, 0)"; // Mặc định blocked = 0 (false)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dg.getMaDocGia());
            pstmt.setString(2, dg.getHoTen());
            
            if (dg.getNgaySinh() != null) {
                pstmt.setTimestamp(3, new Timestamp(dg.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setString(4, dg.getEmail());
            pstmt.setString(5, dg.getDiaChi());
            pstmt.setString(6, dg.getSdt());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Có thể thêm check lỗi trùng khóa chính (maDocGia)
            return false;
        }
    }

    /**
     * Sửa thông tin độc giả (Không sửa trạng thái blocked ở đây)
     */
    public boolean suaDocGia(DocGia dg) {
        String sql = "UPDATE DOCGIA SET hoTen = ?, ngaySinh = ?, email = ?, diaChi = ?, sdt = ? " +
                     "WHERE maDocGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dg.getHoTen());
            
            if (dg.getNgaySinh() != null) {
                pstmt.setTimestamp(2, new Timestamp(dg.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(2, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setString(3, dg.getEmail());
            pstmt.setString(4, dg.getDiaChi());
            pstmt.setString(5, dg.getSdt());
            pstmt.setString(6, dg.getMaDocGia()); // WHERE
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa độc giả
     */
    public boolean xoaDocGia(String maDocGia) {
        String sql = "DELETE FROM DOCGIA WHERE maDocGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maDocGia);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi này thường xảy ra nếu độc giả đang mượn sách (khóa ngoại)
            System.err.println("Lỗi xóa độc giả (FK constraint?): " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm độc giả (theo Tên, Email, SĐT, Mã ĐG)
     */
    public List<DocGia> timKiemDocGia(String keyword) {
        List<DocGia> ds = new ArrayList<>();
        String sql = "SELECT * FROM DOCGIA WHERE " +
                     "hoTen LIKE ? OR email LIKE ? OR sdt LIKE ? OR maDocGia LIKE ?";
        String kwLike = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, kwLike);
            pstmt.setString(2, kwLike);
            pstmt.setString(3, kwLike);
            pstmt.setString(4, kwLike);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRowToDocGia(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Cập nhật trạng thái Khóa / Mở khóa
     */
    public boolean khoaMoKhoaDocGia(String maDocGia, boolean block) {
        String sql = "UPDATE DOCGIA SET blocked = ? WHERE maDocGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, block);
            pstmt.setString(2, maDocGia);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}