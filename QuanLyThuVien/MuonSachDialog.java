package QuanLyThuVien;

import com.toedter.calendar.JDateChooser; // Thư viện JCalendar
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;

/**
 * Dialog (cửa sổ pop-up) để tạo Phiếu Mượn Sách mới.
 */
public class MuonSachDialog extends JDialog {

    private JComboBox<DocGia> cbDocGia;
    private JComboBox<Sach> cbSach;
    private JDateChooser dateChooserHenTra; // Dùng JCalendar
    private JComboBox<String> cbLoaiMuon;
    private JButton btnXacNhan, btnHuy;

    private DocGiaDAO docGiaDAO;
    private SachDAO sachDAO;
    private MuonTraDAO muonTraDAO;

    private boolean borrowSuccess = false;

    public MuonSachDialog(JFrame parent, MuonTraDAO muonTraDAO) {
        super(parent, "Tạo Phiếu Mượn Sách", true);
        this.muonTraDAO = muonTraDAO;
        this.docGiaDAO = new DocGiaDAO();
        this.sachDAO = new SachDAO();

        initUI();
        loadComboBoxData();

        setSize(450, 250);
        setLocationRelativeTo(parent);
    }

    // Thay thế toàn bộ hàm initUI()
    private void initUI() {
    setLayout(new BorderLayout(10, 10));

    // Tăng GridLayout lên 4 hàng
    JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10)); 
    fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // --- Chọn Độc Giả ---
    fieldsPanel.add(new JLabel("Độc Giả:"));
    cbDocGia = new JComboBox<>();
    fieldsPanel.add(cbDocGia);

    // --- Chọn Sách ---
    fieldsPanel.add(new JLabel("Sách:"));
    cbSach = new JComboBox<>();
    fieldsPanel.add(cbSach);

    // --- Chọn Ngày Hẹn Trả ---
    fieldsPanel.add(new JLabel("Ngày Hẹn Trả:"));
    dateChooserHenTra = new JDateChooser();
    dateChooserHenTra.setDateFormatString("dd/MM/yyyy");
    fieldsPanel.add(dateChooserHenTra);

    // --- (NEW) Chọn Loại Mượn ---
    fieldsPanel.add(new JLabel("Loại mượn:"));
    cbLoaiMuon = new JComboBox<>(new String[]{"Mượn về nhà", "Mượn tại chỗ"});
    fieldsPanel.add(cbLoaiMuon);

    add(fieldsPanel, BorderLayout.CENTER);

    // --- Nút bấm ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnXacNhan = new JButton("Xác nhận mượn");
    btnHuy = new JButton("Hủy");
    buttonPanel.add(btnXacNhan);
    buttonPanel.add(btnHuy);
    add(buttonPanel, BorderLayout.SOUTH);

    // --- (NEW) Sự kiện cho Loại Mượn ---
    cbLoaiMuon.addActionListener(e -> {
        String loai = (String) cbLoaiMuon.getSelectedItem();
        
        if ("Mượn tại chỗ".equals(loai)) {
            // TẮT ô chọn ngày
            dateChooserHenTra.setEnabled(false);
            // Tự động set ngày hẹn trả là 23:59:59 của ngày HÔM NAY
            dateChooserHenTra.setDate(getEndOfDay(new Date())); 
        } else { // "Mượn về nhà"
            // MỞ lại ô chọn ngày
            dateChooserHenTra.setEnabled(true);
            // Set lại ngày mặc định (7 ngày sau)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            dateChooserHenTra.setDate(cal.getTime());
        }
    });

    // Kích hoạt sự kiện lần đầu để đảm bảo trạng thái đúng
    cbLoaiMuon.setSelectedItem("Mượn về nhà");

    // --- Sự kiện Nút bấm ---
    btnHuy.addActionListener(e -> dispose());
    btnXacNhan.addActionListener(e -> {
        try {
            xacNhanMuon();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi cơ sở dữ liệu khi thực hiện mượn: " + ex.getMessage(), 
                "Lỗi nghiêm trọng", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IllegalStateException exState) {
            JOptionPane.showMessageDialog(this, 
                "Không thể mượn sách: " + exState.getMessage(), 
                "Lỗi nghiệp vụ", 
                JOptionPane.WARNING_MESSAGE);
        }
    });
    
    // Tăng kích thước cửa sổ để vừa
    setSize(450, 300); 
}

    /**
     * Tải dữ liệu cho ComboBox Độc Giả và Sách
     */
    private void loadComboBoxData() {
        // Load Độc Giả (chỉ những người không bị khóa)
        DefaultComboBoxModel<DocGia> docGiaModel = new DefaultComboBoxModel<>();
        List<DocGia> dsDocGia = docGiaDAO.getAllDocGia();
        for (DocGia dg : dsDocGia) {
            if (!dg.isBlocked()) { // Chỉ thêm độc giả đang hoạt động
                docGiaModel.addElement(dg); // DocGia.toString() hiển thị tên
            }
        }
        cbDocGia.setModel(docGiaModel);

        // Load Sách (chỉ những sách còn hàng)
        DefaultComboBoxModel<Sach> sachModel = new DefaultComboBoxModel<>();
        List<Sach> dsSach = sachDAO.getAllSach();
        for (Sach s : dsSach) {
             if (s.getSoLuong() > 0) { // Chỉ thêm sách còn hàng
                sachModel.addElement(s); // Sach.toString() hiển thị tên
             }
        }
        cbSach.setModel(sachModel);

        // (Để tối ưu, bạn có thể tạo hàm DAO chỉ lấy sách còn hàng)
    }


    /**
     * Xử lý khi nhấn nút "Xác nhận mượn"
     */
    // Thay thế toàn bộ hàm xacNhanMuon()
    private void xacNhanMuon() throws SQLException {
    DocGia selectedDocGia = (DocGia) cbDocGia.getSelectedItem();
    Sach selectedSach = (Sach) cbSach.getSelectedItem();
    Date ngayHenTra = dateChooserHenTra.getDate();
    String loaiMuon = (String) cbLoaiMuon.getSelectedItem();

    // 1. Validate
    if (selectedDocGia == null || selectedSach == null || ngayHenTra == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ Độc giả, Sách và Ngày hẹn trả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 2. Validate Ngày (chỉ kiểm tra nếu mượn về nhà)
    if ("Mượn về nhà".equals(loaiMuon)) {
        if (ngayHenTra.before(new Date())) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn trả (về nhà) phải là một ngày trong tương lai.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    // 3. Tạo đối tượng MuonTra
    MuonTra newMuonTra = new MuonTra();
    newMuonTra.setDocGia(selectedDocGia);
    newMuonTra.setSach(selectedSach);
    newMuonTra.setNgayHenTra(ngayHenTra); // Gán ngày đã được xử lý
    newMuonTra.setLoaiMuon(loaiMuon);     // Gán loại mượn

    // 4. Gọi DAO (đã được cập nhật ở Bước 3)
    try {
        if (muonTraDAO.muonSach(newMuonTra)) {
            JOptionPane.showMessageDialog(this, "Mượn sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            this.borrowSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Mượn sách thất bại (Lỗi không xác định).", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    } catch (IllegalStateException ex) {
        JOptionPane.showMessageDialog(this, "Mượn sách thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
         JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu khi mượn sách: " + ex.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
         ex.printStackTrace();
    }
}

    public boolean isBorrowSuccess() {
        return borrowSuccess;
    }
    /**
 * Hàm tiện ích để lấy thời điểm cuối cùng trong ngày (23:59:59)
 */
    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
