// util/DatabaseConnection.java
package hdkmanagement.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static String host = "127.0.0.1";
    private static String port = "3306";
    private static String database = "hdk_management";
    private static String user = "root";
    private static String password = "";
    
    private static String url = "";
    private static Connection connection = null;
    private static volatile DatabaseConnection instance = null; // Thread-safe singleton
    
    // Private constructor (Singleton pattern)
    private DatabaseConnection() {
        loadConfig();
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            // Đăng ký driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Tạo kết nối
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(" Kết nối database thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println(" Không tìm thấy driver MySQL!");
            JOptionPane.showMessageDialog(null, 
                "Không tìm thấy driver MySQL!\nVui lòng kiểm tra thư viện.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(" Không thể kết nối database!");
            JOptionPane.showMessageDialog(null, 
                "Không thể kết nối database!\n" + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadConfig() {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
            host = prop.getProperty("db.host", "127.0.0.1");
            port = prop.getProperty("db.port", "3306");
            database = prop.getProperty("db.name", "hdk_management");
            user = prop.getProperty("db.user", "root");
            password = prop.getProperty("db.password", "");
        } catch (IOException e) {
            System.out.println("️ Không tìm thấy file config.properties. Đang sử dụng cấu hình mặc định.");
        }
    }
    
    // Lấy instance duy nhất (Singleton) - Double Checked Locking
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    // Lấy kết nối
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    // Kiểm tra kết nối
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Đóng kết nối
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Đã đóng kết nối database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Tạo PreparedStatement
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }
    
    // Tạo PreparedStatement với RETURN_GENERATED_KEYS
    public PreparedStatement prepareStatementWithKeys(String sql) throws SQLException {
        return getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
    
    // Đóng các resource
    public static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void closeResources(ResultSet rs, PreparedStatement ps) {
        closeResources(rs, ps, null);
    }
    
    // Test kết nối
    public static void testConnection() {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            if (db.isConnected()) {
                System.out.println(" Kết nối database đang hoạt động!");
            } else {
                System.out.println(" Kết nối database không hoạt động!");
            }
        } catch (Exception e) {
            System.err.println(" Lỗi kiểm tra kết nối: " + e.getMessage());
        }
    }
}