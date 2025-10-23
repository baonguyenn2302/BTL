package QuanLyThuVien;

import java.util.Date; // Import Date

/**
 * Lớp Model (POJO) đại diện cho đối tượng Sách (Đã cập nhật đầy đủ)
 */
public class Sach {

    // Thuộc tính cũ (9)
    private String maSach;
    private String tenSach;
    private String tacGia;
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuong;
    private String moTa;
    private String duongDanAnh;
    private String duongDanXemTruoc;

    // Thuộc tính mới (3)
    private Date ngayThem;
    private int luotXem;
    private int luotTai;

    // Constructor rỗng
    public Sach() {
        this.luotXem = 0;
        this.luotTai = 0;
    }

    // Constructor đầy đủ (12 thuộc tính)
    public Sach(String maSach, String tenSach, String tacGia, String nhaXuatBan,
                int namXuatBan, int soLuong, String moTa, String duongDanAnh, String duongDanXemTruoc,
                Date ngayThem, int luotXem, int luotTai) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
        this.soLuong = soLuong;
        this.moTa = moTa;
        this.duongDanAnh = duongDanAnh;
        this.duongDanXemTruoc = duongDanXemTruoc;
        this.ngayThem = ngayThem;
        this.luotXem = luotXem;
        this.luotTai = luotTai;
    }

    // --- Getters and Setters (cho tất cả 12 thuộc tính) ---

    public String getMaSach() { return maSach; }
    public void setMaSach(String maSach) { this.maSach = maSach; }
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }
    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }
    public int getNamXuatBan() { return namXuatBan; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getDuongDanAnh() { return duongDanAnh; }
    public void setDuongDanAnh(String duongDanAnh) { this.duongDanAnh = duongDanAnh; }
    public String getDuongDanXemTruoc() { return duongDanXemTruoc; }
    public void setDuongDanXemTruoc(String duongDanXemTruoc) { this.duongDanXemTruoc = duongDanXemTruoc; }
    public Date getNgayThem() { return ngayThem; }
    public void setNgayThem(Date ngayThem) { this.ngayThem = ngayThem; }
    public int getLuotXem() { return luotXem; }
    public void setLuotXem(int luotXem) { this.luotXem = luotXem; }
    public int getLuotTai() { return luotTai; }
    public void setLuotTai(int luotTai) { this.luotTai = luotTai; }

    // Phương thức tiện ích
    public String getTrangThai() { return (this.soLuong > 0) ? "Còn sách" : "Hết sách"; }

    @Override
    public String toString() {
        return "Sach{" + "maSach='" + maSach + '\'' + ", tenSach='" + tenSach + '\'' + ", luotXem=" + luotXem + ", luotTai=" + luotTai + '}';
    }
}