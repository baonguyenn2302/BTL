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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;

/**
 * JDialog để Thêm (hoặc Sửa) Tác Giả.
 */
public class TacGiaEditDialog extends JDialog {

    // 1. Các trường (Fields)
    private JTextField txtMaTacGia;
    private JTextField txtTenTacGia;
    private JTextField txtEmail;
    private JTextField txtSdt;
    private JTextField txtChucDanh;
    
    // 2. Các nút điều khiển
    private JButton btnLuu;
    private JButton btnHuy;
    
    private boolean saveSuccess = false; // Cờ (flag)
    private TacGiaDAO tacGiaDAO; // DAO để lưu
    private boolean isEditMode = false;
    /**
     * Constructor cho việc THÊM TÁC GIẢ MỚI.
     * @param parent Frame cha (MainFrame)
     * @param newMaTacGia Mã tác giả ĐÃ ĐƯỢC TẠO SẴN
     */
    public TacGiaEditDialog(JFrame parent, String newMaTacGia) {
        super(parent, "Thêm Tác Giả Mới", true); // true = Modal Dialog
        
        this.tacGiaDAO = new TacGiaDAO();
        this.isEditMode = false;
        setSize(500, 350); // Kích thước nhỏ hơn form Sách
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel chính chứa form (dùng GridBagLayout)
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Panel chứa nút Lưu / Hủy
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Gán mã tác giả mới và vô hiệu hóa
        txtMaTacGia.setText(newMaTacGia);
        txtMaTacGia.setEnabled(false);
        txtMaTacGia.setBackground(Color.LIGHT_GRAY);
        
        // Gắn sự kiện
        addEventHandlers();
    }
    // ========== CONSTRUCTOR MỚI CHO CHẾ ĐỘ SỬA ==========
    /**
     * Constructor cho việc SỬA TÁC GIẢ CŨ.
     * @param parent Frame cha (MainFrame)
     * @param tacGiaToEdit Đối tượng Tác Giả (đã lấy từ CSDL)
     */
    public TacGiaEditDialog(JFrame parent, TacGia tacGiaToEdit) {
        super(parent, "Cập Nhật Tác Giả", true); // Đổi tiêu đề
        
        this.tacGiaDAO = new TacGiaDAO();
        this.isEditMode = true; // <<< Đánh dấu là chế độ SỬA
        
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // GỌI HÀM MỚI: Hiển thị dữ liệu cũ lên form
        populateForm(tacGiaToEdit); 
        
        addEventHandlers();
    }
    private void populateForm(TacGia tg) {
        txtMaTacGia.setText(tg.getMaTacGia());
        txtMaTacGia.setEnabled(false); // Mã không được sửa
        txtMaTacGia.setBackground(Color.LIGHT_GRAY);

        txtTenTacGia.setText(tg.getTenTacGia());
        txtEmail.setText(tg.getEmail());
        txtSdt.setText(tg.getSdt());
        txtChucDanh.setText(tg.getChucDanh());
    }
    // Dựng GUI cho form
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Hàng 0: Mã Tác Giả ===
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; 
        panel.add(new JLabel("Mã Tác Giả:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        txtMaTacGia = new JTextField(20);
        txtMaTacGia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(txtMaTacGia, gbc);

        // === Hàng 1: Tên Tác Giả ===
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên Tác Giả (*):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtTenTacGia = new JTextField(30);
        panel.add(txtTenTacGia, gbc);

        // === Hàng 2: Email ===
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        txtEmail = new JTextField(30);
        panel.add(txtEmail, gbc);
        
        // === Hàng 3: Số điện thoại ===
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        txtSdt = new JTextField(30);
        panel.add(txtSdt, gbc);
        
        // === Hàng 4: Chức danh ===
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Chức danh:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        txtChucDanh = new JTextField(30);
        panel.add(txtChucDanh, gbc);

        return panel;
    }
    
    // Dựng GUI cho các nút bấm
    private JPanel createButtonPanel() {
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
    
    // Gắn sự kiện
    private void addEventHandlers() {
        // Nút Hủy: Đơn giản là đóng dialog
        btnHuy.addActionListener(e -> dispose());

        // Nút Lưu
        btnLuu.addActionListener(e -> saveTacGia());
    }

    // Xử lý LƯU Tác Giả
    private void saveTacGia() {
        // 1. Validate dữ liệu
        String tenTacGia = txtTenTacGia.getText().trim();
        if (tenTacGia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tác giả không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenTacGia.requestFocus();
            return;
        }

        // 2. Tạo đối tượng TacGia từ form
        TacGia newTacGia = new TacGia();
        newTacGia.setMaTacGia(txtMaTacGia.getText()); // Lấy mã đã tạo sẵn
        newTacGia.setTenTacGia(tenTacGia);
        newTacGia.setEmail(txtEmail.getText().trim());
        newTacGia.setSdt(txtSdt.getText().trim());
        newTacGia.setChucDanh(txtChucDanh.getText().trim());

        boolean success;
        
        if (isEditMode) {
            // Chế độ Sửa: Gọi hàm UPDATE
            success = tacGiaDAO.updateTacGia(newTacGia);
        } else {
            // Chế độ Thêm: Gọi hàm INSERT
            success = tacGiaDAO.insertTacGia(newTacGia);
        }

        // 4. Đóng form nếu thành công
        if (success) {
            this.saveSuccess = true; 
            dispose(); 
        } else {
            String message = isEditMode ? "cập nhật" : "thêm mới";
            JOptionPane.showMessageDialog(this, 
                "Đã xảy ra lỗi khi " + message + " tác giả.", 
                "Lỗi Database", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Getter này để MainFrame kiểm tra
    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}
