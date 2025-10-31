package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.InventoryCheckReceipt;
import vn.edu.iuh.fit.bookshop_be.models.StockReceipt;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryCheckReceiptRepository extends JpaRepository<InventoryCheckReceipt, Integer> {
    @Query("SELECT i FROM InventoryCheckReceipt i " +
            "WHERE i.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY i.createdAt DESC")
    List<InventoryCheckReceipt> getInventoryChecksDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
