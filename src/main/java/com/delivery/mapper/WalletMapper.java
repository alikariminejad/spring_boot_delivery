package com.delivery.mapper;

import com.delivery.dto.WalletResponse;
import com.delivery.wallet.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {

    @Mapping(source = "id", target = "walletId")
    @Mapping(source = "user.username", target = "ownerUsername")
    @Mapping(target = "availableBalance", expression = "java(wallet.getBalance().subtract(wallet.getBlockedBalance()))")
    WalletResponse toDto(Wallet wallet);
}
