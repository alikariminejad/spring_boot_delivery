package com.delivery.settlement;

import com.delivery.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementRequestRepository extends JpaRepository<SettlementRequest, UUID> {
    Page<SettlementRequest> findByCourier(User courier, Pageable pageable);
}
