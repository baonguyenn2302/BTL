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
public class PhieuMuon {
    private String maPhieu;
    private String maDocGia;
    private Date ngayMuon;
    private Date hanTra;
    private Date ngayTraThucTe;
    private int tienPhat; // số tiền phạt nếu trả quá hạn
    private String trangThai; // bao gồm Đang mượn, Đã trả, Quá hạn
    
    public PhieuMuon() {
    }

    // Khi lập phiếu
    public PhieuMuon(String maPhieu, String maDocGia, Date ngayMuon, Date hanTra) {
        this.maPhieu = maPhieu;
        this.maDocGia = maDocGia;
        this.ngayMuon = ngayMuon;
        this.hanTra = hanTra;
        this.trangThai = "Đang mượn";
        this.tienPhat = 0;
        this.ngayTraThucTe = null;
    }

    public PhieuMuon(String maPhieu, String maDocGia, Date ngayMuon, Date hanTra, Date ngayTraThucTe, int tienPhat, String trangThai) {
        this.maPhieu = maPhieu;
        this.maDocGia = maDocGia;
        this.ngayMuon = ngayMuon;
        this.hanTra = hanTra;
        this.ngayTraThucTe = ngayTraThucTe;
        this.tienPhat = tienPhat;
        this.trangThai = trangThai;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getMaDocGia() {
        return maDocGia;
    }

    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public Date getHanTra() {
        return hanTra;
    }

    public void setHanTra(Date hanTra) {
        this.hanTra = hanTra;
    }

    public Date getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(Date ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    public int getTienPhat() {
        return tienPhat;
    }

    public void setTienPhat(int tienPhat) {
        this.tienPhat = tienPhat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Thông tin phiếu mượn: \n" +
                "- Mã phiếu mượn: " + maPhieu + '\n' +
                "- Mã đọc giả: " + maDocGia + '\n' +
                "- Ngày mượn :" + ngayMuon + '\n' +
                "- Hạn trả: " + hanTra + '\n' +
                "- Trạng thái: " + trangThai + '\n' +
                "- Tiền phạt :" + tienPhat + '\n';
    }
}
