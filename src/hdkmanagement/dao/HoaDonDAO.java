// dao/HoaDonDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.HoaDon;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO implements IDAO<HoaDon> {
    
    private DatabaseConnection db;
    
    public HoaDonDAO() {
        db = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean insert(HoaDon entity) {
        String sql = "INSERT INTO HoaDon (MaHD_Code, MaKH, NgayBan, TongTien, ChietKhau, DaThanhToan, ConNo, HinhThucThanhToan, GhiChu, NhanVienBan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMaHD_Code());
            ps.setInt(2, entity.getMaKH());
            ps.setDate(3, entity.getNgayBan());
            ps.setDouble(4, entity.getTongTien());
            ps.setDouble(5, entity.getChietKhau());
            ps.setDouble(6, entity.getDaThanhToan());
            ps.setDouble(7, entity.getConNo());
            ps.setString(8, entity.getHinhThucThanhToan());
            ps.setString(9, entity.getGhiChu());
            ps.setInt(10, entity.getNhanVienBan());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setMaHD(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(HoaDon entity) {
        String sql = "UPDATE HoaDon SET MaKH = ?, NgayBan = ?, TongTien = ?, ChietKhau = ?, " +
                     "DaThanhToan = ?, ConNo = ?, HinhThucThanhToan = ?, GhiChu = ?, TrangThai = ? WHERE MaHD = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, entity.getMaKH());
            ps.setDate(2, entity.getNgayBan());
            ps.setDouble(3, entity.getTongTien());
            ps.setDouble(4, entity.getChietKhau());
            ps.setDouble(5, entity.getDaThanhToan());
            ps.setDouble(6, entity.getConNo());
            ps.setString(7, entity.getHinhThucThanhToan());
            ps.setString(8, entity.getGhiChu());
            ps.setBoolean(9, entity.isTrangThai());
            ps.setInt(10, entity.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE HoaDon SET TrangThai = 0 WHERE MaHD = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public HoaDon getById(int id) {
        String sql = "SELECT * FROM HoaDon WHERE MaHD = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToHoaDon(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE TrangThai = 1 ORDER BY MaHD DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<HoaDon> search(String keyword) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE TrangThai = 1 AND (MaHD_Code LIKE ? OR GhiChu LIKE ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy tổng doanh thu
    public double getTotalRevenue() {
        String sql = "SELECT SUM(TongTien) as Total FROM HoaDon WHERE TrangThai = 1";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("Total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Lấy doanh thu theo ngày
    public double getRevenueByDate(Date date) {
        String sql = "SELECT SUM(TongTien) as Total FROM HoaDon WHERE TrangThai = 1 AND DATE(NgayBan) = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getInt("MaHD"));
        hd.setMaHD_Code(rs.getString("MaHD_Code"));
        hd.setMaKH(rs.getInt("MaKH"));
        hd.setNgayBan(rs.getDate("NgayBan"));
        hd.setTongTien(rs.getDouble("TongTien"));
        hd.setChietKhau(rs.getDouble("ChietKhau"));
        hd.setDaThanhToan(rs.getDouble("DaThanhToan"));
        hd.setConNo(rs.getDouble("ConNo"));
        hd.setHinhThucThanhToan(rs.getString("HinhThucThanhToan"));
        hd.setGhiChu(rs.getString("GhiChu"));
        hd.setTrangThai(rs.getBoolean("TrangThai"));
        hd.setNhanVienBan(rs.getInt("NhanVienBan"));
        hd.setNgayTao(rs.getTimestamp("NgayTao"));
        return hd;
    }

    /** Lấy tổng doanh thu trong khoảng thời gian */
    public double getRevenueByPeriod(String tuNgay, String denNgay) {
        String sql = "SELECT IFNULL(SUM(TongTien), 0) as Total FROM HoaDon WHERE DATE(NgayBan) BETWEEN ? AND ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("Total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Giá vốn hàng bán = tổng (GiaNhap * SoLuong) của các chi tiết HĐ trong kỳ */
    public double getGiaVonByPeriod(String tuNgay, String denNgay) {
        String sql = "SELECT IFNULL(SUM(ct.SoLuong * sp.GiaNhap), 0) AS GiaVon " +
                     "FROM ChiTietHoaDon ct " +
                     "JOIN HoaDon hd ON ct.MaHD = hd.MaHD " +
                     "JOIN SanPham sp ON ct.MaSP = sp.MaSP " +
                     "WHERE DATE(hd.NgayBan) BETWEEN ? AND ? AND hd.TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("GiaVon");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}