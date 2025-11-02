package QuanLyThuVien;


import java.util.ArrayList;
import java.util.List;

public class BoSuuTap {
    private int maBoSuuTap;
    private String tenBoSuuTap;
    private String moTa;
    private String duongDanAnh;
    private List<Sach> dsSach;

    public BoSuuTap() {
        this.dsSach = new ArrayList<>();
    }

    public BoSuuTap(int maBoSuuTap, String tenBoSuuTap, String moTa, String duongDanAnh) {
        this.maBoSuuTap = maBoSuuTap;
        this.tenBoSuuTap = tenBoSuuTap;
        this.moTa = moTa;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

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
        return this.tenBoSuuTap + " (" + getSoLuongSach() + " sách)";
    }
}