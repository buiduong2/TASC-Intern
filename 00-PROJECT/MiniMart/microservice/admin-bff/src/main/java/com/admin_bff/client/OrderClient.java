package com.admin_bff.client;

import java.util.List;
import java.util.Map;

public interface OrderClient {

    // CompletableFuture<Map<Long, Long>> getUserOrderCountByIds(List<Long> ids);
    Map<Long, Long> getUserOrderCountByIds(List<Long> ids);
}
