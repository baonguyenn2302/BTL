// File: BoSuuTap.java (ĐÃ CẬP NHẬT)
package QuanLyThuVien;

import java.util.ArrayList;
import java.util.List;

public class BoSuuTap {
    private int maBoSuuTap;
    private String tenBoSuuTap;
    // private String moTa; // <<< ĐÃ XÓA
    private String duongDanAnh;
    private List<Sach> dsSach;

    public BoSuuTap() {
        this.dsSach = new ArrayList<>();
    }

    // <<< ĐÃ XÓA moTa KHỎI CONSTRUCTOR >>>
    public BoSuuTap(int maBoSuuTap, String tenBoSuuTap, String duongDanAnh) {
        this.maBoSuuTap = maBoSuuTap;
        this.tenBoSuuTap = tenBoSuuTap;
        this.duongDanAnh = duongDanAnh;
        this.dsSach = new ArrayList<>();
    }

    public int getMaBoSuuTap() {
        return maBoSuuTap;
    }

    public void setMaBoSuuTap(int maBoSuuTap) {
        this.maBoSuuTap = maBoSuuTap;
    }

    public String getTenBoSuuTap() {
        return tenBoSuuTap;
    }

    public void setTenBoSuuTap(String tenBoSuuTap) {
        this.tenBoSuuTap = tenBoSuuTap;
    }

    // <<< ĐÃ XÓA getMoTa() VÀ setMoTa() >>>

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public List<Sach> getDsSach() {
        return dsSach;
    }

    public void setDsSach(List<Sach> dsSach) {
        this.dsSach = dsSach;
    }

    public void themSach(Sach sach) {
        if (sach != null && !dsSach.contains(sach)) {
            dsSach.add(sach);
        }
    }

    public void xoaSach(Sach sach) {
        dsSach.remove(sach);
    }

    public int getSoLuongSach() {
        return dsSach.size();
    }

    @Override
    public String toString() {
        // (toString() không bị ảnh hưởng)
        return this.tenBoSuuTap + " (" + getSoLuongSach() + " sách)";
    }
}