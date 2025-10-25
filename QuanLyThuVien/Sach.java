package QuanLyThuVien;

import java.util.Date; // Import Date

/**
 * Lớp Model (POJO) đại diện cho đối tượng Sách.
 * Đã cập nhật để sử dụng maTacGia thay vì tên tác giả trực tiếp.
 */
public class Sach {

    // Thuộc tính
    private String maSach;
    private String tenSach;
    private String maTacGia; // <<< THAY ĐỔI: Giờ là Mã Tác Giả (Khóa ngoại)
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuong;
    private String moTa;
    private String duongDanAnh;
    private String duongDanXemTruoc;
    private Date ngayThem;
    private int luotXem;
    private int luotTai;

    // --- Constructors ---

    /**
     * Constructor rỗng.
     */
    public Sach() {
        this.luotXem = 0;
        this.luotTai = 0;
    }

    /**
     * Constructor đầy đủ (với maTacGia).
     * @param maSach Mã sách
     * @param tenSach Tên sách
     * @param maTacGia Mã tác giả (tham chiếu đến bảng TACGIA) // <<< THAY ĐỔI
     * @param nhaXuatBan Nhà xuất bản
     * @param namXuatBan Năm xuất bản
     * @param soLuong Số lượng
     * @param moTa Mô tả
     * @param duongDanAnh Đường dẫn ảnh bìa
     * @param duongDanXemTruoc Đường dẫn file xem trước
     * @param ngayThem Ngày thêm vào hệ thống
     * @param luotXem Số lượt xem
     * @param luotTai Số lượt tải
     */
    public Sach(String maSach, String tenSach, String maTacGia, String nhaXuatBan, // <<< THAY ĐỔI
                int namXuatBan, int soLuong, String moTa, String duongDanAnh, String duongDanXemTruoc,
                Date ngayThem, int luotXem, int luotTai) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.maTacGia = maTacGia; // <<< THAY ĐỔI
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

    // --- Getters and Setters ---

    public String getMaSach() { return maSach; }
    public void setMaSach(String maSach) { this.maSach = maSach; }
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    // <<< THAY ĐỔI GETTER/SETTER CHO TÁC GIẢ >>>
    public String getMaTacGia() { return maTacGia; }
    public void setMaTacGia(String maTacGia) { this.maTacGia = maTacGia; }
    // <<< KẾT THÚC THAY ĐỔI >>>

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
        return "Sach{" +
               "maSach='" + maSach + '\'' +
               ", tenSach='" + tenSach + '\'' +
               ", maTacGia='" + maTacGia + '\'' + // <<< THAY ĐỔI
               ", nhaXuatBan='" + nhaXuatBan + '\'' +
               ", namXuatBan=" + namXuatBan +
               ", soLuong=" + soLuong +
               ", luotXem=" + luotXem +
               ", luotTai=" + luotTai +
               '}';
    }
}
