package com.delivery.wallet;

import com.delivery.common.BaseEntity;
import com.delivery.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Wallet extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;



}
