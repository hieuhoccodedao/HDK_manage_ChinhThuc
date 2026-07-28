package hdkmanagement.dao;

import hdkmanagement.model.KhuyenMai;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {
    private DatabaseConnection db;

    public KhuyenMaiDAO() {
        db = DatabaseConnection.getInstance();
    }

    /** Tạo mã tự động: KM000001, KM000002, ... */
    public String generateCode() {
        String sql = "SELECT COUNT(*) FROM KhuyenMai";
        try (java.sql.Statement st = db.getConnection().createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return String.format("KM%06d", rs.getInt(1) + 1);
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
        return "KM000001";
    }

    public List<KhuyenMai> getActiveKhuyenMai() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE TrangThai = 1 AND CURRENT_DATE >= NgayBatDau AND CURRENT_DATE <= NgayKetThuc ORDER BY DieuKien DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<KhuyenMai> getAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai ORDER BY MaKM DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai (MaKM_Code, TenKM, MucGiam, DieuKien, NgayBatDau, NgayKetThuc, GhiChu, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, km.getMaKMCode());
            ps.setString(2, km.getTenKM());
            ps.setDouble(3, km.getMucGiam());
            ps.setDouble(4, km.getDieuKien());
            ps.setDate(5, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(6, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(7, km.getGhiChu());
            ps.setBoolean(8, km.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET TenKM = ?, MucGiam = ?, DieuKien = ?, NgayBatDau = ?, NgayKetThuc = ?, GhiChu = ?, TrangThai = ? WHERE MaKM = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setDouble(2, km.getMucGiam());
            ps.setDouble(3, km.getDieuKien());
            ps.setDate(4, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(5, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(6, km.getGhiChu());
            ps.setBoolean(7, km.isTrangThai());
            ps.setInt(8, km.getMaKM());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE MaKM = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maKM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private KhuyenMai mapResultSet(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getInt("MaKM"));
        km.setMaKMCode(rs.getString("MaKM_Code"));
        km.setTenKM(rs.getString("TenKM"));
        km.setMucGiam(rs.getDouble("MucGiam"));
        km.setDieuKien(rs.getDouble("DieuKien"));
        km.setNgayBatDau(rs.getDate("NgayBatDau"));
        km.setNgayKetThuc(rs.getDate("NgayKetThuc"));
        km.setGhiChu(rs.getString("GhiChu"));
        km.setTrangThai(rs.getBoolean("TrangThai"));
        km.setNgayTao(rs.getTimestamp("NgayTao"));
        return km;
    }
}
