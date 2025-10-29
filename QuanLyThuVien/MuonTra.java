package QuanLyThuVien;

import java.util.Date;

/**
 * Lớp Model (POJO) đại diện cho một lượt Mượn Trả sách.
 */
public class MuonTra {

    private int maMuonTra;       // Mã định danh duy nhất (tự tăng)
    private DocGia docGia;       // Đối tượng Độc Giả mượn sách
    private Sach sach;           // Đối tượng Sách được mượn
    private Date ngayMuon;       // Ngày giờ mượn
    private Date ngayHenTra;     // Ngày hẹn trả
    private Date ngayTraThucTe;  // Ngày trả thực tế (null nếu chưa trả)
    private String trangThai;    // Trạng thái: "Đang mượn", "Đã trả", "Quá hạn"
    private String loaiMuon;

    public void setLoaiMuon(String loaiMuon) {
        this.loaiMuon = loaiMuon;
    }

    public String getLoaiMuon() {
        return loaiMuon;
    }
    // Constructors
    public MuonTra() {
    }

    // Getters and Setters
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

    public String getTrangThai() {
        // Tự động xác định trạng thái nếu chưa được set explicitely
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
