package QuanLyThuVien;

import java.util.Objects; // Import cần thiết cho equals/hashCode


public class TacGia {

    private String maTacGia; // Mã định danh duy nhất cho tác giả (Khóa chính)
    private String tenTacGia;
    private String email;
    private String sdt; // Số điện thoại (phone)
    private String trinhDoChuyenMon; // Trình độ chuyên môn
    private String chucDanh;         // Chức danh

    // --- Constructors ---

    /**
     * Constructor rỗng.
     */
    public TacGia() {
    }

    /**
     * Constructor với tất cả các thông tin.
     * @param maTacGia Mã tác giả
     * @param tenTacGia Tên tác giả
     * @param email Email
     * @param sdt Số điện thoại
     * @param trinhDoChuyenMon Trình độ
     * @param chucDanh Chức danh
     */
    public TacGia(String maTacGia, String tenTacGia, String email, String sdt, String trinhDoChuyenMon, String chucDanh) {
        this.maTacGia = maTacGia;
        this.tenTacGia = tenTacGia;
        this.email = email;
        this.sdt = sdt;
        this.trinhDoChuyenMon = trinhDoChuyenMon;
        this.chucDanh = chucDanh;
    }

    // --- Getters and Setters ---

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

    // --- toString(), equals(), hashCode() ---

    @Override
    public String toString() {
        return "TacGia{" +
               "maTacGia='" + maTacGia + '\'' +
               ", tenTacGia='" + tenTacGia + '\'' +
               ", email='" + email + '\'' +
               ", sdt='" + sdt + '\'' +
               ", trinhDoChuyenMon='" + trinhDoChuyenMon + '\'' +
               ", chucDanh='" + chucDanh + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TacGia tacGia = (TacGia) o;
        // Chỉ cần so sánh mã tác giả là đủ nếu nó là duy nhất
        return Objects.equals(maTacGia, tacGia.maTacGia);
    }

    @Override
    public int hashCode() {
        // Chỉ cần hash mã tác giả
        return Objects.hash(maTacGia);
    }
}