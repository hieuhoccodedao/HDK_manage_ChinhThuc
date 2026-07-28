// view/product/frmSanPham.java
package hdkmanagement.view.product;

import hdkmanagement.controller.SanPhamController;
import hdkmanagement.controller.DanhMucController;
import hdkmanagement.model.SanPham;
import hdkmanagement.model.DanhMuc;
import hdkmanagement.util.MessageUtil;
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
import java.util.List;

public class frmSanPham {
    
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel tablePanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    
    private JTable tblSanPham;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Form components
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JComboBox<DanhMuc> cboDanhMuc;
    private JTextField txtDonViTinh;
    private JTextField txtGiaNhap;
    private JTextField txtGiaBan;
    private JTextField txtTonKho;
    private JTextField txtTonToiThieu;
    private JTextArea txtMoTa;
    private JCheckBox chkTrangThai;
    
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    
    // Khai báo controller
    private SanPhamController sanPhamController;
    private DanhMucController danhMucController;
    private int selectedId = -1;
    
    // Màu sắc
    private final Color PRIMARY_COLOR = UITheme.PRIMARY_DARKER;
    private final Color SECONDARY_COLOR = UITheme.PRIMARY;
    private final Color ACCENT_COLOR = UITheme.SUCCESS;
    private final Color DANGER_COLOR = UITheme.DANGER;
    private final Color WARNING_COLOR = UITheme.WARNING;
    private final Color BG_COLOR = UITheme.BG;
    
    public frmSanPham() {
        // Khởi tạo controller
        sanPhamController = new SanPhamController();
        danhMucController = new DanhMucController();
        initComponents();
        loadData();
        loadDanhMuc();
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ===== TOP: Search Panel =====
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Tìm kiếm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(UITheme.font(Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(250, 35));
        
        btnSearch = createButton("Tìm kiếm", SECONDARY_COLOR, Color.WHITE);
        btnRefresh = createButton("Làm mới", new Color(149, 165, 166), Color.WHITE);
        
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        // ===== CENTER: Table Panel =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Danh sách sản phẩm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        // Tạo table
        String[] columns = {"Mã SP", "Tên sản phẩm", "Danh mục", "Đơn vị", "Giá nhập", "Giá bán", "Tồn kho", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSanPham = new JTable(tableModel);
        UITheme.styleTable(tblSanPham);
        
        // Set column widths
        tblSanPham.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblSanPham.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblSanPham.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblSanPham.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblSanPham.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblSanPham.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblSanPham.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblSanPham.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        scrollPane = new JScrollPane(tblSanPham);
        scrollPane.setBorder(null);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== RIGHT: Form Panel =====
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin sản phẩm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        formPanel.setPreferredSize(new Dimension(380, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        
        // Mã SP
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã SP:"), gbc);
        txtMaSP = new JTextField(15);
        txtMaSP.setFont(UITheme.font(Font.PLAIN, 13));
        txtMaSP.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtMaSP, gbc);
        
        // Tên SP
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        txtTenSP = new JTextField(15);
        txtTenSP.setFont(UITheme.font(Font.PLAIN, 13));
        txtTenSP.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtTenSP, gbc);
        
        // Danh mục
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        cboDanhMuc = new JComboBox<>();
        cboDanhMuc.setFont(UITheme.font(Font.PLAIN, 13));
        cboDanhMuc.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(cboDanhMuc, gbc);
        
        // Đơn vị tính
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Đơn vị tính:"), gbc);
        txtDonViTinh = new JTextField(15);
        txtDonViTinh.setFont(UITheme.font(Font.PLAIN, 13));
        txtDonViTinh.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtDonViTinh, gbc);
        
        // Giá nhập
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Giá nhập:"), gbc);
        txtGiaNhap = new JTextField(15);
        txtGiaNhap.setFont(UITheme.font(Font.PLAIN, 13));
        txtGiaNhap.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtGiaNhap, gbc);
        
        // Giá bán
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Giá bán:"), gbc);
        txtGiaBan = new JTextField(15);
        txtGiaBan.setFont(UITheme.font(Font.PLAIN, 13));
        txtGiaBan.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtGiaBan, gbc);
        
        // Tồn kho
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Tồn kho:"), gbc);
        txtTonKho = new JTextField(15);
        txtTonKho.setFont(UITheme.font(Font.PLAIN, 13));
        txtTonKho.setPreferredSize(new Dimension(200, 30));
        txtTonKho.setEditable(false);
        txtTonKho.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtTonKho, gbc);
        
        // Tồn tối thiểu
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Tồn tối thiểu:"), gbc);
        txtTonToiThieu = new JTextField(15);
        txtTonToiThieu.setFont(UITheme.font(Font.PLAIN, 13));
        txtTonToiThieu.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtTonToiThieu, gbc);
        
        // Mô tả
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        txtMoTa = new JTextArea(3, 15);
        txtMoTa.setFont(UITheme.font(Font.PLAIN, 13));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setPreferredSize(new Dimension(200, 60));
        gbc.gridx = 1;
        formPanel.add(moTaScroll, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        chkTrangThai = new JCheckBox("Hoạt động");
        chkTrangThai.setFont(UITheme.font(Font.PLAIN, 13));
        chkTrangThai.setSelected(true);
        gbc.gridx = 1;
        formPanel.add(chkTrangThai, gbc);
        
        // Buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        btnAdd = createButton("Thêm", ACCENT_COLOR, Color.WHITE);
        btnUpdate = createButton("Sửa", WARNING_COLOR, Color.WHITE);
        btnDelete = createButton("Xóa", DANGER_COLOR, Color.WHITE);
        btnClear = createButton("Nhập lại", new Color(149, 165, 166), Color.WHITE);
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(buttonPanel, gbc);
        
        // ===== Add to mainPanel =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.EAST);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add events
        addEvents();
        
        // Disable Update/Delete initially
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    private JButton createButton(String text, Color bg, Color fg) {
        // Dùng UITheme.button() thay vì setBackground() thường,
        // vì Windows Look&Feel bỏ qua màu nền JButton mặc định (nút bị "trắng/trong suốt").
        return UITheme.button(text, bg, bg.darker(), fg);
    }
    
    private void addEvents() {
        // Search
        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        txtSearch.addActionListener(e -> search());
        
        // Table click
        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblSanPham.getSelectedRow();
                if (row >= 0) {
                    selectRow(row);
                }
            }
        });
        
        // CRUD
        btnAdd.addActionListener(e -> addSanPham());
        btnUpdate.addActionListener(e -> updateSanPham());
        btnDelete.addActionListener(e -> deleteSanPham());
        btnClear.addActionListener(e -> clearForm());
    }
    
    private void loadDanhMuc() {
        List<DanhMuc> list = danhMucController.getAllDanhMuc();
        cboDanhMuc.removeAllItems();
        if (list != null && !list.isEmpty()) {
            for (DanhMuc dm : list) {
                cboDanhMuc.addItem(dm);
            }
        }
    }
    
    public void loadData() {
        List<SanPham> list = sanPhamController.getAllSanPham();
        displayData(list);
    }
    
    private void search() {
        String keyword = txtSearch.getText().trim();
        List<SanPham> list = sanPhamController.searchSanPham(keyword);
        displayData(list);
    }
    
    private void displayData(List<SanPham> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (SanPham sp : list) {
                Object[] row = {
                    sp.getMaSP_Code(),
                    sp.getTenSP(),
                    sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : "",
                    sp.getDonViTinh(),
                    ValidateUtil.formatCurrencyVND(sp.getGiaNhap()),
                    ValidateUtil.formatCurrencyVND(sp.getGiaBan()),
                    sp.getTonKho(),
                    sp.isTrangThai() ? "Hoạt động" : "Ngừng kinh doanh"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void selectRow(int row) {
        try {
            String maSP = tableModel.getValueAt(row, 0).toString();
            // Lấy danh sách và tìm sản phẩm theo mã
            List<SanPham> list = sanPhamController.getAllSanPham();
            for (SanPham sp : list) {
                if (sp.getMaSP_Code().equals(maSP)) {
                    selectedId = sp.getMaSP();
                    txtMaSP.setText(sp.getMaSP_Code());
                    txtTenSP.setText(sp.getTenSP());
                    cboDanhMuc.setSelectedItem(sp.getDanhMuc());
                    txtDonViTinh.setText(sp.getDonViTinh());
                    txtGiaNhap.setText(String.valueOf(sp.getGiaNhap()));
                    txtGiaBan.setText(String.valueOf(sp.getGiaBan()));
                    txtTonKho.setText(String.valueOf(sp.getTonKho()));
                    txtTonToiThieu.setText(String.valueOf(sp.getTonToiThieu()));
                    txtMoTa.setText(sp.getMoTa());
                    chkTrangThai.setSelected(sp.isTrangThai());
                    
                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void addSanPham() {
        try {
            SanPham sp = getFormData();
            if (sp == null) return;
            
            boolean result = sanPhamController.addSanPham(sp);
            if (result) {
                MessageUtil.showInfo("Thêm sản phẩm thành công!");
                clearForm();
                loadData();
            }
        } catch (Exception ex) {
            MessageUtil.showError("Lỗi: " + ex.getMessage());
        }
    }
    
    private void updateSanPham() {
        if (selectedId == -1) {
            MessageUtil.showWarning("Vui lòng chọn sản phẩm cần sửa!");
            return;
        }
        
        try {
            SanPham sp = getFormData();
            if (sp == null) return;
            sp.setMaSP(selectedId);
            
            boolean result = sanPhamController.updateSanPham(sp);
            if (result) {
                MessageUtil.showInfo("Cập nhật sản phẩm thành công!");
                clearForm();
                loadData();
            }
        } catch (Exception ex) {
            MessageUtil.showError("Lỗi: " + ex.getMessage());
        }
    }
    
    private void deleteSanPham() {
        if (selectedId == -1) {
            MessageUtil.showWarning("Vui lòng chọn sản phẩm cần xóa!");
            return;
        }
        
        boolean result = sanPhamController.deleteSanPham(selectedId);
        if (result) {
            MessageUtil.showInfo("Xóa sản phẩm thành công!");
            clearForm();
            loadData();
        }
    }
    
    private SanPham getFormData() {
        String maSP = txtMaSP.getText().trim();
        String tenSP = txtTenSP.getText().trim();
        DanhMuc danhMuc = (DanhMuc) cboDanhMuc.getSelectedItem();
        String donViTinh = txtDonViTinh.getText().trim();
        String giaNhapStr = txtGiaNhap.getText().trim();
        String giaBanStr = txtGiaBan.getText().trim();
        String tonToiThieuStr = txtTonToiThieu.getText().trim();
        String moTa = txtMoTa.getText().trim();
        boolean trangThai = chkTrangThai.isSelected();
        
        if (maSP.isEmpty() || tenSP.isEmpty()) {
            MessageUtil.showWarning("Vui lòng nhập mã và tên sản phẩm!");
            return null;
        }
        
        if (danhMuc == null) {
            MessageUtil.showWarning("Vui lòng chọn danh mục!");
            return null;
        }
        
        double giaNhap = 0, giaBan = 0;
        int tonToiThieu = 0;
        
        try {
            giaNhap = Double.parseDouble(giaNhapStr);
            giaBan = Double.parseDouble(giaBanStr);
            tonToiThieu = Integer.parseInt(tonToiThieuStr);
        } catch (NumberFormatException e) {
            MessageUtil.showWarning("Vui lòng nhập số hợp lệ cho giá và tồn kho!");
            return null;
        }
        
        SanPham sp = new SanPham();
        sp.setMaSP_Code(maSP);
        sp.setTenSP(tenSP);
        sp.setMaDM(danhMuc.getMaDM());
        sp.setDanhMuc(danhMuc);
        sp.setDonViTinh(donViTinh);
        sp.setGiaNhap(giaNhap);
        sp.setGiaBan(giaBan);
        sp.setTonKho(0);
        sp.setTonToiThieu(tonToiThieu);
        sp.setMoTa(moTa);
        sp.setTrangThai(trangThai);
        
        return sp;
    }
    
    private void clearForm() {
        selectedId = -1;
        txtMaSP.setText("");
        txtTenSP.setText("");
        cboDanhMuc.setSelectedIndex(0);
        txtDonViTinh.setText("");
        txtGiaNhap.setText("");
        txtGiaBan.setText("");
        txtTonKho.setText("");
        txtTonToiThieu.setText("");
        txtMoTa.setText("");
        chkTrangThai.setSelected(true);
        
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        
        txtMaSP.requestFocus();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
}