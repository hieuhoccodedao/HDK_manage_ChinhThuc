// dao/NhaCungCapDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.NhaCungCap;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO implements IDAO<NhaCungCap> {
    
    private DatabaseConnection db;
    
    public NhaCungCapDAO() {
        db = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean insert(NhaCungCap entity) {
        String sql = "INSERT INTO NhaCungCap (MaNCC_Code, TenNCC, NguoiDaiDien, DiaChi, SDT, Email, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMaNCC_Code());
            ps.setString(2, entity.getTenNCC());
            ps.setString(3, entity.getNguoiDaiDien());
            ps.setString(4, entity.getDiaChi());
            ps.setString(5, entity.getSdt());
            ps.setString(6, entity.getEmail());
            ps.setString(7, entity.getGhiChu());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setMaNCC(rs.getInt(1));
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
    public boolean update(NhaCungCap entity) {
        String sql = "UPDATE NhaCungCap SET TenNCC = ?, NguoiDaiDien = ?, DiaChi = ?, " +
                     "SDT = ?, Email = ?, GhiChu = ?, TrangThai = ?, CongNo = ? WHERE MaNCC = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, entity.getTenNCC());
            ps.setString(2, entity.getNguoiDaiDien());
            ps.setString(3, entity.getDiaChi());
            ps.setString(4, entity.getSdt());
            ps.setString(5, entity.getEmail());
            ps.setString(6, entity.getGhiChu());
            ps.setBoolean(7, entity.isTrangThai());
            ps.setDouble(8, entity.getCongNo());
            ps.setInt(9, entity.getMaNCC());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE NhaCungCap SET TrangThai = 0 WHERE MaNCC = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public NhaCungCap getById(int id) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNCC = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhaCungCap(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap WHERE TrangThai = 1 ORDER BY MaNCC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNhaCungCap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<NhaCungCap> search(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap WHERE TrangThai = 1 AND " +
                     "(TenNCC LIKE ? OR MaNCC_Code LIKE ? OR SDT LIKE ? OR Email LIKE ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToNhaCungCap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private NhaCungCap mapResultSetToNhaCungCap(ResultSet rs) throws SQLException {
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNCC(rs.getInt("MaNCC"));
        ncc.setMaNCC_Code(rs.getString("MaNCC_Code"));
        ncc.setTenNCC(rs.getString("TenNCC"));
        ncc.setNguoiDaiDien(rs.getString("NguoiDaiDien"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSdt(rs.getString("SDT"));
        ncc.setEmail(rs.getString("Email"));
        ncc.setCongNo(rs.getDouble("CongNo"));
        ncc.setGhiChu(rs.getString("GhiChu"));
        ncc.setTrangThai(rs.getBoolean("TrangThai"));
        ncc.setNgayTao(rs.getTimestamp("NgayTao"));
        return ncc;
    }
    
    public NhaCungCap getByCode(String code) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNCC_Code = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhaCungCap(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public NhaCungCap getByPhone(String phone) {
        String sql = "SELECT * FROM NhaCungCap WHERE SDT = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhaCungCap(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}