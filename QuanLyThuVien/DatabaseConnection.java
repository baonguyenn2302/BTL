package QuanLyThuVien;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String SERVER_NAME = "LAPTOP-5B5F23MC\\QLTV"; 
    private static final String DATABASE_NAME = "QuanLyThuVienDB"; 

    private static final String USERNAME = "sa"; 
    private static final String PASSWORD = "quang@05";  
    /**
     * Phương thức tĩnh (static) để lấy kết nối đến CSDL.
     */
    public static Connection getConnection() {
        
        String connectionUrl = "jdbc:sqlserver://" + SERVER_NAME + ";"
                             + "databaseName=" + DATABASE_NAME + ";"
                             + "user=" + USERNAME + ";"
                             + "password=" + PASSWORD + ";"
                             + "encrypt=true;trustServerCertificate=true;"; // Bắt buộc cho driver mới

        Connection conn = null;
        
        try {
            // 1. Nạp driver JDBC
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // 2. Tạo kết nối
            conn = DriverManager.getConnection(connectionUrl);

        } catch (ClassNotFoundException e) {
            System.err.println("LỖI: Không tìm thấy SQL Server JDBC Driver!");
            System.err.println("Bạn đã thêm file .jar của JDBC vào Libraries chưa?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("LỖI: Kết nối CSDL thất bại!");
            System.err.println("Hãy kiểm tra lại 4 thông số (SERVER_NAME, DATABASE_NAME, USERNAME, PASSWORD).");
            e.printStackTrace();
        }
        
        return conn;
    }


     
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println(">>> KIỂM TRA KẾT NỐI THÀNH CÔNG! <<<");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println(">>> KIỂM TRA KẾT NỐI THẤT BẠI! <<<");
        }
    }
    
}
