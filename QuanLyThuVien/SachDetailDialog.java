package QuanLyThuVien;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SachDetailDialog extends JDialog {

    private Sach sach;

    public SachDetailDialog(Frame parent, Sach sach) {
        super(parent, "Chi Tiết Sách", true); // true = modal
        this.sach = sach;
        
        initUI();
        
        setSize(800, 600); // Kích thước cửa sổ chi tiết
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1. TIÊU ĐỀ (TÊN SÁCH) - NORTH ---
        JLabel lblTenSach = new JLabel(sach.getTenSach(), SwingConstants.CENTER);
        lblTenSach.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(lblTenSach, BorderLayout.NORTH);

        // --- 2. ẢNH BÌA - WEST ---
        JPanel coverPanel = new JPanel(new BorderLayout());
        coverPanel.setPreferredSize(new Dimension(250, 350)); // Kích thước lớn hơn cho ảnh
        JLabel lblAnhBia = new JLabel();
        lblAnhBia.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhBia.setVerticalAlignment(SwingConstants.CENTER);
        
        // Tải ảnh
        String duongDanAnh = sach.getDuongDanAnh();
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            try {
                File imgFile = new File(duongDanAnh);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(duongDanAnh);
                    // Chỉnh kích thước ảnh vừa với panel
                    Image image = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
                    lblAnhBia.setIcon(new ImageIcon(image));
                } else {
                     lblAnhBia.setText("Ảnh không tồn tại");
                }
            } catch (Exception e) {
                lblAnhBia.setText("[Lỗi tải ảnh]");
                e.printStackTrace();
            }
        } else {
            lblAnhBia.setText("[Không có ảnh bìa]");
        }
        coverPanel.add(lblAnhBia, BorderLayout.CENTER);
        mainPanel.add(coverPanel, BorderLayout.WEST);

        // --- 3. THÔNG TIN CHI TIẾT - CENTER ---
        JPanel detailPanel = new JPanel();
        // Dùng BoxLayout để xếp các mục theo chiều dọc
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS)); 

        // Thêm các dòng thông tin (Dùng hàm tiện ích createInfoRow)
        detailPanel.add(createInfoRow("Tác giả:", sach.getTacGia()));
        detailPanel.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
        detailPanel.add(createInfoRow("Nhà xuất bản:", sach.getNhaXuatBan()));
        detailPanel.add(Box.createVerticalStrut(5)); 
        detailPanel.add(createInfoRow("Năm xuất bản:", String.valueOf(sach.getNamXuatBan())));
        detailPanel.add(Box.createVerticalStrut(5)); 
        detailPanel.add(createInfoRow("Số lượng còn:", String.valueOf(sach.getSoLuong())));
        detailPanel.add(Box.createVerticalStrut(5)); 
        detailPanel.add(createInfoRow("Mã sách:", sach.getMaSach()));
        detailPanel.add(Box.createVerticalStrut(15)); // Khoảng cách lớn hơn trước Tóm tắt

        // Tóm tắt (Mô tả)
        JLabel lblTomTatTitle = new JLabel("Tóm tắt:");
        lblTomTatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTomTatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(lblTomTatTitle);
        
        JTextArea txtMoTa = new JTextArea(sach.getMoTa());
        txtMoTa.setEditable(false);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(detailPanel.getBackground()); // Màu nền giống panel
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Đặt kích thước tối đa để JTextArea không quá lớn
        scrollMoTa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); 
        detailPanel.add(scrollMoTa);

        mainPanel.add(detailPanel, BorderLayout.CENTER);

        // --- 4. TỆP ĐỌC THỬ - SOUTH ---
        String duongDanXemTruoc = sach.getDuongDanXemTruoc();
        if (duongDanXemTruoc != null && !duongDanXemTruoc.isEmpty()) {
            JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            previewPanel.setBorder(BorderFactory.createTitledBorder("Tệp đọc thử"));

            // Lấy tên file từ đường dẫn
            File previewFile = new File(duongDanXemTruoc);
            JLabel lblFileName = new JLabel(previewFile.getName()); 
            
            JButton btnOpenPreview = new JButton("Mở đọc thử");
            btnOpenPreview.addActionListener(e -> moFileXemTruoc(previewFile));

            previewPanel.add(lblFileName);
            previewPanel.add(btnOpenPreview);
            mainPanel.add(previewPanel, BorderLayout.SOUTH);
        }

        add(mainPanel);
    }

    /**
     * Hàm tiện ích tạo một hàng thông tin (Label: Value)
     */
    private JPanel createInfoRow(String labelText, String valueText) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Quan trọng cho BoxLayout

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(120, 20)); // Cố định chiều rộng Label

        JLabel value = new JLabel( (valueText != null && !valueText.isEmpty()) ? valueText : "N/A");
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        rowPanel.add(label);
        rowPanel.add(value);
        
        // Đặt kích thước tối đa để khớp BoxLayout
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height)); 
        return rowPanel;
    }

    /**
     * Mở file xem trước (PDF) bằng ứng dụng mặc định của hệ thống
     */
    private void moFileXemTruoc(File fileToOpen) {
        if (!fileToOpen.exists()) {
             JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại tại đường dẫn:\n" + fileToOpen.getAbsolutePath(), "Không tìm thấy file", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        // Kiểm tra xem Desktop có được hỗ trợ không
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(fileToOpen);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Không thể mở file đọc thử.\nLỗi: " + ex.getMessage(), "Lỗi mở file", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (SecurityException ex) {
                 JOptionPane.showMessageDialog(this, "Không có quyền truy cập file đọc thử.\nLỗi: " + ex.getMessage(), "Lỗi bảo mật", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tính năng mở file không được hỗ trợ trên hệ thống này.", "Lỗi hệ thống", JOptionPane.WARNING_MESSAGE);
        }
    }
}
