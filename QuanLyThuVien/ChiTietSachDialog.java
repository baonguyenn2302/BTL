// TẠO FILE MỚI: ChiTietSachDialog.java
package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * JDialog để hiển thị nhanh Ảnh, Tên và Mã sách khi double-click.
 */
public class ChiTietSachDialog extends JDialog {

    private JLabel lblAnhSach;
    private JTextField txtMaSach, txtTenSach;
    private Sach sach;

    public ChiTietSachDialog(JFrame parent, Sach sach) {
        super(parent, "Chi Tiết Sách: " + sach.getTenSach(), true);
        this.sach = sach;

        setSize(400, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel Ảnh (Giữa)
        lblAnhSach = new JLabel();
        lblAnhSach.setPreferredSize(new Dimension(350, 350));
        lblAnhSach.setBorder(BorderFactory.createEtchedBorder());
        lblAnhSach.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhSach.setText("(Chưa có ảnh)");
        add(lblAnhSach, BorderLayout.CENTER);

        // 2. Panel Thông tin (Dưới)
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
        
        // 3. Nút Đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 12));
        btnDong.addActionListener(e -> dispose());
        buttonPanel.add(btnDong);
        
        // Gói info và nút đóng vào 1 panel chung
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.add(infoPanel, BorderLayout.CENTER);
        southWrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southWrapper, BorderLayout.SOUTH);

        // Tải dữ liệu
        populateData();
    }
    
    // Panel chứa Mã và Tên
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSach = createDisabledField(15);
        txtTenSach = createDisabledField(25);
        txtTenSach.setFont(new Font("Arial", Font.BOLD, 12));

        // Hàng 0: Mã Sách
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Mã Sách:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtMaSach, gbc);

        // Hàng 1: Tên Sách
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên Sách:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtTenSach, gbc);

        return panel;
    }
    
    // Đổ dữ liệu vào
    private void populateData() {
        txtMaSach.setText(sach.getMaSach());
        txtTenSach.setText(sach.getTenSach());
        
        // Gọi hàm tải ảnh (copy từ ChiTietDocGiaDialog)
        loadImage(sach.getDuongDanAnh());
    }

    // --- (Copy hàm helper từ ChiTietDocGiaDialog) ---
    private void loadImage(String imagePath) { 
        int imgWidth = 350; // Kích thước lớn hơn
        int imgHeight = 350;
        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
                lblAnhSach.setIcon(new ImageIcon(img));
                lblAnhSach.setText(null); 
            } catch (Exception e) {
                e.printStackTrace();
                lblAnhSach.setIcon(null);
                lblAnhSach.setText("Lỗi tải ảnh");
            }
        } else {
            lblAnhSach.setIcon(null);
            lblAnhSach.setText("(Chưa có ảnh)");
        }
    }

    // --- (Copy hàm helper từ ChiTietDocGiaDialog) ---
    private JTextField createDisabledField(int columns) { 
        JTextField field = new JTextField(columns);
        field.setEnabled(false);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setDisabledTextColor(Color.BLACK); 
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEtchedBorder());
        return field;
    }
}