// util/ValidateUtil.java
package hdkmanagement.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidateUtil {
    
    // Kiểm tra số điện thoại
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String regex = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$";
        return Pattern.matches(regex, phone.trim());
    }
    
    // Kiểm tra email
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(regex, email.trim());
    }
    
    // Kiểm tra số nguyên
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Kiểm tra số thực
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Kiểm tra ngày tháng
    public static boolean isValidDate(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    // Định dạng tiền tệ
    public static String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }
    
    public static String formatCurrencyVND(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(amount) + " ₫";
    }
    
    // Định dạng số
    public static String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(number);
    }
    
    // Chuyển đổi String sang Double
    public static double parseDouble(String str) {
        try {
            return Double.parseDouble(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    // Chuyển đổi String sang Int
    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Rút gọn số tiền thành dạng 1.5tr / 2.5tỷ để hiển thị trên biểu đồ */
    public static String shortCurrency(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1ftỷ", amount / 1_000_000_000);
        if (amount >= 1_000_000)     return String.format("%.1ftr", amount / 1_000_000);
        if (amount >= 1_000)         return String.format("%.0fk", amount / 1_000);
        return String.format("%.0f", amount);
    }
}