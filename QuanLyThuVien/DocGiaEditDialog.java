// File: DocGiaEditDialog.java (PHIÊN BẢN CẬP NHẬT)
package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// === CÁC IMPORT MỚI BẮT ĐẦU TỪ ĐÂY ===
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// === KẾT THÚC IMPORT MỚI ===

/**
 * JDialog để Thêm (hoặc Sửa) Độc Giả.
 */
public class DocGiaEditDialog extends JDialog {

    // 1. Các trường (Fields)
    private JTextField txtMaDocGia;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh; 
    private JTextField txtEmail;
    private JTextField txtSdt;
    private JTextField txtDiaChi;
    
    // === BIẾN MỚI CHO ẢNH ===
    private JLabel lblAnhPath;
    private JButton btnChonAnh;
    private String selectedImagePath = null; // Lưu đường dẫn ảnh

    // 2. Các nút điều khiển
    private JButton btnLuu;
    private JButton btnHuy;
    
    private boolean saveSuccess = false;
    private DocGiaDAO docGiaDAO;
    private boolean isEditMode = false;
    private DocGia docGiaGoc;

    /**
     * Constructor cho việc THÊM ĐỘC GIẢ MỚI.
     */
    public DocGiaEditDialog(JFrame parent) {
        super(parent, "Thêm Độc Giả Mới", true); 
        
        this.docGiaDAO = new DocGiaDAO();
        this.isEditMode = false;
        
        
        setSize(550, 450); // Tăng chiều cao một chút
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        addEventHandlers();
        
        txtMaDocGia.requestFocus(); 
    }
    
    // ========== CONSTRUCTOR MỚI CHO CHẾ ĐỘ SỬA ==========
    /**
     * Constructor cho việc SỬA ĐỘC GIẢ CŨ.
     * @param parent Frame cha (MainFrame)
     * @param docGiaToEdit Đối tượng Độc Giả (đã lấy từ CSDL)
     */
    public DocGiaEditDialog(JFrame parent, DocGia docGiaToEdit) {
        super(parent, "Cập Nhật Thông Tin Độc Giả", true); // Đổi tiêu đề
        
        this.docGiaDAO = new DocGiaDAO();
        this.isEditMode = true; // <<< Đánh dấu là chế độ SỬA
        this.docGiaGoc = docGiaToEdit; // <<< Lưu đối tượng gốc
        
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // GỌI HÀM MỚI: Hiển thị dữ liệu cũ lên form
        populateForm(docGiaToEdit); 
        
        addEventHandlers();
    }
    // ========== KẾT THÚC CONSTRUCTOR MỚI ==========
    // ========== HÀM MỚI ĐỂ ĐIỀN DỮ LIỆU CŨ LÊN FORM ==========
    private void populateForm(DocGia dg) {
        txtMaDocGia.setText(dg.getMaDocGia());
        txtMaDocGia.setEnabled(false); // Mã không được sửa
        txtMaDocGia.setBackground(Color.LIGHT_GRAY);

        txtHoTen.setText(dg.getHoTen());
        
        if (dg.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtNgaySinh.setText(sdf.format(dg.getNgaySinh()));
        }

        txtEmail.setText(dg.getEmail());
        txtSdt.setText(dg.getSdt());
        txtDiaChi.setText(dg.getDiaChi());

        // Hiển thị đường dẫn ảnh (nếu có)
        this.selectedImagePath = dg.getDuongDanAnh(); //
        if (this.selectedImagePath != null && !this.selectedImagePath.isEmpty()) {
            File f = new File(this.selectedImagePath);
            lblAnhPath.setText(f.getName()); // Chỉ hiện tên file
            lblAnhPath.setForeground(Color.BLACK);
            lblAnhPath.setToolTipText(this.selectedImagePath);
        }
    }
    
    // Dựng GUI cho form
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Hàng 0: Mã Độc Giả ===
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; 
        panel.add(new JLabel("Mã Độc Giả (*):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtMaDocGia = new JTextField(20);
        txtMaDocGia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(txtMaDocGia, gbc);

        // === Hàng 1: Họ Tên ===
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Họ Tên (*):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtHoTen = new JTextField(30);
        panel.add(txtHoTen, gbc);

        // === Hàng 2: Ngày Sinh ===
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtNgaySinh = new JTextField(15);
        panel.add(txtNgaySinh, gbc);
        
        // === Hàng 3: Email ===
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtEmail = new JTextField(30);
        panel.add(txtEmail, gbc);
        
        // === Hàng 4: Số điện thoại ===
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtSdt = new JTextField(30);
        panel.add(txtSdt, gbc);
        
        // === Hàng 5: Địa chỉ ===
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtDiaChi = new JTextField(30);
        panel.add(txtDiaChi, gbc);
        
        // === HÀNG 6: CHỌN ẢNH (MỚI) ===
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Ảnh Đại Diện:"), gbc);
        
        JPanel anhPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnChonAnh = new JButton("Chọn Tệp...");
        lblAnhPath = new JLabel("(Chưa chọn ảnh)");
        lblAnhPath.setForeground(Color.GRAY);
        anhPanel.add(btnChonAnh);
        anhPanel.add(lblAnhPath);
        
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(anhPanel, gbc);
        // === KẾT THÚC HÀNG 6 ===

        return panel;
    }
    
    // Dựng GUI cho các nút bấm
    private JPanel createButtonPanel() {
        // (Giống hệt code cũ)
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 240, 240)); 
        btnLuu = new JButton("Lưu Lại");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(30, 144, 255));
        btnLuu.setForeground(Color.WHITE);
        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(108, 117, 125));
        btnHuy.setForeground(Color.WHITE);
        panel.add(btnLuu);
        panel.add(btnHuy);
        return panel;
    }
    
    // Gắn sự kiện (ĐÃ SỬA)
    private void addEventHandlers() {
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> saveDocGia());
        
        // === SỰ KIỆN MỚI CHO NÚT CHỌN ẢNH ===
        btnChonAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh đại diện");
            // Lọc chỉ file ảnh (giống SachEditDialog)
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Hình ảnh (jpg, png, gif)", "jpg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                this.selectedImagePath = selectedFile.getAbsolutePath();
                lblAnhPath.setText(selectedFile.getName()); // Hiển thị tên file
                lblAnhPath.setForeground(Color.BLACK);
                lblAnhPath.setToolTipText(this.selectedImagePath);
            }
        });
    }

    // Xử lý LƯU Độc Giả (ĐÃ SỬA)
    private void saveDocGia() {
        // 1. Validate dữ liệu (Giống code cũ)
        String maDocGia = txtMaDocGia.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();

        if (maDocGia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã độc giả không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMaDocGia.requestFocus();
            return;
        }
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        // 2. Validate Ngày sinh (Giống code cũ)
        Date ngaySinh = null;
        if (!ngaySinhStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                ngaySinh = sdf.parse(ngaySinhStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return;
            }
        }
        
        // === BẮT ĐẦU LOGIC XỬ LÝ ẢNH MỚI ===
        // Logic này được sao chép từ saveSach()
        // và đơn giản hóa vì đây là chế độ THÊM MỚI
        
        
        // === BẮT ĐẦU LOGIC XỬ LÝ ẢNH (ĐÃ SỬA) ===
        // Logic này giống hệt SachEditDialog.saveSach()
        
        String finalImagePathToSave = null;
        // Lấy đường dẫn ảnh cũ (nếu là Sửa) hoặc null (nếu là Thêm)
        String oldImagePath = (isEditMode) ? docGiaGoc.getDuongDanAnh() : null;

        // Lấy đường dẫn ảnh vừa được CHỌN (nếu có)
        // this.selectedImagePath được set bởi JFileChooser hoặc populateForm
        String chosenImagePath = this.selectedImagePath;

        // Mặc định, đường dẫn cuối cùng là đường dẫn cũ
        finalImagePathToSave = oldImagePath;

        // TRƯỜNG HỢP 1: Người dùng đã CHỌN một file MỚI
        if (chosenImagePath != null && !chosenImagePath.equals(oldImagePath)) {
            try {
                Path destDir = Paths.get("doc_gia_avatars");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                
                File sourceFile = new File(chosenImagePath);
                // Dùng maDocGia để tạo tên file duy nhất
                String newFileName = maDocGia + "_" + sourceFile.getName();
                Path destPath = destDir.resolve(newFileName);
                
                Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                
                finalImagePathToSave = destPath.toString(); // Lưu đường dẫn MỚI

            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi sao chép tệp ảnh. Sử dụng ảnh cũ (nếu có).", 
                    "Lỗi I/O", 
                    JOptionPane.WARNING_MESSAGE);
                finalImagePathToSave = oldImagePath; // Quay về ảnh cũ
            }
        }
        // === KẾT THÚC LOGIC XỬ LÝ ẢNH ===

        // 3. Tạo đối tượng DocGia
        DocGia docGia = new DocGia();
        docGia.setMaDocGia(maDocGia);
        docGia.setHoTen(hoTen);
        docGia.setNgaySinh(ngaySinh); 
        docGia.setEmail(txtEmail.getText().trim());
        docGia.setSdt(txtSdt.getText().trim());
        docGia.setDiaChi(txtDiaChi.getText().trim());
        docGia.setDuongDanAnh(finalImagePathToSave); 
        
        if (isEditMode) {
            docGia.setBlocked(docGiaGoc.isBlocked());
            docGia.setArchived(docGiaGoc.isArchived());
        } else {
            docGia.setBlocked(false);
            docGia.setArchived(false);
        }
        // 4. Gọi DAO để lưu (Giống code cũ)
        boolean success;
        
        if (isEditMode) {
             success = docGiaDAO.updateDocGia(docGia);
             // success = docGiaDAO.updateDocGia(newDocGia);
        } else {
            // Chế độ Thêm: KIỂM TRA TRÙNG LẶP
            if (docGiaDAO.checkMaDocGiaExists(maDocGia)) {
                JOptionPane.showMessageDialog(this, "Mã độc giả này đã tồn tại. Vui lòng chọn mã khác.", "Lỗi Trùng Mã", JOptionPane.ERROR_MESSAGE);
                txtMaDocGia.requestFocus();
                return; 
            }
            success = docGiaDAO.insertDocGia(docGia);
        }

        // 5. Đóng form nếu thành công (Giống code cũ)
        if (success) {
            this.saveSuccess = true; 
            dispose(); 
        } else {
            String message = isEditMode ? "cập nhật" : "thêm mới";
            JOptionPane.showMessageDialog(this, 
                "Đã xảy ra lỗi khi " + message + " độc giả.", 
                "Lỗi Database", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Getter này để MainFrame kiểm tra
    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}