package com.common_kafka.event.shared.helper;

import java.util.function.Supplier;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.common_kafka.event.shared.res.SagaResult;

public class SagaResultUtils {

    public static <T> SagaResult<T> execute(Supplier<T> action) {
        try {
            T data = action.get();
            return SagaResult.success(data);

        } catch (Exception e) {
            markRollback();
            return SagaResult.failure(e);
        }
    }

    private static void markRollback() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception ignored) {
            // no active transaction - ignore
        }
    }
}
