// controller/SanPhamController.java
package hdkmanagement.controller;

import hdkmanagement.dao.SanPhamDAO;
import hdkmanagement.dao.DanhMucDAO;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.DanhMuc;
import hdkmanagement.util.ValidateUtil;

import java.util.List;

public class SanPhamController extends BaseController {
    
    private SanPhamDAO sanPhamDAO;
    private DanhMucDAO danhMucDAO;
    
    public SanPhamController() {
        super();
        this.sanPhamDAO = new SanPhamDAO();
        this.danhMucDAO = new DanhMucDAO();
    }
    
    // Lấy tất cả sản phẩm
    public List<SanPham> getAllSanPham() {
        try {
            return sanPhamDAO.getAll();
        } catch (Exception e) {
            showError("Lỗi lấy danh sách sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    // Lấy sản phẩm theo ID
    public SanPham getSanPhamById(int id) {
        try {
            return sanPhamDAO.getById(id);
        } catch (Exception e) {
            showError("Lỗi lấy thông tin sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    // Thêm sản phẩm mới
    public boolean addSanPham(SanPham sanPham) {
        try {
            // Validate dữ liệu
            if (!validateSanPham(sanPham)) {
                return false;
            }
            
            // Kiểm tra mã trùng
            if (sanPhamDAO.getByCode(sanPham.getMaSP_Code()) != null) {
                showError("Mã sản phẩm đã tồn tại!");
                return false;
            }
            
            boolean success = sanPhamDAO.insert(sanPham);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Thêm sản phẩm", "Thêm sản phẩm: " + sanPham.getTenSP() + " (" + sanPham.getMaSP_Code() + ")");
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật sản phẩm
    public boolean updateSanPham(SanPham sanPham) {
        try {
            if (!validateSanPham(sanPham)) {
                return false;
            }
            boolean success = sanPhamDAO.update(sanPham);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Cập nhật sản phẩm", "Cập nhật sản phẩm: " + sanPham.getTenSP() + " (" + sanPham.getMaSP_Code() + ")");
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa sản phẩm
    public boolean deleteSanPham(int id) {
        try {
            if (!showConfirm("Bạn có chắc muốn xóa sản phẩm này?")) {
                return false;
            }
            boolean success = sanPhamDAO.delete(id);
            if (success) {
                hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Xóa sản phẩm", "Xóa sản phẩm ID: " + id);
            }
            return success;
        } catch (Exception e) {
            showError("Lỗi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm kiếm sản phẩm
    public List<SanPham> searchSanPham(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return sanPhamDAO.getAll();
            }
            return sanPhamDAO.search(keyword);
        } catch (Exception e) {
            showError("Lỗi tìm kiếm sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    // Lấy sản phẩm sắp hết
    public List<SanPham> getNearExpiryProducts() {
        try {
            return sanPhamDAO.getProductsNearExpiry();
        } catch (Exception e) {
            showError("Lỗi lấy sản phẩm sắp hết: " + e.getMessage());
            return null;
        }
    }
    
    // Lấy sản phẩm bán chạy
    public List<SanPham> getBestSellers() {
        try {
            return sanPhamDAO.getBestSellers();
        } catch (Exception e) {
            showError("Lỗi lấy sản phẩm bán chạy: " + e.getMessage());
            return null;
        }
    }
    
    // Cập nhật tồn kho
    public boolean updateTonKho(int maSP, int soLuong) {
        try {
            if (soLuong < 0) {
                showError("Số lượng không hợp lệ!");
                return false;
            }
            return sanPhamDAO.updateTonKho(maSP, soLuong);
        } catch (Exception e) {
            showError("Lỗi cập nhật tồn kho: " + e.getMessage());
            return false;
        }
    }
    
    // Validate sản phẩm
    private boolean validateSanPham(SanPham sanPham) {
        if (sanPham.getTenSP() == null || sanPham.getTenSP().trim().isEmpty()) {
            showError("Tên sản phẩm không được để trống!");
            return false;
        }
        
        if (sanPham.getMaSP_Code() == null || sanPham.getMaSP_Code().trim().isEmpty()) {
            showError("Mã sản phẩm không được để trống!");
            return false;
        }
        
        if (sanPham.getGiaBan() < 0) {
            showError("Giá bán không hợp lệ!");
            return false;
        }
        
        if (sanPham.getGiaNhap() < 0) {
            showError("Giá nhập không hợp lệ!");
            return false;
        }
        
        if (sanPham.getTonKho() < 0) {
            showError("Tồn kho không hợp lệ!");
            return false;
        }
        
        return true;
    }
    
    // Lấy tổng số sản phẩm
    public int getTotalProducts() {
        try {
            return sanPhamDAO.getAll().size();
        } catch (Exception e) {
            return 0;
        }
    }
}