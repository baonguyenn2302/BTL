// File: ChiTietDocGiaDialog.java (BẢN SẠCH - ĐÃ XÓA CODE TEST)
package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets; 
import java.awt.GridBagLayout; 
import java.awt.GridBagConstraints;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Component;

public class ChiTietDocGiaDialog extends JDialog {

    // === CÁC BIẾN TOÀN CỤC (FIELDS) ===
    private DocGia docGia; // <<< Biến này sẽ lưu độc giả được truyền vào
    private List<MuonTra> lichSuMuon; //
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Các thành phần GUI
    private JLabel lblAnhDocGia;
    private JTextField txtMaDG, txtHoTen, txtNgaySinh, txtEmail, txtSdt, txtDiaChi, txtTrangThai;
    private JTable lichSuTable;
    private DefaultTableModel lichSuTableModel;

    /**
     * HÀM KHỞI TẠO (CONSTRUCTOR)
     * (Đã sửa lỗi logic: gán tham số cho biến toàn cục)
     */
    public ChiTietDocGiaDialog(JFrame parent, DocGia docGia, List<MuonTra> lichSuMuon) {
        super(parent, "Chi Tiết Độc Giả: " + docGia.getHoTen(), true);
        
        // <<< SỬA LỖI LOGIC QUAN TRỌNG NHẤT >>>
        // Lưu 2 tham số (từ MainFrame) vào 2 biến toàn cục
        this.docGia = docGia;
        this.lichSuMuon = lichSuMuon;
        
        // Thiết lập GUI
        setSize(700, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm các panel con
        mainPanel.add(createThongTinPanel(), BorderLayout.NORTH); // (Sử dụng hàm thiết kế mới)
        mainPanel.add(createLichSuPanel(), BorderLayout.CENTER); 
        add(mainPanel, BorderLayout.CENTER);

        // Nút Đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnDong.addActionListener(e -> dispose());
        buttonPanel.add(btnDong);
        add(buttonPanel, BorderLayout.SOUTH);

        // Đổ dữ liệu
        populateData(); 
    }

    // ================================================================
    // === (THIẾT KẾ MỚI) PANEL THÔNG TIN (Dùng BoxLayout) ===
    // ================================================================
    
    private JPanel createThongTinPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(createTitledPanel("Thông tin độc giả").getBorder());

        // 1. Panel Ảnh (Bên trái)
        lblAnhDocGia = new JLabel();
        lblAnhDocGia.setPreferredSize(new Dimension(150, 150));
        lblAnhDocGia.setBorder(BorderFactory.createEtchedBorder());
        lblAnhDocGia.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhDocGia.setText("(Chưa có ảnh)");
        panel.add(lblAnhDocGia, BorderLayout.WEST);

        // 2. Panel Fields (Ở giữa, dùng BoxLayout xếp dọc)
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));

        // Khởi tạo các ô text
        txtMaDG = createDisabledField(10);
        txtHoTen = createDisabledField(20);
        txtNgaySinh = createDisabledField(10);
        txtTrangThai = createDisabledField(20);
        txtEmail = createDisabledField(30);
        txtSdt = createDisabledField(30);
        txtDiaChi = createDisabledField(30);

        // Row 1: Gồm 2 cặp (Mã ĐG, Họ tên)
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row1.add(new JLabel("Mã ĐG:"));
        row1.add(txtMaDG);
        row1.add(Box.createHorizontalStrut(10)); 
        row1.add(new JLabel("Họ tên:"));
        row1.add(txtHoTen);
        fieldsPanel.add(createAlignedRow(row1));

        // Row 2: Gồm 2 cặp (Ngày sinh, Trạng thái)
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row2.add(new JLabel("Ngày sinh:"));
        row2.add(txtNgaySinh);
        row2.add(Box.createHorizontalStrut(10)); 
        row2.add(new JLabel("Trạng thái:"));
        row2.add(txtTrangThai);
        fieldsPanel.add(createAlignedRow(row2));
        
        // Các hàng còn lại: 1 cặp (Label, Field)
        fieldsPanel.add(Box.createVerticalStrut(5)); 
        fieldsPanel.add(createFieldRow(new JLabel("Email:"), txtEmail));
        fieldsPanel.add(Box.createVerticalStrut(5));
        fieldsPanel.add(createFieldRow(new JLabel("SĐT:"), txtSdt));
        fieldsPanel.add(Box.createVerticalStrut(5));
        fieldsPanel.add(createFieldRow(new JLabel("Địa chỉ:"), txtDiaChi));

        fieldsPanel.add(Box.createVerticalGlue());

        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * (HELPER MỚI) Tạo một hàng (Panel) cho 1 cặp Label-Field
     */
    private JPanel createFieldRow(JLabel label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        label.setPreferredSize(new Dimension(80, 25)); 
        label.setHorizontalAlignment(SwingConstants.RIGHT); 
        
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 
        return panel;
    }
    
    /**
     * (HELPER MỚI) Căn lề trái cho một panel
     */
    private JPanel createAlignedRow(JPanel contentPanel) {
        JPanel alignPanel = new JPanel(new BorderLayout());
        alignPanel.add(contentPanel, BorderLayout.WEST);
        alignPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 
        return alignPanel;
    }

    // ================================================================
    // === CÁC HÀM KHÁC (GIỮ NGUYÊN) ===
    // ================================================================

    /**
     * Panel 2: Lịch sử (JTable) - Không đổi
     */
    private JPanel createLichSuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledPanel("Lịch sử mượn trả").getBorder()); 

        String[] columns = {"Mã Mượn", "Tên Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Trạng Thái"};
        lichSuTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        lichSuTable = new JTable(lichSuTableModel);
        lichSuTable.setRowHeight(25);
        lichSuTable.setFont(new Font("Arial", Font.PLAIN, 12));
        lichSuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(new JScrollPane(lichSuTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Helper: Đổ dữ liệu vào GUI
     * (Đã sửa lỗi đánh máy getNgayTraTe)
     */
    private void populateData() {
        // Hàm này dùng this.docGia (đã được gán ở constructor)
        
        // 1. Tải ảnh
        loadImage(this.docGia.getDuongDanAnh()); //
        
        // 2. Điền thông tin text
        txtMaDG.setText(this.docGia.getMaDocGia()); //
        txtHoTen.setText(this.docGia.getHoTen()); //
        txtNgaySinh.setText(this.docGia.getNgaySinh() != null ? sdf.format(this.docGia.getNgaySinh()) : "N/A"); //
        txtEmail.setText(this.docGia.getEmail()); //
        txtSdt.setText(this.docGia.getSdt()); //
        txtDiaChi.setText(this.docGia.getDiaChi()); //
        txtTrangThai.setText(this.docGia.getTrangThai()); //
        
        // 3. Điền bảng lịch sử
        for (MuonTra mt : this.lichSuMuon) {
            
            // Tên hàm đúng là getNgayTraThucTe()
            String ngayTraStr = (mt.getNgayTraThucTe() != null) 
                    ? sdfTime.format(mt.getNgayTraThucTe())
                    : "N/A";

            lichSuTableModel.addRow(new Object[]{
                mt.getMaMuonTra(),
                mt.getSach().getTenSach(), //
                sdfTime.format(mt.getNgayMuon()),
                sdfTime.format(mt.getNgayHenTra()),
                ngayTraStr,
                mt.getTrangThai() //
            });
        }
    }
    
    // --- Các hàm helper còn lại (Không đổi) ---

    private void loadImage(String imagePath) { 
        int imgWidth = 150;
        int imgHeight = 150;
        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
                lblAnhDocGia.setIcon(new ImageIcon(img));
                lblAnhDocGia.setText(null); 
            } catch (Exception e) {
                e.printStackTrace();
                lblAnhDocGia.setIcon(null);
                lblAnhDocGia.setText("Lỗi tải ảnh");
            }
        } else {
            lblAnhDocGia.setIcon(null);
            lblAnhDocGia.setText("(Chưa có ảnh)");
        }
    }

    private GridBagConstraints getDefaultGBC() { 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private JPanel createTitledPanel(String title) { 
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12), new Color(0, 102, 153))
        );
        return panel;
    }

    private JTextField createDisabledField(int columns) { 
        JTextField field = new JTextField(columns);
        field.setEnabled(false);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setDisabledTextColor(Color.BLACK); 
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEtchedBorder());
        return field;
    }

    // <<< TOÀN BỘ HÀM MAIN VÀ CÁC LỚP GIẢ ĐÃ BỊ XÓA >>>
    
}