package com.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse implements Serializable {
    private UUID walletId;
    private BigDecimal balance;
    private BigDecimal blockedBalance;
    private BigDecimal availableBalance;
    private String ownerUsername;
    private static final long serialVersionUID = 1L;
}
