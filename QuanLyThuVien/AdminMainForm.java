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
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

// === IMPORT CÁC FILE ĐÃ CÓ ===
import QuanLyThuVien.SachDetailDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import QuanLyThuVien.Sach;
import QuanLyThuVien.SachDAO;
import QuanLyThuVien.DocGia;
import QuanLyThuVien.DocGiaDAO;
import QuanLyThuVien.DocGiaEditDialog;
import QuanLyThuVien.TacGia;
import QuanLyThuVien.TacGiaDAO;
import QuanLyThuVien.TacGiaEditDialog;
import QuanLyThuVien.BoSuuTap;
import QuanLyThuVien.BoSuuTapDAO;
import QuanLyThuVien.BoSuuTapEditDialog;

// === IMPORT MỚI CHO MƯỢN/TRẢ === // <<< NEW >>>
import QuanLyThuVien.MuonTra;
import QuanLyThuVien.MuonTraDAO;
import QuanLyThuVien.MuonSachDialog;
import com.sun.jdi.connect.spi.Connection;
import javax.swing.JComboBox; // <<< NEW >>>
// === KẾT THÚC IMPORT ===


import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;


public class AdminMainForm extends JFrame {

    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;

    // === Doc Gia components ===
    private JTable docGiaTable;
    private DefaultTableModel docGiaTableModel;
    private DocGiaDAO docGiaDAO;
    private JTextField docGiaSearchField;

    // === Sach components ===
    private JTable sachTable;
    private DefaultTableModel sachTableModel;
    private SachDAO sachDAO;
    private JTextField sachSearchField;

    // === tac gia components ===
    private JTable tacGiaTable;
    private DefaultTableModel tacGiaTableModel;
    private TacGiaDAO tacGiaDAO;
    private JTextField tacGiaSearchField;

    // === BỘ SƯU TẬP components ===
    private JList<BoSuuTap> boSuuTapList; // Đã đổi kiểu
    private DefaultListModel<BoSuuTap> boSuuTapListModel; // Đã đổi kiểu
    private JTable sachTrongBSTTable;
    private DefaultTableModel sachTrongBSTTableModel;
    private BoSuuTapDAO boSuuTapDAO;

    // === MƯỢN TRẢ components === // <<< NEW >>>
    private JTable muonTraTable;
    private DefaultTableModel muonTraTableModel;
    private MuonTraDAO muonTraDAO;
    private JComboBox<String> cbFilterTrangThaiMuonTra; // ComboBox lọc

    // --- Date Formatters ---
    private SimpleDateFormat sdfAdmin = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfMuonTra = new SimpleDateFormat("dd/MM/yyyy HH:mm"); // <<< NEW >>> (Thêm giờ phút)


    // === User/Password Components ===
    private final String currentUsername = "admin";


    public AdminMainForm() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());

        // === KHỞI TẠO CÁC DAO ===
        sachDAO = new SachDAO();
        docGiaDAO = new DocGiaDAO();
        tacGiaDAO = new TacGiaDAO();
        boSuuTapDAO = new BoSuuTapDAO();
        muonTraDAO = new MuonTraDAO(); // <<< NEW >>>
        
            // 1. Model cho Sách
        String[] sachColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Vị trí", "Ngày Thêm"};
        sachTableModel = new DefaultTableModel(sachColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 2. Model cho Độc Giả
        String[] docGiaColumns = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"};
        docGiaTableModel = new DefaultTableModel(docGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 3. Model cho Tác Giả
        String[] tacGiaColumns = {"Mã Tác Giả", "Tên Tác Giả", "Email", "SĐT", "Chuyên Môn", "Chức Danh"};
        tacGiaTableModel = new DefaultTableModel(tacGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 4. Model cho Mượn Trả
        // (Bao gồm cả cột "Loại Mượn" chúng ta đã thêm ở bước trước)
        String[] muonTraColumns = {"Mã Mượn", "Độc Giả", "Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Trạng Thái", "Loại Mượn"};
        muonTraTableModel = new DefaultTableModel(muonTraColumns, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 5. Models cho Bộ Sưu Tập
        boSuuTapListModel = new DefaultListModel<>();
        String[] sachBSTColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"};
        sachTrongBSTTableModel = new DefaultTableModel(sachBSTColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // Header
        headerLabel = new JLabel("TRANG CHỦ QUẢN TRỊ VIÊN | Chào, " + this.currentUsername);
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
     * Xử lý Đăng xuất
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
        }
    }

    /**
     * Tạo menu
     */
    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(220, getHeight()));
        menuPanel.setLayout(new GridLayout(0, 1, 0, 10));

        String[] menuItems = {
            "Tổng quan",
            "Quản lý Sách",
            "Quản lý Độc giả",
            "Quản lý Tác giả",
            "Bộ sưu tập",
            "Quản lý Mượn/Trả", // <<< Sẽ kích hoạt tab này
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
     * Hiển thị panel
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
                newPanel = createBookManagementPanel();
                break;
            case "Quản lý Độc giả":
                newPanel = createDocGiaManagementPanel();
                break;
            case "Quản lý Tác giả":
                newPanel = createTacGiaManagementPanel();
                break;
            case "Bộ sưu tập":
                newPanel = createBoSuuTapManagementPanel();
                break;
            case "Quản lý Mượn/Trả":
                newPanel = createMuonTraManagementPanel(); // <<< UPDATED >>>
                break;
            case "Báo cáo & Thống kê":
                newPanel.add(new JLabel("<< BÁO CÁO & THỐNG KÊ >>", SwingConstants.CENTER));
                break;
            case "Đổi mật khẩu":
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
    // === QUẢN LÝ SÁCH (HOÀN THIỆN - GIỮ NGUYÊN) ===
    // =========================================================================
    // (Giữ nguyên các hàm: createBookManagementPanel, loadSachData, timKiemSach, themSach, suaSach, xoaSach)
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ SÁCH");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        sachSearchField = new JTextField(25);
        JButton searchButton = new JButton("Tìm");
        JButton viewAllButton = new JButton("Xem tất cả");
        searchPanel.add(sachSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Vị trí", "Ngày Thêm"};
        sachTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sachTable = new JTable(sachTableModel);
        sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sachTable.setRowHeight(25);
        sachTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sachTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        sachTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        sachTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        JTableHeader header = sachTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(sachTable));
        JScrollPane scrollPane = new JScrollPane(sachTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Sách");
        JButton editButton = new JButton("Sửa Sách");
        JButton deleteButton = new JButton("Xóa Sách");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        loadSachData();
        searchButton.addActionListener(e -> timKiemSach());
        viewAllButton.addActionListener(e -> loadSachData());
        addButton.addActionListener(e -> themSach());
        editButton.addActionListener(e -> suaSach());
        deleteButton.addActionListener(e -> xoaSach());
        sachTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    String maSach = table.getValueAt(row, 0).toString();
                    Sach sach = sachDAO.getSachByMaSach(maSach);
                    if (sach != null) {
                        SachDetailDialog detailDialog = new SachDetailDialog(AdminMainForm.this, sach);
                        detailDialog.setVisible(true);
                    }
                }
            }
        });
        return panel;
    }
    private void loadSachData() {
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.getAllSach();
            for (Sach s : danhSach) {
                String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                sachTableModel.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(),
                    s.getNhaXuatBan(), s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A",
                    s.getSoLuong(), s.getViTri(), ngayThemStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void timKiemSach() {
        String keyword = sachSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            sachTableModel.setRowCount(0);
            List<Sach> danhSach = sachDAO.timKiemSachNangCao(keyword, "Tất cả");
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Sach s : danhSach) {
                    String ngayThemStr = (s.getNgayThem() != null) ? sdfAdmin.format(s.getNgayThem()) : "N/A";
                    sachTableModel.addRow(new Object[]{
                        s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(),
                        s.getNhaXuatBan(), s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A",
                        s.getSoLuong(), s.getViTri(), ngayThemStr
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void themSach() {
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, null);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadSachData();
    }
    private void suaSach() {
        int row = sachTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sách để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); return; }
        String ma = sachTableModel.getValueAt(row, 0).toString();
        Sach s = sachDAO.getSachByMaSach(ma);
        if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy sách.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        SachEditDialog dialog = new SachEditDialog(this, sachDAO, s);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadSachData();
    }
    // Thêm lại phương thức này vào bên trong class AdminMainForm

    private void xoaSach() {
        int row = sachTable.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(this, "Chọn sách để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        String ma = sachTableModel.getValueAt(row, 0).toString();
        String ten = sachTableModel.getValueAt(row, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách '" + ten + "' (Mã: " + ma + ")?\n"
                + "CẢNH BÁO: Hành động này sẽ ẩn sách khỏi hệ thống nhưng vẫn giữ lịch sử mượn trả.", // Cập nhật thông báo
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Đảm bảo bạn đang gọi sachDAO.xoaSach(ma) (đã sửa bằng Soft Delete)
                if (sachDAO.xoaSach(ma)) { //
                    JOptionPane.showMessageDialog(this, "Xóa (ẩn) sách thành công!");
                    loadSachData(); // Tải lại bảng sách
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // =========================================================================
    // === QUẢN LÝ ĐỘC GIẢ (HOÀN THIỆN - GIỮ NGUYÊN) ===
    // =========================================================================

    // (Giữ nguyên các hàm: createDocGiaManagementPanel, loadDocGiaData, timKiemDocGia,
    // themDocGia, suaDocGia, xoaDocGia, khoaMoKhoaDocGia)
    private JPanel createDocGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm (tên, email, sdt, mã):"));
        docGiaSearchField = new JTextField(25);
        JButton searchButton = new JButton("Tìm");
        JButton viewAllButton = new JButton("Xem tất cả");
        searchPanel.add(docGiaSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"};
        docGiaTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        docGiaTable = new JTable(docGiaTableModel);
        docGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        docGiaTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(docGiaTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Độc Giả");
        JButton editButton = new JButton("Sửa Thông Tin");
        JButton deleteButton = new JButton("Xóa Độc Giả");
        JButton blockButton = new JButton("Khóa / Mở khóa");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(blockButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        loadDocGiaData();
        searchButton.addActionListener(e -> timKiemDocGia());
        viewAllButton.addActionListener(e -> loadDocGiaData());
        addButton.addActionListener(e -> themDocGia());
        editButton.addActionListener(e -> suaDocGia());
        deleteButton.addActionListener(e -> xoaDocGia());
        blockButton.addActionListener(e -> khoaMoKhoaDocGia());
        return panel;
    }
    private void loadDocGiaData() {
        try {
            docGiaTableModel.setRowCount(0);
            List<DocGia> danhSach = docGiaDAO.getAllDocGia();
            SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy");
            for (DocGia dg : danhSach) {
                String trangThai = dg.getTrangThai();
                String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : "";
                docGiaTableModel.addRow(new Object[]{
                    dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(),
                    dg.getSdt(), dg.getDiaChi(), trangThai
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu độc giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void timKiemDocGia() {
        String keyword = docGiaSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            docGiaTableModel.setRowCount(0);
            List<DocGia> danhSach = docGiaDAO.timKiemDocGia(keyword);
            SimpleDateFormat sdfDob = new SimpleDateFormat("dd/MM/yyyy");
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
            for (DocGia dg : danhSach) {
                String trangThai = dg.getTrangThai();
                String ngaySinhStr = (dg.getNgaySinh() != null) ? sdfDob.format(dg.getNgaySinh()) : "";
                docGiaTableModel.addRow(new Object[]{
                    dg.getMaDocGia(), dg.getHoTen(), ngaySinhStr, dg.getEmail(),
                    dg.getSdt(), dg.getDiaChi(), trangThai
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void themDocGia() {
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, null);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void suaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = docGiaTableModel.getValueAt(row, 0).toString();
        DocGia dg = docGiaDAO.getDocGiaByMaDocGia(ma);
        if (dg == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaDAO, dg);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadDocGiaData();
    }
    private void xoaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = docGiaTableModel.getValueAt(row, 0).toString();
        String ten = docGiaTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa độc giả '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (docGiaDAO.xoaDocGia(ma)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDocGiaData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (Độc giả đang mượn sách?).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Độc Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void khoaMoKhoaDocGia() {
        int row = docGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn độc giả để Khóa/Mở.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = docGiaTableModel.getValueAt(row, 0).toString();
        String ten = docGiaTableModel.getValueAt(row, 1).toString();
        String status = docGiaTableModel.getValueAt(row, 6).toString();
        boolean seBiKhoa = status.equals("Hoạt động");
        String action = seBiKhoa ? "KHÓA" : "MỞ KHÓA";
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn " + action + " tài khoản '" + ten + "' (Mã: " + ma + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (docGiaDAO.khoaMoKhoaDocGia(ma, seBiKhoa)) {
                    JOptionPane.showMessageDialog(this, action + " thành công!");
                    loadDocGiaData();
                } else {
                    JOptionPane.showMessageDialog(this, action + " thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi " + action + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // =========================================================================
    // === HÀM ĐỔI MẬT KHẨU (VÔ HIỆU HÓA LOGIC - GIỮ NGUYÊN) ===
    // =========================================================================
     private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 3),
                "ĐỔI MẬT KHẨU TÀI KHOẢN ",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 36),
                new Color(30, 144, 255)
            )
        ));
        formPanel.setPreferredSize(new Dimension(650, 350));
        JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
        oldPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField oldPassField = new JPasswordField(15);
        oldPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        newPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField newPassField = new JPasswordField(15);
        newPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel confirmPassLabel = new JLabel("Xác nhận MK mới:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        final JPasswordField confirmPassField = new JPasswordField(15);
        confirmPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        final JButton changeButton = new JButton("ĐỔI MẬT KHẨU");
        changeButton.setBackground(new Color(30, 144, 255));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(oldPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(oldPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(newPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(newPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(confirmPassLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(confirmPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.insets = new Insets(15, 15, 15, 15);
        formPanel.add(changeButton, gbc);
        panel.add(formPanel);
        changeButton.addActionListener(e -> doiMatKhau(oldPassField, newPassField, confirmPassField));
        return panel;
    }
    private void doiMatKhau(JPasswordField oldPassField, JPasswordField newPassField, JPasswordField confirmPassField) {
        JOptionPane.showMessageDialog(this,
            "Chức năng đổi mật khẩu chưa được hoàn thiện.",
            "Thông báo",
            JOptionPane.WARNING_MESSAGE);
        oldPassField.setText("");
        newPassField.setText("");
        confirmPassField.setText("");
    }


    // =========================================================================
    // === TIỆN ÍCH CHUNG (Renderer) - GIỮ NGUYÊN ===
    // =========================================================================
    class HeaderRenderer implements TableCellRenderer {
        private JTable table; private JPanel panel; private JLabel label; private JButton filterButton;
        public HeaderRenderer(JTable table) { this.table = table; panel = new JPanel(new BorderLayout()); label = new JLabel(); filterButton = new JButton("▼"); filterButton.setFont(new Font("Segoe UI", Font.PLAIN, 10)); filterButton.setPreferredSize(new Dimension(20, 16)); filterButton.setMargin(new Insets(0, 0, 0, 0)); filterButton.setFocusPainted(false); filterButton.setContentAreaFilled(false); filterButton.setBorderPainted(false); label.setHorizontalAlignment(SwingConstants.CENTER); label.setFont(new Font("Segoe UI", Font.BOLD, 12)); panel.add(label, BorderLayout.CENTER); panel.add(filterButton, BorderLayout.EAST); panel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { label.setText(value != null ? value.toString() : ""); for (ActionListener al : filterButton.getActionListeners()) filterButton.removeActionListener(al); final int col = column; filterButton.addActionListener(e -> showColumnFilterDialog(table, col)); return panel; }
    }
    private void showColumnFilterDialog(JTable table, int column) {
        String columnName = table.getColumnName(column); JDialog filterDialog = new JDialog(this, "Lọc " + columnName, true); filterDialog.setLayout(new BorderLayout()); filterDialog.setSize(300, 150); filterDialog.setLocationRelativeTo(this); JPanel contentPanel = new JPanel(new BorderLayout(10, 10)); contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); JLabel instruction = new JLabel("Nhập giá trị lọc:"); JTextField filterField = new JTextField(); JPanel buttonPanel = new JPanel(new FlowLayout()); JButton applyButton = new JButton("Lọc"); JButton cancelButton = new JButton("Hủy"); applyButton.addActionListener(e -> { String filterValue = filterField.getText(); System.out.println("Lọc cột " + columnName + ": " + filterValue); /* TODO: Logic lọc */ filterDialog.dispose(); }); cancelButton.addActionListener(e -> filterDialog.dispose()); buttonPanel.add(applyButton); buttonPanel.add(cancelButton); contentPanel.add(instruction, BorderLayout.NORTH); contentPanel.add(filterField, BorderLayout.CENTER); contentPanel.add(buttonPanel, BorderLayout.SOUTH); filterDialog.add(contentPanel); filterDialog.setVisible(true);
    }


    // =========================================================================
    // === QUẢN LÝ TÁC GIẢ (HOÀN THIỆN - GIỮ NGUYÊN) ===
    // =========================================================================

    // (Giữ nguyên các hàm: createTacGiaManagementPanel, loadTacGiaData, timKiemTacGia,
    // themTacGia, suaTacGia, xoaTacGia)
    private JPanel createTacGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ THÔNG TIN TÁC GIẢ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm (tên, email, sdt, mã):"));
        tacGiaSearchField = new JTextField(25);
        JButton searchButton = new JButton("Tìm");
        JButton viewAllButton = new JButton("Xem tất cả");
        searchPanel.add(tacGiaSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        String[] columnNames = {"Mã Tác Giả", "Tên Tác Giả", "Email", "SĐT", "Chuyên Môn", "Chức Danh"};
        tacGiaTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tacGiaTable = new JTable(tacGiaTableModel);
        tacGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tacGiaTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tacGiaTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Tác Giả");
        JButton editButton = new JButton("Sửa Thông Tin");
        JButton deleteButton = new JButton("Xóa Tác Giả");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        loadTacGiaData();
        searchButton.addActionListener(e -> timKiemTacGia());
        viewAllButton.addActionListener(e -> loadTacGiaData());
        addButton.addActionListener(e -> themTacGia());
        editButton.addActionListener(e -> suaTacGia());
        deleteButton.addActionListener(e -> xoaTacGia());
        return panel;
    }
    private void loadTacGiaData() {
        try {
            tacGiaTableModel.setRowCount(0);
            List<TacGia> danhSach = tacGiaDAO.getAllTacGia();
            for (TacGia tg : danhSach) {
                tacGiaTableModel.addRow(new Object[]{
                    tg.getMaTacGia(), tg.getTenTacGia(), tg.getEmail(),
                    tg.getSdt(), tg.getTrinhDoChuyenMon(), tg.getChucDanh()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu tác giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void timKiemTacGia() {
        String keyword = tacGiaSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            tacGiaTableModel.setRowCount(0);
            List<TacGia> danhSach = tacGiaDAO.timKiemTacGia(keyword);
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tác giả.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
            for (TacGia tg : danhSach) {
                tacGiaTableModel.addRow(new Object[]{
                    tg.getMaTacGia(), tg.getTenTacGia(), tg.getEmail(),
                    tg.getSdt(), tg.getTrinhDoChuyenMon(), tg.getChucDanh()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm Tác Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void themTacGia() {
        TacGiaEditDialog dialog = new TacGiaEditDialog(this, tacGiaDAO, null);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadTacGiaData();
    }
    private void suaTacGia() {
        int row = tacGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn tác giả để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = tacGiaTableModel.getValueAt(row, 0).toString();
        TacGia tg = tacGiaDAO.getTacGiaByMaTacGia(ma);
        if (tg == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tác giả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TacGiaEditDialog dialog = new TacGiaEditDialog(this, tacGiaDAO, tg);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadTacGiaData();
    }
    private void xoaTacGia() {
        int row = tacGiaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn tác giả để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = tacGiaTableModel.getValueAt(row, 0).toString();
        String ten = tacGiaTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa tác giả '" + ten + "' (Mã: " + ma + ")?\n"
                + "CẢNH BÁO: Xóa tác giả cũng sẽ xóa liên kết của họ khỏi tất cả các sách.",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tacGiaDAO.xoaTacGia(ma)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadTacGiaData();
                    loadSachData(); // Tải lại bảng sách
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa Tác Giả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // =========================================================================
    // === QUẢN LÝ BỘ SƯU TẬP (HOÀN THIỆN - GIỮ NGUYÊN) ===
    // =========================================================================

    // (Giữ nguyên các hàm: createBoSuuTapManagementPanel, loadBoSuuTapList, loadSachTrongBoSuuTap,
    // taoBoSuuTapMoi, suaTenBoSuuTap, xoaBoSuuTap, themSachVaoBoSuuTap, xoaSachKhoiBoSuuTap)
    private JPanel createBoSuuTapManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("QUẢN LÝ BỘ SƯU TẬP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        panel.add(title, BorderLayout.NORTH);
        JPanel bstListPanel = new JPanel(new BorderLayout(5, 5));
        bstListPanel.setPreferredSize(new Dimension(300, 0));
        bstListPanel.setBorder(BorderFactory.createTitledBorder("Danh sách Bộ sưu tập"));
        boSuuTapListModel = new DefaultListModel<>();
        boSuuTapList = new JList<>(boSuuTapListModel);
        boSuuTapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bstListPanel.add(new JScrollPane(boSuuTapList), BorderLayout.CENTER);
        JPanel bstControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnTaoBST = new JButton("Tạo mới");
        JButton btnSuaBST = new JButton("Sửa tên/ảnh");
        JButton btnXoaBST = new JButton("Xóa BST");
        bstControlPanel.add(btnTaoBST);
        bstControlPanel.add(btnSuaBST);
        bstControlPanel.add(btnXoaBST);
        bstListPanel.add(bstControlPanel, BorderLayout.SOUTH);
        JPanel sachListPanel = new JPanel(new BorderLayout(5, 5));
        sachListPanel.setBorder(BorderFactory.createTitledBorder("Sách trong Bộ sưu tập đã chọn"));
        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"};
        sachTrongBSTTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sachTrongBSTTable = new JTable(sachTrongBSTTableModel);
        sachListPanel.add(new JScrollPane(sachTrongBSTTable), BorderLayout.CENTER);
        JPanel sachControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnThemSach = new JButton("Thêm sách vào BST");
        JButton btnXoaSach = new JButton("Xóa sách khỏi BST");
        sachControlPanel.add(btnThemSach);
        sachControlPanel.add(btnXoaSach);
        sachListPanel.add(sachControlPanel, BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bstListPanel, sachListPanel);
        splitPane.setDividerLocation(310);
        panel.add(splitPane, BorderLayout.CENTER);
        boSuuTapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                BoSuuTap selectedBST = (BoSuuTap) boSuuTapList.getSelectedValue();
                if (selectedBST != null) {
                    loadSachTrongBoSuuTap(selectedBST);
                } else {
                    sachTrongBSTTableModel.setRowCount(0);
                }
            }
        });
        btnTaoBST.addActionListener(e -> taoBoSuuTapMoi());
        btnSuaBST.addActionListener(e -> suaTenBoSuuTap());
        btnXoaBST.addActionListener(e -> xoaBoSuuTap());
        btnThemSach.addActionListener(e -> themSachVaoBoSuuTap());
        btnXoaSach.addActionListener(e -> xoaSachKhoiBoSuuTap());
        loadBoSuuTapList();
        return panel;
    }
    private void loadBoSuuTapList() {
        try {
            boSuuTapListModel.clear();
            List<BoSuuTap> danhSach = boSuuTapDAO.getAllBoSuuTap();
            for (BoSuuTap bst : danhSach) {
                boSuuTapListModel.addElement(bst);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách BST: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadSachTrongBoSuuTap(BoSuuTap selectedBST) {
        try {
            sachTrongBSTTableModel.setRowCount(0);
            List<Sach> danhSachSach = boSuuTapDAO.getSachTrongBoSuuTap(selectedBST.getMaBoSuuTap());
            for (Sach s : danhSachSach) {
                sachTrongBSTTableModel.addRow(new Object[]{
                    s.getMaSach(), s.getTenSach(), s.getTenTacGiaDisplay(),
                    s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải sách trong BST: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void taoBoSuuTapMoi() {
        BoSuuTapEditDialog dialog = new BoSuuTapEditDialog(this, boSuuTapDAO, null);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadBoSuuTapList();
    }
    private void suaTenBoSuuTap() {
        BoSuuTap selectedBST = (BoSuuTap) boSuuTapList.getSelectedValue();
        if (selectedBST == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BoSuuTapEditDialog dialog = new BoSuuTapEditDialog(this, boSuuTapDAO, selectedBST);
        dialog.setVisible(true);
        if (dialog.isSaveSuccess()) loadBoSuuTapList();
    }
    private void xoaBoSuuTap() {
        BoSuuTap selectedBST = (BoSuuTap) boSuuTapList.getSelectedValue();
        if (selectedBST == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xóa bộ sưu tập '" + selectedBST.getTenBoSuuTap() + "'?\n"
            + "Hành động này không xóa sách, chỉ xóa bộ sưu tập.",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (boSuuTapDAO.xoaBoSuuTap(selectedBST.getMaBoSuuTap())) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadBoSuuTapList();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void themSachVaoBoSuuTap() {
        BoSuuTap selectedBST = (BoSuuTap) boSuuTapList.getSelectedValue();
        if (selectedBST == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập trước khi thêm sách.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Sach> sachHienCo = boSuuTapDAO.getSachTrongBoSuuTap(selectedBST.getMaBoSuuTap());
        ChonSachDialog dialog = new ChonSachDialog(this, sachDAO, sachHienCo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            List<Sach> dsSachChon = dialog.getSelectedSach();
            if (dsSachChon.isEmpty()) return;
            int successCount = 0;
            for (Sach s : dsSachChon) {
                if (boSuuTapDAO.themSachVaoBoSuuTap(selectedBST.getMaBoSuuTap(), s.getMaSach())) {
                    successCount++;
                }
            }
            JOptionPane.showMessageDialog(this, "Đã thêm " + successCount + " sách vào BST '" + selectedBST.getTenBoSuuTap() + "'.");
            loadSachTrongBoSuuTap(selectedBST);
        }
    }
    private void xoaSachKhoiBoSuuTap() {
        BoSuuTap selectedBST = (BoSuuTap) boSuuTapList.getSelectedValue();
        int row = sachTrongBSTTable.getSelectedRow();
        if (selectedBST == null || row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập VÀ một Sách để xóa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maSach = sachTrongBSTTableModel.getValueAt(row, 0).toString();
        String tenSach = sachTrongBSTTableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xóa sách '" + tenSach + "' khỏi bộ sưu tập '" + selectedBST.getTenBoSuuTap() + "'?",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (boSuuTapDAO.xoaSachKhoiBoSuuTap(selectedBST.getMaBoSuuTap(), maSach)) {
                JOptionPane.showMessageDialog(this, "Xóa sách khỏi BST thành công!");
                loadSachTrongBoSuuTap(selectedBST);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // === QUẢN LÝ MƯỢN/TRẢ (ĐÃ HOÀN THIỆN) === // <<< NEW SECTION >>>
    // =========================================================================
    private JPanel createMuonTraManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Title and Filters ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ MƯỢN TRẢ SÁCH");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        String[] trangThaiOptions = {"Tất cả", "Đang mượn", "Quá hạn", "Đã trả"};
        cbFilterTrangThaiMuonTra = new JComboBox<>(trangThaiOptions);
        filterPanel.add(cbFilterTrangThaiMuonTra);
        // (Thêm ô tìm kiếm nếu cần)
        topPanel.add(filterPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Bảng Mượn Trả ---
        String[] columnNames = {"Mã Mượn", "Độc Giả", "Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Trạng Thái", "Loại Mượn"};
        muonTraTableModel = new DefaultTableModel(columnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        muonTraTable = new JTable(muonTraTableModel);
        muonTraTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        muonTraTable.setRowHeight(25);
        muonTraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ chọn 1 dòng
        JScrollPane scrollPane = new JScrollPane(muonTraTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Buttons ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnTaoPhieuMuon = new JButton("Tạo Phiếu Mượn");
        JButton btnDanhDauTra = new JButton("Đánh Dấu Đã Trả");
        // (Thêm nút Gia Hạn nếu muốn)

        controlPanel.add(btnTaoPhieuMuon);
        controlPanel.add(btnDanhDauTra);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- Gán sự kiện ---
        cbFilterTrangThaiMuonTra.addActionListener(e -> loadMuonTraData()); // Lọc khi thay đổi ComboBox
        btnTaoPhieuMuon.addActionListener(e -> taoPhieuMuonMoi());
        btnDanhDauTra.addActionListener(e -> danhDauDaTra());

        // --- Tải dữ liệu ban đầu ---
        loadMuonTraData();

        return panel;
    }

    /**
     * Tải dữ liệu cho bảng Mượn Trả, dựa vào bộ lọc
     */
    private void loadMuonTraData() {
        try {
            muonTraTableModel.setRowCount(0);
            String selectedTrangThai = (String) cbFilterTrangThaiMuonTra.getSelectedItem();
            List<MuonTra> danhSach = muonTraDAO.getDanhSachMuon(selectedTrangThai);

            for (MuonTra mt : danhSach) {
                String tenDocGia = (mt.getDocGia() != null) ? mt.getDocGia().getHoTen() : "[Không rõ]";
                String tenSach = (mt.getSach() != null) ? mt.getSach().getTenSach() : "[Không rõ]";
                String ngayMuonStr = (mt.getNgayMuon() != null) ? sdfMuonTra.format(mt.getNgayMuon()) : "";
                String ngayHenTraStr = (mt.getNgayHenTra() != null) ? sdfMuonTra.format(mt.getNgayHenTra()) : "";
                String ngayTraThucTeStr = (mt.getNgayTraThucTe() != null) ? sdfMuonTra.format(mt.getNgayTraThucTe()) : "";

                muonTraTableModel.addRow(new Object[]{
                    mt.getMaMuonTra(),
                    tenDocGia,
                    tenSach,
                    ngayMuonStr,
                    ngayHenTraStr,
                    ngayTraThucTeStr,
                    mt.getTrangThai(), // Lấy trạng thái đã được tính toán
                    mt.getLoaiMuon()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu mượn trả: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở dialog để tạo phiếu mượn mới
     */
    private void taoPhieuMuonMoi() {
        MuonSachDialog dialog = new MuonSachDialog(this, muonTraDAO);
        dialog.setVisible(true);
        if (dialog.isBorrowSuccess()) {
            loadMuonTraData(); // Tải lại bảng mượn trả
            loadSachData();    // Tải lại bảng sách (vì số lượng thay đổi)
        }
    }

    /**
     * Đánh dấu một phiếu mượn là đã trả
     */
    private void danhDauDaTra() {
        int selectedRow = muonTraTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để đánh dấu đã trả.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy mã phiếu mượn và trạng thái từ bảng
        int maMuonTra = (int) muonTraTableModel.getValueAt(selectedRow, 0); // Cột Mã Mượn
        String trangThaiHienTai = (String) muonTraTableModel.getValueAt(selectedRow, 6); // Cột Trạng Thái

        // Chỉ cho phép trả sách đang mượn hoặc quá hạn
        if (!trangThaiHienTai.equals("Đang mượn") && !trangThaiHienTai.equals("Quá hạn")) {
             JOptionPane.showMessageDialog(this, "Phiếu mượn này đã được trả hoặc có trạng thái không hợp lệ.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận sách đã được trả cho phiếu mượn mã " + maMuonTra + "?",
            "Xác nhận trả sách",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (muonTraDAO.traSach(maMuonTra)) {
                    JOptionPane.showMessageDialog(this, "Đánh dấu trả sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadMuonTraData(); // Tải lại bảng mượn trả
                    loadSachData();    // Tải lại bảng sách (số lượng thay đổi)
                } else {
                     JOptionPane.showMessageDialog(this, "Đánh dấu trả sách thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Lỗi khi đánh dấu trả sách: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
            }
        }
    }

    // (Thêm hàm giaHan() nếu bạn muốn)

    /**
     * Hàm main
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Không thể thiết lập Nimbus L&F. Sử dụng mặc định.");
        }
        SwingUtilities.invokeLater(() -> new AdminMainForm().setVisible(true));
    }
}

