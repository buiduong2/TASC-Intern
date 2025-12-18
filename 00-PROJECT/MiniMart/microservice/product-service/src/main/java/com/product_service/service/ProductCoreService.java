package com.product_service.service;

import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.product_service.dto.req.ProductCheckExistsReq;
import com.product_service.dto.res.ProductCheckExistsRes;
import com.product_service.dto.res.ProductValidationResult;

public interface ProductCoreService {

    ProductCheckExistsRes validateExistsByIdsForInternal(ProductCheckExistsReq req);

    ProductValidationResult processOrderCreationRequested(OrderProductValidationRequestedEvent event);
}
