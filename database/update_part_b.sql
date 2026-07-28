-- =============================================
-- CẬP NHẬT CSDL: PHẦN B (Kho & Bán Hàng)
-- =============================================
USE hdk_management;

-- 1. BẢNG KHUYẾN MÃI
CREATE TABLE IF NOT EXISTS KhuyenMai (
    MaKM INT AUTO_INCREMENT PRIMARY KEY,
    MaKM_Code VARCHAR(20) UNIQUE NOT NULL,
    TenKM VARCHAR(200) NOT NULL,
    MucGiam DECIMAL(5,2) DEFAULT 0 COMMENT 'Phần trăm giảm, ví dụ: 10.00 = 10%',
    DieuKien DECIMAL(15,2) DEFAULT 0 COMMENT 'Giá trị hóa đơn tối thiểu để áp dụng',
    NgayBatDau DATE,
    NgayKetThuc DATE,
    TrangThai BOOLEAN DEFAULT TRUE,
    GhiChu TEXT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Thêm chương trình khuyến mãi theo ý user (Giảm 10% cho hóa đơn >= 10,000,000)
INSERT IGNORE INTO KhuyenMai (MaKM_Code, TenKM, MucGiam, DieuKien, NgayBatDau, NgayKetThuc, TrangThai)
VALUES ('KM-10PT', 'Giảm 10% HĐ trên 10 triệu', 10.00, 10000000, '2024-01-01', '2026-12-31', TRUE);

-- 2. BẢNG LỊCH SỬ KHO (Thẻ kho)
CREATE TABLE IF NOT EXISTS LichSuKho (
    MaLSK INT AUTO_INCREMENT PRIMARY KEY,
    MaSP INT NOT NULL,
    LoaiGiaoDich VARCHAR(50) NOT NULL COMMENT 'Nhập kho, Xuất kho, Kiểm kê...',
    SoLuongThayDoi INT NOT NULL COMMENT '+ là nhập, - là xuất',
    TonKhoHienTai INT NOT NULL COMMENT 'Số lượng tồn sau khi giao dịch',
    ThamChieu VARCHAR(100) COMMENT 'Mã Hóa đơn hoặc Mã Phiếu nhập',
    MaNV INT,
    NgayGiaoDich TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cập nhật bảng HoaDon để lưu thêm mã khuyến mãi đã dùng (tùy chọn theo dõi)
ALTER TABLE HoaDon ADD COLUMN MaKM INT NULL AFTER NhanVienBan;
ALTER TABLE HoaDon ADD CONSTRAINT fk_hoadon_km FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM) ON DELETE SET NULL;
