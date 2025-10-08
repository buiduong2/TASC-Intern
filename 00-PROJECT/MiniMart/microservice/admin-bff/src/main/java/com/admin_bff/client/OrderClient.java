package com.admin_bff.client;

import java.util.List;
import java.util.Map;

public interface OrderClient {

    Map<Long, Long> getUserOrderCountByIds(List<Long> ids);
}
