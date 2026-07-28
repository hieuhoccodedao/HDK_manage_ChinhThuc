// HDKManagement.java
package hdkmanagement;

import com.formdev.flatlaf.FlatLightLaf;
import hdkmanagement.view.auth.frmDangNhap;
import hdkmanagement.util.DatabaseConnection;
import hdkmanagement.util.DatabaseInitializer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class HDKManagement {
    
    public static void main(String[] args) {
        // Thiết lập giao diện Look and Feel hiện đại (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Cải thiện render font chữ trên Windows
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Khởi tạo kết nối database
        DatabaseConnection db = DatabaseConnection.getInstance();
        
        // Kiểm tra kết nối
        if (db.isConnected()) {
            System.out.println(" Kết nối database thành công!");
            
            // Tự động tạo bảng nếu chưa có
            DatabaseInitializer.init();
            
            // Chạy form đăng nhập
            SwingUtilities.invokeLater(() -> {
                new frmDangNhap().setVisible(true);
            });
        } else {
            System.err.println(" Không thể kết nối database!");
            JOptionPane.showMessageDialog(null,
                "Không thể kết nối đến database!\nVui lòng kiểm tra XAMPP MySQL.",
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}