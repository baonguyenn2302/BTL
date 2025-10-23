package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import java.awt.FlowLayout; // Thêm import
import javax.swing.JSeparator;

public class RegisterForm extends JFrame {

    // DAO
    private DocGiaDAO docGiaDAO;
    private UserDAO userDAO;

    // Các trường nhập liệu
    private JTextField txtMaDocGia, txtHoTen, txtNgaySinh, txtDiaChi, txtEmail, txtSdt;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau, txtXacNhanMatKhau;
    
    private JButton btnDangKy, btnQuayLai;

    public RegisterForm() {
        setTitle("ĐĂNG KÝ TÀI KHOẢN ĐỘC GIẢ");
        setSize(450, 600); // Điều chỉnh kích thước cho giao diện 1 cột
        // DISPOSE_ON_CLOSE: Chỉ đóng cửa sổ này, không tắt toàn bộ ứng dụng
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); // Căn giữa

        docGiaDAO = new DocGiaDAO();
        userDAO = new UserDAO();

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Tăng lề

        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // --- GIAO DIỆN ĐÃ SỬA: Dùng 1 cột duy nhất ---
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 hàng, 2 cột (Label + Field)
        
        // --- Thông tin cá nhân ---
        fieldsPanel.add(new JLabel("Mã Độc Giả (*):"));
        txtMaDocGia = new JTextField();
        fieldsPanel.add(txtMaDocGia);

        fieldsPanel.add(new JLabel("Họ Tên (*):"));
        txtHoTen = new JTextField();
        fieldsPanel.add(txtHoTen);

        fieldsPanel.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"));
        txtNgaySinh = new JTextField();
        fieldsPanel.add(txtNgaySinh);

        fieldsPanel.add(new JLabel("Địa Chỉ:"));
        txtDiaChi = new JTextField();
        fieldsPanel.add(txtDiaChi);

        fieldsPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        fieldsPanel.add(txtEmail);

        fieldsPanel.add(new JLabel("Số Điện Thoại:"));
        txtSdt = new JTextField();
        fieldsPanel.add(txtSdt);
        
        // Đường phân cách
        fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        // --- Thông tin tài khoản ---
        fieldsPanel.add(new JLabel("Tên Đăng Nhập (*):"));
        txtTenDangNhap = new JTextField();
        fieldsPanel.add(txtTenDangNhap);

        fieldsPanel.add(new JLabel("Mật Khẩu (*):"));
        txtMatKhau = new JPasswordField();
        fieldsPanel.add(txtMatKhau);

        fieldsPanel.add(new JLabel("Xác Nhận Mật Khẩu (*):"));
        txtXacNhanMatKhau = new JPasswordField();
        fieldsPanel.add(txtXacNhanMatKhau);
        
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Panel Nút ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnDangKy = new JButton("Đăng ký");
        btnQuayLai = new JButton("Quay lại Đăng nhập");
        buttonPanel.add(btnDangKy);
        buttonPanel.add(btnQuayLai);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // --- Xử lý sự kiện (Giữ nguyên) ---
        btnQuayLai.addActionListener(e -> dispose()); // Đóng form đăng ký
        btnDangKy.addActionListener(e -> xuLyDangKy());
    }

    /**
     * === LOGIC LIÊN KẾT (ĐÃ CÓ SẴN) ===
     * Xử lý đăng ký: Tạo Độc Giả -> Tạo User
     */
    private void xuLyDangKy() {
        // --- 1. Lấy và Validate (Kiểm tra) dữ liệu ---
        String maDocGia = txtMaDocGia.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String xacNhanMK = new String(txtXacNhanMatKhau.getPassword());

        if (maDocGia.isEmpty() || hoTen.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Các trường có dấu (*) không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!matKhau.equals(xacNhanMK)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu và Xác nhận mật khẩu không khớp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // --- 2. Tạo đối tượng DocGia ---
        // (Giả sử file DocGia.java của bạn đã có 'diaChi')
        DocGia dg = new DocGia();
        dg.setMaDocGia(maDocGia);
        dg.setHoTen(hoTen);
        dg.setDiaChi(txtDiaChi.getText().trim());
        dg.setEmail(txtEmail.getText().trim());
        dg.setSdt(txtSdt.getText().trim());
        dg.setBlocked(false); // Mặc định tài khoản mới là active
        
        // Xử lý ngày sinh
        try {
            String ngaySinhStr = txtNgaySinh.getText().trim();
            if (!ngaySinhStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date ngaySinh = sdf.parse(ngaySinhStr);
                dg.setNgaySinh(ngaySinh);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng Ngày Sinh không hợp lệ (phải là dd/MM/yyyy)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 3. Tạo đối tượng User ---
        User user = new User();
        user.setTenDangNhap(tenDangNhap);
        user.setMatKhau(matKhau); // LƯU Ý: Nên mã hóa mật khẩu trước khi lưu
        user.setVaiTro("Độc giả");
        user.setMaLienKet(maDocGia); // Liên kết tài khoản với độc giả

        // --- 4. Thực hiện gọi DAO (Liên kết) ---
        try {
            // Bước 4a: Thêm Độc Giả trước
            boolean themDocGiaOK = docGiaDAO.themDocGia(dg);
            
            if (themDocGiaOK) {
                // Bước 4b: Nếu thêm ĐG thành công, mới thêm User
                boolean themUserOK = userDAO.themUser(user);
                
                if (themUserOK) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng quay lại để đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Đóng form đăng ký
                } else {
                    // Lỗi: Đã thêm ĐG nhưng không thêm được User (lỗi trùng Tên ĐN)
                    JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên đăng nhập này đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    // (Lý tưởng nhất là nên xóa ĐG vừa tạo - Rollback)
                    // docGiaDAO.xoaDocGia(maDocGia); 
                }
            } else {
                // Lỗi: Không thêm được ĐG (lỗi trùng Mã ĐG)
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Mã Độc Giả này đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // (Bạn có thể thêm hàm main để chạy thử riêng file này)
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegisterForm().setVisible(true);
        });
    }
    */
}