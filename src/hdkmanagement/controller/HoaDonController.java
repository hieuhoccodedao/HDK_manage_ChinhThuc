package hdkmanagement.controller;

import hdkmanagement.dao.HoaDonDAO;
import hdkmanagement.dao.ChiTietHoaDonDAO;
import hdkmanagement.dao.SanPhamDAO;
import hdkmanagement.dao.KhachHangDAO;
import hdkmanagement.dao.LichSuKhoDAO;
import hdkmanagement.dao.PhieuThuChiDAO;
import hdkmanagement.model.HoaDon;
import hdkmanagement.model.ChiTietHoaDon;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.KhachHang;
import hdkmanagement.model.LichSuKho;
import hdkmanagement.model.PhieuThuChi;
import hdkmanagement.util.DatabaseConnection;
import java.sql.Connection;
import java.util.List;

public class HoaDonController extends BaseController {
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietDAO;
    private SanPhamDAO sanPhamDAO;
    private KhachHangDAO khachHangDAO;
    private LichSuKhoDAO lichSuKhoDAO;
    private PhieuThuChiDAO phieuThuChiDAO;

    public HoaDonController() {
        super();
        this.hoaDonDAO = new HoaDonDAO();
        this.chiTietDAO = new ChiTietHoaDonDAO();
        this.sanPhamDAO = new SanPhamDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.lichSuKhoDAO = new LichSuKhoDAO();
        this.phieuThuChiDAO = new PhieuThuChiDAO();
    }

    public boolean saveHoaDon(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList, String tenKH, String sdtKH) {
        Connection conn = null;
        try {
            if (chiTietList == null || chiTietList.isEmpty()) {
                showError("Hóa đơn phải có ít nhất 1 sản phẩm!");
                return false;
            }
            if (tenKH == null || tenKH.trim().isEmpty() || sdtKH == null || sdtKH.trim().isEmpty()) {
                showError("Vui lòng nhập tên và số điện thoại khách hàng!");
                return false;
            }

            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra và Tạo khách hàng
            KhachHang kh = khachHangDAO.getByPhone(sdtKH.trim());
            if (kh == null) {
                kh = new KhachHang();
                kh.setHoTen(tenKH.trim());
                kh.setSdt(sdtKH.trim());
                kh.setMaKH_Code("KH" + (System.currentTimeMillis() % 1000000));
                kh.setCongNo(0);
                if (!khachHangDAO.insert(kh)) {
                    conn.rollback();
                    showError("Lỗi tạo khách hàng mới!");
                    return false;
                }
            }
            hoaDon.setMaKH(kh.getMaKH());

            // 2. Lưu hóa đơn
            if (!hoaDonDAO.insert(hoaDon)) {
                conn.rollback();
                showError("Lỗi lưu hóa đơn!");
                return false;
            }

            // 3. Lưu chi tiết & trừ kho
            for (ChiTietHoaDon ct : chiTietList) {
                ct.setMaHD(hoaDon.getMaHD());
                if (!chiTietDAO.insert(ct)) {
                    conn.rollback();
                    showError("Lỗi lưu chi tiết sản phẩm!");
                    return false;
                }

                SanPham sp = sanPhamDAO.getById(ct.getMaSP());
                if (sp != null) {
                    int newStock = sp.getTonKho() - ct.getSoLuong();
                    if (newStock < 0) {
                        conn.rollback();
                        showError("Sản phẩm " + sp.getTenSP() + " không đủ tồn kho!");
                        return false;
                    }
                    sp.setTonKho(newStock);
                    sanPhamDAO.update(sp);
                    
                    // Ghi lịch sử kho
                    LichSuKho lsk = new LichSuKho();
                    lsk.setMaSP(sp.getMaSP());
                    lsk.setLoaiGiaoDich("Xuất kho bán hàng");
                    lsk.setSoLuongThayDoi(-ct.getSoLuong());
                    lsk.setTonKhoHienTai(newStock);
                    lsk.setThamChieu(hoaDon.getMaHD_Code());
                    lsk.setMaNV(hoaDon.getNhanVienBan());
                    if (!lichSuKhoDAO.insert(lsk)) {
                        conn.rollback();
                        showError("Lỗi lưu lịch sử kho!");
                        return false;
                    }
                }
            }

            // 4. Cộng công nợ và Cập nhật CRM
            double finalAmount = hoaDon.getTongTien();
            kh.setCongNo(kh.getCongNo() + hoaDon.getConNo());
            // Tích điểm: cứ 100.000đ = 1 điểm
            int diemMoi = (int)(finalAmount / 100_000);
            kh.setDiemTichLuy(kh.getDiemTichLuy() + diemMoi);
            kh.setTongChiTieu(kh.getTongChiTieu() + finalAmount);
            kh.capNhatHangThe();
            khachHangDAO.update(kh);

            conn.commit();

            // 5. Tự động tạo phiếu thu
            PhieuThuChi pt = new PhieuThuChi();
            pt.setMaPhieu_Code(phieuThuChiDAO.generateCode("PT"));
            pt.setLoaiPhieu("Thu");
            pt.setSoTien(hoaDon.getDaThanhToan());
            pt.setLyDo("Thu tiền bán hàng hóa");
            pt.setDoiTuong(tenKH);
            pt.setThamChieu(hoaDon.getMaHD_Code());
            pt.setMaNV(hoaDon.getNhanVienBan());
            phieuThuChiDAO.insert(pt);

            hdkmanagement.dao.SystemLogDAO.getInstance().logAction("Lập hóa đơn", "Lập hóa đơn " + hoaDon.getMaHD_Code() + " cho khách hàng " + tenKH + " với tổng tiền " + hoaDon.getTongTien());
            showInfo(" Lưu hóa đơn thành công! Khách hàng được +" + diemMoi + " điểm. Hạng thẻ: " + kh.getHangThe());
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch(Exception ex) {}
            e.printStackTrace();
            showError("Lỗi hệ thống: " + e.getMessage());
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch(Exception ex) {}
        }
    }
}
