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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane; // <<< THÊM IMPORT
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
// (Thêm import cho Component nếu IDE của bạn yêu cầu)
import java.awt.Component;
import javax.swing.table.DefaultTableCellRenderer;


public class ChiTietMuonTraDialog extends JDialog {

    private List<MuonTra> phieuMuonGroup;
    private MuonTra sharedMT;
    private DocGia docGia;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Fields
    private JTextField txtMaDG, txtHoTen, txtEmail, txtSdt;
    private JTable sachMuonTable;
    private DefaultTableModel sachMuonTableModel;
    private JTextField txtNgayMuon, txtNgayHenTra, txtTrangThai, txtLoaiMuon;

    // <<< BIẾN MỚI >>>
    private DocGiaDAO docGiaDAO;
    private JButton btnKhoaTaiKhoan;
    private boolean dataChanged = false; // Báo cho MainFrame biết
    
    // <<< CONSTRUCTOR ĐÃ SỬA: Thêm DocGiaDAO >>>
    public ChiTietMuonTraDialog(JFrame parent, List<MuonTra> phieuMuonGroup, DocGiaDAO docGiaDAO) {
        super(parent, "Chi Tiết Phiếu Mượn", true);
        
        this.phieuMuonGroup = phieuMuonGroup;
        this.sharedMT = phieuMuonGroup.get(0);
        this.docGia = sharedMT.getDocGia();
        this.docGiaDAO = docGiaDAO; // <<< GÁN DAO
        
        setTitle("Chi Tiết Phiếu Mượn (Nhóm Giao Dịch " + sharedMT.getMaMuonTra() + ")");
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // 1. Panel độc giả
        gbc.gridy = 0;
        infoPanel.add(createNguoiMuonPanel(), gbc);

        // 2. Panel mượn
        gbc.gridy = 1;
        infoPanel.add(createChiTietMuonPanel(), gbc);

        // 3. Panel sách (JTable)
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        infoPanel.add(createSachMuonPanel(), gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // 4. Panel Footer
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // ==================================================
        // <<< PANEL NÚT BẤM (SOUTH) ĐÃ SỬA LẠI >>>
        // ==================================================
        JPanel southPanel = new JPanel(new BorderLayout()); // Dùng BorderLayout
        
        // Nút Khóa (Bên trái)
        btnKhoaTaiKhoan = new JButton("Khóa Tài Khoản (Do Quá Hạn)");
        btnKhoaTaiKhoan.setFont(new Font("Arial", Font.BOLD, 12));
        btnKhoaTaiKhoan.setBackground(new Color(220, 53, 69)); // Màu đỏ
        btnKhoaTaiKhoan.setForeground(Color.WHITE);
        btnKhoaTaiKhoan.setVisible(false); // Ẩn ban đầu
        
        JPanel khoaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        khoaPanel.add(btnKhoaTaiKhoan);
        southPanel.add(khoaPanel, BorderLayout.WEST);

        // Nút Đóng (Bên phải)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 14)); // Sửa font
        btnDong.addActionListener(e -> dispose());
        buttonPanel.add(btnDong);
        southPanel.add(buttonPanel, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
        // ==================================================
        
        // Gắn sự kiện (MỚI)
        addKhoaButtonHandler();
        
        // Điền dữ liệu
        populateData();
    }

    // Panel 1: Thông tin độc giả
    private JPanel createNguoiMuonPanel() {
        JPanel panel = createTitledPanel("Thông tin độc giả");
        GridBagConstraints gbc = getDefaultGBC();

        panel.add(new JLabel("Mã ĐG:"), gbc);
        gbc.gridx = 1; txtMaDG = createDisabledField(10); panel.add(txtMaDG, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; txtHoTen = createDisabledField(20); panel.add(txtHoTen, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtEmail = createDisabledField(30); panel.add(txtEmail, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtSdt = createDisabledField(30); panel.add(txtSdt, gbc);

        return panel;
    }

    // Panel 2: Danh sách sách mượn (JTable)
    private JScrollPane createSachMuonPanel() {
        String[] columns = {"Mã Sách", "Tên Sách", "Ngày Trả", "Trạng Thái"};
        sachMuonTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sachMuonTable = new JTable(sachMuonTableModel);
        sachMuonTable.setRowHeight(25);
        sachMuonTable.setFont(new Font("Arial", Font.PLAIN, 12)); // Sửa font
        sachMuonTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Sửa font
        
        // <<< THÊM RENDERER MÀU (GIỐNG MAINFRAME) >>>
        sachMuonTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                                                                isSelected, hasFocus,
                                                                row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 12));
                if (!isSelected) {
                    String trangThai = table.getValueAt(row, 3).toString(); // Cột 3 là Trạng Thái
                    if (trangThai.equals("Quá hạn")) {
                        c.setForeground(Color.RED); 
                    } else {
                        c.setForeground(Color.BLACK); 
                    }
                } 
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(sachMuonTable);
        scrollPane.setBorder(createTitledPanel("Danh sách sách đã mượn").getBorder());
        return scrollPane;
    }

    // Panel 3: Chi tiết phiếu mượn
    private JPanel createChiTietMuonPanel() {
        JPanel panel = createTitledPanel("Chi tiết phiếu mượn");
        GridBagConstraints gbc = getDefaultGBC();

        panel.add(new JLabel("Ngày mượn:"), gbc);
        gbc.gridx = 1; txtNgayMuon = createDisabledField(15); panel.add(txtNgayMuon, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Ngày hẹn trả:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; txtNgayHenTra = createDisabledField(15); panel.add(txtNgayHenTra, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; txtTrangThai = createDisabledField(15); panel.add(txtTrangThai, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Loại mượn:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; txtLoaiMuon = createDisabledField(15); panel.add(txtLoaiMuon, gbc);

        return panel;
    }

    // Panel 4: Footer
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);

        JPanel footerLabels = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNgayTao = new JLabel("Ngày tạo phiếu: " + sdf.format(sharedMT.getNgayMuon()));
        lblNgayTao.setFont(new Font("Arial", Font.ITALIC, 12)); // Sửa font
        lblNgayTao.setForeground(Color.DARK_GRAY);
        footerLabels.add(lblNgayTao, gbc);

        gbc.gridy = 1;
        String tenNguoiTao = "N/A";
        // (sharedMT đã được gán 'nguoiTao' từ MuonTraDAO.getPhieuMuonGroup)
        if (sharedMT.getNguoiTao() != null && sharedMT.getNguoiTao().getTenNguoiDung() != null) {
            tenNguoiTao = sharedMT.getNguoiTao().getTenNguoiDung();
        }
        
        JLabel lblNguoiTao = new JLabel("Người tạo phiếu: " + tenNguoiTao);
        
        lblNguoiTao.setFont(new Font("Arial", Font.ITALIC, 12)); // Sửa font
        lblNguoiTao.setForeground(Color.DARK_GRAY);
        footerLabels.add(lblNguoiTao, gbc);

        panel.add(footerLabels, BorderLayout.CENTER);
        return panel;
    }

    // Helper methods
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
            new Font("Arial", Font.BOLD, 12), new Color(0, 102, 153)) // Sửa font
        );
        return panel;
    }

    private JTextField createDisabledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setEnabled(false);
        field.setFont(new Font("Arial", Font.PLAIN, 12)); // Sửa font
        field.setDisabledTextColor(Color.BLACK);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEtchedBorder());
        return field;
    }

    // ==================================================
    // <<< HÀM MỚI: Thêm sự kiện cho nút Khóa >>>
    // ==================================================
    private void addKhoaButtonHandler() {
        btnKhoaTaiKhoan.addActionListener(e -> {
            // Xác nhận
            String confirmMsg = "Bạn có chắc chắn muốn KHÓA tài khoản của độc giả:\n"
                              + docGia.getHoTen() + " (Mã: " + docGia.getMaDocGia() + ")?";
            int choice = JOptionPane.showConfirmDialog(this, confirmMsg, 
                "Xác nhận Khóa tài khoản", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                // Gọi DAO (docGiaDAO đã được truyền vào)
                boolean success = docGiaDAO.setBlockStatus(docGia.getMaDocGia(), true); // true = block
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đã khóa tài khoản thành công.");
                    this.dataChanged = true; // Báo cho MainFrame
                    
                    // Cập nhật UI
                    btnKhoaTaiKhoan.setText("Tài khoản này đã bị khóa");
                    btnKhoaTaiKhoan.setEnabled(false);
                    btnKhoaTaiKhoan.setBackground(Color.GRAY);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể khóa tài khoản.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    // <<< HÀM MỚI: Getter cho dataChanged >>>
    public boolean isDataChanged() {
        return dataChanged;
    }
    
    // ==================================================
    // <<< HÀM POPULATEDATA ĐÃ SỬA >>>
    // ==================================================
    private void populateData() {
        txtMaDG.setText(docGia.getMaDocGia());
        txtHoTen.setText(docGia.getHoTen());
        txtEmail.setText(docGia.getEmail());
        txtSdt.setText(docGia.getSdt());

        txtNgayMuon.setText(sdf.format(sharedMT.getNgayMuon()));
        txtNgayHenTra.setText(sdf.format(sharedMT.getNgayHenTra()));
        txtLoaiMuon.setText(sharedMT.getLoaiMuon());

        // --- LOGIC TÍNH TOÁN TRẠNG THÁI CHUNG VÀ HIỂN THỊ NÚT ---
        
        long total = phieuMuonGroup.size();
        long returned = phieuMuonGroup.stream().filter(mt -> mt.getNgayTraThucTe() != null).count();
        String status;
        
        // 1. Kiểm tra xem có cuốn nào "Quá hạn" không
        boolean isOverdue = phieuMuonGroup.stream()
             .anyMatch(mt -> mt.getTrangThai().equals("Quá hạn"));

        // 2. Tính toán trạng thái chung
        if (returned == total) {
            status = "Đã trả (Toàn bộ)";
        } else if (returned > 0) {
            status = "Đang trả (" + returned + "/" + total + ")";
        } else {
            // Nếu chưa trả cuốn nào, lấy trạng thái của phiếu đầu tiên
            // (sẽ là "Đang mượn" hoặc "Quá hạn")
            status = sharedMT.getTrangThai();
        }
        txtTrangThai.setText(status);

        // 3. Xử lý hiển thị nút Khóa
        if (docGia.isBlocked()) {
            // Nếu đã bị khóa, hiện nút bị vô hiệu hóa
            btnKhoaTaiKhoan.setVisible(true);
            btnKhoaTaiKhoan.setText("Tài khoản này đã bị khóa");
            btnKhoaTaiKhoan.setEnabled(false);
            btnKhoaTaiKhoan.setBackground(Color.GRAY);
        } else if (isOverdue) {
            // Nếu quá hạn VÀ chưa bị khóa -> hiện nút
            btnKhoaTaiKhoan.setVisible(true);
            btnKhoaTaiKhoan.setText("Khóa Tài Khoản (Do Quá Hạn)");
            btnKhoaTaiKhoan.setEnabled(true);
            btnKhoaTaiKhoan.setBackground(new Color(220, 53, 69)); // Màu đỏ
        } else {
            // Nếu không quá hạn -> ẩn nút
            btnKhoaTaiKhoan.setVisible(false);
        }
        
        // (Giữ nguyên logic điền JTable)
        for (MuonTra mt : phieuMuonGroup) {
            String ngayTraStr = (mt.getNgayTraThucTe() != null)
                    ? sdf.format(mt.getNgayTraThucTe())
                    : "N/A";

            sachMuonTableModel.addRow(new Object[]{
                mt.getSach().getMaSach(),
                mt.getSach().getTenSach(),
                ngayTraStr,
                mt.getTrangThai()
            });
        }
    }
}