// dao/NhanVienDAO.java
package hdkmanagement.dao;

import hdkmanagement.model.NhanVien;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO implements IDAO<NhanVien> {
    
    private DatabaseConnection db;
    
    public NhanVienDAO() {
        db = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean insert(NhanVien entity) {
        String sql = "INSERT INTO NhanVien (MaNV_Code, HoTen, GioiTinh, NgaySinh, DiaChi, " +
                     "SDT, Email, ChucVu, LuongCoBan, TyLeHoaHong, NgayVaoLam, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getMaNV_Code());
            ps.setString(2, entity.getHoTen());
            ps.setBoolean(3, entity.isGioiTinh());
            ps.setDate(4, entity.getNgaySinh());
            ps.setString(5, entity.getDiaChi());
            ps.setString(6, entity.getSdt());
            ps.setString(7, entity.getEmail());
            ps.setString(8, entity.getChucVu());
            ps.setDouble(9, entity.getLuongCoBan());
            ps.setDouble(10, entity.getTyLeHoaHong());
            ps.setDate(11, entity.getNgayVaoLam());
            ps.setString(12, entity.getGhiChu());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setMaNV(rs.getInt(1));
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
    public boolean update(NhanVien entity) {
        String sql = "UPDATE NhanVien SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, " +
                     "DiaChi = ?, SDT = ?, Email = ?, ChucVu = ?, LuongCoBan = ?, TyLeHoaHong = ?, " +
                     "NgayVaoLam = ?, GhiChu = ?, TrangThai = ? WHERE MaNV = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, entity.getHoTen());
            ps.setBoolean(2, entity.isGioiTinh());
            ps.setDate(3, entity.getNgaySinh());
            ps.setString(4, entity.getDiaChi());
            ps.setString(5, entity.getSdt());
            ps.setString(6, entity.getEmail());
            ps.setString(7, entity.getChucVu());
            ps.setDouble(8, entity.getLuongCoBan());
            ps.setDouble(9, entity.getTyLeHoaHong());
            ps.setDate(10, entity.getNgayVaoLam());
            ps.setString(11, entity.getGhiChu());
            ps.setBoolean(12, entity.isTrangThai());
            ps.setInt(13, entity.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE NhanVien SET TrangThai = 0 WHERE MaNV = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public NhanVien getById(int id) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhanVien(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE TrangThai = 1 ORDER BY MaNV";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<NhanVien> search(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE TrangThai = 1 AND " +
                     "(HoTen LIKE ? OR MaNV_Code LIKE ? OR SDT LIKE ? OR Email LIKE ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getInt("MaNV"));
        nv.setMaNV_Code(rs.getString("MaNV_Code"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setGioiTinh(rs.getBoolean("GioiTinh"));
        nv.setNgaySinh(rs.getDate("NgaySinh"));
        nv.setDiaChi(rs.getString("DiaChi"));
        nv.setSdt(rs.getString("SDT"));
        nv.setEmail(rs.getString("Email"));
        nv.setChucVu(rs.getString("ChucVu"));
        nv.setLuongCoBan(rs.getDouble("LuongCoBan"));
        try { nv.setTyLeHoaHong(rs.getDouble("TyLeHoaHong")); } catch (SQLException ignore) {}
        nv.setNgayVaoLam(rs.getDate("NgayVaoLam"));
        nv.setTrangThai(rs.getBoolean("TrangThai"));
        nv.setGhiChu(rs.getString("GhiChu"));
        nv.setNgayTao(rs.getTimestamp("NgayTao"));
        return nv;
    }
    
    // Tìm kiếm theo mã nhân viên
    public NhanVien getByCode(String code) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV_Code = ? AND TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhanVien(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy nhân viên theo tài khoản
    public NhanVien getByTaiKhoan(int maTK) {
        String sql = "SELECT nv.* FROM NhanVien nv " +
                     "JOIN TaiKhoan tk ON nv.MaNV = tk.MaNV " +
                     "WHERE tk.MaTK = ? AND nv.TrangThai = 1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maTK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToNhanVien(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}