package hdkmanagement.view.hr;

import hdkmanagement.dao.BangLuongDAO;
import hdkmanagement.dao.NhanVienDAO;
import hdkmanagement.dao.PhieuThuChiDAO;
import hdkmanagement.model.BangLuong;
import hdkmanagement.model.NhanVien;
import hdkmanagement.model.PhieuThuChi;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class frmTinhLuong extends JPanel {

    private JSpinner spinnerNam;
    private JComboBox<String> cboThang;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTongLuong;

    private BangLuongDAO bangLuongDAO;
    private NhanVienDAO nhanVienDAO;
    private PhieuThuChiDAO ptcDAO;

    public frmTinhLuong() {
        bangLuongDAO = new BangLuongDAO();
        nhanVienDAO  = new NhanVienDAO();
        ptcDAO       = new PhieuThuChiDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // TITLE
        JLabel title = new JLabel("TÍNH LƯƠNG & HOA HỒNG NHÂN VIÊN", SwingConstants.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 22));
        title.setForeground(UITheme.PRIMARY);

        // FILTER BAR
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        filterBar.setBackground(UITheme.CARD_BG);

        String[] thangItems = {"Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6",
                               "Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"};
        cboThang   = new JComboBox<>(thangItems);
        cboThang.setFont(UITheme.FONT_INPUT);
        cboThang.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));

        spinnerNam = new JSpinner(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2020, 2099, 1));
        spinnerNam.setFont(UITheme.FONT_INPUT);

        JButton btnTinh    = UITheme.primaryButton("  Tính lương tháng");
        JButton btnDuyet   = UITheme.successButton("  Duyệt & Xuất phiếu chi");
        JButton btnXemLich = UITheme.grayButton("  Xem lịch sử");

        btnTinh.addActionListener(e -> tinhLuong());
        btnDuyet.addActionListener(e -> duyetLuong());
        btnXemLich.addActionListener(e -> loadHistory());

        filterBar.add(new JLabel("Tháng:")); filterBar.add(cboThang);
        filterBar.add(new JLabel("Năm:"));   filterBar.add(spinnerNam);
        filterBar.add(btnTinh); filterBar.add(btnDuyet); filterBar.add(btnXemLich);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BG);
        top.add(title, BorderLayout.NORTH);
        top.add(filterBar, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"Mã BL", "Nhân viên", "Tháng", "Doanh số", "Lương CB", "Hoa hồng", "Tổng lương", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        // Color rows
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String tt = (String) t.getModel().getValueAt(r, 7);
                    if ("Đã thanh toán".equals(tt)) setBackground(new Color(220, 252, 231));
                    else if ("Đã duyệt".equals(tt))  setBackground(new Color(254, 249, 195));
                    else                               setBackground(Color.WHITE);
                }
                return this;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // SOUTH
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        south.setBackground(UITheme.CARD_BG);
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));
        lblTongLuong = new JLabel("Tổng lương: 0 đ");
        lblTongLuong.setFont(UITheme.font(Font.BOLD, 16));
        lblTongLuong.setForeground(UITheme.PRIMARY);
        south.add(lblTongLuong);
        add(south, BorderLayout.SOUTH);
    }

    private String getThangNam() {
        int thang = cboThang.getSelectedIndex() + 1;
        int nam   = (int) spinnerNam.getValue();
        return String.format("%04d-%02d", nam, thang);
    }

    private void tinhLuong() {
        String thangNam = getThangNam();
        List<NhanVien> nvList = nhanVienDAO.getAll();
        if (nvList.isEmpty()) { JOptionPane.showMessageDialog(this, "Không có nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        for (NhanVien nv : nvList) {
            BangLuong existBL = bangLuongDAO.getByNhanVienAndThangNam(nv.getMaNV(), thangNam);
            if (existBL != null && "Đã thanh toán".equals(existBL.getTrangThai())) {
                skipped++;
                continue;
            }

            double doanhSo = bangLuongDAO.tinhDoanhSo(nv.getMaNV(), thangNam);
            double luongCB = nv.getLuongCoBan();
            double tienHH  = doanhSo * (nv.getTyLeHoaHong() / 100.0);
            double tongLuong = luongCB + tienHH;

            if (existBL != null) {
                // Update existing record
                existBL.setDoanhSo(doanhSo);
                existBL.setLuongCoBan(luongCB);
                existBL.setTienHoaHong(tienHH);
                existBL.setTongLuong(tongLuong);
                bangLuongDAO.update(existBL);
                updated++;
            } else {
                // Insert new record
                BangLuong bl = new BangLuong();
                bl.setMaBL_Code(bangLuongDAO.generateCode() + "_" + nv.getMaNV());
                bl.setMaNV(nv.getMaNV());
                bl.setThangNam(thangNam);
                bl.setDoanhSo(doanhSo);
                bl.setLuongCoBan(luongCB);
                bl.setTienHoaHong(tienHH);
                bl.setTongLuong(tongLuong);
                bl.setTrangThai("Chờ duyệt");
                bangLuongDAO.insert(bl);
                created++;
            }
        }
        JOptionPane.showMessageDialog(this, "Hoàn tất tính lương tháng " + thangNam + "!\n" +
            "Đã tạo mới: " + created + "\n" +
            "Đã cập nhật: " + updated + "\n" +
            "Bỏ qua (đã thanh toán): " + skipped, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadHistory();
    }

    private void duyetLuong() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng lương để duyệt!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }

        String maBL = (String) tableModel.getValueAt(row, 0);
        String trangThai = (String) tableModel.getValueAt(row, 7);
        if ("Đã thanh toán".equals(trangThai)) { JOptionPane.showMessageDialog(this, "Bảng lương này đã thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }

        // Tìm BangLuong
        String thangNam = getThangNam();
        List<BangLuong> list = bangLuongDAO.getByThangNam(thangNam);
        for (BangLuong bl : list) {
            if (bl.getMaBL_Code().equals(maBL)) {
                java.sql.Connection conn = null;
                try {
                    conn = hdkmanagement.util.DatabaseConnection.getInstance().getConnection();
                    conn.setAutoCommit(false);
                    
                    bl.setTrangThai("Đã thanh toán");
                    boolean updated = bangLuongDAO.update(bl);
                    if (!updated) throw new Exception("Không thể cập nhật trạng thái bảng lương.");

                    // Sinh phiếu chi lương
                    PhieuThuChi pc = new PhieuThuChi();
                    pc.setMaPhieu_Code(ptcDAO.generateCode("PC"));
                    pc.setLoaiPhieu("Chi");
                    pc.setSoTien(bl.getTongLuong());
                    pc.setLyDo("Chi trả lương tháng " + bl.getThangNam());
                    pc.setDoiTuong(bl.getTenNV());
                    pc.setThamChieu(bl.getMaBL_Code());
                    if (SessionManager.getInstance().isLoggedIn())
                        pc.setMaNV(SessionManager.getInstance().getCurrentEmployeeId());
                        
                    boolean inserted = ptcDAO.insert(pc);
                    if (!inserted) throw new Exception("Không thể tạo phiếu chi.");

                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Đã duyệt và xuất phiếu chi lương cho " + bl.getTenNV() + "!\nTổng: " + ValidateUtil.formatCurrencyVND(bl.getTongLuong()), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    try { if (conn != null) conn.rollback(); } catch (java.sql.SQLException e) {}
                    JOptionPane.showMessageDialog(this, "Lỗi khi duyệt lương: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    try { if (conn != null) conn.setAutoCommit(true); } catch (java.sql.SQLException e) {}
                }
                
                loadHistory();
                return;
            }
        }
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        String thangNam = getThangNam();
        List<BangLuong> list = bangLuongDAO.getByThangNam(thangNam);
        double tongLuong = 0;
        for (BangLuong bl : list) {
            tableModel.addRow(new Object[]{
                bl.getMaBL_Code(), bl.getTenNV(), bl.getThangNam(),
                ValidateUtil.formatCurrencyVND(bl.getDoanhSo()),
                ValidateUtil.formatCurrencyVND(bl.getLuongCoBan()),
                ValidateUtil.formatCurrencyVND(bl.getTienHoaHong()),
                ValidateUtil.formatCurrencyVND(bl.getTongLuong()),
                bl.getTrangThai()
            });
            tongLuong += bl.getTongLuong();
        }
        lblTongLuong.setText("Tổng lương phải trả: " + ValidateUtil.formatCurrencyVND(tongLuong));
    }

    public JPanel getPanel() { return this; }
}
