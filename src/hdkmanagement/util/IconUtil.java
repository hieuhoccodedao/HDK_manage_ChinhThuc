package hdkmanagement.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class IconUtil implements Icon {

    public enum IconType {
        DASHBOARD,
        PRODUCT,
        CATEGORY,
        CUSTOMER,
        SUPPLIER,
        EMPLOYEE,
        IMPORT,
        SALE,
        REPORT,
        CHART,
        WARNING,
        MONEY,
        HOME,
        LOGO_HDK,
        EYE,
        EYE_OFF,
        SEND,
        ROBOT,
        SYSTEM_LOG
    }

    private final IconType type;
    private final int width;
    private final int height;
    private final Color color;

    public IconUtil(IconType type, int size, Color color) {
        this.type = type;
        this.width = size;
        this.height = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);

        // Đảm bảo nét vẽ sắc nét
        float strokeWidth = Math.max(1.5f, width / 12f);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);

        int padding = width / 6;
        int size = width - padding * 2;
        int px = padding;
        int py = padding;

        switch (type) {
            case DASHBOARD:
                // Grid 2x2
                int s2 = size / 2 - 1;
                g2.drawRoundRect(px, py, s2, s2, 3, 3);
                g2.drawRoundRect(px + s2 + 2, py, s2, s2, 3, 3);
                g2.drawRoundRect(px, py + s2 + 2, s2, s2, 3, 3);
                g2.drawRoundRect(px + s2 + 2, py + s2 + 2, s2, s2, 3, 3);
                break;
            case PRODUCT:
                // Box
                g2.drawRoundRect(px, py + size / 4, size, size - size / 4, 3, 3);
                Path2D box = new Path2D.Float();
                box.moveTo(px, py + size / 4);
                box.lineTo(px + size / 2, py);
                box.lineTo(px + size, py + size / 4);
                g2.draw(box);
                g2.drawLine(px + size / 2, py, px + size / 2, py + size / 4);
                break;
            case CATEGORY:
                // Folder
                Path2D folder = new Path2D.Float();
                folder.moveTo(px, py + size);
                folder.lineTo(px, py + 2);
                folder.lineTo(px + size / 3, py + 2);
                folder.lineTo(px + size / 2, py + 6);
                folder.lineTo(px + size, py + 6);
                folder.lineTo(px + size, py + size);
                folder.closePath();
                g2.draw(folder);
                break;
            case CUSTOMER:
            case EMPLOYEE:
                // User
                int headSize = size / 2;
                g2.drawOval(px + size / 4, py, headSize, headSize);
                Path2D body = new Path2D.Float();
                body.moveTo(px, py + size);
                body.quadTo(px, py + size / 2 + 2, px + size / 2, py + size / 2 + 2);
                body.quadTo(px + size, py + size / 2 + 2, px + size, py + size);
                g2.draw(body);
                break;
            case SUPPLIER:
                // Building
                g2.drawRect(px + 2, py, size - 4, size);
                g2.drawLine(px, py + size, px + size, py + size);
                g2.fillRect(px + size / 2 - 2, py + size - 6, 4, 6); // Door
                g2.drawLine(px + size / 4 + 1, py + 4, px + size / 4 + 3, py + 4);
                g2.drawLine(px + size * 3 / 4 - 3, py + 4, px + size * 3 / 4 - 1, py + 4);
                g2.drawLine(px + size / 4 + 1, py + 10, px + size / 4 + 3, py + 10);
                g2.drawLine(px + size * 3 / 4 - 3, py + 10, px + size * 3 / 4 - 1, py + 10);
                break;
            case IMPORT:
                // Arrow down into box
                g2.drawLine(px + size / 2, py, px + size / 2, py + size * 2 / 3);
                g2.drawLine(px + size / 2 - 3, py + size * 2 / 3 - 3, px + size / 2, py + size * 2 / 3);
                g2.drawLine(px + size / 2 + 3, py + size * 2 / 3 - 3, px + size / 2, py + size * 2 / 3);
                g2.drawLine(px, py + size / 2, px, py + size);
                g2.drawLine(px, py + size, px + size, py + size);
                g2.drawLine(px + size, py + size, px + size, py + size / 2);
                break;
            case SALE:
                // Shopping cart / Tag
                Path2D tag = new Path2D.Float();
                tag.moveTo(px + size, py);
                tag.lineTo(px + size / 2, py);
                tag.lineTo(px, py + size / 2);
                tag.lineTo(px + size / 2, py + size);
                tag.lineTo(px + size, py + size / 2);
                tag.closePath();
                g2.draw(tag);
                g2.drawOval(px + size * 3 / 4 - 1, py + size / 4 - 1, 2, 2);
                break;
            case REPORT:
                // Chart
                g2.drawLine(px, py, px, py + size);
                g2.drawLine(px, py + size, px + size, py + size);
                g2.drawRect(px + 3, py + size / 2, size / 4, size / 2);
                g2.drawRect(px + size / 4 + 5, py + size / 4, size / 4, size * 3 / 4);
                g2.drawRect(px + size / 2 + 7, py + 2, size / 4, size - 2);
                break;
            case CHART:
                // Line chart
                g2.drawLine(px, py + size, px + size, py + size); // x-axis
                Path2D line = new Path2D.Float();
                line.moveTo(px, py + size - 2);
                line.lineTo(px + size / 3, py + size / 2);
                line.lineTo(px + size * 2 / 3, py + size * 3 / 4);
                line.lineTo(px + size, py + 2);
                g2.draw(line);
                g2.fillOval(px + size - 2, py, 4, 4);
                break;
            case WARNING:
                // Triangle Exclamation
                Path2D warn = new Path2D.Float();
                warn.moveTo(px + size / 2, py);
                warn.lineTo(px + size, py + size);
                warn.lineTo(px, py + size);
                warn.closePath();
                g2.draw(warn);
                g2.drawLine(px + size / 2, py + size / 3, px + size / 2, py + size * 2 / 3);
                g2.fillOval(px + size / 2 - 1, py + size - 3, 2, 2);
                break;
            case MONEY:
                // Dollar / Money stack
                g2.drawRoundRect(px, py + size / 4, size, size / 2, 4, 4);
                g2.drawOval(px + size / 2 - 3, py + size / 2 - 3, 6, 6);
                g2.drawLine(px + size / 2, py + size / 4, px + size / 2, py + size * 3 / 4);
                break;
            case HOME:
                Path2D home = new Path2D.Float();
                home.moveTo(px + size / 2, py);
                home.lineTo(px + size, py + size / 2);
                home.lineTo(px + size - 2, py + size / 2);
                home.lineTo(px + size - 2, py + size);
                home.lineTo(px + size / 2 + 2, py + size);
                home.lineTo(px + size / 2 + 2, py + size / 2 + 2);
                home.lineTo(px + size / 2 - 2, py + size / 2 + 2);
                home.lineTo(px + size / 2 - 2, py + size);
                home.lineTo(px + 2, py + size);
                home.lineTo(px + 2, py + size / 2);
                home.lineTo(px, py + size / 2);
                home.closePath();
                g2.draw(home);
                break;
            case LOGO_HDK:
                // Logo HDK: Hình tượng tòa nhà và công trình xây dựng
                // 1. Tòa nhà cao tầng bên phải
                g2.drawRect(px + size / 2, py + size / 4, size / 3, size * 3 / 4);
                // Các ô cửa sổ
                g2.drawRect(px + size / 2 + 4, py + size / 4 + 4, 3, 3);
                g2.drawRect(px + size / 2 + 12, py + size / 4 + 4, 3, 3);
                g2.drawRect(px + size / 2 + 4, py + size / 2, 3, 3);
                g2.drawRect(px + size / 2 + 12, py + size / 2, 3, 3);
                
                // 2. Tòa nhà mái dốc bên trái
                Path2D roof = new Path2D.Float();
                roof.moveTo(px + 4, py + size / 2 + 8);
                roof.lineTo(px + size / 2, py + size / 4 + 4);
                roof.lineTo(px + size / 2, py + size);
                roof.lineTo(px + 4, py + size);
                roof.closePath();
                g2.draw(roof);
                // Cửa chính
                g2.drawRect(px + size / 4, py + size - 8, 8, 8);
                break;
            case EYE:
                Path2D eye = new Path2D.Float();
                eye.moveTo(px, py + size / 2);
                eye.quadTo(px + size / 2, py - size / 4, px + size, py + size / 2);
                eye.quadTo(px + size / 2, py + size + size / 4, px, py + size / 2);
                g2.draw(eye);
                g2.drawOval(px + size / 2 - size / 4, py + size / 2 - size / 4, size / 2, size / 2);
                g2.fillOval(px + size / 2 - 2, py + size / 2 - 2, 4, 4);
                break;
            case EYE_OFF:
                Path2D eyeOff = new Path2D.Float();
                eyeOff.moveTo(px, py + size / 2);
                eyeOff.quadTo(px + size / 2, py - size / 4, px + size, py + size / 2);
                eyeOff.quadTo(px + size / 2, py + size + size / 4, px, py + size / 2);
                g2.draw(eyeOff);
                g2.drawOval(px + size / 2 - size / 4, py + size / 2 - size / 4, size / 2, size / 2);
                g2.drawLine(px, py, px + size, py + size);
                break;
            case SEND:
                // Máy bay giấy
                Path2D plane = new Path2D.Float();
                plane.moveTo(px + 2, py + size - 2);
                plane.lineTo(px + size - 2, py + 2);
                plane.lineTo(px + size / 2 + 2, py + size - 2);
                plane.lineTo(px + size / 2 - 2, py + size / 2 + 2);
                plane.lineTo(px + 2, py + size / 2 - 2);
                plane.closePath();
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(plane);
                g2.drawLine(px + size / 2 - 2, py + size / 2 + 2, px + size - 2, py + 2);
                break;
            case ROBOT:
                // Robot head
                int hdW = size - 8;
                int hdH = size - 12;
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(px + 4, py + 8, hdW, hdH, 6, 6);
                // Antena
                g2.drawLine(px + size / 2, py + 8, px + size / 2, py + 2);
                g2.fillOval(px + size / 2 - 2, py, 4, 4);
                // Eyes
                g2.drawRect(px + 8, py + 14, 6, 4);
                g2.drawRect(px + size - 14, py + 14, 6, 4);
                // Mouth
                g2.drawLine(px + 10, py + size - 8, px + size - 10, py + size - 8);
                break;
            case SYSTEM_LOG:
                // Log/List icon
                g2.drawRoundRect(px, py, size, size, 4, 4);
                g2.drawLine(px + 6, py + 6, px + 10, py + 6);
                g2.drawLine(px + 14, py + 6, px + size - 6, py + 6);
                g2.drawLine(px + 6, py + size / 2, px + 10, py + size / 2);
                g2.drawLine(px + 14, py + size / 2, px + size - 6, py + size / 2);
                g2.drawLine(px + 6, py + size - 6, px + 10, py + size - 6);
                g2.drawLine(px + 14, py + size - 6, px + size - 6, py + size - 6);
                break;
        }

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
