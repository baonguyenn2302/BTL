package QuanLyThuVien;

import java.util.Date;

public class MuonTra {
    private int maMuonTra;
    private DocGia docGia;
    private Sach sach;
    private Date ngayMuon;
    private Date ngayHenTra;
    private Date ngayTraThucTe;
    private String trangThai;
    private String loaiMuon;

    // ==================================================
    // <<< THÊM TRƯỜNG MỚI (STEP 2) >>>
    // ==================================================
    private TaiKhoan nguoiTao;
    // ==================================================

    public void setLoaiMuon(String loaiMuon) {
        this.loaiMuon = loaiMuon;
    }

    public String getLoaiMuon() {
        return loaiMuon;
    }
    
    public MuonTra() {
    }

    public int getMaMuonTra() {
        return maMuonTra;
    }

    public void setMaMuonTra(int maMuonTra) {
        this.maMuonTra = maMuonTra;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }

    public Sach getSach() {
        return sach;
    }

    public void setSach(Sach sach) {
        this.sach = sach;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public Date getNgayHenTra() {
        return ngayHenTra;
    }

    public void setNgayHenTra(Date ngayHenTra) {
        this.ngayHenTra = ngayHenTra;
    }

    public Date getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(Date ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    // ==================================================
    // <<< THÊM GETTER/SETTER MỚI (STEP 2) >>>
    // ==================================================
    public TaiKhoan getNguoiTao() {
        return nguoiTao;
    }

    public void setNguoiTao(TaiKhoan nguoiTao) {
        this.nguoiTao = nguoiTao;
    }
    // ==================================================

    public String getTrangThai() {
        // (Giữ nguyên logic tính trạng thái)
        if (trangThai == null || trangThai.isEmpty()) {
            if (ngayTraThucTe != null) {
                return "Đã trả";
            } else if (ngayHenTra != null && new Date().after(ngayHenTra)) {
                return "Quá hạn";
            } else {
                return "Đang mượn";
            }
        }
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "MuonTra{" +
                "maMuonTra=" + maMuonTra +
                ", docGia=" + (docGia != null ? docGia.getHoTen() : "N/A") +
                ", sach=" + (sach != null ? sach.getTenSach() : "N/A") +
                ", trangThai='" + getTrangThai() + '\'' +
                '}';
    }
}