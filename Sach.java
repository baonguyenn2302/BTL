/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baonguyenn;

/**
 *
 * @author baonguyenn
 */
public class Sach {
    private String maSach;
    private String tenSach;
    private String tacGia;
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuongTong;
    private int soLuongConLai;
    private String theLoai;
    
    public Sach() {
    }

    public Sach(String maSach, String tenSach, String tacGia, String nhaXuatBan, int namXuatBan, int soLuongTong, int soLuongConLai, String theLoai) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
        this.soLuongTong = soLuongTong;
        this.soLuongConLai = soLuongConLai;
        this.theLoai = theLoai;
    }

    public Sach(String maSach, String tenSach, String tacGia, int soLuongTong) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.soLuongTong = soLuongTong;
        this.soLuongConLai = soLuongTong;
    }

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
        this.tenSach = tenSach;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public int getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(int namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public int getSoLuongTong() {
        return soLuongTong;
    }

    public void setSoLuongTong(int soLuongTong) {
        this.soLuongTong = soLuongTong;
    }

    public int getSoLuongConLai() {
        return soLuongConLai;
    }

    public void setSoLuongConLai(int soLuongConLai) {
        this.soLuongConLai = soLuongConLai;
    }

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }
    
    @Override
    public String toString() {
        return "Thông tin: \n" +
                "- Mã sách: " + maSach + "\n" +
                "- Tên sách: "+ tenSach +  "\n" +
                "- Tác giả: " + tacGia + "\n" +
                "- Số lượng còn lại: " + soLuongConLai + "\n" +
                "- Nhà xuất bản: " + nhaXuatBan + "\n" +
                "- Năm xuát bản: " + namXuatBan + "\n" +
                "- Thể loại: " + theLoai + '\n';
    }
    
    public boolean giamSoLuong() {
        if (this.soLuongConLai > 0) {
            this.soLuongConLai--;
            return true;
        }
        return false;
    }
    
    public void tangSoLuong() {
        if (this.soLuongConLai < this.soLuongTong) {
            this.soLuongConLai++;
        }
    }
}

