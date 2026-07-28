package hdkmanagement.model;

import java.util.Date;

public class LichSuKho {
    private int maLSK;
    private int maSP;
    private String tenSP;
    private String loaiGiaoDich;
    private int soLuongThayDoi;
    private int tonKhoHienTai;
    private String thamChieu;
    private int maNV;
    private String tenNV;
    private Date ngayGiaoDich;

    public LichSuKho() {}

    public int getMaLSK() { return maLSK; }
    public void setMaLSK(int maLSK) { this.maLSK = maLSK; }
    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    public int getSoLuongThayDoi() { return soLuongThayDoi; }
    public void setSoLuongThayDoi(int soLuongThayDoi) { this.soLuongThayDoi = soLuongThayDoi; }
    public int getTonKhoHienTai() { return tonKhoHienTai; }
    public void setTonKhoHienTai(int tonKhoHienTai) { this.tonKhoHienTai = tonKhoHienTai; }
    public String getThamChieu() { return thamChieu; }
    public void setThamChieu(String thamChieu) { this.thamChieu = thamChieu; }
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public Date getNgayGiaoDich() { return ngayGiaoDich; }
    public void setNgayGiaoDich(Date ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }
}
