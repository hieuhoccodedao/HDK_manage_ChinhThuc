package hdkmanagement.view.finance;

import hdkmanagement.dao.HoaDonDAO;
import hdkmanagement.dao.PhieuThuChiDAO;
import hdkmanagement.dao.PhieuNhapDAO;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Calendar;

public class frmBaoCaoKQKD extends JPanel {

    private JSpinner spinnerNam;
    private JComboBox<String> cboThang;
    private JLabel lblDoanhThu, lblGiaVon, lblChiPhi, lblLoiNhuan, lblTienNhap;
    private BarChartPanel barChart;

    private HoaDonDAO hoaDonDAO;
    private PhieuThuChiDAO ptcDAO;
    private PhieuNhapDAO phieuNhapDAO;

    public frmBaoCaoKQKD() {
        hoaDonDAO = new HoaDonDAO();
        ptcDAO    = new PhieuThuChiDAO();
        phieuNhapDAO = new PhieuNhapDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // TOP PANEL
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setBackground(UITheme.BG);

        JLabel title = new JLabel("BÁO CÁO KẾT QUẢ KINH DOANH", SwingConstants.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 22));
        title.setForeground(UITheme.PRIMARY);
        top.add(title, BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        filterBar.setBackground(UITheme.CARD_BG);
        filterBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, UITheme.BORDER));

        String[] thangItems = {"Cả năm","Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6",
                               "Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"};
        cboThang   = new JComboBox<>(thangItems);
        cboThang.setFont(UITheme.FONT_INPUT);
        spinnerNam = new JSpinner(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2020, 2099, 1));
        spinnerNam.setFont(UITheme.FONT_INPUT);
        JButton btnLoad = UITheme.primaryButton("  Xem báo cáo");
        btnLoad.addActionListener(e -> loadData());

        filterBar.add(new JLabel("Tháng:")); filterBar.add(cboThang);
        filterBar.add(new JLabel("Năm:"));   filterBar.add(spinnerNam);
        filterBar.add(btnLoad);
        top.add(filterBar, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // KPI CARDS
        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 16, 0));
        cardsPanel.setBackground(UITheme.BG);
        cardsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        lblDoanhThu = new JLabel("0 đ"); lblGiaVon  = new JLabel("0 đ");
        lblChiPhi   = new JLabel("0 đ"); lblLoiNhuan = new JLabel("0 đ");
        lblTienNhap = new JLabel("0 đ");

        cardsPanel.add(buildKpi(" Doanh Thu", lblDoanhThu, UITheme.SUCCESS));
        cardsPanel.add(buildKpi(" Giá Vốn",   lblGiaVon,   UITheme.WARNING));
        cardsPanel.add(buildKpi(" Chi Phí Khác", lblChiPhi, UITheme.DANGER));
        cardsPanel.add(buildKpi(" Lợi Nhuận", lblLoiNhuan, UITheme.PRIMARY));
        cardsPanel.add(buildKpi(" Nhập Hàng", lblTienNhap, new Color(156, 39, 176)));

        // BAR CHART
        barChart = new BarChartPanel();
        barChart.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        barChart.setBackground(UITheme.CARD_BG);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(UITheme.BG);
        center.add(cardsPanel, BorderLayout.NORTH);
        center.add(barChart, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildKpi(String titleText, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                g2.setColor(UITheme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 16, 18, 16));
        JLabel lTitle = new JLabel(titleText, SwingConstants.CENTER);
        lTitle.setFont(UITheme.font(Font.BOLD, 13)); lTitle.setForeground(UITheme.TEXT_MEDIUM);
        valueLabel.setFont(UITheme.font(Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        int nam = (int) spinnerNam.getValue();
        int thang = cboThang.getSelectedIndex();
        String tuNgay, denNgay;
        if (thang == 0) { tuNgay = nam + "-01-01"; denNgay = nam + "-12-31"; }
        else { 
            tuNgay = String.format("%04d-%02d-01", nam, thang); 
            // Handle last day of month correctly for basic SQL
            Calendar cal = Calendar.getInstance();
            cal.set(nam, thang - 1, 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            denNgay = String.format("%04d-%02d-%02d", nam, thang, lastDay); 
        }

        double doanhThu = hoaDonDAO.getRevenueByPeriod(tuNgay, denNgay);
        double chiPhi   = ptcDAO.getTongChi(tuNgay, denNgay);
        double giaVon   = hoaDonDAO.getGiaVonByPeriod(tuNgay, denNgay);
        double tienNhap = phieuNhapDAO.getTongTienNhapByPeriod(tuNgay, denNgay);
        double loiNhuan = doanhThu - giaVon - chiPhi;

        lblDoanhThu.setText(ValidateUtil.formatCurrencyVND(doanhThu));
        lblGiaVon.setText(ValidateUtil.formatCurrencyVND(giaVon));
        lblChiPhi.setText(ValidateUtil.formatCurrencyVND(chiPhi));
        lblLoiNhuan.setText(ValidateUtil.formatCurrencyVND(loiNhuan));
        lblTienNhap.setText(ValidateUtil.formatCurrencyVND(tienNhap));
        lblLoiNhuan.setForeground(loiNhuan >= 0 ? UITheme.SUCCESS : UITheme.DANGER);
        barChart.setData(doanhThu, giaVon, chiPhi, loiNhuan, tienNhap);
    }

    public JPanel getPanel() { return this; }

    // Inner class: Bar Chart
    private static class BarChartPanel extends JPanel {
        private double[] values = {0, 0, 0, 0, 0};
        private final String[] labels  = {"Doanh Thu", "Giá Vốn", "Chi Phí", "Lợi Nhuận", "Nhập Hàng"};
        private final Color[]  colors  = {UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER, UITheme.PRIMARY, new Color(156, 39, 176)};

        public void setData(double dt, double gv, double cp, double ln, double nh) {
            values = new double[]{dt, gv, cp, Math.max(0, ln), nh};
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padL = 60, padB = 40, padT = 30;
            int chartW = w - padL - 20, chartH = h - padB - padT;

            double max = 1;
            for (double v : values) if (v > max) max = v;

            // Grid lines
            g2.setColor(new Color(230, 234, 240));
            g2.setFont(UITheme.font(Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                int y = padT + chartH - (i * chartH / 4);
                g2.drawLine(padL, y, padL + chartW, y);
                double val = (max / 4) * i;
                g2.setColor(UITheme.TEXT_MUTED);
                g2.drawString(ValidateUtil.shortCurrency(val), 2, y + 4);
                g2.setColor(new Color(230, 234, 240));
            }

            // Bars
            int barW = chartW / (values.length * 2);
            for (int i = 0; i < values.length; i++) {
                int barH = (int)((values[i] / max) * chartH);
                int x = padL + i * (chartW / values.length) + (chartW / values.length - barW) / 2;
                int y = padT + chartH - barH;

                // Bar
                g2.setColor(colors[i]);
                g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 8, 8));

                // Value label above bar
                g2.setColor(UITheme.TEXT_DARK);
                g2.setFont(UITheme.font(Font.BOLD, 11));
                String val = ValidateUtil.shortCurrency(values[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, x + (barW - fm.stringWidth(val)) / 2, y - 5);

                // X label
                g2.setFont(UITheme.font(Font.PLAIN, 11));
                g2.setColor(UITheme.TEXT_MEDIUM);
                int lw = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (barW - lw) / 2, h - 10);
            }
        }
    }
}
