package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JPasswordField; // ĐÃ THÊM: Cần cho form đổi mật khẩu
import java.awt.GridBagLayout; // ĐÃ THÊM: Cần cho layout đổi mật khẩu
import java.awt.GridBagConstraints; // ĐÃ THÊM: Cần cho layout đổi mật khẩu

// === IMPORT TỪ BẢN SỬA LỖI TRƯỚC ===
import QuanLyThuVien.SachDetailDialog; 
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent; 
// === KẾT THÚC IMPORT ===

// Import Models and DAOs
import QuanLyThuVien.DocGia;
import QuanLyThuVien.DocGiaDAO;
import QuanLyThuVien.Sach;
import QuanLyThuVien.SachDAO;
import QuanLyThuVien.User;
import QuanLyThuVien.UserDAO;


public class AdminMainForm extends JFrame {

    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;
    
    // Doc Gia components
    private JTable docGiaTable;
    private DefaultTableModel docGiaTableModel;
    private DocGiaDAO docGiaDAO;
    private JTextField docGiaSearchField;

    // Sach components
    private JTable sachTable;
    private DefaultTableModel sachTableModel;
    private SachDAO sachDAO;
    private JTextField sachSearchField;

    // --- Date Formatter ---
    private SimpleDateFormat sdfAdmin = new SimpleDateFormat("dd/MM/yyyy");

    // =========================================================================
    // === ĐÃ CẬP NHẬT: User/Password Components ===
    // =========================================================================
    private UserDAO userDAO;
    private User currentUser; // <-- NÂNG CẤP: Lưu người dùng đã đăng nhập
    private final String currentUsername = "admin"; 
    /**
     * NÂNG CẤP: Constructor nhận User đã đăng nhập
     * @param loggedInUser Đối tượng User từ LoginForm
     */
    public AdminMainForm(User loggedInUser) { // <-- NÂNG CẤP
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());

        this.currentUser = loggedInUser; // <-- NÂNG CẤP: Lưu user

        docGiaDAO = new DocGiaDAO();
        sachDAO = new SachDAO();
        userDAO = new UserDAO(); // ĐÃ KHỞI TẠO USERDAO

        // NÂNG CẤP: Hiển thị tên user đăng nhập
        headerLabel = new JLabel("TRANG CHỦ QUẢN TRỊ VIÊN | Chào, " + this.currentUser.getTenDangNhap()); 
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBackground(new Color(30, 144, 255)); headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true); headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setPreferredSize(new Dimension(getWidth(), 60));
        add(headerLabel, BorderLayout.NORTH);

        createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        mainContentPanel = new JPanel(); mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 240, 240));
        add(mainContentPanel, BorderLayout.CENTER);

        showPanel("Tổng quan");
        setLocationRelativeTo(null);
    }

    /**
     * HÀM MỚI TỪ BẠN: Xử lý Đăng xuất (Đã sửa để mở lại LoginForm)
     */
    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất không?",
            "Xác nhận Đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // <<< SỬA LỖI: Mở lại LoginForm (logic từ hàm showPanel cũ)
            SwingUtilities.invokeLater(() -> new DangKyForm().setVisible(true)); 
        }
    }

    /**
     * HÀM MỚI TỪ BẠN: Tạo menu (đã tách nút Đăng xuất)
     */
    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(220, getHeight()));
        menuPanel.setLayout(new GridLayout(8, 1, 0, 10));

        String[] menuItems = {
            "Tổng quan",
            "Quản lý Sách",
            "Quản lý Độc giả",
            "Quản lý Mượn/Trả",
            "Báo cáo & Thống kê",
            "Đổi mật khẩu"
        };

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(buttonFont);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(70, 70, 70));
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(200, 40));

            button.addActionListener(e -> showPanel(item));
            menuPanel.add(button);
        }

        JButton logoutButton = new JButton("ĐĂNG XUẤT");
        logoutButton.setFont(buttonFont);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(178, 34, 34)); // Màu đỏ
        logoutButton.setFocusPainted(false);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(200, 40));

        logoutButton.addActionListener(this::btnDangXuatActionPerformed);
        menuPanel.add(logoutButton);

        menuPanel.add(new JLabel());
    }

    /**
     * HÀM MỚI TỪ BẠN: Hiển thị panel (đã bỏ case Đăng xuất)
     */
    private void showPanel(String panelName) {
        mainContentPanel.removeAll();
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.setBackground(Color.WHITE);

        switch (panelName) {
            case "Tổng quan":
                newPanel.add(new JLabel("<< DASHBOARD >>", SwingConstants.CENTER));
                break;
            case "Quản lý Sách":
                newPanel = createBookManagementPanel(); // <<< Dùng bản đã sửa lỗi
                break;
            case "Quản lý Độc giả":
                newPanel = createDocGiaManagementPanel(); // <<< Dùng bản đã sửa lỗi
                break;
            case "Quản lý Mượn/Trả":
                newPanel.add(new JLabel("<< MƯỢN/TRẢ >>", SwingConstants.CENTER));
                break;
            case "Báo cáo & Thống kê":
                newPanel.add(new JLabel("<< BÁO CÁO & THỐNG KÊ >>", SwingConstants.CENTER));
                break;
            case "Đổi mật khẩu":
                // ĐÃ SỬA: Thay thế placeholder bằng hàm tạo form thực tế
                newPanel = createChangePasswordPanel(); 
                break;
            default:
                newPanel.add(new JLabel(panelName + " chưa có.", SwingConstants.CENTER));
        }
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // =========================================================================
    // === QUẢN LÝ SÁCH (PHIÊN BẢN ĐÃ SỬA LỖI CỦA TÔI) ===
    // =========================================================================

    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ THÔNG TIN SÁCH"); title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(new Color(0, 102, 153)); topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        
        searchPanel.add(new JLabel("Tìm kiếm (mã, tên, tác giả, NXB):")); // <<< ĐÃ SỬA
        
        sachSearchField = new JTextField(25); JButton searchButton = new JButton("Tìm"); JButton viewAllButton = new JButton("Xem tất cả"); searchPanel.add(sachSearchField); searchPanel.add(searchButton); searchPanel.add(viewAllButton); topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Số Lượng", "Trạng Thái", "Ngày Cập Nhật"};

        sachTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        sachTable = new JTable(sachTableModel); sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); sachTable.setRowHeight(25);
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        JTableHeader header = sachTable.getTableHeader(); header.setDefaultRenderer(new HeaderRenderer(sachTable));
        JScrollPane scrollPane = new JScrollPane(sachTable); panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Sách"); JButton editButton = new JButton("Sửa Sách"); JButton deleteButton = new JButton("Xóa Sách");
        controlPanel.add(addButton); controlPanel.add(editButton); controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        loadSachData();

        searchButton.addActionListener(e -> timKiemSach()); viewAllButton.addActionListener(e -> loadSachData());
        addButton.addActionListener(e -> themSach()); editButton.addActionListener(e -> suaSach()); deleteButton.addActionListener(e -> xoaSach());

        // === NÂNG CẤP: Sửa sự kiện double-click ===
        sachTable.addMouseListener(new MouseAdapter() { 
            @Override public void mouseClicked(MouseEvent evt) { 
                if (evt.getClickCount() == 2) {
                    int row = sachTable.getSelectedRow();
                    if (row != -1) { 
                        String maSach = sachTableModel.getValueAt(row, 0).toString(); 
                        Sach selectedSach = sachDAO.getSachByMaSach(maSach);
                        if (selectedSach != null) {
                            // Mở JDialog (dùng 'AdminMainForm.this' vì đang ở trong JFrame)
                            SachDetailDialog detailDialog = new SachDetailDialog(AdminMainForm.this, selectedSach);
                            detailDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(AdminMainForm.this, "Không tìm thấy chi tiết sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        return panel;
    }

    /**
     * Tải dữ liệu SÁCH (PHIÊN BẢN ĐÃ SỬA LỖI CỦA TÔI)
     */
    private void loadSachData() {
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.getAllSach();
            for (Sach s : danhSach) {
                String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                sachTableModel.addRow(new Object[]{
                    s.getMaSach(), 
                    s.getTenSach(), 
                    s.getMaTacGia(), // <<< ĐÃ SỬA: Dùng getMaTacGia()
                    s.getNhaXuatBan(),
                    s.getSoLuong(), 
                    s.getTrangThai(), 
                    ngayThemStr 
                });
            }
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    /**
     * Tìm kiếm sách (PHIÊN BẢN ĐÃ SỬA LỖI CỦA TÔI)
     */
    private void timKiemSach() {
        String keyword = sachSearchField.getText().trim(); 
        if (keyword.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        try {
            sachTableModel.setRowCount(0);
            // <<< ĐÃ SỬA: Gọi đúng hàm timKiemSachNangCao
            List<Sach> danhSach = sachDAO.timKiemSachNangCao(keyword, "Tất cả"); 
            
            if (danhSach.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); 
            }
            else {
                for (Sach s : danhSach) {
                    String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                    sachTableModel.addRow(new Object[]{ 
                        s.getMaSach(), 
                        s.getTenSach(), 
                        s.getMaTacGia(), // <<< ĐÃ SỬA: Dùng getMaTacGia()
                        s.getNhaXuatBan(), 
                        s.getSoLuong(), 
                        s.getTrangThai(), 
                        ngayThemStr 
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi khi tìm Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    // === ĐÃ SỬA: Kích hoạt lại các Dialog ===
    private void themSach() { 
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, null); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadSachData();
    }
    private void suaSach() { 
        int row = sachTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = sachTableModel.getValueAt(row, 0).toString(); Sach s = sachDAO.getSachByMaSach(ma); if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, s); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadSachData();
    }
    private void xoaSach() { /* ... Giữ nguyên logic của bạn ... */
        int row = sachTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = sachTableModel.getValueAt(row, 0).toString(); String ten = sachTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (sachDAO.xoaSach(ma)) { JOptionPane.showMessageDialog(this, "Xóa thành công!"); loadSachData(); } else { JOptionPane.showMessageDialog(this, "Xóa thất bại (Sách đang mượn?).", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi xóa Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }


    // =========================================================================
    // === QUẢN LÝ ĐỘC GIẢ (PHIÊN BẢN ĐÃ SỬA LỖI CỦA TÔI) ===
    // =========================================================================
    private JPanel createDocGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5)); JLabel title = new JLabel("QUẢN LÝ ĐỘC GIẢ"); title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(new Color(0, 102, 153)); topPanel.add(title, BorderLayout.NORTH); JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchPanel.add(new JLabel("Tìm kiếm (tên, email):")); docGiaSearchField = new JTextField(25); JButton searchButton = new JButton("Tìm"); JButton viewAllButton = new JButton("Xem tất cả"); searchPanel.add(docGiaSearchField); searchPanel.add(searchButton); searchPanel.add(viewAllButton); topPanel.add(searchPanel, BorderLayout.CENTER); panel.add(topPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Trạng Thái"}; docGiaTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } }; docGiaTable = new JTable(docGiaTableModel); docGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); docGiaTable.setRowHeight(25); JScrollPane scrollPane = new JScrollPane(docGiaTable); panel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); JButton addButton = new JButton("Thêm Độc Giả"); JButton editButton = new JButton("Sửa Thông Tin"); JButton deleteButton = new JButton("Xóa Độc Giả"); JButton blockButton = new JButton("Khóa / Mở khóa"); controlPanel.add(addButton); controlPanel.add(editButton); controlPanel.add(deleteButton); controlPanel.add(blockButton); panel.add(controlPanel, BorderLayout.SOUTH);
        loadDocGiaData(); searchButton.addActionListener(e -> timKiemDocGia()); viewAllButton.addActionListener(e -> loadDocGiaData()); 
        
        // === ĐÃ SỬA: Kích hoạt lại các Dialog ===
        addButton.addActionListener(e -> themDocGia()); 
        editButton.addActionListener(e -> suaDocGia()); 
        
        deleteButton.addActionListener(e -> xoaDocGia()); blockButton.addActionListener(e -> khoaMoKhoaDocGia());
        return panel;
    }
    
    private void loadDocGiaData() { /* ... Giữ nguyên logic ... */ 
        try { docGiaTableModel.setRowCount(0); List<DocGia> danhSach = docGiaDAO.getAllDocGia(); SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy"); for (DocGia dg : danhSach) { String trangThai = dg.isBlocked() ? "Bị khóa" : "Hoạt động"; String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : ""; docGiaTableModel.addRow(new Object[]{ dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(), dg.getSdt(), trangThai }); } } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu độc giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }
    private void timKiemDocGia() { /* ... Giữ nguyên logic ... */ 
        String keyword = docGiaSearchField.getText().trim(); if (keyword.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        try { docGiaTableModel.setRowCount(0); List<DocGia> danhSach = docGiaDAO.timKiemDocGia(keyword); SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy"); if (danhSach.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); } for (DocGia dg : danhSach) { String trangThai = dg.isBlocked() ? "Bị khóa" : "Hoạt động"; String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : ""; docGiaTableModel.addRow(new Object[]{ dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(), dg.getSdt(), trangThai }); } } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi khi tìm Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }
    
    // === ĐÃ SỬA: Kích hoạt lại các Dialog ===
    private void themDocGia() { 
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, null); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void suaDocGia() { 
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); DocGia dg = docGiaDAO.getDocGiaByMaDocGia(ma); if (dg == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, dg); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void xoaDocGia() { /* ... Giữ nguyên logic của bạn ... */
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); int confirm = JOptionPane.showConfirmDialog(this, "Xóa độc giả '" + ma + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (docGiaDAO.xoaDocGia(ma)) { JOptionPane.showMessageDialog(this, "Xóa thành công!"); loadDocGiaData(); } else { JOptionPane.showMessageDialog(this, "Xóa thất bại (Độc giả đang mượn?).", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi xóa Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }
    private void khoaMoKhoaDocGia() { /* ... Giữ nguyên logic của bạn ... */
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để Khóa/Mở.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); String status = docGiaTableModel.getValueAt(row, 5).toString();
        boolean block = status.equals("Hoạt động"); String action = block ? "KHÓA" : "MỞ KHÓA";
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn " + action + " tài khoản '" + ma + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (docGiaDAO.khoaMoKhoaDocGia(ma, block)) { JOptionPane.showMessageDialog(this, action + " thành công!"); loadDocGiaData(); } else { JOptionPane.showMessageDialog(this, action + " thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi " + action + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }


    // =========================================================================
    // === HÀM MỚI TỪ BẠN: ĐỔI MẬT KHẨU ===
    // =========================================================================
     private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        // THAY ĐỔI: SỬA TIÊU ĐỀ TitledBorder
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 3),
                // CHỈ GIỮ "ĐỔI MẬT KHẨU" ĐỂ TIÊU ĐỀ KHÔNG BỊ QUÁ DÀI
                "ĐỔI MẬT KHẨU TÀI KHOẢN ", // Dùng một tiêu đề ngắn hơn và rõ ràng hơn
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 36),
                new Color(30, 144, 255)
            )
        ));
        formPanel.setPreferredSize(new Dimension(650, 350));

        // Components
        JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
        oldPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        final JPasswordField oldPassField = new JPasswordField(15);
        oldPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        oldPassField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        newPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        final JPasswordField newPassField = new JPasswordField(15);
        newPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        newPassField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        JLabel confirmPassLabel = new JLabel("Xác nhận MK mới:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        final JPasswordField confirmPassField = new JPasswordField(15);
        confirmPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        confirmPassField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        final JButton changeButton = new JButton("ĐỔI MẬT KHẨU"); 
        changeButton.setBackground(new Color(30, 144, 255));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        changeButton.setFocusPainted(false);
        changeButton.setPreferredSize(new Dimension(250, 50));
        changeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); 

        // Row 1: Mật khẩu cũ
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.EAST; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(oldPassLabel, gbc);

        gbc.gridx = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        formPanel.add(oldPassField, gbc);

        // Row 2: Mật khẩu mới
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(newPassLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(newPassField, gbc);

        // Row 3: Xác nhận MK mới
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(confirmPassField, gbc);

        // Row 4: Nút ĐỔI MẬT KHẨU
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(15, 15, 15, 15); 

        formPanel.add(changeButton, gbc);

        panel.add(formPanel);

        // Gán sự kiện cho nút
        changeButton.addActionListener(e -> doiMatKhau(oldPassField, newPassField, confirmPassField));

        return panel;
    }

    /**
     * Xử lý logic đổi mật khẩu và cập nhật vào database.
     */
    private void doiMatKhau(JPasswordField oldPassField, JPasswordField newPassField, JPasswordField confirmPassField) {
        String oldPassword = new String(oldPassField.getPassword());
        String newPassword = new String(newPassField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và Xác nhận Mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            oldPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
            return;
        }

        if (newPassword.length() < 6) {
               JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Kiểm tra mật khẩu cũ (Cần UserDAO.kiemTraMatKhauCu)
            // LƯU Ý: Hàm kiemTraMatKhauCu và doiMatKhau cần được định nghĩa trong UserDAO
            if (!userDAO.kiemTraMatKhauCu(currentUsername, oldPassword)) { 
                JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                oldPassField.setText("");
                return;
            }

            // 2. Cập nhật mật khẩu mới
            if (userDAO.doiMatKhau(currentUsername, newPassword)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                oldPassField.setText("");
                newPassField.setText("");
                confirmPassField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi đổi mật khẩu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    // =========================================================================
    // === TIỆN ÍCH CHUNG (Giữ nguyên) ===
    // =========================================================================
    class HeaderRenderer implements TableCellRenderer {
        private JTable table; private JPanel panel; private JLabel label; private JButton filterButton;
        public HeaderRenderer(JTable table) { this.table = table; panel = new JPanel(new BorderLayout()); label = new JLabel(); filterButton = new JButton("▼"); filterButton.setFont(new Font("Segoe UI", Font.PLAIN, 10)); filterButton.setPreferredSize(new Dimension(20, 16)); filterButton.setMargin(new Insets(0, 0, 0, 0)); filterButton.setFocusPainted(false); filterButton.setContentAreaFilled(false); filterButton.setBorderPainted(false); label.setHorizontalAlignment(SwingConstants.CENTER); label.setFont(new Font("Segoe UI", Font.BOLD, 12)); panel.add(label, BorderLayout.CENTER); panel.add(filterButton, BorderLayout.EAST); panel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { label.setText(value != null ? value.toString() : ""); for (ActionListener al : filterButton.getActionListeners()) filterButton.removeActionListener(al); final int col = column; filterButton.addActionListener(e -> showColumnFilterDialog(table, col)); return panel; }
    }
    private void showColumnFilterDialog(JTable table, int column) {
        String columnName = table.getColumnName(column); JDialog filterDialog = new JDialog(this, "Lọc " + columnName, true); filterDialog.setLayout(new BorderLayout()); filterDialog.setSize(300, 150); filterDialog.setLocationRelativeTo(this); JPanel contentPanel = new JPanel(new BorderLayout(10, 10)); contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); JLabel instruction = new JLabel("Nhập giá trị lọc:"); JTextField filterField = new JTextField(); JPanel buttonPanel = new JPanel(new FlowLayout()); JButton applyButton = new JButton("Lọc"); JButton cancelButton = new JButton("Hủy"); applyButton.addActionListener(e -> { String filterValue = filterField.getText(); System.out.println("Lọc cột " + columnName + ": " + filterValue); /* TODO: Logic lọc */ filterDialog.dispose(); }); cancelButton.addActionListener(e -> filterDialog.dispose()); buttonPanel.add(applyButton); buttonPanel.add(cancelButton); contentPanel.add(instruction, BorderLayout.NORTH); contentPanel.add(filterField, BorderLayout.CENTER); contentPanel.add(buttonPanel, BorderLayout.SOUTH); filterDialog.add(contentPanel); filterDialog.setVisible(true);
    }

    // Hàm main (Sửa lại để khớp với constructor mới)
    public static void main(String[] args) {
        // Hàm main này chỉ để test.
        // Khi chạy thật, LoginForm sẽ gọi constructor có tham số.
        User testUser = new User("admin_test", "123456", "Cán bộ TV", "NV001");
        SwingUtilities.invokeLater(() -> new AdminMainForm(testUser).setVisible(true));
    }
}
