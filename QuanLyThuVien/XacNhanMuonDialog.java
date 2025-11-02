// TẠO FILE MỚI: XacNhanMuonDialog.java
package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class XacNhanMuonDialog extends JDialog {

    // Thông tin độc giả
    private JTextField txtMaDG, txtHoTen, txtEmail, txtSdt, txtSoSach;
    
    // Thông tin mượn
    private JTable sachMuonTable;
    private JRadioButton rbTaiCho, rbVeNha;
    private JTextField txtNgayMuon, txtNgayHenTra;
    private JButton btnXacNhan, btnHuy;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    // Dữ liệu truyền vào
    private DocGia docGia;
    private DefaultTableModel sachDaChonModel;
    
    private MuonTraDAO muonTraDAO;
    private SachDAO sachDAO;
    
    private boolean saveSuccess = false; // Cờ (flag)

    public XacNhanMuonDialog(JFrame parent, DocGia docGia, DefaultTableModel sachDaChonModel) {
        super(parent, "Xác Nhận Phiếu Mượn", true);
        this.docGia = docGia;
        this.sachDaChonModel = sachDaChonModel;
        this.muonTraDAO = new MuonTraDAO();
        this.sachDAO = new SachDAO();
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel thông tin độc giả
        add(createNguoiMuonPanel(), BorderLayout.NORTH);
        
        // 2. Panel danh sách sách
        add(createSachMuonPanel(), BorderLayout.CENTER);
        
        // 3. Panel chi tiết mượn (loại mượn, ngày)
        add(createChiTietMuonPanel(), BorderLayout.SOUTH);
        
        // Điền dữ liệu
        populateData();
        
        // Gắn sự kiện
        addEventHandlers();
    }
    
    private JPanel createNguoiMuonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Thông tin độc giả", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
        
        // Hàng 0: Mã ĐG
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã thành viên:"), gbc);
        gbc.gridx = 1;
        txtMaDG = createDisabledField();
        panel.add(txtMaDG, gbc);
        
        // Hàng 0: Số sách
        gbc.gridx = 2;
        panel.add(new JLabel("Số sách mượn:"), gbc);
        gbc.gridx = 3;
        txtSoSach = createDisabledField();
        txtSoSach.setFont(boldFont);
        txtSoSach.setForeground(Color.RED);
        panel.add(txtSoSach, gbc);

        // Hàng 1: Họ Tên
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; // Kéo dài
        txtHoTen = createDisabledField();
        txtHoTen.setFont(boldFont);
        panel.add(txtHoTen, gbc);

        // Hàng 2: Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createDisabledField();
        panel.add(txtEmail, gbc);
        
        // Hàng 2: SĐT
        gbc.gridx = 2;
        panel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 3;
        txtSdt = createDisabledField();
        panel.add(txtSdt, gbc);
        
        return panel;
    }
    
    private JScrollPane createSachMuonPanel() {
        // Chúng ta tạo 1 JTable mới và sao chép model
        // để tránh ảnh hưởng đến JTable gốc trong MainFrame
        sachMuonTable = new JTable(sachDaChonModel);
        sachMuonTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sachMuonTable.setRowHeight(25);
        sachMuonTable.setEnabled(false); // Không cho sửa
        
        JScrollPane scrollPane = new JScrollPane(sachMuonTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Danh sách sách đã chọn", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 102, 153))
        );
        return scrollPane;
    }
    
    private JPanel createChiTietMuonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Panel cho Loại mượn và Ngày
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Loại mượn
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Loại mượn:"), gbc);
        
        rbTaiCho = new JRadioButton("Mượn tại chỗ");
        rbVeNha = new JRadioButton("Mượn về nhà", true); // Mặc định
        ButtonGroup group = new ButtonGroup();
        group.add(rbTaiCho);
        group.add(rbVeNha);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(rbVeNha);
        radioPanel.add(rbTaiCho);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        detailsPanel.add(radioPanel, gbc);
        
        // Ngày mượn
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        detailsPanel.add(new JLabel("Ngày mượn:"), gbc);
        gbc.gridx = 1;
        txtNgayMuon = createDisabledField();
        detailsPanel.add(txtNgayMuon, gbc);
        
        // Ngày hẹn trả
        gbc.gridx = 2; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Ngày hẹn trả:"), gbc);
        gbc.gridx = 3;
        txtNgayHenTra = new JTextField(15); // Sẽ được tính toán
        txtNgayHenTra.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(txtNgayHenTra, gbc);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Panel cho Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnXacNhan = new JButton("Xác Nhận Mượn");
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setBackground(new Color(30, 144, 255));
        btnXacNhan.setForeground(Color.WHITE);

        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(108, 117, 125));
        btnHuy.setForeground(Color.WHITE);
        
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Helper để tạo ô text bị vô hiệu hóa
    private JTextField createDisabledField() {
        JTextField field = new JTextField(20);
        field.setEnabled(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setDisabledTextColor(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEtchedBorder());
        return field;
    }

    // Điền dữ liệu ban đầu
    private void populateData() {
        // 1. Thông tin độc giả
        txtMaDG.setText(docGia.getMaDocGia());
        txtHoTen.setText(docGia.getHoTen());
        txtEmail.setText(docGia.getEmail());
        txtSdt.setText(docGia.getSdt());
        txtSoSach.setText(String.valueOf(sachDaChonModel.getRowCount()));
        
        // 2. Ngày mượn (bây giờ)
        txtNgayMuon.setText(sdf.format(new Date()));
        
        // 3. Tính ngày trả (mặc định là "Mượn về nhà")
        tinhNgayHenTra();
    }
    
    // Logic tính ngày hẹn trả
    private void tinhNgayHenTra() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // Bắt đầu từ hôm nay
        
        if (rbTaiCho.isSelected()) {
            // Mượn tại chỗ: +1 ngày
            cal.add(Calendar.DAY_OF_YEAR, 1);
            txtNgayHenTra.setText(sdf.format(cal.getTime()));
            txtNgayHenTra.setEnabled(false); // Không cho sửa
            
        } else { // rbVeNha.isSelected()
            // Mượn về nhà: +7 ngày (bạn có thể đổi)
            cal.add(Calendar.DAY_OF_YEAR, 7);
            txtNgayHenTra.setText(sdf.format(cal.getTime()));
            txtNgayHenTra.setEnabled(true); // Cho phép sửa
        }
    }
    
    // Gắn các sự kiện
    private void addEventHandlers() {
        // Nút Hủy
        btnHuy.addActionListener(e -> dispose());
        
        // Radio buttons
        rbTaiCho.addActionListener(e -> tinhNgayHenTra());
        rbVeNha.addActionListener(e -> tinhNgayHenTra());
        
        // Nút Xác Nhận (LOGIC CHÍNH)
        btnXacNhan.addActionListener(e -> {
            try {
                // 1. Lấy ngày mượn và ngày trả
                Date ngayMuon = sdf.parse(txtNgayMuon.getText());
                Date ngayHenTra = sdf.parse(txtNgayHenTra.getText());
                String loaiMuon = rbVeNha.isSelected() ? "Về nhà" : "Tại chỗ";

                // 2. Lấy danh sách sách đầy đủ (cần gọi DAO)
                List<Sach> sachMuonList = new ArrayList<>();
                for (int i = 0; i < sachDaChonModel.getRowCount(); i++) {
                    String maSach = sachDaChonModel.getValueAt(i, 0).toString();
                    Sach s = sachDAO.getSachByMa(maSach);
                    if (s == null) {
                        throw new Exception("Lỗi: Không tìm thấy sách " + maSach);
                    }
                    sachMuonList.add(s);
                }

                // 3. Gọi DAO để lưu
                boolean success = muonTraDAO.themMoiPhieuMuon(this.docGia, sachMuonList, ngayMuon, ngayHenTra, loaiMuon);

                // 4. Đóng form
                if (success) {
                    this.saveSuccess = true;
                    JOptionPane.showMessageDialog(this, "Tạo phiếu mượn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    throw new Exception("Lỗi CSDL khi tạo phiếu mượn.");
                }
                
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày hẹn trả không hợp lệ. Vui lòng dùng dd/MM/yyyy HH:mm", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Getter này để MainFrame biết có thành công hay không
    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}