package hdkmanagement.view.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

public final class UITheme {

    private UITheme() {}

    // BẢNG MÀU CHỦ ĐẠO
    public static final Color PRIMARY        = new Color(37, 99, 235);   // blue-600
    public static final Color PRIMARY_DARK   = new Color(29, 78, 216);   // blue-700
    public static final Color PRIMARY_DARKER = new Color(15, 23, 42);    // slate-900
    public static final Color PRIMARY_LIGHT  = new Color(219, 234, 254); // blue-100

    public static final Color SUCCESS        = new Color(16, 185, 129);  // emerald-500
    public static final Color SUCCESS_HOVER  = new Color(5, 150, 105);   // emerald-600
    public static final Color WARNING        = new Color(245, 158, 11);  // amber-500
    public static final Color WARNING_HOVER  = new Color(217, 119, 6);   // amber-600
    public static final Color DANGER         = new Color(239, 68, 68);   // red-500
    public static final Color DANGER_HOVER   = new Color(220, 38, 38);   // red-600
    public static final Color GRAY           = new Color(107, 114, 128); // gray-500
    public static final Color GRAY_HOVER     = new Color(75, 85, 99);    // gray-600

    public static final Color BG             = new Color(241, 245, 249); // slate-100
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color HEADER_BG      = new Color(15, 23, 42);    // slate-900
    public static final Color BORDER         = new Color(226, 232, 240); // slate-200
    public static final Color BORDER_STRONG  = new Color(203, 213, 225); // slate-300

    public static final Color TEXT_DARK      = new Color(15, 23, 42);    
    public static final Color TEXT_MEDIUM    = new Color(30, 41, 59);    
    public static final Color TEXT_MUTED     = new Color(100, 116, 139); 
    public static final Color TEXT_WHITE     = Color.WHITE;

    public static final Color TABLE_STRIPE   = new Color(248, 250, 252); // slate-50
    public static final Color TABLE_SELECTED = new Color(219, 234, 254); // blue-100

    public static final String FONT_FAMILY = pickReadableFont();

    public static final Font FONT_TITLE   = font(Font.BOLD, 26);
    public static final Font FONT_SUB     = font(Font.PLAIN, 14);
    public static final Font FONT_SECTION = font(Font.BOLD, 17);
    public static final Font FONT_BUTTON  = font(Font.BOLD, 13);
    public static final Font FONT_LABEL   = font(Font.BOLD, 13);
    public static final Font FONT_INPUT   = font(Font.PLAIN, 14);
    public static final Font FONT_TABLE   = font(Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER = font(Font.BOLD, 13);

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    private static String pickReadableFont() {
        String[] preferred = {"Segoe UI", "Inter", "Roboto", "Noto Sans", "Arial", "Tahoma"};
        Set<String> available = new HashSet<>();
        for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            available.add(name);
        }
        for (String candidate : preferred) {
            if (available.contains(candidate)) return candidate;
        }
        return "SansSerif";
    }

    // ===================================================================
    // BUTTON BO GÓC + ANIMATION CHUYỂN MÀU MƯỢT MÀ
    // ===================================================================
    public static JButton button(String text, Color base, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            private float alpha = 0f;
            private Timer timer;
            private boolean hovered = false;

            {
                timer = new Timer(15, e -> {
                    if (hovered && alpha < 1f) {
                        alpha += 0.15f;
                        if (alpha > 1f) alpha = 1f;
                        repaint();
                    } else if (!hovered && alpha > 0f) {
                        alpha -= 0.15f;
                        if (alpha < 0f) alpha = 0f;
                        repaint();
                    } else {
                        timer.stop();
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; timer.start(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; timer.start(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color current = !isEnabled() ? new Color(203, 213, 225) : 
                    new Color(
                        (int)(base.getRed() + (hover.getRed() - base.getRed()) * alpha),
                        (int)(base.getGreen() + (hover.getGreen() - base.getGreen()) * alpha),
                        (int)(base.getBlue() + (hover.getBlue() - base.getBlue()) * alpha)
                    );

                // Bóng nhẹ dưới nút
                if (!getModel().isPressed() && isEnabled()) {
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), getHeight() - 2, 10, 10));
                }

                g2.setColor(getModel().isPressed() ? hover.darker() : current);
                int yOffset = getModel().isPressed() ? 1 : 0;
                g2.fill(new RoundRectangle2D.Float(0, yOffset, getWidth(), getHeight() - 2, 10, 10));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton primaryButton(String text)  { return button(text, PRIMARY, PRIMARY_DARK, TEXT_WHITE); }
    public static JButton successButton(String text)  { return button(text, SUCCESS, SUCCESS_HOVER, TEXT_WHITE); }
    public static JButton warningButton(String text)   { return button(text, WARNING, WARNING_HOVER, TEXT_WHITE); }
    public static JButton dangerButton(String text)    { return button(text, DANGER, DANGER_HOVER, TEXT_WHITE); }
    public static JButton grayButton(String text)      { return button(text, GRAY, GRAY_HOVER, TEXT_WHITE); }
    public static JButton outlineButton(String text)   { return button(text, CARD_BG, BORDER, TEXT_MEDIUM); }

    // ===================================================================
    // CARD BO GÓC + ANIMATION 3D ELEVATION (Nảy thẻ khi Hover)
    // ===================================================================
    public static JPanel card() {
        return card(new BorderLayout());
    }

    public static JPanel card(LayoutManager layout) {
        JPanel shadowWrap = new JPanel(new BorderLayout());
        shadowWrap.setOpaque(false);

        JPanel content = new JPanel(layout) {
            private float hoverAlpha = 0f;
            private Timer timer;
            private boolean hovered = false;

            {
                timer = new Timer(15, e -> {
                    if (hovered && hoverAlpha < 1f) {
                        hoverAlpha += 0.1f;
                        if (hoverAlpha > 1f) hoverAlpha = 1f;
                        repaint();
                    } else if (!hovered && hoverAlpha > 0f) {
                        hoverAlpha -= 0.1f;
                        if (hoverAlpha < 0f) hoverAlpha = 0f;
                        repaint();
                    } else {
                        timer.stop();
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; timer.start(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; timer.start(); }
                });
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int yOffset = (int)(3 * hoverAlpha);
                int shadowAlpha = 15 + (int)(15 * hoverAlpha);
                int shadowSpread = (int)(2 * hoverAlpha);
                
                // Bóng đổ lan tỏa theo độ cao
                g2.setColor(new Color(15, 23, 42, shadowAlpha));
                g2.fill(new RoundRectangle2D.Float(2 - shadowSpread, 3, getWidth() - 4 + shadowSpread*2, getHeight() - 4, 14, 14));
                
                // Dịch chuyển nội dung thẻ lên yOffset
                g2.translate(0, -yOffset);
                
                // Nền thẻ
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 6, 14, 14));
                g2.setColor(new Color(226, 232, 240, 200)); // BORDER nhạt
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 7, 14, 14));
                
                // Vẽ các children (labels, charts...) bị dịch lên
                super.paint(g2); 
                g2.dispose();
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Không làm gì, đã vẽ ở paint()
            }
        };
        
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 20, 20, 20));
        shadowWrap.add(content, BorderLayout.CENTER);
        shadowWrap.putClientProperty("content", content);
        return content;
    }

    // ===================================================================
    // Ô NHẬP LIỆU CHUẨN HÓA
    // ===================================================================
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_STRONG, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
    }

    // ===================================================================
    // BẢNG DỮ LIỆU CÓ ROW HOVER (Sáng dòng khi di chuột)
    // ===================================================================
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(36); // Cao hơn một chút cho sang trọng
        table.setGridColor(BORDER);
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        header.setReorderingAllowed(false);
        
        // Thêm MouseMotion để Hover Effect
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != (Integer) (table.getClientProperty("hoverRow") != null ? table.getClientProperty("hoverRow") : -1)) {
                    table.putClientProperty("hoverRow", row);
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.putClientProperty("hoverRow", -1);
                table.repaint();
            }
        });

        // Set Renderer để tô màu nền
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    Object hoverRowObj = table.getClientProperty("hoverRow");
                    int hoverRow = hoverRowObj != null ? (Integer) hoverRowObj : -1;
                    if (row == hoverRow) {
                        c.setBackground(new Color(241, 245, 249)); // slate-100
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_STRIPE);
                    }
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }
}
