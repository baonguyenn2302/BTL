package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) cho việc Quản lý Mượn Trả sách.
 */
public class MuonTraDAO {

    private SachDAO sachDAO;     // Dùng để lấy thông tin sách
    private DocGiaDAO docGiaDAO; // Dùng để lấy thông tin độc giả

    public MuonTraDAO() {
        this.sachDAO = new SachDAO();
        this.docGiaDAO = new DocGiaDAO();
    }

    /**
     * Helper: Ánh xạ ResultSet sang đối tượng MuonTra (bao gồm cả DocGia và Sach)
     */
    private MuonTra mapRowToMuonTra(ResultSet rs) throws SQLException {
        MuonTra mt = new MuonTra();
        mt.setMaMuonTra(rs.getInt("maMuonTra"));

        // Lấy thông tin Độc Giả
        String maDocGia = rs.getString("maDocGia");
        DocGia dg = docGiaDAO.getDocGiaByMaDocGia(maDocGia); // Dùng DAO để lấy đối tượng
        mt.setDocGia(dg != null ? dg : new DocGia(maDocGia, "Không tìm thấy", null, null, null, null, false)); // Xử lý nếu không tìm thấy

        // Lấy thông tin Sách
        String maSach = rs.getString("maSach");
        Sach sach = sachDAO.getSachByMaSach(maSach); // Dùng DAO để lấy đối tượng
        mt.setSach(sach != null ? sach : new Sach(maSach, "Không tìm thấy", null, 0, 0, null, null, null, null, null)); // Xử lý nếu không tìm thấy


        mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
        mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
        mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
        mt.setTrangThai(rs.getString("trangThai")); // Lấy trạng thái từ DB

        return mt;
    }

    /**
     * Lấy danh sách phiếu mượn, có thể lọc theo trạng thái.
     * Dùng JOIN để lấy tên sách, tên độc giả.
     */
    public List<MuonTra> getDanhSachMuon(String filterTrangThai) {
        List<MuonTra> ds = new ArrayList<>();
        // Lấy các cột cần thiết từ MUONTRA và mã để JOIN sau
        StringBuilder sql = new StringBuilder("SELECT mt.maMuonTra, mt.maDocGia, mt.maSach, mt.ngayMuon, mt.ngayHenTra, mt.ngayTraThucTe, mt.trangThai ")
                           .append("FROM MUONTRA mt ");

        List<Object> params = new ArrayList<>();

        // Logic lọc trạng thái
        if (filterTrangThai != null && !filterTrangThai.equalsIgnoreCase("Tất cả")) {
            if (filterTrangThai.equals("Quá hạn")) {
                // Điều kiện quá hạn: chưa trả VÀ ngày hiện tại > ngày hẹn trả
                sql.append("WHERE mt.ngayTraThucTe IS NULL AND GETDATE() > mt.ngayHenTra ");
            } else if (filterTrangThai.equals("Đang mượn")) {
                // Điều kiện đang mượn: chưa trả VÀ ngày hiện tại <= ngày hẹn trả
                 sql.append("WHERE mt.ngayTraThucTe IS NULL AND (GETDATE() <= mt.ngayHenTra OR mt.ngayHenTra IS NULL) ");
            } else if (filterTrangThai.equals("Đã trả")) {
                sql.append("WHERE mt.ngayTraThucTe IS NOT NULL ");
            }
             // Bạn có thể thêm các trạng thái khác nếu cần
        }

        sql.append("ORDER BY mt.ngayMuon DESC"); // Sắp xếp mới nhất lên đầu

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for(int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRowToMuonTra(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }


    /**
     * Thực hiện cho mượn sách (Dùng Transaction)
     * @param muonTra Đối tượng MuonTra chứa maDocGia, maSach, ngayHenTra
     * @return true nếu thành công, false nếu thất bại (sách hết, độc giả bị khóa...)
     * @throws SQLException Nếu có lỗi CSDL nghiêm trọng
     * @throws IllegalStateException Nếu sách hết hoặc độc giả bị khóa
     */
    public boolean muonSach(MuonTra muonTra) throws SQLException, IllegalStateException {
        Connection conn = null;
        // Thay thế câu lệnh SQL cũ
        String sqlInsertMuon = "INSERT INTO MUONTRA(maDocGia, maSach, ngayHenTra, trangThai, loaiMuon) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateSach = "UPDATE SACH SET soLuong = soLuong - 1 WHERE maSach = ?";
        String sqlCheckSach = "SELECT soLuong FROM SACH WHERE maSach = ?";
        String sqlCheckDocGia = "SELECT blocked FROM DOCGIA WHERE maDocGia = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Kiểm tra Độc Giả có bị khóa không
            try (PreparedStatement pstmtCheckDG = conn.prepareStatement(sqlCheckDocGia)) {
                pstmtCheckDG.setString(1, muonTra.getDocGia().getMaDocGia());
                try (ResultSet rs = pstmtCheckDG.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getBoolean("blocked")) {
                            throw new IllegalStateException("Độc giả đang bị khóa, không thể mượn sách.");
                        }
                    } else {
                         throw new IllegalStateException("Không tìm thấy độc giả.");
                    }
                }
            }

            // 2. Kiểm tra Sách có còn không
            int currentSoLuong = 0;
            try (PreparedStatement pstmtCheckSach = conn.prepareStatement(sqlCheckSach)) {
                pstmtCheckSach.setString(1, muonTra.getSach().getMaSach());
                try (ResultSet rs = pstmtCheckSach.executeQuery()) {
                    if (rs.next()) {
                        currentSoLuong = rs.getInt("soLuong");
                        if (currentSoLuong <= 0) {
                            throw new IllegalStateException("Sách đã hết, không thể mượn.");
                        }
                    } else {
                         throw new IllegalStateException("Không tìm thấy sách.");
                    }
                }
            }

            // 3. Thêm vào bảng MUONTRA
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertMuon)) {
                pstmtInsert.setString(1, muonTra.getDocGia().getMaDocGia());
                pstmtInsert.setString(2, muonTra.getSach().getMaSach());
                pstmtInsert.setTimestamp(3, new Timestamp(muonTra.getNgayHenTra().getTime()));
                pstmtInsert.setString(4, "Đang mượn"); // Trạng thái ban đầu
                pstmtInsert.setString(5, muonTra.getLoaiMuon());
                pstmtInsert.executeUpdate();
            }

            // 4. Giảm số lượng sách
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateSach)) {
                pstmtUpdate.setString(1, muonTra.getSach().getMaSach());
                pstmtUpdate.executeUpdate();
            }

            conn.commit(); // Hoàn tất Transaction thành công
            return true;

        } catch (SQLException | IllegalStateException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
             // Ném lại lỗi để lớp gọi có thể xử lý (hiển thị thông báo)
            throw e;
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
     * Đánh dấu sách đã trả (Dùng Transaction)
     * @param maMuonTra Mã của phiếu mượn cần đánh dấu đã trả
     * @return true nếu thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi CSDL nghiêm trọng
     */
    public boolean traSach(int maMuonTra) throws SQLException {
        Connection conn = null;
        String sqlGetMaSach = "SELECT maSach FROM MUONTRA WHERE maMuonTra = ?";
        String sqlUpdateMuon = "UPDATE MUONTRA SET ngayTraThucTe = GETDATE(), trangThai = ? WHERE maMuonTra = ?";
        String sqlUpdateSach = "UPDATE SACH SET soLuong = soLuong + 1 WHERE maSach = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Lấy mã sách từ phiếu mượn
            String maSach = null;
            try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetMaSach)) {
                pstmtGet.setInt(1, maMuonTra);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    if (rs.next()) {
                        maSach = rs.getString("maSach");
                    } else {
                        throw new SQLException("Không tìm thấy phiếu mượn với mã: " + maMuonTra);
                    }
                }
            }
             if (maSach == null) {
                 throw new SQLException("Không lấy được mã sách từ phiếu mượn.");
             }

            // 2. Cập nhật bảng MUONTRA
            try (PreparedStatement pstmtUpdateMuon = conn.prepareStatement(sqlUpdateMuon)) {
                // Xác định trạng thái cuối cùng (Đã trả / Quá hạn trả) - Tùy chọn
                // Ở đây đơn giản là "Đã trả"
                pstmtUpdateMuon.setString(1, "Đã trả");
                pstmtUpdateMuon.setInt(2, maMuonTra);
                int rowsAffected = pstmtUpdateMuon.executeUpdate();
                if (rowsAffected == 0) {
                     throw new SQLException("Cập nhật phiếu mượn thất bại (có thể đã được trả?).");
                }
            }

            // 3. Tăng số lượng sách
            try (PreparedStatement pstmtUpdateSach = conn.prepareStatement(sqlUpdateSach)) {
                pstmtUpdateSach.setString(1, maSach);
                pstmtUpdateSach.executeUpdate();
            }

            conn.commit(); // Hoàn tất Transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lại lỗi để lớp gọi xử lý
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
     * Gia hạn phiếu mượn
     */
    public boolean giaHan(int maMuonTra, Date ngayHenTraMoi) {
        String sql = "UPDATE MUONTRA SET ngayHenTra = ? WHERE maMuonTra = ? AND ngayTraThucTe IS NULL"; // Chỉ gia hạn phiếu chưa trả
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setTimestamp(1, new Timestamp(ngayHenTraMoi.getTime()));
             pstmt.setInt(2, maMuonTra);
             
             return pstmt.executeUpdate() > 0;
             
         } catch(SQLException e) {
             e.printStackTrace();
             return false;
         }
    }
}
