package hdkmanagement.dao;

import hdkmanagement.model.LichSuKho;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LichSuKhoDAO {
    private DatabaseConnection db;

    public LichSuKhoDAO() {
        db = DatabaseConnection.getInstance();
    }

    public List<LichSuKho> getAll() {
        List<LichSuKho> list = new ArrayList<>();
        String sql = "SELECT l.*, s.TenSP, n.HoTen as TenNV " +
                     "FROM LichSuKho l " +
                     "JOIN SanPham s ON l.MaSP = s.MaSP " +
                     "LEFT JOIN NhanVien n ON l.MaNV = n.MaNV " +
                     "ORDER BY l.MaLSK DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LichSuKho lsk = new LichSuKho();
                lsk.setMaLSK(rs.getInt("MaLSK"));
                lsk.setMaSP(rs.getInt("MaSP"));
                lsk.setTenSP(rs.getString("TenSP"));
                lsk.setLoaiGiaoDich(rs.getString("LoaiGiaoDich"));
                lsk.setSoLuongThayDoi(rs.getInt("SoLuongThayDoi"));
                lsk.setTonKhoHienTai(rs.getInt("TonKhoHienTai"));
                lsk.setThamChieu(rs.getString("ThamChieu"));
                lsk.setMaNV(rs.getInt("MaNV"));
                lsk.setTenNV(rs.getString("TenNV"));
                lsk.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich"));
                list.add(lsk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(LichSuKho lsk) {
        String sql = "INSERT INTO LichSuKho (MaSP, LoaiGiaoDich, SoLuongThayDoi, TonKhoHienTai, ThamChieu, MaNV) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lsk.getMaSP());
            ps.setString(2, lsk.getLoaiGiaoDich());
            ps.setInt(3, lsk.getSoLuongThayDoi());
            ps.setInt(4, lsk.getTonKhoHienTai());
            ps.setString(5, lsk.getThamChieu());
            ps.setInt(6, lsk.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
