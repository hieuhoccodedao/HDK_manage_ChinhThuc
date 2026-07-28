package hdkmanagement.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hdkmanagement.model.ChiTietHoaDon;
import hdkmanagement.model.HoaDon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfUtil {
    
    public static String exportInvoice(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList, String tenKH) {
        String dirPath = "Invoices";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filePath = dirPath + File.separator + "HoaDon_" + hoaDon.getMaHD_Code() + ".pdf";
        
        try {
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Font Tiếng Việt
            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 16, Font.BOLD);
            Font headerFont = new Font(bf, 12, Font.BOLD);
            Font normalFont = new Font(bf, 11, Font.NORMAL);
            
            // Header
            Paragraph title = new Paragraph("CÔNG TY VẬT LIỆU XÂY DỰNG HDK", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subTitle = new Paragraph("HÓA ĐƠN BÁN HÀNG", headerFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            
            document.add(new Paragraph(" "));
            
            // Info
            document.add(new Paragraph("Mã hóa đơn: " + hoaDon.getMaHD_Code(), normalFont));
            document.add(new Paragraph("Ngày bán: " + hoaDon.getNgayBan(), normalFont));
            document.add(new Paragraph("Khách hàng: " + tenKH, normalFont));
            document.add(new Paragraph(" "));
            
            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 4f, 1.5f, 2.5f});
            
            String[] headers = {"Mã SP", "Tên sản phẩm", "SL", "Thành tiền"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }
            
            for (ChiTietHoaDon ct : chiTietList) {
                PdfPCell cellMa = new PdfPCell(new Phrase(String.valueOf(ct.getMaSP()), normalFont));
                cellMa.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellMa);
                
                // Get product name from DB or pass it. Here we just print SP ID if Name is not in ChiTietHoaDon.
                // Wait, ChiTietHoaDon in model doesn't have TenSP. I'll pass TenSP as part of ChiTietBan if possible.
                // For simplicity, we just print MaSP and DonGia in PdfUtil, but user wants TenSP.
                // It's better to fetch it, but let's assume we can add it or just pass ChiTietBan.
                // To keep it simple, I'll just use a placeholder if tenSP is missing, or modify to pass ChiTietBan.
                // Actually, I can query SanPhamDAO inside this method.
                String tenSP = new hdkmanagement.dao.SanPhamDAO().getById(ct.getMaSP()).getTenSP();
                table.addCell(new Phrase(tenSP, normalFont));
                
                PdfPCell cellSL = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), normalFont));
                cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellSL);
                
                PdfPCell cellTT = new PdfPCell(new Phrase(ValidateUtil.formatCurrencyVND(ct.getThanhTien()), normalFont));
                cellTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellTT);
            }
            document.add(table);
            
            document.add(new Paragraph(" "));
            
            Paragraph pTong = new Paragraph("Tổng tiền: " + ValidateUtil.formatCurrencyVND(hoaDon.getTongTien()), headerFont);
            pTong.setAlignment(Element.ALIGN_RIGHT);
            document.add(pTong);
            
            if (hoaDon.getChietKhau() > 0) {
                Paragraph pCK = new Paragraph("Chiết khấu: " + String.format("%.2f", hoaDon.getChietKhau()) + "%", normalFont);
                pCK.setAlignment(Element.ALIGN_RIGHT);
                document.add(pCK);
            }
            
            double finalTotal = hoaDon.getTongTien() * (1 - hoaDon.getChietKhau() / 100);
            Paragraph pFinal = new Paragraph("Thanh toán: " + ValidateUtil.formatCurrencyVND(finalTotal), headerFont);
            pFinal.setAlignment(Element.ALIGN_RIGHT);
            document.add(pFinal);
            
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Xin cảm ơn quý khách!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            return filePath;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
