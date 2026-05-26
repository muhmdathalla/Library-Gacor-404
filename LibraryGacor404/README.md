# Perpustakaan Digital Gacor404 🚀

## Fitur Utama
- ✅ Login Admin & Member
- ✅ **Register member baru** (tab Daftar di halaman login)
- ✅ **Auto-setup database** (buat DB + tabel otomatis saat pertama jalan)
- ✅ CRUD Buku (Tambah, Edit, Hapus, Cari)
- ✅ CRUD Anggota (Tambah, Edit, Hapus) dengan auto-generate Member ID
- ✅ Peminjaman & Pengembalian buku dengan denda
- ✅ Export laporan (TXT, CSV, PDF)
- ✅ UI modern dengan tampilan bersih

---

## Cara Menjalankan

### Prasyarat
1. **MySQL 8+** berjalan di `localhost:3306`
   - Default: user `root`, password kosong
   - Jika berbeda, edit `src/config/DatabaseConnection.java` bagian `USER` dan `PASSWORD`
2. **JDK 17+**
3. **mysql-connector-j** (JDBC driver) sudah di-add ke Libraries project

### Di NetBeans
1. Buka project ini di NetBeans
2. Klik kanan project → Properties → Libraries → Add JAR
3. Tambahkan `mysql-connector-j-x.x.x.jar`
4. Run → aplikasi otomatis buat database kalau belum ada!

### Compile Manual (tanpa IDE)
```bash
# Letakkan mysql-connector-j.jar di folder lib/
javac -cp "lib/mysql-connector-j.jar" -d build/classes \
  $(find src -name "*.java")

java -cp "build/classes:lib/mysql-connector-j.jar" app.LibraryApp
```

---

## Akun Demo
| Username | Password   | Role  |
|----------|------------|-------|
| admin    | admin123   | Admin |
| ridho    | member123  | Member|
| budi     | member123  | Member|

---

## Struktur Project
```
src/
├── app/            → LibraryApp.java (main)
├── config/         → DatabaseConnection.java (singleton + auto-init)
├── controller/     → AdminController, MemberController, AuthController
├── model/          → User, Admin, Member, Book, Transaction
├── repository/     → Interface + Implementasi CRUD (JDBC)
├── report/         → ReportGenerator (TXT, CSV, PDF)
└── view/
    ├── util/       → UiStyles.java (tema visual)
    ├── LoginView.java
    ├── AdminDashboardView.java
    └── MemberDashboardView.java

sql/
└── schema.sql      → Script manual (opsional, app sudah auto-init)
```

---

## Konfigurasi Database
Edit `src/config/DatabaseConnection.java`:
```java
private static final String HOST     = "localhost";  // Host MySQL
private static final int    PORT     = 3306;         // Port MySQL
private static final String USER     = "root";       // Username MySQL
private static final String PASSWORD = "";           // Password MySQL
```
