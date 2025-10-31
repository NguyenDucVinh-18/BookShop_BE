package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.InventoryCheckReceiptDetail;

@Repository
public interface InventoryCheckReceiptDetailRepository extends JpaRepository<InventoryCheckReceiptDetail, Integer> {
}
