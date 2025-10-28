package QuanLyThuVien;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Model (POJO) đại diện cho đối tượng Sách.
 * ĐÃ TÁI CẤU TRÚC: Hỗ trợ nhiều tác giả (List<TacGia>)
 */
public class Sach {

    // Thuộc tính
    private String maSach;
    private String tenSach;
    // private String maTacGia; // <<< ĐÃ XÓA
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuong;
    private String moTa; 
    private String duongDanAnh; 
    private Date ngayThem; 
    private String viTri; 

    // === THUỘC TÍNH MỚI THAY THẾ ===
    private List<TacGia> danhSachTacGia;

    // --- Constructors ---
    public Sach() {
        // Khởi tạo danh sách rỗng để tránh lỗi NullPointerException
        this.danhSachTacGia = new ArrayList<>();
    }

    // Constructor đầy đủ đã được cập nhật
    public Sach(String maSach, String tenSach, String nhaXuatBan,
                int namXuatBan, int soLuong, String moTa, String duongDanAnh,
                Date ngayThem, String viTri, List<TacGia> danhSachTacGia) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
        this.soLuong = soLuong;
        this.moTa = moTa;
        this.duongDanAnh = duongDanAnh;
        this.ngayThem = ngayThem;
        this.viTri = viTri;
        this.danhSachTacGia = (danhSachTacGia != null) ? danhSachTacGia : new ArrayList<>();
    }

    // --- Getters and Setters (đã cập nhật) ---

    public String getMaSach() { 
        return maSach; 
    }
    public void setMaSach(String maSach) { 
        this.maSach = maSach; 
    }
    public String getTenSach() {
        return tenSach;
    }
    public void setTenSach(String tenSach) {
        this.tenSach = tenSach; }
    
    // <<< ĐÃ XÓA getMaTacGia() và setMaTacGia() >>>

    public String getNhaXuatBan() { 
        return nhaXuatBan;
    }
    public void setNhaXuatBan(String nhaXuatBan) { 
        this.nhaXuatBan = nhaXuatBan; }
    public int getNamXuatBan() { return namXuatBan;
    }
    public void setNamXuatBan(int namXuatBan) {
        this.namXuatBan = namXuatBan; 
    }
    public int getSoLuong() { 
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong; }
    public String getMoTa() { return moTa;
    }
    public void setMoTa(String moTa) { 
        this.moTa = moTa; 
    }
    public String getDuongDanAnh() { 
        return duongDanAnh; 
    }
    public void setDuongDanAnh(String duongDanAnh) { 
        this.duongDanAnh = duongDanAnh; 
    }
    public Date getNgayThem() { 
        return ngayThem;
    }
    public void setNgayThem(Date ngayThem) { 
        this.ngayThem = ngayThem;
    }
    public String getViTri() { 
        return viTri;
    }
    public void setViTri(String viTri) {
        this.viTri = viTri; 
    }

    // === GETTER/SETTER MỚI CHO TÁC GIẢ ===
    public List<TacGia> getDanhSachTacGia() {
        return danhSachTacGia;
    }

    public void setDanhSachTacGia(List<TacGia> danhSachTacGia) {
        this.danhSachTacGia = danhSachTacGia;
    }
    
    // === HÀM TIỆN ÍCH MỚI ===
    
    /**
     * Lấy tên các tác giả dưới dạng một chuỗi, phân tách bằng dấu phẩy
     */
    public String getTenTacGiaDisplay() {
        if (danhSachTacGia == null || danhSachTacGia.isEmpty()) {
            return "N/A";
        }
        // Dùng Java Stream để lấy tên và nối chuỗi
        return danhSachTacGia.stream()
                             .map(TacGia::getTenTacGia) // Lấy tên của mỗi tác giả
                             .collect(Collectors.joining(", ")); // Nối lại
    }

    // Phương thức tiện ích (vẫn giữ)
    public String getTrangThai() { 
        return (this.soLuong > 0) ? "Còn sách" : "Hết sách"; 
    }

    @Override
    public String toString() {
        return "Sach{" +
               "maSach='" + maSach + '\'' +
               ", tenSach='" + tenSach + '\'' +
               ", tacGia='" + getTenTacGiaDisplay() + '\'' + // Cập nhật
               ", soLuong=" + soLuong +
               '}';
    }
}