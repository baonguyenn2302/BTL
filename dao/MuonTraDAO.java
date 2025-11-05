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


import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MuonTraDAO {
    
    // --- (Hàm getAllMuonTra - ĐÃ SỬA LỖI LOGIC 'QUÁ HẠN') ---
    public List<MuonTra> getAllMuonTra() {
        List<MuonTra> danhSach = new ArrayList<>();
        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "ORDER BY mt.ngayMuon DESC"; 

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                DocGia dg = new DocGia();
                dg.setMaDocGia(rs.getString("maDocGia"));
                dg.setHoTen(rs.getString("hoTen"));
                
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                // <<< SỬA LOGIC: KHÔNG setTrangThai TỪ DB >>>
                
                mt.setDocGia(dg);
                mt.setSach(s);
                
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach; 
    }

    // --- (Hàm themMoiPhieuMuon - Đã sửa lỗi font) ---
    public boolean themMoiPhieuMuon(TaiKhoan admin, DocGia docGia, List<Sach> danhSachSach, Date ngayMuon, Date ngayHenTra, String loaiMuon) {
        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        // <<< SỬA SQL >>>
        String sqlInsert = "INSERT INTO MUONTRA (maDocGia, maSach, ngayMuon, ngayHenTra, loaiMuon, trangThai, maNguoiTao) "
                         + "VALUES (?, ?, ?, ?, ?, N'Đang mượn', ?)"; // Thêm maNguoiTao
        
        String sqlUpdate = "UPDATE SACH SET conLai = conLai - 1 WHERE maSach = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            psInsert = conn.prepareStatement(sqlInsert);
            psUpdate = conn.prepareStatement(sqlUpdate);

            for (Sach s : danhSachSach) {
                psInsert.setString(1, docGia.getMaDocGia());
                psInsert.setString(2, s.getMaSach());
                psInsert.setTimestamp(3, new java.sql.Timestamp(ngayMuon.getTime()));
                psInsert.setTimestamp(4, new java.sql.Timestamp(ngayHenTra.getTime()));
                psInsert.setString(5, loaiMuon);
                psInsert.setInt(6, admin.getMaTaiKhoan()); // <<< THÊM tham số 6
                psInsert.addBatch(); 
                
                psUpdate.setString(1, s.getMaSach());
                psUpdate.addBatch(); 
            }
            psInsert.executeBatch();
            psUpdate.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException e2) { e2.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
            if (psUpdate != null) try { psUpdate.close(); } catch (SQLException e) { /* Bỏ qua */ }
            DatabaseConnection.closeResource(psInsert, conn);
        }
    }
    
    // --- (Hàm danhDauDaTra - Giữ nguyên) ---
    public boolean danhDauDaTra(int maMuonTra, String maSach) {
        Connection conn = null;
        PreparedStatement psUpdateMuonTra = null;
        PreparedStatement psUpdateSach = null;
        String sqlUpdateMuonTra = "UPDATE MUONTRA SET ngayTraThucTe = GETDATE(), trangThai = N'Đã trả' "
                               + "WHERE maMuonTra = ?";
        String sqlUpdateSach = "UPDATE SACH SET conLai = conLai + 1 WHERE maSach = ?";
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            psUpdateMuonTra = conn.prepareStatement(sqlUpdateMuonTra);
            psUpdateMuonTra.setInt(1, maMuonTra);
            psUpdateMuonTra.executeUpdate();
            psUpdateSach = conn.prepareStatement(sqlUpdateSach);
            psUpdateSach.setString(1, maSach);
            psUpdateSach.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException e2) { e2.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
            if (psUpdateMuonTra != null) try { psUpdateMuonTra.close(); } catch (SQLException e) { /* Bỏ qua */ }
            DatabaseConnection.closeResource(psUpdateSach, conn);
        }
    }
    
    // --- (Hàm giaHanPhieuMuon - Giữ nguyên) ---
    public boolean giaHanPhieuMuon(int maMuonTra, Date ngayHenTraMoi) {
        String sql = "UPDATE MUONTRA SET ngayHenTra = ?, trangThai = N'Đang mượn' "
                   + "WHERE maMuonTra = ? AND ngayTraThucTe IS NULL";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new java.sql.Timestamp(ngayHenTraMoi.getTime()));
            ps.setInt(2, maMuonTra);
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    // --- (Hàm getChiTietMuonTra - Giữ nguyên) ---
    public MuonTra getChiTietMuonTra(int maMuonTra) {
        MuonTra mt = null;
        TaiKhoan nguoiTao = null;
        String sql = "SELECT mt.*, dg.hoTen, dg.email AS dg_email, dg.sdt AS dg_sdt, " 
                   + "s.tenSach, s.nhaXuatBan, s.namXuatBan, s.viTri, tg.maTacGia, tg.tenTacGia, " 
                   + "tk.maTaiKhoan, tk.tenNguoiDung AS tenNguoiTao "
                   + "FROM MUONTRA mt "
                   + "LEFT JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "LEFT JOIN SACH s ON mt.maSach = s.maSach "
                   + "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach "
                   + "LEFT JOIN TACGIA tg ON st.maTacGia = tg.maTacGia "
                   + "LEFT JOIN TAIKHOAN tk ON mt.maNguoiTao = tk.maTaiKhoan "
                   + "WHERE mt.maMuonTra = ?"; 
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maMuonTra);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (mt == null) {
                    DocGia dg = new DocGia();
                    dg.setMaDocGia(rs.getString("maDocGia"));
                    dg.setHoTen(rs.getString("hoTen"));
                    dg.setEmail(rs.getString("dg_email"));
                    dg.setSdt(rs.getString("dg_sdt"));
                    
                    Sach s = new Sach();
                    s.setMaSach(rs.getString("maSach"));
                    s.setTenSach(rs.getString("tenSach"));
                    s.setNhaXuatBan(rs.getString("nhaXuatBan"));
                    s.setNamXuatBan(rs.getInt("namXuatBan"));
                    s.setViTri(rs.getString("viTri"));
                    
                    nguoiTao = new TaiKhoan(0, null, null, null, null, null, null, null);
                    nguoiTao.setMaTaiKhoan(rs.getInt("maTaiKhoan"));
                    String tenNguoiTao = rs.getString("tenNguoiTao");
                    if (rs.wasNull()) {
                        nguoiTao.setTenNguoiDung("N/A (Tài khoản đã xóa)");
                    } else {
                        nguoiTao.setTenNguoiDung(tenNguoiTao);
                    }
                    
                    mt = new MuonTra();
                    mt.setMaMuonTra(rs.getInt("maMuonTra"));
                    mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                    mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                    mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                    mt.setLoaiMuon(rs.getString("loaiMuon"));
                    
                    mt.setDocGia(dg);
                    mt.setSach(s);
                    mt.setNguoiTao(nguoiTao);
                }
                String maTacGia = rs.getString("maTacGia");
                if (maTacGia != null && mt != null) {
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(maTacGia);
                    tg.setTenTacGia(rs.getString("tenTacGia"));
                    boolean daTonTai = mt.getSach().getDanhSachTacGia().stream()
                                         .anyMatch(t -> t.getMaTacGia().equals(maTacGia));
                    if (!daTonTai) {
                        mt.getSach().getDanhSachTacGia().add(tg);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return mt;
    }
    
    // --- (Hàm getPhieuMuonGroup - Giữ nguyên) ---
    public List<MuonTra> getPhieuMuonGroup(String maDocGia, Date ngayMuon) {
        List<MuonTra> phieuMuonGroup = new ArrayList<>();
        DocGia docGia = null; 
        TaiKhoan nguoiTao = null;
        String sql = "SELECT mt.*, dg.*, s.maSach, s.tenSach, "
                   + "tk.maTaiKhoan, tk.tenNguoiDung AS tenNguoiTao "
                   + "FROM MUONTRA mt "
                   + "LEFT JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "LEFT JOIN SACH s ON mt.maSach = s.maSach "
                   + "LEFT JOIN TAIKHOAN tk ON mt.maNguoiTao = tk.maTaiKhoan "
                   + "WHERE mt.maDocGia = ? AND mt.ngayMuon = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            ps.setTimestamp(2, new java.sql.Timestamp(ngayMuon.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                if (docGia == null) {
                    docGia = new DocGia(
                        rs.getString("maDocGia"), rs.getString("hoTen"),
                        rs.getDate("ngaySinh"), rs.getString("email"),
                        rs.getString("diaChi"), rs.getString("sdt"),
                        rs.getString("duongDanAnh"), rs.getBoolean("blocked"),
                        rs.getBoolean("isArchived")
                    );
                }
                if (nguoiTao == null) {
                    nguoiTao = new TaiKhoan(0, null, null, null, null, null, null, null); 
                    nguoiTao.setMaTaiKhoan(rs.getInt("maTaiKhoan"));
                    String tenNguoiTao = rs.getString("tenNguoiTao");
                    
                    if (rs.wasNull()) {
                        nguoiTao.setTenNguoiDung("N/A (Tài khoản đã xóa)");
                    } else {
                        nguoiTao.setTenNguoiDung(tenNguoiTao);
                    }
                }
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                
                mt.setDocGia(docGia);
                mt.setSach(s);
                mt.setNguoiTao(nguoiTao);
                phieuMuonGroup.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return phieuMuonGroup;
    }
    
    // --- (Hàm deleteMuonTra - Giữ nguyên) ---
    public boolean deleteMuonTra(int maMuonTra) {
        String sql = "DELETE FROM MUONTRA WHERE maMuonTra = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maMuonTra);
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    // ================================================================
    // <<< HÀM NÀY ĐÃ ĐƯỢC SỬA LỖI CÚ PHÁP (XÓA 'N' TRƯỚC CHUỖI) >>>
    // ================================================================
    public List<MuonTra> searchMuonTra(String mode, String value) {
        List<MuonTra> danhSachTho = new ArrayList<>(); // Danh sách thô từ DB
        List<Object> params = new ArrayList<>();
        String condition = "";
        String filterTrangThaiJava = null; // Cờ để lọc bằng Java

        // 1. Xây dựng điều kiện SQL
        switch (mode) {
            case "Mã mượn":
                condition = "mt.maMuonTra = ?";
                try {
                    params.add(Integer.parseInt(value));
                } catch (NumberFormatException e) { return danhSachTho; }
                break;
            case "Độc giả":
                condition = "dg.hoTen LIKE ?";
                params.add("%" + value + "%"); // <<< SỬA Ở ĐÂY
                break;
            case "Sách":
                condition = "s.tenSach LIKE ?";
                params.add("%" + value + "%"); // <<< SỬA Ở ĐÂY
                break;
            case "Ngày mượn":
                condition = "CONVERT(nvarchar, mt.ngayMuon, 103) LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Ngày trả":
                condition = "CONVERT(nvarchar, mt.ngayTraThucTe, 103) LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Ngày hẹn trả":
                condition = "CONVERT(nvarchar, mt.ngayHenTra, 103) LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Trạng thái":
                filterTrangThaiJava = value; 
                String sqlSearchValue;
                if (value.equalsIgnoreCase("Quá hạn") || value.equalsIgnoreCase("Đang mượn")) {
                    sqlSearchValue = "Đang mượn";
                } else { // "Đã trả"
                    sqlSearchValue = "Đã trả";
                }
                condition = "mt.trangThai LIKE ?";
                params.add("%" + sqlSearchValue + "%"); // <<< SỬA Ở ĐÂY
                break;
            case "Loại mượn":
                condition = "mt.loaiMuon LIKE ?";
                params.add("%" + value + "%"); // <<< SỬA Ở ĐÂY
                break;
            default:
                return danhSachTho; // Mode không hợp lệ
        }

        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE " + condition 
                   + " ORDER BY mt.ngayMuon DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();

            // 2. Đọc kết quả thô
            while (rs.next()) {
                DocGia dg = new DocGia();
                dg.setMaDocGia(rs.getString("maDocGia"));
                dg.setHoTen(rs.getString("hoTen"));
                
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                
                mt.setDocGia(dg);
                mt.setSach(s);
                
                danhSachTho.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }

        // 3. LỌC BẰNG JAVA (ĐỂ XỬ LÝ "QUÁ HẠN")
        if (filterTrangThaiJava != null) {
            List<MuonTra> danhSachDaLoc = new ArrayList<>();
            for (MuonTra mt : danhSachTho) {
                if (mt.getTrangThai().equalsIgnoreCase(filterTrangThaiJava)) {
                    danhSachDaLoc.add(mt);
                }
            }
            return danhSachDaLoc; // Trả về danh sách đã lọc
        }
        
        return danhSachTho; // Trả về danh sách thô (nếu không lọc theo Trạng thái)
    }
    
    // --- (Hàm getLichSuMuonTraByDocGia - Giữ nguyên) ---
    public List<MuonTra> getLichSuMuonTraByDocGia(String maDocGia) {
        List<MuonTra> danhSach = new ArrayList<>();
        String sql = "SELECT mt.*, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.maDocGia = ? "
                   + "ORDER BY mt.ngayMuon DESC"; 
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            rs = ps.executeQuery();
            while (rs.next()) {
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                mt.setSach(s);
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
    
    // --- (Các hàm getSoLuong... và getDanhSach... - Giữ nguyên) ---
    public int getSoLuongSachDenHanHomNay() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM MUONTRA "
                   + "WHERE trangThai = N'Đang mượn' "
                   + "AND CAST(ngayHenTra AS DATE) = CAST(GETDATE() AS DATE)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return count;
    }
    public List<MuonTra> getDanhSachSachDenHanHomNay() {
        List<MuonTra> danhSach = new ArrayList<>();
        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGIA "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.trangThai = N'Đang mượn' "
                   + "AND CAST(mt.ngayHenTra AS DATE) = CAST(GETDATE() AS DATE) "
                   + "ORDER BY mt.ngayHenTra ASC"; 
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                DocGia dg = new DocGia();
                dg.setMaDocGia(rs.getString("maDocGia"));
                dg.setHoTen(rs.getString("hoTen"));
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                mt.setDocGia(dg);
                mt.setSach(s);
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
    public int getSoLuongSachQuaHan() {
        int count = 0;
        // Logic: Lấy các phiếu "Đang mượn" VÀ ngày hẹn trả đã nhỏ hơn ngày hôm nay
        String sql = "SELECT COUNT(*) FROM MUONTRA "
                   + "WHERE trangThai = N'Đang mượn' "
                   + "AND ngayHenTra < GETDATE()"; // GETDATE() là ngày giờ hiện tại
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return count;
    }
    public List<Object[]> getTopSachMuon(Date from, Date to) {
        List<Object[]> danhSach = new ArrayList<>();
        // Lấy 23:59:59 của ngày kết thúc
        Date toTimestamp = new Date(to.getTime() + (24 * 60 * 60 * 1000 - 1000));

        String sql = "SELECT TOP 10 s.tenSach, COUNT(mt.maSach) AS luotMuon "
                   + "FROM MUONTRA mt "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.ngayMuon BETWEEN ? AND ? "
                   + "GROUP BY s.tenSach "
                   + "ORDER BY luotMuon DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new java.sql.Timestamp(from.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(toTimestamp.getTime()));
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] row = new Object[] {
                    rs.getString("tenSach"),
                    rs.getInt("luotMuon")
                };
                danhSach.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }

    // ==================================================
    // <<< HÀM THỐNG KÊ 2: TOP ĐỘC GIẢ TÍCH CỰC >>>
    // ==================================================
    /**
     * (Báo cáo) Lấy Top 10 độc giả mượn nhiều nhất trong khoảng thời gian.
     * @param from Ngày bắt đầu
     * @param to Ngày kết thúc
     * @return List<Object[]> (Object[0] = Tên Độc Giả, Object[1] = Lượt mượn)
     */
    public List<Object[]> getTopDocGia(Date from, Date to) {
        List<Object[]> danhSach = new ArrayList<>();
        Date toTimestamp = new Date(to.getTime() + (24 * 60 * 60 * 1000 - 1000));

        String sql = "SELECT TOP 10 dg.hoTen, COUNT(mt.maDocGia) AS luotMuon "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "WHERE mt.ngayMuon BETWEEN ? AND ? "
                   + "GROUP BY dg.hoTen "
                   + "ORDER BY luotMuon DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new java.sql.Timestamp(from.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(toTimestamp.getTime()));
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] row = new Object[] {
                    rs.getString("hoTen"),
                    rs.getInt("luotMuon")
                };
                danhSach.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
}