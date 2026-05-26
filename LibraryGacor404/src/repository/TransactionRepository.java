package repository;

import java.util.List;
import model.Transaction;

public interface TransactionRepository {
    List<Transaction> findAll();
    List<Transaction> findByMemberId(int memberId);
    boolean borrowBook(int memberId, int bookId);
    boolean returnBook(int transactionId, double fine);
    Transaction findById(int transactionId);
}
