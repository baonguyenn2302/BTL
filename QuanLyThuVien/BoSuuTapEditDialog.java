package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Dialog (cửa sổ pop-up) để Thêm và Sửa thông tin Bộ Sưu Tập.
 */
public class BoSuuTapEditDialog extends JDialog {

    private JTextField txtTenBoSuuTap, txtDuongDanAnh;
    private JTextArea txtMoTa;
    private JButton btnChonAnh, btnLuu, btnHuy;

    private BoSuuTapDAO boSuuTapDAO;
    private BoSuuTap bstToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;

    public BoSuuTapEditDialog(JFrame parent, BoSuuTapDAO dao, BoSuuTap bstToEdit) {
        super(parent, "Quản lý Bộ Sưu Tập", true);

        this.boSuuTapDAO = dao;
        this.bstToEdit = bstToEdit;
        this.isEditMode = (bstToEdit != null);

        initUI();

        if (isEditMode) {
            setTitle("Sửa Thông Tin Bộ Sưu Tập");
            populateFields();
        } else {
            setTitle("Tạo Bộ Sưu Tập Mới");
        }

        setSize(450, 350);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new BorderLayout(10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel trên (Tên và Ảnh)
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        topPanel.add(new JLabel("Tên Bộ Sưu Tập:"));
        txtTenBoSuuTap = new JTextField(20);
        topPanel.add(txtTenBoSuuTap);

        // Panel Ảnh
        topPanel.add(new JLabel("Ảnh Bìa (.png, .jpg):"));
        JPanel anhPanel = new JPanel(new BorderLayout(5, 0));
        txtDuongDanAnh = new JTextField();
        btnChonAnh = new JButton("...");
        btnChonAnh.setPreferredSize(new Dimension(30, 20));
        anhPanel.add(txtDuongDanAnh, BorderLayout.CENTER);
        anhPanel.add(btnChonAnh, BorderLayout.EAST);
        topPanel.add(anhPanel);
        
        fieldsPanel.add(topPanel, BorderLayout.NORTH);

        // Panel Mô tả (ở giữa)
        JPanel moTaPanel = new JPanel(new BorderLayout(0, 5));
        moTaPanel.add(new JLabel("Mô Tả:"), BorderLayout.NORTH);
        txtMoTa = new JTextArea(5, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        moTaPanel.add(new JScrollPane(txtMoTa), BorderLayout.CENTER);
        
        fieldsPanel.add(moTaPanel, BorderLayout.CENTER);

        add(fieldsPanel, BorderLayout.CENTER);

        // Panel nút Lưu/Hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> saveBoSuuTap());
        btnChonAnh.addActionListener(e -> chonVaCopyFile());
    }

    /**
     * Điền thông tin (chế độ Sửa)
     */
    private void populateFields() {
        txtTenBoSuuTap.setText(bstToEdit.getTenBoSuuTap());
        txtMoTa.setText(bstToEdit.getMoTa());
        txtDuongDanAnh.setText(bstToEdit.getDuongDanAnh());
    }

    /**
     * Xử lý logic khi nhấn nút Lưu
     */
    private void saveBoSuuTap() {
        String tenBST = txtTenBoSuuTap.getText().trim();
        if (tenBST.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên Bộ Sưu Tập không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Tạo hoặc cập nhật đối tượng BST
        BoSuuTap bst = (isEditMode) ? bstToEdit : new BoSuuTap();
        bst.setTenBoSuuTap(tenBST);
        bst.setMoTa(txtMoTa.getText().trim());
        bst.setDuongDanAnh(txtDuongDanAnh.getText().trim());

        // 2. Gọi DAO
        try {
            boolean success;
            if (isEditMode) {
                success = boSuuTapDAO.suaBoSuuTap(bst);
            } else {
                success = boSuuTapDAO.themBoSuuTap(bst);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Tạo") + " bộ sưu tập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.saveSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Tạo") + " bộ sưu tập thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }
    
    /**
     * Hàm chọn ảnh (giống hệt SachEditDialog)
     */
    private void chonVaCopyFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh bìa");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Tệp ảnh (*.png, *.jpg)", "png", "jpg", "jpeg");
        String subFolder = "images"; // Lưu chung thư mục ảnh
        
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File fileDaChon = fileChooser.getSelectedFile();
                Path thuMucProject = Paths.get(System.getProperty("user.dir"));
                Path thuMucLuuTru = thuMucProject.resolve(subFolder);
                if (!Files.exists(thuMucLuuTru)) {
                    Files.createDirectories(thuMucLuuTru);
                }
                Path fileDich = thuMucLuuTru.resolve(fileDaChon.getName());
                Files.copy(fileDaChon.toPath(), fileDich, StandardCopyOption.REPLACE_EXISTING);
                Path duongDanTuongDoiPath = thuMucProject.relativize(fileDich);
                String duongDanTuongDoi = duongDanTuongDoiPath.toString().replace(File.separatorChar, '/');
                txtDuongDanAnh.setText(duongDanTuongDoi);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
