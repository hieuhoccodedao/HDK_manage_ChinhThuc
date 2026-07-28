package hdkmanagement.view.dashboard;

import hdkmanagement.controller.KhachHangController;
import hdkmanagement.controller.SanPhamController;
import hdkmanagement.dao.HoaDonDAO;
import hdkmanagement.model.HoaDon;
import hdkmanagement.model.KhachHang;
import hdkmanagement.model.SanPham;
import hdkmanagement.util.IconUtil;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;
import hdkmanagement.view.auth.frmDangNhap;
import hdkmanagement.view.customer.frmKhachHang;
import hdkmanagement.view.employee.frmNhanVien;
import hdkmanagement.view.product.frmDanhMuc;
import hdkmanagement.view.product.frmSanPham;
import hdkmanagement.view.purchase.frmNhapHang;
import hdkmanagement.view.sale.frmBanHang;
import hdkmanagement.view.supplier.frmNhaCungCap;
import hdkmanagement.view.report.frmBaoCao;
import hdkmanagement.view.system.frmXemLog;
import hdkmanagement.view.sale.frmKhuyenMai;
import hdkmanagement.view.inventory.frmLichSuKho;
import hdkmanagement.view.finance.frmThuChi;
import hdkmanagement.view.finance.frmBaoCaoKQKD;
import hdkmanagement.view.hr.frmTinhLuong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class frmDashboard extends JFrame {

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel menuPanel;
    private CardLayout cardLayout;

    // ===== CÁC FORM =====
    private frmTrangChu trangChuForm;
    private frmSanPham sanPhamForm;
    private frmDanhMuc danhMucForm;
    private frmKhachHang khachHangForm;
    private frmNhaCungCap nhaCungCapForm;
    private frmNhanVien nhanVienForm;
    private frmNhapHang nhapHangForm;
    private frmBanHang banHangForm;
    private frmBaoCao baoCaoForm;
    private frmXemLog xemLogForm;
    private frmKhuyenMai khuyenMaiForm;
    private frmLichSuKho lichSuKhoForm;
    private frmThuChi thuChiForm;
    private frmBaoCaoKQKD baoCaoKQKDForm;
    private frmTinhLuong tinhLuongForm;
    private JScrollPane dashboardScrollPane;

    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton selectedMenuButton;

    // ===== MÀU SẮC =====
    private final Color PRIMARY_DARK = UITheme.PRIMARY_DARKER;
    private final Color PRIMARY_BLUE = new Color(30, 58, 138);
    private final Color ACCENT_BLUE = UITheme.PRIMARY;
    private final Color BG_GRAY = UITheme.BG;
    private final Color CARD_WHITE = UITheme.CARD_BG;
    private final Color BORDER_GRAY = UITheme.BORDER;
    private final Color TEXT_DARK = UITheme.TEXT_MEDIUM;
    private final Color TEXT_GRAY = UITheme.GRAY;
    private final Color MENU_BG = UITheme.TEXT_MEDIUM;
    private final Color MENU_HOVER = new Color(51, 65, 85);
    private final Color MENU_SELECTED = UITheme.PRIMARY;
    private final Color MENU_TEXT = new Color(148, 163, 184);
    private final Color WHITE = UITheme.TEXT_WHITE;

    public frmDashboard() {
        // Khởi tạo các form
        trangChuForm = new frmTrangChu();
        sanPhamForm = new frmSanPham();
        danhMucForm = new frmDanhMuc();
        khachHangForm = new frmKhachHang();
        nhaCungCapForm = new frmNhaCungCap();
        nhanVienForm = new frmNhanVien();
        nhapHangForm = new frmNhapHang();
        banHangForm = new frmBanHang();
        baoCaoForm = new frmBaoCao();
        xemLogForm = new frmXemLog();
        khuyenMaiForm = new frmKhuyenMai();
        lichSuKhoForm = new frmLichSuKho();
        thuChiForm = new frmThuChi();
        baoCaoKQKDForm = new frmBaoCaoKQKD();
        tinhLuongForm = new frmTinhLuong();

        initComponents();
        setLocationRelativeTo(null);
        showDashboard();
    }

    private void initComponents() {
        setTitle("Công Ty HDK - Hệ thống quản lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 600));

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_GRAY);

        createHeader();
        createMenu();
        createContentPanels();

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuScroll.setPreferredSize(new Dimension(240, 0));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuScroll, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    // ============================================================
    // HEADER
    // ============================================================
    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));

        // LEFT: Logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 15));
        leftPanel.setOpaque(false);
        JLabel lblLogo = new JLabel(new IconUtil(IconUtil.IconType.HOME, 24, ACCENT_BLUE));
        JLabel lblCompany = new JLabel("HDK ERP");
        lblCompany.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCompany.setForeground(TEXT_DARK);
        leftPanel.add(lblLogo);
        leftPanel.add(lblCompany);

        // RIGHT: User Info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        rightPanel.setOpaque(false);

        String employeeName = SessionManager.getInstance().getCurrentEmployeeName();
        JLabel lblAvatar = new JLabel(initials(employeeName), SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAvatar.setForeground(CARD_WHITE);
        lblAvatar.setPreferredSize(new Dimension(36, 36));
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(ACCENT_BLUE);
        
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        JLabel lblName = new JLabel(employeeName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(TEXT_DARK);
        JLabel lblRole = new JLabel("Quản trị viên");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(TEXT_GRAY);
        userInfo.add(lblName);
        userInfo.add(lblRole);
        
        JButton btnLogout = createFlatButton("ĐĂNG XUẤT", UITheme.DANGER, UITheme.TEXT_WHITE);
        btnLogout.addActionListener(e -> logout());
        
        JButton btnChatAI = createFlatButton(" HỎI AI", new Color(14, 165, 233), UITheme.TEXT_WHITE);
        btnChatAI.setIcon(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.ROBOT, 16, UITheme.TEXT_WHITE));
        btnChatAI.addActionListener(e -> {
            hdkmanagement.view.chatbot.frmChatbot chat = new hdkmanagement.view.chatbot.frmChatbot(frmDashboard.this);
            chat.setVisible(true);
        });
        
        rightPanel.add(lblAvatar);
        rightPanel.add(userInfo);
        rightPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        rightPanel.add(btnChatAI);
        rightPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        rightPanel.add(btnLogout);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }

    private String initials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        String first = String.valueOf(parts[0].charAt(0));
        return first.toUpperCase();
    }

    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ============================================================
    // MENU BÊN TRÁI
    // ============================================================
    private void createMenu() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(MENU_BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));

        JLabel lblMenuLogo = new JLabel("HỆ THỐNG", new IconUtil(IconUtil.IconType.DASHBOARD, 24, WHITE), SwingConstants.LEFT);
        lblMenuLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMenuLogo.setForeground(WHITE);
        lblMenuLogo.setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 0));
        lblMenuLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(lblMenuLogo);

        List<Object[]> menuList = new ArrayList<>();
        menuList.add(new Object[]{IconUtil.IconType.HOME, "Trang chủ", "home"});
        menuList.add(new Object[]{IconUtil.IconType.DASHBOARD, "Tổng quan", "dashboard"});
        menuList.add(new Object[]{IconUtil.IconType.PRODUCT, "Sản phẩm", "sanpham"});
        menuList.add(new Object[]{IconUtil.IconType.CATEGORY, "Danh mục", "danhmuc"});
        menuList.add(new Object[]{IconUtil.IconType.CUSTOMER, "Khách hàng", "khachhang"});
        menuList.add(new Object[]{IconUtil.IconType.SUPPLIER, "Nhà cung cấp", "nhacungcap"});
        menuList.add(new Object[]{IconUtil.IconType.EMPLOYEE, "Nhân viên", "nhanvien"});
        menuList.add(new Object[]{IconUtil.IconType.IMPORT, "Nhập hàng", "nhaphang"});
        menuList.add(new Object[]{IconUtil.IconType.SALE, "Bán hàng", "banhang"});
        menuList.add(new Object[]{IconUtil.IconType.SALE, "Khuyến mãi", "khuyenmai"});
        menuList.add(new Object[]{IconUtil.IconType.PRODUCT, "Lịch sử kho", "lichsukho"});
        menuList.add(new Object[]{IconUtil.IconType.MONEY, "Thu / Chi", "thuchi"});
        menuList.add(new Object[]{IconUtil.IconType.REPORT, "Kết quả KD", "kqkd"});
        menuList.add(new Object[]{IconUtil.IconType.REPORT, "Báo cáo", "baocao"});

        if (SessionManager.getInstance().isAdmin()) {
            menuList.add(new Object[]{IconUtil.IconType.EMPLOYEE, "Tính lương", "tinhluong"});
            menuList.add(new Object[]{IconUtil.IconType.SYSTEM_LOG, "Nhật ký hệ thống", "systemlog"});
        }

        for (Object[] item : menuList) {
            JButton btn = createMenuItem((IconUtil.IconType) item[0], (String) item[1], (String) item[2]);
            menuButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        menuPanel.add(Box.createVerticalGlue());
    }

    private JButton createMenuItem(IconUtil.IconType iconType, String text, String action) {
        JButton btn = new JButton("  " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean selected = this == selectedMenuButton;
                boolean hovered = getModel().isRollover();

                if (selected) {
                    g2d.setColor(MENU_SELECTED);
                    g2d.fill(new RoundRectangle2D.Double(10, 2, getWidth() - 20, getHeight() - 4, 12, 12));
                    g2d.setColor(new Color(255, 255, 255, 200));
                    g2d.fillRoundRect(0, getHeight() / 2 - 10, 4, 20, 4, 4);
                } else if (hovered) {
                    g2d.setColor(MENU_HOVER);
                    g2d.fill(new RoundRectangle2D.Double(10, 2, getWidth() - 20, getHeight() - 4, 12, 12));
                }
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("iconType", iconType);
        btn.setIcon(new IconUtil(iconType, 20, MENU_TEXT));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(MENU_TEXT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(240, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { updateMenuState(); }
            @Override
            public void mouseExited(MouseEvent e) { updateMenuState(); }
        });

        btn.addActionListener(e -> {
            selectedMenuButton = btn;
            updateMenuState();
            showPanel(action);
        });

        return btn;
    }

    private void updateMenuState() {
        for (JButton b : menuButtons) {
            boolean selected = (b == selectedMenuButton);
            boolean hovered = b.getModel().isRollover();
            Color c = (selected || hovered) ? WHITE : MENU_TEXT;
            b.setForeground(c);
            IconUtil.IconType type = (IconUtil.IconType) b.getClientProperty("iconType");
            b.setIcon(new IconUtil(type, 20, c));
        }
        menuPanel.repaint();
    }

    // ============================================================
    // CONTENT PANELS
    // ============================================================
    private void createContentPanels() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_GRAY);

        dashboardScrollPane = new JScrollPane(createDashboardPanel());
        dashboardScrollPane.setBorder(null);
        dashboardScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(trangChuForm.getPanel(), "home");
        contentPanel.add(dashboardScrollPane, "dashboard");

        contentPanel.add(sanPhamForm.getPanel(), "sanpham");
        contentPanel.add(danhMucForm.getPanel(), "danhmuc");
        contentPanel.add(khachHangForm.getPanel(), "khachhang");
        contentPanel.add(nhaCungCapForm.getPanel(), "nhacungcap");
        contentPanel.add(nhanVienForm.getPanel(), "nhanvien");
        contentPanel.add(nhapHangForm.getPanel(), "nhaphang");
        contentPanel.add(banHangForm.getPanel(), "banhang");
        contentPanel.add(khuyenMaiForm, "khuyenmai");
        contentPanel.add(lichSuKhoForm, "lichsukho");
        contentPanel.add(thuChiForm, "thuchi");
        contentPanel.add(baoCaoKQKDForm, "kqkd");
        contentPanel.add(tinhLuongForm, "tinhluong");
        contentPanel.add(baoCaoForm.getPanel(), "baocao");
        contentPanel.add(xemLogForm.getPanel(), "systemlog");
    }

    // ============================================================
    // NEW DASHBOARD PANEL
    // ============================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_GRAY);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Tiêu đề và nút Làm mới
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_GRAY);
        JLabel lblTitle = new JLabel("TỔNG QUAN HỆ THỐNG");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(TEXT_DARK);
        titlePanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = createFlatButton(" Làm mới", UITheme.PRIMARY, UITheme.TEXT_WHITE);
        btnRefresh.setPreferredSize(new Dimension(120, 36));
        btnRefresh.addActionListener(e -> {
            if (dashboardScrollPane != null) {
                dashboardScrollPane.setViewportView(createDashboardPanel());
            }
        });
        titlePanel.add(btnRefresh, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);

        // 1. Thẻ thống kê
        centerPanel.add(createQuickStatsPanel(), BorderLayout.NORTH);

        // 2. Biểu đồ và Bảng dữ liệu
        JPanel dataPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        dataPanel.setOpaque(false);
        dataPanel.add(createChartCard());
        dataPanel.add(createRecentOrdersCard());

        centerPanel.add(dataPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== 1. QUICK STATS =====
    private JPanel createQuickStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        // Lấy dữ liệu
        double revenueToday = 0;
        int ordersToday = 0;
        int lowStock = 0;
        double totalDebt = 0;

        try {
            HoaDonDAO hDao = new HoaDonDAO();
            Date today = new Date(System.currentTimeMillis());
            revenueToday = hDao.getRevenueByDate(today);

            List<HoaDon> hList = hDao.getAll();
            String todayStr = today.toString();
            for (HoaDon h : hList) {
                if (h.getNgayBan().toString().equals(todayStr)) {
                    ordersToday++;
                }
            }

            SanPhamController sCtrl = new SanPhamController();
            for (SanPham s : sCtrl.getAllSanPham()) {
                if (s.getTonKho() < 10) lowStock++;
            }

            KhachHangController kCtrl = new KhachHangController();
            for (KhachHang k : kCtrl.getAllKhachHang()) {
                totalDebt += k.getCongNo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        statsPanel.add(createStatCard("Doanh thu hôm nay", ValidateUtil.formatCurrencyVND(revenueToday), IconUtil.IconType.MONEY, UITheme.WARNING));
        statsPanel.add(createStatCard("Hóa đơn hôm nay", ordersToday + " đơn", IconUtil.IconType.SALE, UITheme.SUCCESS));
        statsPanel.add(createStatCard("Tồn kho thấp", lowStock + " sản phẩm", IconUtil.IconType.WARNING, UITheme.DANGER));
        statsPanel.add(createStatCard("Tổng công nợ", ValidateUtil.formatCurrencyVND(totalDebt), IconUtil.IconType.CUSTOMER, new Color(14, 165, 233)));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, IconUtil.IconType iconType, Color iconBg) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(BORDER_GRAY);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Icon Box
        JPanel iconBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(iconBg.getRed(), iconBg.getGreen(), iconBg.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(54, 54));
        JLabel lblIcon = new JLabel(new IconUtil(iconType, 28, iconBg), SwingConstants.CENTER);
        iconBox.add(lblIcon);

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SUB);
        lblTitle.setForeground(TEXT_GRAY);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(TEXT_DARK);
        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        card.add(iconBox, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // ===== 2. CHART CARD =====
    private JPanel createChartCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);

        JLabel lblTitle = new JLabel("BIỂU ĐỒ DOANH THU (7 NGÀY)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel chartWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(BORDER_GRAY);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chartWrapper.setOpaque(false);
        chartWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Custom vẽ biểu đồ thu nhỏ
        JPanel chartView = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Lưới ngang
                g2.setColor(new Color(240, 240, 240));
                for (int i = 1; i <= 4; i++) {
                    int y = h - (i * h / 5);
                    g2.drawLine(0, y, w, y);
                }

                // Vẽ Line ảo mượt mà (Mockup Line Chart)
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int[] data = {10, 40, 30, 80, 50, 90, 70};
                int points = data.length;
                int stepX = w / (points - 1);
                
                Path2D line = new Path2D.Float();
                line.moveTo(0, h - (data[0] * h / 100));
                for(int i = 1; i < points; i++) {
                    line.lineTo(i * stepX, h - (data[i] * h / 100));
                }
                g2.draw(line);

                // Các điểm node
                g2.setColor(WHITE);
                for(int i = 0; i < points; i++) {
                    int px = i * stepX;
                    int py = h - (data[i] * h / 100);
                    g2.fillOval(px - 4, py - 4, 8, 8);
                    g2.setColor(UITheme.PRIMARY);
                    g2.drawOval(px - 4, py - 4, 8, 8);
                    g2.setColor(WHITE);
                }

                g2.dispose();
            }
        };
        chartView.setOpaque(false);

        chartWrapper.add(chartView, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(chartWrapper, BorderLayout.CENTER);

        return card;
    }

    // ===== 3. RECENT ORDERS TABLE =====
    private JPanel createRecentOrdersCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);

        JLabel lblTitle = new JLabel("GIAO DỊCH GẦN ĐÂY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(BORDER_GRAY);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã HD", "Ngày", "Tổng Tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            HoaDonDAO hDao = new HoaDonDAO();
            List<HoaDon> list = hDao.getAll();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int limit = Math.min(5, list.size());
            for (int i = 0; i < limit; i++) {
                HoaDon h = list.get(i);
                model.addRow(new Object[]{
                    h.getMaHD_Code(),
                    sdf.format(h.getNgayBan()),
                    ValidateUtil.formatCurrencyVND(h.getTongTien())
                });
            }
        } catch (Exception e) {}

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(UITheme.TABLE_STRIPE);
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.setSelectionForeground(UITheme.TEXT_DARK);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(UITheme.CARD_BG);
        th.setForeground(UITheme.TEXT_MEDIUM);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(CARD_WHITE);

        tableWrapper.add(sp, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(tableWrapper, BorderLayout.CENTER);

        return card;
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        switch (name) {
            case "home": break;
            case "dashboard": break;
            case "sanpham": sanPhamForm.loadData(); break;
            case "danhmuc": danhMucForm.loadData(); break;
            case "khachhang": khachHangForm.loadData(); break;
            case "nhacungcap": nhaCungCapForm.loadData(); break;
            case "nhanvien": nhanVienForm.loadData(); break;
            case "nhaphang": nhapHangForm.loadData(); break;
            case "banhang":   banHangForm.loadData(); break;
            case "khuyenmai": break;
            case "lichsukho": break;
            case "thuchi":    break;
            case "kqkd":      break;
            case "tinhluong": break;
            case "baocao":    baoCaoForm.loadData(); break;
            case "systemlog": xemLogForm.loadData(); break;
        }
    }

    private void showDashboard() {
        if (!menuButtons.isEmpty()) {
            selectedMenuButton = menuButtons.get(0);
            updateMenuState();
        }
        showPanel("home");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            dispose();
            new frmDangNhap().setVisible(true);
        }
    }
}