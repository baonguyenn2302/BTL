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
import java.awt.CardLayout;
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
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager; 
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer; 
import java.text.SimpleDateFormat;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;
import com.toedter.calendar.JDateChooser;
import java.util.stream.Collectors;
public class MainFrame extends JFrame {
    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JLabel headerLabel;

    private JTable docGiaTable;
    private DefaultTableModel docGiaTableModel;
    private JTextField docGiaSearchField;

    private JTable sachTable;
    private DefaultTableModel sachTableModel;
    private JTextField sachSearchField;
    private SachDAO sachDAO;
    private TacGiaDAO tacGiaDAO;
    private DocGiaDAO docGiaDAO;
    private MuonTraDAO muonTraDAO; 
    private DocGia docGiaHienTai;
    private JTable tacGiaTable;
    private DefaultTableModel tacGiaTableModel;
    private JTextField tacGiaSearchField;
    private BoSuuTapDAO boSuuTapDAO;
    private JList<BoSuuTap> boSuuTapList;
    private DefaultListModel<BoSuuTap> boSuuTapListModel;
    private JTable sachTrongBSTTable;
    private DefaultTableModel sachTrongBSTTableModel;

    private JTable muonTraTable;
    private DefaultTableModel muonTraTableModel;
    private JComboBox<String> cbFilterTrangThaiMuonTra;
    private JComboBox<String> cbMuonTraSearchMode;
    private JTextField txtMuonTraSearchValue;
    private JTabbedPane muonTraTabbedPane;
    private JTextField txtMaDGMuon;
    private JButton btnTimDocGia;
    private JLabel lblAnhDocGiaMuon;     // <<< THÊM DÒNG NÀY (Panel ảnh)
    private JTextField txtEmailDGMuon;   // <<< THÊM DÒNG NÀY
    private JTextField txtSdtDGMuon;     // <<< THÊM DÒNG NÀY
    private JTextField txtTrangThaiDGMuon; // <<< THÊM DÒNG NÀY
    private List<MuonTra> currentMuonTraList;
    private JTextField txtTenDGMuon;
    private JTextField txtSoSachDaMuon;
    private JComboBox<String> cbDocGiaSearchMode;
    private JTable sachTimKiemTable;
    private DefaultTableModel sachTimKiemTableModel;
    private JTextField txtTimSachMuon;
    
    private JComboBox<String> cbSachSearchMode;
    private JComboBox<String> cbTimSachMuonMode;
    private JTable sachDaChonTable;
    private DefaultTableModel sachDaChonTableModel;
    private TaiKhoan currentUserAccount;
    private String currentUsername;
    
    private CardLayout tongQuanCardLayout;
    private JPanel tongQuanMainCardPanel;  // Panel chính của tab Tổng quan
    private JPanel dashboardPanel;         // Card 1: Chứa widget + lưới BST
    private JPanel bstSachGridPanel;       // Card 2: Chứa lưới Sách
    
    private JPanel tqCollectionGridPanel;  // Lưới BST (trên Card 1)
    private JPanel tqSachGridPanel;        // Lưới Sách (trên Card 2)
    private JLabel tqSachGridTitle;        // Tiêu đề của Card 2
    private JTextField bstSearchField;
    
    // <<< THÊM CÁC BIẾN MỚI CHO TAB BÁO CÁO >>>
    private DefaultTableModel topSachTableModel;
    private DefaultTableModel topDocGiaTableModel;
    private JDateChooser dateFromChooser;
    private JDateChooser dateToChooser;
    
    // === THÊM CÁC BIẾN MỚI CHO PANEL TÀI KHOẢN ===
    private JPanel taiKhoanGridPanel; // Panel dạng lưới
    private TaiKhoan selectedTaiKhoan; // Tài khoản đang được chọn
    private JPanel selectedTaiKhoanPanel; // Panel cell đang được chọn (để highlight)
    private TaiKhoanDAO taiKhoanDAO;
    
    public MainFrame(TaiKhoan taiKhoan) {
        this.currentUserAccount = taiKhoan;
        this.currentUsername = taiKhoan.getTenNguoiDung();
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - " + this.currentUsername.toUpperCase());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout());
        
        sachDAO = new SachDAO();
        tacGiaDAO = new TacGiaDAO();
        docGiaDAO = new DocGiaDAO();
        muonTraDAO = new MuonTraDAO();
        boSuuTapDAO = new BoSuuTapDAO();
        taiKhoanDAO = new TaiKhoanDAO();
        this.currentMuonTraList = new java.util.ArrayList<>();
        // 1. Model cho Sách (ĐÃ SỬA CỘT)
        String[] sachColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Nhà XB", "Năm XB", "Số Lượng", "Còn lại", "Vị trí", "Ngày Thêm"};
        sachTableModel = new DefaultTableModel(sachColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 2. Model cho Độc Giả
        String[] docGiaColumns = {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"};
        docGiaTableModel = new DefaultTableModel(docGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 3. Model cho Tác Giả
        String[] tacGiaColumns = {"Mã Tác Giả", "Tên Tác Giả", "Email", "SĐT", "Chức Danh"};
        tacGiaTableModel = new DefaultTableModel(tacGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 4. Model cho Mượn Trả
        String[] muonTraColumns = {"Mã Mượn", "Độc Giả", "Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Trạng Thái", "Loại Mượn"};
        muonTraTableModel = new DefaultTableModel(muonTraColumns, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // 5. Models cho Bộ Sưu Tập
        boSuuTapListModel = new DefaultListModel<BoSuuTap>();
        String[] sachBSTColumns = {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"};
        sachTrongBSTTableModel = new DefaultTableModel(sachBSTColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        // === KHỞI TẠO MODEL MỚI CHO TAB MƯỢN SÁCH ===
        String[] sachTimKiemColumns = {"Mã sách", "Tên sách", "NXB", "Năm xuất bản", "Trạng thái"};
        sachTimKiemTableModel = new DefaultTableModel(sachTimKiemColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        String[] sachDaChonColumns = {"Mã sách", "Tên sách"};
        sachDaChonTableModel = new DefaultTableModel(sachDaChonColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        // === KHỞI TẠO MODEL MỚI CHO TAB THỐNG KÊ ===
        String[] topSachColumns = {"Hạng", "Tên Sách", "Lượt Mượn"};
        topSachTableModel = new DefaultTableModel(topSachColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        String[] topDocGiaColumns = {"Hạng", "Tên Độc Giả", "Lượt Mượn"};
        topDocGiaTableModel = new DefaultTableModel(topDocGiaColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        // Header
        headerLabel = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN | Chào, " + this.currentUsername);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
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

    private void showNotImplementedMessage() {
        JOptionPane.showMessageDialog(this, "Tính năng này chưa được triển khai (logic đã bị xóa).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Hiển thị hộp thoại xác nhận
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận Đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        // 2. Nếu người dùng chọn "Yes"
        if (choice == JOptionPane.YES_OPTION) {
            // 3. Đóng cửa sổ MainFrame hiện tại
            this.dispose(); 
            
            // 4. Mở lại cửa sổ LoginFrame
            // (Chúng ta gọi hàm main của LoginFrame để đảm bảo 
            // các cài đặt UIManager được áp dụng lại)
            LoginFrame.main(null);
        }
    }
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
            "Quản lý Mượn/Trả", 
            "Báo cáo & Thống kê",
            "Tài khoản"
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
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setFocusPainted(false);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(200, 40));

        logoutButton.addActionListener(this::btnDangXuatActionPerformed);
        menuPanel.add(logoutButton);

        menuPanel.add(new JLabel());
    }

    private void showPanel(String panelName) {
        mainContentPanel.removeAll();
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.setBackground(Color.WHITE);

        switch (panelName) {
            case "Tổng quan":
                // ==================================================
                // <<< BẮT ĐẦU: TAB TỔNG QUAN VỚI CARDLAYOUT >>>
                // ==================================================
                
                // 1. Khởi tạo Layout và Panel chính
                tongQuanCardLayout = new CardLayout();
                tongQuanMainCardPanel = new JPanel(tongQuanCardLayout);
                
                // --- CARD 1: DASHBOARD CHÍNH (Widget + Lưới BST) ---
                dashboardPanel = new JPanel(new BorderLayout(10, 20));
                dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dashboardPanel.setBackground(Color.WHITE);

                // (1.A) Hàng Widget (Giữ nguyên logic)
                int soSachDenHan = muonTraDAO.getSoLuongSachDenHanHomNay();
                int soSachQuaHan = muonTraDAO.getSoLuongSachQuaHan();
                int soSachHetHang = sachDAO.getSoLuongSachHetHang();
                int soDocGiaBiKhoa = docGiaDAO.getSoLuongDocGiaBiKhoa();
                JPanel widgetRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
                widgetRowPanel.setBackground(Color.WHITE);
                
                widgetRowPanel.add(createDashboardWidget("Sách đến hạn hôm nay", String.valueOf(soSachDenHan), 
                    new Color(255, 248, 225), new Color(255, 193, 7),
                    (soSachDenHan > 0) ? Color.RED : new Color(0, 100, 0), "DEN_HAN"));
                widgetRowPanel.add(createDashboardWidget("Sách đã quá hạn", String.valueOf(soSachQuaHan), 
                    new Color(255, 235, 238), new Color(220, 53, 69),
                    (soSachQuaHan > 0) ? Color.RED : Color.BLACK, "QUA_HAN"));
                widgetRowPanel.add(createDashboardWidget("Sách đã hết hàng", String.valueOf(soSachHetHang), 
                    new Color(232, 245, 253), new Color(30, 144, 255),
                    Color.BLACK, "HET_HANG"));
                widgetRowPanel.add(createDashboardWidget("Độc giả bị khóa", String.valueOf(soDocGiaBiKhoa), 
                    new Color(248, 249, 250), new Color(108, 117, 125),
                    (soDocGiaBiKhoa > 0) ? new Color(108, 117, 125) : Color.BLACK, "KHOA"));
                
                dashboardPanel.add(widgetRowPanel, BorderLayout.NORTH);

                // (1.B) Hàng Lưới Bộ Sưu Tập
                // THAY ĐỔI: Dùng FlowLayout(FlowLayout.LEFT) để các ô tự ngắt dòng
                tqCollectionGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
                tqCollectionGridPanel.setBackground(Color.WHITE);
                tqCollectionGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                List<BoSuuTap> danhSachBST = boSuuTapDAO.getAllBoSuuTap();
                for (BoSuuTap bst : danhSachBST) {
                    // (createCollectionCell sẽ gọi showSachGridForBST, 
                    //  hàm đó sẽ lật CardLayout)
                    JPanel cell = createCollectionCell(bst); 
                    tqCollectionGridPanel.add(cell);
                }
                
                // THAY ĐỔI: Xóa bỏ panel 'gridWrapperBST' (vì nó gây kéo giãn)
                // Thêm 'tqCollectionGridPanel' trực tiếp vào JScrollPane
                JScrollPane bstGridScroll = new JScrollPane(tqCollectionGridPanel); 
                
                // Gán Border (tiêu đề) cho JScrollPane
                bstGridScroll.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Các Bộ Sưu Tập", 
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 14), new Color(0, 102, 153))
                );
                
                dashboardPanel.add(bstGridScroll, BorderLayout.CENTER);
                
                
                
                // --- CARD 2: LƯỚI SÁCH TRONG BST ---
                bstSachGridPanel = new JPanel(new BorderLayout(10, 10));
                bstSachGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                bstSachGridPanel.setBackground(Color.WHITE);

                // (2.A) Panel Nút Quay lại & Tiêu đề
                JPanel sachTopPanel = new JPanel(new BorderLayout(10, 5));
                sachTopPanel.setBackground(Color.WHITE);
                
                JButton btnQuayLai = new JButton(" Quay lại Tổng quan");
                btnQuayLai.setFont(new Font("Arial", Font.BOLD, 12));
                btnQuayLai.addActionListener(e -> tongQuanCardLayout.show(tongQuanMainCardPanel, "DASHBOARD"));
                sachTopPanel.add(btnQuayLai, BorderLayout.WEST);

                tqSachGridTitle = new JLabel("Sách trong: ...");
                tqSachGridTitle.setFont(new Font("Arial", Font.BOLD, 16));
                tqSachGridTitle.setForeground(new Color(0, 102, 153));
                tqSachGridTitle.setHorizontalAlignment(SwingConstants.CENTER);
                sachTopPanel.add(tqSachGridTitle, BorderLayout.CENTER);
                
                bstSachGridPanel.add(sachTopPanel, BorderLayout.NORTH);
                // (2.B) Panel Lưới Sách (sẽ được điền sau)
                // THAY ĐỔI: Dùng FlowLayout
                tqSachGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                tqSachGridPanel.setBackground(Color.WHITE);
                tqSachGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // THAY ĐỔI: Xóa bỏ panel 'gridWrapperSach'
                // Thêm 'tqSachGridPanel' trực tiếp vào JScrollPane
                JScrollPane sachGridScroll = new JScrollPane(tqSachGridPanel);
                
                sachGridScroll.setBorder(BorderFactory.createEtchedBorder());
                
                bstSachGridPanel.add(sachGridScroll, BorderLayout.CENTER);
                
                // --- Thêm 2 Card vào Panel chính ---
                tongQuanMainCardPanel.add(dashboardPanel, "DASHBOARD");
                tongQuanMainCardPanel.add(bstSachGridPanel, "GRID_SACH");
                
                // Đặt Card Panel làm nội dung chính
                newPanel.add(tongQuanMainCardPanel, BorderLayout.CENTER);
                
                // Hiển thị Card 1 (Dashboard)
                tongQuanCardLayout.show(tongQuanMainCardPanel, "DASHBOARD");
                break;
                // <<< KẾT THÚC TAB TỔNG QUAN >>>
                // ==================================================
                
            case "Quản lý Sách":
                newPanel = createBookManagementPanel();
                loadSachData();              
                break;
            case "Quản lý Độc giả":
                newPanel = createDocGiaManagementPanel();
                loadDocGiaData();
                break;
            case "Quản lý Tác giả":
                newPanel = createTacGiaManagementPanel();
                loadTacGiaData();
                break;
            case "Bộ sưu tập":
                newPanel = createBoSuuTapManagementPanel(); // Hàm này đã được khôi phục
                loadBoSuuTapListData(); // Tải JList
                break;
            case "Quản lý Mượn/Trả":
                newPanel = createMuonTraManagementPanel(); 
                loadMuonTraData(); 
                break;
            case "Báo cáo & Thống kê":
                newPanel.add(new JLabel("<< BÁO CÁO & THỐNG KÊ >>", SwingConstants.CENTER));
                newPanel = createBaoCaoPanel();
                break;
            case "Tài khoản":
                newPanel = createTaiKhoanManagementPanel(); // <<< SỬA
                loadTaiKhoanData(); // <<< THÊM: Tải dữ liệu khi mở panel
                break;
            default:
                newPanel.add(new JLabel(panelName + " chưa có.", SwingConstants.CENTER));
        }
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Quản lý Sách
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel trên cùng, chứa Tiêu đề và Panel Lọc
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ SÁCH");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);

        // === BẮT ĐẦU GIAO DIỆN TÌM KIẾM MỚI ===
        // Panel Tìm kiếm (Dùng FlowLayout đơn giản)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 1. Ô chọn chế độ
        searchPanel.add(new JLabel("Tìm theo:"));
        String[] searchModes = {"Mã sách", "Tên sách", "Tác giả", "Năm xuất bản", "Trạng thái"};
        cbSachSearchMode = new JComboBox<>(searchModes);
        cbSachSearchMode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(cbSachSearchMode);

        // 2. Ô nhập liệu (Tái sử dụng sachSearchField)
        sachSearchField = new JTextField(25); // 25 cột
        sachSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(sachSearchField);
        
        // 3. Nút Tìm kiếm
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(searchButton);
        
        // 4. Nút Xem tất cả
        JButton viewAllButton = new JButton("Xem tất cả");
        viewAllButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(viewAllButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        // === KẾT THÚC GIAO DIỆN TÌM KIẾM MỚI ===

        // === Bảng JTable (Không đổi) ===
        sachTable = new JTable(sachTableModel);
        sachTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sachTable.setRowHeight(25);
        sachTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kiểm tra nếu là double-click
                if (e.getClickCount() == 2) {
                    int row = sachTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // Lấy mã sách từ cột 0
                        String maSach = sachTable.getValueAt(row, 0).toString();
                        // Gọi hàm helper đã tạo
                        showChiTietSachDialog(maSach); 
                    }
                }
            }
        });
        // ... (Code setColumnModel... giữ nguyên)
        sachTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sachTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        sachTable.getColumnModel().getColumn(2).setPreferredWidth(180); 
        sachTable.getColumnModel().getColumn(5).setPreferredWidth(60);  
        sachTable.getColumnModel().getColumn(6).setPreferredWidth(70);  
        sachTable.getColumnModel().getColumn(7).setPreferredWidth(120); 
        sachTable.getColumnModel().getColumn(8).setPreferredWidth(100); 
        
        JTableHeader header = sachTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(sachTable));
        JScrollPane scrollPane = new JScrollPane(sachTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // === Panel Nút dưới (Không đổi) ===
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Sách");
        JButton editButton = new JButton("Sửa Sách");
        JButton deleteButton = new JButton("Xóa Sách");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // === Gán sự kiện (ĐÃ SỬA) ===
        // Nút Tìm sẽ gọi locSachData() (sẽ sửa ở bước 4)
        searchButton.addActionListener(e -> locSachData()); 
        
        // Nút Xem Tất Cả
        viewAllButton.addActionListener(e -> {
            loadSachData(); // Tải lại tất cả
            sachSearchField.setText(""); // Reset ô tìm
            cbSachSearchMode.setSelectedIndex(0); // Reset chế độ
        });
        
        // Các nút Thêm, Sửa, Xóa (Không đổi)
        addButton.addActionListener(e -> {
            try {
                themSach();
            } catch (Exception ex) {
                System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        editButton.addActionListener(e -> suaSach());
        deleteButton.addActionListener(e -> xoaSach());
        
        return panel;
    }

    // === CÁC HÀM LOGIC SÁCH ===
    private void updateTableData(List<Sach> sachList) {

        sachTableModel.setRowCount(0); 



        for (Sach sach : sachList) {

            String tenTacGiaStr = sach.getDanhSachTacGia().stream()

                .map(TacGia::getTenTacGia)

                .collect(Collectors.joining(", "));



            sachTableModel.addRow(new Object[]{

                sach.getMaSach(),

                sach.getTenSach(),

                tenTacGiaStr,

                sach.getNhaXuatBan(),

                sach.getNamXuatBan(),

                sach.getSoLuong(),

                sach.getConLai(),

                sach.getViTri()

            });

        }

    }
    
    // HÀM NÀY ĐÃ ĐƯỢC SỬA (trong các hướng dẫn trước)
    private void themSach() throws Exception {
        // Bước 1: Lấy mã sách mới nhất từ CSDL
        String newMaSach = sachDAO.generateNewMaSach();

        // Bước 2: Tạo và hiển thị Dialog
        SachEditDialog dialog = new SachEditDialog(this, newMaSach);
        dialog.setVisible(true);

        // Bước 3: (Sau khi dialog đóng lại) Kiểm tra xem có lưu thành công không
        if (dialog.isSaveSuccess()) {
            // Nếu thành công, TẢI LẠI DỮ LIỆU BẢNG
            loadSachData(); 
            JOptionPane.showMessageDialog(this, "Đã thêm sách thành công!");
        }
    }
    // HÀM MỚI DÀNH CHO NÚT SỬA SÁCH
    private void suaSach() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = sachTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một cuốn sách để sửa.", 
                "Chưa chọn sách", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy Mã Sách từ hàng đã chọn (Cột 0)
        String maSach = sachTableModel.getValueAt(selectedRow, 0).toString();

        // 3. Dùng DAO để lấy TOÀN BỘ thông tin sách (bao gồm tác giả)
        Sach sachToEdit = sachDAO.getSachByMa(maSach);

        if (sachToEdit == null) {
            JOptionPane.showMessageDialog(this, "Không thể tìm thấy dữ liệu sách (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Mở Dialog ở chế độ SỬA (dùng Constructor mới)
        SachEditDialog dialog = new SachEditDialog(this, sachToEdit);
        dialog.setVisible(true);

        // 5. Nếu lưu thành công, tải lại bảng
        if (dialog.isSaveSuccess()) {
            loadSachData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật sách thành công!");
        }
    }
    // ... (Các hàm logic khác chưa triển khai) ...
    // HÀM MỚI DÀNH CHO NÚT XÓA SÁCH
    private void xoaSach() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = sachTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một cuốn sách để xóa.", 
                "Chưa chọn sách", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy thông tin sách từ JTable
        String maSach = sachTableModel.getValueAt(selectedRow, 0).toString();
        String tenSach = sachTableModel.getValueAt(selectedRow, 1).toString();

        // 3. Hiển thị hộp thoại xác nhận (Confirm Dialog)
        String confirmMsg = "Bạn có chắc chắn muốn xóa (lưu trữ) sách:\n"
                          + tenSach + " (Mã: " + maSach + ")\n"
                          + "Sách sẽ bị ẩn đi, không xóa vĩnh viễn.";
                          
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "Xác nhận Xóa Mềm", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        // 4. Xử lý nếu người dùng đồng ý (chọn Yes)
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi DAO để thực hiện xóa mềm
            boolean success = sachDAO.softDeleteSach(maSach);
            
            if (success) {
                // Tải lại bảng (cuốn sách sẽ biến mất vì isArchived=1)
                loadSachData(); 
                JOptionPane.showMessageDialog(this, "Đã xóa (lưu trữ) sách thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sách thất bại (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Nếu người dùng chọn "No" hoặc đóng dialog, không làm gì cả.
    }

    // Quản lý Độc giả
    private JPanel createDocGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel title = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(0, 102, 153));
        topPanel.add(title, BorderLayout.NORTH);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm theo:"));
        String[] searchModes = {"Mã độc giả", "Họ tên", "SĐT", "Trạng thái"};
        cbDocGiaSearchMode = new JComboBox<>(searchModes);
        cbDocGiaSearchMode.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(cbDocGiaSearchMode);
        
        docGiaSearchField = new JTextField(25);
        docGiaSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(docGiaSearchField);
        
        JButton searchButton = new JButton("Tìm");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(searchButton);
        
        JButton viewAllButton = new JButton("Xem tất cả");
        viewAllButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(viewAllButton);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        docGiaTable = new JTable(docGiaTableModel);
        docGiaTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        docGiaTable.setFont(new Font("Arial", Font.PLAIN, 12)); 
        docGiaTable.setRowHeight(25);
        panel.add(new JScrollPane(docGiaTable), BorderLayout.CENTER);
        
        // --- Control Panel (ĐÃ THÊM NÚT CHI TIẾT) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton chiTietButton = new JButton("Xem Chi Tiết"); // <<< NÚT MỚI
        chiTietButton.setFont(new Font("Arial", Font.BOLD, 12));

        JButton addButton = new JButton("Thêm Độc Giả");
        JButton editButton = new JButton("Sửa Thông Tin");
        JButton deleteButton = new JButton("Xóa Độc Giả");
        JButton blockButton = new JButton("Khóa / Mở khóa");
        
        controlPanel.add(chiTietButton); // <<< THÊM VÀO PANEL
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(blockButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // ==================================================
        // <<< GÁN SỰ KIỆN >>>
        // ==================================================
        
        // --- SỰ KIỆN NÚT CHI TIẾT (MỚI) ---
        chiTietButton.addActionListener(e -> {
            int selectedRow = docGiaTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một độc giả để xem chi tiết.", "Chưa chọn độc giả", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Lấy mã độc giả từ JTable
            String maDocGia = docGiaTableModel.getValueAt(selectedRow, 0).toString();

            // 2. Gọi DAO lấy thông tin đầy đủ
            DocGia docGiaChiTiet = docGiaDAO.getDocGiaByMa(maDocGia);
            
            // 3. Gọi DAO lấy lịch sử mượn
            List<MuonTra> lichSuMuon = muonTraDAO.getLichSuMuonTraByDocGia(maDocGia);

            if (docGiaChiTiet != null) {
                // 4. Mở Dialog chi tiết
                ChiTietDocGiaDialog dialog = new ChiTietDocGiaDialog(this, docGiaChiTiet, lichSuMuon);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tải chi tiết độc giả.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // --- Các sự kiện cũ (Giữ nguyên) ---
        searchButton.addActionListener(e -> {
            String mode = cbDocGiaSearchMode.getSelectedItem().toString();
            String value = docGiaSearchField.getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị cần tìm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<DocGia> danhSach = docGiaDAO.searchDocGia(mode, value);
            renderDocGiaTable(danhSach);
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả nào khớp.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewAllButton.addActionListener(e -> {
            loadDocGiaData(); 
            docGiaSearchField.setText("");
        });
        
        addButton.addActionListener(e -> themDocGia());
        editButton.addActionListener(e -> suaDocGia());
        deleteButton.addActionListener(e -> xoaDocGia());
        blockButton.addActionListener(e -> toggleBlockDocGia());
        
        return panel;
    }
    
    // Quản lý Tác giả
    private JPanel createTacGiaManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("QUẢN LÝ TÁC GIẢ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        tacGiaTable = new JTable(tacGiaTableModel);
        tacGiaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tacGiaTable.setRowHeight(25);
        
        JTableHeader header = tacGiaTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(tacGiaTable));
        JScrollPane scrollPane = new JScrollPane(tacGiaTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm Tác Giả");
        JButton editButton = new JButton("Sửa Tác Giả");
        JButton deleteButton = new JButton("Xóa Tác Giả");
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> themTacGia());
        editButton.addActionListener(e -> suaTacGia());
        deleteButton.addActionListener(e -> xoaTacGia());

        return panel;
    }

    // Quản lý Bộ sưu tập
    private JPanel createBoSuuTapManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ==================================================
        // <<< PANEL TÌM KIẾM (MỚI) >>>
        // ==================================================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm theo tên BST:"));
        
        bstSearchField = new JTextField(20); // Khởi tạo biến
        bstSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(bstSearchField);
        
        JButton btnBstSearch = new JButton("Tìm");
        btnBstSearch.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(btnBstSearch);
        
        JButton btnBstViewAll = new JButton("Xem tất cả");
        btnBstViewAll.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(btnBstViewAll);

        panel.add(searchPanel, BorderLayout.NORTH); // Thêm thanh tìm kiếm lên trên
        
        // --- JList bên trái (Danh sách BST) ---
        boSuuTapList = new JList<>(boSuuTapListModel);
        boSuuTapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boSuuTapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BoSuuTap) {
                    BoSuuTap bst = (BoSuuTap) value;
                    ((JLabel) c).setText(bst.getTenBoSuuTap()); 
                    ((JLabel) c).setFont(new Font("Arial", Font.PLAIN, 14));
                }
                return c;
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(boSuuTapList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Các Bộ Sưu Tập", 
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12), new Color(0, 102, 153))
        );
        listScrollPane.setPreferredSize(new Dimension(300, 0));
        
        // --- JTable bên phải (Sách trong BST) ---
        sachTrongBSTTable = new JTable(sachTrongBSTTableModel);
        sachTrongBSTTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        sachTrongBSTTable.setFont(new Font("Arial", Font.PLAIN, 12));
        sachTrongBSTTable.setRowHeight(25);
        sachTrongBSTTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(sachTrongBSTTable);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, tableScrollPane);
        splitPane.setDividerLocation(300);
        panel.add(splitPane, BorderLayout.CENTER); // Thêm SplitPane vào giữa

        // --- Panel Nút bấm (Giữ nguyên) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Thêm BST");
        JButton editButton = new JButton("Sửa BST");
        JButton deleteButton = new JButton("Xóa BST");
        JButton btnAddBook = new JButton("Thêm Sách vào BST");
        btnAddBook.setFont(new Font("Arial", Font.BOLD, 12));
        JButton btnRemoveBook = new JButton("Xóa Sách khỏi BST");
        btnRemoveBook.setFont(new Font("Arial", Font.BOLD, 12));
        btnRemoveBook.setBackground(new Color(220, 53, 69));
        btnRemoveBook.setForeground(Color.WHITE);
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(btnAddBook);
        controlPanel.add(btnRemoveBook);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // ==================================================
        // <<< GÁN SỰ KIỆN (ĐÃ CẬP NHẬT) >>>
        // ==================================================
        
        // --- SỰ KIỆN NÚT TÌM KIẾM (MỚI) ---
        btnBstSearch.addActionListener(e -> {
            String value = bstSearchField.getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên BST cần tìm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Gọi DAO
            List<BoSuuTap> danhSach = boSuuTapDAO.searchBoSuuTapByName(value);
            // Hiển thị
            renderBoSuuTapList(danhSach);
            // Xóa JTable
            sachTrongBSTTableModel.setRowCount(0);
        });
        
        // --- SỰ KIỆN NÚT XEM TẤT CẢ (MỚI) ---
        btnBstViewAll.addActionListener(e -> {
            loadBoSuuTapListData(); // Tải lại tất cả
            bstSearchField.setText("");
            sachTrongBSTTableModel.setRowCount(0);
        });

        // --- CÁC SỰ KIỆN CŨ (Giữ nguyên) ---
        addButton.addActionListener(e -> {
            BoSuuTapEditDialog dialog = new BoSuuTapEditDialog(this);
            dialog.setVisible(true);
            if (dialog.isSaveSuccess()) {
                loadBoSuuTapListData(); 
                JOptionPane.showMessageDialog(this, "Đã thêm Bộ sưu tập thành công!");
            }
        });
        
        editButton.addActionListener(e -> {
            BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
            if (selectedBST == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập để sửa.", "Chưa chọn BST", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BoSuuTapEditDialog dialog = new BoSuuTapEditDialog(this, selectedBST);
            dialog.setVisible(true);
            if (dialog.isSaveSuccess()) {
                loadBoSuuTapListData(); 
                JOptionPane.showMessageDialog(this, "Đã cập nhật Bộ sưu tập thành công!");
            }
        });
        
        deleteButton.addActionListener(e -> {
            BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
            if (selectedBST == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập để xóa.", "Chưa chọn BST", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String confirmMsg = "Bạn có chắc chắn muốn XÓA VĨNH VIỄN Bộ sưu tập:\n"
                              + selectedBST.getTenBoSuuTap() + "?\n"
                              + "(Tất cả sách liên kết cũng sẽ bị gỡ khỏi bộ sưu tập này)";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, "Xác nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = boSuuTapDAO.deleteBoSuuTap(selectedBST.getMaBoSuuTap());
                if (success) {
                    loadBoSuuTapListData(); 
                    sachTrongBSTTableModel.setRowCount(0);
                    JOptionPane.showMessageDialog(this, "Đã xóa Bộ sưu tập thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa khỏi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnAddBook.addActionListener(e -> {
            BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
            if (selectedBST == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập trước khi thêm sách.", "Chưa chọn BST", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AddSachToBSTDialog dialog = new AddSachToBSTDialog(this, selectedBST.getMaBoSuuTap());
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadSachTrongBST(selectedBST);
            }
        });
        
        btnRemoveBook.addActionListener(e -> {
            BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
            if (selectedBST == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bộ sưu tập.", "Chưa chọn BST", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = sachTrongBSTTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách trong bảng để xóa.", "Chưa chọn sách", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maSach = sachTrongBSTTableModel.getValueAt(selectedRow, 0).toString();
            String tenSach = sachTrongBSTTableModel.getValueAt(selectedRow, 1).toString();
            String confirmMsg = "Bạn có chắc chắn muốn XÓA sách:\n"
                              + tenSach + "\n"
                              + "ra khỏi Bộ sưu tập '" + selectedBST.getTenBoSuuTap() + "'?";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, "Xác nhận Xóa Sách", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = boSuuTapDAO.removeSachFromBoSuuTap(selectedBST.getMaBoSuuTap(), maSach);
                if (success) {
                    loadSachTrongBST(selectedBST);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa sách khỏi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        boSuuTapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                BoSuuTap selectedBST = boSuuTapList.getSelectedValue();
                loadSachTrongBST(selectedBST);
            }
        });

        return panel;
    }
    
    // Quản lý Mượn trả
    private JPanel createMuonTraManagementPanel() {
        // 1. Tạo Panel chính
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 2. <<< SỬA: GÁN JTabbedPane VÀO BIẾN TOÀN CỤC >>>
        // Thay vì: JTabbedPane tabbedPane = new JTabbedPane();
        this.muonTraTabbedPane = new JTabbedPane(); //
        this.muonTraTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 3. Tạo Tab 1 (Danh sách)
        JPanel tab1 = createDanhSachMuonTraPanel();
        this.muonTraTabbedPane.addTab("  Quản lý Mượn Trả  ", tab1); 

        // 4. Tạo Tab 2 (Form mượn)
        JPanel tab2 = createTaoPhieuMuonPanel();
        this.muonTraTabbedPane.addTab("  Tạo Phiếu Mượn  ", tab2);
        
        // 5. Thêm TabbedPane vào Panel chính
        panel.add(this.muonTraTabbedPane, BorderLayout.CENTER);
        
        // 6. Thêm Trình lắng nghe sự kiện khi chuyển tab
        this.muonTraTabbedPane.addChangeListener(e -> {
            int selectedIndex = this.muonTraTabbedPane.getSelectedIndex();
            
            if (selectedIndex == 0) {
                // Tải lại TOÀN BỘ dữ liệu (reset)
                loadMuonTraData(); //
            }
        });
        return panel;
    }
    private JPanel createDanhSachMuonTraPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // --- Panel Tìm kiếm (Giữ nguyên) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Tìm theo:"));
        String[] searchModes = {
            "Mã mượn", "Độc giả", "Sách", "Ngày mượn", "Ngày trả", 
            "Ngày hẹn trả", "Trạng thái", "Loại mượn"
        };
        cbMuonTraSearchMode = new JComboBox<>(searchModes);
        cbMuonTraSearchMode.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(cbMuonTraSearchMode);
        txtMuonTraSearchValue = new JTextField(20);
        txtMuonTraSearchValue.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(txtMuonTraSearchValue);
        JButton btnMuonTraSearch = new JButton("Tìm kiếm");
        btnMuonTraSearch.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(btnMuonTraSearch);
        JButton btnMuonTraViewAll = new JButton("Xem tất cả");
        btnMuonTraViewAll.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(btnMuonTraViewAll);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // --- JTable (Giữ nguyên) ---
        muonTraTable = new JTable(muonTraTableModel);
        muonTraTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        muonTraTable.setFont(new Font("Arial", Font.PLAIN, 12));
        muonTraTable.setRowHeight(25);
        muonTraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- Trình Render Màu (Giữ nguyên) ---
        muonTraTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                                                                isSelected, hasFocus,
                                                                row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 12));
                if (!isSelected) {
                    String trangThai = table.getValueAt(row, 6).toString();
                    if (trangThai.equals("Quá hạn")) {
                        c.setForeground(Color.RED); 
                    } else {
                        c.setForeground(Color.BLACK); 
                    }
                } 
                return c;
            }
        });
        
        JTableHeader header = muonTraTable.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(muonTraTable));
        JScrollPane scrollPane = new JScrollPane(muonTraTable); 
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // --- Panel Nút Bấm (Giữ nguyên) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        JButton chiTietButton = new JButton("Xem Chi Tiết"); 
        JButton traSachButton = new JButton("Đánh Dấu Đã Trả"); 
        JButton giaHanButton = new JButton("Gia Hạn"); 
        JButton xoaButton = new JButton("Xóa"); 
        xoaButton.setBackground(new Color(220, 53, 69));
        xoaButton.setForeground(Color.WHITE);
        xoaButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        controlPanel.add(chiTietButton); 
        controlPanel.add(traSachButton);
        controlPanel.add(giaHanButton);
        controlPanel.add(xoaButton); 
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // --- Sự kiện Nút Tìm kiếm (Giữ nguyên) ---
        btnMuonTraSearch.addActionListener(e -> {
            String mode = cbMuonTraSearchMode.getSelectedItem().toString();
            String value = txtMuonTraSearchValue.getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị cần tìm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<MuonTra> danhSach = muonTraDAO.searchMuonTra(mode, value);
            renderMuonTraTable(danhSach);
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu mượn nào khớp.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnMuonTraViewAll.addActionListener(e -> {
            loadMuonTraData(); 
            txtMuonTraSearchValue.setText("");
        });

        // ==================================================
        // <<< SỰ KIỆN NÚT XEM CHI TIẾT (ĐÃ SỬA) >>>
        // ==================================================
        chiTietButton.addActionListener(e -> {
            int selectedRow = muonTraTable.getSelectedRow();
            if (selectedRow == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để xem chi tiết.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
                return; 
            }
            MuonTra selectedMT = this.currentMuonTraList.get(selectedRow);
            String maDocGia = selectedMT.getDocGia().getMaDocGia();
            java.util.Date ngayMuon = selectedMT.getNgayMuon();
            List<MuonTra> phieuMuonGroup = muonTraDAO.getPhieuMuonGroup(maDocGia, ngayMuon);

            if (phieuMuonGroup != null && !phieuMuonGroup.isEmpty()) {
                // <<< SỬA: Truyền 'docGiaDAO' vào >>>
                ChiTietMuonTraDialog dialog = new ChiTietMuonTraDialog(MainFrame.this, phieuMuonGroup, this.docGiaDAO);
                dialog.setVisible(true);
                
                // <<< MỚI: Kiểm tra sau khi dialog đóng >>>
                if (dialog.isDataChanged()) {
                    loadDocGiaData(); // Tải lại bảng Độc Giả (để cập nhật trạng thái "Đã khóa")
                }
            } else { 
                JOptionPane.showMessageDialog(MainFrame.this, "Không thể tải chi tiết phiếu mượn (Không tìm thấy nhóm).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- (Các sự kiện nút Trả, Gia Hạn, Xóa giữ nguyên) ---
        traSachButton.addActionListener(e -> {
            int selectedRow = muonTraTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để đánh dấu đã trả.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MuonTra selectedMT = this.currentMuonTraList.get(selectedRow);
            if (selectedMT.getTrangThai().equals("Đã trả")) {
                JOptionPane.showMessageDialog(this, "Phiếu mượn này đã được trả.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String confirmMsg = "Bạn có chắc chắn muốn đánh dấu sách:\n"
                              + selectedMT.getSach().getTenSach() + "\n"
                              + "(Độc giả: " + selectedMT.getDocGia().getHoTen() + ")\n"
                              + "là ĐÃ TRẢ?";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, "Xác nhận Trả Sách", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = muonTraDAO.danhDauDaTra(selectedMT.getMaMuonTra(), selectedMT.getSach().getMaSach());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đánh dấu đã trả thành công!");
                    loadMuonTraData();
                    loadSachData();
                } else {
                    JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi CSDL khi cập nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        giaHanButton.addActionListener(e -> {
            int selectedRow = muonTraTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để gia hạn.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MuonTra selectedMT = this.currentMuonTraList.get(selectedRow);
            if (selectedMT.getTrangThai().equals("Đã trả")) {
                JOptionPane.showMessageDialog(this, "Phiếu mượn này đã được trả. Không thể gia hạn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.util.Date ngayHenTraHienTai = selectedMT.getNgayHenTra();
            Calendar cal = Calendar.getInstance();
            cal.setTime(ngayHenTraHienTai);
            cal.add(Calendar.DAY_OF_YEAR, 7);
            java.util.Date ngayDeNghi = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Object newDateObj = JOptionPane.showInputDialog(this, "Ngày hẹn trả hiện tại: " + sdf.format(ngayHenTraHienTai) + "\nNhập ngày hẹn trả mới (dd/MM/yyyy HH:mm):", "Gia Hạn Sách (Mã phiếu: " + selectedMT.getMaMuonTra() + ")", JOptionPane.PLAIN_MESSAGE, null, null, sdf.format(ngayDeNghi));
            if (newDateObj == null) return;
            String newDateStr = newDateObj.toString();
            try {
                java.util.Date ngayGiaHan = sdf.parse(newDateStr.trim());
                if (!ngayGiaHan.after(ngayHenTraHienTai)) {
                    JOptionPane.showMessageDialog(this, "Ngày gia hạn mới phải sau ngày hẹn trả hiện tại (" + sdf.format(ngayHenTraHienTai) + ").", "Lỗi Ngày", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = muonTraDAO.giaHanPhieuMuon(selectedMT.getMaMuonTra(), ngayGiaHan);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Gia hạn phiếu mượn thành công!");
                    loadMuonTraData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gia hạn thất bại. Phiếu này có thể đã được trả.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng dùng dd/MM/yyyy HH:mm", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        xoaButton.addActionListener(e -> {
            int selectedRow = muonTraTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để xóa.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MuonTra selectedMT = this.currentMuonTraList.get(selectedRow);
            if (!selectedMT.getTrangThai().equals("Đã trả")) {
                JOptionPane.showMessageDialog(this, "Lỗi: Chỉ có thể xóa phiếu mượn đã ở trạng thái 'Đã trả'.\n" + "Nếu sách đang mượn, bạn phải dùng nút 'Đánh Dấu Đã Trả' trước.", "Không thể xóa", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String confirmMsg = "Bạn có chắc chắn muốn XÓA VĨNH VIỄN lịch sử mượn này?\n"
                              + "Mã phiếu: " + selectedMT.getMaMuonTra() + "\n"
                              + "Sách: " + selectedMT.getSach().getTenSach() + "\n\n"
                              + "Hành động này không thể hoàn tác.";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, "Xác nhận Xóa Lịch Sử", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = muonTraDAO.deleteMuonTra(selectedMT.getMaMuonTra());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đã xóa lịch sử mượn thành công!");
                    loadMuonTraData();
                } else {
                    JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi CSDL khi xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    private JPanel createTaoPhieuMuonPanel() {
        // Panel chính (để chứa JSplitPane)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 1. Tạo Panel bên trái (Người mượn)
        JPanel nguoiMuonPanel = createPhieuMuonLeftPanel();

        // 2. Tạo 2 Panel bên phải (Tìm Sách và Sách đã chọn)
        JPanel timSachPanel = createPhieuMuonRightTopPanel();
        JPanel sachDaChonPanel = createPhieuMuonRightBottomPanel();

        // 3. Gộp 2 panel phải vào một JSplitPane DỌC
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                    timSachPanel, sachDaChonPanel);
        rightSplitPane.setDividerLocation(300); // Tùy chỉnh độ cao ban đầu
        rightSplitPane.setResizeWeight(0.5);

        // 4. Gộp panel trái và rightSplitPane vào JSplitPane NGANG
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                    nguoiMuonPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(300); // Tùy chỉnh độ rộng ban đầu
        
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        return mainPanel;
    }
    // TRONG FILE: MainFrame.java
// HÃY XÓA HÀM createPhieuMuonLeftPanel() CŨ VÀ THAY BẰNG HÀM NÀY:

    /**
     * HÀM HELPER 1 (ĐÃ SỬA LẠI HOÀN TOÀN): Tạo Panel "Người mượn" (bên trái)
     */
    private JPanel createPhieuMuonLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Người mượn", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ... (Tất cả code GridBagLayout GBC từ Hàng 0 đến Hàng 13
        //      cho txtMaDGMuon, btnTimDocGia, lblAnhDocGiaMuon, 
        //      txtEmailDGMuon, txtSdtDGMuon, txtTrangThaiDGMuon, 
        //      txtTenDGMuon, txtSoSachDaMuon... GIỮ NGUYÊN)
        
        // Hàng 0: Mã thành viên
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Mã thành viên:"), gbc);
        // Hàng 1: Ô nhập Mã + Nút Tìm
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 1.0; gbc.gridwidth = 1;
        txtMaDGMuon = new JTextField(15);
        panel.add(txtMaDGMuon, gbc);
        gbc.gridx = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        btnTimDocGia = new JButton("Tìm");
        panel.add(btnTimDocGia, gbc);
        // Hàng 2: Khu vực hiển thị ảnh
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        lblAnhDocGiaMuon = new JLabel();
        lblAnhDocGiaMuon.setPreferredSize(new Dimension(200, 200));
        lblAnhDocGiaMuon.setBorder(BorderFactory.createEtchedBorder());
        lblAnhDocGiaMuon.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhDocGiaMuon.setText("(Chưa có ảnh)");
        panel.add(lblAnhDocGiaMuon, gbc);
        // Hàng 3: Email (MỚI)
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Email:"), gbc);
        // Hàng 4: Ô Email
        gbc.gridy = 4;
        txtEmailDGMuon = new JTextField();
        txtEmailDGMuon.setEnabled(false);
        txtEmailDGMuon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmailDGMuon.setDisabledTextColor(Color.BLACK);
        panel.add(txtEmailDGMuon, gbc);
        // Hàng 5: SĐT
        gbc.gridy = 5;
        panel.add(new JLabel("SĐT:"), gbc);
        // Hàng 6: Ô SĐT
        gbc.gridy = 6;
        txtSdtDGMuon = new JTextField();
        txtSdtDGMuon.setEnabled(false);
        txtSdtDGMuon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSdtDGMuon.setDisabledTextColor(Color.BLACK);
        panel.add(txtSdtDGMuon, gbc);
        // Hàng 7: Trạng thái
        gbc.gridy = 7;
        panel.add(new JLabel("Trạng thái:"), gbc);
        // Hàng 8: Ô Trạng thái
        gbc.gridy = 8;
        txtTrangThaiDGMuon = new JTextField();
        txtTrangThaiDGMuon.setEnabled(false);
        txtTrangThaiDGMuon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTrangThaiDGMuon.setDisabledTextColor(Color.BLACK);
        panel.add(txtTrangThaiDGMuon, gbc);
        // Hàng 9: Họ và tên
        gbc.gridy = 9;
        panel.add(new JLabel("Họ và tên:"), gbc);
        // Hàng 10: Ô Họ tên
        gbc.gridy = 10;
        txtTenDGMuon = new JTextField();
        txtTenDGMuon.setEnabled(false);
        txtTenDGMuon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTenDGMuon.setDisabledTextColor(Color.BLACK);
        panel.add(txtTenDGMuon, gbc);
        // Hàng 11: Số sách đã mượn
        gbc.gridy = 11;
        panel.add(new JLabel("Số sách trong giỏ:"), gbc); // <<< Sửa chữ
        // Hàng 12: Ô Số sách
        gbc.gridy = 12;
        txtSoSachDaMuon = new JTextField("0");
        txtSoSachDaMuon.setEnabled(false);
        txtSoSachDaMuon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtSoSachDaMuon.setDisabledTextColor(Color.RED);
        panel.add(txtSoSachDaMuon, gbc);
        // Hàng 13: Panel đệm
        gbc.gridy = 13; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);
        
        // === GÁN SỰ KIỆN TÌM ĐỘC GIẢ (ĐÃ SỬA LOGIC) ===
        btnTimDocGia.addActionListener(e -> {
            String maDocGia = txtMaDGMuon.getText().trim();
            if (maDocGia.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã độc giả.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DocGia dg = docGiaDAO.getDocGiaByMa(maDocGia);
            
            if (dg == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả với mã: " + maDocGia, "Không tìm thấy", JOptionPane.ERROR_MESSAGE);
                clearFormNguoiMuon();
                this.docGiaHienTai = null; // <<< SỬA
            } else if (dg.isArchived()) {
                 JOptionPane.showMessageDialog(this, "Độc giả này đã bị xóa (lưu trữ). Không thể mượn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 clearFormNguoiMuon();
                 this.docGiaHienTai = null; // <<< SỬA
            } else {
                // TÌM THẤY: Lưu lại độc giả
                this.docGiaHienTai = dg; // <<< SỬA: LƯU ĐỘC GIẢ
                
                // Hiển thị thông tin
                txtTenDGMuon.setText(dg.getHoTen());
                txtEmailDGMuon.setText(dg.getEmail() != null ? dg.getEmail() : "");
                txtSdtDGMuon.setText(dg.getSdt() != null ? dg.getSdt() : "");
                txtTrangThaiDGMuon.setText(dg.getTrangThai());
                
                // TODO: Cần gọi 1 hàm DAO khác để lấy số sách đang mượn (LỊCH SỬ)
                // txtSoSachDaMuon.setText("0"); // Tạm thời
                // (Giữ nguyên logic cũ là đếm giỏ hàng)
                
                loadImageForMuon(dg.getDuongDanAnh());
                
                if (dg.isBlocked()) {
                     JOptionPane.showMessageDialog(this, "Độc giả này đang bị KHÓA. Không thể mượn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                     txtTrangThaiDGMuon.setForeground(Color.RED);
                } else {
                     txtTrangThaiDGMuon.setForeground(new Color(0, 100, 0));
                }
            }
        });

        return panel;
    }
    private JPanel createPhieuMuonRightTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Tìm Sách", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );

        // === Panel Top: Thanh tìm kiếm và nút "Tạo phiếu" ===
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Panel Top-Left: Thanh tìm kiếm
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchBarPanel.add(new JLabel("Tìm theo:"));
        
        String[] searchModes = {"Tên sách", "Mã sách", "Tác giả"};
        cbTimSachMuonMode = new JComboBox<>(searchModes);
        cbTimSachMuonMode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchBarPanel.add(cbTimSachMuonMode);

        txtTimSachMuon = new JTextField(15);
        searchBarPanel.add(txtTimSachMuon);
        
        JButton btnTimSach = new JButton("Tìm");
        btnTimSach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBarPanel.add(btnTimSach);
        
        JButton btnXemTatCaSachMuon = new JButton("Xem tất cả");
        btnXemTatCaSachMuon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBarPanel.add(btnXemTatCaSachMuon);

        topPanel.add(searchBarPanel, BorderLayout.WEST);

        // Panel Top-Right: Nút "Tạo phiếu"
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnTaoPhieuMuon = new JButton("Tạo phiếu mượn");
        btnTaoPhieuMuon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTaoPhieuMuon.setBackground(new Color(30, 144, 255));
        btnTaoPhieuMuon.setForeground(Color.WHITE);
        actionPanel.add(btnTaoPhieuMuon);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // === Center: Bảng kết quả tìm kiếm ===
        sachTimKiemTable = new JTable(sachTimKiemTableModel);
        sachTimKiemTable.setRowHeight(25);
        sachTimKiemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // <<< DÒNG CODE SỬ DỤNG IMPORT
        sachTimKiemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kiểm tra nếu là double-click
                if (e.getClickCount() == 2) {
                    int row = sachTimKiemTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // Lấy mã sách từ cột 0
                        String maSach = sachTimKiemTable.getValueAt(row, 0).toString();
                        // Gọi hàm helper đã tạo
                        showChiTietSachDialog(maSach); 
                    }
                }
            }
        });
        panel.add(new JScrollPane(sachTimKiemTable), BorderLayout.CENTER);

        // === Bottom: Nút "Thêm vào giỏ" ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnThemSachVaoGio = new JButton("Thêm sách đã chọn vào giỏ");
        btnThemSachVaoGio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bottomPanel.add(btnThemSachVaoGio);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // === GÁN SỰ KIỆN ===
        
        // 1. Nút Tìm
        btnTimSach.addActionListener(e -> {
            String mode = cbTimSachMuonMode.getSelectedItem().toString();
            String value = txtTimSachMuon.getText().trim();

            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị cần tìm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                txtTimSachMuon.requestFocus();
                return;
            }
            
            List<Sach> danhSach = sachDAO.searchSach(mode, value);
            renderSachTimKiemTable(danhSach);
            
            if(danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 2. Nút Xem tất cả
        btnXemTatCaSachMuon.addActionListener(e -> {
            List<Sach> danhSach = sachDAO.getAllSach();
            renderSachTimKiemTable(danhSach);
            txtTimSachMuon.setText("");
        });

        // 3. Nút Thêm vào giỏ
        btnThemSachVaoGio.addActionListener(e -> {
            int selectedRow = sachTimKiemTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một sách từ bảng 'Tìm Sách' (ở trên) để thêm.", "Chưa chọn sách", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maSach = sachTimKiemTableModel.getValueAt(selectedRow, 0).toString();
            String tenSach = sachTimKiemTableModel.getValueAt(selectedRow, 1).toString();
            String trangThai = sachTimKiemTableModel.getValueAt(selectedRow, 4).toString();

            if (trangThai.equalsIgnoreCase("Hết sách")) {
                JOptionPane.showMessageDialog(this, "Sách '" + tenSach + "' đã hết. Không thể mượn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            for (int i = 0; i < sachDaChonTableModel.getRowCount(); i++) {
                String maSachTrongGio = sachDaChonTableModel.getValueAt(i, 0).toString();
                if (maSach.equals(maSachTrongGio)) {
                    JOptionPane.showMessageDialog(this, "Sách '" + tenSach + "' đã có trong giỏ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            sachDaChonTableModel.addRow(new Object[]{maSach, tenSach});
            
            // Cập nhật số lượng sách trong giỏ
            updateSoSachDaChonCount(); 
        });
        
        // 4. Nút Tạo phiếu mượn (LOGIC MỚI)
        btnTaoPhieuMuon.addActionListener(e -> {
            
            // Validate 1: Phải chọn độc giả
            if (this.docGiaHienTai == null) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng tìm và chọn một độc giả hợp lệ ở panel 'Người mượn'.", 
                    "Chưa có độc giả", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate 2: Độc giả không bị khóa
            if (this.docGiaHienTai.isBlocked()) {
                JOptionPane.showMessageDialog(this, 
                    "Độc giả '" + this.docGiaHienTai.getHoTen() + "' đang bị KHÓA. Không thể tạo phiếu.", 
                    "Lỗi Độc Giả", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate 3: Giỏ hàng phải có sách
            if (sachDaChonTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Giỏ hàng đang trống. Vui lòng thêm ít nhất 1 cuốn sách.", 
                    "Giỏ hàng trống", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // --- Nếu tất cả OK: Mở Dialog Xác Nhận ---
            
            XacNhanMuonDialog dialog = new XacNhanMuonDialog(this, this.currentUserAccount, this.docGiaHienTai, this.sachDaChonTableModel);
            dialog.setVisible(true);
            
            // --- Xử lý sau khi Dialog đóng ---
            if (dialog.isSaveSuccess()) {
                // Xóa sạch form
                clearFormNguoiMuon();
                
                // Xóa giỏ hàng và bảng tìm kiếm
                sachDaChonTableModel.setRowCount(0);
                sachTimKiemTableModel.setRowCount(0);
                
                // Cập nhật lại số lượng (về 0)
                updateSoSachDaChonCount();
                
                // Tải lại bảng sách chính (vì số lượng 'conLai' đã thay đổi)
                loadSachData();
            }
            // Nếu không save (nhấn Hủy) thì không làm gì, giữ nguyên form
        });

        return panel;
    }
    private void renderSachTimKiemTable(List<Sach> danhSach) {
        // Xóa dữ liệu cũ
        sachTimKiemTableModel.setRowCount(0);

        // Các cột: {"Mã sách", "Tên sách", "NXB", "Năm xuất bản", "Trạng thái"}
        for (Sach s : danhSach) {
            
            String namXB = (s.getNamXuatBan() == 0) ? "N/A" : String.valueOf(s.getNamXuatBan());
            
            // Hàm s.getTrangThai() (từ Sach.java) sẽ trả về "Còn sách" hoặc "Hết sách"
            String trangThai = s.getTrangThai(); 
            
            Object[] row = new Object[] {
                s.getMaSach(),
                s.getTenSach(),
                s.getNhaXuatBan(),
                namXB,
                trangThai // <<< Cột trạng thái rất quan trọng
            };
            sachTimKiemTableModel.addRow(row);
        }
    }

    /**
     * HÀM HELPER 3: Tạo Panel "Sách đã chọn" (bên phải-dưới)
     */
    private JPanel createPhieuMuonRightBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Sách đã chọn (Giỏ)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );

        // Top: Nút Xóa (Giữ nguyên)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXoaChon = new JButton("Xóa khỏi giỏ");
        btnXoaChon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoaChon.setBackground(new Color(220, 53, 69));
        btnXoaChon.setForeground(Color.WHITE);
        topPanel.add(btnXoaChon);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center: Bảng sách đã chọn (Giữ nguyên)
        sachDaChonTable = new JTable(sachDaChonTableModel);
        sachDaChonTable.setRowHeight(25);
        sachDaChonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(sachDaChonTable), BorderLayout.CENTER);
        
        // GÁN SỰ KIỆN NÚT XÓA
        btnXoaChon.addActionListener(e -> {
            int selectedRow = sachDaChonTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một sách từ giỏ (ở dưới) để xóa.", "Chưa chọn sách", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Bạn chắc chắn muốn xóa sách này khỏi giỏ?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                sachDaChonTableModel.removeRow(selectedRow);
                
                // <<< DÒNG MỚI ĐƯỢC THÊM: CẬP NHẬT SỐ LƯỢNG ===
                updateSoSachDaChonCount(); 
                // =============================================
            }
        });

        return panel;
    }
    // Hàm đổi Mật khẩu
    private JPanel createTaiKhoanManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. Panel Nút bấm (NORTH) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnTaoTaiKhoan = new JButton("Tạo Tài khoản");
        JButton btnSuaTaiKhoan = new JButton("Sửa Tài khoản");
        JButton btnXoaTaiKhoan = new JButton("Xóa Tài khoản");
        
        btnXoaTaiKhoan.setBackground(new Color(220, 53, 69)); // Màu đỏ
        btnXoaTaiKhoan.setForeground(Color.WHITE);
        
        controlPanel.add(btnTaoTaiKhoan);
        controlPanel.add(btnSuaTaiKhoan);
        controlPanel.add(btnXoaTaiKhoan);
        
        panel.add(controlPanel, BorderLayout.NORTH);

        // --- 2. Panel Lưới (CENTER) ---
        // Sử dụng GridLayout với 3 cột, tự động thêm hàng
        taiKhoanGridPanel = new JPanel(new GridLayout(0, 3, 10, 10)); 
        taiKhoanGridPanel.setBackground(Color.WHITE);
        
        // Thêm 1 panel đệm để lưới không bị kéo dãn ra toàn bộ
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(taiKhoanGridPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. KIỂM TRA QUYỀN ADMIN ---
        // Chỉ admin mới được dùng các nút này
        if (currentUserAccount != null && "admin".equalsIgnoreCase(currentUserAccount.getQuyen())) {
            btnTaoTaiKhoan.setEnabled(true);
            btnSuaTaiKhoan.setEnabled(true);
            btnXoaTaiKhoan.setEnabled(true);
        } else {
            // Nếu không phải admin, vô hiệu hóa
            btnTaoTaiKhoan.setEnabled(false);
            btnSuaTaiKhoan.setEnabled(false);
            btnXoaTaiKhoan.setEnabled(false);
            
            JLabel lblWarning = new JLabel("Bạn không có quyền thực hiện thao tác trên trang này.");
            lblWarning.setFont(new Font("Arial", Font.BOLD, 14));
            lblWarning.setForeground(Color.RED);
            controlPanel.add(lblWarning);
        }

        // --- 4. GÁN SỰ KIỆN ---
        
        // Nút TẠO
        btnTaoTaiKhoan.addActionListener(e -> {
            TaiKhoanEditDialog dialog = new TaiKhoanEditDialog(this); // Mở dialog rỗng
            dialog.setVisible(true);
            
            if (dialog.isSaveSuccess()) {
                loadTaiKhoanData(); // Tải lại lưới
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!");
            }
        });

        // Nút SỬA
        btnSuaTaiKhoan.addActionListener(e -> {
            if (selectedTaiKhoan == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ lưới để sửa.", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            TaiKhoanEditDialog dialog = new TaiKhoanEditDialog(this, selectedTaiKhoan); // Mở dialog với dữ liệu
            dialog.setVisible(true);
            
            if (dialog.isSaveSuccess()) {
                loadTaiKhoanData(); // Tải lại lưới
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
            }
        });

        // Nút XÓA
        btnXoaTaiKhoan.addActionListener(e -> {
            if (selectedTaiKhoan == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ lưới để xóa.", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Không cho phép admin tự xóa mình
            if (selectedTaiKhoan.getMaTaiKhoan() == currentUserAccount.getMaTaiKhoan()) {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String confirmMsg = "Bạn có chắc chắn muốn XÓA VĨNH VIỄN tài khoản:\n"
                              + selectedTaiKhoan.getTenNguoiDung() + " (" + selectedTaiKhoan.getTenDangNhap() + ")?\n"
                              + "Hành động này không thể hoàn tác.";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, "Xác nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = taiKhoanDAO.deleteTaiKhoan(selectedTaiKhoan.getMaTaiKhoan());
                if (success) {
                    loadTaiKhoanData(); // Tải lại
                    JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công.");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại. Lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    /**
     * HÀM MỚI: Tải tất cả tài khoản và hiển thị lên lưới
     */
    private void loadTaiKhoanData() {
        if (taiKhoanGridPanel == null) return; // Panel chưa được tạo

        // Xóa lựa chọn cũ
        selectedTaiKhoan = null;
        if (selectedTaiKhoanPanel != null) {
            selectedTaiKhoanPanel.setBorder(BorderFactory.createEtchedBorder());
            selectedTaiKhoanPanel = null;
        }
        
        // Xóa lưới cũ
        taiKhoanGridPanel.removeAll();
        
        // Lấy dữ liệu mới từ DAO (Hàm này sẽ được thêm ở Bước 3)
        List<TaiKhoan> danhSach = taiKhoanDAO.getAllTaiKhoan();
        
        for (TaiKhoan tk : danhSach) {
            JPanel cell = createTaiKhoanCell(tk); // Hàm helper tạo 1 ô
            taiKhoanGridPanel.add(cell);
        }
        
        taiKhoanGridPanel.revalidate();
        taiKhoanGridPanel.repaint();
    }

    /**
     * HÀM HELPER MỚI: Tạo 1 ô (cell) trong lưới
     */
    private JPanel createTaiKhoanCell(TaiKhoan tk) {
        JPanel cellPanel = new JPanel(new BorderLayout(10, 5));
        cellPanel.setBorder(BorderFactory.createEtchedBorder());
        cellPanel.setBackground(Color.WHITE);
        // Tăng chiều cao để chứa đủ thông tin
        cellPanel.setPreferredSize(new Dimension(250, 120)); // <<< SỬA

        // 1. Ảnh (Bên trái - WEST) - Giữ nguyên
        JLabel lblAnh = new JLabel();
        lblAnh.setPreferredSize(new Dimension(80, 80));
        lblAnh.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        loadDynamicImage(lblAnh, tk.getDuongDanAnh(), 80, 80, "(Ảnh)");
        cellPanel.add(lblAnh, BorderLayout.WEST);

        // 2. Thông tin (Ở giữa - CENTER) - (ĐÃ SỬA)
        // Dùng GridBagLayout để căn chỉnh (Nhãn bên trái, Giá trị bên phải)
        JPanel infoPanel = new JPanel(new GridBagLayout()); 
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.insets = new Insets(2, 2, 2, 5); // Khoảng cách
        gbcInfo.anchor = GridBagConstraints.WEST; // Căn lề trái

        // --- Thêm các Label và Giá trị ---
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font valueFont = new Font("Arial", Font.PLAIN, 12);
        
        // Tên người dùng
        gbcInfo.gridx = 0; gbcInfo.gridy = 0; gbcInfo.weightx = 0.0;
        JLabel lblTenUserTitle = new JLabel("Tên user:");
        lblTenUserTitle.setFont(labelFont);
        infoPanel.add(lblTenUserTitle, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        JLabel lblTenNguoiDung = new JLabel(tk.getTenNguoiDung());
        lblTenNguoiDung.setFont(valueFont);
        infoPanel.add(lblTenNguoiDung, gbcInfo);
        
        // Tên tài khoản
        gbcInfo.gridy = 1; gbcInfo.gridx = 0; gbcInfo.weightx = 0.0;
        JLabel lblTenTKTitle = new JLabel("Tài khoản:");
        lblTenTKTitle.setFont(labelFont);
        infoPanel.add(lblTenTKTitle, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        JLabel lblTenDangNhap = new JLabel(tk.getTenDangNhap());
        lblTenDangNhap.setFont(valueFont);
        infoPanel.add(lblTenDangNhap, gbcInfo);

        // Email
        gbcInfo.gridy = 2; gbcInfo.gridx = 0; gbcInfo.weightx = 0.0;
        JLabel lblEmailTitle = new JLabel("Email:");
        lblEmailTitle.setFont(labelFont);
        infoPanel.add(lblEmailTitle, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        JLabel lblEmail = new JLabel(tk.getEmail());
        lblEmail.setFont(valueFont);
        infoPanel.add(lblEmail, gbcInfo);

        // SĐT
        gbcInfo.gridy = 3; gbcInfo.gridx = 0; gbcInfo.weightx = 0.0;
        JLabel lblSdtTitle = new JLabel("SĐT:");
        lblSdtTitle.setFont(labelFont);
        infoPanel.add(lblSdtTitle, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        // Kiểm tra SĐT có null hoặc rỗng không
        String sdt = (tk.getSdt() != null && !tk.getSdt().isEmpty()) ? tk.getSdt() : "N/A";
        JLabel lblSdt = new JLabel(sdt);
        lblSdt.setFont(valueFont);
        infoPanel.add(lblSdt, gbcInfo);

        cellPanel.add(infoPanel, BorderLayout.CENTER);

        // 3. Sự kiện Click để CHỌN (Giữ nguyên)
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Hủy viền highlight của ô cũ
                if (selectedTaiKhoanPanel != null) {
                    selectedTaiKhoanPanel.setBorder(BorderFactory.createEtchedBorder());
                    selectedTaiKhoanPanel.setBackground(Color.WHITE);
                }
                
                // Đặt ô này là ô được chọn
                selectedTaiKhoan = tk;
                selectedTaiKhoanPanel = cellPanel;
                
                // Highlight ô mới
                cellPanel.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2));
                cellPanel.setBackground(new Color(230, 245, 255));
            }
        });

        return cellPanel;
    }
    
    // CUSTOM RENDERER CHO HEADER CỦA JTABLE
    private class HeaderRenderer implements TableCellRenderer {
        final TableCellRenderer defaultRenderer;
        public HeaderRenderer(JTable table) {
            this.defaultRenderer = table.getTableHeader().getDefaultRenderer();
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER); 
            label.setBackground(new Color(173, 216, 230)); 
            label.setForeground(Color.BLACK);
            return label;
        }
    }
    // TẠO HÀM MỚI NÀY (hoặc viết đè lên hàm cũ)
    private void loadSachData() {
        // Lấy TẤT CẢ Sách
        List<Sach> danhSach = sachDAO.getAllSach();
        // Hiển thị lên JTable
        renderSachTable(danhSach);
    }
    private void loadTacGiaData() {
        // Xóa dữ liệu cũ
        tacGiaTableModel.setRowCount(0);

        // Lấy danh sách Tác giả từ DAO
        List<TacGia> danhSach = tacGiaDAO.getAllTacGia();

        for (TacGia tg : danhSach) {
            Object[] row = new Object[] {
                tg.getMaTacGia(),
                tg.getTenTacGia(),
                tg.getEmail(),
                tg.getSdt(),
                tg.getChucDanh()
            };
            tacGiaTableModel.addRow(row);
        }
    }
    private void themTacGia() {
        try {
            // Bước 1: Lấy mã tác giả mới nhất từ CSDL
            String newMaTacGia = tacGiaDAO.generateNewMaTacGia(); //

            // Bước 2: Tạo và hiển thị Dialog mới
            TacGiaEditDialog dialog = new TacGiaEditDialog(this, newMaTacGia);
            dialog.setVisible(true);

            // Bước 3: (Sau khi dialog đóng lại) Kiểm tra xem có lưu thành công không
            if (dialog.isSaveSuccess()) {
                // Nếu thành công, TẢI LẠI DỮ LIỆU BẢNG TÁC GIẢ
                loadTacGiaData(); 
                JOptionPane.showMessageDialog(this, "Đã thêm tác giả thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tạo mã tác giả mới: " + e.getMessage(), 
                "Lỗi CSDL", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void suaTacGia() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = tacGiaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một tác giả để sửa.", 
                "Chưa chọn tác giả", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy Mã Tác Giả từ hàng đã chọn (Cột 0)
        String maTacGia = tacGiaTableModel.getValueAt(selectedRow, 0).toString();

        // 3. Dùng DAO để lấy TOÀN BỘ thông tin tác giả
        TacGia tacGiaToEdit = tacGiaDAO.getTacGiaByMa(maTacGia);

        if (tacGiaToEdit == null) {
            JOptionPane.showMessageDialog(this, "Không thể tìm thấy dữ liệu tác giả (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Mở Dialog ở chế độ SỬA (dùng Constructor mới)
        TacGiaEditDialog dialog = new TacGiaEditDialog(this, tacGiaToEdit);
        dialog.setVisible(true);

        // 5. Nếu lưu thành công, tải lại bảng
        if (dialog.isSaveSuccess()) {
            loadTacGiaData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật tác giả thành công!");
        }
    }
    private void xoaTacGia() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = tacGiaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một tác giả để xóa.", 
                "Chưa chọn tác giả", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy thông tin tác giả từ JTable
        String maTacGia = tacGiaTableModel.getValueAt(selectedRow, 0).toString();
        String tenTacGia = tacGiaTableModel.getValueAt(selectedRow, 1).toString();

        // 3. Hiển thị hộp thoại xác nhận (Confirm Dialog) - RẤT QUAN TRỌNG
        String confirmMsg = "Bạn có chắc chắn muốn XÓA VĨNH VIỄN tác giả:\n"
                          + tenTacGia + " (Mã: " + maTacGia + ")\n\n"
                          + "CẢNH BÁO: Hành động này không thể hoàn tác.\n"
                          + "Tác giả này sẽ bị gỡ khỏi tất cả các sách họ đang liên kết.";
                          
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "Xác nhận XÓA VĨNH VIỄN", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE // Dùng biểu tượng cảnh báo
        );

        // 4. Xử lý nếu người dùng đồng ý (chọn Yes)
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi DAO để thực hiện xóa cứng
            boolean success = tacGiaDAO.deleteTacGia(maTacGia);
            
            if (success) {
                // Tải lại bảng Tác Giả
                loadTacGiaData(); 
                
                // QUAN TRỌNG: Tải lại cả bảng Sách
                // vì cột "Tác giả" trong bảng Sách có thể đã thay đổi
                loadSachData(); 
                
                JOptionPane.showMessageDialog(this, "Đã xóa tác giả thành công.");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Xóa tác giả thất bại.\n" + 
                    "Hãy đảm bảo không còn dữ liệu nào liên quan (ví dụ: trong Mượn Trả) trước khi xóa.", 
                    "Lỗi CSDL", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        // Nếu người dùng chọn "No", không làm gì cả.
    }
    private void renderDocGiaTable(List<DocGia> danhSach) {
        // Xóa dữ liệu cũ
        docGiaTableModel.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (DocGia dg : danhSach) {
            String ngaySinhStr = (dg.getNgaySinh() != null) ? sdf.format(dg.getNgaySinh()) : "N/A";
            
            // Cột: {"Mã ĐG", "Họ Tên", "Ngày Sinh", "Email", "SĐT", "Địa Chỉ", "Trạng Thái"}
            Object[] row = new Object[] {
                dg.getMaDocGia(),
                dg.getHoTen(),
                ngaySinhStr,
                dg.getEmail(),
                dg.getSdt(),       
                dg.getDiaChi(),    
                dg.getTrangThai()  // Dùng hàm tiện ích
            };
            docGiaTableModel.addRow(row);
        }
    }

    /**
     * HÀM CŨ (Đã sửa): Tải TẤT CẢ độc giả
     */
    private void loadDocGiaData() {
        // Lấy danh sách Độc Giả từ DAO
        List<DocGia> danhSach = docGiaDAO.getAllDocGia();
        
        // Gọi hàm render mới
        renderDocGiaTable(danhSach);
    }
    private void themDocGia() {
        // Không cần tạo mã trước
        
        // Bước 1: Tạo và hiển thị Dialog mới
        // Dùng constructor không cần mã
        DocGiaEditDialog dialog = new DocGiaEditDialog(this);
        dialog.setVisible(true);

        // Bước 2: (Sau khi dialog đóng lại) Kiểm tra xem có lưu thành công không
        if (dialog.isSaveSuccess()) {
            // Nếu thành công, TẢI LẠI DỮ LIỆU BẢNG ĐỘC GIẢ
            loadDocGiaData(); 
            JOptionPane.showMessageDialog(this, "Đã thêm độc giả thành công!");
        }
    }
    // HÀM MỚI: DÀNH CHO NÚT SỬA ĐỘC GIẢ
    private void suaDocGia() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = docGiaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một độc giả để sửa.", 
                "Chưa chọn độc giả", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy Mã Độc Giả từ hàng đã chọn (Cột 0)
        String maDocGia = docGiaTableModel.getValueAt(selectedRow, 0).toString();

        // 3. Dùng DAO để lấy TOÀN BỘ thông tin độc giả
        DocGia docGiaToEdit = docGiaDAO.getDocGiaByMa(maDocGia);

        if (docGiaToEdit == null) {
            JOptionPane.showMessageDialog(this, "Không thể tìm thấy dữ liệu độc giả (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Mở Dialog ở chế độ SỬA (dùng Constructor mới)
        DocGiaEditDialog dialog = new DocGiaEditDialog(this, docGiaToEdit);
        dialog.setVisible(true);

        // 5. Nếu lưu thành công, tải lại bảng
        if (dialog.isSaveSuccess()) {
            loadDocGiaData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin độc giả thành công!");
        }
    }
    private void xoaDocGia() {
        // 1. Kiểm tra chọn hàng (Giữ nguyên)
        int selectedRow = docGiaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một độc giả để xóa.", 
                "Chưa chọn độc giả", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy thông tin (Giữ nguyên)
        String maDocGia = docGiaTableModel.getValueAt(selectedRow, 0).toString();
        String hoTen = docGiaTableModel.getValueAt(selectedRow, 1).toString();

        // 3. (BỎ KIỂM TRA ĐÃ KHÓA)

        // 4. Hiển thị hộp thoại xác nhận (ĐÃ SỬA)
        // (Giống hệt xoaSach)
        String confirmMsg = "Bạn có chắc chắn muốn XÓA (LƯU TRỮ) độc giả:\n"
                          + hoTen + " (Mã: " + maDocGia + ")\n\n"
                          + "Độc giả này sẽ bị ẩn đi, không xóa vĩnh viễn.";
                          
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "Xác nhận Xóa Mềm", // <<< SỬA
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        // 5. Xử lý nếu đồng ý (ĐÃ SỬA)
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi hàm XÓA MỀM (isArchived = 1) MỚI
            boolean success = docGiaDAO.softDeleteDocGia(maDocGia);
            
            if (success) {
                // Tải lại bảng Độc Giả (độc giả sẽ biến mất)
                loadDocGiaData(); 
                JOptionPane.showMessageDialog(this, "Đã xóa (lưu trữ) độc giả thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa (lưu trữ) độc giả thất bại (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void toggleBlockDocGia() {
        // 1. Kiểm tra xem người dùng đã chọn hàng nào chưa
        int selectedRow = docGiaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một độc giả để thay đổi trạng thái.", 
                "Chưa chọn độc giả", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Lấy thông tin độc giả từ JTable
        String maDocGia = docGiaTableModel.getValueAt(selectedRow, 0).toString();
        String hoTen = docGiaTableModel.getValueAt(selectedRow, 1).toString();
        String trangThaiHienTai = docGiaTableModel.getValueAt(selectedRow, 6).toString(); //

        // 3. Xác định hành động (toggle logic)
        boolean isCurrentlyBlocked = trangThaiHienTai.equals("Đã khóa");
        boolean newState = !isCurrentlyBlocked; // Đảo ngược trạng thái
        String actionVerb = isCurrentlyBlocked ? "MỞ KHÓA" : "KHÓA";

        // 4. Hiển thị hộp thoại xác nhận
        String confirmMsg = "Bạn có chắc chắn muốn " + actionVerb + " độc giả:\n"
                          + hoTen + " (Mã: " + maDocGia + ")?";
                          
        int choice = JOptionPane.showConfirmDialog(
            this, 
            confirmMsg, 
            "Xác nhận " + actionVerb, 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        // 5. Xử lý nếu người dùng đồng ý (chọn Yes)
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi DAO để cập nhật cột 'blocked'
            boolean success = docGiaDAO.setBlockStatus(maDocGia, newState);
            
            if (success) {
                // Tải lại bảng để cập nhật cột Trạng Thái
                loadDocGiaData(); 
                JOptionPane.showMessageDialog(this, "Đã " + actionVerb.toLowerCase() + " độc giả thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại (Lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void clearFormNguoiMuon() {
        txtTenDGMuon.setText("");
        txtEmailDGMuon.setText("");
        txtSdtDGMuon.setText("");
        txtTrangThaiDGMuon.setText("");
        txtSoSachDaMuon.setText("0");
        lblAnhDocGiaMuon.setIcon(null);
        lblAnhDocGiaMuon.setText("(Chưa có ảnh)");
        txtMaDGMuon.setText(""); // <<< THÊM
        
        this.docGiaHienTai = null; // <<< THÊM
    }
    private void loadImageForMuon(String imagePath) {
        // Đặt kích thước ảnh mong muốn
        int imgWidth = 200;
        int imgHeight = 200; // Bạn có thể đổi kích thước này

        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
                lblAnhDocGiaMuon.setIcon(new ImageIcon(img));
                lblAnhDocGiaMuon.setText(null); // Xóa văn bản
            } catch (Exception e) {
                e.printStackTrace();
                lblAnhDocGiaMuon.setIcon(null);
                lblAnhDocGiaMuon.setText("Lỗi tải ảnh");
            }
        } else {
            lblAnhDocGiaMuon.setIcon(null);
            lblAnhDocGiaMuon.setText("(Chưa có ảnh)");
        }
    }
    
    /**
     * HÀM MỚI: Thực hiện logic lọc (sẽ được hoàn thiện sau)
     */
    private void locSachData() {
        // 1. Lấy chế độ và giá trị
        String mode = cbSachSearchMode.getSelectedItem().toString();
        String value = sachSearchField.getText().trim();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị cần tìm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            sachSearchField.requestFocus();
            return;
        }

        // 2. Validate cho Năm xuất bản (phải là số)
        if (mode.equals("Năm xuất bản")) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Năm xuất bản phải là một con số.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
                sachSearchField.requestFocus();
                return;
            }
        }
        
        // 3. Gọi DAO với mode và value
        List<Sach> danhSach = sachDAO.searchSach(mode, value);

        // 4. Hiển thị kết quả (Hàm renderSachTable đã có)
        renderSachTable(danhSach);
        
        // 5. Thông báo kết quả
        if (danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào khớp với điều kiện.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void renderSachTable(List<Sach> danhSach) {
        // Xóa dữ liệu cũ
        sachTableModel.setRowCount(0);

        // Định dạng ngày
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Sach s : danhSach) {
            String conLaiStr = (s.getConLai() > 0) ? String.valueOf(s.getConLai()) : "Hết";
            String tacGiaDisplay = s.getTenTacGiaDisplay();
            Object[] row = new Object[] {
                s.getMaSach(),
                s.getTenSach(),
                tacGiaDisplay,
                s.getNhaXuatBan(),
                s.getNamXuatBan() == 0 ? "N/A" : s.getNamXuatBan(),
                s.getSoLuong(),
                conLaiStr,
                s.getViTri(),
                s.getNgayThem() != null ? sdf.format(s.getNgayThem()) : "N/A"
            };
            sachTableModel.addRow(row);
        }
    }
    private void updateSoSachDaChonCount() {
        // Đếm số hàng trong bảng "Sách đã chọn"
        int count = sachDaChonTableModel.getRowCount();
        
        // Cập nhật ô text 'txtSoSachDaMuon'
        // (Ô text này đã được khởi tạo trong hàm createPhieuMuonLeftPanel)
        if (txtSoSachDaMuon != null) {
            txtSoSachDaMuon.setText(String.valueOf(count));
        }
    }
    private void loadMuonTraData() {
        // Lấy danh sách TẤT CẢ từ DAO
        List<MuonTra> danhSach = muonTraDAO.getAllMuonTra();

        // Gọi hàm render mới
        renderMuonTraTable(danhSach);
    }
    private void renderMuonTraTable(List<MuonTra> danhSach) {
        // Xóa dữ liệu cũ
        muonTraTableModel.setRowCount(0);

        // Lưu lại danh sách hiện tại (QUAN TRỌNG)
        this.currentMuonTraList = danhSach;
        
        if (danhSach.isEmpty()) {
            return; // Không làm gì nếu danh sách rỗng
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (MuonTra mt : this.currentMuonTraList) {
            
            String ngayTraStr = (mt.getNgayTraThucTe() != null) 
                               ? sdf.format(mt.getNgayTraThucTe()) 
                               : "N/A";
            
            Object[] row = new Object[] {
                mt.getMaMuonTra(),
                mt.getDocGia().getHoTen(), 
                mt.getSach().getTenSach(), 
                sdf.format(mt.getNgayMuon()),
                sdf.format(mt.getNgayHenTra()),
                ngayTraStr,
                mt.getTrangThai(), // Tự động tính "Đang mượn", "Quá hạn", "Đã trả"
                mt.getLoaiMuon()
            };
            muonTraTableModel.addRow(row);
        }
    }
    private void locSachDenHanHomNay() {
        // 1. Lấy dữ liệu
        List<MuonTra> danhSach = muonTraDAO.getDanhSachSachDenHanHomNay();
        
        // 2. Hiển thị lên bảng (hàm này đã có)
        renderMuonTraTable(danhSach); //
        
        // 3. Tự động chuyển tab (bên trong tab Mượn/Trả)
        if (this.muonTraTabbedPane != null) {
            // Chuyển về tab "Danh sách Mượn Trả" (index = 0)
            this.muonTraTabbedPane.setSelectedIndex(0); //
        }
    }
    /**
     * HÀM MỚI (Helper): Hiển thị danh sách BST lên JList
     */
    private void renderBoSuuTapList(List<BoSuuTap> danhSach) {
        if (boSuuTapListModel == null) {
            boSuuTapListModel = (DefaultListModel<BoSuuTap>) boSuuTapList.getModel();
        }
        
        boSuuTapListModel.clear();
        
        for (BoSuuTap bst : danhSach) {
            boSuuTapListModel.addElement(bst);
        }
    }

    /**
     * HÀM CŨ (Đã sửa): Tải TẤT CẢ Bộ Sưu Tập
     */
    private void loadBoSuuTapListData() {
        List<BoSuuTap> danhSach = boSuuTapDAO.getAllBoSuuTap();
        renderBoSuuTapList(danhSach);
    }
    private void loadSachTrongBST(BoSuuTap bst) {
        // Xóa bảng
        sachTrongBSTTableModel.setRowCount(0);
        
        if (bst == null) return;
        
        // Tải sách cho BST đó
        List<Sach> sachList = boSuuTapDAO.getSachByBoSuuTap(bst.getMaBoSuuTap());
        
        // Hiển thị lên JTable
        // Cột: {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"}
        for (Sach s : sachList) {
            sachTrongBSTTableModel.addRow(new Object[]{
                s.getMaSach(),
                s.getTenSach(),
                s.getTenTacGiaDisplay(), // Dùng hàm helper
                s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A"
            });
        }
    }
    private JPanel createDashboardWidget(String title, String value, Color bgColor, Color borderColor, Color valueColor, final String actionCommand) {
        JPanel widgetPanel = new JPanel(new BorderLayout());
        widgetPanel.setBackground(bgColor);
        widgetPanel.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        widgetPanel.setPreferredSize(new Dimension(220, 100)); // Kích thước cố định

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblCount = new JLabel(value);
        lblCount.setFont(new Font("Arial", Font.BOLD, 48));
        lblCount.setForeground(valueColor);
        lblCount.setHorizontalAlignment(SwingConstants.CENTER);
        
        widgetPanel.add(lblTitle, BorderLayout.NORTH);
        widgetPanel.add(lblCount, BorderLayout.CENTER);
        
        // Thêm sự kiện Click
        widgetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Gọi hàm xử lý click
                handleDashboardClick(actionCommand);
            }
        });
        
        return widgetPanel;
    }
    
    /**
     * HÀM HELPER MỚI: Xử lý sự kiện khi nhấn vào widget
     */
    private void handleDashboardClick(String action) {
        switch (action) {
            case "DEN_HAN":
                // 1. Chuyển tab
                showPanel("Quản lý Mượn/Trả");
                // 2. Chuyển tab con
                if (this.muonTraTabbedPane != null) {
                    this.muonTraTabbedPane.setSelectedIndex(0);
                }
                // 3. Lấy dữ liệu và hiển thị
                List<MuonTra> dsDenHan = muonTraDAO.getDanhSachSachDenHanHomNay();
                renderMuonTraTable(dsDenHan);
                break;
                
            case "QUA_HAN":
                showPanel("Quản lý Mượn/Trả");
                if (this.muonTraTabbedPane != null) {
                    this.muonTraTabbedPane.setSelectedIndex(0);
                }
                // Tìm kiếm
                List<MuonTra> dsQuaHan = muonTraDAO.searchMuonTra("Trạng thái", "Quá hạn");
                renderMuonTraTable(dsQuaHan);
                // Cập nhật UI tìm kiếm
                cbMuonTraSearchMode.setSelectedItem("Trạng thái");
                txtMuonTraSearchValue.setText("Quá hạn");
                break;
                
            case "HET_HANG":
                showPanel("Quản lý Sách");
                // Tìm kiếm
                List<Sach> dsHetHang = sachDAO.searchSach("Trạng thái", "Hết");
                renderSachTable(dsHetHang);
                // Cập nhật UI tìm kiếm
                cbSachSearchMode.setSelectedItem("Trạng thái");
                sachSearchField.setText("Hết sách");
                break;
                
            case "KHOA":
                showPanel("Quản lý Độc giả");
                // Tìm kiếm
                List<DocGia> dsKhoa = docGiaDAO.searchDocGia("Trạng thái", "Đã khóa");
                renderDocGiaTable(dsKhoa);
                // Cập nhật UI tìm kiếm
                cbDocGiaSearchMode.setSelectedItem("Trạng thái");
                docGiaSearchField.setText("Đã khóa");
                break;
        }
    }
    private void loadDynamicImage(JLabel label, String imagePath, int width, int height, String defaultText) {
        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
                label.setText(null); 
            } catch (Exception e) {
                e.printStackTrace();
                label.setIcon(null);
                label.setText("Lỗi ảnh");
            }
        } else {
            label.setIcon(null);
            label.setText(defaultText);
        }
    }

    /**
     * HÀM HELPER MỚI 2: Tạo một "Ô" (Cell) cho lưới Bộ Sưu Tập
     */
    private JPanel createCollectionCell(BoSuuTap bst) {
        JPanel cellPanel = new JPanel(new BorderLayout(5, 5));
        cellPanel.setBorder(BorderFactory.createEtchedBorder());
        cellPanel.setBackground(Color.WHITE);
        cellPanel.setPreferredSize(new Dimension(180, 190)); // Kích thước của 1 ô
        
        // 1. Ảnh (ở giữa)
        JLabel lblAnh = new JLabel();
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(140, 140));
        loadDynamicImage(lblAnh, bst.getDuongDanAnh(), 140, 140, "(Không có ảnh)");
        cellPanel.add(lblAnh, BorderLayout.CENTER);

        // 2. Tên (ở dưới)
        JLabel lblTen = new JLabel(bst.getTenBoSuuTap());
        lblTen.setFont(new Font("Arial", Font.BOLD, 12));
        lblTen.setHorizontalAlignment(SwingConstants.CENTER);
        lblTen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cellPanel.add(lblTen, BorderLayout.SOUTH);

        // ==================================================
        // <<< SỬA LẠI LOGIC MOUSELISTENER >>>
        // ==================================================
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Gọi hàm helper để tải lưới sách
                showSachGridForBST(bst);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                cellPanel.setBackground(new Color(240, 240, 240)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cellPanel.setBackground(Color.WHITE);
            }
        });

        return cellPanel;
    }
    // Hàm main
    private JPanel createSachCell(Sach sach) {
        JPanel cellPanel = new JPanel(new BorderLayout(5, 5));
        cellPanel.setBorder(BorderFactory.createEtchedBorder());
        cellPanel.setBackground(Color.WHITE);
        cellPanel.setPreferredSize(new Dimension(150, 180)); 
        cellPanel.setToolTipText(sach.getTenSach());

        // 1. Ảnh (ở giữa)
        JLabel lblAnh = new JLabel();
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(140, 140));
        // Gọi hàm tải ảnh (đã có từ trước)
        loadDynamicImage(lblAnh, sach.getDuongDanAnh(), 140, 140, "(Không có ảnh)");
        cellPanel.add(lblAnh, BorderLayout.CENTER);

        // 2. Tên (ở dưới)
        JLabel lblTen = new JLabel(sach.getTenSach());
        lblTen.setFont(new Font("Arial", Font.BOLD, 12));
        lblTen.setHorizontalAlignment(SwingConstants.CENTER);
        lblTen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cellPanel.add(lblTen, BorderLayout.SOUTH);

        // (Bạn có thể thêm MouseListener ở đây nếu muốn nhấn vào sách)
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Gọi hàm helper mới với mã sách của ô này
                switchToBookManagementAndSelect(sach.getMaSach());
            }

            // (Các hiệu ứng hover để người dùng biết là có thể click)
            @Override
            public void mouseEntered(MouseEvent e) {
                cellPanel.setBackground(new Color(240, 240, 240)); 
                cellPanel.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255))); // Viền xanh
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cellPanel.setBackground(Color.WHITE);
                cellPanel.setBorder(BorderFactory.createEtchedBorder()); // Viền xám cũ
            }
        });
        return cellPanel;
    }

    /**
     * HÀM HELPER MỚI: Tải và hiển thị lưới Sách cho một BST
     */
    private void showSachGridForBST(BoSuuTap bst) {
        // 1. Đặt tiêu đề
        tqSachGridTitle.setText("Các sách trong: " + bst.getTenBoSuuTap());
        
        // 2. Xóa sách cũ
        tqSachGridPanel.removeAll();
        
        // 3. Tải dữ liệu sách
        List<Sach> sachList = boSuuTapDAO.getSachByBoSuuTap(bst.getMaBoSuuTap());
        
        // 4. Đổ sách vào lưới
        if (sachList.isEmpty()) {
            tqSachGridPanel.add(new JLabel("Bộ sưu tập này chưa có sách."));
        } else {
            for (Sach s : sachList) {
                JPanel cell = createSachCell(s);
                tqSachGridPanel.add(cell);
            }
        }
        
        tqSachGridPanel.revalidate();
        tqSachGridPanel.repaint();
        
        // 5. Lật CardLayout (của tab Tổng quan)
        tongQuanCardLayout.show(tongQuanMainCardPanel, "GRID_SACH");
    }
    private JPanel createBaoCaoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- 1. Panel BỘ LỌC (NORTH) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Chọn khoảng thời gian"));
        
        filterPanel.add(new JLabel("Từ ngày:"));
        dateFromChooser = new JDateChooser();
        dateFromChooser.setPreferredSize(new Dimension(120, 25));
        dateFromChooser.setDate(new Date()); // Mặc định là hôm nay
        filterPanel.add(dateFromChooser);
        
        filterPanel.add(new JLabel("Đến ngày:"));
        dateToChooser = new JDateChooser();
        dateToChooser.setPreferredSize(new Dimension(120, 25));
        dateToChooser.setDate(new Date()); // Mặc định là hôm nay
        filterPanel.add(dateToChooser);
        
        JButton btnXemBaoCao = new JButton("Xem Báo cáo");
        btnXemBaoCao.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(btnXemBaoCao);
        
        panel.add(filterPanel, BorderLayout.NORTH);

        // --- 2. JSplitPane (CENTER) ---
        // (Chứa 2 bảng Top)
        
        // 2.A: Bảng Top Sách (Bên trái)
        JTable topSachTable = new JTable(topSachTableModel);
        topSachTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollSach = new JScrollPane(topSachTable);
        scrollSach.setBorder(BorderFactory.createTitledBorder(
            "Top 10 Sách được mượn nhiều nhất"
        ));
        
        // 2.B: Bảng Top Độc Giả (Bên phải)
        JTable topDocGiaTable = new JTable(topDocGiaTableModel);
        topDocGiaTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollDocGia = new JScrollPane(topDocGiaTable);
        scrollDocGia.setBorder(BorderFactory.createTitledBorder(
            "Top 10 Độc giả mượn nhiều nhất"
        ));

        // 2.C: Ghép 2 bảng
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                scrollSach, scrollDocGia);
        splitPane.setDividerLocation(0.5); // Chia đôi
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // --- 3. SỰ KIỆN NÚT BẤM ---
        btnXemBaoCao.addActionListener(e -> {
            Date from = dateFromChooser.getDate();
            Date to = dateToChooser.getDate();

            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn cả ngày bắt đầu và kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (from.after(to)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tải dữ liệu Top Sách
            List<Object[]> topSachData = muonTraDAO.getTopSachMuon(from, to);
            topSachTableModel.setRowCount(0); // Xóa bảng cũ
            int rank = 1;
            for (Object[] row : topSachData) {
                topSachTableModel.addRow(new Object[]{rank++, row[0], row[1]});
            }

            // Tải dữ liệu Top Độc Giả
            List<Object[]> topDocGiaData = muonTraDAO.getTopDocGia(from, to);
            topDocGiaTableModel.setRowCount(0); // Xóa bảng cũ
            rank = 1;
            for (Object[] row : topDocGiaData) {
                topDocGiaTableModel.addRow(new Object[]{rank++, row[0], row[1]});
            }
            
            if (topSachData.isEmpty() && topDocGiaData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu mượn trả nào trong khoảng thời gian này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return panel;
    }
    private void showChiTietSachDialog(String maSach) {
        // Đảm bảo DAO đã được khởi tạo
        if (sachDAO == null) {
            sachDAO = new SachDAO(); 
        }
        
        // Lấy thông tin sách đầy đủ (bao gồm đường dẫn ảnh)
        Sach sach = sachDAO.getSachByMa(maSach);
        
        if (sach != null) {
            // Tạo và hiển thị dialog mới (từ Bước 1)
            ChiTietSachDialog dialog = new ChiTietSachDialog(this, sach);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tải chi tiết sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void switchToBookManagementAndSelect(String maSachToSelect) {
        if (maSachToSelect == null || maSachToSelect.isEmpty()) {
            return;
        }

        // 1. Chuyển sang panel "Quản lý Sách".
        // Hành động này sẽ tự động gọi 'createBookManagementPanel()'
        // (nếu chưa có) và 'loadSachData()', đảm bảo
        // 'sachTable' và 'sachTableModel' đã được khởi tạo và nạp dữ liệu.
        showPanel("Quản lý Sách");

        // 2. Lấy model của bảng sách
        DefaultTableModel model = (DefaultTableModel) sachTable.getModel();

        // 3. Lặp qua từng hàng trong bảng để tìm mã sách
        for (int i = 0; i < model.getRowCount(); i++) {
            
            // Lấy giá trị mã sách từ Cột 0
            String maSachTrongBang = model.getValueAt(i, 0).toString();

            // 4. So sánh
            if (maSachToSelect.equals(maSachTrongBang)) {
                
                // 5. TÌM THẤY! Chọn (highlight) hàng đó
                sachTable.setRowSelectionInterval(i, i);
                
                // 6. (Quan trọng) Cuộn JScrollPane để hàng được chọn
                //    nằm trong tầm nhìn của người dùng
                sachTable.scrollRectToVisible(sachTable.getCellRect(i, 0, true));
                
                // 7. Thoát vòng lặp vì đã tìm thấy
                break;
            }
        }
    }
//    public static void main(String[] args) {
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
//            System.out.println("Không thể thiết lập Nimbus L&F. Sử dụng mặc định.");
//        }
//        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
//    }

}

