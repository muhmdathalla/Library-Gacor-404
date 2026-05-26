package repository;

import java.util.List;
import model.Book;

public interface BookRepository {
    List<Book> findAll();
    List<Book> searchByKeyword(String keyword);
    Book findById(int id);
    boolean insert(Book book);
    boolean update(Book book);
    boolean delete(int id);
    boolean decreaseStock(int bookId);
    boolean increaseStock(int bookId);
}
