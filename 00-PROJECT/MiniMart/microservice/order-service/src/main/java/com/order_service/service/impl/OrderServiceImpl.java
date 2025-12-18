package com.order_service.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminSummaryDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.exception.ErrorCode;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;
import com.order_service.repository.OrderItemRepository;
import com.order_service.repository.OrderRepository;
import com.order_service.saga.handler.OrderCreationSyncHandler;
import com.order_service.service.OrderCoreService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderCoreService coreService;

    private final OrderItemRepository itemRepository;

    private final OrderCreationSyncHandler creationSyncHandler;

    private final OrderMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable, Long userId) {
        return repository.findByUserIdForCLientSummary(userId, pageable)
                .map(mapper::toClientSummaryDTO);
    }

    @Override
    public Page<OrderAdminSummaryDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        return repository.findAll((root, query, builder) -> builder.conjunction(), pageable)
                .map(mapper::toAdminSummaryDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailDTO findByIdAndUserId(long id, Long userId) {

        Order order = repository.findByIdAndUserIdForClientDetail(id, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        List<OrderItem> items = itemRepository.findByOrderId(id);

        order.setOrderItems(items);

        return mapper.toClientDetailDTO(order);
    }

    @Override
    public OrderDTO create(OrderCreateReq req, Long userId) {

        Order order = coreService.createRaw(req, userId);

        if (order.getPaymentMethod().equals(PaymentMethod.CASH)
                || order.getPaymentMethod().equals(PaymentMethod.VNPAY)) {
            Pair<Order, String> pair = creationSyncHandler.create(order);
            return mapper.toClientSummaryDTO(pair.getFirst(), pair.getSecond());
        } else {
            eventPublisher.publishEvent(new OrderPreparedDomainEvent(order));
        }
        return mapper.toClientSummaryDTO(order);
    }

    @Override
    public OrderDTO createFromCart(OrderCreateReq req, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFromCart'");
    }

    @Transactional
    @Override
    public void cancel(Long orderId, Long userId) {
        Order order = repository.findByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, orderId));
        cancel(order);
        return;
    }

    @Transactional
    @Override
    public OrderDTO cancelAdmin(long id) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        order = cancel(order);

        return mapper.toClientSummaryDTO(order);
    }

    private Order cancel(Order order) {
        OrderStatus currentStatus = order.getStatus();
        Set<OrderStatus> cancelAbleStatuses = Set.of(
                OrderStatus.VALIDATING,
                OrderStatus.CONFIRMED,
                OrderStatus.ALLOCATED);

        if (!cancelAbleStatuses.contains(currentStatus)) {
            throw new GenericException(ErrorCode.ORDER_CANCEL_STATUS_INVALID, order.getId());
        }

        order.setStatus(OrderStatus.CANCELING);
        eventPublisher.publishEvent(new OrderCancelDomainEvent(order));

        repository.save(order);
        return order;
    }

    @Transactional
    @Override
    public OrderDTO updateStatus(long id, OrderUpdateReq req) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        OrderStatus newStatus = OrderStatus.valueOf(req.getStatus());
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.ALLOCATED) {
            if (newStatus != OrderStatus.SHIPPING) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_STEP_INVALID, id, currentStatus,
                        OrderStatus.SHIPPING);
            }
        } else if (currentStatus == OrderStatus.SHIPPING) {
            if (newStatus != OrderStatus.COMPLETED) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_STEP_INVALID, id, currentStatus,
                        OrderStatus.COMPLETED);
            }
        } else {
            throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_INVALID, id);

        }

        if (newStatus == OrderStatus.SHIPPING) {
            if (order.getPaymentMethod() == PaymentMethod.VNPAY && order.getPaymentStatus() == PaymentStatus.PAID) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_PAYMENT);
            }
        }
        order.setStatus(newStatus);
        repository.save(order);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            eventPublisher.publishEvent(order);
        }

        return mapper.toClientSummaryDTO(order);
    }

}
