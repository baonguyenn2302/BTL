/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baonguyenn;

/**
 *
 * @author baonguyenn
 */
public class User {
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // Cán bộ thư viện hoặc chỉ là đọc giả
    private String maLienKet; // hyperlink
    
    public User() {
    }

    public User(String tenDangNhap, String matKhau, String vaiTro, String maLienKet) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.maLienKet = maLienKet;
    }

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

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getMaLienKet() {
        return maLienKet;
    }

    public void setMaLienKet(String maLienKet) {
        this.maLienKet = maLienKet;
    }
    
    @Override
    public String toString() {
        return "Thông tin tài khoản: \n" +
                "- Tên đăng nhập: " + tenDangNhap + '\n' +
                "- Vai trò: " + vaiTro + '\n' +
                "- Mã liên kết: " + maLienKet + '\n';
    }
}
