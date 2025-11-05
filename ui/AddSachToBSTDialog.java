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
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class AddSachToBSTDialog extends JDialog {

    private int maBoSuuTap;
    private BoSuuTapDAO bstDAO;
    private SachDAO sachDAO; // Dùng để tìm kiếm sách

    private JTable sachAvailableTable;
    private DefaultTableModel sachAvailableTableModel;
    private JComboBox<String> cbSachSearchMode;
    private JTextField txtSachSearchValue;
    
    // Cờ để MainFrame biết có cần tải lại JTable hay không
    private boolean dataChanged = false; 

    public AddSachToBSTDialog(JFrame parent, int maBoSuuTap) {
        super(parent, "Thêm Sách vào Bộ Sưu Tập", true);
        this.maBoSuuTap = maBoSuuTap;
        this.bstDAO = new BoSuuTapDAO();
        this.sachDAO = new SachDAO();

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel Tìm kiếm ở trên
        add(createSearchPanel(), BorderLayout.NORTH);

        // 2. Bảng Sách (kết quả) ở giữa
        String[] columns = {"Mã Sách", "Tên Sách", "Tác giả", "Năm XB"};
        sachAvailableTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sachAvailableTable = new JTable(sachAvailableTableModel);
        sachAvailableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sachAvailableTable.setFont(new Font("Arial", Font.PLAIN, 12));
        add(new JScrollPane(sachAvailableTable), BorderLayout.CENTER);

        // 3. Panel Nút bấm ở dưới
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Tải tất cả sách ban đầu
        loadAllSachData();
    }

    // Panel 1: Thanh Tìm kiếm (giống MainFrame)
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        searchPanel.add(new JLabel("Tìm theo:"));
        String[] searchModes = {"Tên sách", "Mã sách", "Tác giả", "Năm xuất bản"};
        cbSachSearchMode = new JComboBox<>(searchModes);
        searchPanel.add(cbSachSearchMode);

        txtSachSearchValue = new JTextField(20);
        searchPanel.add(txtSachSearchValue);
        
        JButton btnSearch = new JButton("Tìm");
        JButton btnViewAll = new JButton("Xem tất cả");
        
        searchPanel.add(btnSearch);
        searchPanel.add(btnViewAll);
        
        // Sự kiện
        btnSearch.addActionListener(e -> searchSachData());
        btnViewAll.addActionListener(e -> loadAllSachData());

        return searchPanel;
    }

    // Panel 3: Nút bấm
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnAdd = new JButton("Thêm sách đã chọn");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(btnAdd);
        panel.add(btnClose);
        
        // Sự kiện
        btnClose.addActionListener(e -> dispose());
        btnAdd.addActionListener(e -> addSachToBST());
        
        return panel;
    }

    // Logic 1: Tải tất cả sách
    private void loadAllSachData() {
        List<Sach> danhSach = sachDAO.getAllSach();
        renderSachTable(danhSach);
        txtSachSearchValue.setText("");
    }
    
    // Logic 2: Tải sách theo tìm kiếm
    private void searchSachData() {
        String mode = cbSachSearchMode.getSelectedItem().toString();
        String value = txtSachSearchValue.getText().trim();
        
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị tìm kiếm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Sach> danhSach = sachDAO.searchSach(mode, value);
        renderSachTable(danhSach);
        
        if (danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Logic 3: Hiển thị (Helper)
    private void renderSachTable(List<Sach> danhSach) {
        sachAvailableTableModel.setRowCount(0);
        for (Sach s : danhSach) {
            sachAvailableTableModel.addRow(new Object[]{
                s.getMaSach(),
                s.getTenSach(),
                s.getTenTacGiaDisplay(),
                s.getNamXuatBan() > 0 ? s.getNamXuatBan() : "N/A"
            });
        }
    }

    // Logic 4: Thêm vào CSDL
    private void addSachToBST() {
        int selectedRow = sachAvailableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ bảng để thêm.", "Chưa chọn sách", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maSach = sachAvailableTableModel.getValueAt(selectedRow, 0).toString();
        String tenSach = sachAvailableTableModel.getValueAt(selectedRow, 1).toString();

        // Gọi DAO
        boolean success = bstDAO.addSachToBoSuuTap(this.maBoSuuTap, maSach);
        
        if (success) {
            this.dataChanged = true; // Đánh dấu là đã thay đổi
            JOptionPane.showMessageDialog(this, "Đã thêm sách '" + tenSach + "' vào bộ sưu tập!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // (Không đóng dialog, để người dùng có thể thêm sách khác)
        } else {
            // Lỗi này thường là do sách đã tồn tại
            JOptionPane.showMessageDialog(this, "Thêm sách thất bại. Có thể sách này đã có trong bộ sưu tập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Getter để MainFrame biết có cần tải lại không
    public boolean isDataChanged() {
        return dataChanged;
    }
}