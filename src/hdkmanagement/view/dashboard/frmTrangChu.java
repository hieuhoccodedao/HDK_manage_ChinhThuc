package hdkmanagement.view.dashboard;

import hdkmanagement.util.SessionManager;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class frmTrangChu {

    private JPanel mainPanel;
    private float floatY = 0;
    private float floatAngle = 0;
    private float alpha = 0f;
    private String currentTime = "";
    private String currentDate = "";

    public frmTrangChu() {
        initComponents();
        startAnimation();
    }

    private void initComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // 1. Vẽ nền Gradient mượt mà
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        w, h, new Color(30, 58, 138)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Grid mờ trang trí nền
                g2.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < w; i += 60) g2.drawLine(i, 0, i, h);
                for (int i = 0; i < h; i += 60) g2.drawLine(0, i, w, i);

                // Cài đặt độ mờ tổng thể (Fade in)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                // 2. Vẽ thời gian (Đồng hồ)
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 80));
                FontMetrics fmTime = g2.getFontMetrics();
                int timeWidth = fmTime.stringWidth(currentTime);
                g2.drawString(currentTime, (w - timeWidth) / 2, h / 2 - 120);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                FontMetrics fmDate = g2.getFontMetrics();
                int dateWidth = fmDate.stringWidth(currentDate);
                g2.drawString(currentDate, (w - dateWidth) / 2, h / 2 - 80);

                // 3. Vẽ Logo động ở trung tâm (Lơ lửng)
                int centerX = w / 2;
                int centerY = h / 2 + 50 + (int) floatY;

                // Bóng đổ của logo
                g2.setColor(new Color(0, 0, 0, 40));
                int shadowWidth = 120 - (int) (floatY * 1.5);
                g2.fillOval(centerX - shadowWidth / 2, h / 2 + 200, shadowWidth, 20);

                // Hình đa giác xoay (tách riêng Graphics2D để không ảnh hưởng đến chữ bên dưới)
                Graphics2D g2Logo = (Graphics2D) g2.create();
                g2Logo.translate(centerX, centerY);
                g2Logo.rotate(Math.toRadians(floatAngle * 10)); // Xoay rất chậm
                
                // Vẽ LOGO HDK thay cho khối lập phương
                hdkmanagement.util.IconUtil logoIcon = new hdkmanagement.util.IconUtil(hdkmanagement.util.IconUtil.IconType.LOGO_HDK, 120, new Color(56, 189, 248, 220));
                logoIcon.paintIcon(null, g2Logo, -60, -60);

                g2Logo.dispose(); // Hủy Graphics2D tạm thời

                // 4. Lời chào
                String empName = SessionManager.getInstance().getCurrentEmployeeName();
                if (empName == null || empName.isBlank()) empName = "Quản trị viên";
                String welcome = "Xin chào, " + empName;
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                g2.setColor(Color.WHITE);
                FontMetrics fmWel = g2.getFontMetrics();
                g2.drawString(welcome, (w - fmWel.stringWidth(welcome)) / 2, centerY + 130);

                String subTitle = "CHÀO MỪNG ĐẾN VỚI HỆ THỐNG HDK ERP";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(new Color(148, 163, 184));
                FontMetrics fmSub = g2.getFontMetrics();
                g2.drawString(subTitle, (w - fmSub.stringWidth(subTitle)) / 2, centerY + 170);

                g2.dispose();
            }
        };
    }

    private void startAnimation() {
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiệu ứng Fade-in
                if (alpha < 1.0f) {
                    alpha += 0.02f;
                    if (alpha > 1.0f) alpha = 1.0f;
                }

                // Hiệu ứng Lơ lửng (Floating)
                floatAngle += 0.05f;
                floatY = (float) Math.sin(floatAngle) * 15f;

                // Cập nhật thời gian
                SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, dd 'tháng' MM, yyyy", new java.util.Locale("vi", "VN"));
                Date now = new Date();
                currentTime = timeFmt.format(now);
                currentDate = dateFmt.format(now);

                mainPanel.repaint();
            }
        });
        timer.start();
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
