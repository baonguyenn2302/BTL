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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea; // <<< Sẽ không còn dùng
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BoSuuTapEditDialog extends JDialog {

    private JTextField txtTenBST;
    // private JTextArea txtMoTa; // <<< ĐÃ XÓA
    private JLabel lblAnhPath;
    private JButton btnChonAnh, btnLuu, btnHuy;
    
    private String selectedImagePath = null;
    private BoSuuTap bstGoc;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;
    private BoSuuTapDAO bstDAO;

    public BoSuuTapEditDialog(JFrame parent) {
        super(parent, "Thêm Bộ Sưu Tập Mới", true);
        this.isEditMode = false;
        initDialog();
    }

    public BoSuuTapEditDialog(JFrame parent, BoSuuTap bstToEdit) {
        super(parent, "Cập Nhật Bộ Sưu Tập", true);
        this.isEditMode = true;
        this.bstGoc = bstToEdit;
        initDialog();
        populateForm();
    }

    private void initDialog() {
        this.bstDAO = new BoSuuTapDAO();
        setSize(550, 250); // <<< Giảm chiều cao
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        addEventHandlers();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Tên BST
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên Bộ Sưu Tập (*):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        txtTenBST = new JTextField(30);
        panel.add(txtTenBST, gbc);

        // Hàng 1: Mô Tả (<<< ĐÃ XÓA KHỎI ĐÂY >>>)
        
        // Hàng 2: Ảnh Bìa (Sửa gridy 2 -> 1)
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Ảnh Bìa:"), gbc);

        JPanel anhPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnChonAnh = new JButton("Chọn Tệp...");
        lblAnhPath = new JLabel("(Chưa chọn ảnh)");
        lblAnhPath.setForeground(Color.GRAY);
        anhPanel.add(btnChonAnh);
        anhPanel.add(lblAnhPath);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(anhPanel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnLuu = new JButton("Lưu Lại");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 14));
        btnLuu.setBackground(new Color(30, 144, 255));
        btnLuu.setForeground(Color.WHITE);
        
        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        
        panel.add(btnLuu);
        panel.add(btnHuy);
        return panel;
    }

    private void populateForm() {
        if (bstGoc == null) return;
        txtTenBST.setText(bstGoc.getTenBoSuuTap());
        // txtMoTa.setText(bstGoc.getMoTa()); // <<< ĐÃ XÓA
        this.selectedImagePath = bstGoc.getDuongDanAnh();
        if (this.selectedImagePath != null && !this.selectedImagePath.isEmpty()) {
            File f = new File(this.selectedImagePath);
            lblAnhPath.setText(f.getName());
            lblAnhPath.setForeground(Color.BLACK);
        }
    }

    private void addEventHandlers() {
        btnHuy.addActionListener(e -> dispose());
        btnChonAnh.addActionListener(e -> chonAnh());
        btnLuu.addActionListener(e -> saveBoSuuTap());
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh bìa");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "gif", "jpeg");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.selectedImagePath = selectedFile.getAbsolutePath();
            lblAnhPath.setText(selectedFile.getName());
            lblAnhPath.setForeground(Color.BLACK);
        }
    }

    private void saveBoSuuTap() {
        String tenBST = txtTenBST.getText().trim();
        if (tenBST.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bộ sưu tập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xử lý ảnh (giữ nguyên)
        String finalImagePathToSave = (isEditMode) ? bstGoc.getDuongDanAnh() : null;
        if (selectedImagePath != null && !selectedImagePath.equals(finalImagePathToSave)) {
            try {
                Path destDir = Paths.get("collection_covers");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                File sourceFile = new File(selectedImagePath);
                String cleanName = tenBST.replaceAll("[^a-zA-Z0-9.-]", "_");
                String newFileName = cleanName + "_" + System.currentTimeMillis() + "_" + sourceFile.getName();
                Path destPath = destDir.resolve(newFileName);
                Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                finalImagePathToSave = destPath.toString();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi sao chép tệp ảnh.", "Lỗi I/O", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        // Tạo đối tượng
        BoSuuTap bst = (isEditMode) ? bstGoc : new BoSuuTap();
        bst.setTenBoSuuTap(tenBST);
        // bst.setMoTa(txtMoTa.getText().trim()); // <<< ĐÃ XÓA
        bst.setDuongDanAnh(finalImagePathToSave);

        boolean success;
        if (isEditMode) {
            success = bstDAO.updateBoSuuTap(bst);
        } else {
            success = bstDAO.insertBoSuuTap(bst);
        }

        if (success) {
            this.saveSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi lưu vào CSDL.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaveSuccess() {
        return saveSuccess;
    }
}