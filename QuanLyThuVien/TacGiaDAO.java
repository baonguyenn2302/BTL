package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) cho đối tượng Tác Giả.
 */
public class TacGiaDAO {

    /**
     * Helper: Ánh xạ một hàng ResultSet sang đối tượng TacGia
     */
    private TacGia mapRowToTacGia(ResultSet rs) throws SQLException {
        TacGia tg = new TacGia();
        tg.setMaTacGia(rs.getString("maTacGia"));
        tg.setTenTacGia(rs.getString("tenTacGia"));
        tg.setEmail(rs.getString("email"));
        tg.setSdt(rs.getString("sdt"));
        tg.setTrinhDoChuyenMon(rs.getString("trinhDoChuyenMon"));
        tg.setChucDanh(rs.getString("chucDanh"));
        return tg;
    }

    /**
     * Lấy toàn bộ danh sách tác giả
     */
    public List<TacGia> getAllTacGia() {
        List<TacGia> ds = new ArrayList<>();
        String sql = "SELECT * FROM TACGIA ORDER BY tenTacGia";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRowToTacGia(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy thông tin 1 tác giả bằng Mã
     */
    public TacGia getTacGiaByMaTacGia(String maTacGia) {
        String sql = "SELECT * FROM TACGIA WHERE maTacGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maTacGia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTacGia(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm tác giả mới
     */
    public boolean themTacGia(TacGia tg) {
        String sql = "INSERT INTO TACGIA(maTacGia, tenTacGia, email, sdt, trinhDoChuyenMon, chucDanh) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tg.getMaTacGia());
            pstmt.setString(2, tg.getTenTacGia());
            pstmt.setString(3, tg.getEmail());
            pstmt.setString(4, tg.getSdt());
            pstmt.setString(5, tg.getTrinhDoChuyenMon());
            pstmt.setString(6, tg.getChucDanh());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sửa thông tin tác giả
     */
    public boolean suaTacGia(TacGia tg) {
        String sql = "UPDATE TACGIA SET tenTacGia = ?, email = ?, sdt = ?, trinhDoChuyenMon = ?, chucDanh = ? " +
                     "WHERE maTacGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tg.getTenTacGia());
            pstmt.setString(2, tg.getEmail());
            pstmt.setString(3, tg.getSdt());
            pstmt.setString(4, tg.getTrinhDoChuyenMon());
            pstmt.setString(5, tg.getChucDanh());
            pstmt.setString(6, tg.getMaTacGia()); // WHERE
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tác giả
     */
    public boolean xoaTacGia(String maTacGia) {
        String sql = "DELETE FROM TACGIA WHERE maTacGia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maTacGia);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi này xảy ra nếu tác giả còn được liên kết với sách
            // (Chúng ta đã set ON DELETE CASCADE nên nó sẽ tự xóa link)
            System.err.println("Lỗi xóa tác giả: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm tác giả (theo Tên, Email, SĐT, Mã)
     */
    public List<TacGia> timKiemTacGia(String keyword) {
        List<TacGia> ds = new ArrayList<>();
        String sql = "SELECT * FROM TACGIA WHERE " +
                     "tenTacGia LIKE ? OR email LIKE ? OR sdt LIKE ? OR maTacGia LIKE ?";
        String kwLike = "%" + keyword + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, kwLike);
            pstmt.setString(2, kwLike);
            pstmt.setString(3, kwLike);
            pstmt.setString(4, kwLike);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRowToTacGia(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}