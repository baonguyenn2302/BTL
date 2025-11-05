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

public class TaiKhoanDAO {

    /**
     * SỬA: Thêm 'maTaiKhoan' vào constructor
     */
    public TaiKhoan getTaiKhoanByUsername(String username) {
        String sql = "SELECT * FROM TAIKHOAN WHERE tenDangNhap = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                    rs.getInt("maTaiKhoan"), // <<< SỬA
                    rs.getString("tenDangNhap"),
                    rs.getString("matKhau"),
                    rs.getString("tenNguoiDung"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("duongDanAnh"),
                    rs.getString("quyen")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return null;
    }

    /**
     * SỬA: Thêm 'maTaiKhoan' vào constructor
     */
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM TAIKHOAN ORDER BY tenNguoiDung";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(
                    rs.getInt("maTaiKhoan"), // <<< SỬA
                    rs.getString("tenDangNhap"),
                    rs.getString("matKhau"),
                    rs.getString("tenNguoiDung"),
                    rs.getString("email"),
                    rs.getString("sdt"),
                    rs.getString("duongDanAnh"),
                    rs.getString("quyen")
                );
                danhSach.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }

    /**
     * SỬA: Bỏ 'maTaiKhoan' (vì là IDENTITY)
     * Sửa lỗi N'?' (đã sửa ở bước trước)
     */
    public boolean insertTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TAIKHOAN (tenDangNhap, matKhau, tenNguoiDung, email, sdt, duongDanAnh, quyen) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)"; // <<< SỬA
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getTenNguoiDung());
            ps.setString(4, tk.getEmail());
            ps.setString(5, tk.getSdt());
            ps.setString(6, tk.getDuongDanAnh());
            ps.setString(7, tk.getQuyen());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

    /**
     * SỬA: Cho phép cập nhật 'tenDangNhap'
     * SỬA: WHERE bằng 'maTaiKhoan'
     */
    public boolean updateTaiKhoan(TaiKhoan tk) {
        String sql = "UPDATE TAIKHOAN SET tenDangNhap = ?, tenNguoiDung = ?, email = ?, sdt = ?, "
                   + "duongDanAnh = ?, quyen = ? "
                   + "WHERE maTaiKhoan = ?"; // <<< SỬA
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tk.getTenDangNhap()); // <<< SỬA
            ps.setString(2, tk.getTenNguoiDung());
            ps.setString(3, tk.getEmail());
            ps.setString(4, tk.getSdt());
            ps.setString(5, tk.getDuongDanAnh());
            ps.setString(6, tk.getQuyen());
            ps.setInt(7, tk.getMaTaiKhoan()); // <<< SỬA (Mệnh đề WHERE)
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

    /**
     * SỬA: Xóa bằng 'maTaiKhoan' (INT)
     */
    public boolean deleteTaiKhoan(int maTaiKhoan) { // <<< SỬA
        String sql = "DELETE FROM TAIKHOAN WHERE maTaiKhoan = ?"; // <<< SỬA
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maTaiKhoan); // <<< SỬA
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
}