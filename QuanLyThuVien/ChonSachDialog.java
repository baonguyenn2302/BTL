package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

/**
 * Dialog (cửa sổ pop-up) để chọn nhiều Sách (có thanh tìm kiếm).
 */
public class ChonSachDialog extends JDialog {

    private JList<Sach> listSach;
    private DefaultListModel<Sach> listModel;
    private JTextField txtTimKiem;
    private JButton btnOK, btnHuy;
    private boolean confirmed = false;
    
    private SachDAO sachDAO;
    private List<Sach> allSach;
    private List<Sach> selectedSach = new ArrayList<>();

    public ChonSachDialog(JFrame parent, SachDAO sachDAO, List<Sach> currentSelection) {
        super(parent, "Chọn Sách Thêm vào Bộ Sưu Tập", true);
        
        this.sachDAO = sachDAO;
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Thanh tìm kiếm ---
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        txtTimKiem = new JTextField("Tìm kiếm sách...");
        searchPanel.add(txtTimKiem, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // --- Danh sách ---
        listModel = new DefaultListModel<>();
        listSach = new JList<>(listModel);
        listSach.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
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

        // --- Sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnOK.addActionListener(e -> {
            this.selectedSach = listSach.getSelectedValuesList();
            this.confirmed = true;
            dispose();
        });

        // Sự kiện tìm kiếm
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterSach(txtTimKiem.getText().trim());
            }
        });
        
        // Đánh dấu các sách đã được chọn trước đó
        preselectSach(currentSelection);
    }
    
    private void loadAllSach() {
        this.allSach = sachDAO.getAllSach(); // Lấy tất cả sách
        filterSach(""); // Hiển thị tất cả
    }
    
    /**
     * Lọc danh sách sách dựa trên từ khóa
     */
    private void filterSach(String keyword) {
        listModel.clear();
        String kwLower = keyword.toLowerCase();
        for (Sach s : allSach) {
            // Tìm theo tên sách hoặc mã sách
            if (s.getTenSach().toLowerCase().contains(kwLower) || s.getMaSach().toLowerCase().contains(kwLower)) {
                listModel.addElement(s); // Sach.toString() sẽ được gọi
            }
        }
    }

    /**
     * Tìm và chọn các sách đã có trong danh sách
     */
    private void preselectSach(List<Sach> currentSelection) {
        if (currentSelection == null || currentSelection.isEmpty()) {
            return;
        }

        Set<String> selectedMaSach = currentSelection.stream()
                                          .map(Sach::getMaSach)
                                          .collect(Collectors.toSet());
        
        List<Integer> indicesToSelect = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            if (selectedMaSach.contains(listModel.getElementAt(i).getMaSach())) {
                indicesToSelect.add(i);
            }
        }
        
        int[] selectedIndices = indicesToSelect.stream().mapToInt(Integer::intValue).toArray();
        listSach.setSelectedIndices(selectedIndices);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Sach> getSelectedSach() {
        return selectedSach;
    }
}
