// util/SessionManager.java
package hdkmanagement.util;

import hdkmanagement.model.NhanVien;
import hdkmanagement.model.Quyen;
import hdkmanagement.model.TaiKhoan;

public class SessionManager {
    
    private static SessionManager instance;
    private TaiKhoan currentUser;
    private NhanVien currentEmployee;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    // Đăng nhập
    public void login(TaiKhoan taiKhoan, NhanVien nhanVien) {
        this.currentUser = taiKhoan;
        this.currentEmployee = nhanVien;
        System.out.println(" Đăng nhập thành công: " + taiKhoan.getTenDangNhap());
        System.out.println(" Vai trò: " + taiKhoan.getQuyen().getTenQuyen());
        
        // Ghi log hệ thống
        hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Đăng nhập", 
                "Tài khoản " + taiKhoan.getTenDangNhap() + " đăng nhập hệ thống.");
    }
    
    // Đăng xuất
    public void logout() {
        if (this.currentUser != null) {
            hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Đăng xuất", 
                    "Tài khoản " + this.currentUser.getTenDangNhap() + " đăng xuất khỏi hệ thống.");
        }
        this.currentUser = null;
        this.currentEmployee = null;
        System.out.println(" Đã đăng xuất");
    }
    
    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Lấy thông tin user hiện tại
    public TaiKhoan getCurrentUser() {
        return currentUser;
    }
    
    public NhanVien getCurrentEmployee() {
        return currentEmployee;
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getMaTK() : -1;
    }
    
    public int getCurrentEmployeeId() {
        return currentEmployee != null ? currentEmployee.getMaNV() : -1;
    }
    
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getTenDangNhap() : null;
    }
    
    public String getCurrentEmployeeName() {
        return currentEmployee != null ? currentEmployee.getHoTen() : null;
    }
    
    // Kiểm tra quyền
    public boolean hasRole(String roleName) {
        if (currentUser == null || currentUser.getQuyen() == null) {
            return false;
        }
        return currentUser.getQuyen().getTenQuyen().equalsIgnoreCase(roleName);
    }
    
    public boolean isAdmin() {
        return hasRole("Admin");
    }
    
    public boolean isManager() {
        return hasRole("Quản lý") || hasRole("Admin");
    }
    
    public boolean isEmployee() {
        return hasRole("Nhân viên") || hasRole("Quản lý") || hasRole("Admin");
    }
    
    // Kiểm tra phân quyền cụ thể
    public boolean hasPermission(String permission) {
        // Đơn giản: Admin có tất cả quyền
        if (isAdmin()) return true;
        
        // Quản lý có hầu hết quyền trừ quản lý tài khoản và backup
        if (isManager()) {
            return !permission.equals("Quản lý tài khoản") && 
                   !permission.equals("Backup dữ liệu");
        }
        
        // Nhân viên chỉ có quyền cơ bản
        if (isEmployee()) {
            return permission.equals("Lập hóa đơn") ||
                   permission.equals("Tra cứu sản phẩm") ||
                   permission.equals("Kiểm tra tồn") ||
                   permission.equals("Thêm khách hàng") ||
                   permission.equals("Thanh toán") ||
                   permission.equals("In hóa đơn");
        }
        
        return false;
    }
}