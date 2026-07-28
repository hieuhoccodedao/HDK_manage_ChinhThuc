package hdkmanagement.view.report;

import hdkmanagement.dao.HoaDonDAO;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class frmBaoCao {

    private JPanel mainPanel;
    private JLabel lblTitle;

    private JLabel lblDoanhThu;
    private JLabel lblLoiNhuan;
    private JLabel lblSoHoaDon;

    private HoaDonDAO hoaDonDAO;

    public frmBaoCao() {
        hoaDonDAO = new HoaDonDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(UITheme.BG);
        mainPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblTitle = new JLabel("BÁO CÁO THỐNG KÊ");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Doanh thu, lợi nhuận và thống kê bán hàng theo tháng");
        lblSub.setFont(UITheme.FONT_SUB);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        headerPanel.add(titleBox, BorderLayout.WEST);

        // ===== METRICS CARDS =====
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setOpaque(false);

        lblDoanhThu = new JLabel("0 ₫");
        lblDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblDoanhThu.setForeground(UITheme.TEXT_DARK);

        lblLoiNhuan = new JLabel("0 ₫");
        lblLoiNhuan.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLoiNhuan.setForeground(UITheme.SUCCESS);

        lblSoHoaDon = new JLabel("0");
        lblSoHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSoHoaDon.setForeground(UITheme.PRIMARY);

        metricsPanel.add(createMetricCard("Tổng Doanh Thu", lblDoanhThu, UITheme.WARNING));
        metricsPanel.add(createMetricCard("Lợi Nhuận (Ước tính 30%)", lblLoiNhuan, UITheme.SUCCESS));
        metricsPanel.add(createMetricCard("Tổng Số Hóa Đơn", lblSoHoaDon, UITheme.PRIMARY));

        // ===== CHART PANEL =====
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        chartPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel chartTitle = new JLabel("BIỂU ĐỒ DOANH THU & LỢI NHUẬN 6 THÁNG GẦN NHẤT");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartTitle.setForeground(UITheme.TEXT_DARK);
        chartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        chartPanel.add(chartTitle, BorderLayout.NORTH);
        chartPanel.add(new CustomBarChart(), BorderLayout.CENTER);

        // ===== CENTER WRAPPER =====
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);
        centerWrapper.add(metricsPanel, BorderLayout.NORTH);
        centerWrapper.add(chartPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color iconColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(UITheme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SUB);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public void loadData() {
        try {
            double totalRevenue = hoaDonDAO.getTotalRevenue();
            double totalGiaVon = hoaDonDAO.getGiaVonByPeriod("2000-01-01", "2099-12-31");
            hdkmanagement.dao.PhieuThuChiDAO ptcDAO = new hdkmanagement.dao.PhieuThuChiDAO();
            double totalChiPhi = ptcDAO.getTongChi("2000-01-01", "2099-12-31");
            double totalProfit = totalRevenue - totalGiaVon - totalChiPhi;
            
            int totalOrders = hoaDonDAO.getAll().size();

            lblDoanhThu.setText(ValidateUtil.formatCurrencyVND(totalRevenue));
            lblLoiNhuan.setText(ValidateUtil.formatCurrencyVND(totalProfit));
            lblSoHoaDon.setText(String.valueOf(totalOrders));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== LỚP VẼ BIỂU ĐỒ CỘT =====
    private class CustomBarChart extends JPanel {
        private final String[] months = new String[6];
        private final double[] revenues = new double[6];
        private final double[] profits = new double[6];

        public CustomBarChart() {
            setOpaque(false);
            fetchRealData();
        }

        private void fetchRealData() {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            hdkmanagement.dao.HoaDonDAO hdDAO = new hdkmanagement.dao.HoaDonDAO();
            hdkmanagement.dao.PhieuThuChiDAO ptcDAO = new hdkmanagement.dao.PhieuThuChiDAO();
            
            for (int i = 5; i >= 0; i--) {
                java.util.Calendar mCal = (java.util.Calendar) cal.clone();
                mCal.add(java.util.Calendar.MONTH, -i);
                int nam = mCal.get(java.util.Calendar.YEAR);
                int thang = mCal.get(java.util.Calendar.MONTH) + 1;
                
                months[5 - i] = "T" + thang + "/" + (nam % 100);
                
                String tuNgay = String.format("%04d-%02d-01", nam, thang);
                int lastDay = mCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
                String denNgay = String.format("%04d-%02d-%02d", nam, thang, lastDay);
                
                double doanhThu = hdDAO.getRevenueByPeriod(tuNgay, denNgay);
                double giaVon = hdDAO.getGiaVonByPeriod(tuNgay, denNgay);
                double chiPhi = ptcDAO.getTongChi(tuNgay, denNgay);
                
                revenues[5 - i] = doanhThu;
                profits[5 - i] = doanhThu - giaVon - chiPhi;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Vẽ khung background
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, 16, 16));
            g2.setColor(UITheme.BORDER);
            g2.draw(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, 16, 16));

            // Padding biểu đồ
            int padTop = 30;
            int padBottom = 40;
            int padLeft = 80;
            int padRight = 30;

            int chartWidth = width - padLeft - padRight;
            int chartHeight = height - padTop - padBottom;

            // Tìm max revenue để tính scale
            double maxVal = 0;
            for (double v : revenues) {
                if (v > maxVal) maxVal = v;
            }
            if (maxVal == 0) maxVal = 1; // Tránh chia 0
            
            // Làm tròn maxVal lên mốc đẹp (VD: 250,000,000)
            maxVal = Math.ceil(maxVal / 50000000) * 50000000;

            // Vẽ các đường Grid ngang (5 đường)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(UITheme.TEXT_MUTED);
            for (int i = 0; i <= 5; i++) {
                int y = padTop + chartHeight - (i * chartHeight / 5);
                double val = (maxVal / 5) * i;
                
                String label = String.format("%,.0f đ", val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, padLeft - fm.stringWidth(label) - 10, y + 4);

                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(padLeft, y, width - padRight, y);
                g2.setColor(UITheme.TEXT_MUTED);
            }

            // Vẽ các cột
            int numBars = months.length;
            int barGroupWidth = chartWidth / numBars;
            int barWidth = barGroupWidth / 3;

            for (int i = 0; i < numBars; i++) {
                int x = padLeft + (i * barGroupWidth) + (barGroupWidth / 2) - barWidth;

                // Chiều cao cột
                int hRev = (int) ((revenues[i] / maxVal) * chartHeight);
                int hPro = (int) ((profits[i] / maxVal) * chartHeight);

                // Vẽ cột Doanh Thu
                g2.setColor(new Color(59, 130, 246)); // Blue
                g2.fillRect(x, padTop + chartHeight - hRev, barWidth - 2, hRev);

                // Vẽ cột Lợi Nhuận
                g2.setColor(new Color(34, 197, 94)); // Green
                g2.fillRect(x + barWidth, padTop + chartHeight - hPro, barWidth - 2, hPro);

                // Chữ tháng
                g2.setColor(UITheme.TEXT_DARK);
                FontMetrics fm = g2.getFontMetrics();
                String month = months[i];
                g2.drawString(month, x + barWidth - (fm.stringWidth(month) / 2) - 1, padTop + chartHeight + 20);
            }

            // Vẽ Legend (Chú thích)
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            int legX = width / 2 - 80;
            int legY = 15;
            
            g2.setColor(new Color(59, 130, 246));
            g2.fillRect(legX, legY, 12, 12);
            g2.setColor(UITheme.TEXT_DARK);
            g2.drawString("Doanh Thu", legX + 20, legY + 11);

            g2.setColor(new Color(34, 197, 94));
            g2.fillRect(legX + 100, legY, 12, 12);
            g2.setColor(UITheme.TEXT_DARK);
            g2.drawString("Lợi Nhuận", legX + 120, legY + 11);

            g2.dispose();
        }
    }
}
