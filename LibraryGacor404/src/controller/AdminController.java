package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.*;
import report.ReportGenerator;
import repository.*;
import view.AdminDashboardView;
import repository.TransactionRepository;
import repository.TransactionRepositoryImpl;

public class AdminController {

    private final Admin admin;
    private AdminDashboardView view;
    private final BookRepository bookRepo       = new BookRepositoryImpl();
    private final UserRepository userRepo       = new UserRepositoryImpl();
    private final TransactionRepository txRepo  = new TransactionRepositoryImpl();
    private final ReportGenerator reportGen     = new ReportGenerator();

    public AdminController(Admin admin) { this.admin = admin; }

    public void setView(AdminDashboardView v) { this.view = v; refreshAll(); }
    public Admin getAdmin() { return admin; }

    public void refreshAll() {
        if (view == null) return;
        view.loadBooks(bookRepo.findAll());
        view.loadMembers(userRepo.findAllMembers());
        view.loadTransactions(txRepo.findAll());
    }

    public void saveBook(Book book, boolean isUpdate) {
        try {
            boolean ok = isUpdate ? bookRepo.update(book) : bookRepo.insert(book);
            if (ok) {
                admin.logAdminAction((isUpdate ? "Update" : "Tambah") + " buku: " + book.getTitle());
                showInfo("Data buku berhasil disimpan.");
                refreshAll();
            } else showError("Gagal menyimpan data buku.");
        } catch (IllegalArgumentException ex) { showWarn(ex.getMessage()); }
    }

    public void deleteBook(int bookId) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Yakin hapus buku ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (bookRepo.delete(bookId)) {
            admin.logAdminAction("Hapus buku ID " + bookId);
            showInfo("Buku berhasil dihapus."); refreshAll();
        } else showError("Gagal menghapus buku. Mungkin masih ada transaksi aktif.");
    }

    public void saveMember(Member member, String password, boolean isUpdate) {
        try {
            if (userRepo.usernameExists(member.getUsername(), isUpdate ? member.getId() : 0)) {
                showWarn("Username sudah digunakan."); return;
            }
            boolean ok = isUpdate
                    ? userRepo.updateMember(member, password)
                    : userRepo.insertMember(member, password);
            if (ok) {
                admin.logAdminAction((isUpdate ? "Update" : "Tambah") + " anggota: " + member.getUsername());
                showInfo("Data anggota berhasil disimpan."); refreshAll();
            } else showError("Gagal menyimpan data anggota.");
        } catch (IllegalArgumentException ex) { showWarn(ex.getMessage()); }
    }

    public void deleteMember(int userId) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Yakin hapus anggota ini? Semua data transaksinya akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (userRepo.deleteMember(userId)) {
            admin.logAdminAction("Hapus anggota ID " + userId);
            showInfo("Anggota berhasil dihapus."); refreshAll();
        } else showError("Gagal menghapus anggota.");
    }

    public void returnTransaction(int transactionId, String fineText) {
        try {
            double fine = (fineText == null || fineText.trim().isEmpty())
                    ? 0 : Double.parseDouble(fineText.trim());
            if (fine < 0) throw new IllegalArgumentException("Denda tidak boleh negatif.");
            if (txRepo.returnBook(transactionId, fine)) {
                admin.logAdminAction("Pengembalian transaksi " + transactionId + " denda=" + fine);
                showInfo("Buku berhasil dikembalikan."); refreshAll();
            } else showError("Gagal memproses pengembalian.");
        } catch (NumberFormatException ex) { showWarn("Denda harus berupa angka."); }
        catch (IllegalArgumentException ex) { showWarn(ex.getMessage()); }
    }

    public void exportReport(String format) {
        List<Transaction> txs = txRepo.findAll();
        if (txs.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Tidak ada data transaksi.",
                    "Laporan", JOptionPane.WARNING_MESSAGE); return;
        }
        reportGen.exportTransactions(view, txs, format);
    }

    public String generateNextMemberId() { return userRepo.generateNextMemberId(); }

    private void showInfo(String msg)  { JOptionPane.showMessageDialog(view, msg, "Sukses",    JOptionPane.INFORMATION_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(view, msg, "Error",     JOptionPane.ERROR_MESSAGE); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(view, msg, "Peringatan",JOptionPane.WARNING_MESSAGE); }
}
