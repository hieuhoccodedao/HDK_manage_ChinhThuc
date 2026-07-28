// view/auth/frmDangNhap.java
package hdkmanagement.view.auth;

import hdkmanagement.dao.TaiKhoanDAO;
import hdkmanagement.model.TaiKhoan;
import hdkmanagement.util.SessionManager;
import hdkmanagement.view.dashboard.frmDashboard;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class frmDangNhap extends JFrame {

    private JPanel mainPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblStatus;
    private TaiKhoanDAO taiKhoanDAO;

    // ===== ANIMATION VARIABLES =====
    private float floatY = 0;
    private float floatAngle = 0;
    private String currentTime = "";
    private String currentDate = "";
    private Timer animationTimer;

    // ===== MÀU SẮC - lấy từ UITheme dùng chung để đồng bộ với toàn ứng dụng =====
    private final Color GRAD_START   = UITheme.PRIMARY;
    private final Color GRAD_END     = UITheme.PRIMARY_DARKER;
    private final Color ACCENT_COLOR = UITheme.PRIMARY;
    private final Color ACCENT_HOVER = UITheme.PRIMARY_DARK;
    private final Color DANGER_COLOR = UITheme.DANGER;
    private final Color WHITE        = UITheme.TEXT_WHITE;
    private final Color TEXT_MUTED   = UITheme.TEXT_MUTED;
    private final Color TEXT_DARK    = UITheme.TEXT_MEDIUM;
    private final Color FIELD_BG     = new Color(245, 245, 245);
    private final Color FIELD_BORDER = UITheme.BORDER_STRONG;
    private final Color FIELD_FOCUS  = UITheme.PRIMARY;

    // ===== KÍCH THƯỚC CỐ ĐỊNH CHO Ô NHẬP =====
    private final Dimension FIELD_SIZE = new Dimension(350, 46);

    public frmDangNhap() {
        initComponents();
        taiKhoanDAO = new TaiKhoanDAO();
        setLocationRelativeTo(null);
        startAnimation();
    }

    private void initComponents() {
        setTitle("ĐĂNG NHẬP - HDK Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // ===== PANEL CHÍNH =====
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setOpaque(false);

        // ===== LEFT PANEL - BLUE GRADIENT =====
        JPanel leftPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, GRAD_START, getWidth(), getHeight(), GRAD_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Hình tròn trang trí
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.fillOval(-50, 50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 150, 180, 180);
                g2d.fillOval(100, getHeight() - 100, 120, 120);

                // 1. Vẽ đồng hồ và ngày tháng
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 54));
                FontMetrics fmTime = g2d.getFontMetrics();
                int timeW = fmTime.stringWidth(currentTime);
                g2d.drawString(currentTime, (getWidth() - timeW) / 2, 120);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                FontMetrics fmDate = g2d.getFontMetrics();
                int dateW = fmDate.stringWidth(currentDate);
                g2d.drawString(currentDate, (getWidth() - dateW) / 2, 150);

                // 2. Vẽ Logo động ở trung tâm
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2 + (int) floatY - 20;

                // Bóng đổ
                g2d.setColor(new Color(0, 0, 0, 40));
                int shadowWidth = 100 - (int) (floatY * 1.5);
                g2d.fillOval(centerX - shadowWidth / 2, centerY + 80, shadowWidth, 15);

                // Xoay logo (tách riêng Graphics2D để không ảnh hưởng đến chữ bên dưới)
                Graphics2D g2dLogo = (Graphics2D) g2d.create();
                g2dLogo.translate(centerX, centerY);
                g2dLogo.rotate(Math.toRadians(floatAngle * 10));

                // Logo Circle
                g2dLogo.setColor(new Color(255, 255, 255, 200));
                g2dLogo.fillOval(-50, -50, 100, 100);
                hdkmanagement.util.IconUtil logoIcon = new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.LOGO_HDK, 56, UITheme.PRIMARY_DARKER);
                logoIcon.paintIcon(this, g2dLogo, -28, -28);

                g2dLogo.dispose(); // Hủy Graphics2D tạm thời

                // 3. Tiêu đề (dùng g2d gốc để đảm bảo không bị giật tọa độ)
                String title = "HDK MANAGEMENT";
                g2d.setFont(UITheme.font(Font.BOLD, 28));
                g2d.setColor(Color.WHITE);
                FontMetrics fmTitle = g2d.getFontMetrics();
                g2d.drawString(title, (getWidth() - fmTitle.stringWidth(title)) / 2, getHeight() - 110);
                
                String subtitle = "Công ty TNHH Xây dựng & Vật liệu HDK";
                g2d.setFont(UITheme.font(Font.PLAIN, 14));
                g2d.setColor(new Color(255, 255, 255, 200));
                FontMetrics fmSub = g2d.getFontMetrics();
                g2d.drawString(subtitle, (getWidth() - fmSub.stringWidth(subtitle)) / 2, getHeight() - 80);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(450, 600));

        // ===== RIGHT PANEL - FORM ĐĂNG NHẬP =====
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Header
        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(UITheme.font(Font.BOLD, 24));
        lblWelcome.setForeground(TEXT_DARK);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblWelcomeSub = new JLabel("Đăng nhập để tiếp tục quản lý dự án của bạn");
        lblWelcomeSub.setFont(UITheme.font(Font.PLAIN, 13));
        lblWelcomeSub.setForeground(TEXT_MUTED);
        lblWelcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(lblWelcome);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(lblWelcomeSub);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        // ----- Username -----
        JLabel lblUsername = new JLabel("TÊN ĐĂNG NHẬP");
        lblUsername.setFont(UITheme.font(Font.BOLD, 11));
        lblUsername.setForeground(TEXT_MUTED);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        styleField(txtUsername);
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập tên đăng nhập...");

        rightPanel.add(lblUsername);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(txtUsername);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ----- Password -----
        JLabel lblPassword = new JLabel("MẬT KHẨU");
        lblPassword.setFont(UITheme.font(Font.BOLD, 11));
        lblPassword.setForeground(TEXT_MUTED);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel chứa password + nút hiển thị
        JPanel passwordWrapper = new JPanel(new BorderLayout(0, 0));
        passwordWrapper.setBackground(FIELD_BG);
        passwordWrapper.setBorder(new RoundedLineBorder(FIELD_BORDER, 10, 14));
        passwordWrapper.setMaximumSize(FIELD_SIZE);
        passwordWrapper.setPreferredSize(FIELD_SIZE);
        passwordWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.font(Font.PLAIN, 14));
        txtPassword.setForeground(TEXT_DARK);
        txtPassword.setBackground(FIELD_BG);
        txtPassword.setCaretColor(TEXT_DARK);
        txtPassword.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        txtPassword.setOpaque(false);
        txtPassword.putClientProperty("JPasswordField.placeholderText", "Nhập mật khẩu...");
        
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordWrapper.setBorder(new RoundedLineBorder(FIELD_FOCUS, 10, 14));
                passwordWrapper.setBackground(Color.WHITE);
                txtPassword.setBackground(Color.WHITE);
            }
            @Override
            public void focusLost(FocusEvent e) {
                passwordWrapper.setBorder(new RoundedLineBorder(FIELD_BORDER, 10, 14));
                passwordWrapper.setBackground(FIELD_BG);
                txtPassword.setBackground(FIELD_BG);
            }
        });

        // Nút hiển thị mật khẩu
        JButton btnShowPassword = new JButton(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.EYE_OFF, 20, TEXT_MUTED));
        btnShowPassword.setFocusPainted(false);
        btnShowPassword.setBorderPainted(false);
        btnShowPassword.setContentAreaFilled(false);
        btnShowPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnShowPassword.setPreferredSize(new Dimension(40, 46));
        btnShowPassword.setMaximumSize(new Dimension(40, 46));
        btnShowPassword.addActionListener(e -> {
            if (txtPassword.getEchoChar() == 0) {
                txtPassword.setEchoChar('•');
                btnShowPassword.setIcon(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.EYE_OFF, 20, TEXT_MUTED));
            } else {
                txtPassword.setEchoChar((char) 0);
                btnShowPassword.setIcon(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.EYE, 20, TEXT_MUTED));
            }
        });

        passwordWrapper.add(txtPassword, BorderLayout.CENTER);
        passwordWrapper.add(btnShowPassword, BorderLayout.EAST);

        rightPanel.add(lblPassword);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(passwordWrapper);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // ----- Quên mật khẩu -----
        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(UITheme.font(Font.PLAIN, 12));
        lblForgot.setForeground(FIELD_FOCUS);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(frmDangNhap.this,
                    "Vui lòng liên hệ quản trị viên để đặt lại mật khẩu!\n Hotline: 1900 9999",
                    "Hỗ trợ", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        rightPanel.add(lblForgot);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ----- Status -----
        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.font(Font.PLAIN, 12));
        lblStatus.setForeground(DANGER_COLOR);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblStatus);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ----- Nút đăng nhập -----
        btnLogin = createRoundedButton("ĐĂNG NHẬP", GRAD_START, ACCENT_HOVER, Color.WHITE);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(FIELD_SIZE);
        btnLogin.setPreferredSize(FIELD_SIZE);

        // ----- Nút Thoát -----
        btnExit = createRoundedButton("THOÁT", new Color(245, 245, 245), new Color(235, 235, 235), TEXT_DARK);
        btnExit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExit.setMaximumSize(FIELD_SIZE);
        btnExit.setPreferredSize(FIELD_SIZE);

        rightPanel.add(btnLogin);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnExit);

        // Footer
        rightPanel.add(Box.createVerticalGlue());
        JLabel lblFooter = new JLabel("© 2024 HDK Management - Version 2.0");
        lblFooter.setFont(UITheme.font(Font.PLAIN, 11));
        lblFooter.setForeground(TEXT_MUTED);
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblFooter);

        // ===== KẾT HỢP LEFT + RIGHT =====
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        addEvents();
        makeDraggable(mainPanel);
        txtUsername.requestFocus();
    }

    /** Style hiện đại cho ô nhập liệu - KÍCH THƯỚC CỐ ĐỊNH */
    private void styleField(JTextField field) {
        field.setFont(UITheme.font(Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_DARK);
        field.setOpaque(true);
        
        // Cố định kích thước cho tất cả ô nhập
        field.setMaximumSize(FIELD_SIZE);
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setBorder(new RoundedLineBorder(FIELD_BORDER, 10, 14));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedLineBorder(FIELD_FOCUS, 10, 14));
                field.setBackground(Color.WHITE);
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedLineBorder(FIELD_BORDER, 10, 14));
                field.setBackground(FIELD_BG);
            }
        });
    }

    /** Nút bo góc */
    private JButton createRoundedButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton button = new JButton(text) {
            private Color currentBg = bg;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        currentBg = hoverBg;
                        repaint();
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
                g2d.setColor(getModel().isPressed() ? hoverBg.darker() : currentBg);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(UITheme.font(Font.BOLD, 14));
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(FIELD_SIZE);
        button.setPreferredSize(FIELD_SIZE);
        return button;
    }

    /** Kéo thả cửa sổ */
    private void makeDraggable(Component dragSource) {
        final Point[] offset = {null};
        dragSource.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offset[0] = e.getPoint();
            }
        });
        dragSource.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (offset[0] != null) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - offset[0].x, current.y - offset[0].y);
                }
            }
        });
    }

    private void addEvents() {
        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("️ Vui lòng nhập đầy đủ thông tin!");
            lblStatus.setForeground(DANGER_COLOR);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("ĐANG XỬ LÝ...");
        lblStatus.setText(" Đang kiểm tra...");
        lblStatus.setForeground(GRAD_START);

        SwingUtilities.invokeLater(() -> {
            try {
                TaiKhoan taiKhoan = taiKhoanDAO.login(username, password);

                if (taiKhoan != null) {
                    lblStatus.setText(" Đăng nhập thành công!");
                    lblStatus.setForeground(new Color(76, 175, 80));

                    SessionManager session = SessionManager.getInstance();
                    session.login(taiKhoan, taiKhoan.getNhanVien());

                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        frmDashboard dashboard = new frmDashboard();
                        dashboard.setVisible(true);
                    });
                } else {
                    lblStatus.setText(" Sai tên đăng nhập hoặc mật khẩu!");
                    lblStatus.setForeground(DANGER_COLOR);
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            } catch (Exception ex) {
                lblStatus.setText(" Lỗi: " + ex.getMessage());
                lblStatus.setForeground(DANGER_COLOR);
                ex.printStackTrace();
            } finally {
                btnLogin.setEnabled(true);
                btnLogin.setText("ĐĂNG NHẬP");
            }
        });
    }

    /** Border bo góc */
    private static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int padding;

        RoundedLineBorder(Color color, int radius, int padding) {
            this.color = color;
            this.radius = radius;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.6f));
            g2d.draw(new RoundRectangle2D.Double(x + 0.8, y + 0.8, width - 1.6, height - 1.6, radius, radius));
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(padding, padding, padding, padding);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = padding;
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiệu ứng Lơ lửng (Floating)
                floatAngle += 0.05f;
                floatY = (float) Math.sin(floatAngle) * 15f;

                // Cập nhật thời gian
                java.text.SimpleDateFormat timeFmt = new java.text.SimpleDateFormat("HH:mm:ss");
                java.text.SimpleDateFormat dateFmt = new java.text.SimpleDateFormat("EEEE, dd 'tháng' MM, yyyy", new java.util.Locale("vi", "VN"));
                java.util.Date now = new java.util.Date();
                currentTime = timeFmt.format(now);
                currentDate = dateFmt.format(now);

                // Chỉ repaint lại phần leftPanel thay vì toàn bộ mainPanel
                repaint(0, 0, 450, getHeight());
            }
        });
        animationTimer.start();
    }
}