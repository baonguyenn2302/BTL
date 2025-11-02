// TẠO FILE MỚI: MuonTraDAO.java
package QuanLyThuVien;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MuonTraDAO {
    
    public List<MuonTra> getAllMuonTra() {
        List<MuonTra> danhSach = new ArrayList<>();
        
        // Câu SQL JOIN 3 bảng: MUONTRA, DOCGIA, SACH
        // để lấy tên của Độc giả và Sách
        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "ORDER BY mt.ngayMuon DESC"; // Sắp xếp mới nhất lên đầu

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                // 1. Tạo đối tượng Độc Giả (chỉ cần mã và tên)
                DocGia dg = new DocGia();
                dg.setMaDocGia(rs.getString("maDocGia"));
                dg.setHoTen(rs.getString("hoTen"));
                
                // 2. Tạo đối tượng Sách (chỉ cần mã và tên)
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                
                // 3. Tạo đối tượng MuonTra
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                
                // Gán 2 đối tượng trên vào
                mt.setDocGia(dg);
                mt.setSach(s);
                
                // Thêm vào danh sách
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach; // Trả về danh sách
    }

    /**
     * HÀM QUAN TRỌNG NHẤT: Thêm một lô phiếu mượn (cho nhiều sách)
     * Sử dụng Transaction để đảm bảo toàn vẹn dữ liệu.
     * 1. INSERT vào bảng MUONTRA
     * 2. UPDATE bảng SACH (giảm số lượng conLai)
     */
    public boolean themMoiPhieuMuon(DocGia docGia, List<Sach> danhSachSach, Date ngayMuon, Date ngayHenTra, String loaiMuon) {
        
        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        String sqlInsert = "INSERT INTO MUONTRA (maDocGia, maSach, ngayMuon, ngayHenTra, loaiMuon, trangThai) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";
        
        String sqlUpdate = "UPDATE SACH SET conLai = conLai - 1 WHERE maSach = ?";

        try {
            conn = DatabaseConnection.getConnection();
            // BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);

            // Chuẩn bị 2 câu lệnh
            psInsert = conn.prepareStatement(sqlInsert);
            psUpdate = conn.prepareStatement(sqlUpdate);

            // Lặp qua từng cuốn sách trong giỏ hàng
            for (Sach s : danhSachSach) {
                // 1. Thêm vào bảng MUONTRA
                psInsert.setString(1, docGia.getMaDocGia());
                psInsert.setString(2, s.getMaSach());
                psInsert.setTimestamp(3, new java.sql.Timestamp(ngayMuon.getTime()));
                psInsert.setTimestamp(4, new java.sql.Timestamp(ngayHenTra.getTime()));
                psInsert.setString(5, loaiMuon);
                psInsert.setString(6, "Đang mượn"); // Trạng thái ban đầu
                psInsert.addBatch(); // Thêm vào lô

                // 2. Cập nhật (trừ 1) bảng SACH
                psUpdate.setString(1, s.getMaSach());
                psUpdate.addBatch(); // Thêm vào lô
            }

            // Thực thi cả 2 lô
            psInsert.executeBatch();
            psUpdate.executeBatch();

            // KẾT THÚC TRANSACTION (thành công)
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu có lỗi, ROLLBACK (hủy bỏ) mọi thay đổi
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return false;
        } finally {
            // Trả lại chế độ auto-commit
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Đóng tất cả resource
            if (psUpdate != null) try { psUpdate.close(); } catch (SQLException e) { /* Bỏ qua */ }
            DatabaseConnection.closeResource(psInsert, conn);
        }
    }
    public boolean danhDauDaTra(int maMuonTra, String maSach) {
        Connection conn = null;
        PreparedStatement psUpdateMuonTra = null;
        PreparedStatement psUpdateSach = null;

        String sqlUpdateMuonTra = "UPDATE MUONTRA SET ngayTraThucTe = GETDATE(), trangThai = N'Đã trả' "
                               + "WHERE maMuonTra = ?";
        
        String sqlUpdateSach = "UPDATE SACH SET conLai = conLai + 1 WHERE maSach = ?";

        try {
            conn = DatabaseConnection.getConnection();
            // BẮT ĐẦU TRANSACTION
            conn.setAutoCommit(false);

            // 1. Cập nhật bảng MUONTRA
            psUpdateMuonTra = conn.prepareStatement(sqlUpdateMuonTra);
            psUpdateMuonTra.setInt(1, maMuonTra);
            psUpdateMuonTra.executeUpdate();

            // 2. Cập nhật (cộng 1) bảng SACH
            psUpdateSach = conn.prepareStatement(sqlUpdateSach);
            psUpdateSach.setString(1, maSach);
            psUpdateSach.executeUpdate();
            
            // KẾT THÚC TRANSACTION (thành công)
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu có lỗi, ROLLBACK (hủy bỏ) mọi thay đổi
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return false;
        } finally {
            // Trả lại chế độ auto-commit
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Đóng tất cả resource
            if (psUpdateMuonTra != null) try { psUpdateMuonTra.close(); } catch (SQLException e) { /* Bỏ qua */ }
            DatabaseConnection.closeResource(psUpdateSach, conn);
        }
    }
    public boolean giaHanPhieuMuon(int maMuonTra, Date ngayHenTraMoi) {
        // Cập nhật ngày hẹn trả VÀ đặt lại trạng thái là 'Đang mượn'
        // (phòng trường hợp phiếu bị 'Quá hạn')
        String sql = "UPDATE MUONTRA SET ngayHenTra = ?, trangThai = N'Đang mượn' "
                   + "WHERE maMuonTra = ? AND ngayTraThucTe IS NULL";
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            // Dùng setTimestamp để bao gồm cả giờ:phút
            ps.setTimestamp(1, new java.sql.Timestamp(ngayHenTraMoi.getTime()));
            ps.setInt(2, maMuonTra);
            
            int rowsAffected = ps.executeUpdate();
            
            // Trả về true nếu có 1 hàng bị ảnh hưởng (tức là update thành công)
            return (rowsAffected > 0); 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    public MuonTra getChiTietMuonTra(int maMuonTra) {
        MuonTra mt = null;
        
        String sql = "SELECT mt.*, "
                   + "       dg.hoTen, dg.email AS dg_email, dg.sdt AS dg_sdt, " // Chi tiết Độc Giả
                   + "       s.tenSach, s.nhaXuatBan, s.namXuatBan, s.viTri, " // Chi tiết Sách
                   + "       tg.maTacGia, tg.tenTacGia " // Chi tiết Tác Giả
                   + "FROM MUONTRA mt "
                   + "LEFT JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "LEFT JOIN SACH s ON mt.maSach = s.maSach "
                   + "LEFT JOIN SACH_TACGIA st ON s.maSach = st.maSach "
                   + "LEFT JOIN TACGIA tg ON st.maTacGia = tg.maTacGia "
                   + "WHERE mt.maMuonTra = ?"; // Lọc theo đúng 1 mã phiếu

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maMuonTra);
            rs = ps.executeQuery();

            while (rs.next()) {
                // Nếu là hàng đầu tiên, tạo các đối tượng chính
                if (mt == null) {
                    // 1. Tạo Độc Giả (đầy đủ)
                    DocGia dg = new DocGia();
                    dg.setMaDocGia(rs.getString("maDocGia"));
                    dg.setHoTen(rs.getString("hoTen"));
                    dg.setEmail(rs.getString("dg_email"));
                    dg.setSdt(rs.getString("dg_sdt"));
                    
                    // 2. Tạo Sách (đầy đủ)
                    Sach s = new Sach();
                    s.setMaSach(rs.getString("maSach"));
                    s.setTenSach(rs.getString("tenSach"));
                    s.setNhaXuatBan(rs.getString("nhaXuatBan"));
                    s.setNamXuatBan(rs.getInt("namXuatBan"));
                    s.setViTri(rs.getString("viTri"));
                    // (Hàm khởi tạo Sach đã tạo new ArrayList<>() cho danhSachTacGia)
                    
                    // 3. Tạo Phiếu Mượn Trả
                    mt = new MuonTra();
                    mt.setMaMuonTra(rs.getInt("maMuonTra"));
                    mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                    mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                    mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                    mt.setLoaiMuon(rs.getString("loaiMuon"));
                    // Lấy trạng thái đã lưu trong CSDL
                    mt.setTrangThai(rs.getString("trangThai")); 
                    
                    mt.setDocGia(dg);
                    mt.setSach(s);
                }
                
                // 4. Thêm Tác Giả vào Sách
                // (Nếu sách có nhiều tác giả, vòng lặp while sẽ xử lý các hàng tiếp theo)
                String maTacGia = rs.getString("maTacGia");
                if (maTacGia != null && mt != null) {
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(maTacGia);
                    tg.setTenTacGia(rs.getString("tenTacGia"));
                    
                    // Tránh thêm trùng lặp nếu tác giả đã tồn tại
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
        return mt; // Trả về đối tượng MuonTra đầy đủ chi tiết
    }
    public List<MuonTra> getPhieuMuonGroup(String maDocGia, Date ngayMuon) {
        List<MuonTra> phieuMuonGroup = new ArrayList<>();
        DocGia docGia = null; // Dùng chung cho tất cả phiếu

        // Lấy chi tiết phiếu mượn, chi tiết độc giả, và chi tiết sách
        String sql = "SELECT mt.*, dg.*, s.maSach, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "LEFT JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "LEFT JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.maDocGia = ? AND mt.ngayMuon = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            // So sánh Timestamp (bao gồm cả giờ, phút, giây)
            ps.setTimestamp(2, new java.sql.Timestamp(ngayMuon.getTime()));
            
            rs = ps.executeQuery();

            while (rs.next()) {
                // 1. Tạo đối tượng Độc Giả (chỉ 1 lần)
                if (docGia == null) {
                    docGia = new DocGia(
                        rs.getString("maDocGia"),
                        rs.getString("hoTen"),
                        rs.getDate("ngaySinh"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("sdt"),
                        rs.getString("duongDanAnh"),
                        rs.getBoolean("blocked"),
                        rs.getBoolean("isArchived")
                    );
                }
                
                // 2. Tạo đối tượng Sách (tóm tắt)
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));

                // 3. Tạo đối tượng MuonTra
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                mt.setTrangThai(rs.getString("trangThai"));
                
                mt.setDocGia(docGia);
                mt.setSach(s);
                
                phieuMuonGroup.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return phieuMuonGroup;
    }
    public boolean deleteMuonTra(int maMuonTra) {
        String sql = "DELETE FROM MUONTRA WHERE maMuonTra = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, maMuonTra);
            
            int rowsAffected = ps.executeUpdate();
            
            return (rowsAffected > 0); // Trả về true nếu xóa thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Xóa thất bại
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    public List<MuonTra> searchMuonTra(String mode, String value) {
        List<MuonTra> danhSach = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String condition = "";

        // 1. Xây dựng điều kiện WHERE và tham số
        switch (mode) {
            case "Mã mượn":
                condition = "mt.maMuonTra = ?";
                try {
                    params.add(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return danhSach; // Nếu nhập không phải số, trả về rỗng
                }
                break;
            case "Độc giả":
                condition = "dg.hoTen LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Sách":
                condition = "s.tenSach LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Ngày mượn":
                // 103 = dd/mm/yyyy
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
                // Tìm trong CSDL (chỉ 'Đang mượn' hoặc 'Đã trả')
                // 'Quá hạn' được tính ở Java, nên tìm 'Quá hạn' sẽ ra 'Đang mượn'
                String searchValue = value;
                if (value.equalsIgnoreCase("Quá hạn")) {
                    searchValue = "Đang mượn";
                }
                condition = "mt.trangThai LIKE ?";
                params.add("%" + searchValue + "%");
                break;
            case "Loại mượn":
                condition = "mt.loaiMuon LIKE ?";
                params.add("%" + value + "%");
                break;
            default:
                return danhSach; // Mode không hợp lệ
        }

        // 2. Câu SQL cơ sở (giống getAllMuonTra)
        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGia "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE " + condition // Thêm điều kiện tìm kiếm
                   + " ORDER BY mt.ngayMuon DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);

            // Gán các tham số
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            rs = ps.executeQuery();

            // 3. Đọc kết quả (giống getAllMuonTra)
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
                mt.setTrangThai(rs.getString("trangThai")); // Lấy từ DB
                
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
    public List<MuonTra> getLichSuMuonTraByDocGia(String maDocGia) {
        List<MuonTra> danhSach = new ArrayList<>();
        
        // Chỉ JOIN với SACH để lấy tên sách
        String sql = "SELECT mt.*, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.maDocGia = ? " // Lọc theo độc giả
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
                // 1. Tạo đối tượng Sách (tóm tắt)
                Sach s = new Sach();
                s.setMaSach(rs.getString("maSach"));
                s.setTenSach(rs.getString("tenSach"));
                
                // 2. Tạo đối tượng MuonTra
                MuonTra mt = new MuonTra();
                mt.setMaMuonTra(rs.getInt("maMuonTra"));
                mt.setNgayMuon(rs.getTimestamp("ngayMuon"));
                mt.setNgayHenTra(rs.getTimestamp("ngayHenTra"));
                mt.setNgayTraThucTe(rs.getTimestamp("ngayTraThucTe"));
                mt.setLoaiMuon(rs.getString("loaiMuon"));
                mt.setTrangThai(rs.getString("trangThai"));
                
                mt.setSach(s);
                // Không cần set DocGia, vì chúng ta đang lọc theo DocGia
                
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
    public int getSoLuongSachDenHanHomNay() {
        int count = 0;
        // Chỉ tìm sách 'Đang mượn'
        // CAST(... AS DATE) để chỉ so sánh ngày, bỏ qua giờ
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

    /**
     * (HÀM MỚI) Lấy danh sách sách ĐANG MƯỢN và ĐẾN HẠN TRẢ trong hôm nay.
     * @return Danh sách MuonTra
     */
    public List<MuonTra> getDanhSachSachDenHanHomNay() {
        List<MuonTra> danhSach = new ArrayList<>();
        
        // Câu SQL JOIN 3 bảng, giống getAllMuonTra
        // Thêm điều kiện WHERE để lọc
        String sql = "SELECT mt.*, dg.hoTen, s.tenSach "
                   + "FROM MUONTRA mt "
                   + "JOIN DOCGIA dg ON mt.maDocGia = dg.maDocGIA "
                   + "JOIN SACH s ON mt.maSach = s.maSach "
                   + "WHERE mt.trangThai = N'Đang mượn' " //
                   + "AND CAST(mt.ngayHenTra AS DATE) = CAST(GETDATE() AS DATE) "
                   + "ORDER BY mt.ngayHenTra ASC"; // Sắp xếp theo ngày hẹn trả

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // Vòng lặp đọc ResultSet giống hệt getAllMuonTra
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
                mt.setTrangThai(rs.getString("trangThai"));
                
                mt.setDocGia(dg);
                mt.setSach(s);
                
                danhSach.add(mt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach; // Trả về danh sách (có thể rỗng)
    }
}