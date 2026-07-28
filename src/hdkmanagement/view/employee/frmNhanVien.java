// view/employee/frmNhanVien.java
package hdkmanagement.view.employee;

import hdkmanagement.controller.NhanVienController;
import hdkmanagement.controller.TaiKhoanController;
import hdkmanagement.model.NhanVien;
import hdkmanagement.model.TaiKhoan;
import hdkmanagement.model.Quyen;
import hdkmanagement.util.MessageUtil;
import hdkmanagement.util.DateUtil;
import hdkmanagement.util.ValidateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class frmNhanVien {
    
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel tablePanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JPanel accountPanel;
    
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    
    private JTable tblNhanVien;
    private DefaultTableModel tableModel;
    
    // Form components
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JComboBox<String> cboGioiTinh;
    private JTextField txtNgaySinh;
    private JTextArea txtDiaChi;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JTextField txtChucVu;
    private JTextField txtLuongCoBan;
    private JTextField txtNgayVaoLam;
    private JTextArea txtGhiChu;
    private JCheckBox chkTrangThai;
    
    // Account components
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<Quyen> cboQuyen;
    
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnCreateAccount;
    
    private NhanVienController nhanVienController;
    private TaiKhoanController taiKhoanController;
    private int selectedId = -1;
    
    // ===== MÀU SẮC - lấy từ UITheme dùng chung để đồng bộ toàn ứng dụng =====
    private final Color SECONDARY_COLOR = UITheme.PRIMARY;
    private final Color ACCENT_COLOR = UITheme.SUCCESS;
    private final Color DANGER_COLOR = UITheme.DANGER;
    private final Color WARNING_COLOR = UITheme.WARNING;
    private final Color BG_COLOR = UITheme.BG;
    
    public frmNhanVien() {
        nhanVienController = new NhanVienController();
        taiKhoanController = new TaiKhoanController();
        initComponents();
        loadData();
        loadQuyen();
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
            "Tìm kiếm nhân viên",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(UITheme.font(Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(250, 35));
        
        btnSearch = createButton("Tìm kiếm", SECONDARY_COLOR);
        btnRefresh = createButton("Làm mới", new Color(149, 165, 166));
        
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        // ===== Table Panel =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Danh sách nhân viên",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        
        String[] columns = {"Mã NV", "Họ tên", "Giới tính", "SĐT", "Email", "Chức vụ", "Lương", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNhanVien = new JTable(tableModel);
        UITheme.styleTable(tblNhanVien);
        
        tblNhanVien.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblNhanVien.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblNhanVien.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblNhanVien.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblNhanVien.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblNhanVien.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblNhanVien.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblNhanVien.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tblNhanVien);
        scrollPane.setBorder(null);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== Form Panel =====
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin nhân viên",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.font(Font.BOLD, 13)
        ));
        formPanel.setPreferredSize(new Dimension(400, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Mã NV
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã NV:"), gbc);
        txtMaNV = new JTextField(15);
        txtMaNV.setFont(UITheme.font(Font.PLAIN, 13));
        txtMaNV.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtMaNV, gbc);
        
        // Row 1: Họ tên
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        txtHoTen = new JTextField(15);
        txtHoTen.setFont(UITheme.font(Font.PLAIN, 13));
        txtHoTen.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtHoTen, gbc);
        
        // Row 2: Giới tính + Ngày sinh
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setFont(UITheme.font(Font.PLAIN, 13));
        cboGioiTinh.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(cboGioiTinh, gbc);
        
        // Row 3: Ngày sinh
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày sinh:"), gbc);
        txtNgaySinh = new JTextField(15);
        txtNgaySinh.setFont(UITheme.font(Font.PLAIN, 13));
        txtNgaySinh.setPreferredSize(new Dimension(200, 30));
        txtNgaySinh.setToolTipText("dd/MM/yyyy");
        gbc.gridx = 1;
        formPanel.add(txtNgaySinh, gbc);
        
        // Row 4: Địa chỉ
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtDiaChi = new JTextArea(2, 15);
        txtDiaChi.setFont(UITheme.font(Font.PLAIN, 13));
        txtDiaChi.setLineWrap(true);
        txtDiaChi.setWrapStyleWord(true);
        JScrollPane diaChiScroll = new JScrollPane(txtDiaChi);
        diaChiScroll.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 1;
        formPanel.add(diaChiScroll, gbc);
        
        // Row 5: SĐT + Email
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("SĐT:"), gbc);
        txtSDT = new JTextField(15);
        txtSDT.setFont(UITheme.font(Font.PLAIN, 13));
        txtSDT.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtSDT, gbc);
        
        // Row 6: Email
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(15);
        txtEmail.setFont(UITheme.font(Font.PLAIN, 13));
        txtEmail.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        
        // Row 7: Chức vụ + Lương
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Chức vụ:"), gbc);
        txtChucVu = new JTextField(15);
        txtChucVu.setFont(UITheme.font(Font.PLAIN, 13));
        txtChucVu.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtChucVu, gbc);
        
        // Row 8: Lương
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Lương cơ bản:"), gbc);
        txtLuongCoBan = new JTextField(15);
        txtLuongCoBan.setFont(UITheme.font(Font.PLAIN, 13));
        txtLuongCoBan.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        formPanel.add(txtLuongCoBan, gbc);
        
        // Row 9: Ngày vào làm
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Ngày vào làm:"), gbc);
        txtNgayVaoLam = new JTextField(15);
        txtNgayVaoLam.setFont(UITheme.font(Font.PLAIN, 13));
        txtNgayVaoLam.setPreferredSize(new Dimension(200, 30));
        txtNgayVaoLam.setToolTipText("dd/MM/yyyy");
        gbc.gridx = 1;
        formPanel.add(txtNgayVaoLam, gbc);
        
        // Row 10: Ghi chú
        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        txtGhiChu = new JTextArea(2, 15);
        txtGhiChu.setFont(UITheme.font(Font.PLAIN, 13));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane ghiChuScroll = new JScrollPane(txtGhiChu);
        ghiChuScroll.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 1;
        formPanel.add(ghiChuScroll, gbc);
        
        // Row 11: Trạng thái
        gbc.gridx = 0; gbc.gridy = 11;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        chkTrangThai = new JCheckBox("Hoạt động");
        chkTrangThai.setFont(UITheme.font(Font.PLAIN, 13));
        chkTrangThai.setSelected(true);
        gbc.gridx = 1;
        formPanel.add(chkTrangThai, gbc);
        
        // Row 12: Buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAdd = createButton("Thêm", ACCENT_COLOR);
        btnUpdate = createButton("Sửa", WARNING_COLOR);
        btnDelete = createButton("Xóa", DANGER_COLOR);
        btnClear = createButton("Nhập lại", new Color(149, 165, 166));
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 5, 8);
        formPanel.add(buttonPanel, gbc);
        
        // Row 13: Tạo tài khoản
        btnCreateAccount = UITheme.button(" Tạo tài khoản", new Color(155, 89, 182), new Color(125, 69, 152), Color.WHITE);
        btnCreateAccount.setEnabled(false);
        
        gbc.gridx = 0; gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 8, 10, 8);
        formPanel.add(btnCreateAccount, gbc);
        
        // ===== Add to mainPanel =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.EAST);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        addEvents();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    private JButton createButton(String text, Color bg) {
        // Dùng UITheme.button() thay vì setBackground() thường,
        // vì Windows Look&Feel bỏ qua màu nền JButton mặc định (nút bị 'trắng/trong suốt').
        return UITheme.button(text, bg, bg.darker(), Color.WHITE);
    }
    
    private void loadQuyen() {
        // Chưa có QuyenController, tạm thời tạo combobox
        cboQuyen = new JComboBox<>();
        cboQuyen.addItem(new Quyen(1, "Admin", "Toàn quyền"));
        cboQuyen.addItem(new Quyen(2, "Quản lý", "Quản lý"));
        cboQuyen.addItem(new Quyen(3, "Nhân viên", "Nhân viên"));
    }
    
    private void addEvents() {
        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        txtSearch.addActionListener(e -> search());
        
        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblNhanVien.getSelectedRow();
                if (row >= 0) {
                    selectRow(row);
                }
            }
        });
        
        btnAdd.addActionListener(e -> addNhanVien());
        btnUpdate.addActionListener(e -> updateNhanVien());
        btnDelete.addActionListener(e -> deleteNhanVien());
        btnClear.addActionListener(e -> clearForm());
        btnCreateAccount.addActionListener(e -> createAccount());
    }
    
    public void loadData() {
        List<NhanVien> list = nhanVienController.getAllNhanVien();
        displayData(list);
    }
    
    private void search() {
        String keyword = txtSearch.getText().trim();
        List<NhanVien> list = nhanVienController.searchNhanVien(keyword);
        displayData(list);
    }
    
    private void displayData(List<NhanVien> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (NhanVien nv : list) {
                Object[] row = {
                    nv.getMaNV_Code(),
                    nv.getHoTen(),
                    nv.isGioiTinh() ? "Nam" : "Nữ",
                    nv.getSdt(),
                    nv.getEmail(),
                    nv.getChucVu(),
                    ValidateUtil.formatCurrencyVND(nv.getLuongCoBan()),
                    nv.isTrangThai() ? "Hoạt động" : "Ngừng"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void selectRow(int row) {
        String maNV = tableModel.getValueAt(row, 0).toString();
        List<NhanVien> list = nhanVienController.getAllNhanVien();
        for (NhanVien nv : list) {
            if (nv.getMaNV_Code().equals(maNV)) {
                selectedId = nv.getMaNV();
                txtMaNV.setText(nv.getMaNV_Code());
                txtHoTen.setText(nv.getHoTen());
                cboGioiTinh.setSelectedIndex(nv.isGioiTinh() ? 0 : 1);
                txtNgaySinh.setText(DateUtil.formatDate(nv.getNgaySinh()));
                txtDiaChi.setText(nv.getDiaChi());
                txtSDT.setText(nv.getSdt());
                txtEmail.setText(nv.getEmail());
                txtChucVu.setText(nv.getChucVu());
                txtLuongCoBan.setText(String.valueOf(nv.getLuongCoBan()));
                txtNgayVaoLam.setText(DateUtil.formatDate(nv.getNgayVaoLam()));
                txtGhiChu.setText(nv.getGhiChu());
                chkTrangThai.setSelected(nv.isTrangThai());
                
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnCreateAccount.setEnabled(true);
                break;
            }
        }
    }
    
    private void addNhanVien() {
        NhanVien nv = getFormData();
        if (nv == null) return;
        
        if (nhanVienController.addNhanVien(nv)) {
            MessageUtil.showInfo("Thêm nhân viên thành công!");
            clearForm();
            loadData();
        }
    }
    
    private void updateNhanVien() {
        if (selectedId == -1) {
            MessageUtil.showWarning("Vui lòng chọn nhân viên cần sửa!");
            return;
        }
        
        NhanVien nv = getFormData();
        if (nv == null) return;
        nv.setMaNV(selectedId);
        
        if (nhanVienController.updateNhanVien(nv)) {
            MessageUtil.showInfo("Cập nhật nhân viên thành công!");
            clearForm();
            loadData();
        }
    }
    
    private void deleteNhanVien() {
        if (selectedId == -1) {
            MessageUtil.showWarning("Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        
        if (nhanVienController.deleteNhanVien(selectedId)) {
            MessageUtil.showInfo("Xóa nhân viên thành công!");
            clearForm();
            loadData();
        }
    }
    
    private void createAccount() {
        if (selectedId == -1) {
            MessageUtil.showWarning("Vui lòng chọn nhân viên để tạo tài khoản!");
            return;
        }
        
        // Tạo dialog tạo tài khoản
        JDialog dialog = new JDialog((Frame) null, "Tạo tài khoản", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        JTextField txtUsername = new JTextField(20);
        txtUsername.setFont(UITheme.font(Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        
        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        JPasswordField txtPassword = new JPasswordField(20);
        txtPassword.setFont(UITheme.font(Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Xác nhận mật khẩu
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Xác nhận:"), gbc);
        JPasswordField txtConfirm = new JPasswordField(20);
        txtConfirm.setFont(UITheme.font(Font.PLAIN, 13));
        txtConfirm.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(txtConfirm, gbc);
        
        // Quyền
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Quyền:"), gbc);
        JComboBox<Quyen> cboQuyen = new JComboBox<>();
        cboQuyen.addItem(new Quyen(1, "Admin", ""));
        cboQuyen.addItem(new Quyen(2, "Quản lý", ""));
        cboQuyen.addItem(new Quyen(3, "Nhân viên", ""));
        cboQuyen.setFont(UITheme.font(Font.PLAIN, 13));
        cboQuyen.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panel.add(cboQuyen, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnSave = new JButton("Tạo tài khoản");
        btnSave.setBackground(ACCENT_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());
            Quyen quyen = (Quyen) cboQuyen.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty()) {
                MessageUtil.showWarning("Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            
            if (!password.equals(confirm)) {
                MessageUtil.showWarning("Mật khẩu xác nhận không khớp!");
                return;
            }
            
            if (password.length() < 6) {
                MessageUtil.showWarning("Mật khẩu phải có ít nhất 6 ký tự!");
                return;
            }
            
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhau(password);
            tk.setMaNV(selectedId);
            tk.setMaQuyen(quyen.getMaQuyen());
            
            if (taiKhoanController.addTaiKhoan(tk)) {
                MessageUtil.showInfo("Tạo tài khoản thành công!");
                dialog.dispose();
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private NhanVien getFormData() {
        String maNV = txtMaNV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        boolean gioiTinh = cboGioiTinh.getSelectedIndex() == 0;
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String chucVu = txtChucVu.getText().trim();
        String luongStr = txtLuongCoBan.getText().trim();
        String ngayVaoLamStr = txtNgayVaoLam.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();
        boolean trangThai = chkTrangThai.isSelected();
        
        if (maNV.isEmpty() || hoTen.isEmpty()) {
            MessageUtil.showWarning("Vui lòng nhập mã và họ tên!");
            return null;
        }
        
        java.sql.Date ngaySinh = null;
        java.sql.Date ngayVaoLam = null;
        
        if (!ngaySinhStr.isEmpty()) {
            ngaySinh = DateUtil.parseSQLDate(ngaySinhStr);
            if (ngaySinh == null) {
                MessageUtil.showWarning("Ngày sinh không hợp lệ! (dd/MM/yyyy)");
                return null;
            }
        }
        
        if (!ngayVaoLamStr.isEmpty()) {
            ngayVaoLam = DateUtil.parseSQLDate(ngayVaoLamStr);
            if (ngayVaoLam == null) {
                MessageUtil.showWarning("Ngày vào làm không hợp lệ! (dd/MM/yyyy)");
                return null;
            }
        }
        
        double luong = 0;
        if (!luongStr.isEmpty()) {
            try {
                luong = Double.parseDouble(luongStr);
            } catch (NumberFormatException e) {
                MessageUtil.showWarning("Lương không hợp lệ!");
                return null;
            }
        }
        
        NhanVien nv = new NhanVien();
        nv.setMaNV_Code(maNV);
        nv.setHoTen(hoTen);
        nv.setGioiTinh(gioiTinh);
        nv.setNgaySinh(ngaySinh);
        nv.setDiaChi(diaChi);
        nv.setSdt(sdt);
        nv.setEmail(email);
        nv.setChucVu(chucVu);
        nv.setLuongCoBan(luong);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setGhiChu(ghiChu);
        nv.setTrangThai(trangThai);
        
        return nv;
    }
    
    private void clearForm() {
        selectedId = -1;
        txtMaNV.setText("");
        txtHoTen.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtChucVu.setText("");
        txtLuongCoBan.setText("");
        txtNgayVaoLam.setText("");
        txtGhiChu.setText("");
        chkTrangThai.setSelected(true);
        
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCreateAccount.setEnabled(false);
        
        txtMaNV.requestFocus();
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
}