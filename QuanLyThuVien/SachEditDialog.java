package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// ==========================
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * JDialog để Thêm (hoặc Sửa) Sách.
 * Phiên bản này chỉ dựng GUI, chưa có logic save.
 */
public class SachEditDialog extends JDialog {

    // 1. Các trường (Fields)
    private JTextField txtMaSach;
    private JTextField txtTenSach;
    private JTextField txtNhaXuatBan;
    private JTextField txtNamXuatBan;
    private JTextField txtSoLuong;
    private JTextField txtViTri;
    private JTextField txtTacGia; // Dùng để nhập nhiều tác giả, cách nhau bởi dấu phẩy
    private JTextArea txtMoTa;
    
    // 2. Các trường cho Ảnh
    private JLabel lblAnhPath; // Hiển thị đường dẫn ảnh đã chọn
    private JButton btnChonAnh;
    private String selectedImagePath = null; // Lưu đường dẫn ảnh

    // 3. Các nút điều khiển
    private JButton btnLuu;
    private JButton btnHuy;
    
    private boolean saveSuccess = false; // Cờ (flag) để MainFrame biết có save thành công không
    private boolean isEditMode = false;
    private Sach sachGoc;
    /**
     * Constructor cho việc THÊM SÁCH MỚI.
     * @param parent Frame cha (MainFrame)
     * @param newMaSach Mã sách ĐÃ ĐƯỢC TẠO SẴN (từ MainFrame/DAO)
     */
    public SachEditDialog(JFrame parent, String newMaSach) {
        super(parent, "Thêm Sách Mới", true); // true = Modal Dialog
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel chính chứa form (dùng GridBagLayout)
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Panel chứa nút Lưu / Hủy
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Gán mã sách mới và vô hiệu hóa
        txtMaSach.setText(newMaSach);
        txtMaSach.setEnabled(false); // Người dùng không được sửa Mã Sách
        
        // Gắn sự kiện (chỉ là placeholder)
        addEventHandlers();
    }
    
    /**
 * Constructor cho việc SỬA SÁCH CŨ.
 * @param parent Frame cha (MainFrame)
 * @param sachToEdit Đối tượng Sách (đã lấy từ CSDL)
 */
    public SachEditDialog(JFrame parent, Sach sachToEdit) {
        super(parent, "Cập Nhật Sách", true); // Đổi tiêu đề
        this.isEditMode = true; // <<< ĐÁNH DẤU LÀ CHẾ ĐỘ SỬA
        this.sachGoc = sachToEdit;
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        addEventHandlers();

        // GỌI HÀM MỚI: Hiển thị dữ liệu cũ lên form
        populateForm(sachToEdit); 
    }
    // Hàm này chỉ là helper, sẽ được thay thế bằng logic thật
    private void showNotImplementedMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Chưa Triển Khai", JOptionPane.INFORMATION_MESSAGE);
    }

    // Dựng GUI cho form
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các component
        gbc.fill = GridBagConstraints.HORIZONTAL; // Các component co dãn theo chiều ngang

        // === Hàng 0: Mã Sách ===
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Căn lề phải cho JLabel
        panel.add(new JLabel("Mã Sách:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2; // Kéo dài 2 cột
        gbc.anchor = GridBagConstraints.WEST;
        txtMaSach = new JTextField(20);
        txtMaSach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMaSach.setBackground(Color.LIGHT_GRAY); // Nền xám để biết là bị vô hiệu hóa
        panel.add(txtMaSach, gbc);

        // === Hàng 1: Tên Sách ===
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên Sách (*):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2; // Kéo dài
        gbc.anchor = GridBagConstraints.WEST;
        txtTenSach = new JTextField(30);
        panel.add(txtTenSach, gbc);

        // === Hàng 2: Tác Giả ===
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tác Giả (cách nhau bởi phẩy):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        txtTacGia = new JTextField(30);
        panel.add(txtTacGia, gbc);

        // === Hàng 3: Nhà XB và Năm XB (trên 1 hàng) ===
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Nhà Xuất Bản:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; // Chỉ 1 cột
        gbc.anchor = GridBagConstraints.WEST;
        txtNhaXuatBan = new JTextField(15);
        panel.add(txtNhaXuatBan, gbc);
        
        // Thêm label cho Năm XB
        JPanel namXBPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        namXBPanel.add(new JLabel("Năm XB:"));
        txtNamXuatBan = new JTextField(5);
        namXBPanel.add(txtNamXuatBan);
        
        gbc.gridx = 2; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(namXBPanel, gbc);

        // === Hàng 4: Số Lượng và Vị Trí (trên 1 hàng) ===
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Số Lượng (*):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtSoLuong = new JTextField(5);
        panel.add(txtSoLuong, gbc);

        JPanel viTriPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        viTriPanel.add(new JLabel("Vị Trí:"));
        txtViTri = new JTextField(10);
        viTriPanel.add(txtViTri);

        gbc.gridx = 2; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(viTriPanel, gbc);

        // === Hàng 5: Chọn Ảnh ===
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Ảnh Bìa:"), gbc);
        
        JPanel anhPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnChonAnh = new JButton("Chọn Tệp...");
        lblAnhPath = new JLabel("(Chưa chọn ảnh)");
        lblAnhPath.setForeground(Color.GRAY);
        anhPanel.add(btnChonAnh);
        anhPanel.add(lblAnhPath);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(anhPanel, gbc);

        // === Hàng 6: Mô Tả ===
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Căn lề trên + phải
        panel.add(new JLabel("Mô Tả:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH; // Co dãn cả 2 chiều
        gbc.weightx = 1.0; // Cho phép mở rộng theo chiều ngang
        gbc.weighty = 1.0; // Cho phép mở rộng theo chiều dọc (RẤT QUAN TRỌNG)
        
        txtMoTa = new JTextArea(5, 30); // 5 hàng, 30 cột
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        panel.add(scrollMoTa, gbc);
        
        return panel;
    }
    
    // Dựng GUI cho các nút bấm
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Căn lề phải
        panel.setBackground(new Color(240, 240, 240)); // Màu nền xám nhạt

        btnLuu = new JButton("Lưu Lại");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(30, 144, 255)); // Màu xanh
        btnLuu.setForeground(Color.WHITE);

        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(108, 117, 125)); // Màu xám
        btnHuy.setForeground(Color.WHITE);

        panel.add(btnLuu);
        panel.add(btnHuy);
        
        return panel;
    }
    
    // Gắn sự kiện (phiên bản GUI)
    // THAY THẾ HÀM NÀY BẰNG CODE BÊN DƯỚI
    private void addEventHandlers() {
    // Nút Hủy: Đơn giản là đóng dialog
        btnHuy.addActionListener(e -> dispose());

        // Nút Lưu (ĐÃ CÓ LOGIC)
        btnLuu.addActionListener(e -> saveSach());

        // Nút Chọn Ảnh (ĐÃ CÓ LOGIC)
        btnChonAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh bìa sách");
            // Lọc chỉ file ảnh
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
    // HÀM MỚI ĐỂ XỬ LÝ LƯU SÁCH
    // TRONG FILE: SachEditDialog.java
// HÃY THAY THẾ TOÀN BỘ HÀM NÀY

    private void saveSach() {
        // 1. Validate dữ liệu (Kiểm tra bắt buộc)
        String tenSach = txtTenSach.getText().trim();
        if (tenSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sách không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenSach.requestFocus();
            return;
        }

        int soLuong = 0;
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoLuong.requestFocus();
            return;
        }

        int namXB = 0;
        String namXBStr = txtNamXuatBan.getText().trim();
        if (!namXBStr.isEmpty()) {
            try {
                namXB = Integer.parseInt(namXBStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Năm xuất bản không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtNamXuatBan.requestFocus();
                return;
            }
        }

        // 2. Tạo đối tượng Sach
        Sach newSach = new Sach();
        newSach.setMaSach(txtMaSach.getText()); // Lấy từ mã đã tạo sẵn
        newSach.setTenSach(tenSach);
        newSach.setSoLuong(soLuong);
        // 'conLai' sẽ được tính toán ở logic (isEditMode) bên dưới
        newSach.setNhaXuatBan(txtNhaXuatBan.getText().trim());
        newSach.setNamXuatBan(namXB);
        newSach.setViTri(txtViTri.getText().trim());
        newSach.setMoTa(txtMoTa.getText().trim());

        // === BẮT ĐẦU LOGIC XỬ LÝ ẢNH MỚI ===

        String finalImagePathToSave = null;
        // Lấy đường dẫn ảnh cũ (nếu là Sửa) hoặc null (nếu là Thêm)
        String oldImagePath = (isEditMode) ? sachGoc.getDuongDanAnh() : null;

        // Lấy đường dẫn ảnh vừa được CHỌN (nếu có)
        // this.selectedImagePath được set bởi JFileChooser 
        // hoặc bởi populateForm
        String chosenImagePath = this.selectedImagePath;

        // Mặc định, đường dẫn cuối cùng là đường dẫn cũ
        finalImagePathToSave = oldImagePath;

        // TRƯỜNG HỢP 1: Người dùng đã CHỌN một file MỚI
        // (Tức là đường dẫn được chọn KHÁC với đường dẫn cũ VÀ nó không rỗng)
        if (chosenImagePath != null && !chosenImagePath.equals(oldImagePath)) {
            try {
                // 1. Thư mục đích (đã tạo ở Bước 1)
                Path destDir = Paths.get("book_covers");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }

                // 2. Lấy file nguồn
                File sourceFile = new File(chosenImagePath);

                // 3. Tạo tên file mới (duy nhất) để tránh trùng lặp
                // Ví dụ: SA005_ten_file_goc.jpg
                String newFileName = newSach.getMaSach() + "_" + sourceFile.getName();

                // 4. Tạo đường dẫn đích
                Path destPath = destDir.resolve(newFileName);

                // 5. Sao chép file (GHI ĐÈ nếu file cũ tồn tại)
                Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

                // 6. Lưu đường dẫn MỚI (dạng tương đối) vào DB
                finalImagePathToSave = destPath.toString();

            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                // Nếu copy lỗi, báo cho người dùng và (quan trọng) KHÔNG THAY ĐỔI ẢNH CŨ
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi sao chép tệp ảnh. Sử dụng ảnh cũ (nếu có).", 
                    "Lỗi I/O", 
                    JOptionPane.WARNING_MESSAGE);
                finalImagePathToSave = oldImagePath; // Quay về ảnh cũ
            }
        }
        // TRƯỜNG HỢP 2: Người dùng KHÔNG chọn file mới
        // 'finalImagePathToSave' vẫn là 'oldImagePath' (hoặc null nếu thêm mới).
        // Logic này đã đúng.

        // Gán đường dẫn cuối cùng cho đối tượng Sách
        newSach.setDuongDanAnh(finalImagePathToSave); 

        // === KẾT THÚC LOGIC XỬ LÝ ẢNH ===

        // 3. XỬ LÝ TÁC GIẢ (Yêu cầu chính của bạn)
        TacGiaDAO tacGiaDAO = new TacGiaDAO();
        List<TacGia> authorsList = new ArrayList<>();
        String authorsString = txtTacGia.getText().trim();

        if (!authorsString.isEmpty()) {
            // Tách chuỗi bằng dấu phẩy, và xóa khoảng trắng thừa
            String[] authorNames = authorsString.split("\\s*,\\s*");

            for (String name : authorNames) {
                if (!name.isEmpty()) {
                    // TÌM HOẶC TẠO MỚI TÁC GIẢ
                    TacGia tg = tacGiaDAO.findOrCreateTacGia(name);
                    if (tg != null) {
                        authorsList.add(tg);
                    } else {
                        // Lỗi nghiêm trọng: Không thể thêm tác giả
                        JOptionPane.showMessageDialog(this, "Không thể thêm tác giả: " + name, "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }

        // 4. Gọi DAO để lưu vào CSDL (ĐÃ SỬA LOGIC)
        SachDAO sachDAO = new SachDAO();
        boolean success;

        if (isEditMode) {
            // === CHẾ ĐỘ SỬA ===

            // 1. Tính toán số sách đang được mượn (DÙNG BIẾN sachGoc)
            int soSachDaMuon = sachGoc.getSoLuong() - sachGoc.getConLai();

            // 2. Lấy số lượng MỚI mà admin nhập (từ newSach)
            int newSoLuong = newSach.getSoLuong();

            // 3. KIỂM TRA QUAN TRỌNG:
            // Admin không thể set tổng số lượng < số sách đang mượn.
            if (newSoLuong < soSachDaMuon) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi: Số lượng tổng (" + newSoLuong + ") không thể nhỏ hơn số sách đang được mượn (" + soSachDaMuon + ").",
                    "Lỗi Logic",
                    JOptionPane.ERROR_MESSAGE);
                txtSoLuong.requestFocus();
                return; // Dừng lại, không lưu
            }

            // 4. Tính số lượng còn lại MỚI (Logic đã đúng)
            int newConLai = newSoLuong - soSachDaMuon;
            newSach.setConLai(newConLai); // Cập nhật 'conLai' cho đối tượng newSach

            // 5. Gọi DAO để CẬP NHẬT
            success = sachDAO.updateSach(newSach, authorsList);

        } else {
            // === CHẾ ĐỘ THÊM ===
            // (Logic này vốn đã đúng)
            newSach.setConLai(newSach.getSoLuong()); // Khi thêm mới, conLai = soLuong
            success = sachDAO.addSach(newSach, authorsList);
        }

        // 5. Đóng form nếu thành công (Giữ nguyên)
        if (success) {
            this.saveSuccess = true; 
            dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi lưu sách vào CSDL.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }
    // HÀM MỚI: Điền dữ liệu của sách cũ lên form
    private void populateForm(Sach s) {
        txtMaSach.setText(s.getMaSach());
        txtMaSach.setEnabled(false); // Mã sách không được sửa
        txtMaSach.setBackground(Color.LIGHT_GRAY);

        txtTenSach.setText(s.getTenSach());
        txtNhaXuatBan.setText(s.getNhaXuatBan());

        // Tránh hiển thị "0" nếu không có năm XB
        if(s.getNamXuatBan() > 0) {
            txtNamXuatBan.setText(String.valueOf(s.getNamXuatBan()));
        }

        txtSoLuong.setText(String.valueOf(s.getSoLuong()));
        txtViTri.setText(s.getViTri());
        txtMoTa.setText(s.getMoTa());

        // Hiển thị danh sách tác giả (dùng hàm có sẵn trong Sach.java)
        txtTacGia.setText(s.getTenTacGiaDisplay()); 

        // Hiển thị đường dẫn ảnh (nếu có)
        this.selectedImagePath = s.getDuongDanAnh();
        if (this.selectedImagePath != null && !this.selectedImagePath.isEmpty()) {
            File f = new File(this.selectedImagePath);
            lblAnhPath.setText(f.getName()); // Chỉ hiện tên file
            lblAnhPath.setForeground(Color.BLACK);
            lblAnhPath.setToolTipText(this.selectedImagePath);
        }
    }
    // Getter này để MainFrame kiểm tra xem dialog đã lưu thành công chưa
    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}
