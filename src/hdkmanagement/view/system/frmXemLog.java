package hdkmanagement.view.system;

import hdkmanagement.dao.SystemLogDAO;
import hdkmanagement.model.SystemLog;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class frmXemLog {
    private JPanel mainPanel;
    private JPanel feedPanel;
    private JTextField txtSearch;
    private SystemLogDAO logDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
    
    private String currentCategory = "Tất cả";
    private String[] categories = {"Tất cả", "Bán hàng", "Nhập hàng", "Thu / Chi", "Sản phẩm & Kho", "Hệ thống"};
    private List<JButton> tabButtons = new ArrayList<>();

    public frmXemLog() {
        logDAO = SystemLogDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UITheme.BG);
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("NHẬT KÝ HỆ THỐNG");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);
        
        // TABS PANEL
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        for (String cat : categories) {
            JButton btnTab = new JButton(cat) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (currentCategory.equals(cat)) {
                        g2.setColor(UITheme.PRIMARY);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                        setForeground(Color.WHITE);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                        g2.setColor(UITheme.BORDER);
                        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                        setForeground(UITheme.TEXT_MEDIUM);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btnTab.setFont(UITheme.font(Font.BOLD, 13));
            btnTab.setContentAreaFilled(false);
            btnTab.setFocusPainted(false);
            btnTab.setBorder(new EmptyBorder(8, 16, 8, 16));
            btnTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnTab.addActionListener(e -> {
                currentCategory = cat;
                for(JButton b : tabButtons) b.repaint();
                loadData();
            });
            tabButtons.add(btnTab);
            tabsPanel.add(btnTab);
        }
        
        JPanel titleAndTabs = new JPanel(new BorderLayout());
        titleAndTabs.setOpaque(false);
        titleAndTabs.add(lblTitle, BorderLayout.NORTH);
        titleAndTabs.add(tabsPanel, BorderLayout.CENTER);
        
        // SEARCH PANEL
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 36));
        txtSearch.setFont(UITheme.FONT_INPUT);
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(UITheme.PRIMARY);
        btnSearch.setForeground(UITheme.TEXT_WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSearch.addActionListener(e -> loadData());
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setBackground(UITheme.GRAY);
        btnRefresh.setForeground(UITheme.TEXT_WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            currentCategory = "Tất cả";
            for(JButton b : tabButtons) b.repaint();
            loadData();
        });

        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        headerPanel.add(titleAndTabs, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // ACTIVITY FEED
        feedPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                
                if (getComponentCount() > 0) {
                    g2.setColor(UITheme.BORDER_STRONG);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(40, 20, 40, getHeight() - 20);
                }
                g2.dispose();
            }
        };
        feedPanel.setOpaque(false);
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(feedPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        feedPanel.removeAll();
        String keyword = txtSearch.getText().trim();
        List<SystemLog> list = logDAO.getByCategory(currentCategory, keyword);
        
        for (SystemLog log : list) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(12, 45, 12, 10));
            
            JPanel content = new JPanel(new GridLayout(2, 1, 0, 4));
            content.setOpaque(false);
            
            String user = log.getTenDangNhap() != null ? log.getTenDangNhap() : "Hệ thống";
            JLabel lblAction = new JLabel("<html><b>" + log.getHanhDong() + "</b> bởi <font color='#2563eb'>" + user + "</font></html>");
            lblAction.setFont(UITheme.font(Font.PLAIN, 14));
            lblAction.setForeground(UITheme.TEXT_DARK);
            
            JLabel lblTimeDetail = new JLabel(sdf.format(log.getNgayTao()) + " - " + log.getChiTiet());
            lblTimeDetail.setFont(UITheme.font(Font.PLAIN, 12));
            lblTimeDetail.setForeground(UITheme.TEXT_MUTED);
            
            content.add(lblAction);
            content.add(lblTimeDetail);
            row.add(content, BorderLayout.CENTER);
            
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(UITheme.PRIMARY);
                    g2.fillOval(7, 16, 12, 12);
                    
                    g2.setColor(new Color(37, 99, 235, 50));
                    g2.fillOval(3, 12, 20, 20);
                    
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(26, 0));
            row.add(dot, BorderLayout.WEST);
            
            feedPanel.add(row);
        }
        
        if (list.isEmpty()) {
            JLabel empty = new JLabel("    Không tìm thấy nhật ký hoạt động nào.");
            empty.setFont(UITheme.font(Font.PLAIN, 14));
            empty.setForeground(UITheme.TEXT_MUTED);
            feedPanel.add(empty);
        }
        
        feedPanel.revalidate();
        feedPanel.repaint();
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
