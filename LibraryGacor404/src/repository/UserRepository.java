package repository;

import java.util.List;
import model.Member;
import model.User;

public interface UserRepository {
    User authenticate(String username, String password);
    List<Member> findAllMembers();
    Member findMemberByUserId(int userId);
    boolean insertMember(Member member, String rawPassword);
    boolean updateMember(Member member, String rawPassword);
    boolean deleteMember(int userId);
    boolean usernameExists(String username, int excludeUserId);
    String generateNextMemberId();
}
