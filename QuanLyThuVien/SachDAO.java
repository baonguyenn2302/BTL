package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Sử dụng Timestamp
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class SachDAO {

    // === CRUD OPERATIONS ===

    /**
     * Thêm sách mới (Chỉ 9 cột, 3 cột còn lại tự động)
     */
    public boolean themSach(Sach sach) {
        String sql = "INSERT INTO SACH(maSach, tenSach, tacGia, nhaXuatBan, namXuatBan, soLuong, moTa, duongDanAnh, duongDanXemTruoc) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sach.getMaSach());
            pstmt.setString(2, sach.getTenSach());
            pstmt.setString(3, sach.getTacGia());
            pstmt.setString(4, sach.getNhaXuatBan());
            if (sach.getNamXuatBan() > 0) pstmt.setInt(5, sach.getNamXuatBan());
            else pstmt.setNull(5, java.sql.Types.INTEGER);
            pstmt.setInt(6, sach.getSoLuong());
            pstmt.setString(7, sach.getMoTa());
            pstmt.setString(8, sach.getDuongDanAnh());
            pstmt.setString(9, sach.getDuongDanXemTruoc());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("PRIMARY KEY")) System.err.println("Lỗi: Mã sách '" + sach.getMaSach() + "' đã tồn tại.");
            return false;
        }
    }

    /**
     * Sửa thông tin sách (Không sửa ngayThem, luotXem, luotTai)
     */
    public boolean suaSach(Sach sach) {
        String sql = "UPDATE SACH SET tenSach = ?, tacGia = ?, nhaXuatBan = ?, namXuatBan = ?, " +
                     "soLuong = ?, moTa = ?, duongDanAnh = ?, duongDanXemTruoc = ? WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sach.getTenSach());
            pstmt.setString(2, sach.getTacGia());
            pstmt.setString(3, sach.getNhaXuatBan());
            if (sach.getNamXuatBan() > 0) pstmt.setInt(4, sach.getNamXuatBan());
            else pstmt.setNull(4, java.sql.Types.INTEGER);
            pstmt.setInt(5, sach.getSoLuong());
            pstmt.setString(6, sach.getMoTa());
            pstmt.setString(7, sach.getDuongDanAnh());
            pstmt.setString(8, sach.getDuongDanXemTruoc());
            pstmt.setString(9, sach.getMaSach());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Xóa sách
     */
    public boolean xoaSach(String maSach) {
        String sql = "DELETE FROM SACH WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSach);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Lỗi xóa sách (FK constraint?): " + e.getMessage()); return false; }
    }

    // === DATA RETRIEVAL METHODS ===

    public Sach getSachByMaSach(String maSach) {
        String sql = "SELECT * FROM SACH WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSach);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return mapRowToSach(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Sach> getAllSach() {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT * FROM SACH ORDER BY tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) ds.add(mapRowToSach(rs));
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    public List<Sach> getSachByTacGia(String tacGia) {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT * FROM SACH WHERE tacGia = ? ORDER BY tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tacGia);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    // === NEW RETRIEVAL METHODS FOR TABS ===

    public List<Sach> getSachMoiNhat(int limit) {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT TOP (?) * FROM SACH ORDER BY ngayThem DESC, tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    public List<Sach> getSachXemNhieuNhat(int limit) {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT TOP (?) * FROM SACH WHERE luotXem > 0 ORDER BY luotXem DESC, tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    public List<Sach> getSachTaiNhieuNhat(int limit) {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT TOP (?) * FROM SACH WHERE luotTai > 0 ORDER BY luotTai DESC, tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    // === COUNTER UPDATE METHODS ===

    public boolean tangLuotXem(String maSach) {
        String sql = "UPDATE SACH SET luotXem = ISNULL(luotXem, 0) + 1 WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSach); return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean tangLuotTai(String maSach) {
        String sql = "UPDATE SACH SET luotTai = ISNULL(luotTai, 0) + 1 WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSach); return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // === ADVANCED SEARCH METHOD ===
    public List<Sach> timKiemSachNangCao(String keyword, String searchType) {
        List<Sach> ds = new ArrayList<>(); StringBuilder sql = new StringBuilder("SELECT * FROM SACH WHERE ");
        List<Object> params = new ArrayList<>(); String kwLike = "%" + keyword + "%";
        try {
            switch (searchType) {
                case "Nhan đề": sql.append("tenSach LIKE ?"); params.add(kwLike); break;
                case "Tác giả": sql.append("tacGia LIKE ?"); params.add(kwLike); break;
                case "Năm xuất bản": try { int nam = Integer.parseInt(keyword); sql.append("namXuatBan = ?"); params.add(nam); } catch (NumberFormatException e) { System.err.println("Lỗi: Năm XB phải số."); return ds; } break;
                // case "Chủ đề": sql.append("chuDe LIKE ?"); params.add(kwLike); break;
                default: sql.append("(tenSach LIKE ? OR tacGia LIKE ? OR nhaXuatBan LIKE ? OR maSach LIKE ?)"); params.add(kwLike); params.add(kwLike); params.add(kwLike); params.add(kwLike); break;
            }
            sql.append(" ORDER BY tenSach");
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
                try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    // === OLD SEARCH METHOD (kept for compatibility if needed) ===
    public List<Sach> timKiemSach(String keyword) {
        List<Sach> ds = new ArrayList<>(); String sql = "SELECT * FROM SACH WHERE tenSach LIKE ? OR tacGia LIKE ? ORDER BY tenSach";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String kwLike = "%" + keyword + "%"; pstmt.setString(1, kwLike); pstmt.setString(2, kwLike);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    // === DATA MAPPING UTILITY (UPDATED) ===
    private Sach mapRowToSach(ResultSet rs) throws SQLException {
        Sach s = new Sach();
        s.setMaSach(rs.getString("maSach"));
        s.setTenSach(rs.getString("tenSach"));
        s.setTacGia(rs.getString("tacGia"));
        s.setNhaXuatBan(rs.getString("nhaXuatBan"));
        s.setNamXuatBan(rs.getInt("namXuatBan"));
        s.setSoLuong(rs.getInt("soLuong"));
        s.setMoTa(rs.getString("moTa"));
        s.setDuongDanAnh(rs.getString("duongDanAnh"));
        s.setDuongDanXemTruoc(rs.getString("duongDanXemTruoc"));
        Timestamp ts = rs.getTimestamp("ngayThem"); // Read as Timestamp
        if (ts != null) s.setNgayThem(new java.util.Date(ts.getTime())); // Convert to Date
        s.setLuotXem(rs.getInt("luotXem"));
        s.setLuotTai(rs.getInt("luotTai"));
        return s;
    }
}