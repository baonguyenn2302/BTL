package QuanLyThuVien;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

// Import các layout bị thiếu
import java.awt.BorderLayout;
import java.awt.FlowLayout;
// Import để sửa lỗi căn chỉnh
import java.awt.Dimension; 

public class LoginForm extends JFrame {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnDangKy;
    
    private UserDAO userDAO;

    public LoginForm() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ THƯ VIỆN");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        userDAO = new UserDAO(); 
        
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(lblTitle);

        // --- Panel Tên đăng nhập ---
        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        JLabel userLabel = new JLabel("Tên đăng nhập:"); // Tạo biến cho Label
        userPanel.add(userLabel, BorderLayout.WEST);
        txtTenDangNhap = new JTextField();
        userPanel.add(txtTenDangNhap, BorderLayout.CENTER);
        panel.add(userPanel);

        // --- Panel Mật khẩu ---
        JPanel passPanel = new JPanel(new BorderLayout(5, 5));
        JLabel passLabel = new JLabel("Mật khẩu:"); // Tạo biến cho Label
        passPanel.add(passLabel, BorderLayout.WEST);
        txtMatKhau = new JPasswordField();
        passPanel.add(txtMatKhau, BorderLayout.CENTER);
        panel.add(passPanel);

        // === SỬA LỖI 1: CĂN CHỈNH THẲNG HÀNG ===
        // Lấy kích thước của label dài nhất ("Tên đăng nhập:")
        Dimension labelSize = userLabel.getPreferredSize();
        // Ép cả hai label phải có cùng kích thước đó
        userLabel.setPreferredSize(labelSize);
        passLabel.setPreferredSize(labelSize);
        // === KẾT THÚC SỬA LỖI 1 ===

        // --- Panel Nút ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnDangNhap = new JButton("Đăng nhập");
        btnDangKy = new JButton("Đăng ký");
        buttonPanel.add(btnDangNhap);
        buttonPanel.add(btnDangKy);
        panel.add(buttonPanel);

        add(panel);

        // --- Xử lý sự kiện ---
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        btnDangKy.addActionListener(e -> xuLyDangKy());
        this.getRootPane().setDefaultButton(btnDangNhap);
    }

    /**
     * SỬA LỖI 2: SỬA LẠI LOGIC HIỂN THỊ THÔNG BÁO
     */
    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.checkLogin(tenDangNhap, matKhau);

        if (user == null) {
            // Đăng nhập thất bại
            JOptionPane.showMessageDialog(this, "Sai Tên đăng nhập hoặc Mật khẩu!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        } else {
            // Đăng nhập thành công
            // Bỏ thông báo "Thành công" chung ở đây
            
            // Thêm .trim() để đảm bảo vai trò "Cán bộ TV " (có dấu cách) vẫn đúng
            if (user.getVaiTro().trim().equals("Cán bộ TV")) {
                // Mở Form Admin
                AdminMainForm adminForm = new AdminMainForm();
                adminForm.setVisible(true);
                
                // Đóng cửa sổ Đăng nhập
                this.dispose(); 
            } else {
                // Nếu là "Độc giả" hoặc vai trò khác
                JOptionPane.showMessageDialog(this, "Đăng nhập Độc giả thành công! (Form Độc giả chưa được tạo)");
                // Tạm thời chưa đóng form
            }
        }
    }
    
    private void xuLyDangKy() {
        // Mở form Đăng ký
        RegisterForm registerForm = new RegisterForm();
        registerForm.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}