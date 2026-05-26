package app;

import config.DatabaseConnection;
import javax.swing.*;
import view.LoginView;

/**
 * Entry point Aplikasi Perpustakaan Digital Gacor404.
 * Otomatis inisialisasi database + tabel saat pertama kali dijalankan.
 */
public class LibraryApp {

    public static void main(String[] args) {
        // Inisialisasi DB dulu sebelum tampilkan UI
        boolean dbOk = DatabaseConnection.getInstance().initializeDatabase();
        if (!dbOk) {
            // Pesan error sudah ditampilkan oleh initializeDatabase()
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new LoginView().setVisible(true);
        });
    }
}
