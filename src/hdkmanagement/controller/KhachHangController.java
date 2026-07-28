// controller/KhachHangController.java
package hdkmanagement.controller;

import hdkmanagement.dao.KhachHangDAO;
import hdkmanagement.model.KhachHang;
import hdkmanagement.util.ValidateUtil;

import java.util.List;

public class KhachHangController extends BaseController {
    
    private KhachHangDAO khachHangDAO;
    
    public KhachHangController() {
        super();
        this.khachHangDAO = new KhachHangDAO();
    }
    
    // Lấy tất cả khách hàng
    public List<KhachHang> getAllKhachHang() {
        try {
            return khachHangDAO.getAll();
        } catch (Exception e) {
            showError("Lỗi lấy danh sách khách hàng: " + e.getMessage());
            return null;
        }
    }
    
    // Lấy khách hàng theo ID
    public KhachHang getKhachHangById(int id) {
        try {
            return khachHangDAO.getById(id);
        } catch (Exception e) {
            showError("Lỗi lấy thông tin khách hàng: " + e.getMessage());
            return null;
        }
    }
    
    // Thêm khách hàng
    public boolean addKhachHang(KhachHang khachHang) {
        try {
            if (!validateKhachHang(khachHang)) {
                return false;
            }
            
            if (khachHangDAO.getByPhone(khachHang.getSdt()) != null) {
                showError("Số điện thoại đã tồn tại!");
                return false;
            }
            
            boolean success = khachHangDAO.insert(khachHang);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Thêm khách hàng", "Thêm khách hàng: " + khachHang.getHoTen() + " (" + khachHang.getSdt() + ")");
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi thêm khách hàng: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật khách hàng
    public boolean updateKhachHang(KhachHang khachHang) {
        try {
            if (!validateKhachHang(khachHang)) {
                return false;
            }
            boolean success = khachHangDAO.update(khachHang);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Cập nhật khách hàng", "Cập nhật khách hàng: " + khachHang.getHoTen() + " (" + khachHang.getSdt() + ")");
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi cập nhật khách hàng: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa khách hàng
    public boolean deleteKhachHang(int id) {
        try {
            if (!showConfirm("Bạn có chắc muốn xóa khách hàng này?")) {
                return false;
            }
            boolean success = khachHangDAO.delete(id);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Xóa khách hàng", "Xóa khách hàng ID: " + id);
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi xóa khách hàng: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm khách hàng
    public List<KhachHang> searchKhachHang(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return khachHangDAO.getAll();
            }
            return khachHangDAO.search(keyword);
        } catch (Exception e) {
            showError("Lỗi tìm kiếm khách hàng: " + e.getMessage());
            return null;
        }
    }
    
    // Tìm khách hàng theo số điện thoại
    public KhachHang getKhachHangByPhone(String phone) {
        try {
            return khachHangDAO.getByPhone(phone);
        } catch (Exception e) {
            showError("Lỗi tìm khách hàng: " + e.getMessage());
            return null;
        }
    }
    
    // Validate khách hàng
    private boolean validateKhachHang(KhachHang khachHang) {
        if (khachHang.getHoTen() == null || khachHang.getHoTen().trim().isEmpty()) {
            showError("Họ tên không được để trống!");
            return false;
        }
        
        if (khachHang.getSdt() == null || khachHang.getSdt().trim().isEmpty()) {
            showError("Số điện thoại không được để trống!");
            return false;
        }
        
        if (!ValidateUtil.isValidPhone(khachHang.getSdt())) {
            showError("Số điện thoại không hợp lệ!");
            return false;
        }
        
        return true;
    }
}