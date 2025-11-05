package dao;

import model.Sach;
import model.DocGia;
import model.TacGia;
import model.MuonTra;
import model.BoSuuTap;
import model.TaiKhoan;

import dao.SachDAO;
import dao.DocGiaDAO;
import dao.TacGiaDAO;
import dao.MuonTraDAO;
import dao.BoSuuTapDAO;
import dao.TaiKhoanDAO;

import ui.AddSachToBSTDialog;
import ui.BoSuuTapEditDialog;
import ui.ChiTietDocGiaDialog;
import ui.ChiTietMuonTraDialog;
import ui.ChiTietSachDialog;
import ui.DocGiaEditDialog;
import ui.LoginFrame;
import ui.MainFrame;
import ui.SachEditDialog;
import ui.TacGiaEditDialog;
import ui.TaiKhoanEditDialog;
import ui.XacNhanMuonDialog;

import util.DatabaseConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class TacGiaDAO {

    // 1. Tìm tác giả theo TÊN
    public TacGia findTacGiaByTen(String tenTacGia) {
        String sql = "SELECT * FROM TACGIA WHERE tenTacGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tenTacGia);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new TacGia(
                    rs.getString("maTacGia"),
                    rs.getString("tenTacGia"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("chucDanh")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return null; // Không tìm thấy
    }
    public List<TacGia> getAllTacGia() {
        List<TacGia> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM TACGIA ORDER BY maTacGia";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TacGia tg = new TacGia(
                    rs.getString("maTacGia"),
                    rs.getString("tenTacGia"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("chucDanh")
                );
                danhSach.add(tg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach; // Trả về danh sách (có thể rỗng)
    }
    public boolean insertTacGia(TacGia tacGia) {
        // Mã tác giả đã được tạo và gán từ form
        String sql = "INSERT INTO TACGIA (maTacGia, tenTacGia, email, sdt, chucDanh) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tacGia.getMaTacGia());
            ps.setString(2, tacGia.getTenTacGia());
            ps.setString(3, tacGia.getEmail());
            ps.setString(4, tacGia.getSdt());
            ps.setString(5, tacGia.getChucDanh());
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); // Trả về true nếu thêm thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Thêm thất bại
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

    // 2. Thêm một tác giả mới (private vì nó được gọi bởi findOrCreate)
    private TacGia addTacGia(String tenTacGia) {
        String newMaTacGia = generateNewMaTacGia();
        String sql = "INSERT INTO TACGIA (maTacGia, tenTacGia) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newMaTacGia);
            ps.setString(2, tenTacGia);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Trả về đối tượng Tác giả vừa được tạo
                return new TacGia(newMaTacGia, tenTacGia, null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
        return null; // Thêm thất bại
    }

    // 3. Hàm tạo Mã Tác Giả mới (ví dụ: TG001 -> TG002)
    public String generateNewMaTacGia() {
        String sql = "SELECT TOP 1 maTacGia FROM TACGIA ORDER BY maTacGia DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String newId = "TG001"; // ID mặc định nếu bảng trống

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("maTacGia");
                // Lấy phần số từ ID (ví dụ: TG005 -> 5)
                String trimmedLastId = lastId.trim();
                int nextIdNum = Integer.parseInt(lastId.substring(2)) + 1;
                // Format lại (ví dụ: 6 -> TG006)
                newId = String.format("TG%03d", nextIdNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return newId;
    }

    /**
     * HÀM QUAN TRỌNG: Tìm Tác Giả theo tên, nếu không có, tạo mới và trả về.
     */
    public TacGia findOrCreateTacGia(String tenTacGia) {
        // 1. Thử tìm
        TacGia tacGia = findTacGiaByTen(tenTacGia);
        
        // 2. Nếu tìm thấy, trả về ngay
        if (tacGia != null) {
            return tacGia;
        }
        
        // 3. Nếu không tìm thấy, tạo mới và trả về
        return addTacGia(tenTacGia);
    }

    
    // 7. Lấy một Tác Giả theo Mã
    public TacGia getTacGiaByMa(String maTacGia) {
        String sql = "SELECT * FROM TACGIA WHERE maTacGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maTacGia);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new TacGia(
                    rs.getString("maTacGia"),
                    rs.getString("tenTacGia"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("chucDanh")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return null; // Không tìm thấy
    }

    // 8. Cập nhật thông tin Tác Giả
    public boolean updateTacGia(TacGia tacGia) {
        String sql = "UPDATE TACGIA SET tenTacGia = ?, email = ?, sdt = ?, chucDanh = ? WHERE maTacGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tacGia.getTenTacGia());
            ps.setString(2, tacGia.getEmail());
            ps.setString(3, tacGia.getSdt());
            ps.setString(4, tacGia.getChucDanh());
            ps.setString(5, tacGia.getMaTacGia()); // Điều kiện WHERE
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); // Trả về true nếu cập nhật thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Cập nhật thất bại
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    public boolean deleteTacGia(String maTacGia) {
        String sql = "DELETE FROM TACGIA WHERE maTacGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maTacGia);
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); // Trả về true nếu xóa thành công

        } catch (SQLException e) {
            // Lỗi có thể xảy ra nếu tác giả vẫn còn liên kết
            // (mặc dù chúng ta đã set ON DELETE CASCADE)
            e.printStackTrace();
            return false; // Xóa thất bại
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

}