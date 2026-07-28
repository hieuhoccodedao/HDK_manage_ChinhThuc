-- ============================================================
-- SCRIPT NÂNG CẤP GIAI ĐOẠN 2: Tài chính + CRM + Nhân sự
-- Chạy script này trong MySQL Workbench/HeidiSQL
-- ============================================================

USE hdk_management;

-- ============================================================
-- PHẦN D: CRM - TÍCH ĐIỂM & PHÂN HẠNG
-- ============================================================
ALTER TABLE KhachHang 
    ADD COLUMN IF NOT EXISTS DiemTichLuy     INT           NOT NULL DEFAULT 0    COMMENT 'Điểm tích lũy hiện tại',
    ADD COLUMN IF NOT EXISTS TongChiTieu     DOUBLE        NOT NULL DEFAULT 0    COMMENT 'Tổng tiền đã mua (VNĐ)',
    ADD COLUMN IF NOT EXISTS HangThe         VARCHAR(20)   NOT NULL DEFAULT 'Đồng' COMMENT 'Đồng / Bạc / Vàng / Kim Cương';

-- ============================================================
-- PHẦN E: NHÂN SỰ - TỶ LỆ HOA HỒNG
-- ============================================================
ALTER TABLE NhanVien
    ADD COLUMN IF NOT EXISTS TyLeHoaHong DOUBLE NOT NULL DEFAULT 0 COMMENT 'Phần trăm hoa hồng (%) trên doanh số';

-- ============================================================
-- PHẦN B: TÀI CHÍNH - BẢNG PHIẾU THU CHI
-- ============================================================
CREATE TABLE IF NOT EXISTS PhieuThuChi (
    MaPhieu         INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MaPhieu_Code    VARCHAR(20) NOT NULL UNIQUE,
    LoaiPhieu       ENUM('Thu', 'Chi') NOT NULL       COMMENT 'Phiếu Thu hoặc Phiếu Chi',
    SoTien          DOUBLE      NOT NULL DEFAULT 0,
    LyDo            NVARCHAR(255) NOT NULL,
    DoiTuong        NVARCHAR(100)                       COMMENT 'Tên khách hàng / NCC / Chi phí',
    ThamChieu       VARCHAR(30)                         COMMENT 'Mã HĐ / Phiếu NK liên quan',
    MaNV            INT                                 COMMENT 'Người lập phiếu',
    NgayLap         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    GhiChu          NVARCHAR(255),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- PHẦN E: NHÂN SỰ - BẢNG LƯƠNG
-- ============================================================
CREATE TABLE IF NOT EXISTS BangLuong (
    MaBangLuong     INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MaBL_Code       VARCHAR(20) NOT NULL UNIQUE,
    MaNV            INT         NOT NULL,
    ThangNam        VARCHAR(7)  NOT NULL                COMMENT 'Định dạng: YYYY-MM',
    DoanhSo         DOUBLE      NOT NULL DEFAULT 0,
    LuongCoBan      DOUBLE      NOT NULL DEFAULT 0,
    TienHoaHong     DOUBLE      NOT NULL DEFAULT 0,
    TongLuong       DOUBLE      NOT NULL DEFAULT 0,
    TrangThai       ENUM('Chờ duyệt', 'Đã duyệt', 'Đã thanh toán') NOT NULL DEFAULT 'Chờ duyệt',
    NgayTao         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    GhiChu          NVARCHAR(255),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) ON DELETE CASCADE,
    UNIQUE KEY uq_nv_thang (MaNV, ThangNam)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DỮ LIỆU MẪU
-- ============================================================
-- Thêm phiếu thu mẫu (minh họa)
INSERT IGNORE INTO PhieuThuChi (MaPhieu_Code, LoaiPhieu, SoTien, LyDo, DoiTuong) 
VALUES ('PT000001', 'Thu', 5000000, 'Thu tiền công nợ', 'Khách hàng mẫu');

SELECT 'Script cập nhật Giai đoạn 2 hoàn tất!' AS KetQua;
