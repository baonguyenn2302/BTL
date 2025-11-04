// File: LoginFrame.java (ĐÃ SỬA LỖI FONT TIẾNG VIỆT)
package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Font; // <<< Quan trọng
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LoginFrame extends JFrame {

    // 1. Khai báo các thành phần
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // 2. Khai báo DAO
    private TaiKhoanDAO taiKhoanDAO;

    // 3. Hàm khởi tạo
    public LoginFrame() {
        taiKhoanDAO = new TaiKhoanDAO();

        setTitle("Đăng Nhập Hệ Thống Quản Lý Thư Viện");
        setSize(450, 250); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        initUI();
        addEventHandlers();
    }

    // 4. Hàm dựng giao diện (ĐÃ SỬA FONT)
    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 

        // <<< ĐỊNH NGHĨA FONT CHUNG >>>
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // --- Hàng 0: Tiêu đề ---
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitle, gbc);

        // --- Hàng 1: Tên đăng nhập ---
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(labelFont); // <<< SỬA FONT
        panel.add(lblUsername, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        txtUsername = new JTextField(20);
        txtUsername.setFont(fieldFont); // <<< SỬA FONT
        panel.add(txtUsername, gbc);

        // --- Hàng 2: Mật khẩu ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(labelFont); // <<< SỬA FONT
        panel.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(fieldFont); // <<< SỬA FONT
        panel.add(txtPassword, gbc);

        // --- Hàng 3: Nút Đăng Nhập ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnLogin, gbc);

        add(panel, BorderLayout.CENTER);
    }

    // 5. Hàm gắn sự kiện
    private void addEventHandlers() {
        btnLogin.addActionListener(e -> xuLyDangNhap());
        getRootPane().setDefaultButton(btnLogin);
        
        // Cũng thêm KeyListener cho ô username (cho tiện)
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus(); // Chuyển sang ô mật khẩu
                }
            }
        });
    }

    // 6. Hàm logic đăng nhập (sử dụng plaintext)
    private void xuLyDangNhap() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ tên đăng nhập và mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan tk = taiKhoanDAO.getTaiKhoanByUsername(username);

        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.equals(tk.getMatKhau())) {
            
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Chào " + tk.getTenNguoiDung());
            
            MainFrame mainFrame = new MainFrame(tk);
            mainFrame.setVisible(true);
            
            this.dispose();
            
        } else {
            JOptionPane.showMessageDialog(this, "Mật khẩu không chính xác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 7. Hàm main (Điểm khởi động)
    public static void main(String[] args) {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Không thể thiết lập Nimbus. Sử dụng mặc định.");
        }
        
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}