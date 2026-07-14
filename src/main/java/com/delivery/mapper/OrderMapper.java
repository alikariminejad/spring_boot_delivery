package com.delivery.mapper;

import com.delivery.dto.OrderResponse;
import com.delivery.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderResponse toDto(Order order);
}
