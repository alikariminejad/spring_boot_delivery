package com.delivery.mapper;

import com.delivery.dto.NotificationResponse;
import com.delivery.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "referenceId", target = "referenceId")
    @Mapping(source = "createdAt", target = "createdAt")
    NotificationResponse toDto(Notification notification);
}
