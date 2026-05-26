package model;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String fullName;

    public User() {}

    public User(int id, String username, String password, String fullName) {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
    }

    public abstract String displayRole();

    public int getId() { return id; }
    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("ID tidak valid.");
        this.id = id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        this.username = username.trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty())
            throw new IllegalArgumentException("Nama lengkap tidak boleh kosong.");
        this.fullName = fullName.trim();
    }

    @Override
    public String toString() { return fullName + " (" + displayRole() + ")"; }
}
