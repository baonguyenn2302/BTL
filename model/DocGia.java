package model;

import java.util.Date;

/**
 * Lớp Model (POJO) đại diện cho một Độc Giả.
 * Dựa trên bảng DOCGIA trong CSDL.
 */
public class DocGia {
    private String maDocGia;
    private String hoTen;
    private Date ngaySinh;
    private String email;
    private String diaChi;
    private String sdt;
    private String duongDanAnh; //
    private boolean blocked; //
    private boolean isArchived;
    public DocGia() {
    }

    // Constructor đầy đủ
    public DocGia(String maDocGia, String hoTen, Date ngaySinh, String email, String diaChi, String sdt, String duongDanAnh, boolean blocked, boolean isArchived) { // <<< SỬA
        this.maDocGia = maDocGia;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.duongDanAnh = duongDanAnh;
        this.blocked = blocked;
        this.isArchived = isArchived; // <<< THÊM
    }
    
    // Getters and Setters
    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
    public String getMaDocGia() {
        return maDocGia;
    }

    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    
    // Hàm tiện ích để hiển thị trạng thái lên JTable
    public String getTrangThai() {
        return this.blocked ? "Đã khóa" : "Hoạt động";
    }
}