package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

/**
 * Dialog (cửa sổ pop-up) để chọn nhiều Tác Giả.
 */
public class ChonTacGiaDialog extends JDialog {

    private JList<TacGia> listTacGia;
    private DefaultListModel<TacGia> listModel;
    private JButton btnOK, btnHuy;
    private boolean confirmed = false;
    private List<TacGia> allTacGias;
    private List<TacGia> selectedTacGias = new ArrayList<>();

    public ChonTacGiaDialog(JDialog parent, TacGiaDAO tacGiaDAO, List<TacGia> currentSelection) {
        super(parent, "Chọn Tác Giả", true);
        
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Lấy danh sách tác giả
        this.allTacGias = tacGiaDAO.getAllTacGia();
        
        // --- Danh sách ---
        listModel = new DefaultListModel<>();
        for (TacGia tg : allTacGias) {
            listModel.addElement(tg); // TacGia.toString() sẽ được gọi
        }
        
        listTacGia = new JList<>(listModel);
        // Cho phép chọn nhiều
        listTacGia.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(listTacGia);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

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
            this.selectedTacGias = listTacGia.getSelectedValuesList();
            this.confirmed = true;
            dispose();
        });

        // Đánh dấu các tác giả đã được chọn trước đó
        preselectAuthors(currentSelection);
    }

    /**
     * Tìm và chọn các tác giả đã có trong danh sách
     */
    private void preselectAuthors(List<TacGia> currentSelection) {
        if (currentSelection == null || currentSelection.isEmpty()) {
            return;
        }

        // Tạo một Set chứa các mã tác giả đã chọn để tìm kiếm nhanh
        Set<String> selectedMaTacGia = currentSelection.stream()
                                          .map(TacGia::getMaTacGia)
                                          .collect(Collectors.toSet());
        
        List<Integer> indicesToSelect = new ArrayList<>();
        for (int i = 0; i < allTacGias.size(); i++) {
            if (selectedMaTacGia.contains(allTacGias.get(i).getMaTacGia())) {
                indicesToSelect.add(i);
            }
        }
        
        // Chuyển List<Integer> sang mảng int[]
        int[] selectedIndices = indicesToSelect.stream().mapToInt(Integer::intValue).toArray();
        listTacGia.setSelectedIndices(selectedIndices);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<TacGia> getSelectedTacGias() {
        return selectedTacGias;
    }
}
