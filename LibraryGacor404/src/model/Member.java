package model;

import java.util.*;

public class Member extends User {
    private String memberId;
    private double fineAmount;
    private final List<Transaction> borrowingHistory = new ArrayList<>();

    public Member() {}

    public Member(int id, String username, String password, String fullName,
                  String memberId, double fineAmount) {
        super(id, username, password, fullName);
        setMemberId(memberId);
        setFineAmount(fineAmount);
    }

    @Override public String displayRole() { return "Anggota Perpustakaan"; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty())
            throw new IllegalArgumentException("Member ID tidak boleh kosong.");
        this.memberId = memberId.trim();
    }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) {
        if (fineAmount < 0) throw new IllegalArgumentException("Denda tidak boleh negatif.");
        this.fineAmount = fineAmount;
    }

    public List<Transaction> getBorrowingHistory() {
        return Collections.unmodifiableList(borrowingHistory);
    }
    public void addTransaction(Transaction t) { if (t != null) borrowingHistory.add(t); }
    public void clearBorrowingHistory() { borrowingHistory.clear(); }
}
