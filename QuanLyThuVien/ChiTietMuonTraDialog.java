package QuanLyThuVien;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

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

    public ChiTietMuonTraDialog(JFrame parent, List<MuonTra> phieuMuonGroup) {
        super(parent, "Chi Tiết Phiếu Mượn", true);

        this.phieuMuonGroup = phieuMuonGroup;
        this.sharedMT = phieuMuonGroup.get(0);
        this.docGia = sharedMT.getDocGia();

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

        // Nút Đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.addActionListener(e -> dispose());
        buttonPanel.add(btnDong);
        add(buttonPanel, BorderLayout.SOUTH);

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
        sachMuonTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sachMuonTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

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
        lblNgayTao.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNgayTao.setForeground(Color.DARK_GRAY);
        footerLabels.add(lblNgayTao, gbc);

        gbc.gridy = 1;
        JLabel lblNguoiTao = new JLabel("Người tạo phiếu: admin");
        lblNguoiTao.setFont(new Font("Segoe UI", Font.ITALIC, 12));
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
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );
        return panel;
    }

    private JTextField createDisabledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setEnabled(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setDisabledTextColor(Color.BLACK);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEtchedBorder());
        return field;
    }

    // Điền dữ liệu
    private void populateData() {
        txtMaDG.setText(docGia.getMaDocGia());
        txtHoTen.setText(docGia.getHoTen());
        txtEmail.setText(docGia.getEmail());
        txtSdt.setText(docGia.getSdt());

        txtNgayMuon.setText(sdf.format(sharedMT.getNgayMuon()));
        txtNgayHenTra.setText(sdf.format(sharedMT.getNgayHenTra()));
        txtLoaiMuon.setText(sharedMT.getLoaiMuon());

        long total = phieuMuonGroup.size();
        long returned = phieuMuonGroup.stream().filter(mt -> mt.getNgayTraThucTe() != null).count();
        String status;
        if (returned == total) {
            status = "Đã trả (Toàn bộ)";
        } else if (returned > 0) {
            status = "Đang trả (" + returned + "/" + total + ")";
        } else {
            status = sharedMT.getTrangThai();
        }
        txtTrangThai.setText(status);

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
