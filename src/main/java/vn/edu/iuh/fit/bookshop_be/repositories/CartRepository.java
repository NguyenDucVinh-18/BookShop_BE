package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.Cart;
import vn.edu.iuh.fit.bookshop_be.models.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUser(User user);

}
