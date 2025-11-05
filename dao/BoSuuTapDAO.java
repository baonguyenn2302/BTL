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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BoSuuTapDAO {

    /**
     * 1. Lấy tất cả Bộ Sưu Tập (Đã xóa moTa)
     */
    public List<BoSuuTap> getAllBoSuuTap() {
        List<BoSuuTap> danhSach = new ArrayList<>();
        String sql = "SELECT maBoSuuTap, tenBoSuuTap, duongDanAnh FROM BOSUUTAP ORDER BY tenBoSuuTap";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                BoSuuTap bst = new BoSuuTap(
                    rs.getInt("maBoSuuTap"),
                    rs.getString("tenBoSuuTap"),
                    // rs.getString("moTa"), // <<< ĐÃ XÓA
                    rs.getString("duongDanAnh")
                );
                danhSach.add(bst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }

    /**
     * 2. Lấy sách (Giữ nguyên, không ảnh hưởng)
     */
    public List<Sach> getSachByBoSuuTap(int maBoSuuTap) {
        Map<String, Sach> sachMap = new LinkedHashMap<>();
        String sql = "SELECT s.*, tg.maTacGia, tg.tenTacGia "
                   + "FROM BOSUUTAP_SACH bs "
                   + "JOIN SACH s ON bs.maSach = s.maSach "
                   + "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach "
                   + "LEFT JOIN TACGIA tg ON st.maTacGia = tg.maTacGia "
                   + "WHERE bs.maBoSuuTap = ? AND s.isArchived = 0 "
                   + "ORDER BY s.tenSach";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maBoSuuTap);
            rs = ps.executeQuery();
            while (rs.next()) {
                String maSach = rs.getString("maSach");
                Sach sach = sachMap.get(maSach);
                if (sach == null) {
                    sach = new Sach();
                    sach.setMaSach(maSach);
                    sach.setTenSach(rs.getString("tenSach"));
                    sach.setNhaXuatBan(rs.getString("nhaXuatBan"));
                    sach.setNamXuatBan(rs.getInt("namXuatBan"));
                    sach.setSoLuong(rs.getInt("soLuong"));
                    // (moTa đã bị xóa)
                    sach.setDuongDanAnh(rs.getString("duongDanAnh")); // <<< DÒNG QUAN TRỌNG NHẤT
                    sach.setNgayThem(rs.getTimestamp("ngayThem"));
                    sach.setViTri(rs.getString("viTri"));
                    sach.setConLai(rs.getInt("conLai"));
                    sach.setArchived(rs.getBoolean("isArchived"));
                    sachMap.put(maSach, sach);
                }
                String maTacGia = rs.getString("maTacGia");
                if (maTacGia != null) {
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(maTacGia);
                    tg.setTenTacGia(rs.getString("tenTacGia"));
                    sach.getDanhSachTacGia().add(tg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return new ArrayList<>(sachMap.values());
    }

    /**
     * 3. Thêm một Bộ Sưu Tập mới (Đã xóa moTa)
     */
    public boolean insertBoSuuTap(BoSuuTap bst) {
        String sql = "INSERT INTO BOSUUTAP (tenBoSuuTap, duongDanAnh) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bst.getTenBoSuuTap());
            // ps.setString(2, bst.getMoTa()); // <<< ĐÃ XÓA
            ps.setString(2, bst.getDuongDanAnh()); // <<< Sửa chỉ số 3 -> 2
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

    /**
     * 4. Cập nhật thông tin một Bộ Sưu Tập (Đã xóa moTa)
     */
    public boolean updateBoSuuTap(BoSuuTap bst) {
        String sql = "UPDATE BOSUUTAP SET tenBoSuuTap = ?, duongDanAnh = ? "
                   + "WHERE maBoSuuTap = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, bst.getTenBoSuuTap());
            // ps.setString(2, bst.getMoTa()); // <<< ĐÃ XÓA
            ps.setString(2, bst.getDuongDanAnh()); // <<< Sửa chỉ số 3 -> 2
            ps.setInt(3, bst.getMaBoSuuTap());     // <<< Sửa chỉ số 4 -> 3
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }

    /**
     * 5. Xóa một Bộ Sưu Tập (Giữ nguyên, không ảnh hưởng)
     */
    public boolean deleteBoSuuTap(int maBoSuuTap) {
        String sql = "DELETE FROM BOSUUTAP WHERE maBoSuuTap = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maBoSuuTap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    // (Các hàm 6, 7 - Thêm/Xóa Sách - Giữ nguyên, không ảnh hưởng)
    public boolean addSachToBoSuuTap(int maBoSuuTap, String maSach) {
        String sql = "INSERT INTO BOSUUTAP_SACH (maBoSuuTap, maSach) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maBoSuuTap);
            ps.setString(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    public boolean removeSachFromBoSuuTap(int maBoSuuTap, String maSach) {
        String sql = "DELETE FROM BOSUUTAP_SACH WHERE maBoSuuTap = ? AND maSach = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maBoSuuTap);
            ps.setString(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    // (Hàm 8 - Tìm kiếm - Đã xóa moTa)
    public List<BoSuuTap> searchBoSuuTapByName(String tenBST) {
        List<BoSuuTap> danhSach = new ArrayList<>();
        String sql = "SELECT maBoSuuTap, tenBoSuuTap, duongDanAnh FROM BOSUUTAP WHERE tenBoSuuTap LIKE ? ORDER BY tenBoSuuTap";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + tenBST + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                BoSuuTap bst = new BoSuuTap(
                    rs.getInt("maBoSuuTap"),
                    rs.getString("tenBoSuuTap"),
                    // rs.getString("moTa"), // <<< ĐÃ XÓA
                    rs.getString("duongDanAnh")
                );
                danhSach.add(bst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
}