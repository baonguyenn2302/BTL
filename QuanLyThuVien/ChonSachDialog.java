package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // <<< NEW
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JComboBox; // <<< NEW
import javax.swing.JLabel; // <<< NEW
import javax.swing.JCheckBox; // <<< NEW
import javax.swing.BoxLayout; // <<< NEW
import javax.swing.ListCellRenderer; // <<< NEW
import java.awt.Component; // <<< NEW
import java.awt.Color; // <<< NEW
import java.awt.Font; // <<< NEW
import java.awt.event.MouseAdapter; // <<< NEW
import java.awt.event.MouseEvent; // <<< NEW

/**
 * Dialog (cửa sổ pop-up) để chọn nhiều Sách (Đã nâng cấp với Checkbox và Lọc).
 */
public class ChonSachDialog extends JDialog {

    private JList<Sach> listSach;
    private DefaultListModel<Sach> listModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cbSearchType; // <<< NEW
    private JButton btnTimKiem, btnXemTatCa; // <<< NEW
    private JButton btnOK, btnHuy;
    
    private boolean confirmed = false;
    private SachDAO sachDAO;
    private List<Sach> allSach; // Giữ danh sách đầy đủ để build kết quả
    private List<Sach> selectedSach = new ArrayList<>();
    
    // Dùng Set để quản lý trạng thái chọn (giữ trạng thái khi lọc)
    private Set<String> selectedMaSachSet; // <<< NEW

    public ChonSachDialog(JFrame parent, SachDAO sachDAO, List<Sach> currentSelection) {
        super(parent, "Chọn Sách Thêm vào Bộ Sưu Tập", true);
        
        this.sachDAO = sachDAO;
        
        // (NEW) Khởi tạo Set chứa các mã đã chọn
        this.selectedMaSachSet = currentSelection.stream()
                                          .map(Sach::getMaSach)
                                          .collect(Collectors.toSet());
        
        setSize(550, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- (NEW) Thanh tìm kiếm nâng cao ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        // Hỗ trợ tìm kiếm theo các kiểu mà SachDAO.timKiemSachNangCao hỗ trợ
        String[] searchTypes = {"Tất cả", "Nhan đề", "Tác giả", "Năm xuất bản", "Mã sách"};
        cbSearchType = new JComboBox<>(searchTypes);
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm");
        btnXemTatCa = new JButton("Xem tất cả");
        
        searchPanel.add(new JLabel("Tìm theo:"));
        searchPanel.add(cbSearchType);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(btnXemTatCa);
        add(searchPanel, BorderLayout.NORTH);

        // --- (UPDATED) Danh sách ---
        listModel = new DefaultListModel<>();
        listSach = new JList<>(listModel);
        
        // (NEW) Dùng Renderer tùy chỉnh để hiển thị Checkbox và thông tin Sách
        listSach.setCellRenderer(new SachCheckboxRenderer(this.selectedMaSachSet));
        listSach.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(listSach);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Tải dữ liệu ban đầu
        loadAllSach();

        // --- Nút bấm ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnOK = new JButton("OK");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnOK);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- (UPDATED) Sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        
        btnOK.addActionListener(e -> {
            // Lọc danh sách 'allSach' dựa trên 'selectedMaSachSet'
            // Điều này đảm bảo chúng ta trả về đối tượng Sach đầy đủ
            this.selectedSach = allSach.stream()
                .filter(s -> selectedMaSachSet.contains(s.getMaSach()))
                .collect(Collectors.toList());
                
            this.confirmed = true;
            dispose();
        });

        // (NEW) Sự kiện cho các nút tìm kiếm
        btnTimKiem.addActionListener(e -> filterSach());
        btnXemTatCa.addActionListener(e -> loadAllSach());

        // (NEW) Sự kiện click chuột để chọn/bỏ chọn checkbox
        listSach.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listSach.locationToIndex(e.getPoint());
                if (index != -1) {
                    Sach s = listModel.getElementAt(index);
                    String maSach = s.getMaSach();
                    
                    // Đảo ngược trạng thái trong Set
                    if (selectedMaSachSet.contains(maSach)) {
                        selectedMaSachSet.remove(maSach);
                    } else {
                        selectedMaSachSet.add(maSach);
                    }
                    
                    // Vẽ lại item đó để cập nhật checkbox
                    listSach.repaint(listSach.getCellBounds(index, index));
                }
            }
        });
    }
    
    /**
     * Tải TOÀN BỘ sách và hiển thị
     */
    private void loadAllSach() {
        this.allSach = sachDAO.getAllSach(); // Lấy tất cả sách
        listModel.clear();
        for (Sach s : allSach) {
            listModel.addElement(s);
        }
    }
    
    /**
     * Lọc danh sách sách dựa trên DAO
     */
    private void filterSach() {
        String keyword = txtTimKiem.getText().trim();
        String searchType = (String) cbSearchType.getSelectedItem();

        // Gọi hàm tìm kiếm nâng cao của DAO
        List<Sach> results = sachDAO.timKiemSachNangCao(keyword, searchType);
        
        listModel.clear();
        for (Sach s : results) {
            listModel.addElement(s);
        }
    }

    /**
     * (DEPRECATED) Hàm preselectSach không còn cần thiết,
     * vì logic đã được chuyển vào constructor.
     */
    // private void preselectSach(List<Sach> currentSelection) { ... }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Sach> getSelectedSach() {
        return selectedSach;
    }
    
    // --- (NEW) Inner class để vẽ JCheckBox và thông tin Sách ---
    
    // --- (NEW) Inner class để vẽ JCheckBox và thông tin Sách ---
    
    class SachCheckboxRenderer extends JPanel implements ListCellRenderer<Sach> {
        private JCheckBox checkBox;
        private JLabel lblTenSach;
        private JLabel lblTacGia;
        private JLabel lblMaSach;
        private Set<String> selectedMaSet;
        
        // <<< SỬA LỖI: Di chuyển infoPanel ra đây làm trường (field)
        private JPanel infoPanel; 
        
        public SachCheckboxRenderer(Set<String> selectedMaSet) {
            this.selectedMaSet = selectedMaSet;
            setLayout(new BorderLayout(10, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            checkBox = new JCheckBox();
            
            // Panel chứa thông tin
            // <<< SỬA LỖI: Bỏ 'JPanel' ở đầu dòng này
            infoPanel = new JPanel(); 
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            
            lblTenSach = new JLabel();
            lblTenSach.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            lblTacGia = new JLabel();
            lblTacGia.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            
            lblMaSach = new JLabel();
            lblMaSach.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblMaSach.setForeground(Color.GRAY);
            
            infoPanel.add(lblTenSach);
            infoPanel.add(lblTacGia);
            infoPanel.add(lblMaSach);
            
            add(checkBox, BorderLayout.WEST);
            add(infoPanel, BorderLayout.CENTER);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Sach> list, Sach value, 
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            
            // Điền thông tin
            lblTenSach.setText(value.getTenSach());
            lblTacGia.setText("Tác giả: " + value.getTenTacGiaDisplay()); //
            lblMaSach.setText("Mã: " + value.getMaSach() + " | SL: " + value.getSoLuong());
            
            // Đặt trạng thái checkbox
            checkBox.setSelected(selectedMaSet.contains(value.getMaSach()));
            
            // Xử lý màu sắc khi được JList highlight (nhấn giữ chuột)
            // <<< SỬA LỖI: Thêm infoPanel.setBackground/setForeground
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                infoPanel.setBackground(list.getSelectionBackground());
                infoPanel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                infoPanel.setBackground(list.getBackground());
                infoPanel.setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setOpaque(true);
            
            return this;
        }
    }
}
