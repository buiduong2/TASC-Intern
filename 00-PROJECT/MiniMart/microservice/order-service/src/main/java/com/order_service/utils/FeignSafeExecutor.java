package com.order_service.utils;

import java.util.Map.Entry;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class FeignSafeExecutor {

    @Autowired
    JsonSafeParser parser;

    public <P, F> SafeResponse<P, F> call(
            Supplier<P> fn,
            Entry<Integer, Class<F>> erroEntry) {

        try {
            P successData = fn.get();
            System.out.println(successData.getClass());
            return SafeResponse.of(true, 200, successData, null, null);

        } catch (FeignException ex) {
            ex.printStackTrace();
            int status = ex.status();
            String raw = ex.contentUTF8();

            Class<F> errorClass = erroEntry.getValue();

            int expectedStatus = erroEntry.getKey();

            if (expectedStatus == status) {

                return SafeResponse.of(false, status, null, parser.parse(raw, errorClass), null);
            } else {
                return SafeResponse.of(null, status, null, null, raw);
            }

        }
    }

    public <T> T handleResponse(SafeResponse<?, ?> res, Supplier<T> onSuccess, Supplier<T> onFailure) {
        if (Boolean.TRUE.equals(res.getSuccess()) && res.getSuccessBody() != null) {
            return onSuccess.get();
        } else if (Boolean.FALSE.equals(res.getSuccess()) && res.getFailureBody() != null) {
            return onFailure.get();
        } else if (res.getSuccess() == null) {
            throw new IllegalStateException("Invalid remote result: success must be true or false.");
        }

        return null;

    }
}