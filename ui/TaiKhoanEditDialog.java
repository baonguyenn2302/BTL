package ui;

import model.Sach;
import model.DocGia;
import model.TacGia;
import model.MuonTra;
import model.BoSuuTap;
import model.TaiKhoan;

import dao.SachDAO;
import dao.DocGiaDAO;
import dao.TacGiaDAO;
import dao.MuonTraDAO;
import dao.BoSuuTapDAO;
import dao.TaiKhoanDAO;

import ui.AddSachToBSTDialog;
import ui.BoSuuTapEditDialog;
import ui.ChiTietDocGiaDialog;
import ui.ChiTietMuonTraDialog;
import ui.ChiTietSachDialog;
import ui.DocGiaEditDialog;
import ui.LoginFrame;
import ui.MainFrame;
import ui.SachEditDialog;
import ui.TacGiaEditDialog;
import ui.TaiKhoanEditDialog;
import ui.XacNhanMuonDialog;

import util.DatabaseConnection;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser; // Thư viện chọn file
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter; // Thư viện lọc file
import java.io.File; // Thư viện File
import java.io.IOException; // <<< THÊM
import java.nio.file.Files; // <<< THÊM
import java.nio.file.Paths; // <<< THÊM
import java.nio.file.StandardCopyOption; // <<< THÊM
import java.util.UUID; // <<< THÊM (Để tạo tên tệp duy nhất)

public class TaiKhoanEditDialog extends JDialog {

    private JTextField txtTenDangNhap, txtTenNguoiDung, txtEmail, txtSdt, txtDuongDanAnh;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbQuyen;
    private JButton btnLuu, btnHuy;

    private TaiKhoanDAO taiKhoanDAO;
    private TaiKhoan taiKhoanHienTai; // Lưu tài khoản khi Sửa
    private boolean isEditMode = false;
    private boolean saveSuccess = false;
    public static final String UPLOAD_DIR = "uploads/avatars";
    /**
     * Constructor cho chế độ THÊM MỚI
     */
    public TaiKhoanEditDialog(JFrame parent) {
        super(parent, "Tạo Tài khoản Mới", true);
        this.isEditMode = false;
        initDialog();
    }

    /**
     * Constructor cho chế độ SỬA
     */
    public TaiKhoanEditDialog(JFrame parent, TaiKhoan taiKhoanToEdit) {
        super(parent, "Sửa Tài khoản", true);
        this.isEditMode = true;
        this.taiKhoanHienTai = taiKhoanToEdit;
        initDialog();
        populateData(); // Điền dữ liệu cũ vào form
    }

    /**
     * Hàm dựng giao diện
     * (Bao gồm logic nút "Chọn..." cho ảnh)
     */
    private void initDialog() {
        taiKhoanDAO = new TaiKhoanDAO();
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Hàng 0: Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        txtTenDangNhap = new JTextField(20);
        formPanel.add(txtTenDangNhap, gbc);
        gbc.gridwidth = 1; 

        // Hàng 1: Mật khẩu
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        txtMatKhau = new JPasswordField(20);
        formPanel.add(txtMatKhau, gbc);
        gbc.gridwidth = 1; 

        // Hàng 2: Tên người dùng
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên hiển thị:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        txtTenNguoiDung = new JTextField(20);
        formPanel.add(txtTenNguoiDung, gbc);
        gbc.gridwidth = 1; 

        // Hàng 3: Email
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        gbc.gridwidth = 1; 

        // Hàng 4: SĐT
        gbc.gridy = 4; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        txtSdt = new JTextField(20);
        formPanel.add(txtSdt, gbc);
        gbc.gridwidth = 1; 

        // Hàng 5: Ảnh (ĐÃ SỬA: Thêm nút Chọn)
        gbc.gridy = 5; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Đường dẫn ảnh:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDuongDanAnh = new JTextField(20);
        txtDuongDanAnh.setEnabled(false); // Không cho nhập tay
        txtDuongDanAnh.setDisabledTextColor(Color.BLACK); 
        formPanel.add(txtDuongDanAnh, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; 
        JButton btnChonAnh = new JButton("Chọn...");
        btnChonAnh.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(btnChonAnh, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        
        // Hàng 6: Quyền
        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Quyền:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; 
        gbc.weightx = 1.0;
        cbQuyen = new JComboBox<>(new String[]{"admin", "user"}); 
        formPanel.add(cbQuyen, gbc);
        gbc.gridwidth = 1; 

        add(formPanel, BorderLayout.CENTER);

        // Panel Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        // Gắn sự kiện
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> luuTaiKhoan());
        
        // Sự kiện cho nút Chọn Ảnh
        btnChonAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh đại diện");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG, GIF)", "jpg", "png", "gif"));
            fileChooser.setCurrentDirectory(new File(".")); // Mở ở thư mục dự án
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Gọi hàm helper mới để sao chép ảnh
                    String relativePath = copyAndSaveImage(selectedFile);

                    // Hiển thị đường dẫn tương đối lên ô text
                    txtDuongDanAnh.setText(relativePath); 

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể lưu tệp ảnh. " + ex.getMessage(), "Lỗi sao chép tệp", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Điều chỉnh cho chế độ SỬA
        if (isEditMode) {
            // txtTenDangNhap.setEnabled(false); // <<< ĐÃ XÓA: Giờ có thể sửa
            txtMatKhau.setToolTipText("Để trống nếu không muốn đổi mật khẩu");
        }
    }

    /**
     * Điền dữ liệu vào form nếu ở chế độ SỬA
     */
    private void populateData() {
        if (taiKhoanHienTai == null) return;
        txtTenDangNhap.setText(taiKhoanHienTai.getTenDangNhap());
        txtTenNguoiDung.setText(taiKhoanHienTai.getTenNguoiDung());
        txtEmail.setText(taiKhoanHienTai.getEmail());
        txtSdt.setText(taiKhoanHienTai.getSdt());
        txtDuongDanAnh.setText(taiKhoanHienTai.getDuongDanAnh());
        cbQuyen.setSelectedItem(taiKhoanHienTai.getQuyen());
        // Để trống ô mật khẩu
    }

    /**
     * Xử lý logic khi nhấn LƯU (Cả Tạo mới và Sửa)
     */
    private void luuTaiKhoan() {
        // 1. Validate (Kiểm tra dữ liệu)
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String tenNguoiDung = txtTenNguoiDung.getText().trim();
        
        if (tenDangNhap.isEmpty() || tenNguoiDung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và Tên hiển thị là bắt buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = false;
        
        try {
            if (isEditMode) {
                // 2. CHẾ ĐỘ SỬA
                
                // Lấy mật khẩu: Nếu người dùng để trống, giữ mật khẩu cũ
                String matKhauCuoiCung;
                if (matKhau.isEmpty()) {
                    matKhauCuoiCung = taiKhoanHienTai.getMatKhau(); // Giữ mật khẩu cũ (đã hash)
                } else {
                    matKhauCuoiCung = matKhau; // Mật khẩu mới
                    // LƯU Ý: Nếu bạn hash mật khẩu, hãy hash chuỗi 'matKhau' này
                }
                
                TaiKhoan tk = new TaiKhoan(
                    taiKhoanHienTai.getMaTaiKhoan(), // Dùng maTaiKhoan (ID) cũ
                    tenDangNhap,
                    matKhauCuoiCung,
                    tenNguoiDung,
                    txtEmail.getText().trim(),
                    txtSdt.getText().trim(),
                    txtDuongDanAnh.getText().trim(),
                    cbQuyen.getSelectedItem().toString()
                );
                
                success = taiKhoanDAO.updateTaiKhoan(tk);

            } else {
                // 3. CHẾ ĐỘ TẠO MỚI
                
                if (matKhau.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "Mật khẩu là bắt buộc khi tạo mới.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                TaiKhoan tk = new TaiKhoan(
                    0, // maTaiKhoan = 0, CSDL sẽ tự tạo
                    tenDangNhap,
                    matKhau, // LƯU Ý: Nếu bạn hash mật khẩu, hãy hash chuỗi 'matKhau' này
                    tenNguoiDung,
                    txtEmail.getText().trim(),
                    txtSdt.getText().trim(),
                    txtDuongDanAnh.getText().trim(),
                    cbQuyen.getSelectedItem().toString()
                );
                
                success = taiKhoanDAO.insertTaiKhoan(tk);
            }

            // 4. Xử lý kết quả
            if (success) {
                this.saveSuccess = true;
                dispose();
            } else {
                // Lỗi này xảy ra nếu vi phạm ràng buộc UNIQUE (tenDangNhap)
                JOptionPane.showMessageDialog(this, "Lưu thất bại. Tên đăng nhập có thể đã tồn tại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
    }
    private String copyAndSaveImage(File fileToCopy) throws IOException {
        // 1. Tạo thư mục 'uploads/avatars' nếu nó chưa tồn tại
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Tạo thư mục (bao gồm cả thư mục cha)
        }

        // 2. Lấy phần mở rộng của tệp (ví dụ: .png, .jpg)
        String originalFileName = fileToCopy.getName();
        String fileExtension = "";
        int lastDot = originalFileName.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = originalFileName.substring(lastDot);
        }

        // 3. Tạo một tên tệp duy nhất để tránh trùng lặp
        // Ví dụ: 550e8400-e29b-41d4-a716-446655440000.png
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 4. Tạo đường dẫn đích (đường dẫn tương đối)
        // Ví dụ: uploads/avatars/550e8400-e29b-41d4-a716-446655440000.png
        String relativePath = UPLOAD_DIR + "/" + uniqueFileName;
        
        // 5. Thực hiện sao chép tệp
        Files.copy(fileToCopy.toPath(), Paths.get(relativePath), StandardCopyOption.REPLACE_EXISTING);

        // 6. Trả về đường dẫn tương đối để lưu vào CSDL
        return relativePath.replace("/", "\\"); // (Tùy chọn: Dùng dấu \ cho Windows)
    }
    /**
     * Hàm để MainFrame kiểm tra xem có lưu thành công hay không
     */
    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}