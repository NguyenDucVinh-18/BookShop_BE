package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.ReturnOrder;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Integer> {
}
