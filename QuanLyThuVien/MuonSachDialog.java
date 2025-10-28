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

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
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
        // Mặc định ngày hẹn trả là 7 ngày sau
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        dateChooserHenTra.setDate(cal.getTime());
        fieldsPanel.add(dateChooserHenTra);


        add(fieldsPanel, BorderLayout.CENTER);

        // --- Nút bấm ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnXacNhan = new JButton("Xác nhận mượn");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnXacNhan.addActionListener(e -> {
            try {
                xacNhanMuon(); // Gọi hàm có thể ném SQLException
            } catch (SQLException ex) {
                // Xử lý lỗi SQLException ở đây (hiển thị thông báo)
                JOptionPane.showMessageDialog(this, 
                    "Lỗi cơ sở dữ liệu khi thực hiện mượn: " + ex.getMessage(), 
                    "Lỗi nghiêm trọng", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // In chi tiết lỗi ra console
            } catch (IllegalStateException exState) {
                // Bắt cả lỗi IllegalStateException (sách hết, độc giả khóa)
                JOptionPane.showMessageDialog(this, 
                    "Không thể mượn sách: " + exState.getMessage(), 
                    "Lỗi nghiệp vụ", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
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
    private void xacNhanMuon() throws SQLException {
        DocGia selectedDocGia = (DocGia) cbDocGia.getSelectedItem();
        Sach selectedSach = (Sach) cbSach.getSelectedItem();
        Date ngayHenTra = dateChooserHenTra.getDate();

        // Validate
        if (selectedDocGia == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedSach == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ngayHenTra == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hẹn trả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Kiểm tra ngày hẹn trả phải sau ngày hiện tại
        if (ngayHenTra.before(new Date())) {
             JOptionPane.showMessageDialog(this, "Ngày hẹn trả phải là một ngày trong tương lai.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
        }


        // Tạo đối tượng MuonTra
        MuonTra newMuonTra = new MuonTra();
        newMuonTra.setDocGia(selectedDocGia);
        newMuonTra.setSach(selectedSach);
        newMuonTra.setNgayHenTra(ngayHenTra);
        // ngayMuon và trangThai sẽ được DAO xử lý

        // Gọi DAO để thực hiện mượn
        try {
            if (muonTraDAO.muonSach(newMuonTra)) {
                JOptionPane.showMessageDialog(this, "Mượn sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                this.borrowSuccess = true;
                dispose(); // Đóng dialog
            } else {
                // Trường hợp này ít xảy ra vì đã có throw Exception
                JOptionPane.showMessageDialog(this, "Mượn sách thất bại (Lỗi không xác định).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalStateException ex) {
            // Lỗi do sách hết hoặc độc giả bị khóa
            JOptionPane.showMessageDialog(this, "Mượn sách thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            // Lỗi CSDL nghiêm trọng
             JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu khi mượn sách: " + ex.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

    public boolean isBorrowSuccess() {
        return borrowSuccess;
    }
}
