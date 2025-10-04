package com.admin_bff.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.admin_bff.client.AuthClient;
import com.admin_bff.client.OrderClient;
import com.admin_bff.client.ProfileClient;
import com.admin_bff.dto.res.ProfileDTO;
import com.admin_bff.dto.res.UserAuthDTO;
import com.admin_bff.dto.res.UserSummaryDTO;
import com.admin_bff.mapper.UserMapper;
import com.admin_bff.service.UserService;
import com.common.dto.PageResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final AuthClient authClient;
    private final ProfileClient profileClient;
    private final OrderClient orderClient;
    private final UserMapper userMapper;
    private final Executor executor;

    @Autowired
    @Lazy
    private UserService self;

    @Override
    public CompletableFuture<PageResponseDTO<UserSummaryDTO>> getUsers(MultiValueMap<String, String> params) {

        CompletableFuture<PageResponseDTO<UserAuthDTO>> authFuture = CompletableFuture.supplyAsync(() -> {
            return authClient.getUsers(params);
        }, executor);

        // 2. Chain the subsequent logic (non-blocking until the next I/O)
        return authFuture.thenCompose(authRes -> {

            // This logic runs on the same thread that completed authFuture.
            List<Long> userIds = authRes.getContent().stream().map(UserAuthDTO::getUserId).toList();

            if (userIds.isEmpty()) {
                return CompletableFuture.completedFuture(PageResponseDTO.empty(authRes));
            }

            // 3. Run Profile and Order Clients concurrently on the feignExecutor
            // These calls are also Blocking I/O, so they need the Executor.
            CompletableFuture<Map<Long, ProfileDTO>> profileFuture = CompletableFuture.supplyAsync(() -> {
                return profileClient.getProfileByIds(userIds);
            }, executor);

            CompletableFuture<Map<Long, Long>> orderFuture = CompletableFuture.supplyAsync(() -> {
                return orderClient.getUserOrderCountByIds(userIds);
            }, executor);

            // 4. Combine results and map DTOs
            return profileFuture.thenCombine(orderFuture, (profiles, orderCounts) -> {
                List<UserSummaryDTO> summaryContent = userMapper.toSummaryDTO(
                        authRes.getContent(), profiles, orderCounts);
                return PageResponseDTO.from(authRes, summaryContent);
            });
        });
    }

}
