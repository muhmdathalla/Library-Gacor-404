package repository;

import config.DatabaseConnection;
import java.sql.*;
import java.util.*;
import model.Book;

public class BookRepositoryImpl implements BookRepository {

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id,title,author,publisher,stock,status FROM books ORDER BY title";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException ex) { System.err.println("BookRepo.findAll: " + ex.getMessage()); }
        return books;
    }

    @Override
    public List<Book> searchByKeyword(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id,title,author,publisher,stock,status FROM books "
                   + "WHERE title LIKE ? OR author LIKE ? OR publisher LIKE ? ORDER BY title";
        String p = "%" + keyword.trim() + "%";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) books.add(mapRow(rs)); }
        } catch (SQLException ex) { System.err.println("BookRepo.search: " + ex.getMessage()); }
        return books;
    }

    @Override
    public Book findById(int id) {
        String sql = "SELECT id,title,author,publisher,stock,status FROM books WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapRow(rs); }
        } catch (SQLException ex) { System.err.println("BookRepo.findById: " + ex.getMessage()); }
        return null;
    }

    @Override
    public boolean insert(Book book) {
        String sql = "INSERT INTO books (title,author,publisher,stock,status) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, book.getTitle()); ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher()); ps.setInt(4, book.getStock());
            ps.setString(5, book.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("BookRepo.insert: " + ex.getMessage()); return false; }
    }

    @Override
    public boolean update(Book book) {
        String sql = "UPDATE books SET title=?,author=?,publisher=?,stock=?,status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, book.getTitle()); ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher()); ps.setInt(4, book.getStock());
            ps.setString(5, book.getStatus()); ps.setInt(6, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("BookRepo.update: " + ex.getMessage()); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("BookRepo.delete: " + ex.getMessage()); return false; }
    }

    @Override
    public boolean decreaseStock(int bookId) {
        String sql = "UPDATE books SET stock=stock-1, "
                   + "status=IF(stock-1<=0,'UNAVAILABLE',status) WHERE id=? AND stock>0";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId); return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("BookRepo.decreaseStock: " + ex.getMessage()); return false; }
    }

    @Override
    public boolean increaseStock(int bookId) {
        String sql = "UPDATE books SET stock=stock+1, status='AVAILABLE' WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId); return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("BookRepo.increaseStock: " + ex.getMessage()); return false; }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                rs.getString("publisher"), rs.getInt("stock"), rs.getString("status"));
    }
}
