package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) cho Bộ Sưu Tập.
 */
public class BoSuuTapDAO {

    /**
     * Helper: Ánh xạ ResultSet sang BoSuuTap
     */
    private BoSuuTap mapRowToBoSuuTap(ResultSet rs) throws SQLException {
        BoSuuTap bst = new BoSuuTap();
        bst.setMaBoSuuTap(rs.getInt("maBoSuuTap"));
        bst.setTenBoSuuTap(rs.getString("tenBoSuuTap"));
        bst.setMoTa(rs.getString("moTa"));
        bst.setDuongDanAnh(rs.getString("duongDanAnh")); // <<< ĐÃ THÊM
        return bst;
    }
    
    /**
     * Lấy tất cả các Bộ Sưu Tập (không kèm sách)
     */
    public List<BoSuuTap> getAllBoSuuTap() {
        List<BoSuuTap> ds = new ArrayList<>();
        String sql = "SELECT * FROM BOSUUTAP ORDER BY tenBoSuuTap";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRowToBoSuuTap(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy danh sách Sách (đã được rút gọn) thuộc một Bộ Sưu Tập
     * (Hàm này dùng SachDAO.mapRowToSach, nhưng SachDAO không có sẵn, 
     * nên chúng ta sẽ dùng một hàm mapRowToSach rút gọn tạm thời ở đây)
     */
    public List<Sach> getSachTrongBoSuuTap(int maBoSuuTap) {
        List<Sach> dsSach = new ArrayList<>();
        // Lấy thông tin cơ bản của sách và thông tin tác giả
        String sql = "SELECT s.*, t.maTacGia as maTacGia_join, t.tenTacGia as tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st_tacgia ON s.maSach = st_tacgia.maSach " +
                     "LEFT JOIN TACGIA t ON st_tacgia.maTacGia = t.maTacGia " +
                     "WHERE s.maSach IN (SELECT maSach FROM BOSUUTAP_SACH WHERE maBoSuuTap = ?) " +
                     "ORDER BY s.tenSach, t.tenTacGia";

        // Sử dụng lại logic join của SachDAO để nhóm tác giả
        // Vì chúng ta không thể gọi SachDAO.getSachInternal, 
        // chúng ta sẽ dùng một SachDAO tạm thời
        SachDAO sachDAO = new SachDAO(); 
        // Gọi hàm helper (giả định là public hoặc protected, nếu không được,
        // chúng ta phải lặp lại logic đó ở đây)
        // *** Giả định SachDAO.getSachInternal đã được điều chỉnh để có thể truy cập ***
        // Nếu getSachInternal là private, chúng ta phải dùng cách khác:
        
        // Cách đơn giản (nếu SachDAO không truy cập được):
        // Chúng ta sẽ tự làm lại logic join của SachDAO
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maBoSuuTap);
            List<Sach> sachList = new SachDAO().getSachInternal(sql, maBoSuuTap); // Dùng lại hàm helper của SachDAO
            return sachList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsSach;
    }


    /**
     * Thêm Bộ Sưu Tập mới
     */
    public boolean themBoSuuTap(BoSuuTap bst) {
        String sql = "INSERT INTO BOSUUTAP(tenBoSuuTap, moTa, duongDanAnh) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, bst.getTenBoSuuTap());
            pstmt.setString(2, bst.getMoTa());
            pstmt.setString(3, bst.getDuongDanAnh()); // <<< ĐÃ THÊM
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) return false;
            
            // Lấy mã (IDENTITY) vừa được tạo
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bst.setMaBoSuuTap(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sửa thông tin Bộ Sưu Tập
     */
    public boolean suaBoSuuTap(BoSuuTap bst) {
        String sql = "UPDATE BOSUUTAP SET tenBoSuuTap = ?, moTa = ?, duongDanAnh = ? WHERE maBoSuuTap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bst.getTenBoSuuTap());
            pstmt.setString(2, bst.getMoTa());
            pstmt.setString(3, bst.getDuongDanAnh()); // <<< ĐÃ THÊM
            pstmt.setInt(4, bst.getMaBoSuuTap()); // WHERE
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa Bộ Sưu Tập (CSDL sẽ tự xóa link trong BOSUUTAP_SACH)
     */
    public boolean xoaBoSuuTap(int maBoSuuTap) {
        String sql = "DELETE FROM BOSUUTAP WHERE maBoSuuTap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maBoSuuTap);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm một Sách vào một Bộ Sưu Tập
     */
    public boolean themSachVaoBoSuuTap(int maBoSuuTap, String maSach) {
        String sql = "INSERT INTO BOSUUTAP_SACH(maBoSuuTap, maSach) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maBoSuuTap);
            pstmt.setString(2, maSach);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi có thể xảy ra nếu link đã tồn tại (vi phạm Khóa chính tổ hợp)
            System.err.println("Lỗi thêm sách vào BST (có thể đã tồn tại): " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa một Sách khỏi một Bộ Sưu Tập
     */
    public boolean xoaSachKhoiBoSuuTap(int maBoSuuTap, String maSach) {
        String sql = "DELETE FROM BOSUUTAP_SACH WHERE maBoSuuTap = ? AND maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maBoSuuTap);
            pstmt.setString(2, maSach);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}