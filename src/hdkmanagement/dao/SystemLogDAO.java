// dao/SystemLogDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.SystemLog;
import hdkmanagement.util.DatabaseConnection;
import hdkmanagement.util.SessionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SystemLogDAO implements IDAO<SystemLog> {

    private static SystemLogDAO instance;

    private SystemLogDAO() {}

    public static SystemLogDAO getInstance() {
        if (instance == null) {
            instance = new SystemLogDAO();
        }
        return instance;
    }

    /**
     * Ghi log hệ thống dựa vào Session hiện tại.
     * @param hanhDong Tên hành động (vd: Đăng nhập, Thêm sản phẩm)
     * @param chiTiet Chi tiết hành động
     */
    public void logAction(String hanhDong, String chiTiet) {
        int maTK = SessionManager.getInstance().getCurrentUserId();
        if (maTK == -1) {
            // Không có tài khoản đăng nhập (có thể là lỗi đăng nhập)
            // Có thể truyền null vào DB nếu cột cho phép, hoặc dùng ID admin nếu cần
            // Ở đây tạm dùng -1 và CSDL có thể báo lỗi foreign key nếu không bắt
            // Tốt nhất là cho phép MaTK = NULL trong CSDL khi không có user.
            System.err.println("Cảnh báo: Ghi log khi không có Session (Mã TK = -1)");
        }
        
        String sql = "INSERT INTO HeThongLog (MaTK, HanhDong, ChiTiet) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
             
            if (maTK == -1) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, maTK);
            }
            ps.setString(2, hanhDong);
            ps.setString(3, chiTiet);
            
            ps.executeUpdate();
            System.out.println(" Logged: [" + hanhDong + "] " + chiTiet);
        } catch (Exception e) {
            System.err.println(" Lỗi ghi log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean insert(SystemLog entity) {
        String sql = "INSERT INTO HeThongLog (MaTK, HanhDong, ChiTiet) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
             
            ps.setInt(1, entity.getMaTK());
            ps.setString(2, entity.getHanhDong());
            ps.setString(3, entity.getChiTiet());
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(SystemLog entity) {
        // Log không cho phép sửa
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM HeThongLog WHERE MaLog = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
             
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SystemLog getById(int id) {
        String sql = "SELECT * FROM HeThongLog WHERE MaLog = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
             
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SystemLog log = new SystemLog();
                    log.setMaLog(rs.getInt("MaLog"));
                    log.setMaTK(rs.getInt("MaTK"));
                    log.setHanhDong(rs.getString("HanhDong"));
                    log.setChiTiet(rs.getString("ChiTiet"));
                    log.setNgayTao(rs.getTimestamp("NgayTao"));
                    return log;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SystemLog> getAll() {
        List<SystemLog> list = new ArrayList<>();
        // Kết hợp với bảng TaiKhoan để lấy Tên đăng nhập
        String sql = "SELECT l.*, t.TenDangNhap FROM HeThongLog l " +
                     "LEFT JOIN TaiKhoan t ON l.MaTK = t.MaTK " +
                     "ORDER BY l.NgayTao DESC";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setMaLog(rs.getInt("MaLog"));
                log.setMaTK(rs.getInt("MaTK"));
                log.setTenDangNhap(rs.getString("TenDangNhap"));
                log.setHanhDong(rs.getString("HanhDong"));
                log.setChiTiet(rs.getString("ChiTiet"));
                log.setNgayTao(rs.getTimestamp("NgayTao"));
                list.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<SystemLog> search(String keyword) {
        List<SystemLog> list = new ArrayList<>();
        String sql = "SELECT l.*, t.TenDangNhap FROM HeThongLog l " +
                     "LEFT JOIN TaiKhoan t ON l.MaTK = t.MaTK " +
                     "WHERE l.HanhDong LIKE ? OR l.ChiTiet LIKE ? OR t.TenDangNhap LIKE ? " +
                     "ORDER BY l.NgayTao DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SystemLog log = new SystemLog();
                    log.setMaLog(rs.getInt("MaLog"));
                    log.setMaTK(rs.getInt("MaTK"));
                    log.setTenDangNhap(rs.getString("TenDangNhap"));
                    log.setHanhDong(rs.getString("HanhDong"));
                    log.setChiTiet(rs.getString("ChiTiet"));
                    log.setNgayTao(rs.getTimestamp("NgayTao"));
                    list.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SystemLog> getByCategory(String category, String keyword) {
        List<SystemLog> list = new ArrayList<>();
        String categoryCondition = "";
        
        if (category != null && !category.equals("Tất cả")) {
            switch (category) {
                case "Bán hàng":
                    categoryCondition = " AND (l.HanhDong LIKE '%hóa đơn%' OR l.HanhDong LIKE '%khách hàng%') ";
                    break;
                case "Nhập hàng":
                    categoryCondition = " AND l.HanhDong LIKE '%Nhập hàng%' ";
                    break;
                case "Thu / Chi":
                    categoryCondition = " AND (l.HanhDong LIKE '%phiếu thu%' OR l.HanhDong LIKE '%phiếu chi%') ";
                    break;
                case "Sản phẩm & Kho":
                    categoryCondition = " AND (l.HanhDong LIKE '%sản phẩm%' OR l.HanhDong LIKE '%danh mục%' OR l.HanhDong LIKE '%kho%') ";
                    break;
                case "Hệ thống":
                    categoryCondition = " AND (l.HanhDong LIKE '%Đăng nhập%' OR l.HanhDong LIKE '%Đăng xuất%') ";
                    break;
            }
        }
        
        String sql = "SELECT l.*, t.TenDangNhap FROM HeThongLog l " +
                     "LEFT JOIN TaiKhoan t ON l.MaTK = t.MaTK " +
                     "WHERE 1=1 " + categoryCondition;
                     
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (l.HanhDong LIKE ? OR l.ChiTiet LIKE ? OR t.TenDangNhap LIKE ?) ";
        }
        sql += " ORDER BY l.NgayTao DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SystemLog log = new SystemLog();
                    log.setMaLog(rs.getInt("MaLog"));
                    log.setMaTK(rs.getInt("MaTK"));
                    log.setTenDangNhap(rs.getString("TenDangNhap"));
                    log.setHanhDong(rs.getString("HanhDong"));
                    log.setChiTiet(rs.getString("ChiTiet"));
                    log.setNgayTao(rs.getTimestamp("NgayTao"));
                    list.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
