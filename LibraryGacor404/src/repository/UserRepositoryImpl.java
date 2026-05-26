package repository;

import config.DatabaseConnection;
import java.sql.*;
import java.util.*;
import model.*;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User authenticate(String username, String password) {
        String sql = "SELECT u.id,u.username,u.password,u.full_name,u.role,"
                   + "m.member_id,m.fine_amount "
                   + "FROM users u LEFT JOIN members m ON u.id=m.user_id "
                   + "WHERE u.username=? AND u.password=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim()); ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException ex) { System.err.println("UserRepo.auth: " + ex.getMessage()); }
        return null;
    }

    @Override
    public List<Member> findAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT u.id,u.username,u.password,u.full_name,m.member_id,m.fine_amount "
                   + "FROM users u INNER JOIN members m ON u.id=m.user_id ORDER BY m.member_id";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMember(rs));
        } catch (SQLException ex) { System.err.println("UserRepo.findAll: " + ex.getMessage()); }
        return list;
    }

    @Override
    public Member findMemberByUserId(int userId) {
        String sql = "SELECT u.id,u.username,u.password,u.full_name,m.member_id,m.fine_amount "
                   + "FROM users u INNER JOIN members m ON u.id=m.user_id WHERE u.id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapMember(rs); }
        } catch (SQLException ex) { System.err.println("UserRepo.findMember: " + ex.getMessage()); }
        return null;
    }

    @Override
    public boolean insertMember(Member member, String rawPassword) {
        Connection c = null;
        try {
            c = DatabaseConnection.getInstance().getConnection();
            if (c == null) return false;
            c.setAutoCommit(false);

            String userSql = "INSERT INTO users (username,password,full_name,role) VALUES (?,?,?,'MEMBER')";
            try (PreparedStatement ps = c.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, member.getUsername());
                ps.setString(2, rawPassword);
                ps.setString(3, member.getFullName());
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) member.setId(keys.getInt(1));
                }
            }

            String memberSql = "INSERT INTO members (user_id,member_id,fine_amount) VALUES (?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(memberSql)) {
                ps.setInt(1, member.getId());
                ps.setString(2, member.getMemberId());
                ps.setDouble(3, member.getFineAmount());
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            c.commit(); return true;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("UserRepo.insert: " + ex.getMessage()); return false;
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public boolean updateMember(Member member, String rawPassword) {
        Connection c = null;
        try {
            c = DatabaseConnection.getInstance().getConnection();
            if (c == null) return false;
            c.setAutoCommit(false);

            String userSql = "UPDATE users SET username=?,password=?,full_name=? WHERE id=?";
            try (PreparedStatement ps = c.prepareStatement(userSql)) {
                ps.setString(1, member.getUsername());
                ps.setString(2, rawPassword);
                ps.setString(3, member.getFullName());
                ps.setInt(4, member.getId());
                ps.executeUpdate();
            }

            String memberSql = "UPDATE members SET member_id=?,fine_amount=? WHERE user_id=?";
            try (PreparedStatement ps = c.prepareStatement(memberSql)) {
                ps.setString(1, member.getMemberId());
                ps.setDouble(2, member.getFineAmount());
                ps.setInt(3, member.getId());
                ps.executeUpdate();
            }

            c.commit(); return true;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("UserRepo.update: " + ex.getMessage()); return false;
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public boolean deleteMember(int userId) {
        String sql = "DELETE FROM users WHERE id=? AND role='MEMBER'";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); return ps.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("UserRepo.delete: " + ex.getMessage()); return false; }
    }

    @Override
    public boolean usernameExists(String username, int excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND id<>?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim()); ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) { System.err.println("UserRepo.usernameExists: " + ex.getMessage()); }
        return false;
    }

    @Override
    public String generateNextMemberId() {
        String sql = "SELECT member_id FROM members ORDER BY member_id DESC LIMIT 1";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("member_id"); // e.g. MBR-007
                try {
                    int num = Integer.parseInt(last.replace("MBR-", ""));
                    return String.format("MBR-%03d", num + 1);
                } catch (NumberFormatException ignored) {}
            }
        } catch (SQLException ex) { System.err.println("UserRepo.genId: " + ex.getMessage()); }
        return "MBR-001";
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new Admin(rs.getInt("id"), rs.getString("username"),
                    rs.getString("password"), rs.getString("full_name"));
        }
        return mapMember(rs);
    }

    private Member mapMember(ResultSet rs) throws SQLException {
        return new Member(rs.getInt("id"), rs.getString("username"),
                rs.getString("password"), rs.getString("full_name"),
                rs.getString("member_id"), rs.getDouble("fine_amount"));
    }
}
