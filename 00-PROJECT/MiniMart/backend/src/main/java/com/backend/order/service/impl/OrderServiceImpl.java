package com.backend.order.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.order.dto.req.OrderAddressReq;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.mapper.OrderMapper;
import com.backend.order.model.Order;
import com.backend.order.model.OrderAddress;
import com.backend.order.model.OrderItem;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.Payment;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.ShippingMethod;
import com.backend.order.repository.OrderRepository;
import com.backend.order.repository.ShippingMethodRepository;
import com.backend.order.service.OrderItemService;
import com.backend.order.service.OrderService;
import com.backend.user.model.Customer;
import com.backend.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ShippingMethodRepository shippingMethodRepository;

    private final OrderRepository repository;

    private final CustomerRepository customerRepository;

    private final OrderItemService orderItemService;

    private final ApplicationEventPublisher publisher;

    private final OrderMapper mapper;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable) {
        throw new UnsupportedOperationException("Unimplemented method 'findPage'");
    }

    @Override
    @Transactional(timeout = 5)
    public OrderDTO create(OrderCreateReq req, long userId) {

        Order order = createRawOrder(req);
        order.setAddress(createOrderAddress(req.getAddress()));
        order.setCustomer(getCustomer(userId));

        order.setOrderItems(orderItemService.create(order, req.getOrderItems()));
        order.setShippingMethod(getShippingMethod(req.getShippingMethodId()));
        order.setPayment(createPayment(order, req.getPaymentMethod()));
        order.setTotal(order.getPayment().getAmountDue());

        publisher.publishEvent(mapper.toCreatedEvent(order));

        repository.save(order);
        return mapper.toDTO(order);
    }

    private Order createRawOrder(OrderCreateReq req) {
        Order order = new Order();
        order.setMessage(req.getMessage());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    private ShippingMethod getShippingMethod(long ShippingMethodId) {
        return shippingMethodRepository.getReferenceById(ShippingMethodId);
    }

    private OrderAddress createOrderAddress(OrderAddressReq dto) {
        return mapper.toAddress(dto);
    }

    private Payment createPayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setName(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountPaid(0);
        double shippingCost = order.getShippingMethod().getCost();
        double subTotal = order.getOrderItems().stream().mapToDouble(OrderItem::getTotalPrice).sum();
        payment.setAmountDue(shippingCost + subTotal);
        return payment;
    }

    private Customer getCustomer(long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer for UserId = " + userId + " not found"));
    }

    @Transactional
    @Override
    public void cancel(Long orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id = " + orderId + " is not found"));
        updateStatus(order);
        orderItemService.releaseStockAllocation(order);
        repository.save(order);

    }

    private void updateStatus(Order order) {
        order.setStatus(OrderStatus.CANCELED);
    }

}
