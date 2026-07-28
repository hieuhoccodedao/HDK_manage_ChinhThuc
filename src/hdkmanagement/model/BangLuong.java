package hdkmanagement.model;

import java.sql.Timestamp;

/**
 * Bảng lương nhân viên theo tháng.
 */
public class BangLuong {
    private int maBangLuong;
    private String maBL_Code;
    private int maNV;
    private String tenNV;           // join từ NhanVien
    private String thangNam;        // "YYYY-MM"
    private double doanhSo;
    private double luongCoBan;
    private double tienHoaHong;
    private double tongLuong;
    private String trangThai;       // "Chờ duyệt" | "Đã duyệt" | "Đã thanh toán"
    private Timestamp ngayTao;
    private String ghiChu;

    public BangLuong() {}

    // Getters & Setters
    public int getMaBangLuong()                 { return maBangLuong; }
    public void setMaBangLuong(int v)           { this.maBangLuong = v; }
    public String getMaBL_Code()                { return maBL_Code; }
    public void setMaBL_Code(String v)          { this.maBL_Code = v; }
    public int getMaNV()                        { return maNV; }
    public void setMaNV(int v)                  { this.maNV = v; }
    public String getTenNV()                    { return tenNV; }
    public void setTenNV(String v)              { this.tenNV = v; }
    public String getThangNam()                 { return thangNam; }
    public void setThangNam(String v)           { this.thangNam = v; }
    public double getDoanhSo()                  { return doanhSo; }
    public void setDoanhSo(double v)            { this.doanhSo = v; }
    public double getLuongCoBan()               { return luongCoBan; }
    public void setLuongCoBan(double v)         { this.luongCoBan = v; }
    public double getTienHoaHong()              { return tienHoaHong; }
    public void setTienHoaHong(double v)        { this.tienHoaHong = v; }
    public double getTongLuong()                { return tongLuong; }
    public void setTongLuong(double v)          { this.tongLuong = v; }
    public String getTrangThai()                { return trangThai; }
    public void setTrangThai(String v)          { this.trangThai = v; }
    public Timestamp getNgayTao()               { return ngayTao; }
    public void setNgayTao(Timestamp v)         { this.ngayTao = v; }
    public String getGhiChu()                   { return ghiChu; }
    public void setGhiChu(String v)             { this.ghiChu = v; }
}
