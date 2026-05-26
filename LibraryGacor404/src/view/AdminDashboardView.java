package view;

import controller.AdminController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import view.util.UiStyles;

public class AdminDashboardView extends JFrame {

    private final AdminController controller;

    // Book tab
    private DefaultTableModel bookModel;
    private JTable tblBooks;
    private JTextField txtBookId, txtTitle, txtAuthor, txtPublisher, txtStock, txtStatus;

    // Member tab
    private DefaultTableModel memberModel;
    private JTable tblMembers;
    private JTextField txtMemberUserId, txtMemberId, txtUsername, txtFullName, txtFine;
    private JPasswordField txtPassword;

    // Transaction tab
    private DefaultTableModel txModel;
    private JTable tblTx;

    public AdminDashboardView(AdminController controller) {
        this.controller = controller;
        UiStyles.applyGlobalDefaults();
        buildUI();
    }

    private void buildUI() {
        Admin admin = controller.getAdmin();
        setTitle("Admin Dashboard - " + admin.getFullName() + " | Perpustakaan Gacor404");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiStyles.BG);

        // ---- Top bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UiStyles.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("📚 Perpustakaan Digital Gacor404 — Dashboard Admin");
        title.setFont(UiStyles.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel info = new JLabel("Halo, " + admin.getFullName() + " · Administrator");
        info.setFont(UiStyles.FONT_SUBTITLE);
        info.setForeground(new Color(186, 211, 252));

        JButton btnLogout = new JButton("Exit");
        btnLogout.setBackground(new Color(0, 0, 128));
        btnLogout.setForeground(Color.BLUE);
        btnLogout.setFont(UiStyles.FONT_LABEL);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1, true));
        btnLogout.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title); left.add(Box.createVerticalStrut(4)); left.add(info);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ---- Tabs ----
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("📖 Manajemen Buku", buildBookTab());
        tabs.addTab("👥 Manajemen Anggota", buildMemberTab());
        tabs.addTab("🔄 Log Transaksi", buildTxTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ===================== BOOK TAB =====================
    private JPanel buildBookTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(UiStyles.BG);

        bookModel = new DefaultTableModel(
                new String[]{"ID", "Judul", "Penulis", "Penerbit", "Stok", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooks = new JTable(bookModel);
        UiStyles.styleTable(tblBooks);
        tblBooks.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillBookForm();
        });

        panel.add(UiStyles.sectionHeader("Daftar Buku"), BorderLayout.NORTH);
        panel.add(UiStyles.scrollPane(tblBooks), BorderLayout.CENTER);
        panel.add(buildBookForm(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildBookForm() {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(UiStyles.CARD_BG);
        card.setBorder(UiStyles.cardBorder());

        txtBookId    = UiStyles.styledField(5); txtBookId.setEditable(false);
        txtTitle     = UiStyles.styledField(20);
        txtAuthor    = UiStyles.styledField(16);
        txtPublisher = UiStyles.styledField(16);
        txtStock     = UiStyles.styledField(5);
        txtStatus    = UiStyles.styledField(12); txtStatus.setText("AVAILABLE");

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        fields.setBackground(UiStyles.CARD_BG);
        fields.add(labeledField("ID", txtBookId));
        fields.add(labeledField("Judul *", txtTitle));
        fields.add(labeledField("Penulis *", txtAuthor));
        fields.add(labeledField("Penerbit", txtPublisher));
        fields.add(labeledField("Stok *", txtStock));
        fields.add(labeledField("Status", txtStatus));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setBackground(UiStyles.CARD_BG);
        JButton btnAdd    = UiStyles.successButton("+ Tambah");
        JButton btnUpdate = UiStyles.primaryButton("✎ Update");
        JButton btnDelete = UiStyles.dangerButton("✕ Hapus");
        JButton btnClear  = UiStyles.outlineButton("Bersihkan");

        btnAdd.addActionListener(e    -> saveBook(false));
        btnUpdate.addActionListener(e -> saveBook(true));
        btnDelete.addActionListener(e -> {
            String id = txtBookId.getText().trim();
            if (id.isEmpty()) { warn("Pilih buku dari tabel."); return; }
            controller.deleteBook(Integer.parseInt(id));
            clearBookForm();
        });
        btnClear.addActionListener(e -> clearBookForm());
        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete); btns.add(btnClear);

        card.add(fields, BorderLayout.CENTER);
        card.add(btns, BorderLayout.SOUTH);
        return card;
    }

    // ===================== MEMBER TAB =====================
    private JPanel buildMemberTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(UiStyles.BG);

        memberModel = new DefaultTableModel(
                new String[]{"User ID", "Member ID", "Username", "Nama Lengkap", "Denda (Rp)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMembers = new JTable(memberModel);
        UiStyles.styleTable(tblMembers);
        tblMembers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillMemberForm();
        });

        panel.add(UiStyles.sectionHeader("Daftar Anggota"), BorderLayout.NORTH);
        panel.add(UiStyles.scrollPane(tblMembers), BorderLayout.CENTER);
        panel.add(buildMemberForm(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMemberForm() {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(UiStyles.CARD_BG);
        card.setBorder(UiStyles.cardBorder());

        txtMemberUserId = UiStyles.styledField(5); txtMemberUserId.setEditable(false);
        txtMemberId     = UiStyles.styledField(10);
        txtUsername     = UiStyles.styledField(14);
        txtPassword     = UiStyles.styledPasswordField(14);
        txtFullName     = UiStyles.styledField(18);
        txtFine         = UiStyles.styledField(10); txtFine.setText("0");

        // Auto-generate next member ID when add is clicked
        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        fields.setBackground(UiStyles.CARD_BG);
        fields.add(labeledField("User ID", txtMemberUserId));
        fields.add(labeledField("Member ID *", txtMemberId));
        fields.add(labeledField("Username *", txtUsername));
        fields.add(labeledField("Password *", txtPassword));
        fields.add(labeledField("Nama Lengkap *", txtFullName));
        fields.add(labeledField("Denda (Rp)", txtFine));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setBackground(UiStyles.CARD_BG);
        JButton btnAdd    = UiStyles.successButton("+ Tambah");
        JButton btnUpdate = UiStyles.primaryButton("✎ Update");
        JButton btnDelete = UiStyles.dangerButton("✕ Hapus");
        JButton btnClear  = UiStyles.outlineButton("Bersihkan");

        btnAdd.addActionListener(e -> {
            // Auto-fill member ID jika kosong
            if (txtMemberId.getText().trim().isEmpty())
                txtMemberId.setText(controller.generateNextMemberId());
            saveMember(false);
        });
        btnUpdate.addActionListener(e -> saveMember(true));
        btnDelete.addActionListener(e -> {
            String id = txtMemberUserId.getText().trim();
            if (id.isEmpty()) { warn("Pilih anggota dari tabel."); return; }
            controller.deleteMember(Integer.parseInt(id));
            clearMemberForm();
        });
        btnClear.addActionListener(e -> clearMemberForm());

        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete); btns.add(btnClear);

        card.add(fields, BorderLayout.CENTER);
        card.add(btns, BorderLayout.SOUTH);
        return card;
    }

    // ===================== TRANSACTION TAB =====================
    private JPanel buildTxTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(UiStyles.BG);

        txModel = new DefaultTableModel(
                new String[]{"ID", "Anggota", "Buku", "Tgl Pinjam", "Tgl Kembali", "Status", "Denda"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTx = new JTable(txModel);
        UiStyles.styleTable(tblTx);

        panel.add(UiStyles.sectionHeader("Log Transaksi Peminjaman"), BorderLayout.NORTH);
        panel.add(UiStyles.scrollPane(tblTx), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btns.setBackground(UiStyles.BG);
        JButton btnReturn = UiStyles.primaryButton("🔄 Proses Pengembalian");
        JButton btnRefresh = UiStyles.outlineButton("↺ Refresh");
        JButton btnTxt    = UiStyles.outlineButton("Export TXT");
        JButton btnCsv    = UiStyles.outlineButton("Export CSV");
        JButton btnPdf    = UiStyles.outlineButton("Export PDF");

        btnReturn.addActionListener(e -> processReturn());
        btnRefresh.addActionListener(e -> controller.refreshAll());
        btnTxt.addActionListener(e -> controller.exportReport("TXT"));
        btnCsv.addActionListener(e -> controller.exportReport("CSV"));
        btnPdf.addActionListener(e -> controller.exportReport("PDF"));

        btns.add(btnReturn); btns.add(btnRefresh);
        btns.add(new JSeparator(SwingConstants.VERTICAL));
        btns.add(btnTxt); btns.add(btnCsv); btns.add(btnPdf);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    // ===================== DATA LOAD =====================
    public void loadBooks(List<Book> books) {
        bookModel.setRowCount(0);
        for (Book b : books)
            bookModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(),
                    b.getPublisher(), b.getStock(), b.getStatus()});
    }

    public void loadMembers(List<Member> members) {
        memberModel.setRowCount(0);
        for (Member m : members)
            memberModel.addRow(new Object[]{m.getId(), m.getMemberId(), m.getUsername(),
                    m.getFullName(), String.format("%.0f", m.getFineAmount())});
    }

    public void loadTransactions(List<Transaction> txs) {
        txModel.setRowCount(0);
        for (Transaction t : txs)
            txModel.addRow(new Object[]{t.getTransactionId(), t.getMemberName(),
                    t.getBookTitle(), t.getBorrowDate(),
                    t.getReturnDate() != null ? t.getReturnDate() : "-",
                    t.getStatus(), String.format("Rp %.0f", t.getFine())});
    }

    // ===================== HELPERS =====================
    private void processReturn() {
        int row = tblTx.getSelectedRow();
        if (row < 0) { warn("Pilih transaksi dari tabel."); return; }
        int txId = (int) txModel.getValueAt(row, 0);
        String status = String.valueOf(txModel.getValueAt(row, 5));
        if ("RETURNED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Transaksi sudah dikembalikan.", "Info",
                    JOptionPane.INFORMATION_MESSAGE); return;
        }
        String fine = JOptionPane.showInputDialog(this,
                "Masukkan denda (Rp) — isi 0 jika tidak ada:", "0");
        if (fine != null) controller.returnTransaction(txId, fine);
    }

    private void saveBook(boolean isUpdate) {
        try {
            Book book = new Book();
            if (isUpdate && !txtBookId.getText().trim().isEmpty())
                book.setId(Integer.parseInt(txtBookId.getText().trim()));
            book.setTitle(txtTitle.getText());
            book.setAuthor(txtAuthor.getText());
            book.setPublisher(txtPublisher.getText());
            book.setStock(Integer.parseInt(txtStock.getText().trim()));
            book.setStatus(txtStatus.getText().trim().isEmpty() ? "AVAILABLE" : txtStatus.getText());
            controller.saveBook(book, isUpdate);
            if (!isUpdate) clearBookForm();
        } catch (NumberFormatException ex) { warn("Stok harus berupa angka."); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); }
    }

    private void saveMember(boolean isUpdate) {
        try {
            Member m = new Member();
            if (isUpdate && !txtMemberUserId.getText().trim().isEmpty())
                m.setId(Integer.parseInt(txtMemberUserId.getText().trim()));
            m.setMemberId(txtMemberId.getText());
            m.setUsername(txtUsername.getText());
            m.setFullName(txtFullName.getText());
            m.setFineAmount(txtFine.getText().trim().isEmpty() ? 0
                    : Double.parseDouble(txtFine.getText().trim()));
            String pw = new String(txtPassword.getPassword());
            if (!isUpdate && pw.isEmpty()) { warn("Password wajib diisi untuk anggota baru."); return; }
            if (pw.isEmpty()) pw = "member123"; // default if update without changing password
            controller.saveMember(m, pw, isUpdate);
            if (!isUpdate) clearMemberForm();
        } catch (NumberFormatException ex) { warn("Denda harus berupa angka."); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); }
    }

    private void fillBookForm() {
        int row = tblBooks.getSelectedRow(); if (row < 0) return;
        txtBookId.setText(String.valueOf(bookModel.getValueAt(row, 0)));
        txtTitle.setText(String.valueOf(bookModel.getValueAt(row, 1)));
        txtAuthor.setText(String.valueOf(bookModel.getValueAt(row, 2)));
        txtPublisher.setText(String.valueOf(bookModel.getValueAt(row, 3)));
        txtStock.setText(String.valueOf(bookModel.getValueAt(row, 4)));
        txtStatus.setText(String.valueOf(bookModel.getValueAt(row, 5)));
    }

    private void fillMemberForm() {
        int row = tblMembers.getSelectedRow(); if (row < 0) return;
        txtMemberUserId.setText(String.valueOf(memberModel.getValueAt(row, 0)));
        txtMemberId.setText(String.valueOf(memberModel.getValueAt(row, 1)));
        txtUsername.setText(String.valueOf(memberModel.getValueAt(row, 2)));
        txtFullName.setText(String.valueOf(memberModel.getValueAt(row, 3)));
        txtFine.setText(String.valueOf(memberModel.getValueAt(row, 4)));
        txtPassword.setText("");
    }

    private void clearBookForm() {
        txtBookId.setText(""); txtTitle.setText(""); txtAuthor.setText("");
        txtPublisher.setText(""); txtStock.setText(""); txtStatus.setText("AVAILABLE");
        tblBooks.clearSelection();
    }

    private void clearMemberForm() {
        txtMemberUserId.setText(""); txtMemberId.setText(""); txtUsername.setText("");
        txtPassword.setText(""); txtFullName.setText(""); txtFine.setText("0");
        tblMembers.clearSelection();
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UiStyles.CARD_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(UiStyles.TEXT_MUTED);
        p.add(lbl);
        p.add(Box.createVerticalStrut(3));
        p.add(field);
        return p;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
