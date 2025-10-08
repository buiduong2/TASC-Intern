package com.admin_bff.client.fallback;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.admin_bff.client.OrderClient;

@Service
public class OrderClientImpl implements OrderClient {

    @Override
    public Map<Long, Long> getUserOrderCountByIds(List<Long> ids) {
        Random random = new Random();
        Map<Long, Long> orderCounts = ids.stream()
                .collect(Collectors.toMap(Function.identity(), id -> (long) random.nextInt(11)));
        return orderCounts;
    }

}
