package hdkmanagement.dao;

import hdkmanagement.model.BangLuong;
import hdkmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangLuongDAO {

    private DatabaseConnection db;

    public BangLuongDAO() {
        db = DatabaseConnection.getInstance();
    }

    public boolean insert(BangLuong bl) {
        String sql = "INSERT INTO BangLuong (MaBL_Code, MaNV, ThangNam, DoanhSo, LuongCoBan, TienHoaHong, TongLuong, TrangThai, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bl.getMaBL_Code());
            ps.setInt(2, bl.getMaNV());
            ps.setString(3, bl.getThangNam());
            ps.setDouble(4, bl.getDoanhSo());
            ps.setDouble(5, bl.getLuongCoBan());
            ps.setDouble(6, bl.getTienHoaHong());
            ps.setDouble(7, bl.getTongLuong());
            ps.setString(8, bl.getTrangThai() != null ? bl.getTrangThai() : "Chờ duyệt");
            ps.setString(9, bl.getGhiChu());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) bl.setMaBangLuong(rs.getInt(1));
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(BangLuong bl) {
        String sql = "UPDATE BangLuong SET TrangThai=?, GhiChu=?, DoanhSo=?, LuongCoBan=?, TienHoaHong=?, TongLuong=? WHERE MaBangLuong=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, bl.getTrangThai());
            ps.setString(2, bl.getGhiChu());
            ps.setDouble(3, bl.getDoanhSo());
            ps.setDouble(4, bl.getLuongCoBan());
            ps.setDouble(5, bl.getTienHoaHong());
            ps.setDouble(6, bl.getTongLuong());
            ps.setInt(7, bl.getMaBangLuong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<BangLuong> getAll() {
        List<BangLuong> list = new ArrayList<>();
        String sql = "SELECT bl.*, nv.HoTen AS TenNV FROM BangLuong bl " +
                     "JOIN NhanVien nv ON bl.MaNV = nv.MaNV ORDER BY bl.ThangNam DESC, nv.HoTen";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<BangLuong> getByThangNam(String thangNam) {
        List<BangLuong> list = new ArrayList<>();
        String sql = "SELECT bl.*, nv.HoTen AS TenNV FROM BangLuong bl " +
                     "JOIN NhanVien nv ON bl.MaNV = nv.MaNV WHERE bl.ThangNam = ? ORDER BY nv.HoTen";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, thangNam);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Tính doanh số của nhân viên trong tháng từ bảng HoaDon */
    public double tinhDoanhSo(int maNV, String thangNam) {
        // thangNam format: YYYY-MM
        String sql = "SELECT IFNULL(SUM(TongTien), 0) FROM HoaDon " +
                     "WHERE NhanVienBan = ? AND DATE_FORMAT(NgayBan, '%Y-%m') = ? AND TrangThai != 'Hủy'";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maNV);
            ps.setString(2, thangNam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public String generateCode() {
        String sql = "SELECT MAX(MaBangLuong) FROM BangLuong";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return "BL" + String.format("%06d", rs.getInt(1) + 1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "BL000001";
    }

    public BangLuong getByNhanVienAndThangNam(int maNV, String thangNam) {
        String sql = "SELECT bl.*, nv.HoTen AS TenNV FROM BangLuong bl " +
                     "JOIN NhanVien nv ON bl.MaNV = nv.MaNV " +
                     "WHERE bl.MaNV = ? AND bl.ThangNam = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, maNV);
            ps.setString(2, thangNam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private BangLuong map(ResultSet rs) throws SQLException {
        BangLuong bl = new BangLuong();
        bl.setMaBangLuong(rs.getInt("MaBangLuong"));
        bl.setMaBL_Code(rs.getString("MaBL_Code"));
        bl.setMaNV(rs.getInt("MaNV"));
        bl.setTenNV(rs.getString("TenNV"));
        bl.setThangNam(rs.getString("ThangNam"));
        bl.setDoanhSo(rs.getDouble("DoanhSo"));
        bl.setLuongCoBan(rs.getDouble("LuongCoBan"));
        bl.setTienHoaHong(rs.getDouble("TienHoaHong"));
        bl.setTongLuong(rs.getDouble("TongLuong"));
        bl.setTrangThai(rs.getString("TrangThai"));
        bl.setNgayTao(rs.getTimestamp("NgayTao"));
        bl.setGhiChu(rs.getString("GhiChu"));
        return bl;
    }
}
