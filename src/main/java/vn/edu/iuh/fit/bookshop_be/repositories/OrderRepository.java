package vn.edu.iuh.fit.bookshop_be.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.bookshop_be.models.Order;
import vn.edu.iuh.fit.bookshop_be.models.Customer;
import vn.edu.iuh.fit.bookshop_be.models.OrderStatus;
import vn.edu.iuh.fit.bookshop_be.models.ReturnOrder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("select o from Order o where o.customer = ?1")
    List<Order> findByCustomer(Customer customer, Sort createdAt);

    @Query("select o from Order o where o.paymentRef = ?1")
    Order findByPaymentRef(String paymentRef);


    @Query("select o from Order o where o.id = ?1 and o.customer = ?2")
    Order findByIdAndCustomer(Integer id, Customer customer);

    // tinh so luong ban cua tung san pham

    @Query("SELECT SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "WHERE oi.product.id = ?1")
    Long countTotalProductSold(Integer productId);

    // Đếm số hóa đơn đã hoàn thành (DELIVERED)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :completedStatus")
    Long countCompletedOrdersBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("completedStatus") OrderStatus completedStatus);

    // Đếm số hóa đơn chưa hoàn thành (khác DELIVERED, CANCELED, REFUNDED)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status IN :uncompletedStatuses")
    Long countUncompletedOrdersBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("uncompletedStatuses") List<OrderStatus> uncompletedStatuses);

    // Đếm số hóa đơn hủy hoặc hoàn trả
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status IN :refundOrCanceledStatuses")
    Long countRefundOrCanceledOrdersBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            @Param("refundOrCanceledStatuses") List<OrderStatus> refundOrCanceledStatuses);


    // Tổng doanh thu hóa đơn đã hoàn thành
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :completedStatus")
    Double calculateTotalRevenueCompletedBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("completedStatus") OrderStatus completedStatus);

    // Tổng doanh thu hóa đơn chưa hoàn thành
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status IN :uncompletedStatuses")
    Double calculateTotalRevenueUncompletedBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   @Param("uncompletedStatuses") List<OrderStatus> uncompletedStatuses);

    @Query("SELECT SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "WHERE oi.product.id = :productId " +
            "AND oi.order.createdAt BETWEEN :startDate AND :endDate" +
            " AND oi.order.status = 'DELIVERED'")
    Long countTotalProductSoldBetween(@Param("productId") Integer productId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("select o from Order o where o.returnOrder = ?1")
    Order findByReturnOrder(ReturnOrder returnOrder);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findAllByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

}
