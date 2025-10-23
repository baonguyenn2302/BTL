package QuanLyThuVien;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.border.Border;
import java.nio.file.Files;
import java.nio.file.Path; // Import Path
import java.nio.file.Paths; // Import Paths
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
// Thêm import cho ListCellRenderer và EmptyBorder
import javax.swing.border.EmptyBorder;


public class LibraryHomePageView extends JFrame {

    private SachDAO sachDAO;

    // Các panel chính
    private JPanel mainContentPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JComponent galleryView; // Gallery ban đầu

    // Biến cho tìm kiếm
    private JTextField txtSearch;
    private JComboBox<String> cmbSearchType;

    // --- Hằng số cho số lượng item hiển thị ---
    private static final int ITEMS_PER_GALLERY = 9; // Số sách trong gallery
    private static final int ITEMS_PER_AUTHOR_LIST = 10; // Số sách trong list tác giả

    public LibraryHomePageView() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - TRANG CHỦ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sachDAO = new SachDAO();

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        leftPanel = createLeftPanel(); // Đã thêm action listener cho tác giả
        rightPanel = createRightPanel();
        galleryView = createGalleryTabs();

        mainContentPanel.add(leftPanel, BorderLayout.WEST);
        mainContentPanel.add(rightPanel, BorderLayout.EAST);
        mainContentPanel.add(galleryView, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Phương thức 1: Tạo Header (Giữ nguyên) ---
    private JPanel createHeaderPanel() { /* ... Giữ nguyên ... */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel logo = new JLabel("  [LOGO PTIT]", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 24)); logo.setPreferredSize(new Dimension(200, 50));
        topBar.add(logo, BorderLayout.WEST);
        JPanel navMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        String[] menuItems = {"TRANG CHỦ", "DUYỆT THEO", "TRỢ GIÚP", "LIÊN HỆ"};
        for (String item : menuItems) {
            JButton btn = new JButton(item); btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
            navMenu.add(btn);
        }
        topBar.add(navMenu, BorderLayout.CENTER);
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> { LoginForm loginForm = new LoginForm(); loginForm.setVisible(true); });
        topBar.add(btnLogin, BorderLayout.EAST);
        header.add(topBar, BorderLayout.NORTH);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(new Color(240, 240, 240));
        String[] searchOptions = {"Tất cả", "Nhan đề", "Tác giả", "Chủ đề", "Năm xuất bản"};
        cmbSearchType = new JComboBox<>(searchOptions);
        txtSearch = new JTextField(40);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(200, 0, 0)); btnSearch.setForeground(Color.WHITE); btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> xuLyTimKiem());
        searchBar.add(cmbSearchType); searchBar.add(txtSearch); searchBar.add(btnSearch);
        header.add(searchBar, BorderLayout.CENTER);
        return header;
     }

    // --- Phương thức 2: Tạo các Panel cho Nội dung Chính ---

    // Tạo Cột Trái (WEST) - Đã thêm action listener
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(250, 700));

        // Phần Duyệt theo (Không đổi)
        String[] browseItems = {"Đơn vị & Bộ sưu tập", "Năm xuất bản", "Tác giả", "Nhan đề", "Chủ đề"};
        panel.add(createSidebarSection("Duyệt theo", browseItems, new Color(220, 0, 0), null)); // Không cần listener
        panel.add(Box.createVerticalStrut(15));

        // Phần Hồ sơ tác giả (Thêm listener)
        List<Sach> allBooks = sachDAO.getAllSach();
        Map<String, Long> authorCounts = allBooks.stream()
                .filter(s -> s.getTacGia() != null && !s.getTacGia().isEmpty())
                .collect(Collectors.groupingBy(Sach::getTacGia, Collectors.counting()));

        // Chuẩn bị dữ liệu cho createSidebarSection
        String[] authorDisplayItems = authorCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                .toArray(String[]::new);
        String[] authorNamesOnly = authorCounts.keySet().stream().sorted().toArray(String[]::new); // Lấy tên thật đã sắp xếp

        // Tạo section Hồ sơ tác giả và truyền listener
        panel.add(createSidebarSection("Hồ sơ tác giả", authorDisplayItems, new Color(220, 0, 0), authorNamesOnly));

        return panel;
    }

    // Tạo Cột Phải (EAST) - Không đổi
    private JPanel createRightPanel() { /* ... Giữ nguyên ... */
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(250, 700));
        panel.add(createSimpleLinkButton("Hướng dẫn tìm kiếm"));
        panel.add(createSimpleLinkButton("Hòm thư góp ý"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createDatabaseLinkSection());
        return panel;
    }
    // Tạo Giao diện Gallery (JTabbedPane) - Đã cập nhật gọi DAO
    private JComponent createGalleryTabs() {
        JTabbedPane documentTabs = new JTabbedPane();
        documentTabs.setFont(new Font("Arial", Font.BOLD, 14));

        // Gọi các hàm DAO mới (giới hạn số lượng)
        List<Sach> newBooks = sachDAO.getSachMoiNhat(ITEMS_PER_GALLERY);
        List<Sach> viewedBooks = sachDAO.getSachXemNhieuNhat(ITEMS_PER_GALLERY);
        List<Sach> downloadedBooks = sachDAO.getSachTaiNhieuNhat(ITEMS_PER_GALLERY);

        documentTabs.addTab("Tài liệu mới cập nhập", createDocumentGalleryPanel(newBooks));
        documentTabs.addTab("Xem nhiều nhất", createDocumentGalleryPanel(viewedBooks));
        documentTabs.addTab("Download nhiều nhất", createDocumentGalleryPanel(downloadedBooks));

        return documentTabs;
     }

    // --- Các Phương thức tiện ích (Sidebar, DB Links) ---

    // Đã cập nhật createSidebarSection để nhận listener
    private JPanel createSidebarSection(String title, String[] itemsToDisplay, Color titleColor, String[] actionCommands) {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title); /* ... style title ... */
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true); titleLabel.setBackground(titleColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleLabel.getPreferredSize().height));
        panel.add(titleLabel);

        Border buttonBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(10, 5, 10, 5));

        for (int i = 0; i < itemsToDisplay.length; i++) {
            JButton btn = new JButton(itemsToDisplay[i]);
            btn.setFocusPainted(false); btn.setBorder(buttonBorder);
            btn.setBackground(new Color(240, 240, 240)); btn.setForeground(Color.BLUE.darker());
            btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));

            // === THÊM ACTION LISTENER NẾU CÓ ===
            if (actionCommands != null && i < actionCommands.length) {
                final String authorName = actionCommands[i]; // Lấy tên tác giả thật
                btn.addActionListener(e -> showAuthorProfileView(authorName)); // Gọi hàm hiển thị hồ sơ
            }
            // ===================================

            panel.add(btn);
        }
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); return panel;
    }
    private JButton createSimpleLinkButton(String text) { /* ... Giữ nguyên ... */
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setFocusPainted(false);
        btn.setBackground(new Color(240, 240, 240));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height)); return btn;
    }
    private JPanel createDatabaseLinkSection() { /* ... Giữ nguyên ... */
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Cơ sở dữ liệu trực tuyến"));
        String[] dbNames = {"ProQuest", "IG Publishing", "SSAGE journals"};
        for (String name : dbNames) {
            JLabel dbLabel = new JLabel("[LOGO] " + name, SwingConstants.CENTER);
            dbLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); dbLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dbLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); dbLabel.setMinimumSize(new Dimension(0, 50));
            panel.add(dbLabel); panel.add(Box.createVerticalStrut(5));
        }
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); return panel;
    }


    // =========================================================================
    // === CÁC PHƯƠNG THỨC ĐỂ CHUYỂN ĐỔI GIAO DIỆN VÀ CHI TIẾT SÁCH ===
    // =========================================================================
    private void showGalleryView() { /* ... Giữ nguyên ... */
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) mainContentPanel.remove(centerComponent);
        mainContentPanel.add(galleryView, BorderLayout.CENTER);
        mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    /**
     * === ĐÃ CẬP NHẬT: Gọi tangLuotXem() ===
     * Hiển thị giao diện Chi tiết Sách và tăng lượt xem.
     */
    private void showDetailView(Sach sach) {
        // --- TĂNG LƯỢT XEM ---
        sachDAO.tangLuotXem(sach.getMaSach());
        // Lấy lại sách để có lượt xem mới nhất (tùy chọn)
        Sach updatedSach = sachDAO.getSachByMaSach(sach.getMaSach());
        if (updatedSach == null) updatedSach = sach; // Dùng sách cũ nếu lỗi
        // ---------------------

        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) mainContentPanel.remove(centerComponent);
        // Tạo panel chi tiết với sách đã cập nhật
        JPanel detailPanel = createSachDetailPanel(updatedSach);
        mainContentPanel.add(detailPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    // Tạo Panel Chi tiết Sách (Giữ nguyên)
    private JPanel createSachDetailPanel(Sach sach) { /* ... Giữ nguyên ... */
        JPanel detailContentPanel = new JPanel(new BorderLayout(15, 15));
        detailContentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnBack = new JButton("< Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12)); btnBack.setFocusPainted(false);
        // Sửa action quay lại cho phù hợp (quay lại gallery hoặc hồ sơ tác giả?)
        // Tạm thời quay lại gallery
        btnBack.addActionListener(e -> showGalleryView());
        topPanel.add(btnBack, BorderLayout.WEST);
        detailContentPanel.add(topPanel, BorderLayout.NORTH);
        JPanel centerWrapper = new JPanel(new BorderLayout(15, 15));
        JPanel coverPanel = new JPanel(new BorderLayout()); coverPanel.setPreferredSize(new Dimension(250, 350));
        JLabel lblAnhBia = new JLabel(); lblAnhBia.setHorizontalAlignment(SwingConstants.CENTER); lblAnhBia.setVerticalAlignment(SwingConstants.CENTER);
        String duongDanAnh = sach.getDuongDanAnh();
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            try {
                File imgFile = new File(duongDanAnh); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), duongDanAnh);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image image = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH);
                    lblAnhBia.setIcon(new ImageIcon(image));
                } else { lblAnhBia.setText("Ảnh lỗi (" + duongDanAnh + ")"); }
            } catch (Exception e) { lblAnhBia.setText("[Lỗi ảnh]"); e.printStackTrace(); }
        } else { lblAnhBia.setText("[Không có ảnh bìa]"); }
        coverPanel.add(lblAnhBia, BorderLayout.CENTER); centerWrapper.add(coverPanel, BorderLayout.WEST);
        JPanel infoPanel = new JPanel(); infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel lblTenSach = new JLabel(sach.getTenSach()); lblTenSach.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTenSach.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(lblTenSach); infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(createInfoRow("Tác giả:", sach.getTacGia())); infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Nhà xuất bản:", sach.getNhaXuatBan())); infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Năm xuất bản:", (sach.getNamXuatBan() > 0) ? String.valueOf(sach.getNamXuatBan()) : "N/A")); infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Số lượng còn:", String.valueOf(sach.getSoLuong()))); infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Mã sách:", sach.getMaSach()));
        // Hiển thị lượt xem/tải ở đây (tùy chọn)
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Lượt xem:", String.valueOf(sach.getLuotXem())));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createInfoRow("Lượt tải:", String.valueOf(sach.getLuotTai())));
        infoPanel.add(Box.createVerticalStrut(15));

        JLabel lblTomTatTitle = new JLabel("Tóm tắt:"); lblTomTatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTomTatTitle.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(lblTomTatTitle);
        JTextArea txtMoTa = new JTextArea(sach.getMoTa()); txtMoTa.setEditable(false); txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true); txtMoTa.setBackground(infoPanel.getBackground());
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa); scrollMoTa.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollMoTa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); infoPanel.add(scrollMoTa);
        centerWrapper.add(infoPanel, BorderLayout.CENTER); detailContentPanel.add(centerWrapper, BorderLayout.CENTER);
        String duongDanXemTruoc = sach.getDuongDanXemTruoc();
        if (duongDanXemTruoc != null && !duongDanXemTruoc.isEmpty()) {
            JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            previewPanel.setBorder(BorderFactory.createTitledBorder("Tệp đọc thử"));
            File previewFile = new File(duongDanXemTruoc);
            JLabel lblFileName = new JLabel(previewFile.getName());
            JButton btnOpenPreview = new JButton("📖 Mở đọc thử"); JButton btnDownloadPreview = new JButton("💾 Tải xuống");
            btnOpenPreview.addActionListener(e -> moFileXemTruoc(previewFile));
            // Truyền mã sách vào hàm tải
            btnDownloadPreview.addActionListener(e -> taiFileXemTruoc(previewFile, sach.getMaSach()));
            previewPanel.add(lblFileName); previewPanel.add(btnOpenPreview); previewPanel.add(btnDownloadPreview);
            detailContentPanel.add(previewPanel, BorderLayout.SOUTH);
        }
        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(detailContentPanel), BorderLayout.CENTER); }};
     }
    // Hàm createInfoRow (Giữ nguyên)
    private JPanel createInfoRow(String labelText, String valueText) { /* ... Giữ nguyên ... */
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText); label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(120, 20));
        JLabel value = new JLabel( (valueText != null && !valueText.isEmpty() && !valueText.equals("0")) ? valueText : "N/A");
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rowPanel.add(label); rowPanel.add(value);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height)); return rowPanel;
    }
    // Hàm moFileXemTruoc (Giữ nguyên)
    private void moFileXemTruoc(File fileToOpen) { /* ... Giữ nguyên ... */
         if (!fileToOpen.exists()) {
             File absoluteFile = new File(System.getProperty("user.dir"), fileToOpen.getPath());
             if (!absoluteFile.exists()) {
                 JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại.\nKiểm tra đường dẫn: " + fileToOpen.getPath(), "Không tìm thấy file", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             fileToOpen = absoluteFile;
        }
        if (Desktop.isDesktopSupported()) {
            try { Desktop.getDesktop().open(fileToOpen); }
            catch (IOException | SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Không thể mở file đọc thử.\nLỗi: " + ex.getMessage(), "Lỗi mở file", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else { JOptionPane.showMessageDialog(this, "Tính năng mở file không được hỗ trợ.", "Lỗi hệ thống", JOptionPane.WARNING_MESSAGE); }
    }

    /**
     * === ĐÃ CẬP NHẬT: Gọi tangLuotTai() ===
     * Mở JFileChooser để tải file xem trước và tăng lượt tải.
     */
    private void taiFileXemTruoc(File fileToDownload, String maSach) { // Nhận thêm mã sách
         if (!fileToDownload.exists()) {
             File absoluteFile = new File(System.getProperty("user.dir"), fileToDownload.getPath());
             if (!absoluteFile.exists()) {
                 JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại.", "Không tìm thấy file", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             fileToDownload = absoluteFile;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file đọc thử");
        fileChooser.setSelectedFile(new File(fileToDownload.getName()));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                // Copy file
                Files.copy(fileToDownload.toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // --- TĂNG LƯỢT TẢI ---
                sachDAO.tangLuotTai(maSach);
                // --------------------

                JOptionPane.showMessageDialog(this, "Tải file thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Hàm createDocumentGalleryPanel (Giữ nguyên)
    private JScrollPane createDocumentGalleryPanel(List<Sach> bookList) { /* ... Giữ nguyên ... */
        if (bookList == null || bookList.isEmpty()) { JPanel emptyPanel = new JPanel(new GridBagLayout()); emptyPanel.add(new JLabel("Không có sách nào.")); return new JScrollPane(emptyPanel); } JPanel galleryPanel = new JPanel(new GridLayout(0, 3, 10, 10)); galleryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); for (Sach sach : bookList) { JPanel bookPanel = new JPanel(new BorderLayout(5, 5)); bookPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); bookPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); bookPanel.putClientProperty("sachObject", sach); JLabel coverLabel = new JLabel(); coverLabel.setPreferredSize(new Dimension(150, 200)); coverLabel.setHorizontalAlignment(SwingConstants.CENTER); coverLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); try { String imagePath = sach.getDuongDanAnh(); if (imagePath != null && !imagePath.isEmpty()) { File imgFile = new File(imagePath); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), imagePath); if (imgFile.exists()) { ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH); coverLabel.setIcon(new ImageIcon(img)); } else { coverLabel.setText("[Ảnh lỗi]"); } } else { coverLabel.setText("[No Cover]"); } } catch (Exception e) { coverLabel.setText("[Lỗi ảnh]"); e.printStackTrace(); } bookPanel.add(coverLabel, BorderLayout.CENTER); JLabel nameLabel = new JLabel(sach.getTenSach()); nameLabel.setHorizontalAlignment(SwingConstants.CENTER); nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); bookPanel.add(nameLabel, BorderLayout.SOUTH); bookPanel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { Sach clickedSach = (Sach) ((JPanel)e.getSource()).getClientProperty("sachObject"); if (clickedSach != null) showDetailView(clickedSach); } }); galleryPanel.add(bookPanel); } JScrollPane scrollPane = new JScrollPane(galleryPanel); return scrollPane;
    }


    // =========================================================================
    // === PHƯƠNG THỨC XỬ LÝ TÌM KIẾM (Giữ nguyên) ===
    // =========================================================================
    private void xuLyTimKiem() { /* ... Giữ nguyên ... */
        String keyword = txtSearch.getText().trim(); String searchType = (String) cmbSearchType.getSelectedItem(); if (keyword.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; } System.out.println("Tìm kiếm: '" + keyword + "' theo '" + searchType + "'"); List<Sach> searchResults = sachDAO.timKiemSachNangCao(keyword, searchType); showSearchResultsView(searchResults);
    }
    private void showSearchResultsView(List<Sach> results) { /* ... Giữ nguyên ... */
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) mainContentPanel.remove(centerComponent); JScrollPane searchResultPanel = createDocumentGalleryPanel(results); mainContentPanel.add(searchResultPanel, BorderLayout.CENTER); mainContentPanel.revalidate(); mainContentPanel.repaint(); if (results.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    }

    // =========================================================================
    // === PHƯƠNG THỨC CHO HỒ SƠ TÁC GIẢ (Giữ nguyên) ===
    // =========================================================================
    private void showAuthorProfileView(String authorName) { /* ... Giữ nguyên ... */
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) { mainContentPanel.remove(centerComponent); } JPanel authorPanel = createAuthorProfilePanel(authorName); mainContentPanel.add(authorPanel, BorderLayout.CENTER); mainContentPanel.revalidate(); mainContentPanel.repaint();
    }
    private JPanel createAuthorProfilePanel(String authorName) { /* ... Giữ nguyên ... */
        JPanel profilePanel = new JPanel(new BorderLayout(10, 10)); profilePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); JPanel topPanel = new JPanel(new BorderLayout()); JButton btnBack = new JButton("< Quay lại"); btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12)); btnBack.setFocusPainted(false); btnBack.addActionListener(e -> showGalleryView()); topPanel.add(btnBack, BorderLayout.WEST); profilePanel.add(topPanel, BorderLayout.NORTH); JPanel contentPanel = new JPanel(new BorderLayout(0, 15)); JPanel authorInfoPanel = new JPanel(new BorderLayout(10, 5)); authorInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); JLabel lblAuthorName = new JLabel(authorName); lblAuthorName.setFont(new Font("Segoe UI", Font.BOLD, 20)); lblAuthorName.setForeground(Color.WHITE); lblAuthorName.setOpaque(true); lblAuthorName.setBackground(new Color(0, 153, 204)); lblAuthorName.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); authorInfoPanel.add(lblAuthorName, BorderLayout.NORTH); JPanel detailInfoPanel = new JPanel(); detailInfoPanel.setLayout(new BoxLayout(detailInfoPanel, BoxLayout.Y_AXIS)); detailInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); detailInfoPanel.add(createAuthorInfoRow("Email:", "Chưa cập nhật")); detailInfoPanel.add(Box.createVerticalStrut(5)); detailInfoPanel.add(createAuthorInfoRow("Phone:", "Chưa cập nhật")); detailInfoPanel.add(Box.createVerticalStrut(10)); detailInfoPanel.add(createAuthorInfoRow("Trình độ chuyên môn:", "Chưa cập nhật")); detailInfoPanel.add(Box.createVerticalStrut(5)); detailInfoPanel.add(createAuthorInfoRow("Chức danh:", "Chưa cập nhật")); authorInfoPanel.add(detailInfoPanel, BorderLayout.CENTER); contentPanel.add(authorInfoPanel, BorderLayout.NORTH); JPanel bookListSection = new JPanel(new BorderLayout(0, 10)); List<Sach> authorBooks = sachDAO.getSachByTacGia(authorName); JLabel lblBookCount = new JLabel("Kết quả: 1-" + Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()) + "/" + authorBooks.size()); lblBookCount.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblBookCount.setForeground(Color.DARK_GRAY); bookListSection.add(lblBookCount, BorderLayout.NORTH); if (authorBooks.isEmpty()) { bookListSection.add(new JLabel("Chưa có sách.", SwingConstants.CENTER), BorderLayout.CENTER); } else { JPanel bookItemsPanel = new JPanel(); bookItemsPanel.setLayout(new BoxLayout(bookItemsPanel, BoxLayout.Y_AXIS)); for (int i = 0; i < Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()); i++) { Sach sach = authorBooks.get(i); JPanel bookEntryPanel = createAuthorBookEntryPanel(sach); bookEntryPanel.setAlignmentX(Component.LEFT_ALIGNMENT); bookItemsPanel.add(bookEntryPanel); if (i < Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()) - 1) { bookItemsPanel.add(new JSeparator(SwingConstants.HORIZONTAL)); } } JScrollPane listScrollPane = new JScrollPane(bookItemsPanel); listScrollPane.setBorder(BorderFactory.createEmptyBorder()); bookListSection.add(listScrollPane, BorderLayout.CENTER); } contentPanel.add(bookListSection, BorderLayout.CENTER); profilePanel.add(contentPanel, BorderLayout.CENTER); return new JPanel(new BorderLayout()) {{ add(new JScrollPane(profilePanel), BorderLayout.CENTER); }};
    }
    private JPanel createAuthorInfoRow(String labelText, String valueText) { /* ... Giữ nguyên ... */
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); JLabel label = new JLabel(labelText); label.setFont(new Font("Segoe UI", Font.PLAIN, 12)); label.setForeground(Color.DARK_GRAY); JLabel value = new JLabel(valueText); value.setFont(new Font("Segoe UI", Font.PLAIN, 12)); rowPanel.add(label); rowPanel.add(value); rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height)); return rowPanel;
    }
    private JPanel createAuthorBookEntryPanel(Sach sach) { /* ... Giữ nguyên ... */
        JPanel entryPanel = new JPanel(new BorderLayout(15, 5)); entryPanel.setBorder(new EmptyBorder(10, 5, 10, 5)); entryPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); entryPanel.setBackground(Color.WHITE); entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); entryPanel.putClientProperty("sachObject", sach); JLabel coverLabel = new JLabel(); int thumbSize = 80; coverLabel.setPreferredSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setMinimumSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setMaximumSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setHorizontalAlignment(SwingConstants.CENTER); coverLabel.setVerticalAlignment(SwingConstants.CENTER); coverLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); String imgPath = sach.getDuongDanAnh(); if (imgPath != null && !imgPath.isEmpty()) { try { File imgFile = new File(imgPath); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), imgPath); if (imgFile.exists()) { ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image img = icon.getImage().getScaledInstance(thumbSize, (int)(thumbSize * 1.4), Image.SCALE_SMOOTH); coverLabel.setIcon(new ImageIcon(img)); } else { coverLabel.setText("N/A"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } } catch (Exception e) { coverLabel.setText("Lỗi"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } } else { coverLabel.setText("N/A"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } entryPanel.add(coverLabel, BorderLayout.WEST); JPanel infoPanel = new JPanel(); infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); infoPanel.setOpaque(false); JLabel titleLabel = new JLabel(sach.getTenSach()); titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(titleLabel); infoPanel.add(Box.createVerticalStrut(3)); String authorStr = sach.getTacGia() != null ? sach.getTacGia() : "N/A"; JLabel authorLabel = new JLabel("Tác giả: " + authorStr); authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); authorLabel.setForeground(Color.DARK_GRAY); authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(authorLabel); infoPanel.add(Box.createVerticalStrut(3)); String yearStr = sach.getNamXuatBan() > 0 ? String.valueOf(sach.getNamXuatBan()) : "N/A"; JLabel yearLabel = new JLabel("Năm XB: " + yearStr); yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); yearLabel.setForeground(Color.DARK_GRAY); yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(yearLabel); infoPanel.add(Box.createVerticalStrut(3)); JLabel maSachLabel = new JLabel("Mã sách: " + sach.getMaSach()); maSachLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); maSachLabel.setForeground(Color.GRAY); maSachLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(maSachLabel); infoPanel.add(Box.createVerticalGlue()); entryPanel.add(infoPanel, BorderLayout.CENTER); entryPanel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { Sach clickedSach = (Sach) ((JPanel)e.getSource()).getClientProperty("sachObject"); if (clickedSach != null) { showDetailView(clickedSach); } } @Override public void mouseEntered(MouseEvent e) { ((JPanel)e.getSource()).setBackground(new Color(230, 245, 255)); } @Override public void mouseExited(MouseEvent e) { ((JPanel)e.getSource()).setBackground(Color.WHITE); } }); return entryPanel;
    }


    public static void main(String[] args) {
        // Giữ nguyên main này nếu muốn Trang Chủ chạy đầu tiên
        // Xóa đi nếu muốn LoginForm chạy đầu tiên
        SwingUtilities.invokeLater(() -> new LibraryHomePageView());
    }
}