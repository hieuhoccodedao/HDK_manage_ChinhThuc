// view/sale/frmBanHang.java
package hdkmanagement.view.sale;

import hdkmanagement.controller.SanPhamController;
import hdkmanagement.controller.KhachHangController;
import hdkmanagement.controller.HoaDonController;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.KhachHang;
import hdkmanagement.util.MessageUtil;
import hdkmanagement.util.DateUtil;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;
import hdkmanagement.dao.KhuyenMaiDAO;
import hdkmanagement.model.KhuyenMai;
import hdkmanagement.util.PdfUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class frmBanHang {
    
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel tablePanel;
    private JPanel buttonPanel;
    private JPanel searchPanel;
    
    // Form components
    private JTextField txtMaHD;
    private JTextField txtTenKhachHang;
    private JTextField txtSdtKhachHang;
    private JTextField txtNgayBan;
    private JTextField txtTimKiemSP;
    private JButton btnTimKiemSP;
    private JTextArea txtGhiChu;
    
    // Product selection
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JButton btnAddProduct;
    private JButton btnRemoveProduct;
    private JLabel lblTonKho;
    
    // Table
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JLabel lblTongTien;
    private JTextField txtChietKhau;
    private JTextField txtThanhToan;
    private JLabel lblConNo;
    private JLabel lblKhuyenMai;  // Hiển thị tên chương trình KM đang áp dụng
    
    // Buttons
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnInHoaDon;
    
    private SanPhamController sanPhamController;
    private KhachHangController khachHangController;
    private HoaDonController hoaDonController;
    private KhuyenMaiDAO khuyenMaiDAO;
    private List<ChiTietBan> chiTietList;
    private List<KhuyenMai> activePromotions;
    private KhuyenMai appliedPromotion = null;
    private SanPham selectedSanPham = null;
    
    private final Color SECONDARY_COLOR = UITheme.PRIMARY;
    private final Color ACCENT_COLOR = UITheme.SUCCESS;
    private final Color DANGER_COLOR = UITheme.DANGER;
    private final Color BG_COLOR = UITheme.BG;
    
    private boolean isUpdatingPayment = false;
    
    public frmBanHang() {
        sanPhamController = new SanPhamController();
        khachHangController = new KhachHangController();
        hoaDonController = new HoaDonController();
        khuyenMaiDAO = new KhuyenMaiDAO();
        chiTietList = new ArrayList<>();
        initComponents();
        loadData();
        loadPromotions();
    }
    
    private void loadPromotions() {
        activePromotions = khuyenMaiDAO.getActiveKhuyenMai();
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ===== Search Panel =====
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Tìm kiếm sản phẩm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        txtTimKiemSP = new JTextField(25);
        txtTimKiemSP.setFont(UITheme.font(Font.PLAIN, 14));
        txtTimKiemSP.setPreferredSize(new Dimension(250, 35));
        
        btnTimKiemSP = UITheme.button("Tìm kiếm", SECONDARY_COLOR, SECONDARY_COLOR.darker(), Color.WHITE);
        
        searchPanel.add(new JLabel("Tên/Mã SP:"));
        searchPanel.add(txtTimKiemSP);
        searchPanel.add(btnTimKiemSP);
        
        // ===== Form Panel =====
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin hóa đơn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Mã hóa đơn
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã hóa đơn:"), gbc);
        txtMaHD = new JTextField(15);
        txtMaHD.setFont(UITheme.font(Font.PLAIN, 13));
        txtMaHD.setPreferredSize(new Dimension(200, 30));
        txtMaHD.setEditable(false);
        txtMaHD.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtMaHD, gbc);
        
        // Khách hàng (Tên)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên KH:"), gbc);
        txtTenKhachHang = new JTextField(15);
        txtTenKhachHang.setFont(UITheme.font(Font.PLAIN, 13));
        txtTenKhachHang.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtTenKhachHang, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số ĐT:"), gbc);
        txtSdtKhachHang = new JTextField(15);
        txtSdtKhachHang.setFont(UITheme.font(Font.PLAIN, 13));
        txtSdtKhachHang.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtSdtKhachHang, gbc);
        
        // Ngày bán
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày bán:"), gbc);
        txtNgayBan = new JTextField(15);
        txtNgayBan.setFont(UITheme.font(Font.PLAIN, 13));
        txtNgayBan.setPreferredSize(new Dimension(200, 30));
        txtNgayBan.setText(DateUtil.getCurrentDateString());
        gbc.gridx = 1;
        formPanel.add(txtNgayBan, gbc);
        
        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        txtGhiChu = new JTextArea(2, 15);
        txtGhiChu.setFont(UITheme.font(Font.PLAIN, 13));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane ghiChuScroll = new JScrollPane(txtGhiChu);
        ghiChuScroll.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 1;
        formPanel.add(ghiChuScroll, gbc);
        
        // ===== Product Selection Panel =====
        JPanel productPanel = new JPanel(new GridBagLayout());
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Chọn sản phẩm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));

        
        GridBagConstraints pgc = new GridBagConstraints();
        pgc.insets = new Insets(5, 10, 5, 10);
        pgc.fill = GridBagConstraints.HORIZONTAL;
        
        // Sản phẩm
        pgc.gridx = 0; pgc.gridy = 0;
        productPanel.add(new JLabel("Sản phẩm:"), pgc);
        cboSanPham = new JComboBox<>();
        cboSanPham.setFont(UITheme.font(Font.PLAIN, 13));
        cboSanPham.setPreferredSize(new Dimension(200, 30));
        pgc.gridx = 1;
        productPanel.add(cboSanPham, pgc);
        
        // Tồn kho
        pgc.gridx = 2; pgc.gridy = 0;
        productPanel.add(new JLabel("Tồn kho:"), pgc);
        lblTonKho = new JLabel("0");
        lblTonKho.setFont(UITheme.font(Font.BOLD, 14));
        lblTonKho.setForeground(SECONDARY_COLOR);
        pgc.gridx = 3;
        productPanel.add(lblTonKho, pgc);
        
        // Số lượng
        pgc.gridx = 0; pgc.gridy = 1;
        productPanel.add(new JLabel("Số lượng:"), pgc);
        txtSoLuong = new JTextField(10);
        txtSoLuong.setFont(UITheme.font(Font.PLAIN, 13));
        txtSoLuong.setPreferredSize(new Dimension(100, 30));
        pgc.gridx = 1;
        productPanel.add(txtSoLuong, pgc);
        
        // Đơn giá
        pgc.gridx = 2; pgc.gridy = 1;
        productPanel.add(new JLabel("Đơn giá:"), pgc);
        txtDonGia = new JTextField(10);
        txtDonGia.setFont(UITheme.font(Font.PLAIN, 13));
        txtDonGia.setPreferredSize(new Dimension(150, 30));
        pgc.gridx = 3;
        productPanel.add(txtDonGia, pgc);
        
        // Buttons
        btnAddProduct = UITheme.button(" Thêm", SECONDARY_COLOR, SECONDARY_COLOR.darker(), Color.WHITE);
        pgc.gridx = 4; pgc.gridy = 1;
        productPanel.add(btnAddProduct, pgc);
        
        btnRemoveProduct = UITheme.button("️ Xóa", DANGER_COLOR, DANGER_COLOR.darker(), Color.WHITE);
        pgc.gridx = 5; pgc.gridy = 1;
        productPanel.add(btnRemoveProduct, pgc);
        
        // ===== Table Panel =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Chi tiết hóa đơn",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        // tablePanel.setPreferredSize(new Dimension(0, 200)); // Bỏ setPreferredSize để bảng tự động co giãn
        
        String[] columns = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChiTiet = new JTable(tableModel);
        UITheme.styleTable(tblChiTiet);
        
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        scrollPane.setBorder(null);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== Payment Panel =====
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints pgc2 = new GridBagConstraints();
        pgc2.insets = new Insets(5, 15, 5, 15);
        pgc2.fill = GridBagConstraints.HORIZONTAL;
        
        pgc2.gridx = 0; pgc2.gridy = 0;
        paymentPanel.add(new JLabel("Tổng tiền:"), pgc2);
        lblTongTien = new JLabel("0 ₫");
        lblTongTien.setFont(UITheme.font(Font.BOLD, 16));
        lblTongTien.setForeground(new Color(231, 76, 60));
        pgc2.gridx = 1;
        paymentPanel.add(lblTongTien, pgc2);
        
        pgc2.gridx = 2; pgc2.gridy = 0;
        paymentPanel.add(new JLabel("Chiết khấu (%):"), pgc2);
        txtChietKhau = new JTextField(8);
        txtChietKhau.setFont(UITheme.font(Font.PLAIN, 13));
        txtChietKhau.setPreferredSize(new Dimension(80, 30));
        txtChietKhau.setText("0");
        pgc2.gridx = 3;
        paymentPanel.add(txtChietKhau, pgc2);
        
        pgc2.gridx = 4; pgc2.gridy = 0;
        paymentPanel.add(new JLabel("Thanh toán:"), pgc2);
        txtThanhToan = new JTextField(10);
        txtThanhToan.setFont(UITheme.font(Font.PLAIN, 13));
        txtThanhToan.setPreferredSize(new Dimension(120, 30));
        pgc2.gridx = 5;
        paymentPanel.add(txtThanhToan, pgc2);
        
        pgc2.gridx = 6; pgc2.gridy = 0;
        paymentPanel.add(new JLabel("Công nợ:"), pgc2);
        lblConNo = new JLabel("0 ₫");
        lblConNo.setFont(UITheme.font(Font.BOLD, 14));
        lblConNo.setForeground(DANGER_COLOR);
        pgc2.gridx = 7;
        paymentPanel.add(lblConNo, pgc2);

        // Nhãn khuyến mãi đang áp dụng
        pgc2.gridx = 0; pgc2.gridy = 1; pgc2.gridwidth = 8; pgc2.insets = new Insets(2, 15, 5, 15);
        lblKhuyenMai = new JLabel(" ");
        lblKhuyenMai.setFont(UITheme.font(Font.BOLD, 12));
        lblKhuyenMai.setForeground(UITheme.SUCCESS);
        paymentPanel.add(lblKhuyenMai, pgc2);
        
        tablePanel.add(paymentPanel, BorderLayout.SOUTH);
        
        // ===== Button Panel =====
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSave = createButton(" Lưu hóa đơn", ACCENT_COLOR);
        btnClear = createButton(" Nhập lại", new Color(149, 165, 166));
        btnInHoaDon = createButton("️ In hóa đơn", SECONDARY_COLOR);
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnInHoaDon);
        
        // ===== Add to mainPanel =====
        JPanel topBoxPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topBoxPanel.setBackground(BG_COLOR);
        
        JPanel rightTopPanel = new JPanel(new BorderLayout(5, 5));
        rightTopPanel.setBackground(BG_COLOR);
        rightTopPanel.add(searchPanel, BorderLayout.NORTH);
        rightTopPanel.add(productPanel, BorderLayout.CENTER);
        
        topBoxPanel.add(formPanel);
        topBoxPanel.add(rightTopPanel);
        
        mainPanel.add(topBoxPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addEvents();
        generateMaHD();
        loadSanPham();
    }
    
    private JButton createButton(String text, Color bg) {
        // Dùng UITheme.button() thay vì setBackground() thường,
        // vì Windows Look&Feel bỏ qua màu nền JButton mặc định (nút bị 'trắng/trong suốt').
        return UITheme.button(text, bg, bg.darker(), Color.WHITE);
    }
    
    private void generateMaHD() {
        String code = "HD" + System.currentTimeMillis() % 1000000;
        txtMaHD.setText(code);
    }
    
    public void loadData() {
    // Load danh sách hóa đơn bán hàng
    loadHoaDonList();
}

private void loadHoaDonList() {
    // Code load dữ liệu của bạn ở đây
}
    
    private void loadKhachHang() {
        // Không dùng ComboBox nữa
    }
    
    private void loadSanPham() {
        List<SanPham> list = sanPhamController.getAllSanPham();
        cboSanPham.removeAllItems();
        if (list != null && !list.isEmpty()) {
            for (SanPham sp : list) {
                if (sp.isTrangThai() && sp.getTonKho() > 0) {
                    cboSanPham.addItem(sp);
                }
            }
        }
        
        // Cập nhật tồn kho khi chọn sản phẩm
        cboSanPham.addActionListener(e -> {
            SanPham sp = (SanPham) cboSanPham.getSelectedItem();
            if (sp != null) {
                lblTonKho.setText(String.valueOf(sp.getTonKho()));
                txtDonGia.setText(String.valueOf(sp.getGiaBan()));
                selectedSanPham = sp;
            }
        });
    }
    
    private void addEvents() {
        btnTimKiemSP.addActionListener(e -> searchProduct());
        btnAddProduct.addActionListener(e -> addChiTiet());
        btnRemoveProduct.addActionListener(e -> removeChiTiet());
        btnSave.addActionListener(e -> saveHoaDon());
        btnClear.addActionListener(e -> clearForm());
        btnInHoaDon.addActionListener(e -> printHoaDon());
        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePayment(); }
            public void removeUpdate(DocumentEvent e) { updatePayment(); }
            public void changedUpdate(DocumentEvent e) { updatePayment(); }
        };
        txtChietKhau.getDocument().addDocumentListener(dl);
        txtThanhToan.getDocument().addDocumentListener(dl);
        
        tblChiTiet.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Select row
            }
        });
    }
    
    private void searchProduct() {
        String keyword = txtTimKiemSP.getText().trim();
        if (keyword.isEmpty()) {
            loadSanPham();
            cboSanPham.setPopupVisible(true);
            return;
        }
        
        List<SanPham> list = sanPhamController.searchSanPham(keyword);
        cboSanPham.removeAllItems();
        
        boolean hasStock = false;
        if (list != null && !list.isEmpty()) {
            for (SanPham sp : list) {
                if (sp.isTrangThai()) {
                    if (sp.getTonKho() > 0) {
                        cboSanPham.addItem(sp);
                        hasStock = true;
                    }
                }
            }
        }
        
        if (!hasStock) {
            if (list != null && !list.isEmpty()) {
                MessageUtil.showWarning("Sản phẩm này đã HẾT HÀNG trong kho!\nHệ thống sẽ hiển thị các sản phẩm gợi ý bên dưới.");
            } else {
                MessageUtil.showWarning("Không tìm thấy sản phẩm nào phù hợp!\nHệ thống sẽ hiển thị các sản phẩm gợi ý bên dưới.");
            }
            // Tự động load lại toàn bộ danh sách sản phẩm còn hàng làm gợi ý
            loadSanPham();
            SwingUtilities.invokeLater(() -> cboSanPham.setPopupVisible(true));
        } else {
            SwingUtilities.invokeLater(() -> cboSanPham.setPopupVisible(true));
        }
    }
    
    private void addChiTiet() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        if (sp == null) {
            MessageUtil.showWarning("Vui lòng chọn sản phẩm!");
            return;
        }
        
        int soLuong;
        double donGia;
        
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) {
                MessageUtil.showWarning("Số lượng phải lớn hơn 0!");
                return;
            }
            if (soLuong > sp.getTonKho()) {
                MessageUtil.showWarning("Số lượng vượt quá tồn kho! (Tồn: " + sp.getTonKho() + ")");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtil.showWarning("Số lượng không hợp lệ!");
            return;
        }
        
        try {
            donGia = Double.parseDouble(txtDonGia.getText().trim());
            if (donGia <= 0) {
                MessageUtil.showWarning("Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtil.showWarning("Đơn giá không hợp lệ!");
            return;
        }
        
        // Kiểm tra trùng sản phẩm
        for (ChiTietBan ct : chiTietList) {
            if (ct.getMaSP() == sp.getMaSP()) {
                MessageUtil.showWarning("Sản phẩm đã có trong danh sách!");
                return;
            }
        }
        
        ChiTietBan ct = new ChiTietBan();
        ct.setMaSP(sp.getMaSP());
        ct.setTenSP(sp.getTenSP());
        ct.setSoLuong(soLuong);
        ct.setDonGia(donGia);
        ct.setThanhTien(soLuong * donGia);
        
        chiTietList.add(ct);
        updateTable();
        updatePayment();
        
        txtSoLuong.setText("");
        cboSanPham.requestFocus();
    }
    
    private void removeChiTiet() {
        int row = tblChiTiet.getSelectedRow();
        if (row == -1) {
            MessageUtil.showWarning("Vui lòng chọn sản phẩm cần xóa!");
            return;
        }
        
        if (MessageUtil.showConfirm("Bạn có chắc muốn xóa sản phẩm này?")) {
            chiTietList.remove(row);
            updateTable();
            updatePayment();
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        for (ChiTietBan ct : chiTietList) {
            Object[] row = {
                ct.getMaSP(),
                ct.getTenSP(),
                ct.getSoLuong(),
                ValidateUtil.formatCurrencyVND(ct.getDonGia()),
                ValidateUtil.formatCurrencyVND(ct.getThanhTien())
            };
            tableModel.addRow(row);
        }
    }
    
    private void updatePayment() {
        if (isUpdatingPayment) return;
        isUpdatingPayment = true;
        
        try {
            double total = 0;
            for (ChiTietBan ct : chiTietList) {
                total += ct.getThanhTien();
            }

            // Reload danh sách KM đang active từ DB (đảm bảo đồng bộ mới nhất)
            activePromotions = khuyenMaiDAO.getActiveKhuyenMai();

            // Tự động chọn KM có mức giảm cao nhất thỏa điều kiện
            appliedPromotion = null;
            if (activePromotions != null) {
                for (KhuyenMai km : activePromotions) {
                    if (total >= km.getDieuKien()) {
                        if (appliedPromotion == null || km.getMucGiam() > appliedPromotion.getMucGiam()) {
                            appliedPromotion = km;
                        }
                    }
                }
            }

            double chietKhau = 0;
            if (appliedPromotion != null) {
                chietKhau = appliedPromotion.getMucGiam();
            } else {
                try {
                    chietKhau = Double.parseDouble(txtChietKhau.getText().trim());
                    if (chietKhau > 100) chietKhau = 100;
                    if (chietKhau < 0)   chietKhau = 0;
                } catch (NumberFormatException e) { chietKhau = 0; }
            }
            
            final double finalChietKhau = chietKhau;
            final double tongSauCK = total * (1 - finalChietKhau / 100);

            double thanhToan = 0;
            try {
                thanhToan = Double.parseDouble(txtThanhToan.getText().trim());
            } catch (NumberFormatException e) { thanhToan = 0; }

            final double conNo = tongSauCK - thanhToan;
            final double finalTotal = total;

            SwingUtilities.invokeLater(() -> {
                try {
                    isUpdatingPayment = true;
                    if (appliedPromotion != null) {
                        txtChietKhau.setText(String.valueOf(finalChietKhau));
                        txtChietKhau.setEditable(false);
                        txtChietKhau.setBackground(new Color(220, 252, 231));
                        lblKhuyenMai.setText(" Áp dụng: " + appliedPromotion.getTenKM()
                            + "  (-" + String.format("%.0f", finalChietKhau) + "%)");
                        lblKhuyenMai.setForeground(UITheme.SUCCESS);
                    } else {
                        txtChietKhau.setEditable(true);
                        txtChietKhau.setBackground(Color.WHITE);
                        lblKhuyenMai.setText(finalTotal > 0 ? "Không có khuyến mãi áp dụng cho hóa đơn này" : " ");
                        lblKhuyenMai.setForeground(UITheme.TEXT_MUTED);
                    }
                    lblTongTien.setText(ValidateUtil.formatCurrencyVND(tongSauCK));
                    lblConNo.setText(ValidateUtil.formatCurrencyVND(conNo));
                    lblConNo.setForeground(conNo > 0 ? UITheme.DANGER : UITheme.SUCCESS);
                } finally {
                    isUpdatingPayment = false;
                }
            });
        } finally {
            isUpdatingPayment = false;
        }
    }
    
    private void saveHoaDon() {
        if (chiTietList.isEmpty()) {
            MessageUtil.showWarning("Vui lòng thêm sản phẩm vào hóa đơn!");
            return;
        }
        
        String tenKH = txtTenKhachHang.getText().trim();
        String sdtKH = txtSdtKhachHang.getText().trim();
        
        if (!MessageUtil.showConfirm("Bạn có chắc muốn lưu hóa đơn này?")) {
            return;
        }
        
        double total = 0;
        for (ChiTietBan ct : chiTietList) {
            total += ct.getThanhTien();
        }
        double chietKhau = 0;
        try { chietKhau = Double.parseDouble(txtChietKhau.getText().trim()); } catch (Exception e) {}
        double tongSauCK = total * (1 - chietKhau / 100);
        double thanhToan = 0;
        try { thanhToan = Double.parseDouble(txtThanhToan.getText().trim()); } catch (Exception e) {}
        double conNo = tongSauCK - thanhToan;

        hdkmanagement.model.HoaDon hd = new hdkmanagement.model.HoaDon();
        hd.setMaHD_Code(txtMaHD.getText().trim());
        hd.setNgayBan(new java.sql.Date(System.currentTimeMillis()));
        hd.setTongTien(total);
        hd.setChietKhau(chietKhau);
        hd.setDaThanhToan(thanhToan);
        hd.setConNo(conNo);
        hd.setHinhThucThanhToan("Tiền mặt");
        hd.setGhiChu(txtGhiChu.getText().trim());
        if (SessionManager.getInstance().isLoggedIn()) {
            hd.setNhanVienBan(Math.max(1, SessionManager.getInstance().getCurrentEmployeeId()));
        } else {
            hd.setNhanVienBan(1);
        }
        
        if (appliedPromotion != null) {
            hd.setMaKM(appliedPromotion.getMaKM());
        }

        List<hdkmanagement.model.ChiTietHoaDon> ctList = new ArrayList<>();
        for (ChiTietBan ct : chiTietList) {
            hdkmanagement.model.ChiTietHoaDon cthd = new hdkmanagement.model.ChiTietHoaDon();
            cthd.setMaSP(ct.getMaSP());
            cthd.setSoLuong(ct.getSoLuong());
            cthd.setDonGia(ct.getDonGia());
            cthd.setThanhTien(ct.getThanhTien());
            ctList.add(cthd);
        }

        if (hoaDonController.saveHoaDon(hd, ctList, tenKH, sdtKH)) {
            // Auto export PDF
            String pdfPath = PdfUtil.exportInvoice(hd, ctList, tenKH);
            if (pdfPath != null) {
                if (MessageUtil.showConfirm("Lưu thành công! Bạn có muốn mở Hóa đơn PDF vừa tạo không?")) {
                    try {
                        Desktop.getDesktop().open(new File(pdfPath));
                    } catch (Exception ex) {
                        MessageUtil.showWarning("Không thể mở file PDF tự động: " + ex.getMessage());
                    }
                }
            }
            clearForm();
            generateMaHD();
        }
    }
    
    private void clearForm() {
        txtTenKhachHang.setText("");
        txtSdtKhachHang.setText("");
        txtNgayBan.setText(DateUtil.getCurrentDateString());
        txtGhiChu.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtChietKhau.setText("0");
        txtThanhToan.setText("");
        chiTietList.clear();
        updateTable();
        updatePayment();
        loadSanPham();
    }
    
    private void printHoaDon() {
        if (chiTietList.isEmpty()) {
            MessageUtil.showWarning("Không có dữ liệu để in!");
            return;
        }
        MessageUtil.showInfo("Vui lòng Bấm [Lưu hóa đơn] để hệ thống tự động xuất PDF!");
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    // Inner class
    private class ChiTietBan {
        private int maSP;
        private String tenSP;
        private int soLuong;
        private double donGia;
        private double thanhTien;
        
        public int getMaSP() { return maSP; }
        public void setMaSP(int maSP) { this.maSP = maSP; }
        public String getTenSP() { return tenSP; }
        public void setTenSP(String tenSP) { this.tenSP = tenSP; }
        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        public double getDonGia() { return donGia; }
        public void setDonGia(double donGia) { this.donGia = donGia; }
        public double getThanhTien() { return thanhTien; }
        public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
    }
}