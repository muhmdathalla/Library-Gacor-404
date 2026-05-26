package model;

public class Admin extends User {
    public Admin() { super(); }
    public Admin(int id, String username, String password, String fullName) {
        super(id, username, password, fullName);
    }
    @Override public String displayRole() { return "Administrator"; }
    public String getDashboardTitle() { return "Dashboard Admin - Perpustakaan Gacor404"; }
    public void logAdminAction(String action) {
        System.out.println("[ADMIN LOG] " + getUsername() + " -> " + action);
    }
}
