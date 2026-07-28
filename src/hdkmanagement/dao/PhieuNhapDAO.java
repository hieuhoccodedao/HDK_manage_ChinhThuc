// dao/PhieuNhapDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.PhieuNhap;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDAO implements IDAO<PhieuNhap> {
    
    private DatabaseConnection db;
    
    public PhieuNhapDAO() {
        db = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean insert(PhieuNhap entity) {
        String sql = "INSERT INTO PhieuNhap (MaPN_Code, MaNCC, NgayNhap, TongTien, DaThanhToan, ConNo, GhiChu, NguoiTao) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMaPN_Code());
            ps.setInt(2, entity.getMaNCC());
            ps.setDate(3, entity.getNgayNhap());
            ps.setDouble(4, entity.getTongTien());
            ps.setDouble(5, entity.getDaThanhToan());
            ps.setDouble(6, entity.getConNo());
            ps.setString(7, entity.getGhiChu());
            ps.setInt(8, entity.getNguoiTao());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setMaPN(rs.getInt(1));
                }
                String detail = "Nhập hàng mã " + entity.getMaPN_Code() + " với tổng tiền " + String.format("%,.0f", entity.getTongTien()) + " VND";
                SystemLogDAO.getInstance().logAction("Nhập hàng", detail);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(PhieuNhap entity) {
        String sql = "UPDATE PhieuNhap SET MaNCC = ?, NgayNhap = ?, TongTien = ?, DaThanhToan = ?, " +
                     "ConNo = ?, GhiChu = ?, TrangThai = ? WHERE MaPN = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, entity.getMaNCC());
            ps.setDate(2, entity.getNgayNhap());
            ps.setDouble(3, entity.getTongTien());
            ps.setDouble(4, entity.getDaThanhToan());
            ps.setDouble(5, entity.getConNo());
            ps.setString(6, entity.getGhiChu());
            ps.setBoolean(7, entity.isTrangThai());
            ps.setInt(8, entity.getMaPN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE PhieuNhap SET TrangThai = 0 WHERE MaPN = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public PhieuNhap getById(int id) {
        String sql = "SELECT * FROM PhieuNhap WHERE MaPN = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPhieuNhap(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<PhieuNhap> getAll() {
        List<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE TrangThai = 1 ORDER BY MaPN DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToPhieuNhap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<PhieuNhap> search(String keyword) {
        List<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhap WHERE TrangThai = 1 AND (MaPN_Code LIKE ? OR GhiChu LIKE ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToPhieuNhap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private PhieuNhap mapResultSetToPhieuNhap(ResultSet rs) throws SQLException {
        PhieuNhap pn = new PhieuNhap();
        pn.setMaPN(rs.getInt("MaPN"));
        pn.setMaPN_Code(rs.getString("MaPN_Code"));
        pn.setMaNCC(rs.getInt("MaNCC"));
        pn.setNgayNhap(rs.getDate("NgayNhap"));
        pn.setTongTien(rs.getDouble("TongTien"));
        pn.setDaThanhToan(rs.getDouble("DaThanhToan"));
        pn.setConNo(rs.getDouble("ConNo"));
        pn.setGhiChu(rs.getString("GhiChu"));
        pn.setTrangThai(rs.getBoolean("TrangThai"));
        pn.setNguoiTao(rs.getInt("NguoiTao"));
        pn.setNgayTao(rs.getTimestamp("NgayTao"));
        return pn;
    }

    /** Lấy tổng giá trị hàng nhập trong khoảng thời gian */
    public double getTongTienNhapByPeriod(String tuNgay, String denNgay) {
        String sql = "SELECT IFNULL(SUM(TongTien), 0) as Total FROM PhieuNhap WHERE DATE(NgayNhap) BETWEEN ? AND ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("Total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}