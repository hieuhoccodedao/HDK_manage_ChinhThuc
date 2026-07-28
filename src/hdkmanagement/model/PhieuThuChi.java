package hdkmanagement.model;

import java.sql.Timestamp;

/**
 * Phiếu Thu / Phiếu Chi - quản lý dòng tiền thực tế của công ty.
 * LoaiPhieu: "Thu" hoặc "Chi"
 */
public class PhieuThuChi {
    private int maPhieu;
    private String maPhieu_Code;
    private String loaiPhieu;       // "Thu" | "Chi"
    private double soTien;
    private String lyDo;
    private String doiTuong;        // Tên KH / NCC / Chi phí khác
    private String thamChieu;       // Mã HĐ / Phiếu NK liên quan
    private int maNV;
    private String tenNV;           // Tên nhân viên lập (join)
    private Timestamp ngayLap;
    private String ghiChu;

    public PhieuThuChi() {}

    // Getters & Setters
    public int getMaPhieu()                     { return maPhieu; }
    public void setMaPhieu(int v)               { this.maPhieu = v; }
    public String getMaPhieu_Code()             { return maPhieu_Code; }
    public void setMaPhieu_Code(String v)       { this.maPhieu_Code = v; }
    public String getLoaiPhieu()                { return loaiPhieu; }
    public void setLoaiPhieu(String v)          { this.loaiPhieu = v; }
    public double getSoTien()                   { return soTien; }
    public void setSoTien(double v)             { this.soTien = v; }
    public String getLyDo()                     { return lyDo; }
    public void setLyDo(String v)               { this.lyDo = v; }
    public String getDoiTuong()                 { return doiTuong; }
    public void setDoiTuong(String v)           { this.doiTuong = v; }
    public String getThamChieu()                { return thamChieu; }
    public void setThamChieu(String v)          { this.thamChieu = v; }
    public int getMaNV()                        { return maNV; }
    public void setMaNV(int v)                  { this.maNV = v; }
    public String getTenNV()                    { return tenNV; }
    public void setTenNV(String v)              { this.tenNV = v; }
    public Timestamp getNgayLap()               { return ngayLap; }
    public void setNgayLap(Timestamp v)         { this.ngayLap = v; }
    public String getGhiChu()                   { return ghiChu; }
    public void setGhiChu(String v)             { this.ghiChu = v; }

    @Override
    public String toString() {
        return maPhieu_Code + " - " + loaiPhieu + ": " + soTien;
    }
}
