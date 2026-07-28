package hdkmanagement.view.finance;

import hdkmanagement.dao.PhieuThuChiDAO;
import hdkmanagement.model.PhieuThuChi;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class frmThuChi extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PhieuThuChiDAO dao;

    // Form fields
    private JComboBox<String> cboLoai;
    private JTextField txtSoTien, txtLyDo, txtDoiTuong, txtGhiChu;
    private JLabel lblTongThu, lblTongChi, lblCanDoi;
    private JButton btnLuu, btnLamMoi;

    public frmThuChi() {
        dao = new PhieuThuChiDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // === TITLE ===
        JLabel title = new JLabel("QUẢN LÝ PHIẾU THU CHI", SwingConstants.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 22));
        title.setForeground(UITheme.PRIMARY);
        add(title, BorderLayout.NORTH);

        // === CENTER: form + bảng ===
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(buildFormPanel(), BorderLayout.NORTH);
        centerPanel.add(buildTablePanel(), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // === SOUTH: tổng kết ===
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.CARD_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                "Lập phiếu mới", TitledBorder.LEFT, TitledBorder.TOP,
                UITheme.font(Font.BOLD, 13), UITheme.PRIMARY));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboLoai = new JComboBox<>(new String[]{"Thu", "Chi"});
        cboLoai.setFont(UITheme.FONT_INPUT);
        txtSoTien  = new JTextField(); UITheme.styleTextField(txtSoTien);
        txtLyDo    = new JTextField(); UITheme.styleTextField(txtLyDo);
        txtDoiTuong= new JTextField(); UITheme.styleTextField(txtDoiTuong);
        txtGhiChu  = new JTextField(); UITheme.styleTextField(txtGhiChu);

        addRow(panel, gbc, 0, "Loại phiếu:", cboLoai);
        addRow(panel, gbc, 1, "Số tiền (VNĐ):", txtSoTien);
        addRow(panel, gbc, 2, "Lý do:", txtLyDo);
        addRow(panel, gbc, 3, "Đối tượng:", txtDoiTuong);
        addRow(panel, gbc, 4, "Ghi chú:", txtGhiChu);

        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        btnLuu = UITheme.primaryButton("  Lưu phiếu");
        btnLuu.addActionListener(e -> savePhieu());
        panel.add(btnLuu, gbc);

        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(label); lbl.setFont(UITheme.FONT_LABEL); lbl.setForeground(UITheme.TEXT_MEDIUM);
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        p.add(comp, g);
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(UITheme.BG);
        JLabel lbl = new JLabel("Danh sách phiếu thu chi"); lbl.setFont(UITheme.font(Font.BOLD, 14)); lbl.setForeground(UITheme.TEXT_MEDIUM);
        btnLamMoi = UITheme.grayButton("  Làm mới");
        btnLamMoi.addActionListener(e -> loadData());
        topBar.add(lbl);
        topBar.add(Box.createHorizontalStrut(12));
        topBar.add(btnLamMoi);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Mã phiếu", "Loại", "Số tiền", "Lý do", "Đối tượng", "Tham chiếu", "Người lập", "Ngày lập"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        // Color rows by type
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String loai = (String) t.getModel().getValueAt(r, 1);
                    setBackground("Thu".equals(loai) ? new Color(220, 252, 231) : new Color(254, 226, 226));
                    setForeground(UITheme.TEXT_DARK);
                }
                return this;
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 32, 8));
        p.setBackground(UITheme.CARD_BG);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        lblTongThu = makeSumLabel("Tổng Thu: 0 đ", UITheme.SUCCESS);
        lblTongChi = makeSumLabel("Tổng Chi: 0 đ", UITheme.DANGER);
        lblCanDoi  = makeSumLabel("Cân đối: 0 đ", UITheme.PRIMARY);

        p.add(lblTongThu); p.add(new JSeparator(SwingConstants.VERTICAL));
        p.add(lblTongChi); p.add(new JSeparator(SwingConstants.VERTICAL));
        p.add(lblCanDoi);
        return p;
    }

    private JLabel makeSumLabel(String text, Color fg) {
        JLabel l = new JLabel(text); l.setFont(UITheme.font(Font.BOLD, 15)); l.setForeground(fg); return l;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        double tongThu = 0, tongChi = 0;
        List<PhieuThuChi> list = dao.getAll();
        for (PhieuThuChi p : list) {
            tableModel.addRow(new Object[]{
                p.getMaPhieu_Code(),
                p.getLoaiPhieu(),
                ValidateUtil.formatCurrencyVND(p.getSoTien()),
                p.getLyDo(),
                p.getDoiTuong(),
                p.getThamChieu(),
                p.getTenNV(),
                p.getNgayLap() != null ? sdf.format(p.getNgayLap()) : ""
            });
            if ("Thu".equals(p.getLoaiPhieu())) tongThu += p.getSoTien();
            else tongChi += p.getSoTien();
        }
        lblTongThu.setText("Tổng Thu: " + ValidateUtil.formatCurrencyVND(tongThu));
        lblTongChi.setText("Tổng Chi: " + ValidateUtil.formatCurrencyVND(tongChi));
        double canDoi = tongThu - tongChi;
        lblCanDoi.setText("Cân đối: " + ValidateUtil.formatCurrencyVND(canDoi));
        lblCanDoi.setForeground(canDoi >= 0 ? UITheme.SUCCESS : UITheme.DANGER);
    }

    private void savePhieu() {
        try {
            String loai = (String) cboLoai.getSelectedItem();
            double soTien = Double.parseDouble(txtSoTien.getText().replaceAll("[,\\.]", "").trim());
            String lyDo = txtLyDo.getText().trim();
            if (lyDo.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }

            PhieuThuChi p = new PhieuThuChi();
            p.setMaPhieu_Code(dao.generateCode("Thu".equals(loai) ? "PT" : "PC"));
            p.setLoaiPhieu(loai);
            p.setSoTien(soTien);
            p.setLyDo(lyDo);
            p.setDoiTuong(txtDoiTuong.getText().trim());
            p.setGhiChu(txtGhiChu.getText().trim());
            if (SessionManager.getInstance().isLoggedIn()) {
                p.setMaNV(SessionManager.getInstance().getCurrentEmployeeId());
            }

            if (dao.insert(p)) {
                JOptionPane.showMessageDialog(this, "Lập phiếu " + loai + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        txtSoTien.setText(""); txtLyDo.setText(""); txtDoiTuong.setText(""); txtGhiChu.setText("");
        cboLoai.setSelectedIndex(0);
    }

    public JPanel getPanel() { return this; }
}
