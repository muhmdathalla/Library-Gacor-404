package model;

import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int memberId;
    private int bookId;
    private String memberName;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;
    private double fine;

    public Transaction() { this.status = "BORROWED"; this.fine = 0; }

    public Transaction(int transactionId, int memberId, int bookId,
                       LocalDate borrowDate, LocalDate returnDate,
                       String status, double fine) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        setBorrowDate(borrowDate);
        this.returnDate = returnDate;
        setStatus(status);
        setFine(fine);
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int v) { this.transactionId = v; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int v) { this.memberId = v; }

    public int getBookId() { return bookId; }
    public void setBookId(int v) { this.bookId = v; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String v) { this.memberName = v; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String v) { this.bookTitle = v; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate v) {
        if (v == null) throw new IllegalArgumentException("Tanggal pinjam wajib diisi.");
        this.borrowDate = v;
    }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate v) { this.returnDate = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) {
        if (v == null || v.trim().isEmpty())
            throw new IllegalArgumentException("Status transaksi tidak boleh kosong.");
        this.status = v.trim().toUpperCase();
    }

    public double getFine() { return fine; }
    public void setFine(double v) {
        if (v < 0) throw new IllegalArgumentException("Denda tidak boleh negatif.");
        this.fine = v;
    }
}
