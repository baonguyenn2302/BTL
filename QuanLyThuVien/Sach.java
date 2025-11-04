/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuanLyThuVien;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author baonguyenn
 */
public class Sach {
    private String maSach;
    private String tenSach;
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuong;
    private String duongDanAnh;
    private Date ngayThem;
    private String viTri;

    private List<TacGia> danhSachTacGia;

    private int conLai;
    private boolean isArchived;

    public Sach() {
        this.danhSachTacGia = new ArrayList<>();
        this.ngayThem = new Date();
        this.conLai = 0;
        this.isArchived = false;
    }

    public Sach(String maSach, String tenSach, String nhaXuatBan,
                int namXuatBan, int soLuong, String duongDanAnh,
                Date ngayThem, String viTri, List<TacGia> danhSachTacGia) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
        this.soLuong = soLuong;
        this.duongDanAnh = duongDanAnh;
        this.ngayThem = (ngayThem != null) ? ngayThem : new Date();
        this.viTri = viTri;
        this.danhSachTacGia = (danhSachTacGia != null) ? danhSachTacGia : new ArrayList<>();
        this.conLai = soLuong; // Khởi tạo ban đầu = số lượng nhập
        this.isArchived = false;
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

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public Date getNgayThem() {
        return ngayThem;
    }

    public void setNgayThem(Date ngayThem) {
        this.ngayThem = ngayThem;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public List<TacGia> getDanhSachTacGia() {
        return danhSachTacGia;
    }

    public void setDanhSachTacGia(List<TacGia> danhSachTacGia) {
        this.danhSachTacGia = (danhSachTacGia != null) ? danhSachTacGia : new ArrayList<>();
    }

    public int getConLai() {
        return conLai;
    }

    public void setConLai(int conLai) {
        this.conLai = conLai;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getTenTacGiaDisplay() {
        if (danhSachTacGia == null || danhSachTacGia.isEmpty()) {
            return "N/A";
        }
        return danhSachTacGia.stream()
                .map(TacGia::getTenTacGia)
                .collect(Collectors.joining(", "));
    }

    public String getTrangThai() {
        return (this.conLai > 0) ? "Còn sách" : "Hết sách";
    }

    @Override
    public String toString() {
        return "Sach{" +
                "maSach='" + maSach + '\'' +
                ", tenSach='" + tenSach + '\'' +
                ", tacGia='" + getTenTacGiaDisplay() + '\'' +
                ", soLuong=" + soLuong +
                ", conLai=" + conLai +
                ", trangThai='" + getTrangThai() + '\'' +
                '}';
    }
}