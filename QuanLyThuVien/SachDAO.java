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

    // Lớp helper tĩnh để lưu trữ thông tin Tác giả và Số sách
    public static class AuthorInfo {
        public String authorName;
        public int bookCount;

        public AuthorInfo(String authorName, int bookCount) {
            this.authorName = authorName;
            this.bookCount = bookCount;
        }
    }

    // === CRUD OPERATIONS ===

    /**
     * Thêm sách mới. Tự động thêm tác giả vào bảng TACGIA nếu chưa tồn tại.
     */
    public boolean themSach(Sach sach) {
        // Sử dụng cột maTacGia trong bảng SACH, giá trị lấy từ sach.getMaTacGia()
        String sql = "INSERT INTO SACH(maSach, tenSach, maTacGia, nhaXuatBan, namXuatBan, soLuong, moTa, duongDanAnh, duongDanXemTruoc) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean sachAddedSuccessfully = false;
        String maTacGiaValue = sach.getMaTacGia(); // Lấy mã tác giả (chính là tên)

        // 1. Kiểm tra và thêm tác giả vào bảng TACGIA nếu cần
        if (maTacGiaValue != null && !maTacGiaValue.isEmpty()) {
            kiemTraVaThemTacGiaNeuChuaCo(maTacGiaValue);
        } else {
            maTacGiaValue = null; // Đảm bảo null nếu rỗng/null
        }


        // 2. Thêm sách vào bảng SACH
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sach.getMaSach());
            pstmt.setString(2, sach.getTenSach());
            pstmt.setString(3, maTacGiaValue); // Lưu maTacGia (tên tác giả) vào cột khóa ngoại của SACH
            pstmt.setString(4, sach.getNhaXuatBan());
            if (sach.getNamXuatBan() > 0) pstmt.setInt(5, sach.getNamXuatBan());
            else pstmt.setNull(5, java.sql.Types.INTEGER);
            pstmt.setInt(6, sach.getSoLuong());
            pstmt.setString(7, sach.getMoTa());
            pstmt.setString(8, sach.getDuongDanAnh());
            pstmt.setString(9, sach.getDuongDanXemTruoc());

            sachAddedSuccessfully = pstmt.executeUpdate() > 0;
            return sachAddedSuccessfully;

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("PRIMARY KEY constraint 'PK__SACH")) { // Kiểm tra PK của SACH
                 System.err.println("Lỗi: Mã sách '" + sach.getMaSach() + "' đã tồn tại.");
            } else if (e.getMessage().contains("FK_Sach_TacGia")) { // Kiểm tra FK
                 System.err.println("Lỗi: Không tìm thấy tác giả '" + maTacGiaValue + "' trong bảng TACGIA (Lỗi logic?).");
            } else {
                 System.err.println("Lỗi SQL khác khi thêm sách: " + e.getMessage());
            }
            return false;
        }
    }

     /**
      * (Helper) Kiểm tra xem tác giả (dùng tên làm mã) đã có trong bảng TACGIA chưa, nếu chưa thì thêm vào.
      * @param maTacGia Tên tác giả cần kiểm tra/thêm, cũng chính là maTacGia.
      */
     private void kiemTraVaThemTacGiaNeuChuaCo(String maTacGia) {
         // String maTacGia = tenTacGia; // Dùng tên làm mã
         String checkSql = "SELECT COUNT(*) FROM TACGIA WHERE maTacGia = ?";
         // Chỉ insert 2 cột bắt buộc, các cột khác sẽ là NULL mặc định
         String insertSql = "INSERT INTO TACGIA (maTacGia, tenTacGia) VALUES (?, ?)";

         try (Connection conn = DatabaseConnection.getConnection()) {
             // Kiểm tra tồn tại
             try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                 checkStmt.setString(1, maTacGia);
                 try (ResultSet rs = checkStmt.executeQuery()) {
                     if (rs.next() && rs.getInt(1) == 0) {
                         // Nếu chưa tồn tại, thực hiện INSERT
                         try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                             insertStmt.setString(1, maTacGia); // Dùng tên làm mã
                             insertStmt.setString(2, maTacGia); // Lưu tên vào cả cột tenTacGia
                             insertStmt.executeUpdate();
                             System.out.println("Đã tự động thêm tác giả mới vào bảng TACGIA: " + maTacGia);
                         } catch (SQLException insertEx) {
                              System.err.println("Lỗi khi INSERT tác giả tự động: " + insertEx.getMessage());
                              // Có thể tên đã được thêm bởi một tiến trình khác?
                         }
                     }
                 }
             }
         } catch (SQLException e) {
             System.err.println("Lỗi khi kiểm tra tác giả tự động: " + e.getMessage());
         }
     }


    /**
     * Sửa thông tin sách. Tự động thêm tác giả vào bảng TACGIA nếu chưa tồn tại.
     */
    public boolean suaSach(Sach sach) {
        String sql = "UPDATE SACH SET tenSach = ?, maTacGia = ?, nhaXuatBan = ?, namXuatBan = ?, " + // Sử dụng maTacGia
                     "soLuong = ?, moTa = ?, duongDanAnh = ?, duongDanXemTruoc = ? WHERE maSach = ?";
        String maTacGiaValue = sach.getMaTacGia(); // Lấy mã tác giả (chính là tên)

        // 1. Kiểm tra và thêm tác giả vào bảng TACGIA nếu cần
        if (maTacGiaValue != null && !maTacGiaValue.isEmpty()) {
            kiemTraVaThemTacGiaNeuChuaCo(maTacGiaValue);
        } else {
            maTacGiaValue = null; // Đảm bảo null nếu rỗng/null
        }

        // 2. Cập nhật sách
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sach.getTenSach());
            pstmt.setString(2, maTacGiaValue); // Cập nhật khóa ngoại maTacGia
            pstmt.setString(3, sach.getNhaXuatBan());
            if (sach.getNamXuatBan() > 0) pstmt.setInt(4, sach.getNamXuatBan());
            else pstmt.setNull(4, java.sql.Types.INTEGER);
            pstmt.setInt(5, sach.getSoLuong());
            pstmt.setString(6, sach.getMoTa());
            pstmt.setString(7, sach.getDuongDanAnh());
            pstmt.setString(8, sach.getDuongDanXemTruoc());
            pstmt.setString(9, sach.getMaSach()); // WHERE clause

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
             if (e.getMessage().contains("FK_Sach_TacGia")) { // Kiểm tra FK
                 System.err.println("Lỗi: Không tìm thấy tác giả '" + maTacGiaValue + "' trong bảng TACGIA khi sửa sách (Lỗi logic?).");
             } else {
                 System.err.println("Lỗi SQL khác khi sửa sách: " + e.getMessage());
             }
            return false;
        }
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

    // Sửa: Lấy sách theo mã tác giả (từ cột maTacGia trong SACH)
    public List<Sach> getSachByTacGia(String maTacGia) {
        List<Sach> ds = new ArrayList<>();
        // Tìm kiếm chính xác mã tác giả (chính là tên) trong bảng SACH
        String sql = "SELECT * FROM SACH WHERE maTacGia = ? ORDER BY tenSach";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maTacGia); // Tìm chính xác mã (tên)
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) ds.add(mapRowToSach(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
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
                case "Nhan đề": sql.append("UPPER(tenSach) LIKE ?"); params.add(kwLike.toUpperCase()); break;
                // Sửa: Tìm trong cột maTacGia (chứa tên), không phân biệt hoa thường
                case "Tác giả": sql.append("UPPER(maTacGia) LIKE ?"); params.add(kwLike.toUpperCase()); break;
                case "Năm xuất bản": try { int nam = Integer.parseInt(keyword); sql.append("namXuatBan = ?"); params.add(nam); } catch (NumberFormatException e) { System.err.println("Lỗi: Năm XB phải số."); return ds; } break;
                // case "Chủ đề": sql.append("chuDe LIKE ?"); params.add(kwLike); break;

                // Sửa: Tìm trong cột maTacGia (chứa tên), không phân biệt hoa thường
                default: sql.append("(UPPER(tenSach) LIKE ? OR UPPER(maTacGia) LIKE ? OR nhaXuatBan LIKE ? OR maSach LIKE ?)");
                         params.add(kwLike.toUpperCase()); params.add(kwLike.toUpperCase()); params.add(kwLike); params.add(kwLike); break;
            }
            sql.append(" ORDER BY tenSach");
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
                try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) ds.add(mapRowToSach(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }


    // === DATA MAPPING UTILITY ===
    private Sach mapRowToSach(ResultSet rs) throws SQLException {
        Sach s = new Sach();
        s.setMaSach(rs.getString("maSach"));
        s.setTenSach(rs.getString("tenSach"));
        s.setMaTacGia(rs.getString("maTacGia")); // Lấy mã tác giả từ cột maTacGia
        s.setNhaXuatBan(rs.getString("nhaXuatBan"));
        s.setNamXuatBan(rs.getInt("namXuatBan"));
        s.setSoLuong(rs.getInt("soLuong"));
        s.setMoTa(rs.getString("moTa"));
        s.setDuongDanAnh(rs.getString("duongDanAnh"));
        s.setDuongDanXemTruoc(rs.getString("duongDanXemTruoc"));
        Timestamp ts = rs.getTimestamp("ngayThem");
        if (ts != null) s.setNgayThem(new java.util.Date(ts.getTime()));
        s.setLuotXem(rs.getInt("luotXem"));
        s.setLuotTai(rs.getInt("luotTai"));
        return s;
    }

    // =================================================================
    // === CÁC PHƯƠNG THỨC CHO PHÂN TRANG VÀ LỌC SÁCH ===
    // =================================================================

    public int getSachCount(String filterYear) {
        return getSachCount("YEAR", filterYear);
    }

    public List<Sach> getSachPaginated(int page, int itemsPerPage, String sortBy, String sortOrder, String filterYear) {
        return getSachPaginated(page, itemsPerPage, sortBy, sortOrder, "YEAR", filterYear);
    }

    public List<String> getDistinctNamXuatBan() {
        List<String> ds = new ArrayList<>();
        String sql = "SELECT DISTINCT namXuatBan FROM SACH WHERE namXuatBan IS NOT NULL AND namXuatBan > 0 ORDER BY namXuatBan DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { ds.add(String.valueOf(rs.getInt("namXuatBan"))); }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    public int getSachCount(String filterType, String filterValue) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM SACH");
        List<Object> params = new ArrayList<>();
        if (filterValue != null && !filterValue.isEmpty()) {
            if ("YEAR".equals(filterType)) {
                try { int nam = Integer.parseInt(filterValue); sql.append(" WHERE namXuatBan = ?"); params.add(nam); }
                catch (NumberFormatException e) { System.err.println("Lỗi: Năm lọc phải là số."); return 0; }
            } else if ("TITLE_PREFIX".equals(filterType)) {
                sql.append(" WHERE UPPER(tenSach) LIKE ?"); params.add(filterValue.toUpperCase() + "%");
            }
            // Thêm các loại filter khác nếu cần
        }
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) { pstmt.setObject(i + 1, params.get(i)); }
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) { return rs.getInt(1); } }
        } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    public List<Sach> getSachPaginated(int page, int itemsPerPage, String sortBy, String sortOrder, String filterType, String filterValue) {
        List<Sach> ds = new ArrayList<>(); StringBuilder sql = new StringBuilder("SELECT * FROM SACH "); List<Object> params = new ArrayList<>();
        if (filterValue != null && !filterValue.isEmpty()) {
            sql.append("WHERE ");
            if ("YEAR".equals(filterType)) {
                try { int nam = Integer.parseInt(filterValue); sql.append("namXuatBan = ? "); params.add(nam); }
                catch (NumberFormatException e) { System.err.println("Lỗi: Năm lọc phải là số."); return ds; }
            } else if ("TITLE_PREFIX".equals(filterType)) {
                sql.append("UPPER(tenSach) LIKE ? "); params.add(filterValue.toUpperCase() + "%");
            }
             // Thêm các loại filter khác nếu cần
        }
        sql.append("ORDER BY "); if ("namXuatBan".equals(sortBy)) { sql.append("namXuatBan"); } else { sql.append("tenSach"); } // Mặc định tenSach
        if ("DESC".equals(sortOrder)) { sql.append(" DESC"); } else { sql.append(" ASC"); }
        int offset = (page - 1) * itemsPerPage; sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); params.add(offset); params.add(itemsPerPage);
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) { pstmt.setObject(i + 1, params.get(i)); }
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) { ds.add(mapRowToSach(rs)); } }
        } catch (SQLException e) { e.printStackTrace(); } return ds;
    }

    // =================================================================
    // === CÁC PHƯƠNG THỨC MỚI CHO DUYỆT THEO TÁC GIẢ ===
    // =================================================================

    /**
     * Lấy tổng số lượng tác giả (duy nhất), lọc theo chữ cái đầu hoặc từ khóa chứa trong tên.
     * @param filterType Loại lọc ("AUTHOR_PREFIX", "AUTHOR_CONTAINS")
     * @param filterValue Giá trị lọc ("A", "Quang")
     * @return Tổng số tác giả thỏa mãn
     */
    public int getAuthorCount(String filterType, String filterValue) {
        // Đếm từ bảng TACGIA
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(maTacGia) FROM TACGIA WHERE maTacGia IS NOT NULL AND maTacGia <> ''");
        List<Object> params = new ArrayList<>();

        if (filterValue != null && !filterValue.isEmpty()) {
            if ("AUTHOR_PREFIX".equals(filterType)) {
                sqlCount.append(" AND UPPER(tenTacGia) LIKE ?"); // Lọc trên tenTacGia
                params.add(filterValue.toUpperCase() + "%");
            } else if ("AUTHOR_CONTAINS".equals(filterType)) {
                sqlCount.append(" AND UPPER(tenTacGia) LIKE ?"); // Lọc trên tenTacGia
                params.add("%" + filterValue.toUpperCase() + "%");
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCount.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Lấy danh sách Tác giả (kèm số sách) có phân trang, lọc, và sắp xếp.
     * @param sortBy Cột để sắp xếp ("authorName" hoặc "bookCount")
     * @param filterType Loại lọc ("AUTHOR_PREFIX", "AUTHOR_CONTAINS")
     * @param filterValue Giá trị lọc ("A", "Quang")
     * @return Danh sách AuthorInfo
     */
    public List<AuthorInfo> getAuthorsPaginated(int page, int itemsPerPage, String sortBy, String sortOrder, String filterType, String filterValue) {
        List<AuthorInfo> ds = new ArrayList<>();
        // Sử dụng LEFT JOIN để lấy cả những tác giả chưa có sách nào
        StringBuilder sql = new StringBuilder(
            "SELECT tg.tenTacGia, COUNT(s.maSach) as bookCount " +
            "FROM TACGIA tg LEFT JOIN SACH s ON tg.maTacGia = s.maTacGia " + // JOIN qua maTacGia
            "WHERE tg.maTacGia IS NOT NULL AND tg.maTacGia <> '' "); // Lấy từ bảng TACGIA
        List<Object> params = new ArrayList<>();

        // 1. Lọc (Filtering) - Áp dụng trên bảng TACGIA
         if (filterValue != null && !filterValue.isEmpty()) {
            if ("AUTHOR_PREFIX".equals(filterType)) {
                sql.append("AND UPPER(tg.tenTacGia) LIKE ? ");
                params.add(filterValue.toUpperCase() + "%");
            } else if ("AUTHOR_CONTAINS".equals(filterType)) {
                sql.append("AND UPPER(tg.tenTacGia) LIKE ? ");
                params.add("%" + filterValue.toUpperCase() + "%");
            }
        }

        // 2. Grouping
        sql.append("GROUP BY tg.tenTacGia, tg.maTacGia "); // Group cả maTacGia

        // 3. Sắp xếp (Sorting)
        sql.append("ORDER BY ");
        if ("bookCount".equals(sortBy)) {
            sql.append("bookCount"); // Sắp xếp theo số sách
        } else {
            sql.append("tg.tenTacGia"); // Mặc định là tên tác giả
        }

        if ("DESC".equals(sortOrder)) {
            sql.append(" DESC");
        } else {
            sql.append(" ASC"); // Mặc định là ASC
        }

        // 4. Phân trang (Pagination)
        int offset = (page - 1) * itemsPerPage;
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(itemsPerPage);

        // 5. Thực thi truy vấn
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Lấy tenTacGia từ kết quả JOIN
                    ds.add(new AuthorInfo(rs.getString("tenTacGia"), rs.getInt("bookCount")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
