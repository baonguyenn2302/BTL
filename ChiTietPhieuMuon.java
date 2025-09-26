/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baonguyenn;

/**
 *
 * @author baonguyenn
 */
public class ChiTietPhieuMuon {
    private String maPhieu;
    private String maSach;
    private int soLuongMuon;
    private String ghiChu;
    
    public ChiTietPhieuMuon() {
    }

    public ChiTietPhieuMuon(String maPhieu, String maSach, int soLuongMuon, String ghiChu) {
        this.maPhieu = maPhieu;
        this.maSach = maSach;
        this.soLuongMuon = soLuongMuon;
        this.ghiChu = ghiChu;
    }
    
    public ChiTietPhieuMuon(String maPhieu, String maSach) {
        this.maPhieu = maPhieu;
        this.maSach = maSach;
        this.soLuongMuon = 1; // Để default số lượng mượn là 1
        this.ghiChu = ""; // Để default là không có ghi chú gì cả
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public int getSoLuongMuon() {
        return soLuongMuon;
    }

    public void setSoLuongMuon(int soLuongMuon) {
        this.soLuongMuon = soLuongMuon;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    @Override
    public String toString() {
        return "Chi tiết phiếu mượn: \n" +
                "- Mã phiếu mượn: " + maPhieu + '\n' +
                "- Mã sách mượn " + maSach + '\n' +
                "- Số lượng mượn: " + soLuongMuon + '\n';
    }
}
