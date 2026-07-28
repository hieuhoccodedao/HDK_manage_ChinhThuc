package hdkmanagement.model;

import java.util.Date;

public class KhuyenMai {
    private int maKM;
    private String maKMCode;
    private String tenKM;
    private double mucGiam;
    private double dieuKien;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private boolean trangThai;
    private String ghiChu;
    private Date ngayTao;

    public KhuyenMai() {}

    public int getMaKM() { return maKM; }
    public void setMaKM(int maKM) { this.maKM = maKM; }
    public String getMaKMCode() { return maKMCode; }
    public void setMaKMCode(String maKMCode) { this.maKMCode = maKMCode; }
    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }
    public double getMucGiam() { return mucGiam; }
    public void setMucGiam(double mucGiam) { this.mucGiam = mucGiam; }
    public double getDieuKien() { return dieuKien; }
    public void setDieuKien(double dieuKien) { this.dieuKien = dieuKien; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
    
    @Override
    public String toString() { return tenKM + " (-" + String.format("%.0f", mucGiam) + "%)"; }
}
