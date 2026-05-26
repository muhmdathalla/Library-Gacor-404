package controller;

import config.DatabaseConnection;
import javax.swing.JOptionPane;
import model.*;
import repository.*;
import view.*;

public class AuthController {

    private final UserRepository userRepository;
    private LoginView loginView;

    public AuthController() {
        this.userRepository = new UserRepositoryImpl();
    }

    public void setLoginView(LoginView v) { this.loginView = v; }

    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView,
                    "Username dan password wajib diisi.",
                    "Validasi Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!DatabaseConnection.getInstance().testConnection()) {
            JOptionPane.showMessageDialog(loginView,
                    "Tidak dapat terhubung ke database MySQL.\nPastikan MySQL berjalan.",
                    "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userRepository.authenticate(username.trim(), password);
            if (user == null) {
                JOptionPane.showMessageDialog(loginView,
                        "Username atau password salah.",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (loginView != null) loginView.dispose();
            routeByRole(user);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(loginView,
                    "Terjadi kesalahan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registrasi member baru oleh diri sendiri (bukan admin).
     */
    public void register(String username, String password, String confirmPass, String fullName) {
        if (username.trim().isEmpty() || password.isEmpty() || fullName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(loginView,
                    "Semua field wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(loginView,
                    "Password dan konfirmasi password tidak sama.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(loginView,
                    "Password minimal 6 karakter.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userRepository.usernameExists(username.trim(), 0)) {
            JOptionPane.showMessageDialog(loginView,
                    "Username sudah digunakan, pilih username lain.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String memberId = userRepository.generateNextMemberId();
        Member member = new Member();
        member.setUsername(username.trim());
        member.setPassword(password);
        member.setFullName(fullName.trim());
        member.setMemberId(memberId);
        member.setFineAmount(0);

        if (userRepository.insertMember(member, password)) {
            JOptionPane.showMessageDialog(loginView,
                    "Registrasi berhasil!\nMember ID Anda: " + memberId
                    + "\nSilakan login dengan akun baru.",
                    "Registrasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
            // Kembalikan ke login view (tidak auto-login untuk keamanan)
            if (loginView != null) loginView.showLoginPanel();
        } else {
            JOptionPane.showMessageDialog(loginView,
                    "Registrasi gagal. Coba lagi.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void routeByRole(User user) {
        if (user instanceof Admin admin) {
            AdminController ctrl = new AdminController(admin);
            AdminDashboardView dash = new AdminDashboardView(ctrl);
            ctrl.setView(dash);
            dash.setVisible(true);
        } else if (user instanceof Member member) {
            MemberController ctrl = new MemberController(member);
            MemberDashboardView dash = new MemberDashboardView(ctrl);
            ctrl.setView(dash);
            dash.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Peran pengguna tidak dikenali.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
