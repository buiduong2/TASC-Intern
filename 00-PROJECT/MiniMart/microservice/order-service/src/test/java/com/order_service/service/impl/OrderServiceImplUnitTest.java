package com.order_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.order_service.dto.req.AddressUpdateReq;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderItemCreateReq;
import com.order_service.dto.res.OrderDTO;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Order;
import com.order_service.repository.OrderItemRepository;
import com.order_service.repository.OrderRepository;
import com.order_service.repository.ShippingMethodRepository;
import com.order_service.saga.handler.OrderCreationSyncHandler;
import com.order_service.service.OrderCoreService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplUnitTest {

    @Mock
    private OrderRepository repository;

    private static OrderRepository coreOrderRepository;

    @Mock
    private static ShippingMethodRepository shippingMethodRepository;

    @Spy
    private static OrderCoreService coreService;

    @Mock
    private OrderItemRepository itemRepository;

    @Mock
    private OrderCreationSyncHandler creationSyncHandler;

    @Spy
    private static OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Captor
    ArgumentCaptor<OrderPreparedDomainEvent> eventCaptor;

    private OrderCreateReq req;

    @BeforeAll
    static void beforeAll() {
        coreOrderRepository = mock(OrderRepository.class);
        OrderCoreServiceImpl coreServiceImpl = new OrderCoreServiceImpl(
                coreOrderRepository,
                mock(ShippingMethodRepository.class),
                mapper);
        coreService = spy(coreServiceImpl);
    }

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(orderService, "coreService", coreService);

        req = new OrderCreateReq();
        req.setMessage("Please deliver between 9AM and 11AM.");
        req.setShippingMethodId(2L);
        req.setPaymentMethod("CASH");

        // ----- Address -----
        AddressUpdateReq address = new AddressUpdateReq();
        address.setFirstName("Nguyen");
        address.setLastName("Van A");
        address.setEmail("nguyenvana@example.com");
        address.setPhone("0987654321");
        address.setDetails("123 Nguyen Trai Street");
        address.setCity("Ho Chi Minh City");
        address.setArea("District 5");

        req.setAddress(address);

        // ----- Order Items -----
        LinkedHashSet<OrderItemCreateReq> orderItems = new LinkedHashSet<>();

        OrderItemCreateReq item1 = new OrderItemCreateReq();
        item1.setProductId(55L);
        item1.setQuantity(2);

        OrderItemCreateReq item2 = new OrderItemCreateReq();
        item2.setProductId(66L);
        item2.setQuantity(2);

        orderItems.add(item1);
        orderItems.add(item2);

        req.setOrderItems(orderItems);

    }

    @Test
    void create_givenReq_shouldCallCoreService() {
        // Given
        long userId = 1;
        doReturn(new OrderDTO()).when(mapper).toClientSummaryDTO(any());

        // When
        orderService.create(req, userId);

        // should
        verify(coreService, times(1)).createRaw(req, userId);
        verify(eventPublisher, times(0)).publishEvent(isA(OrderPreparedDomainEvent.class));

    }

    @Test
    void create_givenCashPayment_shouldCallCreationSyncHandler() {
        // Given
        long userId = 1;
        req.setPaymentMethod("CASH");
        doReturn(new OrderDTO()).when(mapper).toClientSummaryDTO(any());

        // When
        orderService.create(req, userId);

        // should
        verify(coreService, times(1)).createRaw(req, userId);
        verify(creationSyncHandler, times(1)).create(any());
        verify(eventPublisher, times(0)).publishEvent(isA(OrderPreparedDomainEvent.class));

    }

    @Test
    void create_givenVnPayPayment_shouldCallCreationSyncHandler() {
        // Given
        long userId = 1;
        req.setPaymentMethod("VNPAY");
        doReturn(new OrderDTO()).when(mapper).toClientSummaryDTO(any());

        // When
        orderService.create(req, userId);

        // should
        verify(coreService, times(1)).createRaw(req, userId);
        verify(creationSyncHandler, times(1)).create(any());
        verify(eventPublisher, times(0)).publishEvent(isA(OrderPreparedDomainEvent.class));
    }

    @Test
    void create_givenCardPayment_shouldPublishDomanEvent() {
        // Given
        long userId = 1;
        req.setPaymentMethod("CARD");
        doReturn(new OrderDTO()).when(mapper).toClientSummaryDTO(any());

        // When
        orderService.create(req, userId);

        // should
        verify(coreService, times(1)).createRaw(req, userId);
        verify(creationSyncHandler, times(0)).create(any());
        verify(eventPublisher, times(1)).publishEvent(isA(OrderPreparedDomainEvent.class));
    }

    @Test
    void create_givenCardReq_shouldMappingPending() {
        long userId = 1;
        long fakeOrderId = 1L;
        req.setPaymentMethod(PaymentMethod.CARD.name());
        doAnswer(invocation -> {
            Order agrument = invocation.getArgument(0);
            agrument.setId(fakeOrderId);
            return agrument;
        }).when(coreOrderRepository).save(any(Order.class));

        // when
        OrderDTO dto = orderService.create(req, userId);

        // should
        assertThat(dto.getId()).isEqualTo(fakeOrderId);
        assertThat(dto.getPaymentStatus()).isEqualTo(PaymentStatus.PREPARING);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getTotalPrice()).isNotNull()
                .isEqualTo(BigDecimal.ZERO);

    }
}
