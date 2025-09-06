package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.models.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
    public User findByUsername(String username);

    @Query("select u from User u where u.verificationCode = ?1")
    User findByVerificationCode(String verificationCode);

    @Query("select u from User u where u.role = ?1")
    List<User> findByRole(Role role);


}
