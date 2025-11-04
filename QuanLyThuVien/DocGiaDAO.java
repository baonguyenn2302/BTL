// File: DocGiaDAO.java (ĐÃ CẬP NHẬT)
package QuanLyThuVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
public class DocGiaDAO {

    /**
     * 1. Lấy TẤT CẢ Độc Giả
     * @return Danh sách Độc Giả
     */
    public List<DocGia> getAllDocGia() {
        List<DocGia> danhSach = new ArrayList<>();
        
        String sql = "SELECT * FROM DOCGIA WHERE isArchived = 0 ORDER BY maDocGia"; 
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                DocGia dg = new DocGia(
                    rs.getString("maDocGia"),
                    rs.getString("hoTen"),
                    rs.getDate("ngaySinh"), // Lấy về kiểu java.sql.Date
                    rs.getString("email"),
                    rs.getString("diaChi"),
                    rs.getString("sdt"),
                    rs.getString("duongDanAnh"),
                    rs.getBoolean("blocked"),
                    rs.getBoolean("isArchived")
                );
                danhSach.add(dg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach; // Trả về danh sách (có thể rỗng)
    }
    
    public boolean checkMaDocGiaExists(String maDocGia) {
        String sql = "SELECT COUNT(*) FROM DOCGIA WHERE maDocGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return false;
    }
    
    public boolean insertDocGia(DocGia docGia) {
        String sql = "INSERT INTO DOCGIA (maDocGia, hoTen, ngaySinh, email, diaChi, sdt, duongDanAnh, blocked, isArchived) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, docGia.getMaDocGia());
            ps.setString(2, docGia.getHoTen());
            
            if (docGia.getNgaySinh() != null) {
                ps.setDate(3, new java.sql.Date(docGia.getNgaySinh().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            
            ps.setString(4, docGia.getEmail());
            ps.setString(5, docGia.getDiaChi());
            ps.setString(6, docGia.getSdt());
            ps.setString(7, docGia.getDuongDanAnh());
            ps.setBoolean(8, docGia.isBlocked()); 
            ps.setBoolean(9, docGia.isArchived());
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    public DocGia getDocGiaByMa(String maDocGia) {
        String sql = "SELECT * FROM DOCGIA WHERE maDocGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            rs = ps.executeQuery();

            if (rs.next()) {
                // <<< SỬA LỖI LOGIC QUAN TRỌNG TẠI ĐÂY >>>
                // Đảm bảo thứ tự này khớp 100% với file DocGia.java
                // (email, diaChi, sdt)
                return new DocGia(
                    rs.getString("maDocGia"),     //
                    rs.getString("hoTen"),        //
                    rs.getDate("ngaySinh"),     //
                    rs.getString("email"),      //
                    rs.getString("diaChi"),     //
                    rs.getString("sdt"),        //
                    rs.getString("duongDanAnh"), //
                    rs.getBoolean("blocked"),   //
                    rs.getBoolean("isArchived") //
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return null; // Không tìm thấy
    }

    /**
     * Cập nhật thông tin một độc giả trong CSDL.
     * @param docGia Đối tượng DocGia chứa thông tin đã cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateDocGia(DocGia docGia) {
        // <<< SỬA LỖI LOGIC: Thêm 'isArchived = ?' vào câu SQL
        String sql = "UPDATE DOCGIA SET hoTen = ?, ngaySinh = ?, email = ?, diaChi = ?, sdt = ?, duongDanAnh = ?, blocked = ?, isArchived = ? "
                   + "WHERE maDocGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, docGia.getHoTen());
            
            if (docGia.getNgaySinh() != null) {
                ps.setDate(2, new java.sql.Date(docGia.getNgaySinh().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            
            ps.setString(3, docGia.getEmail());
            ps.setString(4, docGia.getDiaChi());
            ps.setString(5, docGia.getSdt());
            ps.setString(6, docGia.getDuongDanAnh());
            ps.setBoolean(7, docGia.isBlocked());
            ps.setBoolean(8, docGia.isArchived()); // <<< SỬA LỖI LOGIC
            ps.setString(9, docGia.getMaDocGia()); // <<< SỬA LỖI LOGIC (Chỉ số 9)
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    public boolean softDeleteDocGia(String maDocGia) {
        String sql = "UPDATE DOCGIA SET isArchived = 1 WHERE maDocGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0); 

        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    public boolean setBlockStatus(String maDocGia, boolean blockStatus) {
        String sql = "UPDATE DOCGIA SET blocked = ? WHERE maDocGia = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, blockStatus);
            ps.setString(2, maDocGia);
            
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResource(ps, conn);
        }
    }
    
    // ==================================================
    // <<< HÀM MỚI: TÌM KIẾM ĐỘC GIẢ >>>
    // ==================================================
    public List<DocGia> searchDocGia(String mode, String value) {
        List<DocGia> danhSach = new ArrayList<>();
        String condition = "";
        List<Object> params = new ArrayList<>();

        // 1. Xây dựng điều kiện WHERE (chỉ tìm trong độc giả chưa bị xóa)
        switch (mode) {
            case "Mã độc giả":
                condition = "maDocGia LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Họ tên":
                condition = "hoTen LIKE ?";
                params.add("%" + value + "%");
                break;
            case "SĐT":
                condition = "sdt LIKE ?";
                params.add("%" + value + "%");
                break;
            case "Trạng thái":
                if (value.equalsIgnoreCase("Hoạt động")) {
                    condition = "blocked = 0";
                } else if (value.equalsIgnoreCase("Đã khóa")) {
                    condition = "blocked = 1";
                } else {
                    return danhSach; // Nếu nhập "abc", không trả về gì
                }
                break;
            default:
                return danhSach; // Mode không hợp lệ
        }

        // 2. Câu SQL
        String sql = "SELECT * FROM DOCGIA WHERE isArchived = 0 AND " + condition + " ORDER BY maDocGia";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            
            // Gán các tham số (nếu có)
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            rs = ps.executeQuery();
            
            // 3. Đọc kết quả (giống getAllDocGia)
            while (rs.next()) {
                DocGia dg = new DocGia(
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
                danhSach.add(dg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResource(rs, ps, conn);
        }
        return danhSach;
    }
    public int getSoLuongDocGiaBiKhoa() {
        int count = 0;
        // Chỉ đếm độc giả chưa bị xóa mềm (isArchived = 0)
        String sql = "SELECT COUNT(*) FROM DOCGIA WHERE blocked = 1 AND isArchived = 0";
        
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
}