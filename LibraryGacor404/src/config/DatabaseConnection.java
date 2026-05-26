package config;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 
 */
public final class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DB_NAME  = "pip_gacor404";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static final String BASE_URL = "jdbc:mysql://" + HOST + ":" + PORT
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_URL   = "jdbc:mysql://" + HOST + ":" + PORT + "/"
            + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException ex) {
            showError("Driver MySQL tidak ditemukan.\nTambahkan mysql-connector-j ke Libraries.\n" + ex.getMessage());
        } catch (SQLException ex) {
            showError("Gagal terhubung ke database.\nPastikan MySQL berjalan.\n" + ex.getMessage());
        }
        return connection;
    }

    /**
     * Coba inisialisasi DB + tabel + data awal secara otomatis.
     * Return true jika berhasil, false jika gagal.
     */
    public boolean initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Koneksi dulu ke server tanpa pilih DB
            try (Connection serverConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
                 Statement st = serverConn.createStatement()) {

                st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME
                        + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }

            // Sekarang koneksi ke DB yang sudah ada
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            try (Statement st = connection.createStatement()) {
                // users
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id        INT AUTO_INCREMENT PRIMARY KEY,
                        username  VARCHAR(50)  NOT NULL UNIQUE,
                        password  VARCHAR(255) NOT NULL,
                        full_name VARCHAR(100) NOT NULL,
                        role      ENUM('ADMIN','MEMBER') NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

                // members
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS members (
                        user_id     INT PRIMARY KEY,
                        member_id   VARCHAR(20) NOT NULL UNIQUE,
                        fine_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                        CONSTRAINT fk_members_user FOREIGN KEY (user_id)
                            REFERENCES users(id) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

                // books
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS books (
                        id        INT AUTO_INCREMENT PRIMARY KEY,
                        title     VARCHAR(200) NOT NULL,
                        author    VARCHAR(100) NOT NULL,
                        publisher VARCHAR(100),
                        stock     INT NOT NULL DEFAULT 0,
                        status    VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                        CONSTRAINT chk_stock CHECK (stock >= 0)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

                // transactions
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                        member_id      INT NOT NULL,
                        book_id        INT NOT NULL,
                        borrow_date    DATE NOT NULL,
                        return_date    DATE NULL,
                        status         VARCHAR(20) NOT NULL DEFAULT 'BORROWED',
                        fine           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                        CONSTRAINT fk_trans_member FOREIGN KEY (member_id) REFERENCES users(id),
                        CONSTRAINT fk_trans_book   FOREIGN KEY (book_id)   REFERENCES books(id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
            }

            // Seed data jika tabel users kosong
            seedDataIfEmpty();
            return true;

        } catch (ClassNotFoundException ex) {
            showError("Driver MySQL tidak ditemukan.\nTambahkan mysql-connector-j ke Libraries.\n" + ex.getMessage());
            return false;
        } catch (SQLException ex) {
            showError("Gagal inisialisasi database:\n" + ex.getMessage()
                    + "\n\nPastikan MySQL berjalan di localhost:" + PORT
                    + " dengan user=" + USER);
            return false;
        }
    }

    private void seedDataIfEmpty() throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();
            if (rs.getInt(1) > 0) return; // sudah ada data
        }

        try (Statement st = connection.createStatement()) {
            // Admin
            st.executeUpdate("""
                INSERT INTO users (username,password,full_name,role)
                VALUES ('admin','admin123','Administrator Gacor404','ADMIN')
            """);
            // Member demo
            st.executeUpdate("""
                INSERT INTO users (username,password,full_name,role)
                VALUES ('ridho','member123','Ridho Anggota','MEMBER'),
                       ('budi','member123','Budi Santoso','MEMBER')
            """);
            st.executeUpdate("""
                INSERT INTO members (user_id,member_id,fine_amount)
                SELECT id,'MBR-001',0 FROM users WHERE username='ridho'
            """);
            st.executeUpdate("""
                INSERT INTO members (user_id,member_id,fine_amount)
                SELECT id,'MBR-002',5000 FROM users WHERE username='budi'
            """);
            // Buku
            st.executeUpdate("""
                INSERT INTO books (title,author,publisher,stock,status) VALUES
                ('Pemrograman Berorientasi Objek','Rinaldi Munir','INFORMATIKA',5,'AVAILABLE'),
                ('Basis Data Lanjut','Fathansyah','ANDI',3,'AVAILABLE'),
                ('Algoritma dan Struktur Data','Thomas Cormen','ITB Press',2,'AVAILABLE'),
                ('Jaringan Komputer','Tanenbaum','Pearson',0,'UNAVAILABLE')
            """);
            // Contoh transaksi
            st.executeUpdate("""
                INSERT INTO transactions (member_id,book_id,borrow_date,status,fine)
                SELECT u.id,b.id,'2026-05-01','BORROWED',0
                FROM users u, books b
                WHERE u.username='ridho' AND b.title='Pemrograman Berorientasi Objek'
            """);
        }
    }

    public boolean testConnection() {
        return getConnection() != null;
    }

    public void closeConnection() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            finally { connection = null; }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
    }
}
