package hdkmanagement.dao;

import hdkmanagement.model.PhieuThuChi;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuThuChiDAO {

    private DatabaseConnection db;

    public PhieuThuChiDAO() {
        db = DatabaseConnection.getInstance();
    }

    public boolean insert(PhieuThuChi p) {
        String sql = "INSERT INTO PhieuThuChi (MaPhieu_Code, LoaiPhieu, SoTien, LyDo, DoiTuong, ThamChieu, MaNV, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getMaPhieu_Code());
            ps.setString(2, p.getLoaiPhieu());
            ps.setDouble(3, p.getSoTien());
            ps.setString(4, p.getLyDo());
            ps.setString(5, p.getDoiTuong());
            ps.setString(6, p.getThamChieu());
            if (p.getMaNV() > 0) ps.setInt(7, p.getMaNV()); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, p.getGhiChu());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setMaPhieu(rs.getInt(1));
                
                String action = p.getLoaiPhieu().equalsIgnoreCase("THU") ? "Lập phiếu thu" : "Lập phiếu chi";
                String detail = action + " " + p.getMaPhieu_Code() + " với số tiền " + String.format("%,.0f", p.getSoTien()) + " VND. Lý do: " + p.getLyDo();
                SystemLogDAO.getInstance().logAction(action, detail);
                
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PhieuThuChi> getAll() {
        List<PhieuThuChi> list = new ArrayList<>();
        String sql = "SELECT p.*, nv.HoTen AS TenNV FROM PhieuThuChi p " +
                     "LEFT JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "ORDER BY p.NgayLap DESC";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<PhieuThuChi> getByLoai(String loai) {
        List<PhieuThuChi> list = new ArrayList<>();
        String sql = "SELECT p.*, nv.HoTen AS TenNV FROM PhieuThuChi p " +
                     "LEFT JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "WHERE p.LoaiPhieu = ? ORDER BY p.NgayLap DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, loai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Tổng tiền thu trong khoảng thời gian */
    public double getTongThu(String tuNgay, String denNgay) {
        return getTong("Thu", tuNgay, denNgay);
    }

    /** Tổng tiền chi trong khoảng thời gian (loại trừ tiền nhập hàng để không làm sai lợi nhuận) */
    public double getTongChi(String tuNgay, String denNgay) {
        String sql = "SELECT IFNULL(SUM(SoTien),0) FROM PhieuThuChi WHERE LoaiPhieu='Chi' AND DATE(NgayLap) BETWEEN ? AND ? AND LyDo NOT LIKE 'Chi tiền nhập hàng%'";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private double getTong(String loai, String tuNgay, String denNgay) {
        String sql = "SELECT IFNULL(SUM(SoTien),0) FROM PhieuThuChi WHERE LoaiPhieu=? AND DATE(NgayLap) BETWEEN ? AND ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, loai);
            ps.setString(2, tuNgay);
            ps.setString(3, denNgay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Tạo mã phiếu tự động */
    public String generateCode(String prefix) {
        String sql = "SELECT COUNT(*) FROM PhieuThuChi WHERE LoaiPhieu = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, prefix.equals("PT") ? "Thu" : "Chi");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return prefix + String.format("%06d", rs.getInt(1) + 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return prefix + "000001";
    }

    private PhieuThuChi map(ResultSet rs) throws SQLException {
        PhieuThuChi p = new PhieuThuChi();
        p.setMaPhieu(rs.getInt("MaPhieu"));
        p.setMaPhieu_Code(rs.getString("MaPhieu_Code"));
        p.setLoaiPhieu(rs.getString("LoaiPhieu"));
        p.setSoTien(rs.getDouble("SoTien"));
        p.setLyDo(rs.getString("LyDo"));
        p.setDoiTuong(rs.getString("DoiTuong"));
        p.setThamChieu(rs.getString("ThamChieu"));
        p.setMaNV(rs.getInt("MaNV"));
        p.setTenNV(rs.getString("TenNV"));
        p.setNgayLap(rs.getTimestamp("NgayLap"));
        p.setGhiChu(rs.getString("GhiChu"));
        return p;
    }
}
