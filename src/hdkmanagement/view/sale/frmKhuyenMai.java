package hdkmanagement.view.sale;

import hdkmanagement.dao.KhuyenMaiDAO;
import hdkmanagement.model.KhuyenMai;
import hdkmanagement.util.DateUtil;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Màn hình quản lý Khuyến Mãi.
 * - Admin: Xem + Thêm + Sửa + Xóa + Bật/Tắt
 * - Nhân viên: Chỉ xem
 */
public class frmKhuyenMai extends JPanel {

    // ── Table ──────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private KhuyenMaiDAO dao;

    // ── Form fields (chỉ Admin mới thấy/dùng) ─────────────────────────────
    private JTextField txtTenKM, txtMucGiam, txtDieuKien, txtNgayBD, txtNgayKT, txtGhiChu;
    private JCheckBox chkTrangThai;
    private JPanel formPanel;

    // ── Buttons ────────────────────────────────────────────────────────────
    private JButton btnThem, btnSua, btnXoa, btnBatTat, btnLamMoi;

    private boolean isAdmin;

    public frmKhuyenMai() {
        dao     = new KhuyenMaiDAO();
        isAdmin = SessionManager.getInstance().isAdmin();
        initComponents();
        loadData();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BUILD UI
    // ══════════════════════════════════════════════════════════════════════
    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildCenter(),   BorderLayout.CENTER);
        if (isAdmin) add(buildFormPanel(), BorderLayout.EAST);
        add(buildToolbar(),  BorderLayout.SOUTH);
    }

    // ── HEADER ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG);

        JLabel title = new JLabel("QUẢN LÝ CHƯƠNG TRÌNH KHUYẾN MÃI", SwingConstants.LEFT);
        title.setFont(UITheme.font(Font.BOLD, 22));
        title.setForeground(UITheme.PRIMARY);

        JLabel roleTag = new JLabel(isAdmin ? "   Quản trị viên" : "   Chỉ xem");
        roleTag.setFont(UITheme.font(Font.BOLD, 12));
        roleTag.setForeground(isAdmin ? UITheme.WARNING : UITheme.TEXT_MUTED);
        roleTag.setOpaque(true);
        roleTag.setBackground(isAdmin ? new Color(255, 251, 235) : new Color(245, 245, 245));
        roleTag.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isAdmin ? new Color(234, 179, 8) : UITheme.BORDER, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        p.add(title, BorderLayout.CENTER);
        p.add(roleTag, BorderLayout.EAST);
        return p;
    }

    // ── TABLE ──────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        String[] cols = {"Mã KM", "Tên chương trình", "Giảm (%)", "Hóa đơn tối thiểu", "Bắt đầu", "Kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setRowHeight(34);

        // Color rows: active=green, inactive=gray
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String tt = (String) t.getModel().getValueAt(r, 6);
                    if ("Đang áp dụng".equals(tt)) {
                        setBackground(new Color(220, 252, 231));
                        setForeground(UITheme.SUCCESS);
                    } else {
                        setBackground(new Color(243, 244, 246));
                        setForeground(UITheme.TEXT_MUTED);
                    }
                    if (c == 6) {
                        setFont(UITheme.font(Font.BOLD, 12));
                    } else {
                        setFont(UITheme.FONT_TABLE);
                        setForeground(sel ? Color.WHITE : UITheme.TEXT_DARK);
                        if ("Đang áp dụng".equals(tt) && !sel) setForeground(UITheme.TEXT_DARK);
                    }
                }
                return this;
            }
        });

        // Click row → fill form (admin only)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && isAdmin) fillFormFromRow();
        });

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UITheme.BG);

        JLabel lbl = new JLabel("Danh sách khuyến mãi");
        lbl.setFont(UITheme.font(Font.BOLD, 14));
        lbl.setForeground(UITheme.TEXT_MEDIUM);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── FORM (Admin only) ──────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        formPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(UITheme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        formPanel.setPreferredSize(new Dimension(310, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);

        // Title
        g.gridx=0; g.gridy=0; g.gridwidth=2;
        JLabel lbl = new JLabel("  Thêm / Sửa khuyến mãi");
        lbl.setFont(UITheme.font(Font.BOLD, 14));
        lbl.setForeground(UITheme.PRIMARY);
        formPanel.add(lbl, g);
        g.gridwidth=1;

        txtTenKM    = makeField(); txtMucGiam = makeField(); txtDieuKien = makeField();
        txtNgayBD   = makeField("dd/MM/yyyy");
        txtNgayKT   = makeField("dd/MM/yyyy");
        txtGhiChu   = makeField();
        chkTrangThai = new JCheckBox("Đang áp dụng"); chkTrangThai.setSelected(true);
        chkTrangThai.setFont(UITheme.FONT_LABEL); chkTrangThai.setOpaque(false);
        chkTrangThai.setForeground(UITheme.SUCCESS);

        addRow(formPanel, g, 1, "Tên chương trình:", txtTenKM);
        addRow(formPanel, g, 2, "Mức giảm (%):", txtMucGiam);
        addRow(formPanel, g, 3, "HĐ tối thiểu (đ):", txtDieuKien);
        addRow(formPanel, g, 4, "Ngày bắt đầu:", txtNgayBD);
        addRow(formPanel, g, 5, "Ngày kết thúc:", txtNgayKT);
        addRow(formPanel, g, 6, "Ghi chú:", txtGhiChu);

        g.gridx=1; g.gridy=7;
        formPanel.add(chkTrangThai, g);

        // Action buttons
        g.gridx=0; g.gridy=8; g.gridwidth=2; g.insets = new Insets(12, 4, 4, 4);
        JPanel btnRow1 = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow1.setOpaque(false);
        btnThem = UITheme.primaryButton("  Thêm mới");
        btnSua  = UITheme.successButton("  Lưu sửa");
        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e  -> suaKhuyenMai());
        btnRow1.add(btnThem); btnRow1.add(btnSua);
        formPanel.add(btnRow1, g);

        g.gridy=9; g.insets = new Insets(4, 4, 4, 4);
        JPanel btnRow2 = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow2.setOpaque(false);
        btnXoa    = UITheme.dangerButton("  Xóa");
        btnBatTat = UITheme.grayButton("  Bật / Tắt");
        btnXoa.addActionListener(e    -> xoaKhuyenMai());
        btnBatTat.addActionListener(e -> batTatKhuyenMai());
        btnRow2.add(btnXoa); btnRow2.add(btnBatTat);
        formPanel.add(btnRow2, g);

        g.gridy=10; g.insets = new Insets(8, 4, 0, 4);
        JLabel hint = new JLabel("<html><font color='#9ca3af'> Click vào dòng trong bảng<br>để chọn khuyến mãi cần sửa/xóa</font></html>");
        hint.setFont(UITheme.font(Font.PLAIN, 11));
        formPanel.add(hint, g);

        return formPanel;
    }

    private JTextField makeField(String placeholder) {
        JTextField tf = new JTextField();
        UITheme.styleTextField(tf);
        tf.setToolTipText(placeholder);
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }
    private JTextField makeField() { return makeField(""); }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx=0; g.gridy=row; g.weightx=0; g.gridwidth=1;
        JLabel l = new JLabel(label); l.setFont(UITheme.FONT_LABEL); l.setForeground(UITheme.TEXT_MEDIUM);
        p.add(l, g);
        g.gridx=1; g.weightx=1;
        p.add(field, g);
    }

    // ── TOOLBAR ────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        btnLamMoi = UITheme.grayButton("  Làm mới danh sách");
        btnLamMoi.addActionListener(e -> { loadData(); clearForm(); });
        p.add(btnLamMoi);

        // Info label
        JLabel info = new JLabel("  ℹ Khuyến mãi đang áp dụng sẽ tự động hiển thị khi lập hóa đơn");
        info.setFont(UITheme.font(Font.ITALIC, 11));
        info.setForeground(UITheme.TEXT_MUTED);
        p.add(info);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  DATA
    // ══════════════════════════════════════════════════════════════════════
    public void loadData() {
        tableModel.setRowCount(0);
        List<KhuyenMai> list = dao.getAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhuyenMai km : list) {
            tableModel.addRow(new Object[]{
                km.getMaKMCode(),
                km.getTenKM(),
                String.format("%.1f%%", km.getMucGiam()),
                ValidateUtil.formatCurrencyVND(km.getDieuKien()),
                km.getNgayBatDau() != null ? sdf.format(km.getNgayBatDau()) : "",
                km.getNgayKetThuc() != null ? sdf.format(km.getNgayKetThuc()) : "",
                km.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng"
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ADMIN ACTIONS
    // ══════════════════════════════════════════════════════════════════════
    private void themKhuyenMai() {
        KhuyenMai km = buildFromForm();
        if (km == null) return;

        km.setMaKMCode(dao.generateCode());
        if (dao.insert(km)) {
            showSuccess(" Thêm khuyến mãi thành công!");
            loadData(); clearForm();
        } else {
            showError("Thêm khuyến mãi thất bại!");
        }
    }

    private void suaKhuyenMai() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Vui lòng chọn một dòng để sửa!"); return; }

        KhuyenMai km = buildFromForm();
        if (km == null) return;

        List<KhuyenMai> list = dao.getAll();
        if (row >= list.size()) return;
        km.setMaKM(list.get(row).getMaKM());
        km.setMaKMCode(list.get(row).getMaKMCode());

        if (dao.update(km)) {
            showSuccess(" Cập nhật khuyến mãi thành công!");
            loadData(); clearForm();
        } else {
            showError("Cập nhật thất bại!");
        }
    }

    private void xoaKhuyenMai() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Vui lòng chọn một dòng để xóa!"); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn XÓA khuyến mãi này?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        List<KhuyenMai> list = dao.getAll();
        if (row >= list.size()) return;
        int maKM = list.get(row).getMaKM();

        if (dao.delete(maKM)) {
            showSuccess(" Đã xóa khuyến mãi!");
            loadData(); clearForm();
        } else {
            showError("Xóa thất bại!");
        }
    }

    private void batTatKhuyenMai() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Vui lòng chọn một dòng!"); return; }

        List<KhuyenMai> list = dao.getAll();
        if (row >= list.size()) return;
        KhuyenMai km = list.get(row);
        km.setTrangThai(!km.isTrangThai());

        if (dao.update(km)) {
            showSuccess(km.isTrangThai() ? " Đã bật khuyến mãi!" : " Đã tắt khuyến mãi!");
            loadData();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════
    private KhuyenMai buildFromForm() {
        String ten = txtTenKM.getText().trim();
        if (ten.isEmpty()) { showWarn("Vui lòng nhập tên chương trình!"); return null; }

        double mucGiam, dieuKien;
        try { mucGiam = Double.parseDouble(txtMucGiam.getText().replace(",","").trim()); }
        catch (NumberFormatException e) { showWarn("Mức giảm không hợp lệ! Nhập số 0-100"); return null; }
        if (mucGiam <= 0 || mucGiam > 100) { showWarn("Mức giảm phải từ 0 đến 100%!"); return null; }

        try { dieuKien = Double.parseDouble(txtDieuKien.getText().replace(",","").trim()); }
        catch (NumberFormatException e) { showWarn("Điều kiện hóa đơn không hợp lệ!"); return null; }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date ngayBD, ngayKT;
        try { ngayBD = sdf.parse(txtNgayBD.getText().trim()); }
        catch (ParseException e) { showWarn("Ngày bắt đầu không đúng định dạng dd/MM/yyyy"); return null; }
        try { ngayKT = sdf.parse(txtNgayKT.getText().trim()); }
        catch (ParseException e) { showWarn("Ngày kết thúc không đúng định dạng dd/MM/yyyy"); return null; }
        if (ngayKT.before(ngayBD)) { showWarn("Ngày kết thúc phải sau ngày bắt đầu!"); return null; }

        KhuyenMai km = new KhuyenMai();
        km.setTenKM(ten);
        km.setMucGiam(mucGiam);
        km.setDieuKien(dieuKien);
        km.setNgayBatDau(ngayBD);
        km.setNgayKetThuc(ngayKT);
        km.setGhiChu(txtGhiChu.getText().trim());
        km.setTrangThai(chkTrangThai.isSelected());
        return km;
    }

    private void fillFormFromRow() {
        int row = table.getSelectedRow();
        if (row < 0 || !isAdmin) return;
        List<KhuyenMai> list = dao.getAll();
        if (row >= list.size()) return;
        KhuyenMai km = list.get(row);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtTenKM.setText(km.getTenKM());
        txtMucGiam.setText(String.valueOf(km.getMucGiam()));
        txtDieuKien.setText(String.valueOf((long)km.getDieuKien()));
        txtNgayBD.setText(km.getNgayBatDau() != null ? sdf.format(km.getNgayBatDau()) : "");
        txtNgayKT.setText(km.getNgayKetThuc() != null ? sdf.format(km.getNgayKetThuc()) : "");
        txtGhiChu.setText(km.getGhiChu() != null ? km.getGhiChu() : "");
        chkTrangThai.setSelected(km.isTrangThai());
    }

    private void clearForm() {
        if (!isAdmin) return;
        txtTenKM.setText(""); txtMucGiam.setText(""); txtDieuKien.setText("");
        txtNgayBD.setText(""); txtNgayKT.setText(""); txtGhiChu.setText("");
        chkTrangThai.setSelected(true);
        table.clearSelection();
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public JPanel getPanel() { return this; }
}
