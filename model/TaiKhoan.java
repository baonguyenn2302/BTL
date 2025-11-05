package model;

public class TaiKhoan {
    private int maTaiKhoan; // <<< THÊM
    private String tenDangNhap;
    private String matKhau; 
    private String tenNguoiDung;
    private String email;
    private String sdt;
    private String duongDanAnh;
    private String quyen;

    // <<< SỬA HÀM KHỞI TẠO >>>
    public TaiKhoan(int maTaiKhoan, String tenDangNhap, String matKhau, String tenNguoiDung, 
                    String email, String sdt, String duongDanAnh, String quyen) {
        this.maTaiKhoan = maTaiKhoan; // <<< THÊM
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.tenNguoiDung = tenNguoiDung;
        this.email = email;
        this.sdt = sdt;
        this.duongDanAnh = duongDanAnh;
        this.quyen = quyen;
    }

    // <<< THÊM GETTER/SETTER CHO maTaiKhoan >>>
    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }
    
    // (Các getter/setter còn lại giữ nguyên)
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
    
    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
    
    public String getTenNguoiDung() {
        return tenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
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
    
    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }
    
    public String getQuyen() {
        return quyen;
    }

    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }
}