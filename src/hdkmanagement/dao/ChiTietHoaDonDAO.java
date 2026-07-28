package hdkmanagement.dao;

import hdkmanagement.model.ChiTietHoaDon;
import hdkmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    private DatabaseConnection db;

    public ChiTietHoaDonDAO() {
        db = DatabaseConnection.getInstance();
    }

    public boolean insert(ChiTietHoaDon entity) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, entity.getMaHD());
            ps.setInt(2, entity.getMaSP());
            ps.setInt(3, entity.getSoLuong());
            ps.setDouble(4, entity.getDonGia());
            ps.setDouble(5, entity.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ChiTietHoaDon> getByMaHD(int maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHD = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHD(rs.getInt("MaHD"));
                ct.setMaSP(rs.getInt("MaSP"));
                ct.setSoLuong(rs.getInt("SoLuong"));
                ct.setDonGia(rs.getDouble("DonGia"));
                ct.setThanhTien(rs.getDouble("ThanhTien"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
