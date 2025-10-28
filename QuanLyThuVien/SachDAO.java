package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp DAO (Data Access Object) cho đối tượng Sách.
 * ĐÃ TÁI CẤU TRÚC: Hỗ trợ nhiều tác giả (dùng bảng SACH_TACGIA)
 */
public class SachDAO {
    
    // Lớp AuthorInfo vẫn giữ nguyên, nó được dùng cho tab "Duyệt Tác Giả"
    public static class AuthorInfo {
        public String authorName;
        public int bookCount;
        public AuthorInfo(String authorName, int bookCount) {
            this.authorName = authorName;
            this.bookCount = bookCount;
        }
    }

    // === HÀM HELPER MỚI: XỬ LÝ JOIN HIỆU QUẢ ===

    /**
     * Helper: Ánh xạ một hàng ResultSet (chỉ các cột của SACH) sang đối tượng Sách.
     * KHÔNG lấy tác giả ở đây.
     */
    private Sach mapRowToSach(ResultSet rs) throws SQLException {
        Sach s = new Sach();
        s.setMaSach(rs.getString("maSach"));
        s.setTenSach(rs.getString("tenSach"));
        s.setNhaXuatBan(rs.getString("nhaXuatBan"));
        s.setNamXuatBan(rs.getInt("namXuatBan"));
        s.setSoLuong(rs.getInt("soLuong"));
        s.setMoTa(rs.getString("moTa"));
        s.setDuongDanAnh(rs.getString("duongDanAnh"));
        Timestamp ts = rs.getTimestamp("ngayThem");
        if (ts != null) s.setNgayThem(new java.util.Date(ts.getTime()));
        s.setViTri(rs.getString("viTri"));
        
        // Khởi tạo danh sách tác giả rỗng
        s.setDanhSachTacGia(new ArrayList<>()); 
        return s;
    }

    /**
     * Helper chung: Thực thi truy vấn SQL và xử lý logic JOIN (Nhiều-nhiều)
     * để nhóm các tác giả vào đúng sách của họ.
     */
    protected List<Sach> getSachInternal(String sql, Object... params) {
        // Dùng LinkedHashMap để giữ đúng thứ tự (ORDER BY)
        Map<String, Sach> sachMap = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String maSach = rs.getString("maSach");
                    Sach sach = sachMap.get(maSach);

                    // Nếu sách chưa có trong Map, tạo mới và thêm vào
                    if (sach == null) {
                        sach = mapRowToSach(rs);
                        sachMap.put(maSach, sach);
                    }

                    // Lấy thông tin tác giả từ hàng JOIN (nếu có)
                    String maTacGia = rs.getString("maTacGia_join");
                    if (maTacGia != null) {
                        TacGia tg = new TacGia();
                        tg.setMaTacGia(maTacGia);
                        tg.setTenTacGia(rs.getString("tenTacGia_join"));
                        
                        // Thêm tác giả vào danh sách của sách
                        sach.getDanhSachTacGia().add(tg);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(sachMap.values());
    }
    
    // === CÁC HÀM GET (ĐÃ CẬP NHẬT) ===

    public List<Sach> getAllSach() {
        String sql = "SELECT s.*, t.maTacGia as maTacGia_join, t.tenTacGia as tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "ORDER BY s.tenSach, t.tenTacGia";
        return getSachInternal(sql);
    }

    public Sach getSachByMaSach(String maSach) {
        String sql = "SELECT s.*, t.maTacGia as maTacGia_join, t.tenTacGia as tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "WHERE s.maSach = ? " +
                     "ORDER BY t.tenTacGia";
        List<Sach> results = getSachInternal(sql, maSach);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Lấy Sách theo Tác Giả (dùng bảng trung gian)
     */
    public List<Sach> getSachByTacGia(String maTacGia) {
         String sql = "SELECT s.*, t.maTacGia as maTacGia_join, t.tenTacGia as tenTacGia_join " +
                     "FROM SACH s " +
                     "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                     "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia " +
                     "WHERE s.maSach IN (SELECT maSach FROM SACH_TACGIA WHERE maTacGia = ?) " +
                     "ORDER BY s.tenSach, t.tenTacGia";
        return getSachInternal(sql, maTacGia);
    }

    // === CRUD OPERATIONS (ĐÃ CẬP NHẬT) ===

    /**
     * Thêm sách mới (Sử dụng Transaction)
     */
    public boolean themSach(Sach sach) {
        String sqlSach = "INSERT INTO SACH(maSach, tenSach, nhaXuatBan, namXuatBan, soLuong, moTa, duongDanAnh, viTri, ngayThem) " +
                         "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlSachTacGia = "INSERT INTO SACH_TACGIA(maSach, maTacGia) VALUES(?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Thêm vào bảng SACH
            try (PreparedStatement pstmtSach = conn.prepareStatement(sqlSach)) {
                pstmtSach.setString(1, sach.getMaSach());
                pstmtSach.setString(2, sach.getTenSach());
                pstmtSach.setString(3, sach.getNhaXuatBan());
                if (sach.getNamXuatBan() > 0) pstmtSach.setInt(4, sach.getNamXuatBan());
                else pstmtSach.setNull(4, java.sql.Types.INTEGER);
                pstmtSach.setInt(5, sach.getSoLuong());
                pstmtSach.setString(6, sach.getMoTa());
                pstmtSach.setString(7, sach.getDuongDanAnh());
                pstmtSach.setString(8, sach.getViTri());
                // Dùng ngày giờ hiện tại
                pstmtSach.setTimestamp(9, new Timestamp(new Date().getTime()));
                
                pstmtSach.executeUpdate();
            }

            // 2. Thêm vào bảng SACH_TACGIA
            if (sach.getDanhSachTacGia() != null && !sach.getDanhSachTacGia().isEmpty()) {
                try (PreparedStatement pstmtLink = conn.prepareStatement(sqlSachTacGia)) {
                    for (TacGia tg : sach.getDanhSachTacGia()) {
                        pstmtLink.setString(1, sach.getMaSach());
                        pstmtLink.setString(2, tg.getMaTacGia());
                        pstmtLink.addBatch(); // Thêm vào lô
                    }
                    pstmtLink.executeBatch(); // Thực thi lô
                }
            }

            conn.commit(); // Hoàn tất Transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sửa thông tin sách (Sử dụng Transaction)
     */
    public boolean suaSach(Sach sach) {
        String sqlSach = "UPDATE SACH SET tenSach = ?, nhaXuatBan = ?, namXuatBan = ?, " +
                         "soLuong = ?, moTa = ?, duongDanAnh = ?, viTri = ? WHERE maSach = ?";
        String sqlDeleteLinks = "DELETE FROM SACH_TACGIA WHERE maSach = ?";
        String sqlInsertLinks = "INSERT INTO SACH_TACGIA(maSach, maTacGia) VALUES(?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật bảng SACH
            try (PreparedStatement pstmtSach = conn.prepareStatement(sqlSach)) {
                pstmtSach.setString(1, sach.getTenSach());
                pstmtSach.setString(2, sach.getNhaXuatBan());
                if (sach.getNamXuatBan() > 0) pstmtSach.setInt(3, sach.getNamXuatBan());
                else pstmtSach.setNull(3, java.sql.Types.INTEGER);
                pstmtSach.setInt(4, sach.getSoLuong());
                pstmtSach.setString(5, sach.getMoTa());
                pstmtSach.setString(6, sach.getDuongDanAnh());
                pstmtSach.setString(7, sach.getViTri());
                pstmtSach.setString(8, sach.getMaSach()); // WHERE
                pstmtSach.executeUpdate();
            }

            // 2. Xóa tất cả link tác giả cũ
            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteLinks)) {
                pstmtDelete.setString(1, sach.getMaSach());
                pstmtDelete.executeUpdate();
            }

            // 3. Thêm link tác giả mới
            if (sach.getDanhSachTacGia() != null && !sach.getDanhSachTacGia().isEmpty()) {
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertLinks)) {
                    for (TacGia tg : sach.getDanhSachTacGia()) {
                        pstmtInsert.setString(1, sach.getMaSach());
                        pstmtInsert.setString(2, tg.getMaTacGia());
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }
            }
            
            conn.commit(); // Hoàn tất Transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Xóa sách (Database đã có ON DELETE CASCADE)
     */
    public boolean xoaSach(String maSach) {
        // Do đã thiết lập ON DELETE CASCADE, CSDL sẽ tự động xóa các
        // hàng liên quan trong SACH_TACGIA và MUONTRA
        String sql = "DELETE FROM SACH WHERE maSach = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSach);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            // Nếu lỗi (ví dụ: MUONTRA không có ON DELETE CASCADE), nó sẽ báo ở đây
            System.err.println("Lỗi xóa sách (FK constraint?): " + e.getMessage()); 
            return false; 
        }
    }
    
    // === TÌM KIẾM VÀ PHÂN TRANG (CẬP NHẬT) ===
    
    /**
     * Tìm kiếm nâng cao
     */
    public List<Sach> timKiemSachNangCao(String keyword, String searchType) {
        String kwLike = "%" + keyword + "%";
        
        // SQL cơ sở
        String sqlBase = "SELECT s.*, t.maTacGia as maTacGia_join, t.tenTacGia as tenTacGia_join " +
                         "FROM SACH s " +
                         "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach " +
                         "LEFT JOIN TACGIA t ON st.maTacGia = t.maTacGia ";
        
        StringBuilder sqlWhere = new StringBuilder();
        List<Object> params = new ArrayList<>();

        try {
            switch (searchType) {
                case "Nhan đề":
                    sqlWhere.append("WHERE UPPER(s.tenSach) LIKE ?");
                    params.add(kwLike.toUpperCase());
                    break;
                case "Tác giả":
                    sqlWhere.append("WHERE s.maSach IN (SELECT st.maSach FROM SACH_TACGIA st JOIN TACGIA t ON st.maTacGia = t.maTacGia WHERE UPPER(t.tenTacGia) LIKE ?)");
                    params.add(kwLike.toUpperCase());
                    break;
                case "Năm xuất bản":
                    int nam = Integer.parseInt(keyword); 
                    sqlWhere.append("WHERE s.namXuatBan = ?"); 
                    params.add(nam); 
                    break;
                default: // Tìm tất cả
                    sqlWhere.append("WHERE (UPPER(s.tenSach) LIKE ? OR s.nhaXuatBan LIKE ? OR s.maSach LIKE ? OR s.viTri LIKE ? OR s.maSach IN (SELECT st.maSach FROM SACH_TACGIA st JOIN TACGIA t ON st.maTacGia = t.maTacGia WHERE UPPER(t.tenTacGia) LIKE ?))");
                    params.add(kwLike.toUpperCase());
                    params.add(kwLike);
                    params.add(kwLike);
                    params.add(kwLike);
                    params.add(kwLike.toUpperCase()); // Cho tìm kiếm tác giả
                    break;
            }
            
            String sqlOrder = " ORDER BY s.tenSach, t.tenTacGia";
            return getSachInternal(sqlBase + sqlWhere.toString() + sqlOrder, params.toArray());
            
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Năm XB phải là số.");
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Các hàm phân trang và duyệt tác giả (getAuthorCount, getAuthorsPaginated...)
    // giữ nguyên vì chúng hoạt động trên logic JOIN riêng
    
    // (Giữ nguyên các hàm: getSachCount, getSachPaginated, getDistinctNamXuatBan, 
    // getAuthorCount, getAuthorsPaginated)
    // ... (Các hàm này không cần thay đổi) ...
}