package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

public class DocGiaEditDialog extends JDialog {

    // Các trường nhập liệu
    private JTextField txtMaDocGia, txtHoTen, txtNgaySinh, txtEmail, txtSdt;
    private JCheckBox chkBiKhoa;
    
    private JButton btnLuu, btnHuy;
    
    private DocGiaDAO docGiaDAO;
    private DocGia docGiaToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false; // Cờ báo lưu thành công

    public DocGiaEditDialog(JFrame parent, DocGiaDAO dao, DocGia docGiaToEdit) {
        super(parent, true); // true = modal (chặn cửa sổ cha)
        
        this.docGiaDAO = dao;
        this.docGiaToEdit = docGiaToEdit;
        this.isEditMode = (docGiaToEdit != null);

        initUI();
        
        if (isEditMode) {
            setTitle("Sửa Thông Tin Độc Giả");
            populateFields();
        } else {
            setTitle("Thêm Độc Giả Mới");
            // Mặc định là không bị khóa khi thêm mới
            chkBiKhoa.setSelected(false); 
        }

        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel chứa các trường nhập liệu
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

        fieldsPanel.add(new JLabel("Số Điện Thoại:"));
        txtSdt = new JTextField();
        fieldsPanel.add(txtSdt);

        fieldsPanel.add(new JLabel("Trạng thái bị khóa:"));
        chkBiKhoa = new JCheckBox();
        fieldsPanel.add(chkBiKhoa);

        add(fieldsPanel, BorderLayout.CENTER);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---

        // Nút Hủy
        btnHuy.addActionListener(e -> dispose()); // Chỉ cần đóng cửa sổ

        // Nút Lưu
        btnLuu.addActionListener(e -> saveDocGia());

        pack(); // Tự động điều chỉnh kích thước
    }

    /**
     * Điền thông tin của độc giả (chế độ Sửa)
     */
    private void populateFields() {
        txtMaDocGia.setText(docGiaToEdit.getMaDocGia());
        txtMaDocGia.setEditable(false); // Không cho sửa Mã (Khóa chính)
        txtHoTen.setText(docGiaToEdit.getHoTen());
        txtEmail.setText(docGiaToEdit.getEmail());
        txtSdt.setText(docGiaToEdit.getSdt());
        chkBiKhoa.setSelected(docGiaToEdit.isBlocked());

        if (docGiaToEdit.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtNgaySinh.setText(sdf.format(docGiaToEdit.getNgaySinh()));
        }
    }

    /**
     * Xử lý logic khi nhấn nút Lưu
     */
    private void saveDocGia() {
        // 1. Kiểm tra dữ liệu (Validate)
        String maDocGia = txtMaDocGia.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        if (maDocGia.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã độc giả và Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lấy dữ liệu từ form
        DocGia dg = new DocGia();
        dg.setMaDocGia(maDocGia);
        dg.setHoTen(hoTen);
        dg.setEmail(txtEmail.getText().trim());
        dg.setSdt(txtSdt.getText().trim());
        dg.setBlocked(chkBiKhoa.isSelected());
        
        // Xử lý ngày sinh
        try {
            String ngaySinhStr = txtNgaySinh.getText().trim();
            if (!ngaySinhStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false); // Không tự động sửa ngày tháng
                Date ngaySinh = sdf.parse(ngaySinhStr);
                dg.setNgaySinh(ngaySinh);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng Ngày Sinh không hợp lệ (phải là dd/MM/yyyy)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Gọi DAO để Thêm hoặc Sửa
        try {
            boolean success;
            if (isEditMode) {
                // Chế độ Sửa (đã có dg.setMaDocGia ở trên)
                success = docGiaDAO.suaDocGia(dg);
            } else {
                // Chế độ Thêm
                success = docGiaDAO.themDocGia(dg);
            }

            // 4. Phản hồi
            if (success) {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " độc giả thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.saveSuccess = true; // Đặt cờ thành công
                dispose(); // Đóng cửa sổ
            } else {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " độc giả thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Phương thức công khai để AdminMainForm kiểm tra xem có lưu thành công hay không
     */
    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }
}
