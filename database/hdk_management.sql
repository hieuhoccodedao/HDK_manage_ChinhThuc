-- =============================================
-- CSDL: hdk_management
-- Hệ thống quản lý Công Ty HDK
-- =============================================

CREATE DATABASE IF NOT EXISTS hdk_management 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hdk_management;

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- BẢNG: Quyen (Quyền hạn)
-- =============================================
CREATE TABLE IF NOT EXISTS Quyen (
    MaQuyen INT AUTO_INCREMENT PRIMARY KEY,
    TenQuyen VARCHAR(50) NOT NULL,
    MoTa VARCHAR(200),
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: NhanVien (Nhân viên)
-- =============================================
CREATE TABLE IF NOT EXISTS NhanVien (
    MaNV INT AUTO_INCREMENT PRIMARY KEY,
    MaNV_Code VARCHAR(20) UNIQUE NOT NULL,
    HoTen VARCHAR(100) NOT NULL,
    GioiTinh BOOLEAN DEFAULT TRUE COMMENT '1=Nam, 0=Nữ',
    NgaySinh DATE,
    DiaChi VARCHAR(200),
    SDT VARCHAR(15),
    Email VARCHAR(100),
    ChucVu VARCHAR(50),
    LuongCoBan DECIMAL(15,2) DEFAULT 0,
    NgayVaoLam DATE,
    TrangThai BOOLEAN DEFAULT TRUE,
    GhiChu TEXT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: TaiKhoan (Tài khoản đăng nhập)
-- =============================================
CREATE TABLE IF NOT EXISTS TaiKhoan (
    MaTK INT AUTO_INCREMENT PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhau VARCHAR(64) NOT NULL COMMENT 'MD5 hash',
    MaNV INT NOT NULL,
    MaQuyen INT NOT NULL,
    TrangThai BOOLEAN DEFAULT TRUE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaQuyen) REFERENCES Quyen(MaQuyen)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: DanhMuc (Danh mục sản phẩm)
-- =============================================
CREATE TABLE IF NOT EXISTS DanhMuc (
    MaDM INT AUTO_INCREMENT PRIMARY KEY,
    MaDM_Code VARCHAR(20) UNIQUE NOT NULL,
    TenDanhMuc VARCHAR(100) NOT NULL,
    MoTa TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: SanPham (Sản phẩm)
-- =============================================
CREATE TABLE IF NOT EXISTS SanPham (
    MaSP INT AUTO_INCREMENT PRIMARY KEY,
    MaSP_Code VARCHAR(20) UNIQUE NOT NULL,
    TenSP VARCHAR(200) NOT NULL,
    MaDM INT,
    DonViTinh VARCHAR(30),
    GiaNhap DECIMAL(15,2) DEFAULT 0,
    GiaBan DECIMAL(15,2) DEFAULT 0,
    TonKho INT DEFAULT 0,
    TonToiThieu INT DEFAULT 5,
    HinhAnh VARCHAR(300),
    MoTa TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDM) REFERENCES DanhMuc(MaDM)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: Kho (Kho hàng)
-- =============================================
CREATE TABLE IF NOT EXISTS Kho (
    MaKho INT AUTO_INCREMENT PRIMARY KEY,
    MaSP INT UNIQUE NOT NULL,
    SoLuong INT DEFAULT 0,
    ViTri VARCHAR(100),
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: KhachHang (Khách hàng)
-- =============================================
CREATE TABLE IF NOT EXISTS KhachHang (
    MaKH INT AUTO_INCREMENT PRIMARY KEY,
    MaKH_Code VARCHAR(20) UNIQUE NOT NULL,
    HoTen VARCHAR(100) NOT NULL,
    SDT VARCHAR(15),
    Email VARCHAR(100),
    DiaChi VARCHAR(200),
    CongNo DECIMAL(15,2) DEFAULT 0,
    GhiChu TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: NhaCungCap (Nhà cung cấp)
-- =============================================
CREATE TABLE IF NOT EXISTS NhaCungCap (
    MaNCC INT AUTO_INCREMENT PRIMARY KEY,
    MaNCC_Code VARCHAR(20) UNIQUE NOT NULL,
    TenNCC VARCHAR(100) NOT NULL,
    NguoiDaiDien VARCHAR(100),
    DiaChi VARCHAR(200),
    SDT VARCHAR(15),
    Email VARCHAR(100),
    CongNo DECIMAL(15,2) DEFAULT 0,
    GhiChu TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: HoaDon (Hóa đơn bán hàng)
-- =============================================
CREATE TABLE IF NOT EXISTS HoaDon (
    MaHD INT AUTO_INCREMENT PRIMARY KEY,
    MaHD_Code VARCHAR(20) UNIQUE NOT NULL,
    MaKH INT,
    NgayBan DATE NOT NULL,
    TongTien DECIMAL(15,2) DEFAULT 0,
    ChietKhau DECIMAL(15,2) DEFAULT 0,
    DaThanhToan DECIMAL(15,2) DEFAULT 0,
    ConNo DECIMAL(15,2) DEFAULT 0,
    HinhThucThanhToan VARCHAR(50) DEFAULT 'Tiền mặt',
    GhiChu TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NhanVienBan INT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH),
    FOREIGN KEY (NhanVienBan) REFERENCES NhanVien(MaNV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: ChiTietHoaDon (Chi tiết hóa đơn)
-- =============================================
CREATE TABLE IF NOT EXISTS ChiTietHoaDon (
    MaCTHD INT AUTO_INCREMENT PRIMARY KEY,
    MaHD INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(15,2) NOT NULL,
    ChietKhau DECIMAL(15,2) DEFAULT 0,
    ThanhTien DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: PhieuNhap (Phiếu nhập hàng)
-- =============================================
CREATE TABLE IF NOT EXISTS PhieuNhap (
    MaPN INT AUTO_INCREMENT PRIMARY KEY,
    MaPN_Code VARCHAR(20) UNIQUE NOT NULL,
    MaNCC INT,
    NgayNhap DATE NOT NULL,
    TongTien DECIMAL(15,2) DEFAULT 0,
    DaThanhToan DECIMAL(15,2) DEFAULT 0,
    ConNo DECIMAL(15,2) DEFAULT 0,
    GhiChu TEXT,
    TrangThai BOOLEAN DEFAULT TRUE,
    NguoiTao INT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC),
    FOREIGN KEY (NguoiTao) REFERENCES NhanVien(MaNV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: ChiTietPhieuNhap (Chi tiết phiếu nhập)
-- =============================================
CREATE TABLE IF NOT EXISTS ChiTietPhieuNhap (
    MaCTPN INT AUTO_INCREMENT PRIMARY KEY,
    MaPN INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(15,2) NOT NULL,
    ThanhTien DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (MaPN) REFERENCES PhieuNhap(MaPN),
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- BẢNG: HeThongLog (Nhật ký hệ thống - Audit Trail)
-- =============================================
CREATE TABLE IF NOT EXISTS HeThongLog (
    MaLog INT AUTO_INCREMENT PRIMARY KEY,
    MaTK INT,
    HanhDong VARCHAR(100) NOT NULL,
    ChiTiet TEXT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaTK) REFERENCES TaiKhoan(MaTK) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- DỮ LIỆU MẪU
-- =============================================

-- Quyền hạn
INSERT IGNORE INTO Quyen (MaQuyen, TenQuyen, MoTa) VALUES
(1, 'Admin', 'Quản trị viên hệ thống - Có tất cả quyền'),
(2, 'Quản lý', 'Quản lý cửa hàng - Xem và chỉnh sửa hầu hết dữ liệu'),
(3, 'Nhân viên', 'Nhân viên bán hàng - Chỉ thao tác bán hàng cơ bản');

-- Nhân viên
INSERT IGNORE INTO NhanVien (MaNV, MaNV_Code, HoTen, GioiTinh, NgaySinh, DiaChi, SDT, Email, ChucVu, LuongCoBan, NgayVaoLam) VALUES
(1, 'NV001', 'Nguyễn Văn Admin', TRUE, '1990-01-15', 'Hà Nội', '0901234567', 'admin@hdk.vn', 'Quản trị viên', 15000000, '2020-01-01'),
(2, 'NV002', 'Trần Thị Quản Lý', FALSE, '1992-05-20', 'TP.HCM', '0912345678', 'quanly@hdk.vn', 'Quản lý', 12000000, '2020-03-01'),
(3, 'NV003', 'Lê Văn Nhân Viên', TRUE, '1995-08-10', 'Đà Nẵng', '0923456789', 'nhanvien@hdk.vn', 'Nhân viên bán hàng', 8000000, '2021-06-01');

-- Tài khoản (mật khẩu mặc định: 123456 -> MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT IGNORE INTO TaiKhoan (TenDangNhap, MatKhau, MaNV, MaQuyen) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, 1),
('quanly', 'e10adc3949ba59abbe56e057f20f883e', 2, 2),
('nhanvien', 'e10adc3949ba59abbe56e057f20f883e', 3, 3);

-- Danh mục sản phẩm
INSERT IGNORE INTO DanhMuc (MaDM, MaDM_Code, TenDanhMuc, MoTa) VALUES
(1, 'DM001', 'Xi măng', 'Các loại xi măng xây dựng'),
(2, 'DM002', 'Gạch', 'Các loại gạch xây dựng'),
(3, 'DM003', 'Cát - Đá', 'Cát, đá xây dựng các loại'),
(4, 'DM004', 'Sắt thép', 'Sắt thép xây dựng các loại'),
(5, 'DM005', 'Sơn', 'Sơn tường và sơn công nghiệp'),
(6, 'DM006', 'Ống nước', 'Ống nước nhựa và kim loại'),
(7, 'DM007', 'Ngói lợp', 'Ngói lợp mái các loại'),
(8, 'DM008', 'Vật liệu hoàn thiện', 'Gạch ốp lát, kính, cửa...');

-- Sản phẩm
INSERT IGNORE INTO SanPham (MaSP_Code, TenSP, MaDM, DonViTinh, GiaNhap, GiaBan, TonKho, TonToiThieu) VALUES
('SP001', 'Xi măng Hà Tiên PCB40', 1, 'Bao 50kg', 75000, 90000, 500, 50),
('SP002', 'Xi măng Hoàng Thạch PCB40', 1, 'Bao 50kg', 72000, 87000, 300, 30),
('SP003', 'Xi măng Nghi Sơn PCB50', 1, 'Bao 50kg', 80000, 95000, 200, 20),
('SP004', 'Gạch ống 4 lỗ 8x8x19', 2, 'Viên', 1200, 1800, 50000, 5000),
('SP005', 'Gạch đặc 5x10x20', 2, 'Viên', 800, 1200, 30000, 3000),
('SP006', 'Cát vàng xây dựng', 3, 'Khối', 250000, 350000, 200, 20),
('SP007', 'Đá 1x2 xây dựng', 3, 'Khối', 200000, 280000, 150, 15),
('SP008', 'Sắt phi 10 CB240T', 4, 'Kg', 18000, 22000, 5000, 500),
('SP009', 'Sắt phi 12 CB300T', 4, 'Kg', 19000, 23500, 4000, 400),
('SP010', 'Sắt phi 16 CB300T', 4, 'Kg', 20000, 25000, 3000, 300),
('SP011', 'Sơn Dulux nội thất 5L', 5, 'Thùng', 280000, 380000, 100, 10),
('SP012', 'Sơn Jotun ngoại thất 5L', 5, 'Thùng', 320000, 430000, 80, 8),
('SP013', 'Ống nước nhựa PPR D25', 6, 'Ống 4m', 45000, 65000, 500, 50),
('SP014', 'Ống nước nhựa PVC D90', 6, 'Ống 4m', 85000, 120000, 300, 30),
('SP015', 'Ngói lợp Hạ Long màu đỏ', 7, 'Viên', 8000, 12000, 10000, 1000);

-- Kho hàng (đồng bộ với sản phẩm)
INSERT IGNORE INTO Kho (MaSP, SoLuong, ViTri) 
SELECT MaSP, TonKho, CONCAT('Khu A - Lô ', MaSP) FROM SanPham;

-- Khách hàng
INSERT IGNORE INTO KhachHang (MaKH, MaKH_Code, HoTen, SDT, Email, DiaChi, CongNo) VALUES
(1, 'KH001', 'Công ty Xây dựng Phú Quý', '0901000001', 'phoquy@email.com', '123 Nguyễn Trãi, Hà Nội', 0),
(2, 'KH002', 'Nguyễn Văn Bình', '0902000002', 'ngvbinh@email.com', '456 Lê Lợi, TP.HCM', 500000),
(3, 'KH003', 'Trần Thị Hoa', '0903000003', 'tthoa@email.com', '789 Trần Phú, Đà Nẵng', 0),
(4, 'KH004', 'Công ty CP Xây Lắp Miền Trung', '0904000004', 'xmtrung@email.com', '321 Phan Chu Trinh, Huế', 2000000),
(5, 'KH005', 'Phạm Văn Đức', '0905000005', 'pvduc@email.com', '654 Bạch Đằng, Hải Phòng', 0);

-- Nhà cung cấp
INSERT IGNORE INTO NhaCungCap (MaNCC, MaNCC_Code, TenNCC, NguoiDaiDien, DiaChi, SDT, Email, CongNo) VALUES
(1, 'NCC001', 'Công ty Xi Măng Hà Tiên', 'Nguyễn Văn A', 'Kiên Giang', '0800000001', 'hatienciment@email.com', 0),
(2, 'NCC002', 'Công ty Xi Măng Hoàng Thạch', 'Trần Văn B', 'Hải Dương', '0800000002', 'hoangthanh@email.com', 1000000),
(3, 'NCC003', 'Gạch Đồng Tâm', 'Lê Văn C', 'Long An', '0800000003', 'dongtam@email.com', 0),
(4, 'NCC004', 'Thép Hòa Phát', 'Phạm Văn D', 'Hà Nội', '0800000004', 'hoaphat@email.com', 5000000),
(5, 'NCC005', 'Sơn Dulux Việt Nam', 'Ngô Thị E', 'TP.HCM', '0800000005', 'dulux@email.com', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- Hiển thị thông báo hoàn thành
SELECT 'Database hdk_management đã được tạo thành công!' AS KetQua;
SELECT COUNT(*) AS SoLuongQuyen FROM Quyen;
SELECT COUNT(*) AS SoLuongNhanVien FROM NhanVien;
SELECT COUNT(*) AS SoLuongTaiKhoan FROM TaiKhoan;
SELECT COUNT(*) AS SoLuongDanhMuc FROM DanhMuc;
SELECT COUNT(*) AS SoLuongSanPham FROM SanPham;
SELECT COUNT(*) AS SoLuongKhachHang FROM KhachHang;
SELECT COUNT(*) AS SoLuongNhaCungCap FROM NhaCungCap;
