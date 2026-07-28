package hdkmanagement.view.chatbot;

import hdkmanagement.api.GeminiAPI;
import hdkmanagement.controller.KhachHangController;
import hdkmanagement.controller.SanPhamController;
import hdkmanagement.dao.HoaDonDAO;
import hdkmanagement.model.HoaDon;
import hdkmanagement.model.KhachHang;
import hdkmanagement.model.SanPham;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.util.List;

public class frmChatbot extends JDialog {

    private JTextPane txtChatHistory;
    private JTextField txtMessage;
    private JButton btnSend;
    private JLabel lblStatus;

    private HTMLEditorKit htmlKit;
    private HTMLDocument htmlDoc;
    private StringBuilder chatHtml;

    public frmChatbot(Frame parent) {
        super(parent, "AI Trợ lý HDK", false);
        initComponents();
        setSize(400, 600);
        setLocationRelativeTo(parent);
        
        // Vị trí mở ở góc dưới bên phải màn hình
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width - getWidth() - 50, screenSize.height - getHeight() - 100);
        
        // Khởi động chat bằng câu chào
        appendMessage("AI Trợ Lý", "Xin chào! Tôi là AI phân tích dữ liệu của HDK Management. Tôi đã đọc xong dữ liệu doanh thu, kho hàng và công nợ của công ty hôm nay. Bạn muốn biết gì nào?", false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ----- HEADER -----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel(" AI HDK Management");
        lblTitle.setIcon(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.ROBOT, 24, Color.WHITE));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        lblStatus = new JLabel("Đang kết nối...");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(255, 255, 255, 200));
        headerPanel.add(lblStatus, BorderLayout.EAST);

        // ----- CHAT HISTORY -----
        txtChatHistory = new JTextPane();
        txtChatHistory.setEditable(false);
        txtChatHistory.setBackground(Color.WHITE);
        
        htmlKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        txtChatHistory.setEditorKit(htmlKit);
        txtChatHistory.setDocument(htmlDoc);
        chatHtml = new StringBuilder();
        chatHtml.append("<html><body style='font-family: Segoe UI, sans-serif; font-size: 13px;'>");
        updateChatView();

        JScrollPane scrollPane = new JScrollPane(txtChatHistory);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ----- INPUT PANEL -----
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        txtMessage = new JTextField();
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMessage.setPreferredSize(new Dimension(0, 40));
        txtMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        btnSend = new JButton(" GỬI") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? UITheme.PRIMARY_DARK : UITheme.PRIMARY);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSend.setIcon(new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.SEND, 14, Color.WHITE));
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputPanel.add(txtMessage, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);

        // Xử lý sự kiện
        btnSend.addActionListener(e -> sendMessage());
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
        
        lblStatus.setText("Sẵn sàng");
    }

    private void appendMessage(String sender, String message, boolean isUser) {
        // Chống lỗi HTML injection nhẹ từ người dùng
        String safeMsg = message.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
        
        String color = isUser ? "#E3F2FD" : "#F5F5F5"; // Xanh nhạt cho User, xám cho AI
        String align = isUser ? "right" : "left";
        String textColor = isUser ? "#0D47A1" : "#212121";

        String bubble = String.format(
            "<div style='text-align: %s; margin-bottom: 10px;'>" +
            "<span style='font-size: 10px; color: #757575;'>%s</span><br/>" +
            "<div style='display: inline-block; background-color: %s; color: %s; padding: 8px 12px; border-radius: 12px; max-width: 80%%; text-align: left;'>" +
            "%s</div></div>",
            align, sender, color, textColor, safeMsg
        );
        chatHtml.append(bubble);
        updateChatView();
    }

    private void updateChatView() {
        txtChatHistory.setText(chatHtml.toString() + "</body></html>");
        // Cuộn xuống dòng cuối cùng
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) txtChatHistory.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void sendMessage() {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty()) return;

        appendMessage("Bạn", msg, true);
        txtMessage.setText("");
        
        lblStatus.setText("AI đang suy nghĩ...");
        btnSend.setEnabled(false);

        // Thu thập dữ liệu thực tế hiện tại
        String systemContext = collectSystemData();

        // Chạy đa luồng (Background Worker) để gọi API, tránh làm đơ giao diện Swing
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return GeminiAPI.sendChatRequest(systemContext, msg);
            }

            @Override
            protected void done() {
                try {
                    String aiResponse = get();
                    appendMessage("AI Trợ Lý", aiResponse, false);
                } catch (Exception e) {
                    appendMessage("Hệ thống", "Lỗi: " + e.getMessage(), false);
                    e.printStackTrace();
                }
                lblStatus.setText("Sẵn sàng");
                btnSend.setEnabled(true);
                txtMessage.requestFocus();
            }
        };
        worker.execute();
    }

    /**
     * Tự động thu thập dữ liệu từ phần mềm để "nhồi" vào não AI.
     */
    private String collectSystemData() {
        StringBuilder context = new StringBuilder();
        context.append("System Role: You are a helpful, professional AI assistant for HDK Management software (Xây dựng & Vật liệu). ");
        context.append("You MUST reply in Vietnamese nicely and professionally. DO NOT use markdown format (**, *, #) in your reply because it will be displayed in HTML. ");
        context.append("Below is the CURRENT real-time data of the company:\n\n");

        try {
            // Lấy doanh thu hôm nay
            HoaDonDAO hDao = new HoaDonDAO();
            Date today = new Date(System.currentTimeMillis());
            double revenueToday = hDao.getRevenueByDate(today);
            context.append("- Doanh thu hôm nay: ").append(ValidateUtil.formatCurrencyVND(revenueToday)).append("\n");

            // Tồn kho thấp
            SanPhamController sCtrl = new SanPhamController();
            List<SanPham> sanPhams = sCtrl.getAllSanPham();
            int lowStockCount = 0;
            StringBuilder lowStockStr = new StringBuilder();
            for (SanPham s : sanPhams) {
                if (s.getTonKho() < 10) {
                    lowStockCount++;
                    lowStockStr.append(s.getTenSP()).append(" (Còn ").append(s.getTonKho()).append("), ");
                }
            }
            context.append("- Tổng số mặt hàng sắp hết (tồn kho < 10): ").append(lowStockCount).append("\n");
            if (lowStockCount > 0) {
                context.append("- Cụ thể các mặt hàng sắp hết: ").append(lowStockStr).append("\n");
            }

            // Khách nợ nhiều nhất
            KhachHangController kCtrl = new KhachHangController();
            List<KhachHang> khs = kCtrl.getAllKhachHang();
            double totalDebt = 0;
            String topDebtCustomer = "Không có";
            double topDebt = 0;
            for (KhachHang k : khs) {
                totalDebt += k.getCongNo();
                if (k.getCongNo() > topDebt) {
                    topDebt = k.getCongNo();
                    topDebtCustomer = k.getHoTen();
                }
            }
            context.append("- Tổng công nợ khách hàng đang nợ công ty: ").append(ValidateUtil.formatCurrencyVND(totalDebt)).append("\n");
            if (topDebt > 0) {
                context.append("- Khách hàng nợ nhiều nhất là '").append(topDebtCustomer).append("' với số tiền: ").append(ValidateUtil.formatCurrencyVND(topDebt)).append("\n");
            }
        } catch (Exception ex) {
            context.append("(Lỗi khi truy xuất dữ liệu database)\n");
        }

        context.append("\nHãy dùng những dữ liệu trên để trả lời câu hỏi của người dùng một cách tự nhiên nhất.");
        return context.toString();
    }
}
