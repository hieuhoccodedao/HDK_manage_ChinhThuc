package hdkmanagement.view.supplier;

import hdkmanagement.controller.NhaCungCapController;
import hdkmanagement.model.NhaCungCap;
import hdkmanagement.util.MessageUtil;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class frmNhaCungCap {

    private JPanel mainPanel;
    private JTextField txtSearch, txtMaNCC, txtTenNCC, txtNguoiDaiDien, txtSDT, txtEmail, txtCongNo;
    private JTextArea txtDiaChi, txtGhiChu;
    private JCheckBox chkTrangThai;
    private JTable tblNhaCungCap;
    private DefaultTableModel tableModel;
    private JButton btnSearch, btnRefresh, btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblCount;
    private JComboBox<String> cbTrangThai;

    private NhaCungCapController nhaCungCapController;
    private int selectedId = -1;

    // ===== MÀU SẮC & FONT - lấy từ UITheme dùng chung để đồng bộ toàn ứng dụng =====
    private final Color PRIMARY = UITheme.PRIMARY;
    private final Color PRIMARY_LIGHT = UITheme.PRIMARY_LIGHT;
    private final Color PRIMARY_DARK = UITheme.PRIMARY_DARK;
    private final Color SUCCESS = UITheme.SUCCESS;
    private final Color SUCCESS_HOVER = UITheme.SUCCESS_HOVER;
    private final Color WARNING = UITheme.WARNING;
    private final Color WARNING_HOVER = UITheme.WARNING_HOVER;
    private final Color DANGER = UITheme.DANGER;
    private final Color DANGER_HOVER = UITheme.DANGER_HOVER;
    private final Color GRAY = UITheme.GRAY;
    private final Color GRAY_HOVER = UITheme.GRAY_HOVER;

    private final Color BG = UITheme.BG;
    private final Color CARD_BG = UITheme.CARD_BG;
    private final Color BORDER = UITheme.BORDER;
    private final Color TEXT = UITheme.TEXT_MEDIUM;
    private final Color TEXT_MUTED = UITheme.TEXT_MUTED;

    private final Font FONT_TITLE = UITheme.font(Font.BOLD, 24);
    private final Font FONT_SUB = UITheme.font(Font.PLAIN, 14);
    private final Font FONT_SECTION = UITheme.font(Font.BOLD, 16);
    private final Font FONT_BUTTON = UITheme.font(Font.BOLD, 13);
    private final Font FONT_LABEL = UITheme.font(Font.BOLD, 12);
    private final Font FONT_PLAIN = UITheme.font(Font.PLAIN, 14);
    private final Font FONT_TABLE = UITheme.font(Font.PLAIN, 13);

    public frmNhaCungCap() {
        nhaCungCapController = new NhaCungCapController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        // ===== TOP: HEADER + SEARCH =====
        JPanel topPanel = createTopPanel();
        
        // ===== CENTER: TABLE + FORM =====
        JPanel centerPanel = new JPanel(new BorderLayout(16, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        centerPanel.add(createFormPanel(), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        addEvents();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    // ============================================================
    // TOP PANEL
    // ============================================================
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        JLabel lblTitle = new JLabel("Quản lý nhà cung cấp");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT);
        
        JLabel lblSub = new JLabel("Trang chủ > Nhà cung cấp");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(TEXT_MUTED);
        
        header.add(lblTitle);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(lblSub);

        // Search
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        search.setOpaque(false);
        
        txtSearch = new JTextField(25);
        txtSearch.setFont(FONT_PLAIN);
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 12, 0, 12)
        ));
        
        // ComboBox trạng thái
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng"});
        cbTrangThai.setFont(FONT_PLAIN);
        cbTrangThai.setPreferredSize(new Dimension(120, 38));
        cbTrangThai.setBackground(CARD_BG);
        cbTrangThai.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 10, 0, 10)
        ));
        
        btnSearch = createStyledButton(" Tìm kiếm", PRIMARY, Color.WHITE);
        btnRefresh = createStyledButton(" Làm mới", GRAY, Color.WHITE);
        
        search.add(new JLabel(" "));
        search.add(txtSearch);
        search.add(Box.createRigidArea(new Dimension(8, 0)));
        search.add(new JLabel("Trạng thái:"));
        search.add(cbTrangThai);
        search.add(Box.createRigidArea(new Dimension(8, 0)));
        search.add(btnSearch);
        search.add(Box.createRigidArea(new Dimension(4, 0)));
        search.add(btnRefresh);

        panel.add(header, BorderLayout.NORTH);
        panel.add(search, BorderLayout.SOUTH);
        
        return panel;
    }

    // ============================================================
    // TABLE PANEL
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 20, 20)
        ));
        panel.setOpaque(true);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        
        JLabel lblTitle = new JLabel(" Danh sách nhà cung cấp");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(TEXT);
        
        lblCount = new JLabel("0 nhà cung cấp");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCount.setForeground(TEXT_MUTED);
        
        titleBar.add(lblTitle, BorderLayout.WEST);
        titleBar.add(lblCount, BorderLayout.EAST);
        panel.add(titleBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã NCC", "Tên NCC", "Người đại diện", "SĐT", "Email", "Công nợ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhaCungCap = new JTable(tableModel);
        styleTable(tblNhaCungCap);

        JScrollPane scroll = new JScrollPane(tblNhaCungCap);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // FORM PANEL
    // ============================================================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setOpaque(true);

        // Title
        JLabel lblTitle = new JLabel("️ Thông tin nhà cung cấp");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(TEXT);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Form fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 0);

        txtMaNCC = createModernTextField();
        txtTenNCC = createModernTextField();
        txtNguoiDaiDien = createModernTextField();
        txtSDT = createModernTextField();
        txtEmail = createModernTextField();
        txtCongNo = createModernTextField();
        txtCongNo.setEditable(false);
        txtCongNo.setBackground(new Color(248, 250, 252));
        txtCongNo.setForeground(TEXT_MUTED);

        txtDiaChi = createModernTextArea(2);
        txtGhiChu = createModernTextArea(2);

        chkTrangThai = new JCheckBox("Hoạt động");
        chkTrangThai.setFont(FONT_PLAIN);
        chkTrangThai.setBackground(CARD_BG);
        chkTrangThai.setSelected(true);
        chkTrangThai.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int row = 0;
        addFormField(form, gbc, row++, "Mã NCC", txtMaNCC);
        addFormField(form, gbc, row++, "Tên NCC", txtTenNCC);
        addFormField(form, gbc, row++, "Người đại diện", txtNguoiDaiDien);
        addFormField(form, gbc, row++, "Địa chỉ", new JScrollPane(txtDiaChi));
        addFormField(form, gbc, row++, "Số điện thoại", txtSDT);
        addFormField(form, gbc, row++, "Email", txtEmail);
        addFormField(form, gbc, row++, "Công nợ", txtCongNo);
        addFormField(form, gbc, row++, "Ghi chú", new JScrollPane(txtGhiChu));
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 4, 0);
        form.add(chkTrangThai, gbc);

        panel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        btnAdd = createStyledButton(" Thêm mới", SUCCESS, Color.WHITE);
        btnUpdate = createStyledButton("️ Cập nhật", WARNING, Color.WHITE);
        btnDelete = createStyledButton("️ Xóa", DANGER, Color.WHITE);
        btnClear = createStyledButton(" Nhập lại", GRAY, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // COMPONENT HELPERS
    // ============================================================
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_PLAIN);
        field.setPreferredSize(new Dimension(200, 36));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY, 2),
                    new EmptyBorder(3, 9, 3, 9)
                ));
                field.setBackground(CARD_BG);
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER, 1),
                    new EmptyBorder(4, 10, 4, 10)
                ));
                field.setBackground(new Color(249, 250, 251));
            }
        });
        
        return field;
    }

    private JTextArea createModernTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 16);
        area.setFont(FONT_PLAIN);
        area.setForeground(TEXT);
        area.setBackground(new Color(249, 250, 251));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY, 2),
                    new EmptyBorder(5, 9, 5, 9)
                ));
                area.setBackground(CARD_BG);
            }
            @Override
            public void focusLost(FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER, 1),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                area.setBackground(new Color(249, 250, 251));
            }
        });
        
        return area;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 0, 2, 0);
        
        JPanel wrap = new JPanel(new BorderLayout(0, 2));
        wrap.setOpaque(false);
        wrap.add(createFieldLabel(label), BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        panel.add(wrap, gbc);
    }

    // ============================================================
    // STYLED BUTTON
    // ============================================================
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            private Color currentBg = bg;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (isEnabled()) {
                            currentBg = new Color(
                                Math.max(0, bg.getRed() - 30),
                                Math.max(0, bg.getGreen() - 30),
                                Math.max(0, bg.getBlue() - 30)
                            );
                            repaint();
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        currentBg = bg;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isEnabled() ? currentBg : new Color(200, 200, 200));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ============================================================
    // TABLE STYLE
    // ============================================================
    private void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(42);
        table.setBackground(CARD_BG);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(243, 244, 246));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(30, 41, 59));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        // Renderers
        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(249, 250, 251));
                }
                return c;
            }
        };
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        // Column widths
        int[] widths = {80, 160, 120, 110, 160, 120, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(right);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
    }

    // ============================================================
    // EVENTS
    // ============================================================
    private void addEvents() {
        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbTrangThai.setSelectedIndex(0);
            loadData();
        });
        txtSearch.addActionListener(e -> search());
        cbTrangThai.addActionListener(e -> search());

        tblNhaCungCap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblNhaCungCap.getSelectedRow();
                if (row >= 0) selectRow(row);
            }
        });

        btnAdd.addActionListener(e -> addNhaCungCap());
        btnUpdate.addActionListener(e -> updateNhaCungCap());
        btnDelete.addActionListener(e -> deleteNhaCungCap());
        btnClear.addActionListener(e -> clearForm());
    }

    // ============================================================
    // DATA METHODS
    // ============================================================
    public void loadData() {
        List<NhaCungCap> list = nhaCungCapController.getAllNhaCungCap();
        displayData(list);
    }

    private void search() {
        String keyword = txtSearch.getText().trim();
        String trangThai = (String) cbTrangThai.getSelectedItem();
        
        List<NhaCungCap> list = nhaCungCapController.searchNhaCungCap(keyword);
        
        // Filter by status
        if (!"Tất cả".equals(trangThai)) {
            boolean isActive = "Hoạt động".equals(trangThai);
            list.removeIf(ncc -> ncc.isTrangThai() != isActive);
        }
        
        displayData(list);
    }

    private void displayData(List<NhaCungCap> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (NhaCungCap ncc : list) {
                Object[] row = {
                    ncc.getMaNCC_Code(),
                    ncc.getTenNCC(),
                    ncc.getNguoiDaiDien(),
                    ncc.getSdt(),
                    ncc.getEmail(),
                    ValidateUtil.formatCurrencyVND(ncc.getCongNo()),
                    ncc.isTrangThai() ? " Hoạt động" : " Ngừng"
                };
                tableModel.addRow(row);
            }
        }
        lblCount.setText(list != null ? list.size() + " nhà cung cấp" : "0 nhà cung cấp");
    }

    private void selectRow(int row) {
        String maNCC = tableModel.getValueAt(row, 0).toString();
        List<NhaCungCap> list = nhaCungCapController.getAllNhaCungCap();

        for (NhaCungCap ncc : list) {
            if (ncc.getMaNCC_Code().equals(maNCC)) {
                selectedId = ncc.getMaNCC();
                txtMaNCC.setText(ncc.getMaNCC_Code());
                txtTenNCC.setText(ncc.getTenNCC());
                txtNguoiDaiDien.setText(ncc.getNguoiDaiDien());
                txtDiaChi.setText(ncc.getDiaChi());
                txtSDT.setText(ncc.getSdt());
                txtEmail.setText(ncc.getEmail());
                txtCongNo.setText(ValidateUtil.formatCurrencyVND(ncc.getCongNo()));
                txtGhiChu.setText(ncc.getGhiChu());
                chkTrangThai.setSelected(ncc.isTrangThai());

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                break;
            }
        }
    }

    private void addNhaCungCap() {
        NhaCungCap ncc = getFormData();
        if (ncc == null) return;

        if (nhaCungCapController.addNhaCungCap(ncc)) {
            MessageUtil.showInfo(" Thêm nhà cung cấp thành công!");
            clearForm();
            loadData();
        }
    }

    private void updateNhaCungCap() {
        if (selectedId == -1) {
            MessageUtil.showWarning("️ Vui lòng chọn nhà cung cấp cần sửa!");
            return;
        }

        NhaCungCap ncc = getFormData();
        if (ncc == null) return;
        ncc.setMaNCC(selectedId);

        // Fetch old data to prevent overwriting debt with 0
        NhaCungCap oldNcc = nhaCungCapController.getNhaCungCapById(selectedId);
        if (oldNcc != null) {
            ncc.setCongNo(oldNcc.getCongNo());
        }

        if (nhaCungCapController.updateNhaCungCap(ncc)) {
            MessageUtil.showInfo(" Cập nhật nhà cung cấp thành công!");
            clearForm();
            loadData();
        }
    }

    private void deleteNhaCungCap() {
        if (selectedId == -1) {
            MessageUtil.showWarning("️ Vui lòng chọn nhà cung cấp cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainPanel,
            "Bạn có chắc chắn muốn xóa nhà cung cấp này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhaCungCapController.deleteNhaCungCap(selectedId)) {
                MessageUtil.showInfo(" Xóa nhà cung cấp thành công!");
                clearForm();
                loadData();
            }
        }
    }

    private NhaCungCap getFormData() {
        String maNCC = txtMaNCC.getText().trim();
        String tenNCC = txtTenNCC.getText().trim();
        String nguoiDaiDien = txtNguoiDaiDien.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();
        boolean trangThai = chkTrangThai.isSelected();

        if (maNCC.isEmpty() || tenNCC.isEmpty()) {
            MessageUtil.showWarning("️ Vui lòng nhập mã và tên nhà cung cấp!");
            return null;
        }

        if (sdt.isEmpty()) {
            MessageUtil.showWarning("️ Vui lòng nhập số điện thoại!");
            return null;
        }

        if (!ValidateUtil.isValidPhone(sdt)) {
            MessageUtil.showWarning("️ Số điện thoại không hợp lệ!");
            return null;
        }

        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNCC_Code(maNCC);
        ncc.setTenNCC(tenNCC);
        ncc.setNguoiDaiDien(nguoiDaiDien);
        ncc.setDiaChi(diaChi);
        ncc.setSdt(sdt);
        ncc.setEmail(email);
        ncc.setCongNo(0);
        ncc.setGhiChu(ghiChu);
        ncc.setTrangThai(trangThai);

        return ncc;
    }

    private void clearForm() {
        selectedId = -1;
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtNguoiDaiDien.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtCongNo.setText("");
        txtGhiChu.setText("");
        chkTrangThai.setSelected(true);

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtMaNCC.requestFocus();
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}