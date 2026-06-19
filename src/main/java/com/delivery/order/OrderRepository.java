package com.delivery.order;

import com.delivery.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByCustomer(User customer, Pageable pageable);
    Page<Order> findByCustomerAndStatus(User customer, OrderStatus status, Pageable pageable);
}
