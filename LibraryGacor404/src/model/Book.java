package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private int stock;
    private String status;

    public Book() { this.status = "AVAILABLE"; }

    public Book(int id, String title, String author, String publisher, int stock, String status) {
        this.id = id;
        setTitle(title); setAuthor(author); setPublisher(publisher);
        setStock(stock); setStatus(status);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Judul buku tidak boleh kosong.");
        this.title = title.trim();
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("Penulis tidak boleh kosong.");
        this.author = author.trim();
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) {
        this.publisher = publisher == null ? "" : publisher.trim();
    }

    public int getStock() { return stock; }
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stok tidak boleh negatif.");
        this.stock = stock;
        if (stock == 0) this.status = "UNAVAILABLE";
        else if ("UNAVAILABLE".equalsIgnoreCase(this.status)) this.status = "AVAILABLE";
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty())
            throw new IllegalArgumentException("Status buku tidak boleh kosong.");
        this.status = status.trim().toUpperCase();
    }

    public boolean isAvailable() { return stock > 0 && "AVAILABLE".equalsIgnoreCase(status); }

    @Override public String toString() { return title + " - " + author; }
}
