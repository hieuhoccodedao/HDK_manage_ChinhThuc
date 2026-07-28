package hdkmanagement.view.inventory;

import hdkmanagement.dao.LichSuKhoDAO;
import hdkmanagement.model.LichSuKho;
import hdkmanagement.util.DateUtil;
import hdkmanagement.view.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class frmLichSuKho extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private LichSuKhoDAO dao;

    public frmLichSuKho() {
        dao = new LichSuKhoDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("NHẬT KÝ KHO HÀNG", SwingConstants.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 24));
        title.setForeground(UITheme.PRIMARY);
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Sản phẩm", "Loại GD", "Thay đổi", "Tồn hiện tại", "Tham chiếu", "Nhân viên", "Ngày GD"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UITheme.BG);
        JButton btnRefresh = UITheme.grayButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        List<LichSuKho> list = dao.getAll();
        for (LichSuKho l : list) {
            String thayDoi = l.getSoLuongThayDoi() > 0 ? "+" + l.getSoLuongThayDoi() : String.valueOf(l.getSoLuongThayDoi());
            model.addRow(new Object[]{
                l.getMaLSK(), l.getTenSP(), l.getLoaiGiaoDich(), thayDoi, 
                l.getTonKhoHienTai(), l.getThamChieu(), l.getTenNV(), DateUtil.formatDateTime(l.getNgayGiaoDich())
            });
        }
    }
}
