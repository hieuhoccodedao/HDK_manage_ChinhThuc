// model/SystemLog.java
package hdkmanagement.model;

import java.sql.Timestamp;

public class SystemLog {
    private int maLog;
    private int maTK;
    private String tenDangNhap; // Optional, for display
    private String hanhDong;
    private String chiTiet;
    private Timestamp ngayTao;

    public SystemLog() {
    }

    public SystemLog(int maTK, String hanhDong, String chiTiet) {
        this.maTK = maTK;
        this.hanhDong = hanhDong;
        this.chiTiet = chiTiet;
    }

    public int getMaLog() {
        return maLog;
    }

    public void setMaLog(int maLog) {
        this.maLog = maLog;
    }

    public int getMaTK() {
        return maTK;
    }

    public void setMaTK(int maTK) {
        this.maTK = maTK;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }
}
