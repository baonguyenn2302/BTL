package QuanLyThuVien;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat; // Thêm import cho Date

/**
 * Dialog hiển thị chi tiết Sách (Phiên bản rút gọn cho Thủ thư).
 * ĐÃ TÁI CẤU TRÚC: Hỗ trợ hiển thị nhiều tác giả.
 */
public class SachDetailDialog extends JDialog {

    private Sach sach;

    // Định dạng ngày
    private SimpleDateFormat sdfDialog = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public SachDetailDialog(Frame parent, Sach sach) {
        super(parent, "Chi Tiết Sách", true);
        this.sach = sach;

        initUI();

        setSize(700, 550); // Có thể điều chỉnh kích thước
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1. TIÊU ĐỀ (TÊN SÁCH) - NORTH ---
        JLabel lblTenSach = new JLabel(sach.getTenSach(), SwingConstants.CENTER);
        lblTenSach.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        mainPanel.add(lblTenSach, BorderLayout.NORTH);

        // --- 2. ẢNH BÌA - WEST --- 
        JPanel coverPanel = new JPanel(new BorderLayout());
        coverPanel.setPreferredSize(new Dimension(200, 280)); 
        JLabel lblAnhBia = new JLabel();
        lblAnhBia.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhBia.setVerticalAlignment(SwingConstants.CENTER);
        String duongDanAnh = sach.getDuongDanAnh();
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            try {
                File imgFile = new File(duongDanAnh);
                if (!imgFile.exists()) {
                     imgFile = new File(System.getProperty("user.dir"), duongDanAnh);
                }
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    Image image = icon.getImage().getScaledInstance(190, 270, Image.SCALE_SMOOTH); 
                    lblAnhBia.setIcon(new ImageIcon(image));
                } else {
                     lblAnhBia.setText("Ảnh không tồn tại");
                     lblAnhBia.setForeground(Color.RED);
                }
            } catch (Exception e) {
                lblAnhBia.setText("[Lỗi tải ảnh]");
                 lblAnhBia.setForeground(Color.RED);
                e.printStackTrace();
            }
        } else {
            lblAnhBia.setText("[Không có ảnh bìa]");
        }
        coverPanel.add(lblAnhBia, BorderLayout.CENTER);
        mainPanel.add(coverPanel, BorderLayout.WEST);

        // --- 3. THÔNG TIN CHI TIẾT - CENTER --- (Đã cập nhật)
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        detailPanel.add(createInfoRow("Mã sách:", sach.getMaSach()));
        detailPanel.add(Box.createVerticalStrut(5));
        
        // <<< THAY ĐỔI DUY NHẤT Ở ĐÂY >>>
        // Thay vì sach.getMaTacGia(), dùng hàm mới getTenTacGiaDisplay()
        detailPanel.add(createInfoRow("Tác giả:", sach.getTenTacGiaDisplay())); 
        
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Nhà xuất bản:", sach.getNhaXuatBan()));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Năm xuất bản:", sach.getNamXuatBan() > 0 ? String.valueOf(sach.getNamXuatBan()) : "N/A"));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Số lượng:", String.valueOf(sach.getSoLuong())));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Vị trí:", sach.getViTri())); // Thêm vị trí
        detailPanel.add(Box.createVerticalStrut(5));
        
        String ngayThemStr = (sach.getNgayThem() != null) ? sdfDialog.format(sach.getNgayThem()) : "N/A";
        detailPanel.add(createInfoRow("Ngày thêm:", ngayThemStr));
        detailPanel.add(Box.createVerticalStrut(15));

        // Tóm tắt (Mô tả)
        JLabel lblTomTatTitle = new JLabel("Mô tả:");
        lblTomTatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTomTatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(lblTomTatTitle);

        JTextArea txtMoTa = new JTextArea(sach.getMoTa() != null ? sach.getMoTa() : "[Chưa có mô tả]");
        txtMoTa.setEditable(false);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(detailPanel.getBackground());
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollMoTa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); 
        detailPanel.add(scrollMoTa);

        mainPanel.add(detailPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Hàm tiện ích tạo một hàng thông tin (Label: Value)
     * (Giữ nguyên hàm này)
     */
    private JPanel createInfoRow(String labelText, String valueText) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(120, 20));
        JLabel value = new JLabel( (valueText != null && !valueText.isEmpty()) ? valueText : "N/A");
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rowPanel.add(label);
        rowPanel.add(value);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height));
        return rowPanel;
    }
}
