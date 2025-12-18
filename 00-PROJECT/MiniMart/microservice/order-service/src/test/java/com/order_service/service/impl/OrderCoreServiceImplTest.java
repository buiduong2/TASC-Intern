package com.order_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Address;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;
import com.order_service.model.ShippingMethod;
import com.order_service.repository.OrderRepository;
import com.order_service.repository.ShippingMethodRepository;

@ExtendWith(MockitoExtension.class)
public class OrderCoreServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private ShippingMethodRepository shippingMethodRepository;

    @Spy
    private OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @InjectMocks
    private OrderCoreServiceImpl coreService;

    ProductValidationPassedEvent validateEvent;

    Order order;

    @BeforeEach
    void setup() {
        validateEvent = createTestEvent();
        order = createPendingOrder();
    }

    Order createPendingOrder() {
        // ----- Shipping Method -----
        ShippingMethod shipping = new ShippingMethod();
        shipping.setId(2L);
        shipping.setName("Standard");
        shipping.setCost(new BigDecimal("30000"));

        // ----- Address -----
        Address address = new Address();
        address.setId(10L);
        address.setFirstName("Nguyen");
        address.setLastName("Van A");
        // address.setEmail("nguyenvana@example.com");
        address.setPhone("0987654321");
        address.setDetails("123 Nguyen Trai Street");
        address.setCity("Ho Chi Minh City");
        address.setArea("District 5");

        // ----- Order -----
        Order order = new Order();
        order.setId(999L);
        order.setMessage("Please deliver between 9AM and 11AM.");
        order.setShippingMethod(shipping);
        order.setAddress(address);
        order.setPaymentMethod(PaymentMethod.CARD);
        order.setPaymentStatus(PaymentStatus.PREPARING);
        order.setStatus(OrderStatus.VALIDATING); // bắt buộc để method chạy OK
        order.setUserId(123L);

        order.setTotalPrice(BigDecimal.ZERO); // bắt buộc: nếu không sẽ ném IdempotentEventException

        // Link back
        address.setOrder(order);

        // ----- Order Items -----
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setProductId(55L);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setProductId(66L);
        item2.setQuantity(2);

        // Liên kết 2 chiều
        order.addOrderItem(item1);
        order.addOrderItem(item2);

        return order;
    }

    ProductValidationPassedEvent createTestEvent() {

        ValidatedItemSnapshot v1 = new ValidatedItemSnapshot();
        v1.setOrderItemId(1L);
        v1.setProductId(55L);
        v1.setQuantity(2);
        v1.setUnitPrice(new BigDecimal("100000")); // 100k mỗi sp

        ValidatedItemSnapshot v2 = new ValidatedItemSnapshot();
        v2.setOrderItemId(2L);
        v2.setProductId(66L);
        v2.setQuantity(2);
        v2.setUnitPrice(new BigDecimal("200000")); // 200k mỗi sp

        Set<ValidatedItemSnapshot> snapshots = Set.of(v1, v2);

        return new ProductValidationPassedEvent(
                999L, // must match order ID
                123L, // must match user ID
                snapshots);
    }

    // 1) Khi order không tồn tại → phải ném ResourceNotFoundException
    @Test
    void process_givenOrderNotFound_shouldThrowResourceNotFound() {
        final long orderId = order.getId();
        final long userId = order.getUserId();

        doReturn(Optional.ofNullable(null))
                .when(repository)
                .findWithItemsByIdAndUserIdForUpdate(orderId, userId);

        assertThrows(ResourceNotFoundException.class, () -> {
            coreService.processProductValidationPassedEvent(validateEvent);
        });

    }

    // 2) Khi order không ở trạng thái VALIDATING → phải ném InvalidStateException
    @Test
    void process_givenOrderStatusNotValidating_shouldThrowInvalidStateException() {
        final long orderId = order.getId();
        final long userId = order.getUserId();

        // given
        order.setStatus(OrderStatus.ALLOCATED);

        // when
        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);

        // then
        assertThrows(InvalidStateException.class, () -> {
            coreService.processProductValidationPassedEvent(validateEvent);
        });

    }

    // 3) Khi order.totalPrice != 0 → phải ném IdempotentEventException
    @Test
    void process_givenOrderTotalPriceNotZero_shouldThrowIdempotentEventException() {
        final long orderId = order.getId();
        final long userId = order.getUserId();

        // given
        order.setStatus(OrderStatus.VALIDATING);
        order.setTotalPrice(BigDecimal.ONE);

        // Then
        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);

        // then
        assertThrows(IdempotentEventException.class, () -> {
            coreService.processProductValidationPassedEvent(validateEvent);
        });

    }

    // 4) Khi dữ liệu hợp lệ → phải cập nhật đơn đúng tổng giá
    @Test
    void process_givenValidEvent_shouldCalculateTotalPriceCorrectly() {
        // given
        final long orderId = order.getId();
        final long userId = order.getUserId();
        BigDecimal expectedTotalPrice = validateEvent.getValidatedItems().stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, (curr, total) -> total.add(curr));

        expectedTotalPrice = expectedTotalPrice.add(order.getShippingMethod().getCost());

        // when
        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);
        Order order = coreService.processProductValidationPassedEvent(validateEvent);

        // Then
        assertThat(order.getOrderItems())
                .allMatch(item -> item.getUnitPrice() != null && item.getUnitPrice() != BigDecimal.ZERO)
                .allMatch(item -> item.getTotalPrice() != null && item.getUnitPrice() != BigDecimal.ZERO);

        assertThat(order.getShippingMethod()).isNotNull();
        assertThat(order).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice);

    }

    // 5) Item không có snapshot trong event → bỏ qua, không cập nhật unitPrice
    @Test
    void process_givenMissingSnapshot_shouldIgnoreOrderItem() {
        final long orderId = order.getId();
        final long userId = order.getUserId();
        Set<ValidatedItemSnapshot> itemSnapshots = new HashSet<>(validateEvent.getValidatedItems());
        ValidatedItemSnapshot itemShapshot = validateEvent.getValidatedItems().stream().findAny().get();
        itemSnapshots.remove(itemShapshot);
        validateEvent.setValidatedItems(itemSnapshots);

        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);

        assertThrows(RuntimeException.class, () -> coreService.processProductValidationPassedEvent(validateEvent));

    }

    // 6) Snapshot quantity != orderItem.quantity → bỏ qua
    @Test
    void process_givenSnapshotQuantityMismatch_shouldIgnoreOrderItem() {
        final long orderId = order.getId();
        final long userId = order.getUserId();
        Set<ValidatedItemSnapshot> itemSnapshots = new HashSet<>(validateEvent.getValidatedItems());
        ValidatedItemSnapshot itemShapshot = validateEvent.getValidatedItems().stream().findAny().get();

        itemShapshot.setQuantity(Integer.MAX_VALUE);
        validateEvent.setValidatedItems(itemSnapshots);

        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);

        assertThrows(RuntimeException.class, () -> coreService.processProductValidationPassedEvent(validateEvent));

    }

    // 7) repository.save(order) phải được gọi đúng 1 lần
    @Test
    void process_givenValidEvent_shouldSaveOrder() {
        final long orderId = order.getId();
        final long userId = order.getUserId();
        doReturn(Optional.of(order)).when(repository).findWithItemsByIdAndUserIdForUpdate(orderId, userId);
        coreService.processProductValidationPassedEvent(validateEvent);

        verify(repository, times(1)).save(order);

    }

}
