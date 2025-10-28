package QuanLyThuVien;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Model (POJO) đại diện cho một Bộ Sưu Tập.
 */
public class BoSuuTap {

    private int maBoSuuTap;
    private String tenBoSuuTap;
    private String moTa;
    private String duongDanAnh; // <<< THUỘC TÍNH MỚI

    // Chúng ta có thể thêm danh sách sách ở đây nếu cần tải tất cả cùng lúc
    // private List<Sach> dsSach;

    public BoSuuTap() {
        // this.dsSach = new ArrayList<>();
    }

    // Getters and Setters
    public int getMaBoSuuTap() {
        return maBoSuuTap;
    }

    public void setMaBoSuuTap(int maBoSuuTap) {
        this.maBoSuuTap = maBoSuuTap;
    }

    public String getTenBoSuuTap() {
        return tenBoSuuTap;
    }

    public void setTenBoSuuTap(String tenBoSuuTap) {
        this.tenBoSuuTap = tenBoSuuTap;
    }

    public String getMoTa() {
        return moTa;
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
    
    // (Tùy chọn: Getter/Setter cho dsSach)

    /**
     * Ghi đè hàm toString() để JList hiển thị tên
     */
    @Override
    public String toString() {
        return this.tenBoSuuTap; 
    }
}
