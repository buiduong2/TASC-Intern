package com.backend.order.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.cart.model.Cart;
import com.backend.cart.repository.CartRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.exception.ValidationException;
import com.backend.common.utils.ErrorCode;
import com.backend.order.dto.event.OrderCanceledEvent;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.req.OrderItemReq;
import com.backend.order.dto.req.OrderUpdateReq;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderFilter;
import com.backend.order.mapper.OrderMapper;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.Payment;
import com.backend.order.model.PaymentStatus;
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
    private final CartRepository cartRepository;

    private final ApplicationEventPublisher publisher;

    private final OrderFactory orderFactory;
    private final OrderItemFactory orderItemFactory;
    private final StockAllocator stockAllocator;
    private final PaymentCalculator paymentCalculator;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable, long userId) {
        return repository.findByIdAndUserIdForPage(userId, pageable)
                .map(mapper::toDTO);
    }

    @Transactional(timeout = 5)
    @Override
    public OrderDTO createFromCart(OrderCreateReq req, long userId) {
        Cart cart = cartRepository.findByUserIdForClearItem(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND.format(userId)));
        LinkedHashSet<OrderItemReq> items = cart.getItems().stream().map(i -> {
            OrderItemReq item = new OrderItemReq();
            item.setProductId(i.getProduct().getId());
            item.setQuantity(i.getQuantity());
            return item;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        req.setOrderItems(items);

        if (req.getOrderItems().isEmpty()) {
            throw new ValidationException("orderItems", "Cart must be not empty");
        }

        OrderDTO orderDTO = create(req, userId);

        cart.clearItem();

        return orderDTO;
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
        Order order = repository.findByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id = " + orderId + " is not found"));
        cancel(order);

    }

    @Transactional
    @Override
    public OrderDTO cancelAdmin(long id) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND.format(id)));
        order = cancel(order);

        return mapper.toDTO(order);
    }

    private Order cancel(Order order) {
        if (!(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SHIPPED)) {
            throw new ValidationException("status", "Order cannot be canceled in current status = " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);
        stockAllocator.release(order);
        order = repository.save(order);
        publisher.publishEvent(new OrderCanceledEvent(order.getId()));
        return order;
    }

    @Override
    public Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        return repository.findAdminAll(filter, pageable);
    }

    @Transactional
    @Override
    public OrderDTO updateStatus(long id, OrderUpdateReq req) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND.format(id)));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELED) {
            throw new ValidationException("status", "Order cannot be update in current status = " + order.getStatus());
        }

        OrderStatus nextStatus = OrderStatus.valueOf(req.getStatus());

        if (nextStatus == OrderStatus.COMPLETED && order.getPayment().getStatus() != PaymentStatus.PAID) {
            throw new ValidationException("status",
                    "Order cannot be update in Payment current Status = " + order.getPayment().getStatus());
        }

        order.setStatus(OrderStatus.valueOf(req.getStatus()));
        return mapper.toDTO(order);
    }

}
