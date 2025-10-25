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

// --- CÁC IMPORT CẦN THIẾT CHO VIỆC TẢI FILE ---
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
// --- KẾT THÚC IMPORT ---


public class SachEditDialog extends JDialog {

    // Các trường nhập liệu
    // Sửa: Đổi tên biến txtTacGia để rõ ràng hơn (Optional)
    private JTextField txtMaSach, txtTenSach, txtTenTacGia, txtNhaXuatBan, txtNamXuatBan, txtSoLuong, txtDuongDanAnh, txtDuongDanXemTruoc;
    private JTextArea txtMoTa;

    private JButton btnLuu, btnHuy;

    private SachDAO sachDAO;
    private Sach sachToEdit;
    private boolean isEditMode = false;
    private boolean saveSuccess = false;

    public SachEditDialog(JFrame parent, SachDAO dao, Sach sachToEdit) {
        super(parent, "Quản lý Sách", true);

        this.sachDAO = dao;
        this.sachToEdit = sachToEdit;
        this.isEditMode = (sachToEdit != null);

        initUI();

        if (isEditMode) {
            setTitle("Sửa Thông Tin Sách");
            populateFields();
        } else {
            setTitle("Thêm Sách Mới");
        }

        setSize(550, 700); // Tăng kích thước
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Thay đổi GridLayout thành 9 hàng (cho 9 thuộc tính)
        JPanel fieldsPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Mã Sách:"));
        txtMaSach = new JTextField(20);
        fieldsPanel.add(txtMaSach);

        fieldsPanel.add(new JLabel("Tên Sách:"));
        txtTenSach = new JTextField();
        fieldsPanel.add(txtTenSach);

        // Giữ nguyên Label là "Tác Giả", nhưng biến là txtTenTacGia
        fieldsPanel.add(new JLabel("Tác Giả:"));
        txtTenTacGia = new JTextField();
        fieldsPanel.add(txtTenTacGia);

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

        // --- Đọc Thử (.pdf) --- (THÊM MỚI)
        fieldsPanel.add(new JLabel("File Đọc Thử (.pdf):"));
        JPanel pdfPanel = new JPanel(new BorderLayout(5, 0));
        txtDuongDanXemTruoc = new JTextField();
        JButton btnTaiLenPdf = new JButton("...");
        btnTaiLenPdf.setPreferredSize(new Dimension(30, 20));
        pdfPanel.add(txtDuongDanXemTruoc, BorderLayout.CENTER);
        pdfPanel.add(btnTaiLenPdf, BorderLayout.EAST);
        fieldsPanel.add(pdfPanel);

        // --- Mô Tả (.txt) ---
        fieldsPanel.add(new JLabel("Mô Tả (Tải từ .txt):"));
        txtMoTa = new JTextArea(5, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtMoTa);
        JButton btnTaiLenTxt = new JButton("Tải lên .txt");
        btnTaiLenTxt.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel moTaPanel = new JPanel();
        moTaPanel.setLayout(new BorderLayout(0, 5));
        moTaPanel.add(scrollPane, BorderLayout.CENTER);
        moTaPanel.add(btnTaiLenTxt, BorderLayout.SOUTH);
        fieldsPanel.add(moTaPanel);

        add(fieldsPanel, BorderLayout.CENTER);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnHuy);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---
        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> saveSach());

        // Gắn sự kiện cho 3 nút tải file
        btnChonAnh.addActionListener(e -> chonVaCopyFile("image"));
        btnTaiLenPdf.addActionListener(e -> chonVaCopyFile("pdf"));
        btnTaiLenTxt.addActionListener(e -> taiNoiDungXemTruoc());
    }

    /**
     * Điền thông tin của sách (chế độ Sửa)
     */
    private void populateFields() {
        txtMaSach.setText(sachToEdit.getMaSach());
        txtMaSach.setEditable(false);
        txtTenSach.setText(sachToEdit.getTenSach());
        // Sửa: Lấy maTacGia (chính là tên) hiển thị vào ô txtTenTacGia
        txtTenTacGia.setText(sachToEdit.getMaTacGia());
        txtNhaXuatBan.setText(sachToEdit.getNhaXuatBan());
        txtNamXuatBan.setText(String.valueOf(sachToEdit.getNamXuatBan()));
        txtSoLuong.setText(String.valueOf(sachToEdit.getSoLuong()));
        txtDuongDanAnh.setText(sachToEdit.getDuongDanAnh());
        txtMoTa.setText(sachToEdit.getMoTa());
        txtDuongDanXemTruoc.setText(sachToEdit.getDuongDanXemTruoc());
    }

    /**
     * Xử lý logic khi nhấn nút Lưu
     */
    private void saveSach() {
        // 1. Validate
        String maSach = txtMaSach.getText().trim();
        String tenSach = txtTenSach.getText().trim();
        if (maSach.isEmpty() || tenSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sách và Tên sách không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int namXB = 0;
        int soLuong = 0;
        try {
            String namXBStr = txtNamXuatBan.getText().trim();
            if (!namXBStr.isEmpty()) {
                 namXB = Integer.parseInt(namXBStr);
                 // Có thể thêm kiểm tra năm hợp lệ (ví dụ: không quá tương lai)
            }
             String soLuongStr = txtSoLuong.getText().trim();
            if (!soLuongStr.isEmpty()) {
                soLuong = Integer.parseInt(soLuongStr);
                if (soLuong < 0) {
                     JOptionPane.showMessageDialog(this, "Số lượng không được là số âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                     return;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm xuất bản và Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lấy dữ liệu từ form
        Sach s = new Sach();
        s.setMaSach(maSach);
        s.setTenSach(tenSach);

        // Sửa: Lấy tên từ txtTenTacGia và set vào maTacGia của đối tượng Sach
        String tenTacGiaNhap = txtTenTacGia.getText().trim();
        s.setMaTacGia(tenTacGiaNhap); // DAO sẽ dùng giá trị này

        s.setNhaXuatBan(txtNhaXuatBan.getText().trim());
        s.setNamXuatBan(namXB);
        s.setSoLuong(soLuong);
        s.setDuongDanAnh(txtDuongDanAnh.getText().trim());
        s.setMoTa(txtMoTa.getText().trim());
        s.setDuongDanXemTruoc(txtDuongDanXemTruoc.getText().trim());

        // 3. Gọi DAO (SachDAO sẽ tự động xử lý việc thêm tác giả vào bảng TACGIA nếu cần)
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
                // SachDAO đã in lỗi cụ thể (trùng mã sách, lỗi FK,...) ra console
                JOptionPane.showMessageDialog(this, (isEditMode ? "Cập nhật" : "Thêm") + " sách thất bại. (Xem console để biết chi tiết)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Phương thức công khai để AdminMainForm kiểm tra xem có lưu thành công hay không
     */
    public boolean isSaveSuccess() {
        return this.saveSuccess;
    }

    // =================================================================
    // ============ PHƯƠNG THỨC TẢI FILE ==============================
    // =================================================================

    /**
     * Hàm chung để chọn file (ảnh hoặc pdf) và copy vào thư mục project
     * @param fileType "image" hoặc "pdf"
     */
    private void chonVaCopyFile(String fileType) {
        JFileChooser fileChooser = new JFileChooser();
        String description;
        String subFolder;
        JTextField targetField;
        FileNameExtensionFilter filter;

        // Cấu hình tùy theo loại file
        if (fileType.equals("image")) {
            fileChooser.setDialogTitle("Chọn ảnh bìa");
            description = "Tệp ảnh (*.png, *.jpg)";
            filter = new FileNameExtensionFilter(description, "png", "jpg", "jpeg");
            subFolder = "images"; // Thư mục lưu ảnh
            targetField = txtDuongDanAnh;
        } else { // pdf
            fileChooser.setDialogTitle("Chọn file đọc thử");
            description = "Tệp PDF (*.pdf)";
            filter = new FileNameExtensionFilter(description, "pdf");
            subFolder = "previews"; // Thư mục lưu file pdf
            targetField = txtDuongDanXemTruoc;
        }

        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File fileDaChon = fileChooser.getSelectedFile();

                // 1. Lấy thư mục gốc của project
                Path thuMucProject = Paths.get(System.getProperty("user.dir"));
                // Tạo đường dẫn đến thư mục con (images hoặc previews)
                Path thuMucLuuTru = thuMucProject.resolve(subFolder);

                // 2. Tạo thư mục con nếu chưa có
                if (!Files.exists(thuMucLuuTru)) {
                    Files.createDirectories(thuMucLuuTru);
                }

                // 3. Copy file đã chọn vào thư mục con
                Path fileDich = thuMucLuuTru.resolve(fileDaChon.getName());
                Files.copy(fileDaChon.toPath(), fileDich, StandardCopyOption.REPLACE_EXISTING);

                // 4. Lấy đường dẫn TƯƠNG ĐỐI (quan trọng!)
                // Sử dụng Path.relativize để đảm bảo đường dẫn đúng trên các HĐH
                Path duongDanTuongDoiPath = thuMucProject.relativize(fileDich);
                // Chuyển sang chuỗi và chuẩn hóa dấu phân cách (thường dùng / cho lưu trữ)
                String duongDanTuongDoi = duongDanTuongDoiPath.toString().replace(File.separatorChar, '/');


                // 5. Gán đường dẫn tương đối vào ô text
                targetField.setText(duongDanTuongDoi);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Mở JFileChooser để chọn file .txt và đọc nội dung vào JTextArea.
     */
    private void taiNoiDungXemTruoc() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file tóm tắt (.txt)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Tệp văn bản (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File fileDaChon = fileChooser.getSelectedFile();

                // Đọc toàn bộ nội dung file (dùng UTF-8 để hỗ trợ tiếng Việt)
                String noiDung = Files.readString(fileDaChon.toPath(), StandardCharsets.UTF_8);

                // Gán nội dung vào ô mô tả
                txtMoTa.setText(noiDung);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi đọc file .txt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
