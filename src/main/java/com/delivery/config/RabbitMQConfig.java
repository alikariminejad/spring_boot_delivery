package com.delivery.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String SETTLEMENT_EXCHANGE = "settlement.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // order events
    @Bean
    public Queue notificationQueue(){
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public TopicExchange orderExchange(){
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange settlementExchange(){
        return new TopicExchange(SETTLEMENT_EXCHANGE);
    }

    @Bean
    public Binding orderPlacedBinding(Queue notificationQueue, TopicExchange orderExchange){
        return BindingBuilder.bind(notificationQueue).to(orderExchange).with("order.placed");
    }

    @Bean
    public Binding courierAssignedBinding(Queue notificationQueue, TopicExchange orderExchange){
        return BindingBuilder.bind(notificationQueue).to(orderExchange).with("order.courier.assigned");
    }

    @Bean
    public Binding orderStatusChangedBinding(Queue notificationQueue, TopicExchange orderExchange){
        return BindingBuilder.bind(notificationQueue).to(orderExchange).with("order.status.changed");
    }

    @Bean
    public Binding orderCancelledBinding(Queue notificationQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderExchange).with("order.cancelled");
    }

    @Bean
    public Binding settlementRequestedBinding(Queue notificationQueue, TopicExchange settlementExchange) {
        return BindingBuilder.bind(notificationQueue).to(settlementExchange).with("settlement.requested");
    }

    @Bean
    public Binding settlementApprovedBinding(Queue notificationQueue, TopicExchange settlementExchange) {
        return BindingBuilder.bind(notificationQueue).to(settlementExchange).with("settlement.approved");
    }

    @Bean
    public Binding settlementRejectedBinding(Queue notificationQueue, TopicExchange settlementExchange) {
        return BindingBuilder.bind(notificationQueue).to(settlementExchange).with("settlement.rejected");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

