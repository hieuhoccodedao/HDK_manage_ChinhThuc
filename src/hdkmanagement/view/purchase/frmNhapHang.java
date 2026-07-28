// view/purchase/frmNhapHang.java
package hdkmanagement.view.purchase;

import hdkmanagement.controller.SanPhamController;
import hdkmanagement.controller.NhaCungCapController;
import hdkmanagement.controller.PhieuNhapController;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.NhaCungCap;
import hdkmanagement.util.MessageUtil;
import hdkmanagement.util.DateUtil;
import hdkmanagement.util.SessionManager;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class frmNhapHang {
    
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel tablePanel;
    private JPanel buttonPanel;
    
    // Form components
    private JTextField txtMaPN;
    private JComboBox<Object> cboNhaCungCap;
    private JTextField txtSdtNhaCungCap;
    private JTextField txtNgayNhap;
    private JTextArea txtGhiChu;
    
    // Product components
    private JComboBox<Object> cboSanPham;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JButton btnAddProduct;
    private JButton btnRemoveProduct;
    
    // Table
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JLabel lblTongTien;
    private JTextField txtChietKhau;
    private JTextField txtThanhToan;
    private JLabel lblConNo;
    
    // Buttons
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnRefresh;
    
    private SanPhamController sanPhamController;
    private NhaCungCapController nhaCungCapController;
    private PhieuNhapController phieuNhapController;
    private List<ChiTietNhap> chiTietList;
    
    private final Color SECONDARY_COLOR = UITheme.PRIMARY;
    private final Color ACCENT_COLOR = UITheme.SUCCESS;
    private final Color DANGER_COLOR = UITheme.DANGER;
    private final Color BG_COLOR = UITheme.BG;
    
    public frmNhapHang() {
        sanPhamController = new SanPhamController();
        nhaCungCapController = new NhaCungCapController();
        phieuNhapController = new PhieuNhapController();
        chiTietList = new ArrayList<>();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ===== Form Panel =====
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin phiếu nhập",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Mã phiếu nhập
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã phiếu nhập:"), gbc);
        txtMaPN = new JTextField(15);
        txtMaPN.setFont(UITheme.font(Font.PLAIN, 13));
        txtMaPN.setPreferredSize(new Dimension(200, 30));
        txtMaPN.setEditable(false);
        txtMaPN.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtMaPN, gbc);
        
        // Nhà cung cấp
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên NCC:"), gbc);
        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setEditable(true);
        cboNhaCungCap.setFont(UITheme.font(Font.PLAIN, 13));
        cboNhaCungCap.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(cboNhaCungCap, gbc);
        
        // SDT NCC
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số ĐT:"), gbc);
        txtSdtNhaCungCap = new JTextField(15);
        txtSdtNhaCungCap.setFont(UITheme.font(Font.PLAIN, 13));
        txtSdtNhaCungCap.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtSdtNhaCungCap, gbc);
        
        // Ngày nhập
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày nhập:"), gbc);
        txtNgayNhap = new JTextField(15);
        txtNgayNhap.setFont(UITheme.font(Font.PLAIN, 13));
        txtNgayNhap.setPreferredSize(new Dimension(200, 30));
        txtNgayNhap.setText(DateUtil.getCurrentDateString());
        gbc.gridx = 1;
        formPanel.add(txtNgayNhap, gbc);
        
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
        productPanel.setPreferredSize(new Dimension(0, 100));
        
        GridBagConstraints pgc = new GridBagConstraints();
        pgc.insets = new Insets(5, 10, 5, 10);
        pgc.fill = GridBagConstraints.HORIZONTAL;
        
        // Sản phẩm
        pgc.gridx = 0; pgc.gridy = 0;
        productPanel.add(new JLabel("Sản phẩm:"), pgc);
        cboSanPham = new JComboBox<>();
        cboSanPham.setEditable(true);
        cboSanPham.setFont(UITheme.font(Font.PLAIN, 13));
        cboSanPham.setPreferredSize(new Dimension(200, 30));
        pgc.gridx = 1;
        productPanel.add(cboSanPham, pgc);
        
        // Số lượng
        pgc.gridx = 2; pgc.gridy = 0;
        productPanel.add(new JLabel("Số lượng:"), pgc);
        txtSoLuong = new JTextField(10);
        txtSoLuong.setFont(UITheme.font(Font.PLAIN, 13));
        txtSoLuong.setPreferredSize(new Dimension(100, 30));
        pgc.gridx = 3;
        productPanel.add(txtSoLuong, pgc);
        
        // Đơn giá
        pgc.gridx = 4; pgc.gridy = 0;
        productPanel.add(new JLabel("Đơn giá:"), pgc);
        txtDonGia = new JTextField(10);
        txtDonGia.setFont(UITheme.font(Font.PLAIN, 13));
        txtDonGia.setPreferredSize(new Dimension(100, 30));
        pgc.gridx = 5;
        productPanel.add(txtDonGia, pgc);
        
        // Buttons
        btnAddProduct = UITheme.button(" Thêm", SECONDARY_COLOR, SECONDARY_COLOR.darker(), Color.WHITE);
        pgc.gridx = 6; pgc.gridy = 0;
        productPanel.add(btnAddProduct, pgc);
        
        btnRemoveProduct = UITheme.button("️ Xóa", DANGER_COLOR, DANGER_COLOR.darker(), Color.WHITE);
        pgc.gridx = 7; pgc.gridy = 0;
        productPanel.add(btnRemoveProduct, pgc);
        
        // ===== Table Panel =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Chi tiết phiếu nhập",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        tablePanel.setPreferredSize(new Dimension(0, 250));
        
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
        
        // ===== Total Panel =====
        JPanel totalPanel = new JPanel(new GridBagLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints pgc2 = new GridBagConstraints();
        pgc2.insets = new Insets(5, 15, 5, 15);
        pgc2.fill = GridBagConstraints.HORIZONTAL;
        
        pgc2.gridx = 0; pgc2.gridy = 0;
        totalPanel.add(new JLabel("Tổng tiền:"), pgc2);
        lblTongTien = new JLabel("0 ₫");
        lblTongTien.setFont(UITheme.font(Font.BOLD, 16));
        lblTongTien.setForeground(new Color(231, 76, 60));
        pgc2.gridx = 1;
        totalPanel.add(lblTongTien, pgc2);
        
        pgc2.gridx = 2; pgc2.gridy = 0;
        totalPanel.add(new JLabel("Chiết khấu (%):"), pgc2);
        txtChietKhau = new JTextField(8);
        txtChietKhau.setFont(UITheme.font(Font.PLAIN, 13));
        txtChietKhau.setPreferredSize(new Dimension(80, 30));
        txtChietKhau.setText("0");
        pgc2.gridx = 3;
        totalPanel.add(txtChietKhau, pgc2);
        
        pgc2.gridx = 4; pgc2.gridy = 0;
        totalPanel.add(new JLabel("Thanh toán:"), pgc2);
        txtThanhToan = new JTextField(10);
        txtThanhToan.setFont(UITheme.font(Font.PLAIN, 13));
        txtThanhToan.setPreferredSize(new Dimension(120, 30));
        pgc2.gridx = 5;
        totalPanel.add(txtThanhToan, pgc2);
        
        pgc2.gridx = 6; pgc2.gridy = 0;
        totalPanel.add(new JLabel("Công nợ:"), pgc2);
        lblConNo = new JLabel("0 ₫");
        lblConNo.setFont(UITheme.font(Font.BOLD, 14));
        lblConNo.setForeground(DANGER_COLOR);
        pgc2.gridx = 7;
        totalPanel.add(lblConNo, pgc2);
        
        tablePanel.add(totalPanel, BorderLayout.SOUTH);
        
        // ===== Button Panel =====
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSave = createButton(" Lưu phiếu nhập", ACCENT_COLOR);
        btnClear = createButton(" Nhập lại", new Color(149, 165, 166));
        btnRefresh = createButton(" Làm mới", SECONDARY_COLOR);
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        
        // ===== Add to mainPanel =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(productPanel, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addEvents();
        generateMaPN();
    }
    
    private JButton createButton(String text, Color bg) {
        // Dùng UITheme.button() thay vì setBackground() thường,
        // vì Windows Look&Feel bỏ qua màu nền JButton mặc định (nút bị 'trắng/trong suốt').
        return UITheme.button(text, bg, bg.darker(), Color.WHITE);
    }
    
    private void generateMaPN() {
        String code = "PN" + System.currentTimeMillis() % 1000000;
        txtMaPN.setText(code);
    }
    
    public void loadData() {
        loadNhaCungCap();
        loadSanPham();
        loadPhieuNhapList();
    }

    private void loadPhieuNhapList() {
        // Code load dữ liệu phiếu nhập nếu cần
    }
    
    private void loadNhaCungCap() {
        List<NhaCungCap> list = nhaCungCapController.getAllNhaCungCap();
        cboNhaCungCap.removeAllItems();
        if (list != null && !list.isEmpty()) {
            for (NhaCungCap ncc : list) {
                if (ncc.isTrangThai()) {
                    cboNhaCungCap.addItem(ncc);
                }
            }
        }
        cboNhaCungCap.setSelectedItem(null);
    }
    
    private void loadSanPham() {
        List<SanPham> list = sanPhamController.getAllSanPham();
        cboSanPham.removeAllItems();
        if (list != null && !list.isEmpty()) {
            for (SanPham sp : list) {
                if (sp.isTrangThai()) {
                    cboSanPham.addItem(sp);
                }
            }
        }
    }
    
    private void addEvents() {
        // Auto-fill Nhà cung cấp
        cboNhaCungCap.addActionListener(e -> {
            Object selected = cboNhaCungCap.getSelectedItem();
            if (selected instanceof NhaCungCap) {
                txtSdtNhaCungCap.setText(((NhaCungCap) selected).getSdt());
            }
        });

        // Auto-fill Sản phẩm
        cboSanPham.addActionListener(e -> {
            Object selected = cboSanPham.getSelectedItem();
            if (selected instanceof SanPham) {
                txtDonGia.setText(String.format("%.0f", ((SanPham) selected).getGiaNhap()));
            }
        });

        btnAddProduct.addActionListener(e -> addChiTiet());
        btnRemoveProduct.addActionListener(e -> removeChiTiet());
        btnSave.addActionListener(e -> savePhieuNhap());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadData();
            generateMaPN();
        });
        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTotal(); }
            public void removeUpdate(DocumentEvent e) { updateTotal(); }
            public void changedUpdate(DocumentEvent e) { updateTotal(); }
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
    
    private void addChiTiet() {
        Object selected = cboSanPham.getSelectedItem();
        if (selected == null || selected.toString().trim().isEmpty()) {
            MessageUtil.showWarning("Vui lòng chọn hoặc nhập tên sản phẩm!");
            return;
        }
        
        SanPham sp = null;
        if (selected instanceof SanPham) {
            sp = (SanPham) selected;
        } else {
            // Nhập tay tên SP mới, cần prompt thông tin
            String tenMoi = selected.toString().trim();
            if (MessageUtil.showConfirm("Sản phẩm '" + tenMoi + "' chưa có trong hệ thống.\nBạn có muốn thêm mới nhanh không?")) {
                String inputGiaNhap = JOptionPane.showInputDialog(mainPanel, "Nhập giá nhập cho '" + tenMoi + "':", txtDonGia.getText().trim());
                if (inputGiaNhap != null) {
                    try {
                        double gia = Double.parseDouble(inputGiaNhap.trim());
                        sp = new SanPham();
                        sp.setTenSP(tenMoi);
                        sp.setMaSP_Code("SP" + (System.currentTimeMillis() % 100000));
                        sp.setMaDM(1); // Mặc định danh mục đầu tiên
                        sp.setDonViTinh("Cái");
                        sp.setGiaNhap(gia);
                        sp.setGiaBan(gia * 1.5); // Giá bán mặc định = 1.5 giá nhập
                        
                        hdkmanagement.dao.SanPhamDAO spDao = new hdkmanagement.dao.SanPhamDAO();
                        if (spDao.insert(sp)) {
                            MessageUtil.showInfo("Đã lưu nhanh sản phẩm mới vào kho!");
                            loadSanPham();
                            cboSanPham.setSelectedItem(sp); // Select lại
                            txtDonGia.setText(String.format("%.0f", gia));
                        } else {
                            MessageUtil.showError("Thêm mới sản phẩm thất bại!");
                            return;
                        }
                    } catch (Exception ex) {
                        MessageUtil.showError("Giá nhập không hợp lệ!");
                        return;
                    }
                } else {
                    return; // Người dùng ấn Cancel
                }
            } else {
                return;
            }
        }
        
        int soLuong;
        double donGia;
        
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) {
                MessageUtil.showWarning("Số lượng phải lớn hơn 0!");
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
        for (ChiTietNhap ct : chiTietList) {
            if (ct.getMaSP() == sp.getMaSP()) {
                MessageUtil.showWarning("Sản phẩm đã có trong danh sách!");
                return;
            }
        }
        
        ChiTietNhap ct = new ChiTietNhap();
        ct.setMaSP(sp.getMaSP());
        ct.setTenSP(sp.getTenSP());
        ct.setSoLuong(soLuong);
        ct.setDonGia(donGia);
        ct.setThanhTien(soLuong * donGia);
        
        chiTietList.add(ct);
        updateTable();
        updateTotal();
        
        txtSoLuong.setText("");
        txtDonGia.setText("");
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
            updateTotal();
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        for (ChiTietNhap ct : chiTietList) {
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
    
    private void updateTotal() {
        double total = 0;
        for (ChiTietNhap ct : chiTietList) {
            total += ct.getThanhTien();
        }
        
        double chietKhau = 0;
        try {
            chietKhau = Double.parseDouble(txtChietKhau.getText().trim());
            if (chietKhau > 100) chietKhau = 100;
            if (chietKhau < 0) chietKhau = 0;
        } catch (NumberFormatException e) {
            chietKhau = 0;
        }
        
        double tongSauCK = total * (1 - chietKhau / 100);
        lblTongTien.setText(ValidateUtil.formatCurrencyVND(tongSauCK));
        
        double thanhToan = 0;
        try {
            thanhToan = Double.parseDouble(txtThanhToan.getText().trim());
        } catch (NumberFormatException e) {
            thanhToan = 0;
        }
        
        double conNo = tongSauCK - thanhToan;
        lblConNo.setText(ValidateUtil.formatCurrencyVND(conNo));
    }
    
    private void savePhieuNhap() {
        Object nccObj = cboNhaCungCap.getSelectedItem();
        String tenNCC = "";
        String sdtNCC = txtSdtNhaCungCap.getText().trim();
        NhaCungCap ncc = null;
        
        if (nccObj instanceof NhaCungCap) {
            ncc = (NhaCungCap) nccObj;
            tenNCC = ncc.getTenNCC();
        } else if (nccObj != null && !nccObj.toString().trim().isEmpty()) {
            tenNCC = nccObj.toString().trim();
            // Hỏi tạo mới NCC
            if (MessageUtil.showConfirm("Nhà cung cấp '" + tenNCC + "' chưa có trong hệ thống.\nTạo mới tự động?")) {
                ncc = new NhaCungCap();
                ncc.setTenNCC(tenNCC);
                ncc.setSdt(sdtNCC);
                ncc.setDiaChi("");
                ncc.setEmail("");
                if (new hdkmanagement.dao.NhaCungCapDAO().insert(ncc)) {
                    MessageUtil.showInfo("Tạo mới nhà cung cấp thành công!");
                }
            }
        }
        
        if (tenNCC.isEmpty()) {
            MessageUtil.showWarning("Vui lòng chọn hoặc nhập tên Nhà cung cấp!");
            return;
        }
        
        if (chiTietList.isEmpty()) {
            MessageUtil.showWarning("Vui lòng thêm sản phẩm vào phiếu nhập!");
            return;
        }
        
        if (!MessageUtil.showConfirm("Bạn có chắc muốn lưu phiếu nhập này?")) {
            return;
        }
        
        double total = 0;
        for (ChiTietNhap ct : chiTietList) {
            total += ct.getThanhTien();
        }
        
        double chietKhau = 0;
        try { chietKhau = Double.parseDouble(txtChietKhau.getText().trim()); } catch (Exception e) {}
        double tongSauCK = total * (1 - chietKhau / 100);
        double thanhToan = 0;
        try { thanhToan = Double.parseDouble(txtThanhToan.getText().trim()); } catch (Exception e) {}
        double conNo = tongSauCK - thanhToan;
        
        hdkmanagement.model.PhieuNhap pn = new hdkmanagement.model.PhieuNhap();
        pn.setMaPN_Code(txtMaPN.getText().trim());
        if (ncc != null) {
            pn.setMaNCC(ncc.getMaNCC());
        }
        pn.setNgayNhap(new java.sql.Date(System.currentTimeMillis()));
        pn.setTongTien(tongSauCK);
        pn.setDaThanhToan(thanhToan);
        pn.setConNo(conNo);
        pn.setGhiChu(txtGhiChu.getText().trim());
        
        if (SessionManager.getInstance().isLoggedIn()) {
            pn.setNguoiTao(Math.max(1, SessionManager.getInstance().getCurrentEmployeeId()));
        } else {
            pn.setNguoiTao(1); // Default
        }
        
        List<hdkmanagement.model.ChiTietPhieuNhap> ctList = new ArrayList<>();
        for (ChiTietNhap ct : chiTietList) {
            hdkmanagement.model.ChiTietPhieuNhap ctpn = new hdkmanagement.model.ChiTietPhieuNhap();
            ctpn.setMaSP(ct.getMaSP());
            ctpn.setSoLuong(ct.getSoLuong());
            ctpn.setDonGia(ct.getDonGia());
            ctpn.setThanhTien(ct.getThanhTien());
            ctList.add(ctpn);
        }
        
        if (phieuNhapController.savePhieuNhap(pn, ctList, tenNCC, sdtNCC)) {
            clearForm();
            generateMaPN();
            loadNhaCungCap(); // refresh combobox
        }
    }
    
    private void clearForm() {
        cboNhaCungCap.setSelectedItem(null);
        txtSdtNhaCungCap.setText("");
        txtNgayNhap.setText(DateUtil.getCurrentDateString());
        txtGhiChu.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtChietKhau.setText("0");
        txtThanhToan.setText("");
        chiTietList.clear();
        updateTable();
        updateTotal();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    // Inner class
    private class ChiTietNhap {
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