package hdkmanagement.util;

import java.sql.Connection;
import java.sql.Statement;

/**
 * DatabaseInitializer: Tự động tạo các bảng mới nếu chưa tồn tại.
 * Chạy một lần khi ứng dụng khởi động.
 */
public class DatabaseInitializer {

    public static void init() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();

            // ============================================================
            // PHASE 1: KhuyenMai & LichSuKho
            // ============================================================
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS KhuyenMai (" +
                "  MaKM         INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  MaKM_Code    VARCHAR(20) NOT NULL UNIQUE," +
                "  TenKM        NVARCHAR(100) NOT NULL," +
                "  MucGiam      DOUBLE NOT NULL DEFAULT 0 COMMENT 'Phan tram chiet khau'," +
                "  DieuKien     DOUBLE NOT NULL DEFAULT 0 COMMENT 'Tong hoa don toi thieu'," +
                "  NgayBatDau   DATE NOT NULL," +
                "  NgayKetThuc  DATE NOT NULL," +
                "  GhiChu       NVARCHAR(255)," +
                "  TrangThai    TINYINT NOT NULL DEFAULT 1," +
                "  NgayTao      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS LichSuKho (" +
                "  MaLSK            INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  MaSP             INT NOT NULL," +
                "  LoaiGiaoDich     VARCHAR(50) NOT NULL," +
                "  SoLuongThayDoi   INT NOT NULL DEFAULT 0," +
                "  TonKhoHienTai    INT NOT NULL DEFAULT 0," +
                "  ThamChieu        VARCHAR(30)," +
                "  MaNV             INT," +
                "  NgayGiaoDich     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // ============================================================
            // PHASE 2: PhieuThuChi & BangLuong
            // ============================================================
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS PhieuThuChi (" +
                "  MaPhieu      INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  MaPhieu_Code VARCHAR(20) NOT NULL UNIQUE," +
                "  LoaiPhieu    ENUM('Thu','Chi') NOT NULL," +
                "  SoTien       DOUBLE NOT NULL DEFAULT 0," +
                "  LyDo         NVARCHAR(255) NOT NULL," +
                "  DoiTuong     NVARCHAR(100)," +
                "  ThamChieu    VARCHAR(30)," +
                "  MaNV         INT," +
                "  NgayLap      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "  GhiChu       NVARCHAR(255)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS BangLuong (" +
                "  MaBangLuong  INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  MaBL_Code    VARCHAR(30) NOT NULL UNIQUE," +
                "  MaNV         INT NOT NULL," +
                "  ThangNam     VARCHAR(7) NOT NULL," +
                "  DoanhSo      DOUBLE NOT NULL DEFAULT 0," +
                "  LuongCoBan   DOUBLE NOT NULL DEFAULT 0," +
                "  TienHoaHong  DOUBLE NOT NULL DEFAULT 0," +
                "  TongLuong    DOUBLE NOT NULL DEFAULT 0," +
                "  TrangThai    VARCHAR(20) NOT NULL DEFAULT 'Cho duyet'," +
                "  NgayTao      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "  GhiChu       TEXT" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // ============================================================
            // PHASE 2: Bổ sung cột mới vào bảng cũ (dùng IF NOT EXISTS trick)
            // ============================================================
            safeAddColumn(stmt, "KhachHang", "DiemTichLuy",  "INT NOT NULL DEFAULT 0");
            safeAddColumn(stmt, "KhachHang", "TongChiTieu",  "DOUBLE NOT NULL DEFAULT 0");
            safeAddColumn(stmt, "KhachHang", "HangThe",      "VARCHAR(20) NOT NULL DEFAULT 'Dong'");
            safeAddColumn(stmt, "NhanVien",  "TyLeHoaHong",  "DOUBLE NOT NULL DEFAULT 0");

            // ============================================================
            // Dữ liệu mẫu KhuyenMai (nếu chưa có)
            // ============================================================
            stmt.executeUpdate(
                "INSERT IGNORE INTO KhuyenMai (MaKM_Code, TenKM, MucGiam, DieuKien, NgayBatDau, NgayKetThuc, TrangThai) " +
                "VALUES ('KM000001', 'Giam 10% hoa don tren 10 trieu', 10, 10000000, '2025-01-01', '2030-12-31', 1)"
            );

            stmt.close();
            System.out.println(" DatabaseInitializer: Khởi tạo DB hoàn tất.");
        } catch (Exception e) {
            System.err.println("️ DatabaseInitializer lỗi: " + e.getMessage());
        }
    }

    /** Thêm cột vào bảng nếu chưa tồn tại, bỏ qua lỗi nếu đã có */
    private static void safeAddColumn(Statement stmt, String table, String column, String definition) {
        try {
            stmt.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (Exception e) {
            // Cột đã tồn tại - bỏ qua lỗi này
        }
    }
}
