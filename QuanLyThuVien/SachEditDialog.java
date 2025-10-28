package QuanLyThuVien;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog (cửa sổ pop-up) để Thêm và Sửa thông tin Sách.
 * ĐÃ TÁI CẤU TRÚC: Hỗ trợ nhiều tác giả
 */
public class SachEditDialog extends JDialog {

    // === CẬP NHẬT TRƯỜNG NHẬP LIỆU ===
    private JTextField txtMaSach, txtTenSach, txtNhaXuatBan, txtNamXuatBan, txtSoLuong, txtDuongDanAnh, txtViTri;
    // private JTextField txtTenTacGia; // <<< ĐÃ XÓA
    private JTextArea txtMoTa;
    
    private JTextField txtHienThiTacGia; // <<< TRƯỜNG MỚI (chỉ hiển thị)
    private JButton btnChonTacGia;      // <<< NÚT MỚI
    
    private List<TacGia> danhSachTacGiaDaChon; // <<< Biến lưu tác giả đã chọn

    private JButton btnLuu, btnHuy;

    private SachDAO sachDAO;
    private TacGiaDAO tacGiaDAO; // <<< CẦN DAO MỚI ĐỂ CHỌN
    private Sach sachToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;

    public SachEditDialog(JFrame parent, SachDAO sachDAO, Sach sachToEdit) {
        super(parent, "Quản lý Sách", true);

        this.sachDAO = sachDAO;
        this.tacGiaDAO = new TacGiaDAO(); // Khởi tạo TacGiaDAO
        this.sachToEdit = sachToEdit;
        this.isEditMode = (sachToEdit != null);
        this.danhSachTacGiaDaChon = new ArrayList<>();

        initUI();

        if (isEditMode) {
            setTitle("Sửa Thông Tin Sách");
            populateFields();
        } else {
            setTitle("Thêm Sách Mới");
        }

        setSize(550, 650); 
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Mã Sách:"));
        txtMaSach = new JTextField(20);
        fieldsPanel.add(txtMaSach);

        fieldsPanel.add(new JLabel("Tên Sách:"));
        txtTenSach = new JTextField();
        fieldsPanel.add(txtTenSach);

        // === PANEL TÁC GIẢ MỚI ===
        fieldsPanel.add(new JLabel("Tác Giả:"));
        JPanel tacGiaPanel = new JPanel(new BorderLayout(5, 0));
        txtHienThiTacGia = new JTextField();
        txtHienThiTacGia.setEditable(false); // Không cho sửa trực tiếp
        btnChonTacGia = new JButton("...");
        btnChonTacGia.setPreferredSize(new Dimension(30, 20));
        tacGiaPanel.add(txtHienThiTacGia, BorderLayout.CENTER);
        tacGiaPanel.add(btnChonTacGia, BorderLayout.EAST);
        fieldsPanel.add(tacGiaPanel);
        // === KẾT THÚC PANEL TÁC GIẢ MỚI ===

        fieldsPanel.add(new JLabel("Nhà Xuất Bản:"));
        txtNhaXuatBan = new JTextField();
        fieldsPanel.add(txtNhaXuatBan);

        fieldsPanel.add(new JLabel("Năm Xuất Bản:"));
        txtNamXuatBan = new JTextField();
        fieldsPanel.add(txtNamXuatBan);

        fieldsPanel.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        fieldsPanel.add(txtSoLuong);

        // --- Ảnh Bìa ---
        fieldsPanel.add(new JLabel("Ảnh Bìa (.png, .jpg):"));
        JPanel anhPanel = new JPanel(new BorderLayout(5, 0));
        txtDuongDanAnh = new JTextField();
        JButton btnChonAnh = new JButton("...");
        btnChonAnh.setPreferredSize(new Dimension(30, 20));
        anhPanel.add(txtDuongDanAnh, BorderLayout.CENTER);
        anhPanel.add(btnChonAnh, BorderLayout.EAST);
        fieldsPanel.add(anhPanel);

        // --- Vị trí ---
        fieldsPanel.add(new JLabel("Vị trí:"));
        txtViTri = new JTextField();
        fieldsPanel.add(txtViTri);

        // Panel Mô tả
        JPanel moTaOuterPanel = new JPanel(new BorderLayout(0, 5));
        moTaOuterPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
        JLabel lblMoTa = new JLabel("Mô Tả (Tải từ .txt hoặc nhập trực tiếp):");
        txtMoTa = new JTextArea(5, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtMoTa);
        JButton btnTaiLenTxt = new JButton("Tải lên .txt");
        moTaOuterPanel.add(lblMoTa, BorderLayout.NORTH);
        moTaOuterPanel.add(scrollPane, BorderLayout.CENTER);
        moTaOuterPanel.add(btnTaiLenTxt, BorderLayout.SOUTH);

        // Panel nút Lưu/Hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);
        
        JPanel centerAndButtons = new JPanel(new BorderLayout());
        centerAndButtons.add(fieldsPanel, BorderLayout.CENTER);
        centerAndButtons.add(buttonPanel, BorderLayout.SOUTH);

        add(centerAndButtons, BorderLayout.CENTER); 
        add(moTaOuterPanel, BorderLayout.SOUTH); 

        // --- Xử lý sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> saveSach());
        btnChonAnh.addActionListener(e -> chonVaCopyFile("image"));
        btnTaiLenTxt.addActionListener(e -> taiNoiDungMoTa());
        
        // SỰ KIỆN MỚI
        btnChonTacGia.addActionListener(e -> openChonTacGiaDialog());
    }

    /**
     * Điền thông tin của sách (chế độ Sửa)
     */
    private void populateFields() {
        txtMaSach.setText(sachToEdit.getMaSach());
        txtMaSach.setEditable(false);
        txtTenSach.setText(sachToEdit.getTenSach());
        
        // Lấy danh sách tác giả
        this.danhSachTacGiaDaChon = new ArrayList<>(sachToEdit.getDanhSachTacGia());
        updateHienThiTacGia(); // Cập nhật ô hiển thị
        
        txtNhaXuatBan.setText(sachToEdit.getNhaXuatBan());
        if (sachToEdit.getNamXuatBan() > 0) {
             txtNamXuatBan.setText(String.valueOf(sachToEdit.getNamXuatBan()));
        } else {
             txtNamXuatBan.setText("");
        }
        txtSoLuong.setText(String.valueOf(sachToEdit.getSoLuong()));
        txtDuongDanAnh.setText(sachToEdit.getDuongDanAnh());
        txtMoTa.setText(sachToEdit.getMoTa());
        txtViTri.setText(sachToEdit.getViTri()); 
    }

    /**
     * Xử lý logic khi nhấn nút Lưu (Đã cập nhật)
     */
    private void saveSach() {
        // 1. Validate (như cũ)
        String maSach = txtMaSach.getText().trim();
        String tenSach = txtTenSach.getText().trim();
        if (maSach.isEmpty() || tenSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sách và Tên sách không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate tác giả
        if (danhSachTacGiaDaChon == null || danhSachTacGiaDaChon.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Sách phải có ít nhất một tác giả!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int namXB = 0;
        int soLuong = 0;
        try {
            String namXBStr = txtNamXuatBan.getText().trim();
            if (!namXBStr.isEmpty()) {
                 namXB = Integer.parseInt(namXBStr);
            }
             String soLuongStr = txtSoLuong.getText().trim();
            if (!soLuongStr.isEmpty()) {
                soLuong = Integer.parseInt(soLuongStr);
                if (soLuong < 0) {
                     JOptionPane.showMessageDialog(this, "Số lượng không được là số âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                     return;
                }
            } else {
                soLuong = 0;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm xuất bản và Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lấy dữ liệu từ form (Đã cập nhật)
        Sach s = new Sach();
        s.setMaSach(maSach);
        s.setTenSach(tenSach);
        s.setDanhSachTacGia(this.danhSachTacGiaDaChon); // <<< CẬP NHẬT
        s.setNhaXuatBan(txtNhaXuatBan.getText().trim());
        s.setNamXuatBan(namXB);
        s.setSoLuong(soLuong);
        s.setDuongDanAnh(txtDuongDanAnh.getText().trim());
        s.setMoTa(txtMoTa.getText().trim());
        s.setViTri(txtViTri.getText().trim()); 
        
        if (isEditMode) {
            s.setNgayThem(sachToEdit.getNgayThem());
        }

        // 3. Gọi DAO (Đã cập nhật)
        try {
            boolean success;
            if (isEditMode) {
                success = sachDAO.suaSach(s);
            } else {
                success = sachDAO.themSach(s);
            }

            // 4. Phản hồi
            if (success) {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.saveSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " sách thất bại. (Mã sách có thể đã tồn tại?)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Mở Dialog chọn tác giả
     */
    private void openChonTacGiaDialog() {
        // Tạo dialog mới, truyền vào DAO và danh sách hiện tại
        ChonTacGiaDialog dialog = new ChonTacGiaDialog(this, tacGiaDAO, this.danhSachTacGiaDaChon);
        dialog.setVisible(true);
        
        // Nếu người dùng nhấn "OK"
        if (dialog.isConfirmed()) {
            this.danhSachTacGiaDaChon = dialog.getSelectedTacGias();
            updateHienThiTacGia();
        }
    }
    
    /**
     * Cập nhật ô text hiển thị tên tác giả
     */
    private void updateHienThiTacGia() {
        if (danhSachTacGiaDaChon == null || danhSachTacGiaDaChon.isEmpty()) {
            txtHienThiTacGia.setText("");
            return;
        }
        String tenTacGias = danhSachTacGiaDaChon.stream()
                                .map(TacGia::getTenTacGia)
                                .collect(Collectors.joining(", "));
        txtHienThiTacGia.setText(tenTacGias);
    }

    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }

    // --- (Hàm chonVaCopyFile và taiNoiDungMoTa giữ nguyên) ---
    private void chonVaCopyFile(String fileType) {
        JFileChooser fileChooser = new JFileChooser();
        String description, subFolder, targetField;
        FileNameExtensionFilter filter;
        if (fileType.equals("image")) {
            fileChooser.setDialogTitle("Chọn ảnh bìa");
            description = "Tệp ảnh (*.png, *.jpg)";
            filter = new FileNameExtensionFilter(description, "png", "jpg", "jpeg");
            subFolder = "images";
            targetField = "anhBia"; // Dùng String để phân biệt
        } else {
             System.err.println("Loại file không hợp lệ: " + fileType);
             return; 
        }
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
                
                if (targetField.equals("anhBia")) {
                    txtDuongDanAnh.setText(duongDanTuongDoi);
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void taiNoiDungMoTa() { 
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file mô tả (.txt)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Tệp văn bản (*.txt)", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File fileDaChon = fileChooser.getSelectedFile();
                String noiDung = Files.readString(fileDaChon.toPath(), StandardCharsets.UTF_8);
                txtMoTa.setText(noiDung); 
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi đọc file .txt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}