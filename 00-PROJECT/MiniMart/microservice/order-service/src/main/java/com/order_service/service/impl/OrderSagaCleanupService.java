package com.order_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.order_service.enums.OrderStatus;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.model.Order;
import com.order_service.repository.OrderRepository;
import com.order_service.service.OrderSagaTrackerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component // Component để Spring quản lý
@RequiredArgsConstructor
@Slf4j
public class OrderSagaCleanupService {
    private final OrderSagaTrackerService sagaTrackerService;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processAwaitingCancellation() {
        log.info("[CRON] Starting processing AWAITING_CANCEL orders.");
        LocalDateTime cutOffTime = LocalDateTime.now().minusHours(1L);
        List<Order> awaitingOrders = orderRepository.findByStatusAndUpdatedAtBefore(
                OrderStatus.AWAITING_CANCEL,
                cutOffTime);

        System.out.println(awaitingOrders);

        for (Order order : awaitingOrders) {
            long orderId = order.getId();

            if (sagaTrackerService.checkCancelRediness(orderId, order.getUserId())) {

                log.info("[CRON][OrderId={}] Saga steps finished/stable. Proceeding to CANCELING.", orderId);

                order.setStatus(OrderStatus.CANCELING);
                orderRepository.save(order);

                eventPublisher.publishEvent(new OrderCancelDomainEvent(order));
            }
        }

    }

}
