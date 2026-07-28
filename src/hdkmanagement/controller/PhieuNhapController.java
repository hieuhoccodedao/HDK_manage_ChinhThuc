package hdkmanagement.controller;

import hdkmanagement.dao.PhieuNhapDAO;
import hdkmanagement.dao.ChiTietPhieuNhapDAO;
import hdkmanagement.dao.SanPhamDAO;
import hdkmanagement.dao.NhaCungCapDAO;
import hdkmanagement.model.PhieuNhap;
import hdkmanagement.model.ChiTietPhieuNhap;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.NhaCungCap;
import hdkmanagement.util.DatabaseConnection;
import java.util.List;
import java.sql.Connection;

public class PhieuNhapController extends BaseController {
    private PhieuNhapDAO phieuNhapDAO;
    private ChiTietPhieuNhapDAO chiTietDAO;
    private SanPhamDAO sanPhamDAO;
    private NhaCungCapDAO nhaCungCapDAO;

    public PhieuNhapController() {
        super();
        this.phieuNhapDAO = new PhieuNhapDAO();
        this.chiTietDAO = new ChiTietPhieuNhapDAO();
        this.sanPhamDAO = new SanPhamDAO();
        this.nhaCungCapDAO = new NhaCungCapDAO();
    }

    public boolean savePhieuNhap(PhieuNhap phieuNhap, List<ChiTietPhieuNhap> chiTietList, String tenNCC, String sdtNCC) {
        Connection conn = null;
        try {
            // Validate
            if (chiTietList == null || chiTietList.isEmpty()) {
                showError("Phiếu nhập phải có ít nhất 1 sản phẩm!");
                return false;
            }
            if (tenNCC == null || tenNCC.trim().isEmpty() || sdtNCC == null || sdtNCC.trim().isEmpty()) {
                showError("Vui lòng nhập tên và số điện thoại nhà cung cấp!");
                return false;
            }

            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // 1. Kiểm tra và Tạo NCC
            NhaCungCap ncc = nhaCungCapDAO.getByPhone(sdtNCC.trim());
            if (ncc == null) {
                ncc = new NhaCungCap();
                ncc.setTenNCC(tenNCC.trim());
                ncc.setSdt(sdtNCC.trim());
                ncc.setMaNCC_Code("NCC" + (System.currentTimeMillis() % 1000000));
                ncc.setCongNo(0);
                if (!nhaCungCapDAO.insert(ncc)) {
                    conn.rollback();
                    showError("Lỗi tạo nhà cung cấp mới!");
                    return false;
                }
            }
            phieuNhap.setMaNCC(ncc.getMaNCC());

            // 1.5. Lưu phiếu nhập (lấy ID tự tăng)
            if (!phieuNhapDAO.insert(phieuNhap)) {
                conn.rollback();
                showError("Lỗi khi lưu phiếu nhập vào CSDL!");
                return false;
            }

            // 2. Lưu chi tiết phiếu nhập & Cập nhật tồn kho
            for (ChiTietPhieuNhap ct : chiTietList) {
                ct.setMaPN(phieuNhap.getMaPN()); // Gán ID vừa tạo
                if (!chiTietDAO.insert(ct)) {
                    conn.rollback();
                    showError("Lỗi lưu chi tiết sản phẩm!");
                    return false;
                }

                // Cập nhật tồn kho
                SanPham sp = sanPhamDAO.getById(ct.getMaSP());
                if (sp != null) {
                    int newStock = sp.getTonKho() + ct.getSoLuong();
                    sp.setTonKho(newStock);
                    sanPhamDAO.update(sp);
                    
                    // Ghi lịch sử kho
                    hdkmanagement.model.LichSuKho lsk = new hdkmanagement.model.LichSuKho();
                    lsk.setMaSP(sp.getMaSP());
                    lsk.setLoaiGiaoDich("Nhập kho mua hàng");
                    lsk.setSoLuongThayDoi(ct.getSoLuong());
                    lsk.setTonKhoHienTai(newStock);
                    lsk.setThamChieu(phieuNhap.getMaPN_Code());
                    lsk.setMaNV(phieuNhap.getNguoiTao());
                    hdkmanagement.dao.LichSuKhoDAO lichSuKhoDAO = new hdkmanagement.dao.LichSuKhoDAO();
                    lichSuKhoDAO.insert(lsk);
                }
            }

            // 3. Cập nhật công nợ nhà cung cấp
            ncc.setCongNo(ncc.getCongNo() + phieuNhap.getConNo());
            nhaCungCapDAO.update(ncc);

            // 4. Tự động tạo phiếu chi nếu có thanh toán
            if (phieuNhap.getDaThanhToan() > 0) {
                hdkmanagement.model.PhieuThuChi pt = new hdkmanagement.model.PhieuThuChi();
                hdkmanagement.dao.PhieuThuChiDAO ptDao = new hdkmanagement.dao.PhieuThuChiDAO();
                pt.setMaPhieu_Code(ptDao.generateCode("PC"));
                pt.setLoaiPhieu("Chi");
                pt.setSoTien(phieuNhap.getDaThanhToan());
                pt.setLyDo("Chi tiền nhập hàng (PN: " + phieuNhap.getMaPN_Code() + ")");
                pt.setDoiTuong(tenNCC);
                pt.setThamChieu(phieuNhap.getMaPN_Code());
                pt.setMaNV(phieuNhap.getNguoiTao());
                ptDao.insert(pt);
            }

            conn.commit();
            showInfo(" Lưu phiếu nhập thành công!");
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
