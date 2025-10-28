package QuanLyThuVien;

/**
 * Lớp Model (POJO) đại diện cho đối tượng Tác Giả.
 */
public class TacGia {

    private String maTacGia;
    private String tenTacGia;
    private String email;
    private String sdt;
    private String trinhDoChuyenMon;
    private String chucDanh;

    // Constructors
    public TacGia() {
    }

    public TacGia(String maTacGia, String tenTacGia, String email, String sdt, String trinhDoChuyenMon, String chucDanh) {
        this.maTacGia = maTacGia;
        this.tenTacGia = tenTacGia;
        this.email = email;
        this.sdt = sdt;
        this.trinhDoChuyenMon = trinhDoChuyenMon;
        this.chucDanh = chucDanh;
    }

    // Getters and Setters
    public String getMaTacGia() {
        return maTacGia;
    }

    public void setMaTacGia(String maTacGia) {
        this.maTacGia = maTacGia;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTrinhDoChuyenMon() {
        return trinhDoChuyenMon;
    }

    public void setTrinhDoChuyenMon(String trinhDoChuyenMon) {
        this.trinhDoChuyenMon = trinhDoChuyenMon;
    }

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
    }

    @Override
    public String toString() {
        return this.tenTacGia; // Thường chỉ cần hiển thị tên là đủ
    }
}
