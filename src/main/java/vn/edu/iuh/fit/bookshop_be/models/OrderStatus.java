package vn.edu.iuh.fit.bookshop_be.models;

public enum OrderStatus {
    UNPAID,
    PROCESSING,
    PENDING,
    SHIPPING,
    DELIVERED,
    CANCELED,
    REFUNDED,
    REFUND_REQUESTED,
    REFUNDING,
    REFUND_REJECTED
}
