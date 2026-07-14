package com.delivery.mapper;

import com.delivery.dto.SettlementRequestResponse;
import com.delivery.settlement.SettlementRequest;
import com.delivery.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettlementMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "courier.username", target = "courierUsername")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "processedBy", target = "processedByUsername", qualifiedByName = "toUsername")
    @Mapping(source = "processedAt", target = "processedAt")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "createdAt", target = "createdAt")
    SettlementRequestResponse toDto(SettlementRequest request);

    @Named("toUsername")
    default String toUsername(User user){
        return user != null ? user.getUsername() : null;
    }
}
