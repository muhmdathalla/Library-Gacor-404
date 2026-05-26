package repository;

import config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import model.Transaction;
import java.sql.Date;
public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public List<Transaction> findAll() {
        return query("SELECT t.transaction_id,t.member_id,t.book_id,t.borrow_date,t.return_date,"
                + "t.status,t.fine,u.full_name AS member_name,b.title AS book_title "
                + "FROM transactions t "
                + "INNER JOIN users u ON t.member_id=u.id "
                + "INNER JOIN books b ON t.book_id=b.id "
                + "ORDER BY t.transaction_id DESC", null);
    }

    @Override
    public List<Transaction> findByMemberId(int memberId) {
        System.out.println("[DEBUG] findByMemberId called with memberId=" + memberId);
        return query("SELECT t.transaction_id,t.member_id,t.book_id,t.borrow_date,t.return_date,"
                + "t.status,t.fine,u.full_name AS member_name,b.title AS book_title "
                + "FROM transactions t "
                + "INNER JOIN users u ON t.member_id=u.id "
                + "INNER JOIN books b ON t.book_id=b.id "
                + "WHERE t.member_id=? ORDER BY t.transaction_id DESC",
                ps -> ps.setInt(1, memberId));
    }

    @Override
    public Transaction findById(int transactionId) {
        List<Transaction> res = query("SELECT t.transaction_id,t.member_id,t.book_id,t.borrow_date,"
                + "t.return_date,t.status,t.fine,u.full_name AS member_name,b.title AS book_title "
                + "FROM transactions t "
                + "INNER JOIN users u ON t.member_id=u.id "
                + "INNER JOIN books b ON t.book_id=b.id "
                + "WHERE t.transaction_id=?",
                ps -> ps.setInt(1, transactionId));
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public boolean borrowBook(int memberId, int bookId) {
        Connection c = null;
        try {
            c = DatabaseConnection.getInstance().getConnection();
            if (c == null) return false;
            c.setAutoCommit(false);

            String stockSql = "UPDATE books SET stock=stock-1, "
                    + "status=IF(stock-1<=0,'UNAVAILABLE',status) WHERE id=? AND stock>0";
            try (PreparedStatement ps = c.prepareStatement(stockSql)) {
                ps.setInt(1, bookId);
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            String txSql = "INSERT INTO transactions (member_id,book_id,borrow_date,status) VALUES (?,?,?,'BORROWED')";
            try (PreparedStatement ps = c.prepareStatement(txSql)) {
                ps.setInt(1, memberId); ps.setInt(2, bookId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            c.commit(); return true;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("TxRepo.borrow: " + ex.getMessage()); return false;
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public boolean returnBook(int transactionId, double fine) {
        Connection c = null;
        try {
            Transaction tx = findById(transactionId);
            if (tx == null || "RETURNED".equalsIgnoreCase(tx.getStatus())) return false;

            c = DatabaseConnection.getInstance().getConnection();
            if (c == null) return false;
            c.setAutoCommit(false);

            String updateTx = "UPDATE transactions SET return_date=?,status='RETURNED',fine=? "
                    + "WHERE transaction_id=? AND status='BORROWED'";
            try (PreparedStatement ps = c.prepareStatement(updateTx)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setDouble(2, fine); ps.setInt(3, transactionId);
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            String updateStock = "UPDATE books SET stock=stock+1,status='AVAILABLE' WHERE id=?";
            try (PreparedStatement ps = c.prepareStatement(updateStock)) {
                ps.setInt(1, tx.getBookId());
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            if (fine > 0) {
                String updateFine = "UPDATE members SET fine_amount=fine_amount+? WHERE user_id=?";
                try (PreparedStatement ps = c.prepareStatement(updateFine)) {
                    ps.setDouble(1, fine); ps.setInt(2, tx.getMemberId());
                    ps.executeUpdate();
                }
            }

            c.commit(); return true;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("TxRepo.return: " + ex.getMessage()); return false;
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    // -------------------------------------------------------

    @FunctionalInterface
    interface Setter { void set(PreparedStatement ps) throws SQLException; }

    private List<Transaction> query(String sql, Setter setter) {
        List<Transaction> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (setter != null) setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) { System.err.println("TxRepo.query: " + ex.getMessage()); }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction(
                rs.getInt("transaction_id"), rs.getInt("member_id"), rs.getInt("book_id"),
                rs.getDate("borrow_date").toLocalDate(),
                rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                rs.getString("status"), rs.getDouble("fine"));
        t.setMemberName(rs.getString("member_name"));
        t.setBookTitle(rs.getString("book_title"));
        return t;
    }
}
