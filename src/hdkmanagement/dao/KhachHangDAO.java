// dao/KhachHangDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.KhachHang;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO implements IDAO<KhachHang> {
    
    private DatabaseConnection db;
    
    public KhachHangDAO() {
        db = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean insert(KhachHang entity) {
        String sql = "INSERT INTO KhachHang (MaKH_Code, HoTen, SDT, Email, DiaChi, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMaKH_Code());
            ps.setString(2, entity.getHoTen());
            ps.setString(3, entity.getSdt());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getDiaChi());
            ps.setString(6, entity.getGhiChu());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setMaKH(rs.getInt(1));
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
    public boolean update(KhachHang entity) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SDT = ?, Email = ?, " +
                     "DiaChi = ?, GhiChu = ?, TrangThai = ?, " +
                     "DiemTichLuy = ?, TongChiTieu = ?, HangThe = ?, CongNo = ? WHERE MaKH = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, entity.getHoTen());
            ps.setString(2, entity.getSdt());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getDiaChi());
            ps.setString(5, entity.getGhiChu());
            ps.setBoolean(6, entity.isTrangThai());
            ps.setInt(7, entity.getDiemTichLuy());
            ps.setDouble(8, entity.getTongChiTieu());
            ps.setString(9, entity.getHangThe());
            ps.setDouble(10, entity.getCongNo());
            ps.setInt(11, entity.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE KhachHang SET TrangThai = 0 WHERE MaKH = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public KhachHang getById(int id) {
        String sql = "SELECT * FROM KhachHang WHERE MaKH = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToKhachHang(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE TrangThai = 1 ORDER BY MaKH";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKhachHang(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<KhachHang> search(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE TrangThai = 1 AND " +
                     "(HoTen LIKE ? OR SDT LIKE ? OR Email LIKE ? OR MaKH_Code LIKE ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToKhachHang(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setMaKH(rs.getInt("MaKH"));
        kh.setMaKH_Code(rs.getString("MaKH_Code"));
        kh.setHoTen(rs.getString("HoTen"));
        kh.setSdt(rs.getString("SDT"));
        kh.setEmail(rs.getString("Email"));
        kh.setDiaChi(rs.getString("DiaChi"));
        kh.setCongNo(rs.getDouble("CongNo"));
        kh.setGhiChu(rs.getString("GhiChu"));
        kh.setTrangThai(rs.getBoolean("TrangThai"));
        kh.setNgayTao(rs.getTimestamp("NgayTao"));
        try { kh.setDiemTichLuy(rs.getInt("DiemTichLuy")); } catch (SQLException ignore) {}
        try { kh.setTongChiTieu(rs.getDouble("TongChiTieu")); } catch (SQLException ignore) {}
        try { kh.setHangThe(rs.getString("HangThe")); } catch (SQLException ignore) {}
        return kh;
    }
    
    public KhachHang getByPhone(String phone) {
        String sql = "SELECT * FROM KhachHang WHERE SDT = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToKhachHang(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public KhachHang getByCode(String code) {
        String sql = "SELECT * FROM KhachHang WHERE MaKH_Code = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToKhachHang(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}