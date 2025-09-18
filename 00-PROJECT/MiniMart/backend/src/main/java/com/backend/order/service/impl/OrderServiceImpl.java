package com.backend.order.service.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.order.dto.event.OrderCanceledEvent;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderFilter;
import com.backend.order.mapper.OrderMapper;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.Payment;
import com.backend.order.repository.OrderItemRepository;
import com.backend.order.repository.OrderRepository;
import com.backend.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper mapper;

    private final ApplicationEventPublisher publisher;

    private final OrderFactory orderFactory;
    private final OrderItemFactory orderItemFactory;
    private final StockAllocator stockAllocator;
    private final PaymentCalculator paymentCalculator;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable, long userId) {
        throw new UnsupportedOperationException("Unimplemented method 'findPage'");
    }

    @Override
    @Transactional(timeout = 5)
    public OrderDTO create(OrderCreateReq req, long userId) {
        Order order = orderFactory.create(req, userId);

        List<OrderItem> orderItems = orderItemFactory.create(order, req.getOrderItems());
        order.setOrderItems(orderItems);

        stockAllocator.allocate(orderItems);
        orderItems.forEach(OrderItem::calculateAvgCost);

        Payment payment = paymentCalculator.calculate(order, req.getPaymentMethod());
        order.setPayment(payment);
        order.setTotal(payment.getAmountTotal());

        orderItemRepository.saveAll(orderItems);
        repository.save(order);

        publisher.publishEvent(mapper.toCreatedEvent(order));
        return mapper.toDTO(order);
    }

    @Transactional
    @Override
    public void cancel(Long orderId, long userId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id = " + orderId + " is not found"));
        if (!(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SHIPPED)) {
            throw new IllegalStateException("Order cannot be canceled in status = " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);
        stockAllocator.release(order);

        repository.save(order);
        publisher.publishEvent(new OrderCanceledEvent(order.getId()));

    }

    @Override
    public Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        return repository.findAdminAll(filter, pageable);
    }

}
