package com.backend.order.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
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
        order.setShippingMethod(getShippingMethod(req.getShippingMethodId()));
        order.setAddress(createOrderAddress(req.getAddress()));
        order.setPayment(createPayment(req.getPaymentMethod()));
        order.setCustomer(getCustomer(userId));

        order.setOrderItems(orderItemService.create(order, req.getOrderItems()));

        // SnapShot
        order.setTotal(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(0D, Double::sum));

        publisher.publishEvent(mapper.toCreatedEvent(order));

        // TODO implement nốt
        return null;
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

    private OrderAddress createOrderAddress(OrderCreateReq.AddressDTO dto) {
        return mapper.toAddress(dto);
    }

    private Payment createPayment(PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setName(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    private Customer getCustomer(long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer for UserId = " + userId + " not found"));
    }

}
