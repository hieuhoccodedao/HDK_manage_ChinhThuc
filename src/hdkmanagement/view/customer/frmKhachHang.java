package hdkmanagement.view.customer;

import hdkmanagement.controller.KhachHangController;
import hdkmanagement.model.KhachHang;
import hdkmanagement.util.MessageUtil;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class frmKhachHang {

    private JPanel mainPanel;
    private JTextField txtSearch, txtMaKH, txtHoTen, txtSDT, txtEmail, txtCongNo;
    private JTextArea txtDiaChi, txtGhiChu;
    private JCheckBox chkTrangThai;
    private JTable tblKhachHang;
    private DefaultTableModel tableModel;
    private JButton btnSearch, btnRefresh, btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblCount;
    private JComboBox<String> cbSearchType;

    private KhachHangController khachHangController;
    private int selectedId = -1;

    // ===================================================================
    // MÀU SẮC & FONT - lấy từ UITheme dùng chung để đồng bộ toàn ứng dụng
    // ===================================================================
    private final Color PRIMARY       = UITheme.PRIMARY;
    private final Color PRIMARY_DARK  = UITheme.PRIMARY_DARK;
    private final Color PRIMARY_LIGHT = UITheme.PRIMARY_LIGHT;

    private final Color SUCCESS       = UITheme.SUCCESS;
    private final Color SUCCESS_HOVER = UITheme.SUCCESS_HOVER;
    private final Color WARNING       = UITheme.WARNING;
    private final Color WARNING_HOVER = UITheme.WARNING_HOVER;
    private final Color DANGER        = UITheme.DANGER;
    private final Color DANGER_HOVER  = UITheme.DANGER_HOVER;
    private final Color GRAY          = UITheme.GRAY;
    private final Color GRAY_HOVER    = UITheme.GRAY_HOVER;

    private final Color BG            = UITheme.BG;
    private final Color CARD_BG       = UITheme.CARD_BG;
    private final Color HEADER_BG     = UITheme.HEADER_BG;
    private final Color BORDER        = UITheme.BORDER_STRONG;

    private final Color TEXT_DARK     = UITheme.TEXT_DARK;
    private final Color TEXT_MEDIUM   = UITheme.TEXT_MEDIUM;
    private final Color TEXT_MUTED    = UITheme.TEXT_MUTED;
    private final Color TEXT_WHITE    = UITheme.TEXT_WHITE;

    private static final String BASE_FONT = UITheme.FONT_FAMILY;
    private final Font FONT_TITLE   = UITheme.font(Font.BOLD, 28);
    private final Font FONT_SUB     = UITheme.font(Font.PLAIN, 15);
    private final Font FONT_SECTION = UITheme.font(Font.BOLD, 18);
    private final Font FONT_BUTTON  = UITheme.font(Font.BOLD, 14);
    private final Font FONT_LABEL   = UITheme.font(Font.BOLD, 13);
    private final Font FONT_PLAIN   = UITheme.font(Font.PLAIN, 15);
    private final Font FONT_TABLE   = UITheme.font(Font.PLAIN, 14);
    private final Font FONT_HEADER  = UITheme.font(Font.BOLD, 13);

    public frmKhachHang() {
        khachHangController = new KhachHangController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        // TOP
        JPanel topPanel = createTopPanel();

        // CENTER
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setBackground(BG);
        splitPane.setResizeWeight(0.65);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createFormPanel());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        addEvents();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    // ===================================================================
    // TOP PANEL
    // ===================================================================
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(" Quản lý khách hàng");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSub = new JLabel("Tìm kiếm, thêm mới, cập nhật và quản lý thông tin khách hàng");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(TEXT_MUTED);

        header.add(lblTitle);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(lblSub);

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchLeft.setOpaque(false);

        // Search type combo
        cbSearchType = new JComboBox<>(new String[]{"Tất cả", "Mã KH", "Họ tên", "SĐT", "Email"});
        cbSearchType.setFont(FONT_PLAIN);
        cbSearchType.setPreferredSize(new Dimension(120, 38));
        cbSearchType.setBackground(CARD_BG);
        cbSearchType.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 10, 0, 10)
        ));

        txtSearch = new JTextField(25);
        txtSearch.setFont(FONT_PLAIN);
        txtSearch.setPreferredSize(new Dimension(300, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 14, 0, 14)
        ));
        txtSearch.setForeground(TEXT_MEDIUM);

        btnSearch = createStyledButton(" Tìm kiếm", PRIMARY, TEXT_WHITE);
        btnRefresh = createStyledButton(" Làm mới", GRAY, TEXT_WHITE);

        searchLeft.add(new JLabel("Tìm theo:"));
        searchLeft.add(cbSearchType);
        searchLeft.add(txtSearch);
        searchLeft.add(btnSearch);
        searchLeft.add(btnRefresh);

        searchBar.add(searchLeft, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(searchBar, BorderLayout.CENTER);

        return panel;
    }

    // ===================================================================
    // TABLE PANEL
    // ===================================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 20, 20)
        ));
        panel.setOpaque(true);

        // Title Bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel lblTitle = new JLabel(" Danh sách khách hàng");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(TEXT_DARK);

        lblCount = new JLabel("0 khách hàng");
        lblCount.setFont(FONT_SUB);
        lblCount.setForeground(TEXT_MUTED);

        titleBar.add(lblTitle, BorderLayout.WEST);
        titleBar.add(lblCount, BorderLayout.EAST);
        panel.add(titleBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã KH", "Họ tên", "SĐT", "Email", "Địa chỉ", "Công nợ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(tableModel);
        styleTable(tblKhachHang);

        JScrollPane scroll = new JScrollPane(tblKhachHang);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ===================================================================
    // FORM PANEL
    // ===================================================================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 20, 20)
        ));
        panel.setOpaque(true);

        // Title
        JLabel lblTitle = new JLabel("️ Thông tin khách hàng");
        lblTitle.setFont(FONT_SECTION);
        lblTitle.setForeground(TEXT_DARK);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Initialize fields
        txtMaKH = createModernTextField();
        txtHoTen = createModernTextField();
        txtSDT = createModernTextField();
        txtEmail = createModernTextField();
        txtCongNo = createModernTextField();
        txtCongNo.setEditable(false);
        txtCongNo.setBackground(new Color(248, 250, 252));
        txtCongNo.setForeground(TEXT_MUTED);

        txtDiaChi = createModernTextArea(2);
        txtGhiChu = createModernTextArea(2);

        chkTrangThai = new JCheckBox(" Đang hoạt động");
        chkTrangThai.setFont(FONT_PLAIN);
        chkTrangThai.setBackground(CARD_BG);
        chkTrangThai.setForeground(TEXT_MEDIUM);
        chkTrangThai.setSelected(true);
        chkTrangThai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkTrangThai.setFocusPainted(false);

        int row = 0;
        addFormField(form, gbc, row++, "MÃ KHÁCH HÀNG", txtMaKH);
        addFormField(form, gbc, row++, "HỌ TÊN", txtHoTen);
        addFormField(form, gbc, row++, "SỐ ĐIỆN THOẠI", txtSDT);
        addFormField(form, gbc, row++, "EMAIL", txtEmail);
        addFormField(form, gbc, row++, "ĐỊA CHỈ", new JScrollPane(txtDiaChi));
        addFormField(form, gbc, row++, "CÔNG NỢ", txtCongNo);
        addFormField(form, gbc, row++, "GHI CHÚ", new JScrollPane(txtGhiChu));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        form.add(chkTrangThai, gbc);

        // Scroll for form
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getViewport().setOpaque(false);
        formScroll.setOpaque(false);
        formScroll.getVerticalScrollBar().setUnitIncrement(14);
        panel.add(formScroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        btnAdd = createStyledButton(" Thêm mới", SUCCESS, TEXT_WHITE);
        btnUpdate = createStyledButton("️ Cập nhật", WARNING, TEXT_WHITE);
        btnDelete = createStyledButton("️ Xóa", DANGER, TEXT_WHITE);
        btnClear = createStyledButton(" Nhập lại", GRAY, TEXT_WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ===================================================================
    // COMPONENT HELPERS
    // ===================================================================
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_PLAIN);
        field.setPreferredSize(new Dimension(200, 38));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(TEXT_MEDIUM);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(4, 12, 4, 12)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY, 2, true),
                    new EmptyBorder(3, 11, 3, 11)
                ));
                field.setBackground(CARD_BG);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER, 1, true),
                    new EmptyBorder(4, 12, 4, 12)
                ));
                field.setBackground(new Color(249, 250, 251));
            }
        });

        return field;
    }

    private JTextArea createModernTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 16);
        area.setFont(FONT_PLAIN);
        area.setForeground(TEXT_MEDIUM);
        area.setBackground(new Color(249, 250, 251));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
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
        gbc.insets = new Insets(4, 0, 4, 0);

        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.add(createFieldLabel(label), BorderLayout.NORTH);
        wrap.add(field, BorderLayout.CENTER);
        panel.add(wrap, gbc);
    }

    // ===================================================================
    // STYLED BUTTON
    // ===================================================================
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
                g2d.setColor(isEnabled() ? currentBg : new Color(203, 213, 225));
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
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===================================================================
    // TABLE STYLE
    // ===================================================================
    private void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(42);
        table.setBackground(CARD_BG);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(238, 241, 245));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        // Cell renderer
        DefaultTableCellRenderer left = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                    c.setForeground(TEXT_MEDIUM);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
        left.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                    c.setForeground(TEXT_MEDIUM);
                }
                return c;
            }
        };
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 250, 252));
                    c.setForeground(TEXT_MEDIUM);
                }
                setBorder(new EmptyBorder(0, 0, 0, 16));
                return c;
            }
        };
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        // Set column widths
        int[] widths = {80, 160, 110, 180, 180, 120, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Apply renderers
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(left);
        table.getColumnModel().getColumn(4).setCellRenderer(left);
        table.getColumnModel().getColumn(5).setCellRenderer(right);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
    }

    // ===================================================================
    // EVENTS
    // ===================================================================
    private void addEvents() {
        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbSearchType.setSelectedIndex(0);
            loadData();
        });
        txtSearch.addActionListener(e -> search());

        tblKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKhachHang.getSelectedRow();
                if (row >= 0) selectRow(row);
            }
        });

        btnAdd.addActionListener(e -> addKhachHang());
        btnUpdate.addActionListener(e -> updateKhachHang());
        btnDelete.addActionListener(e -> deleteKhachHang());
        btnClear.addActionListener(e -> clearForm());
    }

    // ===================================================================
    // DATA METHODS
    // ===================================================================
    public void loadData() {
        List<KhachHang> list = khachHangController.getAllKhachHang();
        displayData(list);
    }

    private void search() {
        String keyword = txtSearch.getText().trim();
        String searchType = (String) cbSearchType.getSelectedItem();

        List<KhachHang> list;
        if (keyword.isEmpty() || "Tất cả".equals(searchType)) {
            list = khachHangController.getAllKhachHang();
        } else {
            list = khachHangController.searchKhachHang(keyword);
        }
        displayData(list);
    }

    private void displayData(List<KhachHang> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (KhachHang kh : list) {
                Object[] row = {
                        kh.getMaKH_Code(),
                        kh.getHoTen(),
                        kh.getSdt(),
                        kh.getEmail(),
                        kh.getDiaChi(),
                        ValidateUtil.formatCurrencyVND(kh.getCongNo()),
                        kh.isTrangThai() ? " Hoạt động" : " Ngừng"
                };
                tableModel.addRow(row);
            }
        }
        lblCount.setText(list != null ? list.size() + " khách hàng" : "0 khách hàng");
    }

    private void selectRow(int row) {
        String maKH = tableModel.getValueAt(row, 0).toString();
        List<KhachHang> list = khachHangController.getAllKhachHang();

        for (KhachHang kh : list) {
            if (kh.getMaKH_Code().equals(maKH)) {
                selectedId = kh.getMaKH();
                txtMaKH.setText(kh.getMaKH_Code());
                txtHoTen.setText(kh.getHoTen());
                txtSDT.setText(kh.getSdt());
                txtEmail.setText(kh.getEmail());
                txtDiaChi.setText(kh.getDiaChi());
                txtCongNo.setText(ValidateUtil.formatCurrencyVND(kh.getCongNo()));
                txtGhiChu.setText(kh.getGhiChu());
                chkTrangThai.setSelected(kh.isTrangThai());

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                break;
            }
        }
    }

    private void addKhachHang() {
        KhachHang kh = getFormData();
        if (kh == null) return;

        if (khachHangController.addKhachHang(kh)) {
            MessageUtil.showInfo(" Thêm khách hàng thành công!");
            clearForm();
            loadData();
        }
    }

    private void updateKhachHang() {
        if (selectedId == -1) {
            MessageUtil.showWarning("️ Vui lòng chọn khách hàng cần sửa!");
            return;
        }

        KhachHang kh = getFormData();
        if (kh == null) return;
        kh.setMaKH(selectedId);

        // Fetch old data to prevent overwriting debt and stats with 0/null
        KhachHang oldKh = khachHangController.getKhachHangById(selectedId);
        if (oldKh != null) {
            kh.setCongNo(oldKh.getCongNo());
            kh.setDiemTichLuy(oldKh.getDiemTichLuy());
            kh.setTongChiTieu(oldKh.getTongChiTieu());
            kh.setHangThe(oldKh.getHangThe());
        }

        if (khachHangController.updateKhachHang(kh)) {
            MessageUtil.showInfo(" Cập nhật khách hàng thành công!");
            clearForm();
            loadData();
        }
    }

    private void deleteKhachHang() {
        if (selectedId == -1) {
            MessageUtil.showWarning("️ Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainPanel,
                "Bạn có chắc chắn muốn xóa khách hàng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangController.deleteKhachHang(selectedId)) {
                MessageUtil.showInfo(" Xóa khách hàng thành công!");
                clearForm();
                loadData();
            }
        }
    }

    private KhachHang getFormData() {
        String maKH = txtMaKH.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();
        boolean trangThai = chkTrangThai.isSelected();

        if (maKH.isEmpty() || hoTen.isEmpty()) {
            MessageUtil.showWarning("️ Vui lòng nhập mã và họ tên khách hàng!");
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

        KhachHang kh = new KhachHang();
        kh.setMaKH_Code(maKH);
        kh.setHoTen(hoTen);
        kh.setSdt(sdt);
        kh.setEmail(email);
        kh.setDiaChi(diaChi);
        kh.setCongNo(0);
        kh.setGhiChu(ghiChu);
        kh.setTrangThai(trangThai);

        return kh;
    }

    private void clearForm() {
        selectedId = -1;
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtCongNo.setText("");
        txtGhiChu.setText("");
        chkTrangThai.setSelected(true);

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtMaKH.requestFocus();
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}