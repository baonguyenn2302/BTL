package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TacGiaDAO {

    /**
     * Lấy thông tin chi tiết của một tác giả dựa trên mã (maTacGia).
     * @param maTacGia Mã tác giả (trong trường hợp này là tên tác giả).
     * @return Đối tượng TacGia nếu tìm thấy, null nếu không.
     */
    public TacGia getTacGiaByMa(String maTacGia) {
        String sql = "SELECT * FROM TACGIA WHERE maTacGia = ?";
        TacGia tacGia = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTacGia);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tacGia = mapRowToTacGia(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin tác giả: " + e.getMessage());
            e.printStackTrace();
        }
        return tacGia;
    }

    /**
     * Cập nhật thông tin chi tiết cho một tác giả (ngoại trừ maTacGia và tenTacGia).
     * @param tacGia Đối tượng TacGia chứa thông tin mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateTacGiaDetails(TacGia tacGia) {
        // Cập nhật các trường thông tin chi tiết, không cập nhật khóa chính (maTacGia)
        // tenTacGia cũng thường không đổi nếu dùng làm mã, nhưng có thể cập nhật nếu cần
        String sql = "UPDATE TACGIA SET tenTacGia = ?, email = ?, sdt = ?, trinhDoChuyenMon = ?, chucDanh = ? " +
                     "WHERE maTacGia = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tacGia.getTenTacGia()); // Cập nhật tên nếu cần
            pstmt.setString(2, tacGia.getEmail());
            pstmt.setString(3, tacGia.getSdt());
            pstmt.setString(4, tacGia.getTrinhDoChuyenMon());
            pstmt.setString(5, tacGia.getChucDanh());
            pstmt.setString(6, tacGia.getMaTacGia()); // Điều kiện WHERE

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thông tin tác giả: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả tác giả (có thể dùng cho mục đích quản trị).
     * @return List các đối tượng TacGia.
     */
    public List<TacGia> getAllTacGia() {
        List<TacGia> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM TACGIA ORDER BY tenTacGia"; // Sắp xếp theo tên

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapRowToTacGia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tác giả: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }


    /**
     * Xóa một tác giả (Cẩn thận: Cần xử lý sách liên quan dựa trên ràng buộc FK).
     * @param maTacGia Mã tác giả cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
     public boolean xoaTacGia(String maTacGia) {
         // Lưu ý: Ràng buộc FK 'ON DELETE SET NULL' sẽ tự động cập nhật bảng SACH.
         // Nếu ràng buộc là 'NO ACTION', lệnh DELETE này sẽ thất bại nếu tác giả còn sách.
         String sql = "DELETE FROM TACGIA WHERE maTacGia = ?";
         try (Connection conn = DatabaseConnection.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, maTacGia);
             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             System.err.println("Lỗi khi xóa tác giả: " + e.getMessage());
             // In ra thông báo cụ thể nếu lỗi do khóa ngoại
             if (e.getMessage().contains("The DELETE statement conflicted with the REFERENCE constraint")) {
                 System.err.println("Không thể xóa tác giả vì vẫn còn sách liên kết. Hãy xóa hoặc gán lại sách cho tác giả khác trước.");
             }
             e.printStackTrace();
             return false;
         }
     }

    /**
     * Hàm tiện ích: Map một hàng từ ResultSet thành đối tượng TacGia.
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
}

