package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.Order;
import vn.edu.iuh.fit.bookshop_be.models.User;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("select o from Order o where o.user = ?1")
    List<Order> findByUser(User user);

    @Query("select o from Order o where o.paymentRef = ?1")
    Order findByPaymentRef(String paymentRef);
}
