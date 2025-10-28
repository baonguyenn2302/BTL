package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

/**
 * Dialog (cửa sổ pop-up) để Thêm và Sửa thông tin Tác Giả.
 */
public class TacGiaEditDialog extends JDialog {

    private JTextField txtMaTacGia, txtTenTacGia, txtEmail, txtSdt, txtTrinhDo, txtChucDanh;
    private JButton btnLuu, btnHuy;

    private TacGiaDAO tacGiaDAO;
    private TacGia tacGiaToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;

    public TacGiaEditDialog(JFrame parent, TacGiaDAO dao, TacGia tacGiaToEdit) {
        super(parent, "Quản lý Tác Giả", true);

        this.tacGiaDAO = dao;
        this.tacGiaToEdit = tacGiaToEdit;
        this.isEditMode = (tacGiaToEdit != null);

        initUI();

        if (isEditMode) {
            setTitle("Sửa Thông Tin Tác Giả");
            populateFields();
        } else {
            setTitle("Thêm Tác Giả Mới");
        }

        setSize(450, 350);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Mã Tác Giả:"));
        txtMaTacGia = new JTextField(20);
        fieldsPanel.add(txtMaTacGia);

        fieldsPanel.add(new JLabel("Tên Tác Giả:"));
        txtTenTacGia = new JTextField();
        fieldsPanel.add(txtTenTacGia);

        fieldsPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        fieldsPanel.add(txtEmail);

        fieldsPanel.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        fieldsPanel.add(txtSdt);

        fieldsPanel.add(new JLabel("Trình Độ Chuyên Môn:"));
        txtTrinhDo = new JTextField();
        fieldsPanel.add(txtTrinhDo);

        fieldsPanel.add(new JLabel("Chức Danh:"));
        txtChucDanh = new JTextField();
        fieldsPanel.add(txtChucDanh);

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
        btnLuu.addActionListener(e -> saveTacGia());
    }

    /**
     * Điền thông tin của tác giả (chế độ Sửa)
     */
    private void populateFields() {
        txtMaTacGia.setText(tacGiaToEdit.getMaTacGia());
        txtMaTacGia.setEditable(false); // Không cho sửa Mã
        txtTenTacGia.setText(tacGiaToEdit.getTenTacGia());
        txtEmail.setText(tacGiaToEdit.getEmail());
        txtSdt.setText(tacGiaToEdit.getSdt());
        txtTrinhDo.setText(tacGiaToEdit.getTrinhDoChuyenMon());
        txtChucDanh.setText(tacGiaToEdit.getChucDanh());
    }

    /**
     * Xử lý logic khi nhấn nút Lưu
     */
    private void saveTacGia() {
        // 1. Validate
        String maTacGia = txtMaTacGia.getText().trim();
        String tenTacGia = txtTenTacGia.getText().trim();
        if (maTacGia.isEmpty() || tenTacGia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Tác Giả và Tên Tác Giả không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lấy dữ liệu từ form
        TacGia tg = new TacGia();
        tg.setMaTacGia(maTacGia);
        tg.setTenTacGia(tenTacGia);
        tg.setEmail(txtEmail.getText().trim());
        tg.setSdt(txtSdt.getText().trim());
        tg.setTrinhDoChuyenMon(txtTrinhDo.getText().trim());
        tg.setChucDanh(txtChucDanh.getText().trim());

        // 3. Gọi DAO
        try {
            boolean success;
            if (isEditMode) {
                success = tacGiaDAO.suaTacGia(tg);
            } else {
                success = tacGiaDAO.themTacGia(tg);
            }

            // 4. Phản hồi
            if (success) {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " tác giả thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.saveSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " tác giả thất bại. (Mã tác giả có thể đã tồn tại?)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }
}
