package view;

import controller.AuthController;
import java.awt.*;
import javax.swing.*;
import view.util.UiStyles;

/**
 * Halaman Login + Register yang lebih modern.
 */
public class LoginView extends JFrame {

    private final AuthController authController;

    // Login fields
    private JTextField   txtLoginUsername;
    private JPasswordField txtLoginPassword;

    // Register fields
    private JTextField   txtRegUsername;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegConfirm;
    private JTextField   txtRegFullName;

    private JTabbedPane tabs;

    public LoginView() {
        this.authController = new AuthController();
        this.authController.setLoginView(this);
        UiStyles.applyGlobalDefaults();
        buildUI();
    }

    private void buildUI() {
        setTitle("Perpustakaan Digital Gacor404");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(460, 650);  
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UiStyles.BG);
        setLayout(new BorderLayout());

        // ---- Header ----
        JPanel header = new JPanel();
        header.setBackground(UiStyles.PRIMARY);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Perpustakaan Digital Gacor404");
        title.setFont(UiStyles.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistem Manajemen Perpustakaan");
        sub.setFont(UiStyles.FONT_SUBTITLE);
        sub.setForeground(new Color(186, 211, 252));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(6));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // ---- Tabs ----
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("  Masuk  ", buildLoginPanel());
        tabs.addTab("  Daftar  ", buildRegisterPanel());
        tabs.setBackground(UiStyles.CARD_BG);
        add(tabs, BorderLayout.CENTER);

        // ---- Footer hint ----
        JLabel hint = new JLabel(
                "<html><center>Demo: <b>admin</b>/admin123 &nbsp;|&nbsp; <b>ridho</b>/member123</center></html>",
                SwingConstants.CENTER);
        hint.setFont(UiStyles.FONT_SMALL);
        hint.setForeground(UiStyles.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 0, 10, 0));
        add(hint, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(null);
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiStyles.CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtLoginUsername = UiStyles.styledField(0);
        txtLoginPassword = UiStyles.styledPasswordField(0);

        gbc.gridy = 0; panel.add(label("Username"), gbc);
        gbc.gridy = 1; panel.add(txtLoginUsername, gbc);
        gbc.gridy = 2; panel.add(label("Password"), gbc);
        gbc.gridy = 3; panel.add(txtLoginPassword, gbc);

        JButton btnLogin = UiStyles.primaryButton("Masuk");
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> doLogin());
        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 4, 0);
        panel.add(btnLogin, gbc);

        txtLoginPassword.addActionListener(e -> doLogin());
        txtLoginUsername.addActionListener(e -> txtLoginPassword.requestFocus());

        return panel;
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiStyles.CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 36, 20, 36));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtRegFullName  = UiStyles.styledField(0);
        txtRegUsername  = UiStyles.styledField(0);
        txtRegPassword  = UiStyles.styledPasswordField(0);
        txtRegConfirm   = UiStyles.styledPasswordField(0);

        gbc.gridy = 0; panel.add(label("Nama Lengkap"), gbc);
        gbc.gridy = 1; panel.add(txtRegFullName, gbc);
        gbc.gridy = 2; panel.add(label("Username"), gbc);
        gbc.gridy = 3; panel.add(txtRegUsername, gbc);
        gbc.gridy = 4; panel.add(label("Password (min 6 karakter)"), gbc);
        gbc.gridy = 5; panel.add(txtRegPassword, gbc);
        gbc.gridy = 6; panel.add(label("Konfirmasi Password"), gbc);
        gbc.gridy = 7; panel.add(txtRegConfirm, gbc);

        JButton btnReg = UiStyles.successButton("Daftar Sekarang");
        btnReg.setPreferredSize(new Dimension(0, 42));
        btnReg.addActionListener(e -> doRegister());
        gbc.gridy = 8;
        gbc.insets = new Insets(14, 0, 4, 0);
        panel.add(btnReg, gbc);

        return panel;
    }

    private void doLogin() {
        authController.login(
                txtLoginUsername.getText(),
                new String(txtLoginPassword.getPassword()));
    }

    private void doRegister() {
        authController.register(
                txtRegUsername.getText(),
                new String(txtRegPassword.getPassword()),
                new String(txtRegConfirm.getPassword()),
                txtRegFullName.getText());
    }

    /** Kembali ke tab login setelah register berhasil. */
    public void showLoginPanel() {
        tabs.setSelectedIndex(0);
        // Clear register fields
        txtRegFullName.setText("");
        txtRegUsername.setText("");
        txtRegPassword.setText("");
        txtRegConfirm.setText("");
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UiStyles.FONT_LABEL);
        lbl.setForeground(UiStyles.TEXT_MAIN);
        return lbl;
    }
}
