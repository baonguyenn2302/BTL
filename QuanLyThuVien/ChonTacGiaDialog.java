package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Component; // <<< NEW
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // <<< NEW
import java.util.stream.Collectors; // <<< NEW
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox; // <<< NEW
import javax.swing.JTextField; // <<< NEW
import javax.swing.ListCellRenderer; // <<< NEW
import java.awt.event.KeyAdapter; // <<< NEW
import java.awt.event.KeyEvent; // <<< NEW
import java.awt.event.MouseAdapter; // <<< NEW
import java.awt.event.MouseEvent; // <<< NEW

/**
 * Dialog (cửa sổ pop-up) để chọn nhiều Tác Giả.
 * (Đã nâng cấp: Thêm thanh tìm kiếm và Checkbox)
 */
public class ChonTacGiaDialog extends JDialog {

    private JList<TacGia> listTacGia;
    private DefaultListModel<TacGia> listModel;
    private JTextField txtTimKiem; // <<< NEW
    private JButton btnOK, btnHuy;
    
    private boolean confirmed = false;
    private List<TacGia> allTacGias;
    private List<TacGia> selectedTacGias = new ArrayList<>();
    
    // Dùng Set để quản lý trạng thái chọn (hiệu quả và giữ trạng thái khi lọc)
    private Set<String> selectedMaTacGiaSet; // <<< NEW

    public ChonTacGiaDialog(JDialog parent, TacGiaDAO tacGiaDAO, List<TacGia> currentSelection) {
        super(parent, "Chọn Tác Giả", true);
        
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Lấy danh sách tác giả
        this.allTacGias = tacGiaDAO.getAllTacGia();
        
        // --- (NEW) Khởi tạo Set chứa các mã đã chọn ---
        this.selectedMaTacGiaSet = currentSelection.stream()
                                          .map(TacGia::getMaTacGia)
                                          .collect(Collectors.toSet());

        // --- (NEW) Thanh tìm kiếm ---
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        txtTimKiem = new JTextField("Tìm kiếm tác giả...");
        searchPanel.add(txtTimKiem, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // --- Danh sách ---
        listModel = new DefaultListModel<>();
        listTacGia = new JList<>(listModel);
        
        // (NEW) Tùy chỉnh Renderer để hiển thị Checkbox
        listTacGia.setCellRenderer(new CheckBoxListRenderer(this.selectedMaTacGiaSet));
        
        // (Chúng ta không cần chế độ chọn của JList nữa, nhưng để lại cũng không sao)
        listTacGia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(listTacGia);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

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
            // Lọc danh sách 'allTacGias' dựa trên 'selectedMaTacGiaSet'
            this.selectedTacGias = allTacGias.stream()
                .filter(tg -> selectedMaTacGiaSet.contains(tg.getMaTacGia()))
                .collect(Collectors.toList());
                    
            this.confirmed = true;
            dispose();
        });

        // (NEW) Sự kiện tìm kiếm
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadAndFilterTacGia(txtTimKiem.getText().trim());
            }
        });

        // (NEW) Sự kiện click để toggle (chọn/bỏ chọn)
        listTacGia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listTacGia.locationToIndex(e.getPoint());
                if (index != -1) {
                    TacGia tg = listModel.getElementAt(index);
                    String maTG = tg.getMaTacGia();
                    
                    // Đảo ngược trạng thái trong Set
                    if (selectedMaTacGiaSet.contains(maTG)) {
                        selectedMaTacGiaSet.remove(maTG);
                    } else {
                        selectedMaTacGiaSet.add(maTG);
                    }
                    
                    // Vẽ lại item đó để cập nhật checkbox
                    listTacGia.repaint(listTacGia.getCellBounds(index, index));
                }
            }
        });

        // Tải dữ liệu ban đầu
        loadAndFilterTacGia("");
    }
    
    /**
     * (NEW) Hàm lọc và tải danh sách tác giả
     * Nó sẽ lọc 'allTacGias' dựa trên keyword
     * và KHÔNG làm mất các lựa chọn đã có.
     */
    private void loadAndFilterTacGia(String keyword) {
        listModel.clear();
        String kwLower = keyword.toLowerCase();
        
        for (TacGia tg : allTacGias) {
            // Tìm theo tên hoặc mã
            if (tg.getTenTacGia().toLowerCase().contains(kwLower) || tg.getMaTacGia().toLowerCase().contains(kwLower)) {
                listModel.addElement(tg);
            }
        }
        
        // JList sẽ tự động vẽ lại và renderer sẽ kiểm tra Set để đặt checkbox
    }

    /**
     * (DEPRECATED) Hàm này không cần nữa vì Set đã quản lý
     */
    // private void preselectAuthors(List<TacGia> currentSelection) { ... }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<TacGia> getSelectedTacGias() {
        return selectedTacGias;
    }
    
    // --- (NEW) Inner class để vẽ JCheckBox trong JList ---
    
    class CheckBoxListRenderer extends JCheckBox implements ListCellRenderer<TacGia> {
        private Set<String> selectedMaSet;
        
        public CheckBoxListRenderer(Set<String> selectedMaSet) {
            this.selectedMaSet = selectedMaSet;
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends TacGia> list, TacGia value, 
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            
            setText(value.getTenTacGia());
            
            // Đặt trạng thái checkbox dựa trên Set (nguồn chân lý)
            setSelected(selectedMaSet.contains(value.getMaTacGia()));
            
            // Xử lý màu sắc (vẫn tôn trọng màu highlight của JList)
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            
            return this;
        }
    }
}
