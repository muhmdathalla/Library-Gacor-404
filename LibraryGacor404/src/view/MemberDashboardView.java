package view;

import controller.MemberController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import view.util.UiStyles;

public class MemberDashboardView extends JFrame {

    private final MemberController controller;
    private DefaultTableModel catalogModel;
    private DefaultTableModel historyModel;
    private JTable tblCatalog;
    private JTextField txtSearch;
    private JLabel lblMemberInfo;
    private JLabel lblFine;

    public MemberDashboardView(MemberController controller) {
        this.controller = controller;
        UiStyles.applyGlobalDefaults();
        buildUI();
    }

    private void buildUI() {
        Member member = controller.getMember();
        setTitle("Dashboard Anggota - " + member.getFullName() + " | Perpustakaan Gacor404");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiStyles.BG);

        // ---- Top bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UiStyles.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel leftInfo = new JPanel();
        leftInfo.setOpaque(false);
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));

        lblMemberInfo = new JLabel();
        lblMemberInfo.setFont(UiStyles.FONT_TITLE);
        lblMemberInfo.setForeground(Color.WHITE);

        lblFine = new JLabel();
        lblFine.setFont(UiStyles.FONT_SUBTITLE);
        lblFine.setForeground(new Color(186, 211, 252));

        leftInfo.add(lblMemberInfo);
        leftInfo.add(Box.createVerticalStrut(4));
        leftInfo.add(lblFine);
        updateMemberInfo(member);

        JButton btnLogout = new JButton("Exit");
        btnLogout.setBackground(new Color(0, 0, 128));
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setForeground(Color.BLUE);
        btnLogout.setFont(UiStyles.FONT_LABEL);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        btnLogout.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });

        topBar.add(leftInfo, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ---- Tabs ----
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("📖 Katalog Buku", buildCatalogTab());
        tabs.addTab("📋 Riwayat Peminjaman", buildHistoryTab());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) controller.refreshHistory();
        });
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) controller.refreshHistory();
        });
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(UiStyles.BG);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setBackground(UiStyles.BG);
        JLabel lblSearch = new JLabel("🔍 Cari Buku:");
        lblSearch.setFont(UiStyles.FONT_LABEL);
        txtSearch = UiStyles.styledField(28);
        txtSearch.setToolTipText("Ketik judul, penulis, atau penerbit...");
        txtSearch.addActionListener(e -> controller.refreshCatalog(txtSearch.getText()));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            private void refresh() { controller.refreshCatalog(txtSearch.getText()); }
        });
        JButton btnRefresh = UiStyles.outlineButton("↺ Refresh");
        btnRefresh.addActionListener(e -> controller.refreshCatalog(txtSearch.getText()));
        searchBar.add(lblSearch); searchBar.add(txtSearch); searchBar.add(btnRefresh);

        catalogModel = new DefaultTableModel(
                new String[]{"ID", "Judul", "Penulis", "Penerbit", "Stok", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCatalog = new JTable(catalogModel);
        UiStyles.styleTable(tblCatalog);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(UiStyles.BG);
        JButton btnBorrow = UiStyles.primaryButton("📥 Pinjam Buku Terpilih");
        btnBorrow.addActionListener(e -> {
            int row = tblCatalog.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "Pilih buku dari tabel terlebih dahulu.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE); return;
            }
            int bookId = (int) catalogModel.getValueAt(row, 0);
            controller.borrowBook(bookId);
        });
        actions.add(btnBorrow);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(UiStyles.scrollPane(tblCatalog), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(UiStyles.BG);

        historyModel = new DefaultTableModel(
                new String[]{"ID", "Buku", "Tgl Pinjam", "Tgl Kembali", "Status", "Denda"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblHistory = new JTable(historyModel);
        UiStyles.styleTable(tblHistory);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(UiStyles.BG);
        JButton btnRefresh = UiStyles.outlineButton("↺ Refresh Riwayat");
        btnRefresh.addActionListener(e -> controller.refreshHistory());
        actions.add(btnRefresh);

        panel.add(UiStyles.sectionHeader("Riwayat Peminjaman Saya"), BorderLayout.NORTH);
        panel.add(UiStyles.scrollPane(tblHistory), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    public void loadBooks(List<Book> books) {
        catalogModel.setRowCount(0);
        for (Book b : books)
            catalogModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(),
                    b.getPublisher(), b.getStock(), b.getStatus()});
    }

    public void loadHistory(List<Transaction> txs) {
        historyModel.setRowCount(0);
        for (Transaction t : txs)
            historyModel.addRow(new Object[]{t.getTransactionId(), t.getBookTitle(),
                    t.getBorrowDate(),
                    t.getReturnDate() != null ? t.getReturnDate() : "-",
                    t.getStatus(), String.format("Rp %.0f", t.getFine())});
    }

    public void updateMemberInfo(Member member) {
        lblMemberInfo.setText("👤 " + member.getFullName()
                + "   |   ID: " + member.getMemberId());
        double fine = member.getFineAmount();
        lblFine.setText("Anggota Perpustakaan  |  Denda: Rp " + String.format("%.0f", fine));
        if (fine > 0) lblFine.setForeground(new Color(252, 165, 165));
        else lblFine.setForeground(new Color(186, 211, 252));
    }

    public void triggerHistoryRefresh() {
        controller.refreshHistory();
    }

    public String getCurrentSearchKeyword() {
        return txtSearch.getText();
    }
}
