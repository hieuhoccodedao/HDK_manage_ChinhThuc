package hdkmanagement.dao;

import hdkmanagement.model.ChiTietPhieuNhap;
import hdkmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAO {
    private DatabaseConnection db;

    public ChiTietPhieuNhapDAO() {
        db = DatabaseConnection.getInstance();
    }

    public boolean insert(ChiTietPhieuNhap entity) {
        String sql = "INSERT INTO ChiTietPhieuNhap (MaPN, MaSP, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, entity.getMaPN());
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

    public List<ChiTietPhieuNhap> getByMaPN(int maPN) {
        List<ChiTietPhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaPN = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maPN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                ct.setMaPN(rs.getInt("MaPN"));
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
