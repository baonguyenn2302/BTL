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
import java.text.SimpleDateFormat; // Import để format Date

// Import Models and DAOs
import QuanLyThuVien.DocGia;
import QuanLyThuVien.DocGiaDAO;
import QuanLyThuVien.Sach; // Sach.java đã được cập nhật
import QuanLyThuVien.SachDAO; // SachDAO.java đã được cập nhật
import QuanLyThuVien.User; // Cần cho LoginForm (nếu gọi từ đây)
import QuanLyThuVien.UserDAO; // Cần cho LoginForm


public class AdminMainForm extends JFrame {

    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;
    private final String adminName = "Nguyễn Văn A (Thủ thư)"; // Nên lấy tên từ User đăng nhập

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
    // Định dạng chỉ ngày tháng cho Admin form
    private SimpleDateFormat sdfAdmin = new SimpleDateFormat("dd/MM/yyyy");

    public AdminMainForm() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Nên đổi thành DISPOSE_ON_CLOSE khi có LoginForm
        setSize(1100, 700); // Tăng chiều rộng 1 chút cho cột ngày
        setLayout(new BorderLayout());

        docGiaDAO = new DocGiaDAO();
        sachDAO = new SachDAO();

        headerLabel = new JLabel("TRANG CHỦ QUẢN TRỊ VIÊN | Chào, " + adminName);
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

    private void createMenuPanel() { /* ... Giữ nguyên ... */
        menuPanel = new JPanel(); menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(220, getHeight())); menuPanel.setLayout(new GridLayout(8, 1, 0, 10));
        String[] menuItems = { "Tổng quan", "Quản lý Sách", "Quản lý Độc giả", "Quản lý Mượn/Trả", "Báo cáo & Thống kê", "Đổi mật khẩu", "ĐĂNG XUẤT" };
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        for (String item : menuItems) {
            JButton button = new JButton(item); button.setFont(buttonFont); button.setForeground(Color.WHITE);
            button.setBackground(new Color(70, 70, 70)); button.setFocusPainted(false); button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorderPainted(false); button.setPreferredSize(new Dimension(200, 40));
            button.addActionListener(e -> showPanel(item)); menuPanel.add(button);
        }
        menuPanel.add(new JLabel());
    }

    private void showPanel(String panelName) {
        mainContentPanel.removeAll(); JPanel newPanel = new JPanel(new BorderLayout()); // Dùng BorderLayout
        newPanel.setBackground(Color.WHITE);
        switch (panelName) {
            case "Tổng quan": newPanel.add(new JLabel("<< DASHBOARD >>", SwingConstants.CENTER)); break;
            case "Quản lý Sách": newPanel = createBookManagementPanel(); break; // Đã cập nhật
            case "Quản lý Độc giả": newPanel = createDocGiaManagementPanel(); break;
            case "Quản lý Mượn/Trả": newPanel.add(new JLabel("<< MƯỢN/TRẢ >>", SwingConstants.CENTER)); break;
            case "ĐĂNG XUẤT":
                System.out.println("Đăng xuất!");
                this.dispose(); // Đóng form Admin
                // Mở lại LoginForm
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
                return; // Quan trọng: Kết thúc hàm showPanel tại đây
            default: newPanel.add(new JLabel(panelName + " chưa có.", SwingConstants.CENTER));
        }
        mainContentPanel.add(newPanel, BorderLayout.CENTER); mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    // =========================================================================
    // === QUẢN LÝ SÁCH (ĐÃ CẬP NHẬT CỘT BẢNG VÀ LOAD DATA) ===
    // =========================================================================

    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ THÔNG TIN SÁCH"); title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(new Color(0, 102, 153)); topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchPanel.add(new JLabel("Tìm kiếm (tên, tác giả):")); sachSearchField = new JTextField(25); JButton searchButton = new JButton("Tìm"); JButton viewAllButton = new JButton("Xem tất cả"); searchPanel.add(sachSearchField); searchPanel.add(searchButton); searchPanel.add(viewAllButton); topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // === THAY ĐỔI CỘT: Bỏ Lượt Xem/Tải, Thêm Ngày Cập Nhật ===
        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Số Lượng", "Trạng Thái", "Ngày Cập Nhật"};

        sachTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        sachTable = new JTable(sachTableModel); sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); sachTable.setRowHeight(25);
        // Tùy chỉnh độ rộng cột nếu cần
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Cột ngày rộng hơn

        JTableHeader header = sachTable.getTableHeader(); header.setDefaultRenderer(new HeaderRenderer(sachTable));
        JScrollPane scrollPane = new JScrollPane(sachTable); panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Sách"); JButton editButton = new JButton("Sửa Sách"); JButton deleteButton = new JButton("Xóa Sách");
        controlPanel.add(addButton); controlPanel.add(editButton); controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        loadSachData(); // Tải dữ liệu (đã cập nhật)

        searchButton.addActionListener(e -> timKiemSach()); viewAllButton.addActionListener(e -> loadSachData());
        addButton.addActionListener(e -> themSach()); editButton.addActionListener(e -> suaSach()); deleteButton.addActionListener(e -> xoaSach());

        // Sự kiện double-click (Giữ nguyên)
        sachTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = sachTable.getSelectedRow();
                    if (row != -1) { String maSach = sachTableModel.getValueAt(row, 0).toString(); System.out.println("Double clicked on book: " + maSach); }
                }
            }
        });
        return panel;
    }

    /**
     * Tải dữ liệu SÁCH (Hiển thị Ngày Cập Nhật, bỏ Lượt Xem/Tải)
     */
    private void loadSachData() {
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.getAllSach();
            for (Sach s : danhSach) {
                // Format ngày thêm (dùng sdfAdmin)
                String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                sachTableModel.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTacGia(), s.getNhaXuatBan(),
                    s.getSoLuong(), s.getTrangThai(), ngayThemStr // Chỉ hiển thị ngày
                });
            }
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    /**
     * Tìm kiếm sách (Cập nhật hiển thị ngày)
     */
    private void timKiemSach() {
        String keyword = sachSearchField.getText().trim(); if (keyword.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.timKiemSach(keyword); // Dùng hàm tìm kiếm cũ
            if (danhSach.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); }
            else {
                for (Sach s : danhSach) {
                    String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                    sachTableModel.addRow(new Object[]{ s.getMaSach(), s.getTenSach(), s.getTacGia(), s.getNhaXuatBan(), s.getSoLuong(), s.getTrangThai(), ngayThemStr });
                }
            }
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi khi tìm Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    // Các hàm themSach, suaSach, xoaSach (Giữ nguyên)
    private void themSach() { /* ... Giữ nguyên ... */
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, null); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadSachData();
    }
    private void suaSach() { /* ... Giữ nguyên ... */
        int row = sachTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = sachTableModel.getValueAt(row, 0).toString(); Sach s = sachDAO.getSachByMaSach(ma); if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, s); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadSachData();
    }
    private void xoaSach() { /* ... Giữ nguyên ... */
        int row = sachTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = sachTableModel.getValueAt(row, 0).toString(); String ten = sachTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (sachDAO.xoaSach(ma)) { JOptionPane.showMessageDialog(this, "Xóa thành công!"); loadSachData(); } else { JOptionPane.showMessageDialog(this, "Xóa thất bại (Sách đang mượn?).", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi xóa Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }


    // =========================================================================
    // === QUẢN LÝ ĐỘC GIẢ (Giữ nguyên) ===
    // =========================================================================
    private JPanel createDocGiaManagementPanel() { /* ... Giữ nguyên ... */
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5)); JLabel title = new JLabel("QUẢN LÝ ĐỘC GIẢ"); title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(new Color(0, 102, 153)); topPanel.add(title, BorderLayout.NORTH); JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchPanel.add(new JLabel("Tìm kiếm (tên, email):")); docGiaSearchField = new JTextField(25); JButton searchButton = new JButton("Tìm"); JButton viewAllButton = new JButton("Xem tất cả"); searchPanel.add(docGiaSearchField); searchPanel.add(searchButton); searchPanel.add(viewAllButton); topPanel.add(searchPanel, BorderLayout.CENTER); panel.add(topPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Trạng Thái"}; docGiaTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } }; docGiaTable = new JTable(docGiaTableModel); docGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); docGiaTable.setRowHeight(25); JScrollPane scrollPane = new JScrollPane(docGiaTable); panel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); JButton addButton = new JButton("Thêm Độc Giả"); JButton editButton = new JButton("Sửa Thông Tin"); JButton deleteButton = new JButton("Xóa Độc Giả"); JButton blockButton = new JButton("Khóa / Mở khóa"); controlPanel.add(addButton); controlPanel.add(editButton); controlPanel.add(deleteButton); controlPanel.add(blockButton); panel.add(controlPanel, BorderLayout.SOUTH);
        loadDocGiaData(); searchButton.addActionListener(e -> timKiemDocGia()); viewAllButton.addActionListener(e -> loadDocGiaData()); addButton.addActionListener(e -> themDocGia()); editButton.addActionListener(e -> suaDocGia()); deleteButton.addActionListener(e -> xoaDocGia()); blockButton.addActionListener(e -> khoaMoKhoaDocGia());
        return panel;
    }
    private void loadDocGiaData() { /* ... Giữ nguyên ... */
        try { docGiaTableModel.setRowCount(0); List<DocGia> danhSach = docGiaDAO.getAllDocGia(); SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy"); for (DocGia dg : danhSach) { String trangThai = dg.isBlocked() ? "Bị khóa" : "Hoạt động"; String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : ""; docGiaTableModel.addRow(new Object[]{ dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(), dg.getSdt(), trangThai }); } } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu độc giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }
    private void timKiemDocGia() { /* ... Giữ nguyên ... */
        String keyword = docGiaSearchField.getText().trim(); if (keyword.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        try { docGiaTableModel.setRowCount(0); List<DocGia> danhSach = docGiaDAO.timKiemDocGia(keyword); SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy"); if (danhSach.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); } for (DocGia dg : danhSach) { String trangThai = dg.isBlocked() ? "Bị khóa" : "Hoạt động"; String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : ""; docGiaTableModel.addRow(new Object[]{ dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(), dg.getSdt(), trangThai }); } } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi khi tìm Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }
    private void themDocGia() { /* ... Giữ nguyên ... */
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, null); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void suaDocGia() { /* ... Giữ nguyên ... */
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); DocGia dg = docGiaDAO.getDocGiaByMaDocGia(ma); if (dg == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, dg); dialog.setVisible(true); if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void xoaDocGia() { /* ... Giữ nguyên ... */
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); int confirm = JOptionPane.showConfirmDialog(this, "Xóa độc giả '" + ma + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (docGiaDAO.xoaDocGia(ma)) { JOptionPane.showMessageDialog(this, "Xóa thành công!"); loadDocGiaData(); } else { JOptionPane.showMessageDialog(this, "Xóa thất bại (Độc giả đang mượn?).", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi xóa Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }
    private void khoaMoKhoaDocGia() { /* ... Giữ nguyên ... */
        int row = docGiaTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn độc giả để Khóa/Mở.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = docGiaTableModel.getValueAt(row, 0).toString(); String status = docGiaTableModel.getValueAt(row, 5).toString();
        boolean block = status.equals("Hoạt động"); String action = block ? "KHÓA" : "MỞ KHÓA";
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn " + action + " tài khoản '" + ma + "'?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { try { if (docGiaDAO.khoaMoKhoaDocGia(ma, block)) { JOptionPane.showMessageDialog(this, action + " thành công!"); loadDocGiaData(); } else { JOptionPane.showMessageDialog(this, action + " thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE); } } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi khi " + action + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
    }


    // =========================================================================
    // === TIỆN ÍCH CHUNG (Giữ nguyên) ===
    // =========================================================================
    class HeaderRenderer implements TableCellRenderer { /* ... Giữ nguyên ... */
        private JTable table; private JPanel panel; private JLabel label; private JButton filterButton;
        public HeaderRenderer(JTable table) { this.table = table; panel = new JPanel(new BorderLayout()); label = new JLabel(); filterButton = new JButton("▼"); filterButton.setFont(new Font("Segoe UI", Font.PLAIN, 10)); filterButton.setPreferredSize(new Dimension(20, 16)); filterButton.setMargin(new Insets(0, 0, 0, 0)); filterButton.setFocusPainted(false); filterButton.setContentAreaFilled(false); filterButton.setBorderPainted(false); label.setHorizontalAlignment(SwingConstants.CENTER); label.setFont(new Font("Segoe UI", Font.BOLD, 12)); panel.add(label, BorderLayout.CENTER); panel.add(filterButton, BorderLayout.EAST); panel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { label.setText(value != null ? value.toString() : ""); for (ActionListener al : filterButton.getActionListeners()) filterButton.removeActionListener(al); final int col = column; filterButton.addActionListener(e -> showColumnFilterDialog(table, col)); return panel; }
    }
    private void showColumnFilterDialog(JTable table, int column) { /* ... Giữ nguyên ... */
        String columnName = table.getColumnName(column); JDialog filterDialog = new JDialog(this, "Lọc " + columnName, true); filterDialog.setLayout(new BorderLayout()); filterDialog.setSize(300, 150); filterDialog.setLocationRelativeTo(this); JPanel contentPanel = new JPanel(new BorderLayout(10, 10)); contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); JLabel instruction = new JLabel("Nhập giá trị lọc:"); JTextField filterField = new JTextField(); JPanel buttonPanel = new JPanel(new FlowLayout()); JButton applyButton = new JButton("Lọc"); JButton cancelButton = new JButton("Hủy"); applyButton.addActionListener(e -> { String filterValue = filterField.getText(); System.out.println("Lọc cột " + columnName + ": " + filterValue); /* TODO: Logic lọc */ filterDialog.dispose(); }); cancelButton.addActionListener(e -> filterDialog.dispose()); buttonPanel.add(applyButton); buttonPanel.add(cancelButton); contentPanel.add(instruction, BorderLayout.NORTH); contentPanel.add(filterField, BorderLayout.CENTER); contentPanel.add(buttonPanel, BorderLayout.SOUTH); filterDialog.add(contentPanel); filterDialog.setVisible(true);
    }

    // Hàm main (Nên xóa nếu LoginForm là main class)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminMainForm().setVisible(true));
    }
}
