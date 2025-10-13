package com.inventory_service.utils;

import java.util.Collection;
import java.util.List;

import com.common.exception.ErrorDetail;

public class ErrorDetails {
    public static List<ErrorDetail> productIdstoErrorDetails(Collection<Long> productIds) {

        return List.of(new ErrorDetail("productIds", "Missing some id=" + productIds.toString()));
    }
}
