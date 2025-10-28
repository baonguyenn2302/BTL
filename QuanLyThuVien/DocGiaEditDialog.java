package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

/**
 * Dialog (cửa sổ pop-up) để Thêm và Sửa thông tin Độc Giả.
 */
public class DocGiaEditDialog extends JDialog {

    private JTextField txtMaDocGia, txtHoTen, txtNgaySinh, txtEmail, txtDiaChi, txtSdt;
    private JButton btnLuu, btnHuy;

    private DocGiaDAO docGiaDAO;
    private DocGia docGiaToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;

    // Định dạng ngày
    private SimpleDateFormat sdfDialog = new SimpleDateFormat("dd/MM/yyyy");

    public DocGiaEditDialog(JFrame parent, DocGiaDAO dao, DocGia docGiaToEdit) {
        super(parent, "Quản lý Độc Giả", true);

        this.docGiaDAO = dao;
        this.docGiaToEdit = docGiaToEdit;
        this.isEditMode = (docGiaToEdit != null);
        
        // Cú pháp sdf.setLenient(false) bắt buộc ngày tháng phải hợp lệ
        // (ví dụ: không cho nhập 30/02/2020)
        sdfDialog.setLenient(false);

        initUI();

        if (isEditMode) {
            setTitle("Sửa Thông Tin Độc Giả");
            populateFields();
        } else {
            setTitle("Thêm Độc Giả Mới");
        }

        setSize(450, 350);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Mã Độc Giả:"));
        txtMaDocGia = new JTextField(20);
        fieldsPanel.add(txtMaDocGia);

        fieldsPanel.add(new JLabel("Họ Tên:"));
        txtHoTen = new JTextField();
        fieldsPanel.add(txtHoTen);

        fieldsPanel.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"));
        txtNgaySinh = new JTextField();
        fieldsPanel.add(txtNgaySinh);

        fieldsPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        fieldsPanel.add(txtEmail);

        fieldsPanel.add(new JLabel("Địa Chỉ:"));
        txtDiaChi = new JTextField();
        fieldsPanel.add(txtDiaChi);

        fieldsPanel.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        fieldsPanel.add(txtSdt);

        add(fieldsPanel, BorderLayout.CENTER);

        // Panel nút Lưu/Hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> saveDocGia());
    }

    /**
     * Điền thông tin của độc giả (chế độ Sửa)
     */
    private void populateFields() {
        txtMaDocGia.setText(docGiaToEdit.getMaDocGia());
        txtMaDocGia.setEditable(false); // Không cho sửa Mã
        txtHoTen.setText(docGiaToEdit.getHoTen());
        
        if (docGiaToEdit.getNgaySinh() != null) {
            txtNgaySinh.setText(sdfDialog.format(docGiaToEdit.getNgaySinh()));
        }
        
        txtEmail.setText(docGiaToEdit.getEmail());
        txtDiaChi.setText(docGiaToEdit.getDiaChi());
        txtSdt.setText(docGiaToEdit.getSdt());
    }

    /**
     * Xử lý logic khi nhấn nút Lưu
     */
    private void saveDocGia() {
        // 1. Validate (Kiểm tra dữ liệu đầu vào)
        String maDocGia = txtMaDocGia.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        if (maDocGia.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Độc Giả và Họ Tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate ngày sinh
        Date ngaySinh = null;
        String ngaySinhStr = txtNgaySinh.getText().trim();
        if (!ngaySinhStr.isEmpty()) {
            try {
                ngaySinh = sdfDialog.parse(ngaySinhStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // (Tùy chọn) Validate email, sdt...
        // ...

        // 2. Lấy dữ liệu từ form
        DocGia dg = new DocGia();
        dg.setMaDocGia(maDocGia);
        dg.setHoTen(hoTen);
        dg.setNgaySinh(ngaySinh);
        dg.setEmail(txtEmail.getText().trim());
        dg.setDiaChi(txtDiaChi.getText().trim());
        dg.setSdt(txtSdt.getText().trim());
        // Trạng thái 'blocked' không được quản lý ở đây

        // 3. Gọi DAO
        try {
            boolean success;
            if (isEditMode) {
                // Giữ nguyên trạng thái blocked cũ khi Sửa
                dg.setBlocked(docGiaToEdit.isBlocked());
                success = docGiaDAO.suaDocGia(dg);
            } else {
                // Mặc định là false (0) khi Thêm (đã xử lý trong DAO)
                success = docGiaDAO.themDocGia(dg);
            }

            // 4. Phản hồi
            if (success) {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " độc giả thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.saveSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " độc giả thất bại. (Mã độc giả có thể đã tồn tại?)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Hàm này được gọi bởi AdminMainForm để kiểm tra xem có cần tải lại bảng không.
     */
    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }
}
