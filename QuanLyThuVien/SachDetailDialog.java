package QuanLyThuVien;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files; // Import Files
import java.nio.file.StandardCopyOption; // Import StandardCopyOption
import javax.swing.JFileChooser; // Import JFileChooser
import javax.swing.JOptionPane; // Import JOptionPane

public class SachDetailDialog extends JDialog {

    private Sach sach;
    // Khai báo SachDAO để dùng trong hàm tải file
    private SachDAO sachDAO;
    // Có thể thêm TacGiaDAO nếu muốn lấy thông tin chi tiết hơn
    // private TacGiaDAO tacGiaDAO;

    public SachDetailDialog(Frame parent, Sach sach) {
        super(parent, "Chi Tiết Sách", true); // true = modal
        this.sach = sach;
        this.sachDAO = new SachDAO(); // Khởi tạo SachDAO ở đây
        // Khởi tạo TacGiaDAO nếu cần
        // this.tacGiaDAO = new TacGiaDAO();

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

        // --- Cải thiện logic tải ảnh ---
        String duongDanAnh = sach.getDuongDanAnh();
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            try {
                // Thử đường dẫn tương đối trước
                File imgFile = new File(duongDanAnh);
                // Nếu không tồn tại, thử đường dẫn tuyệt đối dựa trên thư mục chạy
                if (!imgFile.exists()) {
                     imgFile = new File(System.getProperty("user.dir"), duongDanAnh);
                }

                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    // Chỉnh kích thước ảnh vừa với panel
                    Image image = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
                    lblAnhBia.setIcon(new ImageIcon(image));
                } else {
                     // Log lỗi hoặc hiển thị thông báo rõ hơn
                     System.err.println("Không tìm thấy file ảnh: " + duongDanAnh + " hoặc " + imgFile.getAbsolutePath());
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
        // --- Kết thúc cải thiện logic tải ảnh ---

        // --- 3. THÔNG TIN CHI TIẾT - CENTER ---
        JPanel detailPanel = new JPanel();
        // Dùng BoxLayout để xếp các mục theo chiều dọc
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        // --- SỬA CÁCH LẤY TÊN TÁC GIẢ ---
        // Lấy tên tác giả từ maTacGia (vì ta đang lưu tên vào đó)
        String tenTacGiaDisplay = sach.getMaTacGia();
        // TODO (Nâng cao): Nếu có TacGiaDAO và muốn hiển thị tên thật từ bảng TACGIA:
        // TacGiaDAO tacGiaDAO = new TacGiaDAO();
        // TacGia tacGiaObj = tacGiaDAO.getTacGiaByMa(sach.getMaTacGia());
        // if (tacGiaObj != null) tenTacGiaDisplay = tacGiaObj.getTenTacGia();
        detailPanel.add(createInfoRow("Tác giả:", tenTacGiaDisplay));
        // --- KẾT THÚC SỬA ---

        detailPanel.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ
        detailPanel.add(createInfoRow("Nhà xuất bản:", sach.getNhaXuatBan()));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Năm xuất bản:", sach.getNamXuatBan() > 0 ? String.valueOf(sach.getNamXuatBan()) : "N/A"));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Số lượng còn:", String.valueOf(sach.getSoLuong())));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Mã sách:", sach.getMaSach()));
         detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Lượt xem:", String.valueOf(sach.getLuotXem())));
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(createInfoRow("Lượt tải:", String.valueOf(sach.getLuotTai())));
        detailPanel.add(Box.createVerticalStrut(15)); // Khoảng cách lớn hơn trước Tóm tắt

        // Tóm tắt (Mô tả)
        JLabel lblTomTatTitle = new JLabel("Tóm tắt:");
        lblTomTatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTomTatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(lblTomTatTitle);

        JTextArea txtMoTa = new JTextArea(sach.getMoTa() != null ? sach.getMoTa() : "[Chưa có mô tả]");
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

            JButton btnOpenPreview = new JButton("📖 Mở đọc thử");
            btnOpenPreview.addActionListener(e -> moFileXemTruoc(previewFile));
            JButton btnDownloadPreview = new JButton("💾 Tải xuống");
            btnDownloadPreview.addActionListener(e -> taiFileXemTruoc(previewFile, sach.getMaSach()));


            previewPanel.add(lblFileName);
            previewPanel.add(btnOpenPreview);
            previewPanel.add(btnDownloadPreview);
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

        // Xử lý giá trị null hoặc rỗng
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
         // Thử đường dẫn tương đối trước
         File originalFile = fileToOpen; // Giữ lại đường dẫn gốc để thông báo lỗi
         if (!fileToOpen.exists()) {
             // Nếu không tồn tại, thử đường dẫn tuyệt đối dựa trên thư mục chạy
             File absoluteFile = new File(System.getProperty("user.dir"), fileToOpen.getPath());
             if (!absoluteFile.exists()) {
                 JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại.\nĐã kiểm tra:\n1. " + originalFile.getPath() + "\n2. " + absoluteFile.getAbsolutePath(), "Không tìm thấy file", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             fileToOpen = absoluteFile; // Dùng đường dẫn tuyệt đối nếu tìm thấy
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

     /**
      * Tải file xem trước về máy người dùng.
      */
     private void taiFileXemTruoc(File fileToDownload, String maSach) {
         // Thử đường dẫn tương đối trước
         File originalFile = fileToDownload; // Giữ lại đường dẫn gốc để thông báo lỗi
         if (!fileToDownload.exists()) {
             // Nếu không tồn tại, thử đường dẫn tuyệt đối
             File absoluteFile = new File(System.getProperty("user.dir"), fileToDownload.getPath());
             if (!absoluteFile.exists()) {
                 JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại để tải về.\nĐã kiểm tra:\n1. " + originalFile.getPath() + "\n2. " + absoluteFile.getAbsolutePath(), "Không tìm thấy file", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             fileToDownload = absoluteFile; // Dùng đường dẫn tuyệt đối nếu tìm thấy
         }

         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setDialogTitle("Lưu file đọc thử");
         // Đặt tên file mặc định khi lưu
         fileChooser.setSelectedFile(new File(fileToDownload.getName()));

         int userSelection = fileChooser.showSaveDialog(this);

         if (userSelection == JFileChooser.APPROVE_OPTION) {
             File fileToSave = fileChooser.getSelectedFile();

             // Kiểm tra xem người dùng có chọn ghi đè file đã tồn tại không
             if (fileToSave.exists()) {
                 int overwrite = JOptionPane.showConfirmDialog(this,
                         "File '" + fileToSave.getName() + "' đã tồn tại.\nBạn có muốn ghi đè không?",
                         "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION);
                 if (overwrite == JOptionPane.NO_OPTION) {
                     return; // Hủy nếu không muốn ghi đè
                 }
             }

             try {
                 // Copy file
                 Files.copy(fileToDownload.toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                 // Tăng lượt tải (nếu thành công)
                 // Cần đảm bảo sachDAO được khởi tạo
                 if (sachDAO != null) {
                    sachDAO.tangLuotTai(maSach);
                 } else {
                    // Log lỗi nếu sachDAO là null (không nên xảy ra nếu khởi tạo trong constructor)
                    System.err.println("Lỗi nghiêm trọng: sachDAO chưa được khởi tạo trong SachDetailDialog khi cố gắng tăng lượt tải.");
                 }
                 JOptionPane.showMessageDialog(this, "Tải file thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
             } catch (IOException ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
             } catch (SecurityException secEx) {
                  JOptionPane.showMessageDialog(this, "Không có quyền ghi file vào vị trí đã chọn.\nLỗi: " + secEx.getMessage(), "Lỗi quyền ghi", JOptionPane.ERROR_MESSAGE);
                 secEx.printStackTrace();
             }
         }
     }
}

