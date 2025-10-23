package com.order_service.service;

import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.model.Payment;

public interface PaymentService {

    SagaResult<Payment> processInitialPaymentRequest(OrderInitialPaymentRequestedEvent event);

    void processOrderCreationCompensated(OrderCreationCompensatedEvent event);

}
