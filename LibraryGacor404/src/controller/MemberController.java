package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.*;
import repository.*;
import view.MemberDashboardView;

public class MemberController {

    private final Member member;
    private MemberDashboardView view;
    private final BookRepository bookRepo      = new BookRepositoryImpl();
    private final TransactionRepository txRepo = new TransactionRepositoryImpl();

    public MemberController(Member member) { this.member = member; }

    public void setView(MemberDashboardView v) {
        this.view = v;
        refreshCatalog("");
        refreshHistory();
        // Pastikan data sudah terpasang ke view
        javax.swing.SwingUtilities.invokeLater(() -> {
            refreshHistory();
        });
    }

    public Member getMember() { return member; }

    public void refreshCatalog(String keyword) {
        if (view == null) return;
        List<Book> books = (keyword == null || keyword.trim().isEmpty())
                ? bookRepo.findAll()
                : bookRepo.searchByKeyword(keyword.trim());
        view.loadBooks(books);
    }

    public void refreshHistory() {
        if (view == null) return;
        System.out.println("[DEBUG] refreshHistory - member.getId()=" + member.getId() + " member.getUsername()=" + member.getUsername());
        List<Transaction> history = txRepo.findByMemberId(member.getId());
        System.out.println("[DEBUG] history size=" + history.size());
        member.clearBorrowingHistory();
        history.forEach(member::addTransaction);
        view.loadHistory(history);
        view.updateMemberInfo(member);
    }

    public void borrowBook(int bookId) {
        Book book = bookRepo.findById(bookId);
        if (book == null) {
            JOptionPane.showMessageDialog(view, "Buku tidak ditemukan.", "Error",
                    JOptionPane.ERROR_MESSAGE); return;
        }
        if (!book.isAvailable()) {
            JOptionPane.showMessageDialog(view, "Stok buku habis atau tidak tersedia.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE); return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Pinjam buku:\n\"" + book.getTitle() + "\"\noleh " + book.getAuthor() + "?",
                "Konfirmasi Peminjaman", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (txRepo.borrowBook(member.getId(), bookId)) {
            JOptionPane.showMessageDialog(view, "Peminjaman berhasil!\nSelamat membaca.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshCatalog(view.getCurrentSearchKeyword());
            refreshHistory();
        } else {
            JOptionPane.showMessageDialog(view, "Peminjaman gagal. Periksa stok buku.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
