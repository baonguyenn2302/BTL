/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baonguyenn;

import java.util.Date;
/**
 *
 * @author baonguyenn
 */
public class DocGia {
    private String maDocGia;
    private String hoTen;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private boolean blocked; // Trang thai bi khoa: true = blocked, false = active
    
    public DocGia() {
    }

    public DocGia(String maDocGia, String hoTen, Date ngaySinh, String email, String sdt, boolean blocked) {
        this.maDocGia = maDocGia;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.sdt = sdt;
        this.blocked = blocked;
    }

    public DocGia(String maDocGia, String hoTen, String sdt, boolean blocked) {
        this.maDocGia = maDocGia;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.blocked = false;
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    
    @Override
    public String toString() {
        String trangThai = this.blocked ? "BLOCKED" : "ACTIVE";
        return "Thông tin đọc giả: \n" +
                "- Mã đọc giả: "+ maDocGia + '\n' +
                "- Họ và tên: "+ hoTen + '\n' + 
                "- Số điện thoại: "+ sdt + '\n';
    }
}
