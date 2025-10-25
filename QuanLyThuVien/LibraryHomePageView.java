package QuanLyThuVien; // Đảm bảo đây là dòng ĐẦU TIÊN

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;

// === IMPORT ĐÃ THÊM MỚI ===
import QuanLyThuVien.TacGia;
import QuanLyThuVien.TacGiaDAO;
// === KẾT THÚC IMPORT THÊM MỚI ===


public class LibraryHomePageView extends JFrame {

    private SachDAO sachDAO;

    // Các panel chính
    private JPanel mainContentPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JComponent galleryView; // Gallery ban đầu

    // === BIẾN ĐỂ SỬA LỖI QUAY LẠI ===
    private JComponent lastActiveView; // Lưu màn hình trước khi xem chi tiết

    // Biến cho tìm kiếm
    private JTextField txtSearch;
    private JComboBox<String> cmbSearchType;

    // === BIẾN TRẠNG THÁI CHO "DUYỆT THEO..." (TỔNG QUÁT HÓA) ===
    private int currentBookPage = 1;
    private static final int ITEMS_PER_LIST_PAGE = 10; // 10 cuốn mỗi trang
    private String currentSortBy = "namXuatBan"; // Mặc định sắp xếp
    private String currentSortOrder = "ASC";     // Mặc định tăng dần

    private String currentFilterType = ""; // "YEAR" hoặc "TITLE_PREFIX"
    private String currentFilterValue = ""; // "2023" hoặc "A"

    // Components cho view "Duyệt theo"
    private JPanel bookListContainerPanel; // Panel (CENTER) để chứa danh sách
    private JLabel paginationLabel;        // Label (SOUTH) "Trang 1 / X"

    // Components cho "Duyệt theo năm"
    private JTextField yearFilterField;
    private JComboBox<String> yearFilterComboBox;

    // Components cho "Duyệt theo nhan đề"
    private JTextField titleFilterField;
    private JComboBox<String> titleFilterComboBox;


    // --- Hằng số cho số lượng item hiển thị ---
    private static final int ITEMS_PER_GALLERY = 9;
    private static final int ITEMS_PER_AUTHOR_LIST = 10;

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

        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();
        galleryView = createGalleryTabs();

        // === KHỞI TẠO BIẾN SỬA LỖI ===
        lastActiveView = galleryView; // Mặc định là gallery

        mainContentPanel.add(leftPanel, BorderLayout.WEST);
        mainContentPanel.add(rightPanel, BorderLayout.EAST);
        mainContentPanel.add(galleryView, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Phương thức 1: Tạo Header ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(Color.WHITE);
        JPanel topBar = new JPanel(new BorderLayout()); topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel logo = new JLabel("  [LOGO PTIT]", SwingConstants.CENTER); logo.setFont(new Font("Arial", Font.BOLD, 24)); logo.setPreferredSize(new Dimension(200, 50)); topBar.add(logo, BorderLayout.WEST);

        // --- SỬA NÚT "TRANG CHỦ" ---
        JPanel navMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        String[] menuItems = {"TRANG CHỦ", "DUYỆT THEO", "TRỢ GIÚP", "LIÊN HỆ"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);

            if (item.equals("TRANG CHỦ")) {
                // Gán hành động quay về trang chủ (chỉ cho nút này)
                btn.addActionListener(e -> showGalleryView());
            }

            navMenu.add(btn);
        }
        topBar.add(navMenu, BorderLayout.CENTER);
        // --- KẾT THÚC SỬA NÚT "TRANG CHỦ" ---

        JButton btnLogin = new JButton("Đăng nhập"); btnLogin.addActionListener(e -> { DangKyForm loginForm = new DangKyForm(); loginForm.setVisible(true); }); topBar.add(btnLogin, BorderLayout.EAST); header.add(topBar, BorderLayout.NORTH);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); searchBar.setBackground(new Color(240, 240, 240));
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
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setPreferredSize(new Dimension(250, 700));
        String[] browseItems = {"Đơn vị & Bộ sưu tập", "Năm xuất bản", "Tác giả", "Nhan đề", "Chủ đề"};

        // === SỬA: Thêm "BROWSE" ID ===
        panel.add(createSidebarSection("Duyệt theo", "BROWSE", browseItems, new Color(220, 0, 0), null));
        panel.add(Box.createVerticalStrut(15));

        // <<< SỬA LỖI: Gọi getAuthorCount và getAuthorsPaginated từ SachDAO
        // (Trong SachDAO, các hàm này đếm và lấy từ bảng TACGIA, nên logic là đúng)
        List<SachDAO.AuthorInfo> allAuthors = sachDAO.getAuthorsPaginated(1, 999, "authorName", "ASC", null, null);
        String[] authorDisplayItems = allAuthors.stream()
                .map(info -> info.authorName + " (" + info.bookCount + ")")
                .toArray(String[]::new);
        String[] authorNamesOnly = allAuthors.stream()
                .map(info -> info.authorName)
                .toArray(String[]::new);


        // === SỬA: Thêm "AUTHORS" ID ===
        panel.add(createSidebarSection("Hồ sơ tác giả", "AUTHORS", authorDisplayItems, new Color(220, 0, 0), authorNamesOnly));
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setPreferredSize(new Dimension(250, 700)); panel.add(createSimpleLinkButton("Hướng dẫn tìm kiếm")); panel.add(createSimpleLinkButton("Hòm thư góp ý")); panel.add(Box.createVerticalStrut(15)); panel.add(createDatabaseLinkSection()); return panel;
    }
    private JComponent createGalleryTabs() {
        JTabbedPane documentTabs = new JTabbedPane(); documentTabs.setFont(new Font("Arial", Font.BOLD, 14));
        List<Sach> newBooks = sachDAO.getSachMoiNhat(ITEMS_PER_GALLERY); List<Sach> viewedBooks = sachDAO.getSachXemNhieuNhat(ITEMS_PER_GALLERY); List<Sach> downloadedBooks = sachDAO.getSachTaiNhieuNhat(ITEMS_PER_GALLERY);
        documentTabs.addTab("Tài liệu mới cập nhập", createDocumentGalleryPanel(newBooks)); documentTabs.addTab("Xem nhiều nhất", createDocumentGalleryPanel(viewedBooks)); documentTabs.addTab("Download nhiều nhất", createDocumentGalleryPanel(downloadedBooks)); return documentTabs;
     }

    // --- Các Phương thức tiện ích ---

    // === SỬA: Thêm tham số "sectionID" ===
    private JPanel createSidebarSection(String title, String sectionID, String[] itemsToDisplay, Color titleColor, String[] actionCommands) {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title); titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); titleLabel.setForeground(Color.WHITE); titleLabel.setOpaque(true); titleLabel.setBackground(titleColor); titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleLabel.getPreferredSize().height)); panel.add(titleLabel);
        Border buttonBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(10, 5, 10, 5));

        for (int i = 0; i < itemsToDisplay.length; i++) {
            JButton btn = new JButton(itemsToDisplay[i]);
            btn.setFocusPainted(false); btn.setBorder(buttonBorder); btn.setBackground(new Color(240, 240, 240)); btn.setForeground(Color.BLUE.darker()); btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setAlignmentX(Component.LEFT_ALIGNMENT); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));

            // === SỬA: Logic gán sự kiện ===
            if ("BROWSE".equals(sectionID)) {
                // Gán sự kiện cho các nút "Duyệt theo"
                if ("Năm xuất bản".equals(itemsToDisplay[i])) {
                    btn.addActionListener(e -> showBrowseByYearView());
                }
                // --- THÊM MỚI SỰ KIỆN CHO "NHAN ĐỀ" ---
                else if ("Nhan đề".equals(itemsToDisplay[i])) {
                    btn.addActionListener(e -> showBrowseByTitleView());
                }
                // (Bạn có thể thêm else if cho "Tác giả", "Chủ đề" tại đây sau)

            } else if ("AUTHORS".equals(sectionID) && actionCommands != null && i < actionCommands.length) {
                // Giữ nguyên logic cho Hồ sơ tác giả
                final String authorName = actionCommands[i];
                btn.addActionListener(e -> showAuthorProfileView(authorName));
            }
            // === KẾT THÚC SỬA ===

            panel.add(btn);
        }
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JButton createSimpleLinkButton(String text) {
        JButton btn = new JButton(text); btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setFocusPainted(false); btn.setBackground(new Color(240, 240, 240)); btn.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(10, 5, 10, 5))); btn.setAlignmentX(Component.LEFT_ALIGNMENT); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height)); return btn;
    }
    private JPanel createDatabaseLinkSection() {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBorder(BorderFactory.createTitledBorder("Cơ sở dữ liệu trực tuyến")); String[] dbNames = {"ProQuest", "IG Publishing", "SSAGE journals"}; for (String name : dbNames) { JLabel dbLabel = new JLabel("[LOGO] " + name, SwingConstants.CENTER); dbLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); dbLabel.setAlignmentX(Component.LEFT_ALIGNMENT); dbLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); dbLabel.setMinimumSize(new Dimension(0, 50)); panel.add(dbLabel); panel.add(Box.createVerticalStrut(5)); } panel.setAlignmentX(Component.LEFT_ALIGNMENT); return panel;
    }


    // =========================================================================
    // === CÁC PHƯƠNG THỨC ĐỂ CHUYỂN ĐỔI GIAO DIỆN VÀ CHI TIẾT SÁCH ===
    // =========================================================================

    /**
     * === HÀM ĐỂ QUAY LẠI MÀN HÌNH TRƯỚC ĐÓ ===
     * Quay lại màn hình (Gallery, Search, hoặc Author) đã xem gần nhất.
     */
    private void showLastActiveView() {
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            mainContentPanel.remove(centerComponent);
        }

        // Mặc định quay về gallery nếu có lỗi
        if (lastActiveView == null) {
            lastActiveView = galleryView;
        }

        mainContentPanel.add(lastActiveView, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showGalleryView() {
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) mainContentPanel.remove(centerComponent);
        mainContentPanel.add(galleryView, BorderLayout.CENTER);
        // === SỬA LỖI: Cập nhật lastActiveView ===
        lastActiveView = galleryView;
        mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    private void showDetailView(Sach sach) {
        sachDAO.tangLuotXem(sach.getMaSach()); Sach updatedSach = sachDAO.getSachByMaSach(sach.getMaSach()); if (updatedSach == null) updatedSach = sach;
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) mainContentPanel.remove(centerComponent); JPanel detailPanel = createSachDetailPanel(updatedSach); mainContentPanel.add(detailPanel, BorderLayout.CENTER); mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    private JPanel createSachDetailPanel(Sach sach) {
        JPanel detailContentPanel = new JPanel(new BorderLayout(15, 15)); detailContentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); JPanel topPanel = new JPanel(new BorderLayout()); JButton btnBack = new JButton("< Quay lại"); btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12)); btnBack.setFocusPainted(false);

        // --- SỬA NÚT "QUAY LẠI" ---
        // btnBack.addActionListener(e -> showGalleryView()); // Lỗi cũ
        btnBack.addActionListener(e -> showLastActiveView()); // Sửa thành hàm mới

        topPanel.add(btnBack, BorderLayout.WEST); detailContentPanel.add(topPanel, BorderLayout.NORTH); JPanel centerWrapper = new JPanel(new BorderLayout(15, 15)); JPanel coverPanel = new JPanel(new BorderLayout()); coverPanel.setPreferredSize(new Dimension(250, 350)); JLabel lblAnhBia = new JLabel(); lblAnhBia.setHorizontalAlignment(SwingConstants.CENTER); lblAnhBia.setVerticalAlignment(SwingConstants.CENTER); String duongDanAnh = sach.getDuongDanAnh(); if (duongDanAnh != null && !duongDanAnh.isEmpty()) { try { File imgFile = new File(duongDanAnh); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), duongDanAnh); if (imgFile.exists()) { ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image image = icon.getImage().getScaledInstance(240, 340, Image.SCALE_SMOOTH); lblAnhBia.setIcon(new ImageIcon(image)); } else { lblAnhBia.setText("Ảnh lỗi (" + duongDanAnh + ")"); } } catch (Exception e) { lblAnhBia.setText("[Lỗi ảnh]"); e.printStackTrace(); } } else { lblAnhBia.setText("[Không có ảnh bìa]"); } coverPanel.add(lblAnhBia, BorderLayout.CENTER); centerWrapper.add(coverPanel, BorderLayout.WEST); JPanel infoPanel = new JPanel(); infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); JLabel lblTenSach = new JLabel(sach.getTenSach()); lblTenSach.setFont(new Font("Segoe UI", Font.BOLD, 24)); lblTenSach.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(lblTenSach); infoPanel.add(Box.createVerticalStrut(15)); 
        
        // <<< SỬA LỖI: Thay getTacGia() bằng getMaTacGia()
        infoPanel.add(createInfoRow("Tác giả:", sach.getMaTacGia())); 
        
        infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Nhà xuất bản:", sach.getNhaXuatBan())); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Năm xuất bản:", (sach.getNamXuatBan() > 0) ? String.valueOf(sach.getNamXuatBan()) : "N/A")); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Số lượng còn:", String.valueOf(sach.getSoLuong()))); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Mã sách:", sach.getMaSach())); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Lượt xem:", String.valueOf(sach.getLuotXem()))); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(createInfoRow("Lượt tải:", String.valueOf(sach.getLuotTai()))); infoPanel.add(Box.createVerticalStrut(15)); JLabel lblTomTatTitle = new JLabel("Tóm tắt:"); lblTomTatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblTomTatTitle.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(lblTomTatTitle); JTextArea txtMoTa = new JTextArea(sach.getMoTa()); txtMoTa.setEditable(false); txtMoTa.setLineWrap(true); txtMoTa.setWrapStyleWord(true); txtMoTa.setBackground(infoPanel.getBackground()); JScrollPane scrollMoTa = new JScrollPane(txtMoTa); scrollMoTa.setAlignmentX(Component.LEFT_ALIGNMENT); scrollMoTa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); infoPanel.add(scrollMoTa); centerWrapper.add(infoPanel, BorderLayout.CENTER); detailContentPanel.add(centerWrapper, BorderLayout.CENTER); String duongDanXemTruoc = sach.getDuongDanXemTruoc(); if (duongDanXemTruoc != null && !duongDanXemTruoc.isEmpty()) { JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); previewPanel.setBorder(BorderFactory.createTitledBorder("Tệp đọc thử")); File previewFile = new File(duongDanXemTruoc); JLabel lblFileName = new JLabel(previewFile.getName()); JButton btnOpenPreview = new JButton("📖 Mở đọc thử"); JButton btnDownloadPreview = new JButton("💾 Tải xuống"); btnOpenPreview.addActionListener(e -> moFileXemTruoc(previewFile)); btnDownloadPreview.addActionListener(e -> taiFileXemTruoc(previewFile, sach.getMaSach())); previewPanel.add(lblFileName); previewPanel.add(btnOpenPreview); previewPanel.add(btnDownloadPreview); detailContentPanel.add(previewPanel, BorderLayout.SOUTH); } return new JPanel(new BorderLayout()) {{ add(new JScrollPane(detailContentPanel), BorderLayout.CENTER); }};
     }
    private JPanel createInfoRow(String labelText, String valueText) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); JLabel label = new JLabel(labelText); label.setFont(new Font("Segoe UI", Font.BOLD, 14)); label.setPreferredSize(new Dimension(120, 20)); JLabel value = new JLabel( (valueText != null && !valueText.isEmpty() && !valueText.equals("0")) ? valueText : "N/A"); value.setFont(new Font("Segoe UI", Font.PLAIN, 14)); rowPanel.add(label); rowPanel.add(value); rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height)); return rowPanel;
    }
    private void moFileXemTruoc(File fileToOpen) {
         if (!fileToOpen.exists()) { File absoluteFile = new File(System.getProperty("user.dir"), fileToOpen.getPath()); if (!absoluteFile.exists()) { JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại.\nKiểm tra đường dẫn: " + fileToOpen.getPath(), "Không tìm thấy file", JOptionPane.ERROR_MESSAGE); return; } fileToOpen = absoluteFile; } if (Desktop.isDesktopSupported()) { try { Desktop.getDesktop().open(fileToOpen); } catch (IOException | SecurityException ex) { JOptionPane.showMessageDialog(this, "Không thể mở file đọc thử.\nLỗi: " + ex.getMessage(), "Lỗi mở file", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); } } else { JOptionPane.showMessageDialog(this, "Tính năng mở file không được hỗ trợ.", "Lỗi hệ thống", JOptionPane.WARNING_MESSAGE); }
    }
    private void taiFileXemTruoc(File fileToDownload, String maSach) {
         if (!fileToDownload.exists()) { File absoluteFile = new File(System.getProperty("user.dir"), fileToDownload.getPath()); if (!absoluteFile.exists()) { JOptionPane.showMessageDialog(this, "Lỗi: File đọc thử không tồn tại.", "Không tìm thấy file", JOptionPane.ERROR_MESSAGE); return; } fileToDownload = absoluteFile; } JFileChooser fileChooser = new JFileChooser(); fileChooser.setDialogTitle("Lưu file đọc thử"); fileChooser.setSelectedFile(new File(fileToDownload.getName())); int userSelection = fileChooser.showSaveDialog(this); if (userSelection == JFileChooser.APPROVE_OPTION) { File fileToSave = fileChooser.getSelectedFile(); try { Files.copy(fileToDownload.toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING); sachDAO.tangLuotTai(maSach); JOptionPane.showMessageDialog(this, "Tải file thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE); } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); } }
    }

    private JScrollPane createDocumentGalleryPanel(List<Sach> bookList) {
        if (bookList == null || bookList.isEmpty()) { JPanel emptyPanel = new JPanel(new GridBagLayout()); emptyPanel.add(new JLabel("Không có sách nào.")); return new JScrollPane(emptyPanel); } JPanel galleryPanel = new JPanel(new GridLayout(0, 3, 10, 10)); galleryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); for (Sach sach : bookList) { JPanel bookPanel = new JPanel(new BorderLayout(5, 5)); bookPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); bookPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); bookPanel.putClientProperty("sachObject", sach); JLabel coverLabel = new JLabel(); coverLabel.setPreferredSize(new Dimension(150, 200)); coverLabel.setHorizontalAlignment(SwingConstants.CENTER); coverLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); try { String imagePath = sach.getDuongDanAnh(); if (imagePath != null && !imagePath.isEmpty()) { File imgFile = new File(imagePath); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), imagePath); if (imgFile.exists()) { ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH); coverLabel.setIcon(new ImageIcon(img)); } else { coverLabel.setText("[Ảnh lỗi]"); } } else { coverLabel.setText("[No Cover]"); } } catch (Exception e) { coverLabel.setText("[Lỗi ảnh]"); e.printStackTrace(); } bookPanel.add(coverLabel, BorderLayout.CENTER); JLabel nameLabel = new JLabel(sach.getTenSach()); nameLabel.setHorizontalAlignment(SwingConstants.CENTER); nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); bookPanel.add(nameLabel, BorderLayout.SOUTH); bookPanel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { Sach clickedSach = (Sach) ((JPanel)e.getSource()).getClientProperty("sachObject"); if (clickedSach != null) showDetailView(clickedSach); } }); galleryPanel.add(bookPanel); } JScrollPane scrollPane = new JScrollPane(galleryPanel); return scrollPane; }


    // =========================================================================
    // === PHƯƠNG THỨC XỬ LÝ TÌM KIẾM ===
    // =========================================================================
    private void xuLyTimKiem() {
        String keyword = txtSearch.getText().trim(); String searchType = (String) cmbSearchType.getSelectedItem(); if (keyword.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; } System.out.println("Tìm kiếm: '" + keyword + "' theo '" + searchType + "'"); List<Sach> searchResults = sachDAO.timKiemSachNangCao(keyword, searchType); showSearchResultsView(searchResults);
    }
    private void showSearchResultsView(List<Sach> results) {
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) mainContentPanel.remove(centerComponent);
        JScrollPane searchResultPanel = createDocumentGalleryPanel(results);
        mainContentPanel.add(searchResultPanel, BorderLayout.CENTER);
        // === SỬA LỖI: Cập nhật lastActiveView ===
        lastActiveView = searchResultPanel;
        mainContentPanel.revalidate(); mainContentPanel.repaint();
        if (results.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    }

    // =========================================================================
    // === PHƯƠNG THỨC CHO HỒ SƠ TÁC GIẢ ===
    // =========================================================================
    private void showAuthorProfileView(String authorName) {
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER); if (centerComponent != null) { mainContentPanel.remove(centerComponent); }
        JPanel authorPanel = createAuthorProfilePanel(authorName);
        mainContentPanel.add(authorPanel, BorderLayout.CENTER);
        // === SỬA LỖI: Cập nhật lastActiveView ===
        lastActiveView = authorPanel;
        mainContentPanel.revalidate(); mainContentPanel.repaint();
    }

    /**
     * === NÂNG CẤP: LẤY DỮ LIỆU TÁC GIẢ THẬT ===
     * Tạo Panel (GUI) cho Hồ sơ Tác giả, sử dụng TacGiaDAO.
     */
    private JPanel createAuthorProfilePanel(String authorName) {
        JPanel profilePanel = new JPanel(new BorderLayout(10, 10)); profilePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); profilePanel.setBackground(Color.WHITE); JPanel topPanel = new JPanel(new BorderLayout()); topPanel.setOpaque(false); JButton btnBack = new JButton("< Quay lại"); btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12)); btnBack.setFocusPainted(false); btnBack.addActionListener(e -> showGalleryView()); topPanel.add(btnBack, BorderLayout.WEST); profilePanel.add(topPanel, BorderLayout.NORTH); JPanel contentPanel = new JPanel(new BorderLayout(0, 15)); contentPanel.setOpaque(false); JPanel authorInfoPanel = new JPanel(new BorderLayout(10, 5)); authorInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); authorInfoPanel.setOpaque(false); JLabel lblAuthorName = new JLabel(authorName); lblAuthorName.setFont(new Font("Segoe UI", Font.BOLD, 20)); lblAuthorName.setForeground(Color.WHITE); lblAuthorName.setOpaque(true); lblAuthorName.setBackground(new Color(0, 153, 204)); lblAuthorName.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); authorInfoPanel.add(lblAuthorName, BorderLayout.NORTH); 
        
        JPanel detailInfoPanel = new JPanel(); 
        detailInfoPanel.setLayout(new BoxLayout(detailInfoPanel, BoxLayout.Y_AXIS)); 
        detailInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        detailInfoPanel.setOpaque(false); 

        // --- NÂNG CẤP: Lấy dữ liệu chi tiết từ TacGiaDAO ---
        TacGiaDAO tacGiaDAO = new TacGiaDAO();
        // authorName chính là maTacGia
        TacGia tacGiaDetails = tacGiaDAO.getTacGiaByMa(authorName); 
        
        String email = "Chưa cập nhật";
        String sdt = "Chưa cập nhật";
        String trinhDo = "Chưa cập nhật";
        String chucDanh = "Chưa cập nhật";

        if (tacGiaDetails != null) {
            email = (tacGiaDetails.getEmail() != null && !tacGiaDetails.getEmail().isEmpty()) ? tacGiaDetails.getEmail() : email;
            sdt = (tacGiaDetails.getSdt() != null && !tacGiaDetails.getSdt().isEmpty()) ? tacGiaDetails.getSdt() : sdt;
            trinhDo = (tacGiaDetails.getTrinhDoChuyenMon() != null && !tacGiaDetails.getTrinhDoChuyenMon().isEmpty()) ? tacGiaDetails.getTrinhDoChuyenMon() : trinhDo;
            chucDanh = (tacGiaDetails.getChucDanh() != null && !tacGiaDetails.getChucDanh().isEmpty()) ? tacGiaDetails.getChucDanh() : chucDanh;
        }

        detailInfoPanel.add(createAuthorInfoRow("Email:", email)); 
        detailInfoPanel.add(Box.createVerticalStrut(5)); 
        detailInfoPanel.add(createAuthorInfoRow("Phone:", sdt)); 
        detailInfoPanel.add(Box.createVerticalStrut(10)); 
        detailInfoPanel.add(createAuthorInfoRow("Trình độ chuyên môn:", trinhDo)); 
        detailInfoPanel.add(Box.createVerticalStrut(5)); 
        detailInfoPanel.add(createAuthorInfoRow("Chức danh:", chucDanh));
        // --- KẾT THÚC NÂNG CẤP ---
        
        authorInfoPanel.add(detailInfoPanel, BorderLayout.CENTER); 
        contentPanel.add(authorInfoPanel, BorderLayout.NORTH); 
        
        // Phần hiển thị danh sách sách (giữ nguyên)
        JPanel bookListSection = new JPanel(new BorderLayout(0, 10)); bookListSection.setOpaque(false); List<Sach> authorBooks = sachDAO.getSachByTacGia(authorName); JLabel lblBookCount = new JLabel("Kết quả tìm kiếm: 1 đến " + Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()) + " trong tổng số " + authorBooks.size() + " kết quả"); lblBookCount.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblBookCount.setForeground(Color.DARK_GRAY); bookListSection.add(lblBookCount, BorderLayout.NORTH); if (authorBooks.isEmpty()) { bookListSection.add(new JLabel("Chưa có sách.", SwingConstants.CENTER), BorderLayout.CENTER); } else { JPanel bookItemsPanel = new JPanel(); bookItemsPanel.setLayout(new BoxLayout(bookItemsPanel, BoxLayout.Y_AXIS)); bookItemsPanel.setBackground(Color.WHITE); for (int i = 0; i < Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()); i++) { Sach sach = authorBooks.get(i); JPanel bookEntryPanel = createAuthorBookEntryPanel(sach); bookEntryPanel.setAlignmentX(Component.LEFT_ALIGNMENT); bookItemsPanel.add(bookEntryPanel); if (i < Math.min(ITEMS_PER_AUTHOR_LIST, authorBooks.size()) - 1) { bookItemsPanel.add(Box.createRigidArea(new Dimension(0, 1))); JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); bookItemsPanel.add(sep); bookItemsPanel.add(Box.createRigidArea(new Dimension(0, 1))); } } bookItemsPanel.add(Box.createVerticalGlue()); JPanel wrapperPanel = new JPanel(new BorderLayout()); wrapperPanel.setBackground(Color.WHITE); wrapperPanel.add(bookItemsPanel, BorderLayout.NORTH); JScrollPane listScrollPane = new JScrollPane(wrapperPanel); listScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); bookListSection.add(listScrollPane, BorderLayout.CENTER); } contentPanel.add(bookListSection, BorderLayout.CENTER); profilePanel.add(contentPanel, BorderLayout.CENTER); return profilePanel;
    }
    private JPanel createAuthorInfoRow(String labelText, String valueText) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); JLabel label = new JLabel(labelText); label.setFont(new Font("Segoe UI", Font.PLAIN, 12)); label.setForeground(Color.DARK_GRAY); JLabel value = new JLabel(valueText); value.setFont(new Font("Segoe UI", Font.PLAIN, 12)); rowPanel.add(label); rowPanel.add(value); rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height)); return rowPanel;
    }
    private JPanel createAuthorBookEntryPanel(Sach sach) {
        JPanel entryPanel = new JPanel(new BorderLayout(15, 5)); entryPanel.setBorder(new EmptyBorder(10, 5, 10, 5)); entryPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); entryPanel.setBackground(Color.WHITE); entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); entryPanel.putClientProperty("sachObject", sach); JLabel coverLabel = new JLabel(); int thumbSize = 80; coverLabel.setPreferredSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setMinimumSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setMaximumSize(new Dimension(thumbSize, (int)(thumbSize * 1.4))); coverLabel.setHorizontalAlignment(SwingConstants.CENTER); coverLabel.setVerticalAlignment(SwingConstants.CENTER); coverLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); String imgPath = sach.getDuongDanAnh(); if (imgPath != null && !imgPath.isEmpty()) { try { File imgFile = new File(imgPath); if (!imgFile.exists()) imgFile = new File(System.getProperty("user.dir"), imgPath); if (imgFile.exists()) { ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath()); Image img = icon.getImage().getScaledInstance(thumbSize, (int)(thumbSize * 1.4), Image.SCALE_SMOOTH); coverLabel.setIcon(new ImageIcon(img)); } else { coverLabel.setText("N/A"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } } catch (Exception e) { coverLabel.setText("Lỗi"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } } else { coverLabel.setText("N/A"); coverLabel.setFont(new Font("Arial", Font.ITALIC, 10)); } entryPanel.add(coverLabel, BorderLayout.WEST); JPanel infoPanel = new JPanel(); infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); infoPanel.setOpaque(false); JLabel titleLabel = new JLabel(sach.getTenSach()); titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(titleLabel); infoPanel.add(Box.createVerticalStrut(3)); 
        
        // <<< SỬA LỖI: Thay getTacGia() bằng getMaTacGia()
        String authorStr = sach.getMaTacGia() != null ? sach.getMaTacGia() : "N/A"; 
        
        JLabel authorLabel = new JLabel("Tác giả: " + authorStr); authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); authorLabel.setForeground(Color.DARK_GRAY); authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(authorLabel); infoPanel.add(Box.createVerticalStrut(3)); String yearStr = sach.getNamXuatBan() > 0 ? String.valueOf(sach.getNamXuatBan()) : "N/A"; JLabel yearLabel = new JLabel("Năm XB: " + yearStr); yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); yearLabel.setForeground(Color.DARK_GRAY); yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(yearLabel); infoPanel.add(Box.createVerticalStrut(3)); JLabel maSachLabel = new JLabel("Mã sách: " + sach.getMaSach()); maSachLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); maSachLabel.setForeground(Color.GRAY); maSachLabel.setAlignmentX(Component.LEFT_ALIGNMENT); infoPanel.add(maSachLabel); infoPanel.add(Box.createVerticalGlue()); entryPanel.add(infoPanel, BorderLayout.CENTER); entryPanel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { Sach clickedSach = (Sach) ((JPanel)e.getSource()).getClientProperty("sachObject"); if (clickedSach != null) { showDetailView(clickedSach); } } @Override public void mouseEntered(MouseEvent e) { ((JPanel)e.getSource()).setBackground(new Color(230, 245, 255)); } @Override public void mouseExited(MouseEvent e) { ((JPanel)e.getSource()).setBackground(Color.WHITE); } }); return entryPanel;
    }

    // =========================================================================
    // === PHƯƠNG THỨC CHO CÁC CHỨC NĂNG "DUYỆT THEO..." ===
    // =========================================================================

    /**
     * (ĐÃ CẬP NHẬT) Hiển thị giao diện "Duyệt theo năm"
     */
    private void showBrowseByYearView() {
        // 1. Reset trạng thái
        currentBookPage = 1;
        currentSortBy = "namXuatBan"; // Sắp xếp theo năm
        currentSortOrder = "ASC";
        currentFilterType = "YEAR"; // <-- Sửa
        currentFilterValue = "";      // <-- Sửa

        // 2. Tạo Panel chính
        JPanel mainBrowsePanel = new JPanel(new BorderLayout(10, 10));
        mainBrowsePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 3. (NORTH) Panel Tiêu đề và Lọc
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        JLabel title = new JLabel("Tìm kiếm theo: Năm xuất bản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(51, 51, 51));
        northPanel.add(title, BorderLayout.NORTH);

        // --- GIAO DIỆN LỌC (VỚI COMBOBOX) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        filterPanel.add(new JLabel("Chọn năm:"));
        List<String> namList = sachDAO.getDistinctNamXuatBan();
        namList.add(0, "Tất cả");
        yearFilterComboBox = new JComboBox<>(namList.toArray(new String[0]));
        filterPanel.add(yearFilterComboBox);
        filterPanel.add(new JLabel(" hoặc nhập năm:"));
        yearFilterField = new JTextField(6);
        filterPanel.add(yearFilterField);
        JButton findButton = new JButton("Tìm kiếm");
        JButton viewAllButton = new JButton("Xem tất cả");
        filterPanel.add(findButton);
        filterPanel.add(viewAllButton);

        JButton sortButton = new JButton("⚙️ Sắp xếp");
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Giảm gap
        sortPanel.add(sortButton);

        // --- SỬA LẠI LAYOUT filterBar ---
        JPanel filterBar = new JPanel(new BorderLayout(5, 0)); // Dùng lại BorderLayout
        filterBar.add(filterPanel, BorderLayout.CENTER); // Đặt filter controls vào CENTER
        filterBar.add(sortPanel, BorderLayout.EAST);   // Đặt sort vào EAST
        // --- KẾT THÚC SỬA ---
        northPanel.add(filterBar, BorderLayout.CENTER);

        mainBrowsePanel.add(northPanel, BorderLayout.NORTH);

        // 4. (CENTER) Panel chứa danh sách sách
        bookListContainerPanel = new JPanel(new BorderLayout());
        mainBrowsePanel.add(bookListContainerPanel, BorderLayout.CENTER);

        // 5. (SOUTH) Panel phân trang
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("< Trang trước");
        JButton nextButton = new JButton("Trang sau >");
        paginationLabel = new JLabel("Trang 1 / 1");
        paginationPanel.add(prevButton);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(paginationLabel);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(nextButton);
        mainBrowsePanel.add(paginationPanel, BorderLayout.SOUTH);

        // 6. Gán sự kiện (ĐÃ CẬP NHẬT)
        findButton.addActionListener(e -> {
            String typedYear = yearFilterField.getText().trim();
            String selectedYear = (String) yearFilterComboBox.getSelectedItem();

            currentFilterType = "YEAR"; // <-- Đảm bảo đúng Type
            if (!typedYear.isEmpty()) {
                currentFilterValue = typedYear;
                yearFilterComboBox.setSelectedIndex(0);
            } else if (selectedYear != null && !"Tất cả".equals(selectedYear)) {
                currentFilterValue = selectedYear;
            } else {
                currentFilterValue = "";
            }
            currentBookPage = 1;
            updateBookListView();
        });

        viewAllButton.addActionListener(e -> {
            yearFilterField.setText("");
            yearFilterComboBox.setSelectedIndex(0);
            currentFilterType = "YEAR"; // <-- Đảm bảo đúng Type
            currentFilterValue = "";
            currentBookPage = 1;
            updateBookListView();
        });

        yearFilterComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                yearFilterField.setText("");
                String selectedYear = (String) yearFilterComboBox.getSelectedItem();
                currentFilterType = "YEAR"; // <-- Đảm bảo đúng Type
                if (selectedYear != null && !"Tất cả".equals(selectedYear)) {
                    currentFilterValue = selectedYear;
                } else {
                    currentFilterValue = "";
                }
                currentBookPage = 1;
                updateBookListView();
            }
        });

        prevButton.addActionListener(e -> { if (currentBookPage > 1) { currentBookPage--; updateBookListView(); } });
        nextButton.addActionListener(e -> {
            int totalItems = sachDAO.getSachCount(currentFilterType, currentFilterValue);
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_LIST_PAGE);
            if (currentBookPage < totalPages) { currentBookPage++; updateBookListView(); }
        });
        sortButton.addActionListener(e -> showSortDialog());

        // 7. Thay thế giao diện trung tâm
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) mainContentPanel.remove(centerComponent);
        mainContentPanel.add(mainBrowsePanel, BorderLayout.CENTER);
        lastActiveView = mainBrowsePanel;
        mainContentPanel.revalidate();
        mainContentPanel.repaint();

        // 8. Tải dữ liệu lần đầu
        updateBookListView();
    }

    /**
     * (ĐÃ CẬP NHẬT) Hiển thị giao diện "Duyệt theo nhan đề"
     */
    private void showBrowseByTitleView() {
        // 1. Reset trạng thái
        currentBookPage = 1;
        currentSortBy = "tenSach"; // Sắp xếp theo tên
        currentSortOrder = "ASC";
        currentFilterType = "TITLE_PREFIX"; // <-- Sửa
        currentFilterValue = "";          // <-- Sửa

        // 2. Tạo Panel chính
        JPanel mainBrowsePanel = new JPanel(new BorderLayout(10, 10));
        mainBrowsePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 3. (NORTH) Panel Tiêu đề và Lọc
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        JLabel title = new JLabel("Tìm kiếm theo: Nhan đề");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(51, 51, 51));
        northPanel.add(title, BorderLayout.NORTH);

        // --- GIAO DIỆN LỌC (0-9, A-Z) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        filterPanel.add(new JLabel("Chọn chữ cái/số:"));

        // --- SỬA CÁCH TẠO MẢNG "letters" ---
        // 1 (Tất cả) + 10 (Digits) + 26 (Letters) = 37 items
        String[] letters = new String[37];
        letters[0] = "Tất cả";
        // Thêm 0-9
        for (int i = 0; i < 10; i++) {
            letters[i + 1] = String.valueOf(i);
        }
        // Thêm A-Z
        for (int i = 0; i < 26; i++) {
            letters[i + 11] = String.valueOf((char)('A' + i));
        }
        // --- KẾT THÚC SỬA ---

        titleFilterComboBox = new JComboBox<>(letters);
        filterPanel.add(titleFilterComboBox);

        filterPanel.add(new JLabel(" hoặc nhập:"));
        titleFilterField = new JTextField(10);
        filterPanel.add(titleFilterField);
        JButton findButton = new JButton("Tìm ");
        JButton viewAllButton = new JButton("Xem tất cả");
        filterPanel.add(findButton);
        filterPanel.add(viewAllButton);

        JButton sortButton = new JButton("⚙️ Sắp xếp");
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Giảm gap
        sortPanel.add(sortButton);

        // --- SỬA LẠI LAYOUT filterBar ---
        JPanel filterBar = new JPanel(new BorderLayout(5, 0)); // Dùng lại BorderLayout
        filterBar.add(filterPanel, BorderLayout.CENTER); // Đặt filter controls vào CENTER
        filterBar.add(sortPanel, BorderLayout.EAST);   // Đặt sort vào EAST
        // --- KẾT THÚC SỬA ---
        northPanel.add(filterBar, BorderLayout.CENTER);

        mainBrowsePanel.add(northPanel, BorderLayout.NORTH);

        // 4. (CENTER) Panel chứa danh sách sách
        bookListContainerPanel = new JPanel(new BorderLayout());
        mainBrowsePanel.add(bookListContainerPanel, BorderLayout.CENTER);

        // 5. (SOUTH) Panel phân trang (Giống hệt)
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("< Trang trước");
        JButton nextButton = new JButton("Trang sau >");
        paginationLabel = new JLabel("Trang 1 / 1");
        paginationPanel.add(prevButton);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(paginationLabel);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(nextButton);
        mainBrowsePanel.add(paginationPanel, BorderLayout.SOUTH);

        // 6. Gán sự kiện
        findButton.addActionListener(e -> {
            String typedTitle = titleFilterField.getText().trim();
            String selectedLetter = (String) titleFilterComboBox.getSelectedItem();

            currentFilterType = "TITLE_PREFIX"; // <-- Đảm bảo đúng Type
            if (!typedTitle.isEmpty()) {
                currentFilterValue = typedTitle;
                titleFilterComboBox.setSelectedIndex(0);
            } else if (selectedLetter != null && !"Tất cả".equals(selectedLetter)) {
                currentFilterValue = selectedLetter;
            } else {
                currentFilterValue = "";
            }
            currentBookPage = 1;
            updateBookListView();
        });

        viewAllButton.addActionListener(e -> {
            titleFilterField.setText("");
            titleFilterComboBox.setSelectedIndex(0);
            currentFilterType = "TITLE_PREFIX"; // <-- Đảm bảo đúng Type
            currentFilterValue = "";
            currentBookPage = 1;
            updateBookListView();
        });

        titleFilterComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                titleFilterField.setText("");
                String selectedLetter = (String) titleFilterComboBox.getSelectedItem();
                currentFilterType = "TITLE_PREFIX"; // <-- Đảm bảo đúng Type
                if (selectedLetter != null && !"Tất cả".equals(selectedLetter)) {
                    currentFilterValue = selectedLetter;
                } else {
                    currentFilterValue = "";
                }
                currentBookPage = 1;
                updateBookListView();
            }
        });

        prevButton.addActionListener(e -> { if (currentBookPage > 1) { currentBookPage--; updateBookListView(); } });
        nextButton.addActionListener(e -> {
            int totalItems = sachDAO.getSachCount(currentFilterType, currentFilterValue);
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_LIST_PAGE);
            if (currentBookPage < totalPages) { currentBookPage++; updateBookListView(); }
        });
        sortButton.addActionListener(e -> showSortDialog());

        // 7. Thay thế giao diện trung tâm
        Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) mainContentPanel.remove(centerComponent);
        mainContentPanel.add(mainBrowsePanel, BorderLayout.CENTER);
        lastActiveView = mainBrowsePanel;
        mainContentPanel.revalidate();
        mainContentPanel.repaint();

        // 8. Tải dữ liệu lần đầu
        updateBookListView();
    }


    /**
     * (ĐÃ CẬP NHẬT) Cập nhật danh sách sách (dùng biến trạng thái tổng quát)
     */
    private void updateBookListView() {
        // 1. Lấy tổng số mục và tính tổng số trang
        int totalItems = sachDAO.getSachCount(currentFilterType, currentFilterValue);
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_LIST_PAGE);
        if (totalPages == 0) totalPages = 1;

        // 2. Sửa lại trang hiện tại nếu nó vượt quá
        if (currentBookPage > totalPages) currentBookPage = totalPages;
        if (currentBookPage < 1) currentBookPage = 1;

        // 3. Cập nhật nhãn phân trang
        paginationLabel.setText("Trang " + currentBookPage + " / " + totalPages);

        // 4. Lấy dữ liệu sách (dùng hàm DAO tổng quát)
        List<Sach> books = sachDAO.getSachPaginated(currentBookPage, ITEMS_PER_LIST_PAGE, currentSortBy, currentSortOrder, currentFilterType, currentFilterValue);

        // 5. Tạo panel danh sách sách mới
        JScrollPane bookListScrollPane = createBookListViewPanel(books);

        // 6. Cập nhật giao diện
        bookListContainerPanel.removeAll();
        bookListContainerPanel.add(bookListScrollPane, BorderLayout.CENTER);
        bookListContainerPanel.revalidate();
        bookListContainerPanel.repaint();
    }

    /**
     * Tạo JScrollPane chứa danh sách các sách (giống kiểu Hồ sơ tác giả)
     */
    private JScrollPane createBookListViewPanel(List<Sach> bookList) {
        if (bookList == null || bookList.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.add(new JLabel("Không tìm thấy sách nào."));
            return new JScrollPane(emptyPanel);
        }

        JPanel bookItemsPanel = new JPanel();
        bookItemsPanel.setLayout(new BoxLayout(bookItemsPanel, BoxLayout.Y_AXIS));
        bookItemsPanel.setBackground(Color.WHITE);

        for (int i = 0; i < bookList.size(); i++) {
            Sach sach = bookList.get(i);
            // Tận dụng hàm createAuthorBookEntryPanel đã có
            JPanel bookEntryPanel = createAuthorBookEntryPanel(sach);
            bookEntryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bookItemsPanel.add(bookEntryPanel);

            if (i < bookList.size() - 1) {
                bookItemsPanel.add(Box.createRigidArea(new Dimension(0, 1)));
                JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                bookItemsPanel.add(sep);
                bookItemsPanel.add(Box.createRigidArea(new Dimension(0, 1)));
            }
        }

        bookItemsPanel.add(Box.createVerticalGlue());

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(bookItemsPanel, BorderLayout.NORTH);

        JScrollPane listScrollPane = new JScrollPane(wrapperPanel);
        listScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return listScrollPane;
    }

    /**
     * Hiển thị hộp thoại (Dialog) cho phép chọn cách sắp xếp
     */
    private void showSortDialog() {
        // 1. Tạo các components cho dialog
        JComboBox<String> cmbSortBy = new JComboBox<>(new String[]{"Năm xuất bản", "Nhan đề (Tên sách)"});
        JComboBox<String> cmbSortOrder = new JComboBox<>(new String[]{"Tăng dần (ASC)", "Giảm dần (DESC)"});

        // 2. Đặt giá trị mặc định theo trạng thái hiện tại
        if ("namXuatBan".equals(currentSortBy)) {
            cmbSortBy.setSelectedIndex(0);
        } else {
            cmbSortBy.setSelectedIndex(1);
        }
        if ("DESC".equals(currentSortOrder)) {
            cmbSortOrder.setSelectedIndex(1);
        } else {
            cmbSortOrder.setSelectedIndex(0);
        }

        // 3. Tạo panel chứa các component
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Sắp xếp theo:"));
        panel.add(cmbSortBy);
        panel.add(new JLabel("Thứ tự:"));
        panel.add(cmbSortOrder);

        // 4. Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "Tùy chọn sắp xếp",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // 5. Xử lý kết quả
        if (result == JOptionPane.OK_OPTION) {
            // Lấy giá trị mới
            if (cmbSortBy.getSelectedIndex() == 0) {
                currentSortBy = "namXuatBan";
            } else {
                currentSortBy = "tenSach";
            }

            if (cmbSortOrder.getSelectedIndex() == 0) {
                currentSortOrder = "ASC";
            } else {
                currentSortOrder = "DESC";
            }

            // Reset về trang 1 và cập nhật
            currentBookPage = 1;
            updateBookListView();
        }
    }

    public static void main(String[] args) {
        // Giữ nguyên main này nếu muốn Trang Chủ chạy đầu tiên
        // Xóa đi nếu muốn LoginForm chạy đầu tiên
        SwingUtilities.invokeLater(() -> new LibraryHomePageView());
    }
}
