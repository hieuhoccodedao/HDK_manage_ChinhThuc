// model/NhanVien.java
package hdkmanagement.model;

import java.sql.Date;
import java.sql.Timestamp;

public class NhanVien {
    private int maNV;
    private String maNV_Code;
    private String hoTen;
    private boolean gioiTinh;
    private Date ngaySinh;
    private String diaChi;
    private String sdt;
    private String email;
    private String chucVu;
    private double luongCoBan;
    private double tyLeHoaHong;  // % hoa hồng trên doanh số
    private Date ngayVaoLam;
    private boolean trangThai;
    private String ghiChu;
    private Timestamp ngayTao;

    // Constructor mặc định
    public NhanVien() {
        this.trangThai = true;
        this.gioiTinh = true;
    }

    // Constructor đầy đủ
    public NhanVien(int maNV, String maNV_Code, String hoTen, boolean gioiTinh, 
                    Date ngaySinh, String diaChi, String sdt, String email, 
                    String chucVu, double luongCoBan, Date ngayVaoLam, 
                    boolean trangThai, String ghiChu) {
        this.maNV = maNV;
        this.maNV_Code = maNV_Code;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.chucVu = chucVu;
        this.luongCoBan = luongCoBan;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    // Getters và Setters
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public String getMaNV_Code() { return maNV_Code; }
    public void setMaNV_Code(String maNV_Code) { this.maNV_Code = maNV_Code; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }
    public double getTyLeHoaHong() { return tyLeHoaHong; }
    public void setTyLeHoaHong(double v) { this.tyLeHoaHong = v; }
    public Date getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Date ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    @Override
    public String toString() {
        return hoTen;
    }
}