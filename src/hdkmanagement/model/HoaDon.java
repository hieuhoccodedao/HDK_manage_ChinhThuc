// model/HoaDon.java
package hdkmanagement.model;

import java.sql.Date;
import java.sql.Timestamp;

public class HoaDon {
    private int maHD;
    private String maHD_Code;
    private int maKH;
    private Date ngayBan;
    private double tongTien;
    private double chietKhau;
    private double daThanhToan;
    private double conNo;
    private String hinhThucThanhToan;
    private String ghiChu;
    private boolean trangThai;
    private int nhanVienBan;
    private Timestamp ngayTao;
    private int maKM;
    
    // Constructors, getters, setters
    public HoaDon() {
        this.trangThai = true;
    }
    
    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }
    public int getMaKM() { return maKM; }
    public void setMaKM(int maKM) { this.maKM = maKM; }
    public String getMaHD_Code() { return maHD_Code; }
    public void setMaHD_Code(String maHD_Code) { this.maHD_Code = maHD_Code; }
    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }
    public Date getNgayBan() { return ngayBan; }
    public void setNgayBan(Date ngayBan) { this.ngayBan = ngayBan; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public double getChietKhau() { return chietKhau; }
    public void setChietKhau(double chietKhau) { this.chietKhau = chietKhau; }
    public double getDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(double daThanhToan) { this.daThanhToan = daThanhToan; }
    public double getConNo() { return conNo; }
    public void setConNo(double conNo) { this.conNo = conNo; }
    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public int getNhanVienBan() { return nhanVienBan; }
    public void setNhanVienBan(int nhanVienBan) { this.nhanVienBan = nhanVienBan; }
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}