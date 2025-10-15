package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.Order;
import vn.edu.iuh.fit.bookshop_be.models.Customer;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("select o from Order o where o.customer = ?1")
    List<Order> findByCustomer(Customer customer, Sort createdAt);

    @Query("select o from Order o where o.paymentRef = ?1")
    Order findByPaymentRef(String paymentRef);


    @Query("select o from Order o where o.id = ?1 and o.customer = ?2")
    Order findByIdAndCustomer(Integer id, Customer customer);


}
